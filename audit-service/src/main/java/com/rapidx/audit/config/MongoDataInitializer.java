package com.rapidx.audit.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidx.audit.entity.AuditLog;
import com.rapidx.audit.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class MongoDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MongoDataInitializer.class);

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public MongoDataInitializer(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        Long count = auditLogRepository.count().block();
        if (count == null || count == 0) {
            logger.info("MongoDB audit_logs collection is empty. Seeding data from JSON file...");
            try (InputStream inputStream = getClass().getResourceAsStream("/audit-logs.json")) {
                if (inputStream == null) {
                    logger.warn("audit-logs.json not found in resources!");
                    return;
                }
                List<AuditLog> logs = objectMapper.readValue(inputStream, new TypeReference<List<AuditLog>>() {});
                auditLogRepository.saveAll(logs).collectList().block();
                logger.info("Successfully seeded {} audit logs into MongoDB.", logs.size());
            } catch (Exception e) {
                logger.error("Failed to seed MongoDB data: {}", e.getMessage(), e);
            }
        } else {
            logger.info("MongoDB audit_logs collection already contains data. Skipping seeding.");
        }
    }
}
