package org.talentmatch_ai.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@Table(name = "matching_results", indexes = {
        @Index(name = "idx_matching_result_candidate_id", columnList = "candidateId"),
        @Index(name = "idx_matching_result_job_offer_id", columnList = "jobOfferId"),
})
@AllArgsConstructor
@NoArgsConstructor
public class MatchingResult {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private UUID jobOfferId;

    @NotNull
    @Column(nullable = false)
    private UUID candidateId;

    @Min(0)
    @Max(100)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer score;

    @Lob
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String analysis;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private LocalDateTime completedAt;

    @Lob
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String errorMessage;

    @PrePersist
    void onCreate() {
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
        validateState();
    }

    @PreUpdate
    void onUpdate() {
        validateState();
    }

    private void validateState() {
        if ((status == Status.COMPLETED || status == Status.FAILED) && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
        if (status != Status.FAILED) {
            errorMessage = null;
        }
    }
}
