package org.example.talentmatch_ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchingResultMessage {
    private String matchingId;
    private String candidateId;
    private String jobOfferId;
}
