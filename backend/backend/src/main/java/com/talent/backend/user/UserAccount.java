package com.talent.backend.user;

import com.talent.backend.common.AuditableDocument;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document("users")
public class UserAccount extends AuditableDocument {

	@Id
	private String id;

	private String email;

	@Indexed(unique = true)
	private String emailNormalized;

	private String passwordHash;
	private Role role;
	private AccountStatus status;
	private Instant emailVerifiedAt;
	private int failedLoginCount;
	private Instant lockedUntil;
	private boolean mfaEnabled;
	private String totpSecret;
	private List<String> recoveryCodeHashes = new ArrayList<>();
}
