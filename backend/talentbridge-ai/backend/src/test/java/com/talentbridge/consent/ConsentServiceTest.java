package com.talentbridge.consent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsentServiceTest {

    @Mock
    private ConsentRecordRepository repository;

    @Test
    void withdrawingOnePurposeNeverAffectsAnother() {
        ConsentService service = new ConsentService(repository);

        ConsentRecord grantReferral = new ConsentRecord("user-1", ConsentPurpose.REFERRAL_SHARING, "platform-1",
                ConsentEventType.GRANTED, "v1", "test", "user-1");
        ConsentRecord withdrawReferral = new ConsentRecord("user-1", ConsentPurpose.REFERRAL_SHARING, "platform-1",
                ConsentEventType.WITHDRAWN, "v1", "test", "user-1");
        ConsentRecord grantMarketing = new ConsentRecord("user-1", ConsentPurpose.MARKETING_EMAIL, null,
                ConsentEventType.GRANTED, "v1", "test", "user-1");

        when(repository.findByUserIdAndPurposeAndRecipientPlatformIdOrderByAtAsc(
                "user-1", ConsentPurpose.REFERRAL_SHARING, "platform-1"))
                .thenReturn(List.of(grantReferral, withdrawReferral));
        when(repository.findByUserIdAndPurposeOrderByAtAsc("user-1", ConsentPurpose.MARKETING_EMAIL))
                .thenReturn(List.of(grantMarketing));

        assertThat(service.hasActiveConsent("user-1", ConsentPurpose.REFERRAL_SHARING, "platform-1")).isFalse();
        assertThat(service.hasActiveConsent("user-1", ConsentPurpose.MARKETING_EMAIL, null)).isTrue();
    }

    @Test
    void noHistoryMeansNoActiveConsent() {
        ConsentService service = new ConsentService(repository);
        when(repository.findByUserIdAndPurposeOrderByAtAsc("user-2", ConsentPurpose.PROFILE_PROCESSING))
                .thenReturn(List.of());
        assertThat(service.hasActiveConsent("user-2", ConsentPurpose.PROFILE_PROCESSING, null)).isFalse();
    }
}
