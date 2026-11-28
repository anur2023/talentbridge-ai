package com.talent.backend.candidate;

import com.talent.backend.common.AuditableDocument;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document("candidateProfiles")
public class CandidateProfile extends AuditableDocument {

	@Id
	private String id;

	@Indexed(unique = true)
	private String userId;

	private String displayName;
	private String country;
	private String city;
	private List<String> skills = new ArrayList<>();
	private ExperienceBand experienceBand;
	private List<String> languages = new ArrayList<>();
	private List<String> workPreferences = new ArrayList<>();
	private String availability;
	private String profileUrl;
	private boolean marketingEmail;
	private boolean opportunityAlerts;
}
