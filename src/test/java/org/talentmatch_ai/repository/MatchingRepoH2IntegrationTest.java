package org.talentmatch_ai.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.talentmatch_ai.model.MatchingResult;
import org.talentmatch_ai.model.Status;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class MatchingRepoH2IntegrationTest {

    @Autowired
    private MatchingRepo matchingRepo;

    @Test
    void findByCandidateId_shouldReturnOnlyMatchingRowsForCandidate() {
        UUID candidateA = UUID.randomUUID();
        UUID candidateB = UUID.randomUUID();

        matchingRepo.save(MatchingResult.builder()
                .candidateId(candidateA)
                .jobOfferId(UUID.randomUUID())
                .status(Status.PENDING)
                .requestedAt(LocalDateTime.now())
                .build());

        matchingRepo.save(MatchingResult.builder()
                .candidateId(candidateA)
                .jobOfferId(UUID.randomUUID())
                .status(Status.COMPLETED)
                .requestedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .score(78)
                .analysis("ok")
                .build());

        matchingRepo.save(MatchingResult.builder()
                .candidateId(candidateB)
                .jobOfferId(UUID.randomUUID())
                .status(Status.FAILED)
                .requestedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .errorMessage("failed")
                .build());

        assertEquals(2, matchingRepo.findByCandidateId(candidateA).size());
        assertEquals(1, matchingRepo.findByCandidateId(candidateB).size());
    }

    @Test
    void countByStatus_shouldReturnCorrectTotals() {
        matchingRepo.save(MatchingResult.builder()
                .candidateId(UUID.randomUUID())
                .jobOfferId(UUID.randomUUID())
                .status(Status.PENDING)
                .requestedAt(LocalDateTime.now())
                .build());

        matchingRepo.save(MatchingResult.builder()
                .candidateId(UUID.randomUUID())
                .jobOfferId(UUID.randomUUID())
                .status(Status.COMPLETED)
                .requestedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .score(90)
                .analysis("great")
                .build());

        matchingRepo.save(MatchingResult.builder()
                .candidateId(UUID.randomUUID())
                .jobOfferId(UUID.randomUUID())
                .status(Status.FAILED)
                .requestedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .errorMessage("timeout")
                .build());

        assertEquals(1L, matchingRepo.countByStatus(Status.PENDING));
        assertEquals(1L, matchingRepo.countByStatus(Status.COMPLETED));
        assertEquals(1L, matchingRepo.countByStatus(Status.FAILED));
        assertEquals(0L, matchingRepo.countByStatus(Status.PROCESSING));
    }
}

