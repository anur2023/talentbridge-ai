package com.talentbridge.opportunity;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.talentbridge.common.ApiException;
import org.junit.jupiter.api.Test;

class OpportunityStatusMachineTest {

    private final OpportunityStatusMachine machine = new OpportunityStatusMachine();

    @Test
    void publishingRequiresMandatoryFields() {
        Opportunity o = new Opportunity();
        assertThatThrownBy(() -> machine.assertPublishable(o)).isInstanceOf(ApiException.class);
    }

    @Test
    void publishableWhenAllFieldsPresent() {
        Opportunity o = new Opportunity();
        o.setSourceUrl("https://example.com/job");
        o.setApplyUrl("https://example.com/job/apply");
        o.setVerifiedAt(java.time.LocalDate.now());
        o.setEligibility("Open to all.");
        assertThatCode(() -> machine.assertPublishable(o)).doesNotThrowAnyException();
    }

    @Test
    void archivedIsTerminal() {
        assertThatThrownBy(() -> machine.assertTransitionAllowed(OpportunityStatus.ARCHIVED, OpportunityStatus.DRAFT))
                .isInstanceOf(ApiException.class);
    }
}
