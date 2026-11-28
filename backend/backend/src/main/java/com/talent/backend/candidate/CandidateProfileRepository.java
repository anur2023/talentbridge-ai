package com.talent.backend.candidate;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CandidateProfileRepository extends MongoRepository<CandidateProfile, String> {

	Optional<CandidateProfile> findByUserId(String userId);
}
