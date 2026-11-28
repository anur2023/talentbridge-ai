package com.talent.backend.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Document("authTokens")
public class AuthToken {

	@Id
	private String id;
	private String userId;
	@Indexed
	private String tokenHash;
	private TokenPurpose purpose;
	private Instant expiresAt;
	private Instant usedAt;
	private Instant createdAt;
}
