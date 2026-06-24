package com.rapidx.audit.repository;

import com.rapidx.audit.entity.AuditLog;
import com.rapidx.audit.specification.MongoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class ReactiveAuditLogRepositoryImpl implements ReactiveAuditLogRepository {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public ReactiveAuditLogRepositoryImpl(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    private Query getQuery(MongoSpecification<AuditLog> spec) {
        Query query = new Query();
        if (spec != null) {
            query.addCriteria(spec.toMongoCriteria());
        }
        return query;
    }

    @Override
    public Flux<AuditLog> findAll(MongoSpecification<AuditLog> spec) {
        return reactiveMongoTemplate.find(getQuery(spec), AuditLog.class);
    }

    @Override
    public Mono<Page<AuditLog>> findAll(MongoSpecification<AuditLog> spec, Pageable pageable) {
        Query query = getQuery(spec);
        Mono<Long> countMono = reactiveMongoTemplate.count(query, AuditLog.class);
        
        Query pagedQuery = Query.of(query).with(pageable);
        Mono<List<AuditLog>> listMono = reactiveMongoTemplate.find(pagedQuery, AuditLog.class).collectList();
        
        return Mono.zip(listMono, countMono)
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }
}
