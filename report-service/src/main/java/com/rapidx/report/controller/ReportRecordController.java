package com.rapidx.report.controller;

import com.rapidx.report.entity.ReportRecord;
import com.rapidx.report.repository.ReportRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportRecordController {

    private final ReportRecordRepository reportRecordRepository;

    public ReportRecordController(ReportRecordRepository reportRecordRepository) {
        this.reportRecordRepository = reportRecordRepository;
    }

    @PostMapping
    public ResponseEntity<ReportRecord> createReportRecord(@RequestBody ReportRecord record) {
        if (record.getGeneratedAt() == null) {
            record.setGeneratedAt(LocalDateTime.now());
        }
        ReportRecord saved = reportRecordRepository.save(record);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReportRecord>> getAllReportRecords() {
        List<ReportRecord> records = reportRecordRepository.findAll();
        return ResponseEntity.ok(records);
    }
}
