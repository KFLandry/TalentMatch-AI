package org.talentmatch_ai.exception;

public class GithubException extends Exception {

    public GithubException(String message, Exception e) {
        super(message);
    }

    public GithubException(String message) {
        super(message);
    }
}
