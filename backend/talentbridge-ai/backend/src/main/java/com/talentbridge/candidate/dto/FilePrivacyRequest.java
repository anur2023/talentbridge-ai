package com.talentbridge.candidate.dto;

import com.talentbridge.candidate.PrivacyRequestType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FilePrivacyRequest(
        @NotNull PrivacyRequestType type,
        @Size(max = 2000) String details
) {
}
