package com.talentbridge.analytics;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalyticsEventRepository extends MongoRepository<AnalyticsEvent, String> {
    List<AnalyticsEvent> findByType(AnalyticsEventType type);
}
