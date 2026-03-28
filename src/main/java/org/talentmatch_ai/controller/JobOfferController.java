package org.talentmatch_ai.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.talentmatch_ai.model.JobOffer;
import org.talentmatch_ai.service.JobOfferService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/job-offers")
public class JobOfferController {

    private final JobOfferService jobOfferService;

    public JobOfferController(JobOfferService jobOfferService) {
        this.jobOfferService = jobOfferService;
    }

    @PostMapping
    public ResponseEntity<JobOffer> createJobOffer(@Valid @RequestBody JobOffer jobOffer) {
        JobOffer createdJobOffer = jobOfferService.createJobOffer(jobOffer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJobOffer);
    }

    @GetMapping
    public ResponseEntity<List<JobOffer>> getAllJobOffers() {
        List<JobOffer> jobOffers = jobOfferService.getAllJobOffers();
        return ResponseEntity.ok(jobOffers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOffer> getJobOfferById(@PathVariable UUID id) {
        JobOffer jobOffer = jobOfferService.getJobOfferById(id);
        return ResponseEntity.ok(jobOffer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobOffer> updateJobOffer(
            @PathVariable UUID id,
            @Valid @RequestBody JobOffer updatedJobOffer) {
        JobOffer jobOffer = jobOfferService.updateJobOffer(id, updatedJobOffer);
        return ResponseEntity.ok(jobOffer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJobOffer(@PathVariable UUID id) {
        String message = jobOfferService.deleteJobOffer(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(message);
    }
}

