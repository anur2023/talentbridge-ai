package com.talentbridge.finance;

import com.talentbridge.common.BaseEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entries are never overwritten; a correction is a new entry (REVERSED,
 * DISPUTED, etc.), so the ledger itself is append-only in spirit even though
 * it is a normal collection (status is advanced, not rewritten in place,
 * except by the controlled transitions in RewardLedgerService).
 */
@Document(collection = "rewardTransactions")
public class RewardTransaction extends BaseEntity {

    @Id
    private String id;
    private String referralId;
    private String candidateUserId;
    private String platformId;
    private RewardStatus status = RewardStatus.EXPECTED;
    /** Amount stored in minor units (e.g. paise/cents) to avoid floating point errors. */
    private long amountMinorUnits;
    private String currencyCode;
    private BigDecimal fxRateToInr;
    private Long amountInrMinorUnits;
    private BigDecimal feesMinorUnits;
    private LocalDate receivedDate;
    private String evidenceReference;
    private String recordedByUserId;
    private String secondApproverUserId;
    private Instant createdAtEvent = Instant.now();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public String getCandidateUserId() {
        return candidateUserId;
    }

    public void setCandidateUserId(String candidateUserId) {
        this.candidateUserId = candidateUserId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public RewardStatus getStatus() {
        return status;
    }

    public void setStatus(RewardStatus status) {
        this.status = status;
    }

    public long getAmountMinorUnits() {
        return amountMinorUnits;
    }

    public void setAmountMinorUnits(long amountMinorUnits) {
        this.amountMinorUnits = amountMinorUnits;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getFxRateToInr() {
        return fxRateToInr;
    }

    public void setFxRateToInr(BigDecimal fxRateToInr) {
        this.fxRateToInr = fxRateToInr;
    }

    public Long getAmountInrMinorUnits() {
        return amountInrMinorUnits;
    }

    public void setAmountInrMinorUnits(Long amountInrMinorUnits) {
        this.amountInrMinorUnits = amountInrMinorUnits;
    }

    public BigDecimal getFeesMinorUnits() {
        return feesMinorUnits;
    }

    public void setFeesMinorUnits(BigDecimal feesMinorUnits) {
        this.feesMinorUnits = feesMinorUnits;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getEvidenceReference() {
        return evidenceReference;
    }

    public void setEvidenceReference(String evidenceReference) {
        this.evidenceReference = evidenceReference;
    }

    public String getRecordedByUserId() {
        return recordedByUserId;
    }

    public void setRecordedByUserId(String recordedByUserId) {
        this.recordedByUserId = recordedByUserId;
    }

    public String getSecondApproverUserId() {
        return secondApproverUserId;
    }

    public void setSecondApproverUserId(String secondApproverUserId) {
        this.secondApproverUserId = secondApproverUserId;
    }

    public Instant getCreatedAtEvent() {
        return createdAtEvent;
    }
}
