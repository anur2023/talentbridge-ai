package com.talentbridge.consent;

public enum ConsentPurpose {
    PROFILE_PROCESSING,
    /** Requires a named recipient platform (the partner the data would go to). */
    REFERRAL_SHARING,
    MARKETING_EMAIL,
    FUTURE_OPPORTUNITIES
}
