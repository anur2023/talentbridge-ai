package com.talentbridge.opportunity.support;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "listingReports")
public class ListingReport {
    @Id
    private String id;
    private String opportunityId;
    private String reason;
    private String reporterEmail;
    private Instant createdAt = Instant.now();

    public ListingReport() {
    }

    public ListingReport(String opportunityId, String reason, String reporterEmail) {
        this.opportunityId = opportunityId;
        this.reason = reason;
        this.reporterEmail = reporterEmail;
    }

    public String getId() {
        return id;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public String getReason() {
        return reason;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
