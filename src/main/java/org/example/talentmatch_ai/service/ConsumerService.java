package org.example.talentmatch_ai.service;

import org.example.talentmatch_ai.model.KafkaMessage;
import org.example.talentmatch_ai.repository.CandidateRepo;
import org.example.talentmatch_ai.repository.JobOfferRepo;
import org.example.talentmatch_ai.repository.MatchingRepo;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

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

    @KafkaListener(topics = "talentmatch-topic", groupId = "talentmatch-group")
    public void consume(KafkaMessage message) {
        System.out.println("Received message: " + message.getMatchingId());
    }

    public String buildPrompt(KafkaMessage message) {
        // TODO Build a prompt for the LLM based on the candidate and job offer data
        return "Build a prompt for the LLM based on the candidate and job offer data";
    }

    public void processMatching(KafkaMessage message) {
        // TODO Process the matching request using the LLM and update the database with the result
        // TODO Get entities from database
        // TODO Call LLM
        // TODO Extract Score
        // TODO Save result to database
    }

}
