package org.talentmatch_ai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.talentmatch_ai.dto.GithubProfile;
import org.talentmatch_ai.model.Candidate;
import org.talentmatch_ai.repository.CandidateRepo;
import org.talentmatch_ai.dto.GithubRepo;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CandidatesService {
    // objectif créer les méthodes de création d'user
    private final CandidateRepo candidateRepo;
    private final GithubService githubService;

    public CandidatesService(CandidateRepo candidateRepo, GithubService githubService) {
        this.candidateRepo = candidateRepo;
        this.githubService = githubService;
    }

    public Candidate newCandidate(Candidate candidate) {
        if (candidateRepo.existsByEmail(candidate.getEmail())) {
            throw new IllegalArgumentException("Un candidat avec cet email existe déjà : " + candidate.getEmail());
        }

        return candidateRepo.save(candidate);
    }

    public List<Candidate> getAllCandidates () {
        return candidateRepo.findAll();
    }

    public Candidate getCandidateById (UUID id){
        Candidate cand = candidateRepo.getCandidateById(id);
        if (cand == null){
            throw new RuntimeException("utilisateur avec l'id " + id + " introuvable");
        }
        else {
            return cand;
        }
    }

    public Candidate updateCandidate(UUID id, Candidate updatedCandidate) {
        Candidate existing = candidateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé : " + id));

        existing.setFirstName(updatedCandidate.getFirstName());
        existing.setLastName(updatedCandidate.getLastName());
        existing.setEmail(updatedCandidate.getEmail());
        existing.setGithubUsername(updatedCandidate.getGithubUsername());
        existing.setSkills(updatedCandidate.getSkills());
        existing.setYearsOfExperience(updatedCandidate.getYearsOfExperience());
        existing.setBio(updatedCandidate.getBio());

        return candidateRepo.save(existing);
    }

    public void deleteCandidate(UUID id) {
        if (!candidateRepo.existsById(id)) {
            throw new RuntimeException("Candidat non trouvé : " + id);
        }
        candidateRepo.deleteById(id);
    }

    public Candidate buildCandidateFromGithub(String username) {
        GithubProfile profile = githubService.getUserProfile(username);
        List<GithubRepo> repos = githubService.getReposByUsername(username);

        List<String> topSkills = repos.stream()
                .map(GithubRepo::getLanguage)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(l -> l, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        int estimatedYearsOfExperience = repos.stream()
                .map(GithubRepo::getCreatedAt)
                .filter(Objects::nonNull)
                .map(date -> OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).getYear())
                .min(Integer::compareTo)
                .map(firstYear -> LocalDateTime.now().getYear() - firstYear)
                .orElse(0);

        Candidate candidate = Candidate.builder()
                .email(username + "@github.com")
                .githubUsername(username)
                .bio(profile.getBio())
                .skills(topSkills)
                .yearsOfExperience(estimatedYearsOfExperience)
                .build();
        return candidateRepo.save(candidate);
    }

}

