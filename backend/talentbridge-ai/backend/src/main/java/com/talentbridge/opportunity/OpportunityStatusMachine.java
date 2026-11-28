package com.talentbridge.opportunity;

import com.talentbridge.common.ApiException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class OpportunityStatusMachine {

    private static final Map<OpportunityStatus, Set<OpportunityStatus>> ALLOWED = new EnumMap<>(OpportunityStatus.class);

    static {
        ALLOWED.put(OpportunityStatus.DRAFT, EnumSet.of(OpportunityStatus.PENDING_REVIEW, OpportunityStatus.ARCHIVED));
        ALLOWED.put(OpportunityStatus.PENDING_REVIEW, EnumSet.of(OpportunityStatus.PUBLISHED, OpportunityStatus.DRAFT, OpportunityStatus.ARCHIVED));
        ALLOWED.put(OpportunityStatus.PUBLISHED, EnumSet.of(OpportunityStatus.PAUSED, OpportunityStatus.EXPIRED, OpportunityStatus.ARCHIVED));
        ALLOWED.put(OpportunityStatus.PAUSED, EnumSet.of(OpportunityStatus.PUBLISHED, OpportunityStatus.EXPIRED, OpportunityStatus.ARCHIVED));
        ALLOWED.put(OpportunityStatus.EXPIRED, EnumSet.of(OpportunityStatus.ARCHIVED, OpportunityStatus.PENDING_REVIEW));
        ALLOWED.put(OpportunityStatus.ARCHIVED, EnumSet.noneOf(OpportunityStatus.class));
    }

    public void assertTransitionAllowed(OpportunityStatus from, OpportunityStatus to) {
        if (from == to) {
            return;
        }
        Set<OpportunityStatus> allowed = ALLOWED.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw ApiException.badRequest("ILLEGAL_STATUS_TRANSITION",
                    "Cannot move a listing from " + from + " to " + to + ".");
        }
    }

    /** Publishing requires the fields the blueprint lists as mandatory before going live. */
    public void assertPublishable(Opportunity opportunity) {
        StringBuilder missing = new StringBuilder();
        if (isBlank(opportunity.getSourceUrl())) missing.append("sourceUrl,");
        if (isBlank(opportunity.getApplyUrl())) missing.append("applyUrl,");
        if (opportunity.getVerifiedAt() == null) missing.append("verifiedAt,");
        if (isBlank(opportunity.getEligibility())) missing.append("eligibility,");
        if (missing.length() > 0) {
            throw ApiException.badRequest("INCOMPLETE_LISTING",
                    "Cannot publish: missing " + missing.substring(0, missing.length() - 1) + ".");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
