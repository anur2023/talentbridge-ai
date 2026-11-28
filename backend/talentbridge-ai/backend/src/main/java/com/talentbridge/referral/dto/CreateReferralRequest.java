package com.talentbridge.referral.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateReferralRequest(@NotBlank String opportunityId, boolean disclosureAcknowledged) {
}
