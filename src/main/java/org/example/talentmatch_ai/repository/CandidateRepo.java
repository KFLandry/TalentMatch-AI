package org.example.talentmatch_ai.repository;

import org.example.talentmatch_ai.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CandidateRepo extends JpaRepository<Candidate, UUID>
{
}
