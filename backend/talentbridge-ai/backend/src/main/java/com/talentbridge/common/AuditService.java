package com.talentbridge.common;

import org.springframework.stereotype.Service;

/**
 * Every admin write must go through this. Append-only: AuditLogRepository is
 * never used to update or delete a record, only to insert and read.
 */
@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void record(String actorUserId, String actorRole, String action, String targetType, String targetId,
                        String detail) {
        repository.save(new AuditLog(actorUserId, actorRole, action, targetType, targetId, detail,
                CorrelationIdFilter.current()));
    }
}
