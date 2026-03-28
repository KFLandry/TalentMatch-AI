package org.talentmatch_ai.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.talentmatch_ai.dto.GithubProfile;
import org.talentmatch_ai.dto.GithubRepo;
import org.talentmatch_ai.exception.GithubException;

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

    public List<GithubRepo> getReposByUsername(String username) throws GithubException {
        try{
            return restClient.get()
                    .uri("/users/{username}/repos", username)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<GithubRepo>>() {});
        }catch (Exception e) {
            throw new GithubException("Failed to fetch GitHub repositories for user: " + username, e);
        }
    }

    public GithubProfile getUserProfile(String username) throws GithubException {
        try {
            return restClient.get()
                    .uri("/users/{username}", username)
                    .retrieve()
                    .body(GithubProfile.class);
        } catch (Exception e) {
            throw new GithubException("Failed to fetch GitHub profile for user: " + username, e);
        }
    }
}