package org.example.talentmatch_ai.controller;

import org.example.talentmatch_ai.dto.MatchingDto;
import org.example.talentmatch_ai.dto.MatchingRequest;
import org.example.talentmatch_ai.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
