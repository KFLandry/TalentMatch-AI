package org.example.talentmatch_ai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
public class JobOffer {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "champ title obligatoire")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "champ company obligatoire")
    @Column(nullable = false)
    private String company;

    @NotEmpty(message = "champ requiredSkills obligatoire")
    @ElementCollection
    @Column(nullable = false)
    private List<String> requiredSkills;

    @NotBlank(message = "champ description obligatoire")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "champ location obligatoire")
    @Column(nullable = false)
    private String location;

    private String salaryRange;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant postedAt;

}