package com.rapidx.history.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidx.history.entity.HistoryRecord;
import com.rapidx.history.repository.HistoryRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class HistoryKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(HistoryKafkaConsumer.class);

    private final HistoryRecordRepository repository;
    private final ObjectMapper objectMapper;

    private HistoryRecord lastReceivedRecord;

    public HistoryKafkaConsumer(HistoryRecordRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${history.kafka.topic:history-topic}", groupId = "history-group")
    public void consumeHistoryEvent(String message) {
        log.info("Received Kafka message: {}", message);
        try {
            HistoryRecord record = objectMapper.readValue(message, HistoryRecord.class);
            record.setId(null); // Clear ID to let DB2 auto-generate it
            HistoryRecord saved = repository.save(record);
            log.info("Successfully persisted HistoryRecord to DB2: {}", saved);
            this.lastReceivedRecord = saved;
        } catch (Exception e) {
            log.error("Failed to process consumed Kafka message: {}", message, e);
        }
    }

    public HistoryRecord getLastReceivedRecord() {
        return lastReceivedRecord;
    }

    public void clearLastReceivedRecord() {
        this.lastReceivedRecord = null;
    }
}
