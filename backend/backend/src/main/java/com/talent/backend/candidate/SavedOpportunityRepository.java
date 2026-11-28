package com.talent.backend.candidate;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SavedOpportunityRepository extends MongoRepository<SavedOpportunity, String> {

	List<SavedOpportunity> findByUserIdOrderByCreatedAtDesc(String userId);

	Optional<SavedOpportunity> findByUserIdAndOpportunityId(String userId, String opportunityId);

	long countByUserId(String userId);

	void deleteByUserIdAndOpportunityId(String userId, String opportunityId);
}
