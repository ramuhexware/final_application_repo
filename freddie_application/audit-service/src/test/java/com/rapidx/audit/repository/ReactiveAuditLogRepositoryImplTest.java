package com.rapidx.audit.repository;

import com.rapidx.audit.entity.AuditLog;
import com.rapidx.audit.specification.AuditLogSpecification;
import com.rapidx.audit.specification.MongoSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReactiveAuditLogRepositoryImplTest {

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @InjectMocks
    private ReactiveAuditLogRepositoryImpl repository;

    private MongoSpecification<AuditLog> spec;

    @BeforeEach
    void setUp() {
        spec = AuditLogSpecification.filterLogs("USER_LOGIN", "john_doe", "Logged");
    }

    @Test
    void testFindAll_WithSpec() {
        when(reactiveMongoTemplate.find(any(Query.class), eq(AuditLog.class)))
                .thenReturn(Flux.empty());

        StepVerifier.create(repository.findAll(spec))
                .expectSubscription()
                .expectComplete()
                .verify();

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        verify(reactiveMongoTemplate).find(queryCaptor.capture(), eq(AuditLog.class));
        assertNotNull(queryCaptor.getValue());
    }

    @Test
    void testFindAll_WithSpecAndPageable() {
        Pageable pageable = PageRequest.of(0, 10);
        
        when(reactiveMongoTemplate.count(any(Query.class), eq(AuditLog.class)))
                .thenReturn(Mono.just(0L));
        when(reactiveMongoTemplate.find(any(Query.class), eq(AuditLog.class)))
                .thenReturn(Flux.empty());

        StepVerifier.create(repository.findAll(spec, pageable))
                .assertNext(page -> {
                    assertNotNull(page);
                })
                .expectComplete()
                .verify();

        verify(reactiveMongoTemplate).count(any(Query.class), eq(AuditLog.class));
        verify(reactiveMongoTemplate).find(any(Query.class), eq(AuditLog.class));
    }
}
