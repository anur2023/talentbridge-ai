package com.talentbridge.opportunity;

import com.talentbridge.common.ApiException;
import com.talentbridge.common.AuditService;
import com.talentbridge.opportunity.dto.UpsertPlatformRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PlatformService {

    private final PlatformRepository repository;
    private final AuditService auditService;

    public PlatformService(PlatformRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    public Page<Platform> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Platform require(String id) {
        return repository.findById(id)
                .orElseThrow(() -> ApiException.notFound("PLATFORM_NOT_FOUND", "Platform not found."));
    }

    public Platform create(UpsertPlatformRequest request, String actorUserId, String actorRole) {
        Platform platform = new Platform();
        apply(platform, request);
        Platform saved = repository.save(platform);
        auditService.record(actorUserId, actorRole, "PLATFORM_CREATE", "Platform", saved.getId(), saved.getName());
        return saved;
    }

    public Platform update(String id, UpsertPlatformRequest request, String actorUserId, String actorRole) {
        Platform platform = require(id);
        apply(platform, request);
        Platform saved = repository.save(platform);
        auditService.record(actorUserId, actorRole, "PLATFORM_UPDATE", "Platform", saved.getId(), saved.getName());
        return saved;
    }

    private void apply(Platform platform, UpsertPlatformRequest request) {
        platform.setName(request.name());
        platform.setWebsite(request.website());
        platform.setOfficialTermsUrl(request.officialTermsUrl());
        platform.setReferralProgrammeStatus(
                request.referralProgrammeStatus() != null ? request.referralProgrammeStatus() : ReferralProgrammeStatus.NOT_REVIEWED);
        platform.setLastTermsReviewDate(request.lastTermsReviewDate());
    }
}
