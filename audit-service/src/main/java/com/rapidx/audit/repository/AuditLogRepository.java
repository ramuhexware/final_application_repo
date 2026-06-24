package com.rapidx.audit.repository;

import com.rapidx.audit.entity.AuditLog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends ReactiveMongoRepository<AuditLog, String>, 
                                            ReactiveAuditLogRepository {
}
