package com.talentbridge.opportunity;

import com.talentbridge.common.ApiException;
import com.talentbridge.common.AuditService;
import com.talentbridge.opportunity.dto.UpsertOpportunityRequest;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OpportunityService {

    private final OpportunityRepository repository;
    private final OpportunitySearchRepository searchRepository;
    private final OpportunityStatusMachine statusMachine;
    private final AuditService auditService;

    public OpportunityService(OpportunityRepository repository, OpportunitySearchRepository searchRepository,
                               OpportunityStatusMachine statusMachine, AuditService auditService) {
        this.repository = repository;
        this.searchRepository = searchRepository;
        this.statusMachine = statusMachine;
        this.auditService = auditService;
    }

    public Opportunity create(UpsertOpportunityRequest request, String actorUserId, String actorRole) {
        Opportunity opportunity = new Opportunity();
        apply(opportunity, request);
        opportunity.setAuthorUserId(actorUserId);
        opportunity.setStatus(OpportunityStatus.DRAFT);
        opportunity.setSlug(generateUniqueSlug(request.title()));
        Opportunity saved = repository.save(opportunity);
        auditService.record(actorUserId, actorRole, "OPPORTUNITY_CREATE", "Opportunity", saved.getId(), saved.getTitle());
        return saved;
    }

    public Opportunity update(String id, UpsertOpportunityRequest request, String actorUserId, String actorRole) {
        Opportunity opportunity = require(id);
        apply(opportunity, request);
        Opportunity saved = repository.save(opportunity);
        auditService.record(actorUserId, actorRole, "OPPORTUNITY_UPDATE", "Opportunity", saved.getId(), saved.getTitle());
        return saved;
    }

    public Opportunity transition(String id, OpportunityStatus to, String actorUserId, String actorRole) {
        Opportunity opportunity = require(id);
        statusMachine.assertTransitionAllowed(opportunity.getStatus(), to);

        if (to == OpportunityStatus.PUBLISHED) {
            statusMachine.assertPublishable(opportunity);
            // A listing claiming any referral type other than PUBLIC_APPLICATION must be
            // published by someone other than its author (Day 8 rule, four-eyes control).
            if (opportunity.getReferralType() != ReferralType.PUBLIC_APPLICATION
                    && actorUserId.equals(opportunity.getAuthorUserId())) {
                throw ApiException.forbidden("SELF_PUBLISH_NOT_ALLOWED",
                        "A listing with a referral claim must be published by a different admin than its author.");
            }
            opportunity.setPublishedByUserId(actorUserId);
            opportunity.setStale(false);
        }

        opportunity.setStatus(to);
        Opportunity saved = repository.save(opportunity);
        auditService.record(actorUserId, actorRole, "OPPORTUNITY_STATUS_" + to, "Opportunity", saved.getId(), null);
        return saved;
    }

    public Page<Opportunity> adminList(OpportunityStatus status, Pageable pageable) {
        return status != null ? repository.findByStatus(status, pageable) : repository.findAll(pageable);
    }

    public Opportunity require(String id) {
        return repository.findById(id)
                .orElseThrow(() -> ApiException.notFound("OPPORTUNITY_NOT_FOUND", "Listing not found."));
    }

    public Opportunity requirePublicBySlug(String slug) {
        Opportunity opportunity = repository.findBySlug(slug)
                .orElseThrow(() -> ApiException.notFound("OPPORTUNITY_NOT_FOUND", "Listing not found."));
        if (!opportunity.isPubliclyVisible()) {
            throw ApiException.notFound("OPPORTUNITY_NOT_FOUND", "Listing not found.");
        }
        return opportunity;
    }

    public Page<Opportunity> publicSearch(String keyword, OpportunityCategory category, WorkType workType,
                                           Boolean remote, String experienceBand, String country, String language,
                                           Boolean compensationDisclosed, Pageable pageable) {
        return searchRepository.search(keyword, category, workType, remote, experienceBand, country, language,
                compensationDisclosed, pageable);
    }

    /** Called by the daily scheduler: expires past-deadline listings and flags stale ones. */
    public int expirePastDeadline() {
        var expired = repository.findByStatusAndDeadlineBefore(OpportunityStatus.PUBLISHED, LocalDate.now());
        expired.forEach(o -> o.setStatus(OpportunityStatus.EXPIRED));
        repository.saveAll(expired);
        return expired.size();
    }

    public int flagStale(int staleAfterDays) {
        LocalDate threshold = LocalDate.now().minusDays(staleAfterDays);
        var stale = repository.findByStatusAndVerifiedAtBeforeAndStaleFalse(OpportunityStatus.PUBLISHED, threshold);
        stale.forEach(o -> o.setStale(true));
        repository.saveAll(stale);
        return stale.size();
    }

    private void apply(Opportunity opportunity, UpsertOpportunityRequest request) {
        opportunity.setTitle(request.title());
        opportunity.setDescription(request.description());
        opportunity.setSkills(request.skills() != null ? request.skills() : java.util.List.of());
        opportunity.setCategory(request.category());
        opportunity.setWorkType(request.workType());
        opportunity.setRemote(request.remote());
        opportunity.setExperienceBand(request.experienceBand());
        opportunity.setCountry(request.country());
        opportunity.setLanguages(request.languages() != null ? request.languages() : java.util.List.of());
        opportunity.setCompensationDisclosed(request.compensationDisclosed());
        opportunity.setCompensationText(request.compensationText());
        opportunity.setPlatformId(request.platformId());
        opportunity.setReferralType(request.referralType() != null ? request.referralType() : ReferralType.PUBLIC_APPLICATION);
        opportunity.setSponsored(request.sponsored());
        opportunity.setSourceUrl(request.sourceUrl());
        opportunity.setApplyUrl(request.applyUrl());
        opportunity.setEligibility(request.eligibility());
        opportunity.setDeadline(request.deadline());
        opportunity.setVerifiedAt(request.verifiedAt());
    }

    private String generateUniqueSlug(String title) {
        String base = SlugGenerator.base(title);
        String candidate = base;
        int suffix = 1;
        while (repository.existsBySlug(candidate)) {
            candidate = base + "-" + (++suffix);
        }
        return candidate;
    }
}
