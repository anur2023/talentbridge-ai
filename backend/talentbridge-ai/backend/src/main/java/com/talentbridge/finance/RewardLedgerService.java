package com.talentbridge.finance;

import com.talentbridge.common.ApiException;
import com.talentbridge.common.AuditService;
import com.talentbridge.finance.dto.RecordRewardTransactionRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Enforces: PAID requires a prior CONFIRMED entry with a received date and
 * evidence; estimates (EXPECTED) are never overwritten, only followed by a
 * new entry; expected rewards are never summed into paid revenue (Day 19
 * "done when" + the 12 critical acceptance criteria).
 */
@Service
public class RewardLedgerService {

    private final RewardTransactionRepository repository;
    private final AuditService auditService;
    private final boolean secondApproverRequired;

    public RewardLedgerService(RewardTransactionRepository repository, AuditService auditService,
                                @Value("${talentbridge.finance.second-approver-required:false}") boolean secondApproverRequired) {
        this.repository = repository;
        this.auditService = auditService;
        this.secondApproverRequired = secondApproverRequired;
    }

    public RewardTransaction recordExpected(RecordRewardTransactionRequest request, String actorUserId, String actorRole) {
        RewardTransaction tx = new RewardTransaction();
        tx.setReferralId(request.referralId());
        tx.setCandidateUserId(request.candidateUserId());
        tx.setPlatformId(request.platformId());
        tx.setStatus(RewardStatus.EXPECTED);
        tx.setAmountMinorUnits(request.amountMinorUnits());
        tx.setCurrencyCode(request.currencyCode());
        tx.setFxRateToInr(request.fxRateToInr());
        tx.setFeesMinorUnits(request.feesMinorUnits());
        tx.setReceivedDate(request.receivedDate());
        tx.setEvidenceReference(request.evidenceReference());
        tx.setRecordedByUserId(actorUserId);
        applyFx(tx);
        RewardTransaction saved = repository.save(tx);
        auditService.record(actorUserId, actorRole, "REWARD_EXPECTED", "RewardTransaction", saved.getId(), null);
        return saved;
    }

    /** Advances the referral's ledger by one new entry; never edits a prior entry's status. */
    public RewardTransaction advance(String referralId, RewardStatus to, String evidenceReference, LocalDateReceived received,
                                      String actorUserId, String actorRole, String secondApproverUserId) {
        List<RewardTransaction> history = repository.findByReferralIdOrderByCreatedAtEventAsc(referralId);
        if (history.isEmpty()) {
            throw ApiException.badRequest("NO_LEDGER_ENTRY", "No reward entry exists for this referral yet.");
        }
        RewardTransaction latest = history.get(history.size() - 1);

        if (to == RewardStatus.PAID) {
            boolean hasConfirmed = history.stream().anyMatch(t -> t.getStatus() == RewardStatus.CONFIRMED);
            if (!hasConfirmed) {
                throw ApiException.badRequest("CONFIRMED_REQUIRED_BEFORE_PAID",
                        "A CONFIRMED entry is required before marking a reward PAID.");
            }
            if (received == null || received.date() == null || evidenceReference == null || evidenceReference.isBlank()) {
                throw ApiException.badRequest("PAID_REQUIRES_EVIDENCE",
                        "Marking PAID requires a received date and an evidence reference.");
            }
            if (secondApproverRequired && (secondApproverUserId == null || secondApproverUserId.equals(actorUserId))) {
                throw ApiException.badRequest("SECOND_APPROVER_REQUIRED",
                        "A second approver, different from the recorder, is required for PAID entries.");
            }
        }

        RewardTransaction next = new RewardTransaction();
        next.setReferralId(latest.getReferralId());
        next.setCandidateUserId(latest.getCandidateUserId());
        next.setPlatformId(latest.getPlatformId());
        next.setStatus(to);
        next.setAmountMinorUnits(latest.getAmountMinorUnits());
        next.setCurrencyCode(latest.getCurrencyCode());
        next.setFxRateToInr(latest.getFxRateToInr());
        next.setFeesMinorUnits(latest.getFeesMinorUnits());
        next.setReceivedDate(received != null ? received.date() : latest.getReceivedDate());
        next.setEvidenceReference(evidenceReference != null ? evidenceReference : latest.getEvidenceReference());
        next.setRecordedByUserId(actorUserId);
        next.setSecondApproverUserId(secondApproverUserId);
        applyFx(next);

        RewardTransaction saved = repository.save(next);
        auditService.record(actorUserId, actorRole, "REWARD_" + to, "RewardTransaction", saved.getId(), evidenceReference);
        return saved;
    }

    public record LocalDateReceived(java.time.LocalDate date) {
    }

    public java.util.Map<String, Object> revenueSummary() {
        // Never sum different currencies together; group by currency and status.
        var all = repository.findAll();
        java.util.Map<String, java.util.Map<RewardStatus, Long>> byCurrency = new java.util.TreeMap<>();
        for (RewardTransaction tx : latestPerReferral(all)) {
            byCurrency.computeIfAbsent(tx.getCurrencyCode(), k -> new java.util.EnumMap<>(RewardStatus.class))
                    .merge(tx.getStatus(), tx.getAmountMinorUnits(), Long::sum);
        }
        return java.util.Map.of("byCurrencyAndStatus", byCurrency);
    }

    /** Only the latest entry per referral counts toward totals (older entries are history, not current state). */
    private List<RewardTransaction> latestPerReferral(List<RewardTransaction> all) {
        java.util.Map<String, RewardTransaction> latest = new java.util.LinkedHashMap<>();
        for (RewardTransaction tx : all) {
            latest.merge(tx.getReferralId(), tx, (a, b) -> a.getCreatedAtEvent().isAfter(b.getCreatedAtEvent()) ? a : b);
        }
        return new java.util.ArrayList<>(latest.values());
    }

    private void applyFx(RewardTransaction tx) {
        if (tx.getFxRateToInr() != null) {
            tx.setAmountInrMinorUnits(java.math.BigDecimal.valueOf(tx.getAmountMinorUnits())
                    .multiply(tx.getFxRateToInr()).longValue());
        }
    }
}
