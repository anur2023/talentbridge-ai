package com.talentbridge.analytics;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Cookieless, aggregate-only. Never store an email, name or candidate ID
 * here (Day 20 "done when" + the 12 critical acceptance criteria).
 */
@Document(collection = "analyticsEvents")
public class AnalyticsEvent {

    @Id
    private String id;
    private AnalyticsEventType type;
    private String opportunityId;
    private String utmSource;
    private LocalDate day = LocalDate.now();
    private Instant at = Instant.now();

    public AnalyticsEvent() {
    }

    public AnalyticsEvent(AnalyticsEventType type, String opportunityId, String utmSource) {
        this.type = type;
        this.opportunityId = opportunityId;
        this.utmSource = utmSource;
    }

    public String getId() {
        return id;
    }

    public AnalyticsEventType getType() {
        return type;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public String getUtmSource() {
        return utmSource;
    }

    public LocalDate getDay() {
        return day;
    }

    public Instant getAt() {
        return at;
    }
}
