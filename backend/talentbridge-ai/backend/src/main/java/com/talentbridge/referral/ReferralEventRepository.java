package com.talentbridge.referral;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReferralEventRepository extends MongoRepository<ReferralEvent, String> {
    List<ReferralEvent> findByReferralIdOrderByAtAsc(String referralId);
}
