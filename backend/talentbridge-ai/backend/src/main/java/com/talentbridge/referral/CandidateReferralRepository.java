package com.talentbridge.referral;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CandidateReferralRepository extends MongoRepository<CandidateReferral, String> {
    List<CandidateReferral> findByCandidateUserId(String candidateUserId);
    Optional<CandidateReferral> findByIdAndCandidateUserId(String id, String candidateUserId);
    List<CandidateReferral> findByCandidateUserIdAndOpportunityId(String candidateUserId, String opportunityId);
    Optional<CandidateReferral> findByIdempotencyKey(String idempotencyKey);
    Page<CandidateReferral> findByPlatformId(String platformId, Pageable pageable);
    Page<CandidateReferral> findByStatus(ReferralStatus status, Pageable pageable);
}
