package com.talentbridge.auth;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByNormalizedEmail(String normalizedEmail);
    boolean existsByNormalizedEmail(String normalizedEmail);
    Optional<User> findByEmailVerificationTokenHash(String tokenHash);
    Optional<User> findByPasswordResetTokenHash(String tokenHash);
}
