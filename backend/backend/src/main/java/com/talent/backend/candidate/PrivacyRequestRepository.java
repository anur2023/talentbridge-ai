package com.talent.backend.candidate;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PrivacyRequestRepository extends MongoRepository<PrivacyRequest, String> {

	List<PrivacyRequest> findByUserIdOrderByCreatedAtDesc(String userId);

	List<PrivacyRequest> findByStatusOrderByDueAtAsc(PrivacyRequestStatus status);
}
