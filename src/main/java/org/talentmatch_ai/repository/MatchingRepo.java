package org.talentmatch_ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.talentmatch_ai.model.MatchingResult;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatchingRepo extends JpaRepository<MatchingResult, UUID>
{
    List<MatchingResult> findByJobOfferId(UUID uuid);

    List<MatchingResult> findByCandidateId(UUID uuid);
}
