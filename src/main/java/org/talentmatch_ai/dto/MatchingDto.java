package org.talentmatch_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.talentmatch_ai.model.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class MatchingDto {
    private UUID id;
    private Status status;
    private LocalDateTime requestedAt;
    private String message;
}
