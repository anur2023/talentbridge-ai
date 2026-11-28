package com.talentbridge.candidate;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SavedOpportunityRepository extends MongoRepository<SavedOpportunity, String> {
    List<SavedOpportunity> findByUserId(String userId);
    Optional<SavedOpportunity> findByUserIdAndOpportunityId(String userId, String opportunityId);
    void deleteByUserIdAndOpportunityId(String userId, String opportunityId);
}
