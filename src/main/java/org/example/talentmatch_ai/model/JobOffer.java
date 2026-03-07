package org.example.talentmatch_ai.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "job_offers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobOffer {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String company;

    @NotEmpty(message = "Au moins une compétence est requise")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_offer_required_skills", joinColumns = @JoinColumn(name = "job_offer_id"))
    @Column(name = "required_skill", nullable = false)
    @Builder.Default
    private List<String> requiredSkills = new ArrayList<>();

    @NotBlank
    @Lob
    @Column(nullable = false)
    private String description;

    @NotBlank
    @Column(nullable = false)
    private String location;

    private String salaryRange;

    @Column(nullable = false, updatable = false)
    private LocalDateTime postedAt;

    @PrePersist
    void onCreate() {
        if (postedAt == null) {
            postedAt = LocalDateTime.now();
        }
    }
}
