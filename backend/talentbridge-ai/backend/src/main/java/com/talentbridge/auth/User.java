package com.talentbridge.auth;

import com.talentbridge.common.BaseEntity;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User extends BaseEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String normalizedEmail;

    private String email;
    private String passwordHash;
    private Role role = Role.CANDIDATE;
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    private String emailVerificationTokenHash;
    private Instant emailVerificationExpiresAt;

    private String passwordResetTokenHash;
    private Instant passwordResetExpiresAt;

    private int failedLoginAttempts = 0;
    private Instant lockedUntil;

    private boolean mfaEnabled = false;
    private String mfaSecret;
    private java.util.List<String> mfaRecoveryCodeHashes = new java.util.ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNormalizedEmail() {
        return normalizedEmail;
    }

    public void setNormalizedEmail(String normalizedEmail) {
        this.normalizedEmail = normalizedEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getEmailVerificationTokenHash() {
        return emailVerificationTokenHash;
    }

    public void setEmailVerificationTokenHash(String emailVerificationTokenHash) {
        this.emailVerificationTokenHash = emailVerificationTokenHash;
    }

    public Instant getEmailVerificationExpiresAt() {
        return emailVerificationExpiresAt;
    }

    public void setEmailVerificationExpiresAt(Instant emailVerificationExpiresAt) {
        this.emailVerificationExpiresAt = emailVerificationExpiresAt;
    }

    public String getPasswordResetTokenHash() {
        return passwordResetTokenHash;
    }

    public void setPasswordResetTokenHash(String passwordResetTokenHash) {
        this.passwordResetTokenHash = passwordResetTokenHash;
    }

    public Instant getPasswordResetExpiresAt() {
        return passwordResetExpiresAt;
    }

    public void setPasswordResetExpiresAt(Instant passwordResetExpiresAt) {
        this.passwordResetExpiresAt = passwordResetExpiresAt;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(Instant lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret;
    }

    public java.util.List<String> getMfaRecoveryCodeHashes() {
        return mfaRecoveryCodeHashes;
    }

    public void setMfaRecoveryCodeHashes(java.util.List<String> mfaRecoveryCodeHashes) {
        this.mfaRecoveryCodeHashes = mfaRecoveryCodeHashes;
    }

    public boolean isAdmin() {
        return role != Role.CANDIDATE;
    }
}
