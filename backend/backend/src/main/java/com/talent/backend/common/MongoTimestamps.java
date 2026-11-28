package com.talent.backend.common;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MongoTimestamps extends AbstractMongoEventListener<Object> {

	@Override
	public void onBeforeConvert(BeforeConvertEvent<Object> event) {
		if (event.getSource() instanceof AuditableDocument document) {
			Instant now = Instant.now();
			if (document.getCreatedAt() == null) {
				document.setCreatedAt(now);
			}
			document.setUpdatedAt(now);
		}
	}
}
