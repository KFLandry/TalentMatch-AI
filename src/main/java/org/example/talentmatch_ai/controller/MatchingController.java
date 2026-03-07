package org.example.talentmatch_ai.controller;

import org.example.talentmatch_ai.dto.MatchingDto;
import org.example.talentmatch_ai.dto.MatchingRequest;
import org.example.talentmatch_ai.service.MatchingService;
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
        return ResponseEntity.ok(matchingService.analyzeMatch(request));
    }

    @GetMapping("/{matchingId}")
    public ResponseEntity<MatchingDto> getMatching(@PathVariable String matchingId) {
        return ResponseEntity.ok(matchingService.getMatchingResultById(matchingId));
    }

    @GetMapping
    public ResponseEntity<List<MatchingDto>> getAllMatchings() {
        return ResponseEntity.ok(matchingService.getAllMatching());
    }
}
