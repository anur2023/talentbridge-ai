package com.talentbridge.opportunity.dto;

import com.talentbridge.opportunity.ReferralProgrammeStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record UpsertPlatformRequest(
        @NotBlank String name,
        String website,
        String officialTermsUrl,
        ReferralProgrammeStatus referralProgrammeStatus,
        LocalDate lastTermsReviewDate
) {
}
