package com.talentbridge.consent;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Append-only event. Never updated or deleted — a withdrawal is a new event,
 * not an edit of the grant (FR-CON-01 to FR-CON-07). The repository must
 * only ever be used with insert and read, enforced by ConsentRepository's
 * narrow interface (no delete/update methods exposed to callers).
 */
@Document(collection = "consentRecords")
public class ConsentRecord {

    @Id
    private String id;
    private String userId;
    private ConsentPurpose purpose;
    /** Required (and only meaningful) when purpose == REFERRAL_SHARING. */
    private String recipientPlatformId;
    private ConsentEventType eventType;
    private String noticeVersion;
    private Instant at;
    private String source;
    private String actorUserId;

    public ConsentRecord() {
    }

    public ConsentRecord(String userId, ConsentPurpose purpose, String recipientPlatformId,
                          ConsentEventType eventType, String noticeVersion, String source, String actorUserId) {
        this.userId = userId;
        this.purpose = purpose;
        this.recipientPlatformId = recipientPlatformId;
        this.eventType = eventType;
        this.noticeVersion = noticeVersion;
        this.at = Instant.now();
        this.source = source;
        this.actorUserId = actorUserId;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public ConsentPurpose getPurpose() {
        return purpose;
    }

    public String getRecipientPlatformId() {
        return recipientPlatformId;
    }

    public ConsentEventType getEventType() {
        return eventType;
    }

    public String getNoticeVersion() {
        return noticeVersion;
    }

    public Instant getAt() {
        return at;
    }

    public String getSource() {
        return source;
    }

    public String getActorUserId() {
        return actorUserId;
    }
}
