package com.rapidx.history.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidx.history.entity.HistoryRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class HistoryKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(HistoryKafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${history.kafka.topic:history-topic}")
    private String topicName;

    public HistoryKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendHistoryEvent(HistoryRecord record) {
        log.info("Sending HistoryRecord to Kafka topic {}: {}", topicName, record);
        try {
            String jsonPayload = objectMapper.writeValueAsString(record);
            kafkaTemplate.send(topicName, jsonPayload);
        } catch (Exception e) {
            log.error("Failed to send HistoryRecord to Kafka topic {}", topicName, e);
        }
    }
}
