package com.talent.backend.common;

public final class TextSanitizer {

	private TextSanitizer() {
	}

	public static String plain(String value) {
		if (value == null) {
			return null;
		}
		String stripped = value
				.replaceAll("(?is)<script.*?>.*?</script>", "")
				.replaceAll("(?i)<[^>]+>", "")
				.replace("\u0000", "");
		return stripped.trim();
	}

	public static String slug(String value) {
		String plain = plain(value);
		if (plain == null || plain.isBlank()) {
			return "listing";
		}
		String slug = plain.toLowerCase()
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("(^-|-$)", "");
		if (slug.isBlank()) {
			return "listing";
		}
		return slug.length() > 80 ? slug.substring(0, 80).replaceAll("-$", "") : slug;
	}

	public static String normalizeEmail(String email) {
		return email == null ? null : email.trim().toLowerCase();
	}
}
