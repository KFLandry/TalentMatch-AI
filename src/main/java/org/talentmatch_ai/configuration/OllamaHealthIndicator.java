package org.talentmatch_ai.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component("ollama")
public class OllamaHealthIndicator implements HealthIndicator {

    private final HttpClient httpClient;
    private final String baseUrl;

    public OllamaHealthIndicator(@Value("${spring.ai.ollama.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    @Override
    public Health health() {
        String healthUrl = baseUrl.endsWith("/") ? baseUrl + "api/tags" : baseUrl + "/api/tags";
        HttpRequest request = HttpRequest.newBuilder(URI.create(healthUrl))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode < 300) {
                return Health.up()
                        .withDetail("url", healthUrl)
                        .withDetail("httpStatus", statusCode)
                        .build();
            }

            return Health.down()
                    .withDetail("url", healthUrl)
                    .withDetail("httpStatus", statusCode)
                    .build();
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return Health.down(ex)
                    .withDetail("url", healthUrl)
                    .build();
        }
    }
}

