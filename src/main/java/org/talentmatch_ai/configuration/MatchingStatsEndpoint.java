package org.talentmatch_ai.configuration;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import org.talentmatch_ai.model.Status;
import org.talentmatch_ai.repository.MatchingRepo;

import java.util.Map;

@Component
@Endpoint(id = "matchingstats")
public class MatchingStatsEndpoint {

    private final MatchingRepo matchingRepo;

    public MatchingStatsEndpoint(MatchingRepo matchingRepo) {
        this.matchingRepo = matchingRepo;
    }

    @ReadOperation
    public Map<String, Object> matchingStats() {
        long pending = matchingRepo.countByStatus(Status.PENDING);
        long processing = matchingRepo.countByStatus(Status.PROCESSING);
        long completed = matchingRepo.countByStatus(Status.COMPLETED);
        long failed = matchingRepo.countByStatus(Status.FAILED);
        long total = pending + processing + completed + failed;

        double completionRate = total == 0 ? 0.0 : (double) completed / total;

        return Map.of(
                "total", total,
                "pending", pending,
                "processing", processing,
                "completed", completed,
                "failed", failed,
                "completionRate", completionRate
        );
    }
}

