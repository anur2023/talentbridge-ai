package com.talent.backend.consent;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class ConsentPolicy {

	private ConsentPolicy() {
	}

	public static boolean hasActiveConsent(List<ConsentRecord> history, ConsentPurpose purpose, String recipientId) {
		return history.stream()
				.filter(record -> record.getPurpose() == purpose)
				.filter(record -> sameRecipient(record.getRecipientId(), recipientId))
				.max(Comparator.comparing(ConsentRecord::getCapturedAt))
				.map(record -> record.getEventType() == ConsentEventType.GRANT && record.isGranted())
				.orElse(false);
	}

	private static boolean sameRecipient(String left, String right) {
		String a = left == null || left.isBlank() ? null : left;
		String b = right == null || right.isBlank() ? null : right;
		return Objects.equals(a, b);
	}
}
