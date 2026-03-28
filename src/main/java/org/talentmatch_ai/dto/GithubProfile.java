package org.talentmatch_ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GithubProfile {
    private String login;
    private String bio;
    private String name;
    private String email;

    @JsonProperty("public_repos")
    private Integer publicRepos;
}