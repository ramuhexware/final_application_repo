package com.rapidx.audit.repository;

import com.rapidx.audit.entity.AuditLog;
import com.rapidx.audit.specification.MongoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveAuditLogRepository {
    Flux<AuditLog> findAll(MongoSpecification<AuditLog> spec);
    Mono<Page<AuditLog>> findAll(MongoSpecification<AuditLog> spec, Pageable pageable);
}
