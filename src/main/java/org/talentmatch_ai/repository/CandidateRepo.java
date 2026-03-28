package org.talentmatch_ai.repository;

import org.talentmatch_ai.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidateRepo extends JpaRepository<Candidate, UUID>
{
    Boolean existsByEmail(String email);
    Optional<Candidate> findCandidateByEmail (String email);

    Candidate getCandidateById(UUID id);

}
