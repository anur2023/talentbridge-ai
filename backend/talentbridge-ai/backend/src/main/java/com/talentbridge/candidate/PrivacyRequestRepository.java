package com.talentbridge.candidate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrivacyRequestRepository extends MongoRepository<PrivacyRequest, String> {
    Page<PrivacyRequest> findByStatus(PrivacyRequestStatus status, Pageable pageable);
    Page<PrivacyRequest> findByUserId(String userId, Pageable pageable);
}
