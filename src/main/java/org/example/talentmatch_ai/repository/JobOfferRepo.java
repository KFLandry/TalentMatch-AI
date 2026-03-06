package org.example.talentmatch_ai.repository;

import org.example.talentmatch_ai.model.JobOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobOfferRepo  extends JpaRepository<JobOffer, UUID> {
}
