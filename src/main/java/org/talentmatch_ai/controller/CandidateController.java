package org.talentmatch_ai.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.talentmatch_ai.exception.GithubException;
import org.talentmatch_ai.model.Candidate;
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

    @PostMapping
    // ajout candidat manuellement
    public ResponseEntity<Candidate> create(@Valid @RequestBody Candidate candidate) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidatesService.newCandidate(candidate));
    }

    @GetMapping
    public ResponseEntity<List<Candidate>> getAll() {
        return ResponseEntity.ok(candidatesService.getAllCandidates());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidate> update(
            @PathVariable UUID id,
            @Valid @RequestBody Candidate candidate) {
        return ResponseEntity.ok(candidatesService.updateCandidate(id, candidate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(candidatesService.getCandidateById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(candidatesService.deleteCandidate(id));
    }

    @PostMapping("/import/{username}")
    // ajout candidat automatiquement via github
    public ResponseEntity<Candidate> createFromGithub(@PathVariable String username) throws GithubException {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidatesService.buildCandidateFromGithub(username));
    }
}
