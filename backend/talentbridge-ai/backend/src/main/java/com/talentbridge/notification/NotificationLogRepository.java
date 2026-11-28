package com.talentbridge.notification;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {
    boolean existsByTemplateKeyAndToAndReferenceId(String templateKey, String to, String referenceId);
}
