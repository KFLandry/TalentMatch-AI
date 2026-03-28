package org.talentmatch_ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.talentmatch_ai.dto.MatchingDto;
import org.talentmatch_ai.dto.MatchingRequest;
import org.talentmatch_ai.dto.MatchingResultMapper;
import org.talentmatch_ai.model.MatchingResult;
import org.talentmatch_ai.model.MatchingResultMessage;
import org.talentmatch_ai.model.Status;
import org.talentmatch_ai.repository.CandidateRepo;
import org.talentmatch_ai.repository.JobOfferRepo;
import org.talentmatch_ai.repository.MatchingRepo;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private KafkaTemplate<String, MatchingResultMessage> kafkaTemplate;
    @Mock
    private CandidateRepo candidateRepo;
    @Mock
    private JobOfferRepo jobOfferRepo;
    @Mock
    private MatchingRepo matchingRepo;
    @Mock
    private MatchingResultMapper matchingResultMapper;

    private MatchingService matchingService;

    private UUID candidateId;
    private UUID jobOfferId;
    private MatchingRequest request;

    @BeforeEach
    void setUp() {
        matchingService = new MatchingService(
                kafkaTemplate,
                matchingRepo,
                candidateRepo,
                jobOfferRepo,
                matchingResultMapper,
                "matching-requests"
        );

        candidateId = UUID.randomUUID();
        jobOfferId = UUID.randomUUID();
        request = new MatchingRequest();
        request.setCandidateId(candidateId);
        request.setJobOfferId(jobOfferId);
    }

    @Test
    void analyzeMatch_shouldSavePendingAndReturnAcceptedMessage() {
        when(candidateRepo.existsById(candidateId)).thenReturn(true);
        when(jobOfferRepo.existsById(jobOfferId)).thenReturn(true);
        when(matchingRepo.save(any(MatchingResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Keep the send future pending so callback is not executed in this test.
        when(kafkaTemplate.send(eq("matching-requests"), any(MatchingResultMessage.class)))
                .thenReturn(new CompletableFuture<>());

        when(matchingResultMapper.toDTO(any(MatchingResult.class))).thenAnswer(invocation -> {
            MatchingResult saved = invocation.getArgument(0);
            return MatchingDto.builder()
                    .id(saved.getId())
                    .status(saved.getStatus())
                    .requestedAt(saved.getRequestedAt())
                    .build();
        });

        MatchingDto result = matchingService.analyzeMatch(request);

        ArgumentCaptor<MatchingResult> savedCaptor = ArgumentCaptor.forClass(MatchingResult.class);
        verify(matchingRepo, times(1)).save(savedCaptor.capture());
        MatchingResult saved = savedCaptor.getValue();

        assertAll(
                () -> assertEquals(Status.PENDING, saved.getStatus()),
                () -> assertEquals(candidateId, saved.getCandidateId()),
                () -> assertEquals(jobOfferId, saved.getJobOfferId()),
//                () -> assertNotNull(result.getId()),
                () -> assertEquals("L'analyse a été soumise et sera traitée prochainement", result.getMessage())
        );
    }

    @Test
    void analyzeMatch_shouldMarkAsFailedWhenKafkaPublishFails() {
        when(candidateRepo.existsById(candidateId)).thenReturn(true);
        when(jobOfferRepo.existsById(jobOfferId)).thenReturn(true);
        when(matchingRepo.save(any(MatchingResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompletableFuture<SendResult<String, MatchingResultMessage>> failedFuture =
                CompletableFuture.failedFuture(new RuntimeException("broker unavailable"));
        when(kafkaTemplate.send(eq("matching-requests"), any(MatchingResultMessage.class)))
                .thenReturn(failedFuture);

        when(matchingResultMapper.toDTO(any(MatchingResult.class))).thenAnswer(invocation -> {
            MatchingResult saved = invocation.getArgument(0);
            return MatchingDto.builder()
                    .id(saved.getId())
                    .status(saved.getStatus())
                    .requestedAt(saved.getRequestedAt())
                    .build();
        });

        matchingService.analyzeMatch(request);

        ArgumentCaptor<MatchingResult> savedCaptor = ArgumentCaptor.forClass(MatchingResult.class);
        verify(matchingRepo, times(2)).save(savedCaptor.capture());

        MatchingResult failedState = savedCaptor.getAllValues().get(1);
        assertAll(
                () -> assertEquals(Status.FAILED, failedState.getStatus()),
                () -> assertNotNull(failedState.getCompletedAt()),
                () -> assertNotNull(failedState.getErrorMessage()),
                () -> assertTrue(failedState.getErrorMessage().contains("Kafka publish error"))
        );
    }

    @Test
    void analyzeMatch_shouldThrowWhenCandidateDoesNotExist() {
        when(candidateRepo.existsById(candidateId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> matchingService.analyzeMatch(request));
        verifyNoInteractions(jobOfferRepo, kafkaTemplate, matchingRepo, matchingResultMapper);
    }

    @Test
    void analyzeMatch_shouldThrowWhenJobOfferDoesNotExist() {
        when(candidateRepo.existsById(candidateId)).thenReturn(true);
        when(jobOfferRepo.existsById(jobOfferId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> matchingService.analyzeMatch(request));
        verifyNoInteractions(kafkaTemplate, matchingRepo, matchingResultMapper);
    }
}

