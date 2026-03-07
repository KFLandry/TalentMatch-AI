package org.example.talentmatch_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.talentmatch_ai.model.MatchingResult;
import org.example.talentmatch_ai.model.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class MatchingDto {
    private UUID id;
    private UUID jobOfferId;
    private UUID candidateId;
    private Status status;
    private LocalDateTime requestedAt;
    private String message;

    public static MatchingDto matchingtoDto(MatchingResult matchingResult) {
        return new MatchingDto(
                matchingResult.getId(),
                matchingResult.getJobOfferId(),
                matchingResult.getCandidateId(),
                matchingResult.getStatus(),
                matchingResult.getRequestedAt(),
                matchingResult.getErrorMessage() != null ? matchingResult.getErrorMessage() : "Matching completed successfully"
        );
    }
}
