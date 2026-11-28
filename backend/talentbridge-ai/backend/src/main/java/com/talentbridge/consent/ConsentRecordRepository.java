package com.talentbridge.consent;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Deliberately exposes only save (insert) and read methods — no deleteBy*,
 * so the append-only contract can't be bypassed by a future caller.
 */
public interface ConsentRecordRepository extends MongoRepository<ConsentRecord, String> {
    List<ConsentRecord> findByUserIdOrderByAtAsc(String userId);
    List<ConsentRecord> findByUserIdAndPurposeOrderByAtAsc(String userId, ConsentPurpose purpose);
    List<ConsentRecord> findByUserIdAndPurposeAndRecipientPlatformIdOrderByAtAsc(
            String userId, ConsentPurpose purpose, String recipientPlatformId);
}
