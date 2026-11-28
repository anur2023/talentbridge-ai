package com.talentbridge.auth;

import com.talentbridge.candidate.CandidateProfile;
import com.talentbridge.candidate.CandidateProfileRepository;
import com.talentbridge.common.ApiException;
import com.talentbridge.notification.MailService;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final int verificationHours;
    private final int resetHours;
    private final String frontendBaseUrl;

    public AuthService(UserRepository userRepository,
                        CandidateProfileRepository candidateProfileRepository,
                        PasswordEncoder passwordEncoder,
                        MailService mailService,
                        @Value("${talentbridge.tokens.email-verification-hours}") int verificationHours,
                        @Value("${talentbridge.tokens.password-reset-hours}") int resetHours,
                        @Value("${talentbridge.frontend.base-url}") String frontendBaseUrl) {
        this.userRepository = userRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.verificationHours = verificationHours;
        this.resetHours = resetHours;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public User register(String email, String rawPassword) {
        String normalized = normalize(email);
        if (userRepository.existsByNormalizedEmail(normalized)) {
            // Duplicate-email race: unique index also guards this at the DB level.
            throw ApiException.conflict("EMAIL_ALREADY_REGISTERED", "An account with this email already exists.");
        }
        User user = new User();
        user.setEmail(email.trim());
        user.setNormalizedEmail(normalized);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(Role.CANDIDATE);
        user.setStatus(UserStatus.PENDING_VERIFICATION);

        String token = TokenUtil.generateToken();
        user.setEmailVerificationTokenHash(TokenUtil.hash(token));
        user.setEmailVerificationExpiresAt(Instant.now().plus(Duration.ofHours(verificationHours)));

        User saved;
        try {
            saved = userRepository.save(user);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw ApiException.conflict("EMAIL_ALREADY_REGISTERED", "An account with this email already exists.");
        }

        CandidateProfile profile = new CandidateProfile();
        profile.setUserId(saved.getId());
        candidateProfileRepository.save(profile);

        String link = frontendBaseUrl + "/verify-email?token=" + token;
        mailService.send(saved.getEmail(), "EMAIL_VERIFY", "Verify your TalentBridge AI email",
                "Click to verify your email: " + link + "\nThis link expires in " + verificationHours + " hours.",
                saved.getId());
        return saved;
    }

    public void verifyEmail(String token) {
        String hash = TokenUtil.hash(token);
        User user = userRepository.findByEmailVerificationTokenHash(hash)
                .orElseThrow(() -> ApiException.badRequest("INVALID_TOKEN", "This verification link is invalid or has expired."));

        if (user.getEmailVerificationExpiresAt() == null || user.getEmailVerificationExpiresAt().isBefore(Instant.now())) {
            throw ApiException.badRequest("TOKEN_EXPIRED", "This verification link is invalid or has expired.");
        }
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerificationTokenHash(null);
        user.setEmailVerificationExpiresAt(null);
        userRepository.save(user);
    }

    public User authenticate(String email, String rawPassword) {
        String normalized = normalize(email);
        User user = userRepository.findByNormalizedEmail(normalized)
                .orElseThrow(() -> ApiException.unauthorized("INVALID_CREDENTIALS", "Email or password is incorrect."));

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
            throw ApiException.tooManyRequests("ACCOUNT_LOCKED", "Too many failed attempts. Try again later.");
        }
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setLockedUntil(Instant.now().plus(LOCK_DURATION));
            }
            userRepository.save(user);
            throw ApiException.unauthorized("INVALID_CREDENTIALS", "Email or password is incorrect.");
        }
        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            throw ApiException.forbidden("EMAIL_NOT_VERIFIED", "Please verify your email before logging in.");
        }
        if (user.getStatus() == UserStatus.DISABLED) {
            throw ApiException.forbidden("ACCOUNT_DISABLED", "This account has been disabled.");
        }
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
        return user;
    }

    /** Always returns normally, whether or not the email exists, to avoid enumeration. */
    public void forgotPassword(String email) {
        String normalized = normalize(email);
        userRepository.findByNormalizedEmail(normalized).ifPresent(user -> {
            String token = TokenUtil.generateToken();
            user.setPasswordResetTokenHash(TokenUtil.hash(token));
            user.setPasswordResetExpiresAt(Instant.now().plus(Duration.ofHours(resetHours)));
            userRepository.save(user);
            String link = frontendBaseUrl + "/reset-password?token=" + token;
            mailService.send(user.getEmail(), "PASSWORD_RESET", "Reset your TalentBridge AI password",
                    "Click to reset your password: " + link + "\nThis link expires in " + resetHours + " hour(s).",
                    user.getId() + ":" + user.getPasswordResetTokenHash());
        });
    }

    public void resetPassword(String token, String newPassword) {
        String hash = TokenUtil.hash(token);
        User user = userRepository.findByPasswordResetTokenHash(hash)
                .orElseThrow(() -> ApiException.badRequest("INVALID_TOKEN", "This reset link is invalid or has expired."));

        if (user.getPasswordResetExpiresAt() == null || user.getPasswordResetExpiresAt().isBefore(Instant.now())) {
            throw ApiException.badRequest("TOKEN_EXPIRED", "This reset link is invalid or has expired.");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetTokenHash(null);
        user.setPasswordResetExpiresAt(null);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
