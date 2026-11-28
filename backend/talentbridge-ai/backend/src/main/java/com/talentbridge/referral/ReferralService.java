package com.talentbridge.referral;

import com.talentbridge.common.ApiException;
import com.talentbridge.opportunity.Opportunity;
import com.talentbridge.opportunity.OpportunityRepository;
import org.springframework.stereotype.Service;

@Service
public class ReferralService {

    private final CandidateReferralRepository referralRepository;
    private final ReferralEventRepository eventRepository;
    private final ReferralValidationService validationService;
    private final ReferralStatusMachine statusMachine;
    private final OpportunityRepository opportunityRepository;

    public ReferralService(CandidateReferralRepository referralRepository, ReferralEventRepository eventRepository,
                            ReferralValidationService validationService, ReferralStatusMachine statusMachine,
                            OpportunityRepository opportunityRepository) {
        this.referralRepository = referralRepository;
        this.eventRepository = eventRepository;
        this.validationService = validationService;
        this.statusMachine = statusMachine;
        this.opportunityRepository = opportunityRepository;
    }

    public ReferralValidationResult checkEligibility(String candidateUserId, String opportunityId) {
        // Pre-check without requiring disclosure yet, for the stepper's first screen.
        return validationService.validate(candidateUserId, opportunityId, true);
    }

    public CandidateReferral create(String candidateUserId, String opportunityId, boolean disclosureAcknowledged,
                                     String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existing = referralRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        ReferralValidationResult result = validationService.validate(candidateUserId, opportunityId, disclosureAcknowledged);
        if (!result.passed()) {
            throw ApiException.badRequest("REFERRAL_VALIDATION_FAILED", String.join(",", result.failedCodes()));
        }

        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> ApiException.notFound("OPPORTUNITY_NOT_FOUND", "Listing not found."));

        CandidateReferral referral = new CandidateReferral();
        referral.setCandidateUserId(candidateUserId);
        referral.setOpportunityId(opportunityId);
        referral.setPlatformId(opportunity.getPlatformId());
        referral.setDisclosureAcknowledged(true);
        referral.setStatus(ReferralStatus.SUBMITTED);
        referral.setIdempotencyKey(idempotencyKey);

        CandidateReferral saved;
        try {
            saved = referralRepository.save(referral);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            return referralRepository.findByIdempotencyKey(idempotencyKey).orElseThrow(() -> e);
        }
        eventRepository.save(new ReferralEvent(saved.getId(), null, ReferralStatus.SUBMITTED, candidateUserId, null,
                "Referral submitted by candidate"));
        return saved;
    }

    public CandidateReferral withdraw(String candidateUserId, String referralId) {
        CandidateReferral referral = referralRepository.findByIdAndCandidateUserId(referralId, candidateUserId)
                .orElseThrow(() -> ApiException.notFound("REFERRAL_NOT_FOUND", "Referral not found."));
        return changeStatus(referral, ReferralStatus.WITHDRAWN, candidateUserId, null, "Withdrawn by candidate");
    }

    /** Admin status change — evidence reference is mandatory (Day 16/18 rule). */
    public CandidateReferral adminChangeStatus(String referralId, ReferralStatus to, String evidenceReference,
                                                String actorUserId, String note) {
        if (evidenceReference == null || evidenceReference.isBlank()) {
            throw ApiException.badRequest("EVIDENCE_REQUIRED", "A status change requires an evidence reference.");
        }
        CandidateReferral referral = referralRepository.findById(referralId)
                .orElseThrow(() -> ApiException.notFound("REFERRAL_NOT_FOUND", "Referral not found."));
        return changeStatus(referral, to, actorUserId, evidenceReference, note);
    }

    public CandidateReferral addEvidence(String referralId, String evidenceReference) {
        CandidateReferral referral = referralRepository.findById(referralId)
                .orElseThrow(() -> ApiException.notFound("REFERRAL_NOT_FOUND", "Referral not found."));
        referral.setEvidenceReference(evidenceReference);
        return referralRepository.save(referral);
    }

    public java.util.List<CandidateReferral> listMine(String candidateUserId) {
        return referralRepository.findByCandidateUserId(candidateUserId);
    }

    public CandidateReferral requireOwned(String referralId, String candidateUserId) {
        return referralRepository.findByIdAndCandidateUserId(referralId, candidateUserId)
                .orElseThrow(() -> ApiException.notFound("REFERRAL_NOT_FOUND", "Referral not found."));
    }

    public java.util.List<ReferralEvent> timeline(String referralId) {
        return eventRepository.findByReferralIdOrderByAtAsc(referralId);
    }

    private CandidateReferral changeStatus(CandidateReferral referral, ReferralStatus to, String actorUserId,
                                            String evidenceReference, String note) {
        statusMachine.assertTransitionAllowed(referral.getStatus(), to);
        ReferralStatus previous = referral.getStatus();
        referral.setStatus(to);
        if (evidenceReference != null) {
            referral.setEvidenceReference(evidenceReference);
        }
        CandidateReferral saved = referralRepository.save(referral);
        eventRepository.save(new ReferralEvent(saved.getId(), previous, to, actorUserId, evidenceReference, note));
        return saved;
    }
}
