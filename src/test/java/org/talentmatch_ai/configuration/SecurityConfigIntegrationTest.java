package org.talentmatch_ai.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.talentmatch_ai.dto.MatchingDto;
import org.talentmatch_ai.dto.MatchingRequest;
import org.talentmatch_ai.model.Candidate;
import org.talentmatch_ai.model.Status;
import org.talentmatch_ai.service.CandidatesService;
import org.talentmatch_ai.service.MatchingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MatchingService matchingService;

    @MockitoBean
    private CandidatesService candidatesService;


    @Test
    void matchingAnalyze_shouldReturnUnauthorizedWithoutCredentials() throws Exception {
        String requestBody = "{" +
                "\"candidateId\":\"" + UUID.randomUUID() + "\"," +
                "\"jobOfferId\":\"" + UUID.randomUUID() + "\"" +
                "}";

        mockMvc.perform(post("/api/matching/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void matchingAnalyze_shouldAllowAuthorizedUser() throws Exception {
        MatchingRequest request = new MatchingRequest();
        request.setCandidateId(UUID.randomUUID());
        request.setJobOfferId(UUID.randomUUID());

        String requestBody = "{" +
                "\"candidateId\":\"" + request.getCandidateId() + "\"," +
                "\"jobOfferId\":\"" + request.getJobOfferId() + "\"" +
                "}";

        when(matchingService.analyzeMatch(any(MatchingRequest.class))).thenReturn(
                MatchingDto.builder()
                        .id(UUID.randomUUID())
                        .status(Status.PENDING)
                        .requestedAt(LocalDateTime.now())
                        .message("L'analyse a ete soumise et sera traitee prochainement")
                        .build()
        );

        mockMvc.perform(post("/api/matching/analyze")
                        .with(httpBasic("recruiter", "recruiter123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());
    }

    @Test
    void candidateImport_shouldReturnUnauthorizedWithoutCredentials() throws Exception {
        mockMvc.perform(post("/api/candidate/import/test-user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void candidateImport_shouldAllowAuthorizedUser() throws Exception {
        Candidate candidate = Candidate.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .skills(List.of("Java", "Spring"))
                .yearsOfExperience(4)
                .createdAt(LocalDateTime.now())
                .build();

        when(candidatesService.buildCandidateFromGithub("kflandry")).thenReturn(candidate);

        mockMvc.perform(post("/api/candidate/import/kflandry")
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isCreated());
    }

    @Test
    void publicEndpoint_shouldRemainAccessibleWithoutCredentials() throws Exception {
        when(candidatesService.getAllCandidates()).thenReturn(List.of());

        mockMvc.perform(get("/api/candidate"))
                .andExpect(status().isOk());
    }
}

