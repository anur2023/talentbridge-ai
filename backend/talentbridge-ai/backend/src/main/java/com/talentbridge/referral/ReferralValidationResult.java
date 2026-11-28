package com.talentbridge.referral;

import java.util.List;

/** `passed` is true only if every check passed; `failedCodes` names each failed rule (candidate-safe codes). */
public record ReferralValidationResult(boolean passed, List<String> failedCodes) {
    public static ReferralValidationResult ok() {
        return new ReferralValidationResult(true, List.of());
    }
}
