package com.talentbridge.notification;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notificationLogs")
public class NotificationLog {
    @Id
    private String id;
    private String templateKey;
    private String to;
    private String referenceId;
    private Instant sentAt = Instant.now();

    public NotificationLog() {
    }

    public NotificationLog(String templateKey, String to, String referenceId) {
        this.templateKey = templateKey;
        this.to = to;
        this.referenceId = referenceId;
    }

    public String getId() {
        return id;
    }

    public String getTemplateKey() {
        return templateKey;
    }

    public String getTo() {
        return to;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}
