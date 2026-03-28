package org.talentmatch_ai.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.talentmatch_ai.model.Candidate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CandidateRepoH2IntegrationTest {

    @Autowired
    private CandidateRepo candidateRepo;

    @Test
    void existsByEmail_shouldReturnTrueWhenCandidateExists() {
        Candidate candidate = Candidate.builder()
                .firstName("Alice")
                .lastName("Martin")
                .email("alice.martin@example.com")
                .skills(List.of("Java", "Spring"))
                .yearsOfExperience(3)
                .createdAt(LocalDateTime.now())
                .build();

        candidateRepo.save(candidate);

        assertTrue(candidateRepo.existsByEmail("alice.martin@example.com"));
    }

    @Test
    void findCandidateByEmail_shouldReturnCandidateWhenEmailExists() {
        Candidate candidate = Candidate.builder()
                .firstName("Bob")
                .lastName("Durand")
                .email("bob.durand@example.com")
                .skills(List.of("SQL", "Hibernate"))
                .yearsOfExperience(5)
                .createdAt(LocalDateTime.now())
                .build();

        candidateRepo.save(candidate);

        var found = candidateRepo.findCandidateByEmail("bob.durand@example.com");
        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getFirstName());
    }
}

