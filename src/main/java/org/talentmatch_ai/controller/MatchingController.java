package org.talentmatch_ai.controller;

import org.springframework.http.HttpStatus;
import org.talentmatch_ai.dto.MatchingDto;
import org.talentmatch_ai.dto.MatchingRequest;
import org.talentmatch_ai.model.MatchingResult;
import org.talentmatch_ai.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<MatchingDto> analyzeMatching(@RequestBody MatchingRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(matchingService.analyzeMatch(request));
    }
    @GetMapping("results")
    public ResponseEntity<List<MatchingResult>> getAllMatchings() {
        return ResponseEntity.ok(matchingService.getAllMatching());
    }

    @GetMapping("/results/{matchingId}")
    public ResponseEntity<MatchingResult> getMatching(@PathVariable String matchingId) {
        return ResponseEntity.ok(matchingService.getMatchingResultById(matchingId));
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<MatchingResult>> getMatchingsByCandidate(@PathVariable String candidateId){
        return ResponseEntity.ok(matchingService.getMatchingResultsByCandidateId(candidateId));
    }

    @GetMapping("/job/{jobOfferId}")
    public ResponseEntity<List<MatchingResult>> getMatchingsByJobOffer(@PathVariable String jobOfferId) {
        return ResponseEntity.ok(matchingService.getMatchingResultsByJobOfferId(jobOfferId));
    }

}
