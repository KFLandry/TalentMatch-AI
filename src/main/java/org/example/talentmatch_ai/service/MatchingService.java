package org.example.talentmatch_ai.service;

import org.example.talentmatch_ai.dto.MatchingDto;
import org.example.talentmatch_ai.dto.MatchingRequest;
import org.example.talentmatch_ai.model.KafkaMessage;
import org.example.talentmatch_ai.model.MatchingResult;
import org.example.talentmatch_ai.model.Status;
import org.example.talentmatch_ai.repository.CandidateRepo;
import org.example.talentmatch_ai.repository.JobOfferRepo;
import org.example.talentmatch_ai.repository.MatchingRepo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class MatchingService {
    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;
    private  final CandidateRepo candidateRepo;
    private final JobOfferRepo jobOfferRepo;
    private final MatchingRepo matchingRepo;

        public MatchingService(KafkaTemplate<String, KafkaMessage> kafkaTemplate, MatchingRepo matchingRepo, CandidateRepo candidateRepo, JobOfferRepo jobOfferRepo) {
            this.kafkaTemplate = kafkaTemplate;
            this.candidateRepo = candidateRepo;
            this.jobOfferRepo = jobOfferRepo;
            this.matchingRepo = matchingRepo;
        }

    public MatchingDto analyzeMatch(MatchingRequest matchingRequest) {
        // TODO Check if candidate and job offer exist
        if (!candidateRepo.existsById(matchingRequest.getCandidateId())) {
            throw new RuntimeException("Candidate not found");
        }
        if (!jobOfferRepo.existsById(matchingRequest.getJobOfferId())) {
            throw new RuntimeException("Job offer not found");
        }

        // TODO Write with status "PENDING" to the database
        MatchingResult matchingResult = MatchingResult.builder()
                .candidateId(matchingRequest.getCandidateId())
                .jobOfferId(matchingRequest.getJobOfferId())
                .status(Status.PENDING)
                .requestedAt(java.time.LocalDateTime.now())
                .build();
        MatchingDto matchingDto = MatchingDto.matchingtoDto(matchingRepo.save(matchingResult));

        // TODO push the matching request to Kafka for asynchronous processing
        KafkaMessage kafkaMessage = KafkaMessage.builder()
                .matchingId(String.valueOf(matchingDto.getId()))
                .candidateId(String.valueOf(matchingDto.getCandidateId()))
                .jobOfferId(String.valueOf(matchingDto.getJobOfferId()))
                .build();
        String matchingTopic = "matching-requests";
        CompletableFuture<SendResult<String, KafkaMessage>> future = kafkaTemplate.send(matchingTopic, kafkaMessage);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Sent message=[" + kafkaMessage.getMatchingId() +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                System.out.println("Unable to send message=[" +
                        kafkaMessage.getMatchingId() + "] due to : " + ex.getMessage());
            }
        });


        return matchingDto;
    }

}
