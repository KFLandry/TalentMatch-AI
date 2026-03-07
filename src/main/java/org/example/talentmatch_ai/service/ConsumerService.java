package org.example.talentmatch_ai.service;

import lombok.extern.slf4j.Slf4j;
import org.example.talentmatch_ai.model.*;
import org.example.talentmatch_ai.repository.CandidateRepo;
import org.example.talentmatch_ai.repository.JobOfferRepo;
import org.example.talentmatch_ai.repository.MatchingRepo;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ConsumerService {

    private static final long TIMEOUT_MINUTES = 3; // 3 minutes
    private static final Pattern SCORE_PATTERN = Pattern.compile("(?i)score\\s*[:=]?\\s*(\\d{1,3})");

    private final CandidateRepo candidateRepo;
    private final MatchingRepo matchingRepo;
    private final JobOfferRepo jobOfferRepo;
    private final OllamaChatModel ollamaChatModel;

    public ConsumerService(CandidateRepo candidateRepo, MatchingRepo matchingRepo, JobOfferRepo jobOfferRepo, OllamaChatModel ollamaChatModel) {
        this.candidateRepo = candidateRepo;
        this.matchingRepo = matchingRepo;
        this.jobOfferRepo = jobOfferRepo;
        this.ollamaChatModel = ollamaChatModel;
    }

    @KafkaListener(topics = "matching-requests", groupId = "talentmatch-group")
    public void consume(MatchingResultMessage message) {
        log.info("Received message: {}", message.getMatchingId());

        Optional<MatchingResult> optionalMatchingResult = matchingRepo.findById(UUID.fromString(message.getMatchingId()));
        if (optionalMatchingResult.isEmpty()) {
            log.error("MatchingResult introuvable pour ID: {}, abandon du traitement", message.getMatchingId());
            return;
        }

        MatchingResult matchingResult = optionalMatchingResult.get();
        // Passer en PROCESSING
        matchingResult.setStatus(Status.PROCESSING);
        matchingRepo.save(matchingResult);

        try {
            String prompt = buildPrompt(message);
            String response = processMatching(prompt);
            int score = extractScore(response);

            matchingResult.setScore(score);
            matchingResult.setAnalysis(response);
            matchingResult.setStatus(Status.COMPLETED);
            matchingResult.setCompletedAt(LocalDateTime.now());

        } catch (Exception e) {
            matchingResult.setStatus(Status.FAILED);
            matchingResult.setErrorMessage(e.getMessage());
            matchingResult.setCompletedAt(LocalDateTime.now());
            log.error("Erreur lors du traitement du matching ID: {}, erreur: {}", message.getMatchingId(), e.getMessage());
        }

        matchingRepo.save(matchingResult);
    }

    public String buildPrompt(MatchingResultMessage message) {
        Candidate candidate = candidateRepo.findById(UUID.fromString(message.getCandidateId()))
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        JobOffer jobOffer = jobOfferRepo.findById(UUID.fromString(message.getJobOfferId()))
                .orElseThrow(() -> new RuntimeException("Job offer not found"));

        return """
                Analyse l'adéquation entre le candidat et l'offre d'emploi ci-dessous.
                Donne un score de 0 à 100 et une analyse textuelle détaillée.
                
                === CANDIDAT ===
                Nom : %s %s
                Compétences : %s
                Années d'expérience : %d
                Bio : %s
                
                === OFFRE D'EMPLOI ===
                Titre : %s
                Entreprise : %s
                Compétences requises : %s
                Description : %s
                Localisation : %s
                Salaire : %s
                
                === INSTRUCTIONS ===
                Réponds OBLIGATOIREMENT dans le format suivant :
                Score: [valeur entre 0 et 100]
                
                Points forts:
                - ...
                
                Points faibles:
                - ...
                
                Recommandation:
                ...
                """.formatted(
                candidate.getFirstName(),
                candidate.getLastName(),
                String.join(", ", candidate.getSkills()),
                candidate.getYearsOfExperience(),
                candidate.getBio() != null ? candidate.getBio() : "Non renseigné",
                jobOffer.getTitle(),
                jobOffer.getCompany(),
                String.join(", ", jobOffer.getRequiredSkills()),
                jobOffer.getDescription(),
                jobOffer.getLocation(),
                jobOffer.getSalaryRange() != null ? jobOffer.getSalaryRange() : "Non renseigné"
        );
    }

    public String processMatching(String prompt) {
        try {
            return CompletableFuture.supplyAsync(() -> ollamaChatModel.call(prompt))
                    .get(TIMEOUT_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel à l'IA (timeout ou indisponibilité) : " + e.getMessage(), e);
        }
    }

    public int extractScore(String response) {
        Matcher matcher = SCORE_PATTERN.matcher(response);
        if (matcher.find()) {
            int score = Integer.parseInt(matcher.group(1));
            return Math.min(100, Math.max(0, score));
        }
        throw new RuntimeException("Impossible d'extraire le score depuis la réponse de l'IA");
    }
}
