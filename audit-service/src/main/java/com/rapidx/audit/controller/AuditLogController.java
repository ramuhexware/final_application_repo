package com.rapidx.audit.controller;

import com.rapidx.audit.entity.AuditLog;
import com.rapidx.audit.repository.AuditLogRepository;
import com.rapidx.audit.specification.AuditLogSpecification;
import com.rapidx.audit.specification.MongoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuditLog> createAuditLog(@RequestBody AuditLog auditLog) {
        if (auditLog.getTimestamp() == null) {
            auditLog.setTimestamp(LocalDateTime.now());
        }
        return auditLogRepository.save(auditLog);
    }

    @GetMapping
    public Mono<ResponseEntity<Page<AuditLog>>> getAllAuditLogs(@PageableDefault(size = 20) Pageable pageable) {
        return auditLogRepository.findAll((MongoSpecification<AuditLog>) null, pageable)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AuditLog>> getAuditLogById(@PathVariable String id) {
        return auditLogRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public Mono<ResponseEntity<Page<AuditLog>>> filterAuditLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String details,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        MongoSpecification<AuditLog> spec = AuditLogSpecification.filterLogs(action, username, details);
        return auditLogRepository.findAll(spec, pageable)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<AuditLog>> updateAuditLog(@PathVariable String id, @RequestBody AuditLog auditLogDetails) {
        return auditLogRepository.findById(id)
                .flatMap(existingLog -> {
                    if (auditLogDetails.getAction() != null) {
                        existingLog.setAction(auditLogDetails.getAction());
                    }
                    if (auditLogDetails.getUsername() != null) {
                        existingLog.setUsername(auditLogDetails.getUsername());
                    }
                    if (auditLogDetails.getDetails() != null) {
                        existingLog.setDetails(auditLogDetails.getDetails());
                    }
                    if (auditLogDetails.getTimestamp() != null) {
                        existingLog.setTimestamp(auditLogDetails.getTimestamp());
                    }
                    return auditLogRepository.save(existingLog);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAuditLog(@PathVariable String id) {
        return auditLogRepository.findById(id)
                .flatMap(log -> auditLogRepository.delete(log)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
