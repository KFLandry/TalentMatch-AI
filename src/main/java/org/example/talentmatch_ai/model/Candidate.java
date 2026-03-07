package org.example.talentmatch_ai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.w3c.dom.Text;

import javax.validation.constraints.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "candidate")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "champ firstName obligatoire")
    @Size(max = 255, message = "Max 255 caractères")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "champ lastName obligatoire")
    @Size(max = 255, message = "Max 255 caractères")
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "champ email obligatoire")
    @Size(max = 255, message = "Max 255 caractères")
    @Email
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "champ githubUserName obligatoire")
    @Size(max = 255, message = "Max 255 caractères")
    @Column(nullable = false, unique = true)
    private String githubUserName;

    @NotEmpty(message = "champ skills obligatoire")
    @ElementCollection
    @CollectionTable(name="candidate_skills", joinColumns = @JoinColumn(name="candidate_id"))
    @Column(nullable = false, name = "skill")
    // pour le mappage des List<String> car JPA ne peut pas mapper classiquement les list<String>
    private List<String> skills;

    @NotBlank(message = "champ yearsOfExperience obligatoire")
    @DecimalMin(value = "0.00", message = "yearsOfExperience must be >= 0")
    private Integer yearsOfExperience;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String bio;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

}
