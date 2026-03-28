package org.talentmatch_ai.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private ErrorCode code;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> details;
    private String path;

}
