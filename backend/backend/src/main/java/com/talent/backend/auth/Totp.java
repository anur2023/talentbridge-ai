package com.talent.backend.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Instant;

final class Totp {

	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
	private static final SecureRandom RANDOM = new SecureRandom();

	private Totp() {
	}

	static String randomSecret() {
		byte[] bytes = new byte[20];
		RANDOM.nextBytes(bytes);
		return base32(bytes);
	}

	static boolean matches(String base32Secret, String code, Instant now) {
		if (code == null || !code.matches("\\d{6}")) {
			return false;
		}
		long step = now.getEpochSecond() / 30;
		for (long offset = -1; offset <= 1; offset++) {
			if (code.equals(codeAt(base32Secret, step + offset))) {
				return true;
			}
		}
		return false;
	}

	static String provisioningUri(String secret, String email) {
		return "otpauth://totp/TalentBridge:" + email + "?secret=" + secret + "&issuer=TalentBridge&digits=6&period=30";
	}

	private static String codeAt(String secret, long step) {
		try {
			byte[] key = decodeBase32(secret);
			byte[] data = new byte[8];
			long value = step;
			for (int i = 7; i >= 0; i--) {
				data[i] = (byte) (value & 0xff);
				value >>= 8;
			}
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(key, "HmacSHA1"));
			byte[] hash = mac.doFinal(data);
			int offset = hash[hash.length - 1] & 0x0f;
			int binary = ((hash[offset] & 0x7f) << 24)
					| ((hash[offset + 1] & 0xff) << 16)
					| ((hash[offset + 2] & 0xff) << 8)
					| (hash[offset + 3] & 0xff);
			int otp = binary % 1_000_000;
			return String.format("%06d", otp);
		}
		catch (Exception ex) {
			return "";
		}
	}

	private static String base32(byte[] data) {
		StringBuilder out = new StringBuilder();
		int buffer = 0;
		int bits = 0;
		for (byte datum : data) {
			buffer = (buffer << 8) | (datum & 0xff);
			bits += 8;
			while (bits >= 5) {
				out.append(ALPHABET.charAt((buffer >> (bits - 5)) & 31));
				bits -= 5;
			}
		}
		if (bits > 0) {
			out.append(ALPHABET.charAt((buffer << (5 - bits)) & 31));
		}
		return out.toString();
	}

	private static byte[] decodeBase32(String value) {
		String cleaned = value.replace("=", "").replace(" ", "").toUpperCase();
		int buffer = 0;
		int bits = 0;
		byte[] out = new byte[cleaned.length() * 5 / 8];
		int index = 0;
		for (char character : cleaned.toCharArray()) {
			int val = ALPHABET.indexOf(character);
			if (val < 0) {
				continue;
			}
			buffer = (buffer << 5) | val;
			bits += 5;
			if (bits >= 8) {
				out[index++] = (byte) ((buffer >> (bits - 8)) & 0xff);
				bits -= 8;
			}
		}
		if (index == out.length) {
			return out;
		}
		byte[] trimmed = new byte[index];
		System.arraycopy(out, 0, trimmed, 0, index);
		return trimmed;
	}
}
