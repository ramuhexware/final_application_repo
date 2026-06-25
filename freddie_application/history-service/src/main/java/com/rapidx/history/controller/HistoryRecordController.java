package com.rapidx.history.controller;

import com.rapidx.history.entity.HistoryRecord;
import com.rapidx.history.repository.HistoryRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryRecordController {

    private final HistoryRecordRepository historyRecordRepository;
    private final com.rapidx.history.messaging.HistoryKafkaProducer kafkaProducer;

    public HistoryRecordController(HistoryRecordRepository historyRecordRepository, com.rapidx.history.messaging.HistoryKafkaProducer kafkaProducer) {
        this.historyRecordRepository = historyRecordRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping
    public ResponseEntity<HistoryRecord> createHistoryRecord(@RequestBody HistoryRecord record) {
        if (record.getCreatedAt() == null) {
            record.setCreatedAt(LocalDateTime.now());
        }
        HistoryRecord saved = historyRecordRepository.save(record);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/kafka")
    public ResponseEntity<String> createHistoryRecordViaKafka(@RequestBody HistoryRecord record) {
        if (record.getCreatedAt() == null) {
            record.setCreatedAt(LocalDateTime.now());
        }
        kafkaProducer.sendHistoryEvent(record);
        return ResponseEntity.accepted().body("History event published to Kafka successfully.");
    }

    @GetMapping
    public ResponseEntity<List<HistoryRecord>> getAllHistoryRecords() {
        List<HistoryRecord> records = historyRecordRepository.findAll();
        return ResponseEntity.ok(records);
    }
}
