package com.talentbridge.candidate;

import com.talentbridge.opportunity.Opportunity;
import com.talentbridge.opportunity.OpportunityRepository;
import com.talentbridge.opportunity.OpportunityStatus;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/** Deferred scope's "AI matching" is simplified to a skill-overlap rule per the MVP boundary. */
@Service
public class RecommendationService {

    private final OpportunityRepository opportunityRepository;
    private final CandidateProfileRepository profileRepository;

    public RecommendationService(OpportunityRepository opportunityRepository, CandidateProfileRepository profileRepository) {
        this.opportunityRepository = opportunityRepository;
        this.profileRepository = profileRepository;
    }

    public List<Opportunity> recommend(String userId, int limit) {
        CandidateProfile profile = profileRepository.findByUserId(userId).orElse(null);
        List<Opportunity> published = opportunityRepository.findByStatus(OpportunityStatus.PUBLISHED,
                        org.springframework.data.domain.PageRequest.of(0, 200))
                .stream().filter(Opportunity::isPubliclyVisible).toList();

        if (profile == null || profile.getSkills().isEmpty()) {
            // No profile signal yet: latest matching listings, newest first.
            return published.stream()
                    .sorted(Comparator.comparing(Opportunity::getUpdatedAt).reversed())
                    .limit(limit)
                    .toList();
        }

        java.util.Set<String> mySkills = profile.getSkills().stream()
                .map(String::toLowerCase).collect(java.util.stream.Collectors.toSet());

        return published.stream()
                .sorted(Comparator.<Opportunity>comparingInt(o -> -overlap(mySkills, o))
                        .thenComparing(Comparator.comparing(Opportunity::getUpdatedAt).reversed()))
                .limit(limit)
                .toList();
    }

    private int overlap(java.util.Set<String> mySkills, Opportunity o) {
        return (int) o.getSkills().stream().map(String::toLowerCase).filter(mySkills::contains).count();
    }
}
