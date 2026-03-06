package org.example.talentmatch_ai.repository;

import org.example.talentmatch_ai.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CandidateRepo extends JpaRepository<Candidate, UUID>
{
}
