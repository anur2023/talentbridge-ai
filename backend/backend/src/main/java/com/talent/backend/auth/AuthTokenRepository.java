package com.talent.backend.auth;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuthTokenRepository extends MongoRepository<AuthToken, String> {

	Optional<AuthToken> findByTokenHashAndPurpose(String tokenHash, TokenPurpose purpose);
}
