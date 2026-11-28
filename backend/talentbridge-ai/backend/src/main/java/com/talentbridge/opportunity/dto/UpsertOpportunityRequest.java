package com.talentbridge.opportunity.dto;

import com.talentbridge.opportunity.OpportunityCategory;
import com.talentbridge.opportunity.ReferralType;
import com.talentbridge.opportunity.WorkType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record UpsertOpportunityRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 8000) String description,
        List<String> skills,
        OpportunityCategory category,
        WorkType workType,
        boolean remote,
        String experienceBand,
        String country,
        List<String> languages,
        boolean compensationDisclosed,
        String compensationText,
        String platformId,
        ReferralType referralType,
        boolean sponsored,
        String sourceUrl,
        String applyUrl,
        String eligibility,
        LocalDate deadline,
        LocalDate verifiedAt
) {
}
