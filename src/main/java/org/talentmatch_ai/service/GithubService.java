package org.talentmatch_ai.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.talentmatch_ai.dto.GithubProfile;
import org.talentmatch_ai.dto.GithubRepo;
import org.talentmatch_ai.model.Candidate;

import java.util.List;

@Service
public class GithubService {

    private final RestClient restClient;

    public GithubService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
    }

    public List<GithubRepo> getReposByUsername(String username) {
        return restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .body(new ParameterizedTypeReference<List<GithubRepo>>() {});
    }

    public GithubProfile getUserProfile(String username) {
        return restClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .body(GithubProfile.class);
    }
}