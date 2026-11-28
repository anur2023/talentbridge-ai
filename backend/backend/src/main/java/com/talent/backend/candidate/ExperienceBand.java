package com.talent.backend.candidate;

public enum ExperienceBand {
	STUDENT,
	JUNIOR,
	MID,
	SENIOR,
	LEAD;

	public boolean meets(ExperienceBand required) {
		if (required == null) {
			return true;
		}
		return ordinal() >= required.ordinal();
	}
}
