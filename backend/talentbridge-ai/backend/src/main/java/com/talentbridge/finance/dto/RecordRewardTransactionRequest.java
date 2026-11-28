package com.talentbridge.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordRewardTransactionRequest(
        @NotBlank String referralId,
        @NotBlank String candidateUserId,
        String platformId,
        @Positive long amountMinorUnits,
        @NotBlank String currencyCode,
        BigDecimal fxRateToInr,
        BigDecimal feesMinorUnits,
        LocalDate receivedDate,
        String evidenceReference
) {
}
