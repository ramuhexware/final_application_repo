package com.rapidx.audit.controller;

import com.rapidx.audit.entity.AuditLog;
import com.rapidx.audit.repository.AuditLogRepository;
import com.rapidx.audit.specification.MongoSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rapidx.audit.config.WebFluxConfig;

@WebFluxTest(controllers = {AuditLogController.class, WebFluxConfig.class})
public class AuditLogControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuditLogRepository auditLogRepository;

    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = new AuditLog("1", "USER_LOGIN", "john_doe", "Logged in", LocalDateTime.of(2026, 6, 18, 10, 15, 30));
    }

    @Test
    void testCreateAuditLog() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(Mono.just(auditLog));

        webTestClient.post()
                .uri("/api/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(auditLog)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.action").isEqualTo("USER_LOGIN")
                .jsonPath("$.username").isEqualTo("john_doe");

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetAllAuditLogs() {
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(auditLog));
        when(auditLogRepository.findAll(any(), any(Pageable.class))).thenReturn(Mono.just(page));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/audit")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].id").isEqualTo("1")
                .jsonPath("$.content[0].action").isEqualTo("USER_LOGIN");
    }

    @Test
    void testGetAuditLogById_Success() {
        when(auditLogRepository.findById("1")).thenReturn(Mono.just(auditLog));

        webTestClient.get()
                .uri("/api/audit/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.action").isEqualTo("USER_LOGIN");
    }

    @Test
    void testGetAuditLogById_NotFound() {
        when(auditLogRepository.findById("2")).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/audit/2")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFilterAuditLogs() {
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(auditLog));
        when(auditLogRepository.findAll(any(), any(Pageable.class))).thenReturn(Mono.just(page));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/audit/filter")
                        .queryParam("action", "USER_LOGIN")
                        .queryParam("username", "john_doe")
                        .queryParam("details", "Logged")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].id").isEqualTo("1");

        ArgumentCaptor<MongoSpecification> specCaptor = ArgumentCaptor.forClass(MongoSpecification.class);
        verify(auditLogRepository, times(1)).findAll(specCaptor.capture(), any(Pageable.class));
        assertNotNull(specCaptor.getValue());
    }

    @Test
    void testUpdateAuditLog_Success() {
        AuditLog updateDetails = new AuditLog(null, "USER_LOGOUT", "john_doe", "Logged out", null);
        AuditLog updatedLog = new AuditLog("1", "USER_LOGOUT", "john_doe", "Logged out", auditLog.getTimestamp());

        when(auditLogRepository.findById("1")).thenReturn(Mono.just(auditLog));
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(Mono.just(updatedLog));

        webTestClient.put()
                .uri("/api/audit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDetails)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.action").isEqualTo("USER_LOGOUT")
                .jsonPath("$.details").isEqualTo("Logged out");
    }

    @Test
    void testDeleteAuditLog_Success() {
        when(auditLogRepository.findById("1")).thenReturn(Mono.just(auditLog));
        when(auditLogRepository.delete(auditLog)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/audit/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(auditLogRepository, times(1)).delete(auditLog);
    }
}
