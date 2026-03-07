package org.example.talentmatch_ai.service;

import org.example.talentmatch_ai.model.Candidate;
import org.example.talentmatch_ai.model.JobOffer;
import org.example.talentmatch_ai.model.MatchingResultMessage;
import org.example.talentmatch_ai.model.MatchingResult;
import org.example.talentmatch_ai.model.Status;
import org.example.talentmatch_ai.repository.CandidateRepo;
import org.example.talentmatch_ai.repository.JobOfferRepo;
import org.example.talentmatch_ai.repository.MatchingRepo;
import org.example.talentmatch_ai.util.TestMockFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.ollama.OllamaChatModel;

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

    // ─── buildPrompt ──────────────────────────────────────────────

    @Test
    void buildPrompt_shouldContainCandidateInfo() {
        when(candidateRepo.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(jobOfferRepo.findById(jobOffer.getId())).thenReturn(Optional.of(jobOffer));

        String prompt = consumerService.buildPrompt(matchingResultMessage);

        assertAll(
                () -> assertTrue(prompt.contains("Kevin")),
                () -> assertTrue(prompt.contains("Durant")),
                () -> assertTrue(prompt.contains("Java")),
                () -> assertTrue(prompt.contains("Spring Boot")),
                () -> assertTrue(prompt.contains("5"))
        );
    }

    @Test
    void buildPrompt_shouldContainJobOfferInfo() {
        when(candidateRepo.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(jobOfferRepo.findById(jobOffer.getId())).thenReturn(Optional.of(jobOffer));

        String prompt = consumerService.buildPrompt(matchingResultMessage);

        assertAll(
                () -> assertTrue(prompt.contains("Développeur Java/Spring Boot")),
                () -> assertTrue(prompt.contains("TechCorp")),
                () -> assertTrue(prompt.contains("PostgreSQL")),
                () -> assertTrue(prompt.contains("Paris, France")),
                () -> assertTrue(prompt.contains("45k-60k")),
                () -> assertTrue(prompt.contains("Responsabilités"))
        );
    }

    @Test
    void buildPrompt_shouldContainInstructions() {
        when(candidateRepo.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(jobOfferRepo.findById(jobOffer.getId())).thenReturn(Optional.of(jobOffer));

        String prompt = consumerService.buildPrompt(matchingResultMessage);

        assertAll(
                () -> assertTrue(prompt.contains("Score:")),
                () -> assertTrue(prompt.contains("Points forts")),
                () -> assertTrue(prompt.contains("Points faibles")),
                () -> assertTrue(prompt.contains("Recommandation"))
        );
    }

    @Test
    void buildPrompt_shouldHandleNullBio() {
        candidate.setBio(null);
        when(candidateRepo.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(jobOfferRepo.findById(jobOffer.getId())).thenReturn(Optional.of(jobOffer));

        String prompt = consumerService.buildPrompt(matchingResultMessage);

        assertTrue(prompt.contains("Non renseigné"));
    }

    @Test
    void buildPrompt_shouldHandleNullSalaryRange() {
        jobOffer.setSalaryRange(null);
        when(candidateRepo.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(jobOfferRepo.findById(jobOffer.getId())).thenReturn(Optional.of(jobOffer));

        String prompt = consumerService.buildPrompt(matchingResultMessage);

        assertTrue(prompt.contains("Non renseigné"));
    }

    // ─── extractScore ─────────────────────────────────────────────

    @Test
    void extractScore_shouldParseValidScore() {
        assertEquals(85, consumerService.extractScore("Score: 85\n\nPoints forts:\n- Java"));
    }

    @Test
    void extractScore_shouldParseScoreWithEquals() {
        assertEquals(72, consumerService.extractScore("score = 72\nAnalyse..."));
    }

    @Test
    void extractScore_shouldClampScoreAbove100() {
        assertEquals(100, consumerService.extractScore("Score: 150"));
    }

    @Test
    void extractScore_shouldClampScoreBelow0() {
        assertEquals(0, consumerService.extractScore("Score: 0"));
    }

    @Test
    void extractScore_shouldThrowWhenNoScore() {
        assertThrows(RuntimeException.class, () -> consumerService.extractScore("Pas de score ici"));
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
        when(matchingRepo.findById(matchingResult.getId())).thenReturn(Optional.of(matchingResult));
        when(matchingRepo.save(any(MatchingResult.class))).thenAnswer(i -> i.getArgument(0));
        when(candidateRepo.findById(candidate.getId())).thenReturn(Optional.of(candidate));
        when(jobOfferRepo.findById(jobOffer.getId())).thenReturn(Optional.of(jobOffer));
        when(ollamaChatModel.call(anyString())).thenReturn("Score: 50\n\nPoints forts:\n- OK");

        consumerService.consume(matchingResultMessage);

        ArgumentCaptor<MatchingResult> captor = ArgumentCaptor.forClass(MatchingResult.class);
        verify(matchingRepo, times(2)).save(captor.capture());

        assertEquals(Status.COMPLETED, captor.getAllValues().getFirst().getStatus()); // 1er save = PROCESSING
    }

    // ─── scénarios croisés avec mocks variés ──────────────────────

    @Test
    void buildPrompt_withMismatchedProfiles_shouldContainBothInfos() {
        Candidate dataCandidate = TestMockFactory.dataScienceCandidate();
        JobOffer javaOffer = TestMockFactory.springBootDeveloperOffer();

        MatchingResultMessage msg = MatchingResultMessage.builder()
                .matchingId(matchingResult.getId().toString())
                .candidateId(dataCandidate.getId().toString())
                .jobOfferId(javaOffer.getId().toString())
                .build();

        when(candidateRepo.findById(dataCandidate.getId())).thenReturn(Optional.of(dataCandidate));
        when(jobOfferRepo.findById(javaOffer.getId())).thenReturn(Optional.of(javaOffer));

        String prompt = consumerService.buildPrompt(msg);

        assertAll(
                () -> assertTrue(prompt.contains("Python")),       // candidat data
                () -> assertTrue(prompt.contains("TensorFlow")),   // candidat data
                () -> assertTrue(prompt.contains("Spring Boot")),  // offre Java
                () -> assertTrue(prompt.contains("TechCorp"))      // offre Java
        );
    }
}

