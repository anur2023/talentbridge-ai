package com.talentbridge.referral;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Append-only. Every transition stores actor, time, previous status and evidence (Day 16 "done when"). */
@Document(collection = "referralEvents")
public class ReferralEvent {

    @Id
    private String id;
    private String referralId;
    private ReferralStatus previousStatus;
    private ReferralStatus newStatus;
    private String actorUserId;
    private String evidenceReference;
    private String note;
    private Instant at = Instant.now();

    public ReferralEvent() {
    }

    public ReferralEvent(String referralId, ReferralStatus previousStatus, ReferralStatus newStatus,
                          String actorUserId, String evidenceReference, String note) {
        this.referralId = referralId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.actorUserId = actorUserId;
        this.evidenceReference = evidenceReference;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public String getReferralId() {
        return referralId;
    }

    public ReferralStatus getPreviousStatus() {
        return previousStatus;
    }

    public ReferralStatus getNewStatus() {
        return newStatus;
    }

    public String getActorUserId() {
        return actorUserId;
    }

    public String getEvidenceReference() {
        return evidenceReference;
    }

    public String getNote() {
        return note;
    }

    public Instant getAt() {
        return at;
    }
}
