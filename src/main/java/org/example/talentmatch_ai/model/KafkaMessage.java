package org.example.talentmatch_ai.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class KafkaMessage {
    private String matchingId;
    private String candidateId;
    private String jobOfferId;
}
