package org.talentmatch_ai.service;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.talentmatch_ai.model.JobOffer;
import org.talentmatch_ai.repository.JobOfferRepo;

import java.util.List;
import java.util.UUID;

@Service
public class JobOfferService {

    private final JobOfferRepo jobOfferRepo;

    public JobOfferService(JobOfferRepo jobOfferRepo) {
        this.jobOfferRepo = jobOfferRepo;
    }

    public JobOffer createJobOffer(JobOffer jobOffer) {
        if (jobOffer == null || jobOffer.getTitle() == null || jobOffer.getTitle().isBlank()) {
            throw new IllegalArgumentException("Le titre de l'offre d'emploi est obligatoire");
        }
        if (jobOffer.getCompany() == null || jobOffer.getCompany().isBlank()) {
            throw new IllegalArgumentException("Le nom de l'entreprise est obligatoire");
        }
        if (jobOffer.getRequiredSkills() == null || jobOffer.getRequiredSkills().isEmpty()) {
            throw new IllegalArgumentException("Au moins une compétence requise est obligatoire");
        }

        return jobOfferRepo.save(jobOffer);
    }

    public List<JobOffer> getAllJobOffers() {
        return jobOfferRepo.findAll();
    }

    public JobOffer getJobOfferById(UUID id) {
        return jobOfferRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre d'emploi avec l'id " + id + " introuvable"));
    }

    public JobOffer updateJobOffer(UUID id, JobOffer updatedJobOffer) {
        JobOffer existing = jobOfferRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre d'emploi non trouvée : " + id));

        // Update fields if they are provided and not blank
        if (updatedJobOffer.getTitle() != null && !updatedJobOffer.getTitle().isBlank()) {
            existing.setTitle(updatedJobOffer.getTitle());
        }
        if (updatedJobOffer.getCompany() != null && !updatedJobOffer.getCompany().isBlank()) {
            existing.setCompany(updatedJobOffer.getCompany());
        }
        if (updatedJobOffer.getDescription() != null && !updatedJobOffer.getDescription().isBlank()) {
            existing.setDescription(updatedJobOffer.getDescription());
        }
        if (updatedJobOffer.getLocation() != null && !updatedJobOffer.getLocation().isBlank()) {
            existing.setLocation(updatedJobOffer.getLocation());
        }
        if (updatedJobOffer.getRequiredSkills() != null && !updatedJobOffer.getRequiredSkills().isEmpty()) {
            existing.setRequiredSkills(updatedJobOffer.getRequiredSkills());
        }
        if (updatedJobOffer.getSalaryRange() != null && !updatedJobOffer.getSalaryRange().isBlank()) {
            existing.setSalaryRange(updatedJobOffer.getSalaryRange());
        }

        return jobOfferRepo.save(existing);
    }

    public String deleteJobOffer(UUID id) {
        if (!jobOfferRepo.existsById(id)) {
            throw new ResourceNotFoundException("Offre d'emploi non trouvée : " + id);
        }
        jobOfferRepo.deleteById(id);
        return "Offre d'emploi avec l'id " + id + " supprimée avec succès";
    }
}

