package com.talentbridge.consent;

import com.talentbridge.common.ApiException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConsentService {

    /** Bump when the privacy notice changes; recorded on every new consent event. */
    public static final String CURRENT_NOTICE_VERSION = "v1";

    private final ConsentRecordRepository repository;

    public ConsentService(ConsentRecordRepository repository) {
        this.repository = repository;
    }

    public ConsentRecord grant(String userId, ConsentPurpose purpose, String recipientPlatformId, String source,
                                String actorUserId) {
        validateRecipient(purpose, recipientPlatformId);
        return repository.save(new ConsentRecord(userId, purpose, recipientPlatformId, ConsentEventType.GRANTED,
                CURRENT_NOTICE_VERSION, source, actorUserId));
    }

    public ConsentRecord withdraw(String userId, ConsentPurpose purpose, String recipientPlatformId, String source,
                                   String actorUserId) {
        validateRecipient(purpose, recipientPlatformId);
        return repository.save(new ConsentRecord(userId, purpose, recipientPlatformId, ConsentEventType.WITHDRAWN,
                CURRENT_NOTICE_VERSION, source, actorUserId));
    }

    public List<ConsentRecord> history(String userId) {
        return repository.findByUserIdOrderByAtAsc(userId);
    }

    /**
     * Looks at the latest event for (purpose, recipient) only — independent of
     * every other purpose/recipient pair, so withdrawing one never touches
     * another (the Day 15 "done when" check).
     */
    public boolean hasActiveConsent(String userId, ConsentPurpose purpose, String recipientPlatformId) {
        List<ConsentRecord> events = (purpose == ConsentPurpose.REFERRAL_SHARING)
                ? repository.findByUserIdAndPurposeAndRecipientPlatformIdOrderByAtAsc(userId, purpose, recipientPlatformId)
                : repository.findByUserIdAndPurposeOrderByAtAsc(userId, purpose);
        if (events.isEmpty()) {
            return false;
        }
        ConsentRecord latest = events.get(events.size() - 1);
        return latest.getEventType() == ConsentEventType.GRANTED;
    }

    private void validateRecipient(ConsentPurpose purpose, String recipientPlatformId) {
        if (purpose == ConsentPurpose.REFERRAL_SHARING && (recipientPlatformId == null || recipientPlatformId.isBlank())) {
            throw ApiException.badRequest("RECIPIENT_REQUIRED", "REFERRAL_SHARING consent requires a named recipient platform.");
        }
    }
}
