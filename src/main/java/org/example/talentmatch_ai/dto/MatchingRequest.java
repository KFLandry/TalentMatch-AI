package org.example.talentmatch_ai.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class MatchingRequest {
    private UUID candidateId;
    private  UUID jobOfferId;
}
