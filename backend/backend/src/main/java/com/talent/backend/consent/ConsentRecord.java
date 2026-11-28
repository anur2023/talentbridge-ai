package com.talent.backend.consent;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Document("consentRecords")
public class ConsentRecord {

	@Id
	private String id;
	private String userId;
	private ConsentPurpose purpose;
	private String recipientId;
	private String noticeVersion;
	private ConsentEventType eventType;
	private boolean granted;
	private Instant capturedAt;
	private String source;
	private String actorId;
}
