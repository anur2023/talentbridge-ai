package com.talentbridge.finance;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RewardTransactionRepository extends MongoRepository<RewardTransaction, String> {
    List<RewardTransaction> findByReferralIdOrderByCreatedAtEventAsc(String referralId);
    Page<RewardTransaction> findByStatus(RewardStatus status, Pageable pageable);
    List<RewardTransaction> findByCurrencyCodeAndStatus(String currencyCode, RewardStatus status);
}
