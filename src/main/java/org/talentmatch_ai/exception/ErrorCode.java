package org.talentmatch_ai.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    VALIDATION_ERROR("Validation failed"),
    GITHUB_IMPORT_ERROR("GitHub API error"),
    NOT_FOUND("Resource not found"),
    INTERNAL_ERROR("Internal server error"),
    AI_SERVICE_ERROR("AI service unavailable");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

}
