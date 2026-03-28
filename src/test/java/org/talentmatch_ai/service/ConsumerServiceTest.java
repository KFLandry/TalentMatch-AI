package org.talentmatch_ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.ollama.OllamaChatModel;
import org.talentmatch_ai.model.*;
import org.talentmatch_ai.repository.CandidateRepo;
import org.talentmatch_ai.repository.JobOfferRepo;
import org.talentmatch_ai.repository.MatchingRepo;
import org.talentmatch_ai.util.TestMockFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @Mock
    private CandidateRepo candidateRepo;
    @Mock
    private JobOfferRepo jobOfferRepo;
    @Mock
    private MatchingRepo matchingRepo;
    @Mock
    private OllamaChatModel ollamaChatModel;

    @InjectMocks
    private ConsumerService consumerService;

    private Candidate candidate;
    private JobOffer jobOffer;
    private MatchingResult matchingResult;
    private MatchingResultMessage matchingResultMessage;

    @BeforeEach
    void setUp() {
        candidate = TestMockFactory.javaDeveloperCandidate();
        jobOffer = TestMockFactory.springBootDeveloperOffer();
        matchingResult = TestMockFactory.pendingMatchingResult(candidate.getId(), jobOffer.getId());

        matchingResultMessage = MatchingResultMessage.builder()
                .matchingId(matchingResult.getId().toString())
                .candidateId(candidate.getId().toString())
                .jobOfferId(jobOffer.getId().toString())
                .build();
    }

    // ─── consume (flux complet) ───────────────────────────────────

    @Test
    void consume_shouldCompleteMatchingOnSuccess() {
        String aiResponse = "Score: 82\n\nPoints forts:\n- Compétences Java alignées\n\nPoints faibles:\n- Pas d'exp Kafka\n\nRecommandation:\nProfil solide.";

        when(matchingRepo.findById(matchingResult.getId())).thenReturn(Optional.of(matchingResult));
        when(matchingRepo.save(any(MatchingResult.class))).thenAnswer(i -> i.getArgument(0));
        when(candidateRepo.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(jobOfferRepo.findById(jobOffer.getId())).thenReturn(Optional.of(jobOffer));
        when(ollamaChatModel.call(anyString())).thenReturn(aiResponse);

        consumerService.consume(matchingResultMessage);

        ArgumentCaptor<MatchingResult> captor = ArgumentCaptor.forClass(MatchingResult.class);
        verify(matchingRepo, times(2)).save(captor.capture());

        MatchingResult saved = captor.getAllValues().get(1); // 2e save = résultat final
        assertAll(
                () -> assertEquals(Status.COMPLETED, saved.getStatus()),
                () -> assertEquals(82, saved.getScore()),
                () -> assertNotNull(saved.getAnalysis()),
                () -> assertNotNull(saved.getCompletedAt()),
                () -> assertNull(saved.getErrorMessage())
        );
    }

    @Test
    void consume_shouldFailMatchingOnAiError() {
        when(matchingRepo.findById(matchingResult.getId())).thenReturn(Optional.of(matchingResult));
        when(matchingRepo.save(any(MatchingResult.class))).thenAnswer(i -> i.getArgument(0));
        when(candidateRepo.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(jobOfferRepo.findById(jobOffer.getId())).thenReturn(Optional.of(jobOffer));
        when(ollamaChatModel.call(anyString())).thenThrow(new RuntimeException("Connection refused"));

        consumerService.consume(matchingResultMessage);

        ArgumentCaptor<MatchingResult> captor = ArgumentCaptor.forClass(MatchingResult.class);
        verify(matchingRepo, times(2)).save(captor.capture());

        MatchingResult saved = captor.getAllValues().get(1);
        assertAll(
                () -> assertEquals(Status.FAILED, saved.getStatus()),
                () -> assertNotNull(saved.getErrorMessage()),
                () -> assertNotNull(saved.getCompletedAt()),
                () -> assertNull(saved.getScore())
        );
    }

    @Test
    void consume_shouldSetProcessingBeforeCalling() {
        List<Status> savedStatuses = new ArrayList<>();

        when(matchingRepo.findById(matchingResult.getId())).thenReturn(Optional.of(matchingResult));
        when(matchingRepo.save(any(MatchingResult.class))).thenAnswer(i -> {
            MatchingResult current = i.getArgument(0);
            // Snapshot enum value at save-time to avoid mutable reference issues.
            savedStatuses.add(current.getStatus());
            return current;
        });
        when(candidateRepo.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(jobOfferRepo.findById(jobOffer.getId())).thenReturn(Optional.of(jobOffer));
        when(ollamaChatModel.call(anyString())).thenReturn("Score: 50\n\nPoints forts:\n- OK");

        consumerService.consume(matchingResultMessage);

        verify(matchingRepo, times(2)).save(any(MatchingResult.class));
        assertEquals(Status.PROCESSING, savedStatuses.get(0));
        assertEquals(Status.COMPLETED, savedStatuses.get(1));

        InOrder inOrder = inOrder(matchingRepo, ollamaChatModel);
        inOrder.verify(matchingRepo).save(any(MatchingResult.class));
        inOrder.verify(ollamaChatModel).call(anyString());
    }

    @Test
    void consume_shouldReturnWithoutSavingWhenMatchingNotFound() {
        when(matchingRepo.findById(matchingResult.getId())).thenReturn(Optional.empty());

        consumerService.consume(matchingResultMessage);

        verify(matchingRepo, never()).save(any(MatchingResult.class));
        verifyNoInteractions(candidateRepo, jobOfferRepo, ollamaChatModel);
    }
}

