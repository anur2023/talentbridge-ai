package com.talent.backend.candidate;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Document("privacyRequests")
public class PrivacyRequest {

	@Id
	private String id;
	private String userId;
	private PrivacyRequestType type;
	private String message;
	private PrivacyRequestStatus status;
	private Instant dueAt;
	private Instant createdAt;
	private Instant updatedAt;
}
