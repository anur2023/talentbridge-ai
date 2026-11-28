package com.talentbridge.referral;

import com.talentbridge.common.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** One candidate may only have one active (non-closed/withdrawn) referral per opportunity. */
@Document(collection = "candidateReferrals")
@CompoundIndex(name = "candidate_opportunity", def = "{'candidateUserId': 1, 'opportunityId': 1}")
public class CandidateReferral extends BaseEntity {

    @Id
    private String id;
    private String candidateUserId;
    private String opportunityId;
    private String platformId;
    private ReferralStatus status = ReferralStatus.DRAFT;

    @Indexed(unique = true, sparse = true)
    private String idempotencyKey;
    private boolean disclosureAcknowledged;
    private String evidenceReference;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCandidateUserId() {
        return candidateUserId;
    }

    public void setCandidateUserId(String candidateUserId) {
        this.candidateUserId = candidateUserId;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public ReferralStatus getStatus() {
        return status;
    }

    public void setStatus(ReferralStatus status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public boolean isDisclosureAcknowledged() {
        return disclosureAcknowledged;
    }

    public void setDisclosureAcknowledged(boolean disclosureAcknowledged) {
        this.disclosureAcknowledged = disclosureAcknowledged;
    }

    public String getEvidenceReference() {
        return evidenceReference;
    }

    public void setEvidenceReference(String evidenceReference) {
        this.evidenceReference = evidenceReference;
    }
}
