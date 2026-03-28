package org.talentmatch_ai.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component("kafka")
public class KafkaHealthIndicator implements HealthIndicator {

    private final String bootstrapServers;

    public KafkaHealthIndicator(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    @Override
    public Health health() {
        Map<String, Object> properties = Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(properties)) {
            int topicCount = adminClient.listTopics()
                    .names()
                    .get(Duration.ofSeconds(3).toMillis(), TimeUnit.MILLISECONDS)
                    .size();

            return Health.up()
                    .withDetail("bootstrapServers", bootstrapServers)
                    .withDetail("topicCount", topicCount)
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("bootstrapServers", bootstrapServers)
                    .build();
        }
    }
}

