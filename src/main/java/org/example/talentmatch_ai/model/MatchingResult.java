package org.example.talentmatch_ai.model;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "matching_results")
@Data
@Builder
public class MatchingResult {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    private UUID jobOfferId;
    private UUID candidateId;
    private Double score;
    private String analysis;
    private Status status;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
    private String errorMessage;

}
