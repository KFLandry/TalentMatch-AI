package org.talentmatch_ai.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.talentmatch_ai.model.Candidate;
import org.talentmatch_ai.repository.CandidateRepo;
import org.talentmatch_ai.service.CandidatesService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/candidate")
public class CandidateController {
    private final CandidatesService candidatesService;

    private CandidateController (CandidatesService candidatesService) {
        this.candidatesService = candidatesService;
    }

    @PostMapping("/newCandidate")
    // ajout candidat manuellement
    public ResponseEntity<Candidate> create(@Valid @RequestBody Candidate candidate) {
        return ResponseEntity.status(201).body(candidatesService.newCandidate(candidate));
    }

    @PutMapping("/put/{id}")
    public ResponseEntity<Candidate> update(
            @PathVariable UUID id,
            @Valid @RequestBody Candidate candidate) {
        return ResponseEntity.ok(candidatesService.updateCandidate(id, candidate));
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<Candidate>> getAll() {
        return ResponseEntity.ok(candidatesService.getAllCandidates());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Candidate> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(candidatesService.getCandidateById(id));
    }

    @DeleteMapping("/del/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        candidatesService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/github/{username}")
    // ajout candidat automatiquement via github
    public ResponseEntity<Candidate> createFromGithub(@PathVariable String username) {
        return ResponseEntity.status(201).body(candidatesService.buildCandidateFromGithub(username));
    }
}
