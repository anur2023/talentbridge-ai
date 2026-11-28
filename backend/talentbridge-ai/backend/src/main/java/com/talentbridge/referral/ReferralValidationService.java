package com.talentbridge.referral;

import com.talentbridge.candidate.CandidateProfile;
import com.talentbridge.candidate.CandidateProfileRepository;
import com.talentbridge.consent.ConsentPurpose;
import com.talentbridge.consent.ConsentService;
import com.talentbridge.opportunity.Opportunity;
import com.talentbridge.opportunity.OpportunityRepository;
import com.talentbridge.opportunity.OpportunityStatus;
import com.talentbridge.opportunity.Platform;
import com.talentbridge.opportunity.PlatformRepository;
import com.talentbridge.opportunity.ReferralProgrammeStatus;
import com.talentbridge.opportunity.ReferralType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Every rule in blueprint 11.2. A referral may only be created when ALL of
 * these pass; each failure returns a candidate-safe code rather than an
 * internal message (Day 16 "done when": refused with the right code per rule).
 */
@Service
public class ReferralValidationService {

    private final OpportunityRepository opportunityRepository;
    private final PlatformRepository platformRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final ConsentService consentService;
    private final CandidateReferralRepository referralRepository;
    private final int partnerTermsReviewMaxAgeDays;

    public ReferralValidationService(OpportunityRepository opportunityRepository,
                                      PlatformRepository platformRepository,
                                      CandidateProfileRepository candidateProfileRepository,
                                      ConsentService consentService,
                                      CandidateReferralRepository referralRepository,
                                      @Value("${talentbridge.referral.partner-terms-review-max-age-days:365}") int partnerTermsReviewMaxAgeDays) {
        this.opportunityRepository = opportunityRepository;
        this.platformRepository = platformRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.consentService = consentService;
        this.referralRepository = referralRepository;
        this.partnerTermsReviewMaxAgeDays = partnerTermsReviewMaxAgeDays;
    }

    public ReferralValidationResult validate(String candidateUserId, String opportunityId, boolean disclosureAcknowledged) {
        List<String> failed = new ArrayList<>();

        Opportunity opportunity = opportunityRepository.findById(opportunityId).orElse(null);
        if (opportunity == null) {
            return new ReferralValidationResult(false, List.of("OPPORTUNITY_NOT_FOUND"));
        }

        // 1. Opportunity published and in window.
        if (!opportunity.isPubliclyVisible()) {
            failed.add("OPPORTUNITY_NOT_OPEN");
        }

        Platform platform = opportunity.getPlatformId() != null
                ? platformRepository.findById(opportunity.getPlatformId()).orElse(null)
                : null;

        boolean needsPartnerMethod = opportunity.getReferralType() == ReferralType.OFFICIAL_REFERRAL
                || opportunity.getReferralType() == ReferralType.AFFILIATE_LINK;

        if (needsPartnerMethod) {
            // 2. Partner terms reviewed within the configured period.
            if (platform == null || platform.getLastTermsReviewDate() == null
                    || LocalDate.now().isAfter(platform.getLastTermsReviewDate().plusDays(partnerTermsReviewMaxAgeDays))) {
                failed.add("PARTNER_TERMS_NOT_CURRENT");
            }
            // 3. Method approved for this partner and opportunity.
            if (platform == null || platform.getReferralProgrammeStatus() != ReferralProgrammeStatus.REVIEWED_APPROVED) {
                failed.add("REFERRAL_METHOD_NOT_APPROVED");
            }
        }

        // 4. Required profile fields and eligibility.
        CandidateProfile profile = candidateProfileRepository.findByUserId(candidateUserId).orElse(null);
        if (profile == null || profile.getDisplayName() == null || profile.getDisplayName().isBlank()
                || profile.getSkills().isEmpty() || profile.getExperienceBand() == null) {
            failed.add("PROFILE_INCOMPLETE");
        }

        // 5. Active consent for the named recipient (only relevant when data would be shared).
        boolean needsReferralSharingConsent = opportunity.getReferralType() != ReferralType.PUBLIC_APPLICATION;
        if (needsReferralSharingConsent) {
            String recipientPlatformId = opportunity.getPlatformId();
            if (!consentService.hasActiveConsent(candidateUserId, ConsentPurpose.REFERRAL_SHARING, recipientPlatformId)) {
                failed.add("REFERRAL_CONSENT_MISSING");
            }
        }

        // 6. Duplicate check: no other active (non-terminal) referral for the same opportunity.
        boolean hasActiveDuplicate = referralRepository.findByCandidateUserIdAndOpportunityId(candidateUserId, opportunityId)
                .stream()
                .anyMatch(r -> r.getStatus() != ReferralStatus.WITHDRAWN && r.getStatus() != ReferralStatus.CLOSED
                        && r.getStatus() != ReferralStatus.REJECTED);
        if (hasActiveDuplicate) {
            failed.add("DUPLICATE_REFERRAL");
        }

        // 7. Disclosure acknowledged.
        if (!disclosureAcknowledged) {
            failed.add("DISCLOSURE_NOT_ACKNOWLEDGED");
        }

        return failed.isEmpty() ? ReferralValidationResult.ok() : new ReferralValidationResult(false, failed);
    }
}
