package com.talentbridge.consent.dto;

import com.talentbridge.consent.ConsentPurpose;
import jakarta.validation.constraints.NotNull;

public record ConsentActionRequest(@NotNull ConsentPurpose purpose, String recipientPlatformId, String source) {
}
