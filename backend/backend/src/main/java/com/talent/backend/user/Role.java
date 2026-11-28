package com.talent.backend.user;

public enum Role {
	CANDIDATE,
	EDITOR,
	REFERRAL_ADMIN,
	FINANCE,
	SUPER_ADMIN;

	public boolean isAdmin() {
		return this != CANDIDATE;
	}
}
