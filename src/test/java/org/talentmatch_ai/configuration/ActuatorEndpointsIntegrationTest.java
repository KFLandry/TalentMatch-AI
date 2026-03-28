package org.talentmatch_ai.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.talentmatch_ai.model.Status;
import org.talentmatch_ai.repository.MatchingRepo;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ActuatorEndpointsIntegrationTest {

    private final MockMvc mockMvc;

    ActuatorEndpointsIntegrationTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @MockitoBean
    private MatchingRepo matchingRepo;

    @Test
    void matchingStatsEndpoint_shouldReturnAggregatedStats() throws Exception {
        when(matchingRepo.countByStatus(Status.PENDING)).thenReturn(2L);
        when(matchingRepo.countByStatus(Status.PROCESSING)).thenReturn(1L);
        when(matchingRepo.countByStatus(Status.COMPLETED)).thenReturn(5L);
        when(matchingRepo.countByStatus(Status.FAILED)).thenReturn(2L);

        mockMvc.perform(get("/actuator/matchingstats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.pending").value(2))
                .andExpect(jsonPath("$.processing").value(1))
                .andExpect(jsonPath("$.completed").value(5))
                .andExpect(jsonPath("$.failed").value(2));
    }

    @Test
    void metricsEndpoint_shouldBeExposed() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").exists());
    }
}

