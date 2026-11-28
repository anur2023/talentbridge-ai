package com.talentbridge.candidate;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "savedOpportunities")
@CompoundIndex(name = "user_opportunity_unique", def = "{'userId': 1, 'opportunityId': 1}", unique = true)
public class SavedOpportunity {

    @Id
    private String id;
    private String userId;
    private String opportunityId;
    private Instant savedAt = Instant.now();

    public SavedOpportunity() {
    }

    public SavedOpportunity(String userId, String opportunityId) {
        this.userId = userId;
        this.opportunityId = opportunityId;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public Instant getSavedAt() {
        return savedAt;
    }
}
