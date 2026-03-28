package org.talentmatch_ai.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.talentmatch_ai.dto.MatchingDto;
import org.talentmatch_ai.dto.MatchingRequest;
import org.talentmatch_ai.dto.MatchingResultMapper;
import org.talentmatch_ai.model.MatchingResult;
import org.talentmatch_ai.model.MatchingResultMessage;
import org.talentmatch_ai.model.Status;
import org.talentmatch_ai.repository.CandidateRepo;
import org.talentmatch_ai.repository.JobOfferRepo;
import org.talentmatch_ai.repository.MatchingRepo;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MatchingService {
    private final KafkaTemplate<String, MatchingResultMessage> kafkaTemplate;
    private  final CandidateRepo candidateRepo;
    private final JobOfferRepo jobOfferRepo;
    private final MatchingRepo matchingRepo;
    private final MatchingResultMapper matchingResultMapper;

    public MatchingService(KafkaTemplate<String, MatchingResultMessage> kafkaTemplate, MatchingRepo matchingRepo, CandidateRepo candidateRepo, JobOfferRepo jobOfferRepo, MatchingResultMapper matchingResultMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.candidateRepo = candidateRepo;
        this.jobOfferRepo = jobOfferRepo;
        this.matchingRepo = matchingRepo;
        this.matchingResultMapper = matchingResultMapper;
    }

    public MatchingDto analyzeMatch(MatchingRequest matchingRequest) {
        // TODO Check if candidate and job offer exist
        if (!candidateRepo.existsById(matchingRequest.getCandidateId())) {
            throw new ResourceNotFoundException("Candidate not found"){};
        }
        if (!jobOfferRepo.existsById(matchingRequest.getJobOfferId())) {
            throw new ResourceNotFoundException("Job Offer not found"){};
        }

        // TODO Write with status "PENDING" to the database
        MatchingResult matchingResult = MatchingResult.builder()
                .candidateId(matchingRequest.getCandidateId())
                .jobOfferId(matchingRequest.getJobOfferId())
                .status(Status.PENDING)
                .requestedAt(java.time.LocalDateTime.now())
                .build();
        MatchingDto matchingDto = matchingResultMapper.toDTO(matchingRepo.save(matchingResult));
        matchingDto.setMessage("L'analyse a été soumise et sera traitée prochainement");

        // TODO push the matching request to Kafka for asynchronous processing
        MatchingResultMessage matchingResultMessage = MatchingResultMessage.builder()
                .matchingId(String.valueOf(matchingResult.getId()))
                .candidateId(String.valueOf(matchingResult.getCandidateId()))
                .jobOfferId(String.valueOf(matchingResult.getJobOfferId()))
                .build();
        String matchingTopic = "matching-requests";
        CompletableFuture<SendResult<String, MatchingResultMessage>> future = kafkaTemplate.send(matchingTopic, matchingResultMessage);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[{}] with offset=[{}]", matchingResultMessage.getMatchingId(), result.getRecordMetadata().offset());
            } else {
                log.info("Unable to send message=[{}] due to : {}", matchingResultMessage.getMatchingId(), ex.getMessage());
            }
        });


        return matchingDto;
    }

    public MatchingResult getMatchingResultById(String matchingId) {
        return matchingRepo.findById(UUID.fromString(matchingId))
                .orElseThrow(() -> new ResourceNotFoundException("Matching result not found") {});
    }

    public List<MatchingResult> getAllMatching() {
        return matchingRepo.findAll().stream()
                .toList();
    }


    public List<MatchingResult> getMatchingResultsByJobOfferId(String jobOfferId) {
        return matchingRepo.findByJobOfferId(UUID.fromString(jobOfferId)).stream()
                .toList();
    }

    public List<MatchingResult> getMatchingResultsByCandidateId(String candidateId) {
        return matchingRepo.findByCandidateId(UUID.fromString(candidateId)).stream()
                .toList();
    }
}
