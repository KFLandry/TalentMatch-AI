package org.example.talentmatch_ai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "matchingResult")
public class MatchingResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "candidate_id required")
    @OneToOne
    @JoinColumn(name= "candidate_id", referencedColumnName = "id")
    private Candidate candidateId;

    @NotBlank(message = "jobOffer_id required")
    @OneToOne
    @JoinColumn(name= "jobOfferId", referencedColumnName = "id")
    private JobOffer jobOfferId;

    @NotBlank(message="score field is required")
    @DecimalMin(value = "0.00", message = "score must be >= 0")
    @DecimalMax(value = "100.00", message = "score must be <= 100")
    private Integer score;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String Analysis;

    public enum Status {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    @NotBlank(message = "Status field is required")
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant requestedAt;

    //fait au niveau métier (service)
    private Status completedAt;

    //pareil
    private String errorMessage;

}
