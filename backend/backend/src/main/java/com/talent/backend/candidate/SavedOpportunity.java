package com.talent.backend.candidate;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Document("savedOpportunities")
public class SavedOpportunity {

	@Id
	private String id;
	private String userId;
	private String opportunityId;
	private Instant createdAt;
}
