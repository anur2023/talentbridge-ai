package com.talentbridge.config;

import com.talentbridge.opportunity.Opportunity;
import com.talentbridge.opportunity.OpportunityCategory;
import com.talentbridge.opportunity.OpportunityRepository;
import com.talentbridge.opportunity.OpportunityStatus;
import com.talentbridge.opportunity.Platform;
import com.talentbridge.opportunity.PlatformRepository;
import com.talentbridge.opportunity.ReferralProgrammeStatus;
import com.talentbridge.opportunity.ReferralType;
import com.talentbridge.opportunity.SlugGenerator;
import com.talentbridge.opportunity.WorkType;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Dev/staging only (talentbridge.seed.enabled). Creates clearly-fake,
 * .example-URL synthetic data per the Day 3 seed specification: a handful of
 * platforms and ~30 opportunities.
 */
@Component
public class SeedDataLoader implements CommandLineRunner {

    private final boolean enabled;
    private final OpportunityRepository opportunityRepository;
    private final PlatformRepository platformRepository;

    public SeedDataLoader(@Value("${talentbridge.seed.enabled}") boolean enabled,
                           OpportunityRepository opportunityRepository, PlatformRepository platformRepository) {
        this.enabled = enabled;
        this.opportunityRepository = opportunityRepository;
        this.platformRepository = platformRepository;
    }

    @Override
    public void run(String... args) {
        if (!enabled || opportunityRepository.count() > 0) {
            return;
        }

        Platform platform = new Platform();
        platform.setName("Example AI Labs");
        platform.setWebsite("https://example-ai-labs.example");
        platform.setOfficialTermsUrl("https://example-ai-labs.example/terms");
        platform.setReferralProgrammeStatus(ReferralProgrammeStatus.REVIEWED_APPROVED);
        platform.setLastTermsReviewDate(LocalDate.now().minusDays(10));
        platform = platformRepository.save(platform);

        for (int i = 1; i <= 30; i++) {
            Opportunity o = new Opportunity();
            String title = "Synthetic AI Evaluator Role #" + i;
            o.setTitle(title);
            o.setSlug(SlugGenerator.base(title) + "-" + i);
            o.setDescription("This is a synthetic seed listing for local development and testing only.");
            o.setSkills(java.util.List.of("python", "data-labeling", "prompt-evaluation"));
            o.setCategory(OpportunityCategory.values()[i % OpportunityCategory.values().length]);
            o.setWorkType(WorkType.values()[i % WorkType.values().length]);
            o.setRemote(true);
            o.setExperienceBand("ENTRY");
            o.setCountry("IN");
            o.setLanguages(java.util.List.of("en"));
            o.setCompensationDisclosed(false);
            o.setPlatformId(platform.getId());
            o.setReferralType(ReferralType.PUBLIC_APPLICATION);
            o.setSourceUrl("https://example-ai-labs.example/jobs/" + i);
            o.setApplyUrl("https://example-ai-labs.example/jobs/" + i + "/apply");
            o.setEligibility("Open to applicants in India, 18+.");
            o.setDeadline(LocalDate.now().plusDays(30));
            o.setVerifiedAt(LocalDate.now());
            o.setStatus(OpportunityStatus.PUBLISHED);
            o.setAuthorUserId("seed");
            o.setPublishedByUserId("seed");
            opportunityRepository.save(o);
        }
    }
}
