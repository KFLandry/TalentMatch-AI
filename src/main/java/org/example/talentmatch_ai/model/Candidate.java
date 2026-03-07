package org.example.talentmatch_ai.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
public class Candidate {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;
}
