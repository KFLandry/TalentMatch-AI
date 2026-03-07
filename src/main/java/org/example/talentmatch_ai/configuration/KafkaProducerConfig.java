package org.example.talentmatch_ai.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.example.talentmatch_ai.model.MatchingResultMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS_CONFIG;

    @Value("${spring.kafka.producer.key-serializer}")
    private String KEY_SERIALIZER_CLASS_CONFIG;

    @Value("${spring.kafka.producer.value-serializer}")
    private String VALUE_SERIALIZER_CLASS_CONFIG;

    @Value("${spring.kafka.producer.type-mappings}")
    private String TYPE_MAPPINGS;




    @Bean
    public ProducerFactory<String, MatchingResultMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_CONFIG);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KEY_SERIALIZER_CLASS_CONFIG);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER_CLASS_CONFIG);
        configProps.put(JacksonJsonDeserializer.TYPE_MAPPINGS, TYPE_MAPPINGS);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, MatchingResultMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
