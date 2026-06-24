package com.rapidx.audit.specification;

import com.rapidx.audit.entity.AuditLog;
import org.springframework.data.mongodb.core.query.Criteria;
import java.util.ArrayList;
import java.util.List;

public class AuditLogSpecification {

    public static MongoSpecification<AuditLog> filterLogs(
            String action,
            String username,
            String details
    ) {
        return () -> {
            List<Criteria> criteriaList = new ArrayList<>();

            if (action != null && !action.trim().isEmpty()) {
                criteriaList.add(Criteria.where("action").regex(".*" + action + ".*", "i"));
            }

            if (username != null && !username.trim().isEmpty()) {
                criteriaList.add(Criteria.where("username").regex(".*" + username + ".*", "i"));
            }

            if (details != null && !details.trim().isEmpty()) {
                criteriaList.add(Criteria.where("details").regex(".*" + details + ".*", "i"));
            }

            if (criteriaList.isEmpty()) {
                return new Criteria();
            }

            return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        };
    }
}
