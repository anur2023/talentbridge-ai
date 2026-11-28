package com.talentbridge.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    Page<AuditLog> findByTargetTypeAndTargetId(String targetType, String targetId, Pageable pageable);
}
