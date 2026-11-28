package com.talent.backend.consent;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConsentRecordRepository extends MongoRepository<ConsentRecord, String> {

	List<ConsentRecord> findByUserIdOrderByCapturedAtAsc(String userId);
}
