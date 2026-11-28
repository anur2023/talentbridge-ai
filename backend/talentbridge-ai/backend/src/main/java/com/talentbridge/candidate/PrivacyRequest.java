package com.talentbridge.candidate;

import com.talentbridge.common.BaseEntity;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "privacyRequests")
public class PrivacyRequest extends BaseEntity {

    @Id
    private String id;
    private String userId;
    private PrivacyRequestType type;
    private PrivacyRequestStatus status = PrivacyRequestStatus.OPEN;
    private String details;
    private Instant dueAt;
    private String resolutionNote;

    public PrivacyRequest() {
    }

    public PrivacyRequest(String userId, PrivacyRequestType type, String details, Instant dueAt) {
        this.userId = userId;
        this.type = type;
        this.details = details;
        this.dueAt = dueAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public PrivacyRequestType getType() {
        return type;
    }

    public PrivacyRequestStatus getStatus() {
        return status;
    }

    public void setStatus(PrivacyRequestStatus status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }
}
