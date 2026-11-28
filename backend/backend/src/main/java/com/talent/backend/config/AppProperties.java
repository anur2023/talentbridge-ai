package com.talent.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "talentbridge")
public class AppProperties {

	private String publicBaseUrl = "http://localhost:8080";
	private String mailFrom = "noreply@localhost";
	private Duration verificationTtl = Duration.ofHours(24);
	private Duration passwordResetTtl = Duration.ofHours(1);
	private Duration staleAfter = Duration.ofDays(14);
	private Duration partnerTermsMaxAge = Duration.ofDays(90);
	private Duration privacyRequestDue = Duration.ofDays(30);
	private int loginLockAfter = 5;
	private Duration loginLockFor = Duration.ofMinutes(15);
	private Security security = new Security();
	private Finance finance = new Finance();
	private Seed seed = new Seed();
	private RateLimit rateLimit = new RateLimit();

	@Getter
	@Setter
	public static class Security {
		private boolean mfaRequired = true;
	}

	@Getter
	@Setter
	public static class Finance {
		private boolean requireSecondApprover = false;
	}

	@Getter
	@Setter
	public static class Seed {
		private boolean enabled = false;
		private String adminEmail = "editor@example.com";
		private String adminPassword = "";
		private String superEmail = "superadmin@example.com";
		private String superPassword = "";
		private String candidatePassword = "";
	}

	@Getter
	@Setter
	public static class RateLimit {
		private int register = 5;
		private int login = 10;
		private int forgotPassword = 5;
		private int referral = 10;
		private int search = 60;
		private int support = 5;
	}
}
