package com.talentbridge.common;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Append-only. Only inserted, never updated or deleted. */
@Document(collection = "auditLogs")
public class AuditLog {

    @Id
    private String id;
    private Instant at;
    private String actorUserId;
    private String actorRole;
    private String action;
    private String targetType;
    private String targetId;
    private String detail;
    private String correlationId;

    public AuditLog() {
    }

    public AuditLog(String actorUserId, String actorRole, String action, String targetType, String targetId,
                     String detail, String correlationId) {
        this.at = Instant.now();
        this.actorUserId = actorUserId;
        this.actorRole = actorRole;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.detail = detail;
        this.correlationId = correlationId;
    }

    public String getId() {
        return id;
    }

    public Instant getAt() {
        return at;
    }

    public String getActorUserId() {
        return actorUserId;
    }

    public String getActorRole() {
        return actorRole;
    }

    public String getAction() {
        return action;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getDetail() {
        return detail;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
