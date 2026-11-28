package com.talentbridge.opportunity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Daily job: expires past-deadline listings and flags stale ones (Day 9). */
@Component
public class OpportunityMaintenanceScheduler {

    private static final Logger log = LoggerFactory.getLogger(OpportunityMaintenanceScheduler.class);

    private final OpportunityService opportunityService;
    private final int staleAfterDays;

    public OpportunityMaintenanceScheduler(OpportunityService opportunityService,
                                            @Value("${talentbridge.listing.stale-after-days}") int staleAfterDays) {
        this.opportunityService = opportunityService;
        this.staleAfterDays = staleAfterDays;
    }

    @Scheduled(cron = "0 15 2 * * *")
    public void runDailyMaintenance() {
        int expired = opportunityService.expirePastDeadline();
        int staled = opportunityService.flagStale(staleAfterDays);
        log.info("Opportunity maintenance: expired={} staleFlagged={}", expired, staled);
    }
}
