package com.talentbridge.referral;

import com.talentbridge.common.ApiException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

import static com.talentbridge.referral.ReferralStatus.*;

@Component
public class ReferralStatusMachine {

    private static final Map<ReferralStatus, Set<ReferralStatus>> ALLOWED = new EnumMap<>(ReferralStatus.class);

    static {
        ALLOWED.put(DRAFT, EnumSet.of(READY, WITHDRAWN));
        ALLOWED.put(READY, EnumSet.of(SUBMITTED, WITHDRAWN));
        ALLOWED.put(SUBMITTED, EnumSet.of(ACKNOWLEDGED, REJECTED, WITHDRAWN));
        ALLOWED.put(ACKNOWLEDGED, EnumSet.of(ASSESSMENT, REJECTED, WITHDRAWN));
        ALLOWED.put(ASSESSMENT, EnumSet.of(SELECTED_ENGAGED, REJECTED, WITHDRAWN));
        ALLOWED.put(SELECTED_ENGAGED, EnumSet.of(REWARD_PENDING, DISPUTED, CLOSED));
        ALLOWED.put(REJECTED, EnumSet.of(CLOSED));
        ALLOWED.put(WITHDRAWN, EnumSet.of(CLOSED));
        ALLOWED.put(REWARD_PENDING, EnumSet.of(REWARD_CONFIRMED, DISPUTED, CLOSED));
        ALLOWED.put(REWARD_CONFIRMED, EnumSet.of(PAID, DISPUTED, CLOSED));
        ALLOWED.put(PAID, EnumSet.of(DISPUTED, CLOSED));
        ALLOWED.put(DISPUTED, EnumSet.of(REWARD_PENDING, REJECTED, CLOSED));
        ALLOWED.put(CLOSED, EnumSet.noneOf(ReferralStatus.class));
    }

    public void assertTransitionAllowed(ReferralStatus from, ReferralStatus to) {
        Set<ReferralStatus> allowed = ALLOWED.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw ApiException.badRequest("ILLEGAL_REFERRAL_TRANSITION",
                    "Cannot move a referral from " + from + " to " + to + ".");
        }
    }
}
