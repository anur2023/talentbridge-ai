package com.talent.backend.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Version;

import java.time.Instant;

@Getter
@Setter
public abstract class AuditableDocument {

	private Instant createdAt;
	private Instant updatedAt;

	@Version
	private Long version;
}
