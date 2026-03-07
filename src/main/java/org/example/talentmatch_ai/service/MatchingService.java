package org.example.talentmatch_ai.service;

import lombok.extern.slf4j.Slf4j;
import org.example.talentmatch_ai.dto.MatchingDto;
import org.example.talentmatch_ai.dto.MatchingRequest;
import org.example.talentmatch_ai.dto.MatchingResultMapper;
import org.example.talentmatch_ai.model.MatchingResultMessage;
import org.example.talentmatch_ai.model.MatchingResult;
import org.example.talentmatch_ai.model.Status;
import org.example.talentmatch_ai.repository.CandidateRepo;
import org.example.talentmatch_ai.repository.JobOfferRepo;
import org.example.talentmatch_ai.repository.MatchingRepo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
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
        MatchingDto matchingDto = matchingResultMapper.toDTO(matchingRepo.save(matchingResult));
        matchingDto.setMessage("L'analyse a été soumise et sera traitée prochainement");

        // TODO push the matching request to Kafka for asynchronous processing
        MatchingResultMessage matchingResultMessage = MatchingResultMessage.builder()
                .matchingId(String.valueOf(matchingDto.getId()))
                .candidateId(String.valueOf(matchingDto.getCandidateId()))
                .jobOfferId(String.valueOf(matchingDto.getJobOfferId()))
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

    public MatchingDto getMatchingResultById(String matchingId) {
        return matchingRepo.findById(java.util.UUID.fromString(matchingId))
                .map(MatchingDto::matchingtoDto)
                .orElseThrow(() -> new RuntimeException("Matching result not found"));
    }

    public List<MatchingDto> getAllMatching() {
        return matchingRepo.findAll().stream()
                .map(MatchingDto::matchingtoDto)
                .toList();
    }
}
