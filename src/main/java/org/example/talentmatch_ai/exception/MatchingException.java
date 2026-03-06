package org.example.talentmatch_ai.exception;

public class MatchingException extends Exception{
    public MatchingException(String message) {
        super(message);
    }

    public MatchingException(String message, Throwable cause) {
        super(message, cause);
    }
}
