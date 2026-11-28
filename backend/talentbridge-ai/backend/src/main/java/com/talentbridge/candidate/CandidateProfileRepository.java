package com.talentbridge.candidate;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CandidateProfileRepository extends MongoRepository<CandidateProfile, String> {
    Optional<CandidateProfile> findByUserId(String userId);
}
