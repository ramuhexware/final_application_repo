package com.rapidx.report.controller;

import com.rapidx.report.entity.ReportRecord;
import com.rapidx.report.repository.ReportRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
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

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReports() {
        List<ReportRecord> records = reportRecordRepository.findAll();
        
        // Ensure export folder exists
        File exportDir = new File("exported_reports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        
        String filename = "reports_" + System.currentTimeMillis() + ".csv";
        File file = new File(exportDir, filename);
        
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Title,Content Findings,Generated At\n");
        for (ReportRecord record : records) {
            String title = escapeCsv(record.getTitle());
            String content = escapeCsv(record.getContent());
            String generatedAt = record.getGeneratedAt() != null ? record.getGeneratedAt().toString() : "";
            csvBuilder.append(title).append(",").append(content).append(",").append(generatedAt).append("\n");
        }
        
        String csvContent = csvBuilder.toString();
        
        // Write to local file (server-side File IO)
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(csvContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        byte[] csvBytes = csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "reports.csv");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importReports(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        
        // Ensure import folder exists
        File importDir = new File("uploaded_reports");
        if (!importDir.exists()) {
            importDir.mkdirs();
        }
        
        String filename = "imported_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File savedFile = new File(importDir, filename);
        
        // Write the incoming file stream to local file system (File IO)
        try {
            file.transferTo(savedFile);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save uploaded file: " + e.getMessage());
        }
        
        // Read and parse the file (File IO)
        List<ReportRecord> importedRecords = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(savedFile))) {
            String header = reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                List<String> tokens = parseCsvLine(line);
                if (tokens.size() >= 2) {
                    ReportRecord record = new ReportRecord();
                    record.setTitle(tokens.get(0));
                    record.setContent(tokens.get(1));
                    
                    LocalDateTime dt = LocalDateTime.now();
                    if (tokens.size() >= 3 && !tokens.get(2).isEmpty()) {
                        try {
                            dt = LocalDateTime.parse(tokens.get(2));
                        } catch (Exception ex) {
                            // fall back to now
                        }
                    }
                    record.setGeneratedAt(dt);
                    importedRecords.add(record);
                }
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to read/parse file: " + e.getMessage());
        }
        
        if (!importedRecords.isEmpty()) {
            reportRecordRepository.saveAll(importedRecords);
        }
        
        return ResponseEntity.ok(importedRecords);
    }

    private String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n") || val.contains("\r")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder curVal = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQuotes) {
                if (ch == '\"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                        curVal.append('\"');
                        i++; // skip next double quote
                    } else {
                        inQuotes = false;
                    }
                } else {
                    curVal.append(ch);
                }
            } else {
                if (ch == '\"') {
                    inQuotes = true;
                } else if (ch == ',') {
                    result.add(curVal.toString());
                    curVal.setLength(0);
                } else {
                    curVal.append(ch);
                }
            }
        }
        result.add(curVal.toString());
        return result;
    }
}

