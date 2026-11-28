package com.talentbridge.referral;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.talentbridge.common.ApiException;
import org.junit.jupiter.api.Test;

class ReferralStatusMachineTest {

    private final ReferralStatusMachine machine = new ReferralStatusMachine();

    @Test
    void allowsDraftToReady() {
        assertThatCode(() -> machine.assertTransitionAllowed(ReferralStatus.DRAFT, ReferralStatus.READY))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsSkippingStates() {
        assertThatThrownBy(() -> machine.assertTransitionAllowed(ReferralStatus.DRAFT, ReferralStatus.PAID))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void closedIsTerminal() {
        assertThatThrownBy(() -> machine.assertTransitionAllowed(ReferralStatus.CLOSED, ReferralStatus.DRAFT))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void rejectedMustCloseNotReopen() {
        assertThatThrownBy(() -> machine.assertTransitionAllowed(ReferralStatus.REJECTED, ReferralStatus.SUBMITTED))
                .isInstanceOf(ApiException.class);
        assertThatCode(() -> machine.assertTransitionAllowed(ReferralStatus.REJECTED, ReferralStatus.CLOSED))
                .doesNotThrowAnyException();
    }
}
