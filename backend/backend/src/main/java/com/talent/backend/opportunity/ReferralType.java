package com.talent.backend.opportunity;

public enum ReferralType {
	PUBLIC_APPLICATION,
	OFFICIAL_REFERRAL,
	AFFILIATE,
	MANUAL_PARTNER_SUBMISSION,
	UNVERIFIED;

	public String allowedClaim() {
		return switch (this) {
			case PUBLIC_APPLICATION -> "Apply on official site";
			case OFFICIAL_REFERRAL -> "Official referral route available; terms apply";
			case AFFILIATE -> "Partner link; commission may be subject to program terms";
			case MANUAL_PARTNER_SUBMISSION -> "Partner-approved referral submission";
			case UNVERIFIED -> "Do not present as a commission-earning referral";
		};
	}

	public boolean sharesCandidateData() {
		return this == OFFICIAL_REFERRAL || this == AFFILIATE || this == MANUAL_PARTNER_SUBMISSION;
	}
}
