package org.talentmatch_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.talentmatch_ai.model.Candidate;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidateRepo extends JpaRepository<Candidate, UUID>
{
    Boolean existsByEmail(String email);

    Optional<Candidate> findCandidateByEmail (String email);

    Candidate getCandidateById(UUID id);

    // pas sur que ce soit utile
}
