package com.talentbridge.admin;

import com.talentbridge.candidate.CandidateProfile;
import com.talentbridge.candidate.CandidateProfileRepository;
import com.talentbridge.candidate.PrivacyRequest;
import com.talentbridge.candidate.PrivacyRequestRepository;
import com.talentbridge.candidate.PrivacyRequestStatus;
import com.talentbridge.common.ApiException;
import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.AuditService;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.common.PageRequestUtil;
import com.talentbridge.common.PageResponse;
import com.talentbridge.referral.CandidateReferralRepository;
import com.talentbridge.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Read-only candidate CRM (FR-ADM-01). Only REFERRAL_ADMIN/SUPER_ADMIN may
 * open it; every detail view is written to the audit log (Day 18 rule).
 */
@RestController
@RequestMapping("/api/admin/candidates")
@PreAuthorize("hasAnyRole('REFERRAL_ADMIN', 'SUPER_ADMIN')")
public class AdminCandidateController {

    private final CandidateProfileRepository profileRepository;
    private final CandidateReferralRepository referralRepository;
    private final PrivacyRequestRepository privacyRequestRepository;
    private final AuditService auditService;

    public AdminCandidateController(CandidateProfileRepository profileRepository,
                                     CandidateReferralRepository referralRepository,
                                     PrivacyRequestRepository privacyRequestRepository,
                                     AuditService auditService) {
        this.profileRepository = profileRepository;
        this.referralRepository = referralRepository;
        this.privacyRequestRepository = privacyRequestRepository;
        this.auditService = auditService;
    }

    @GetMapping
    public ApiResponse<PageResponse<CandidateProfile>> list(@RequestParam(required = false) Integer page,
                                                              @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(PageResponse.from(profileRepository.findAll(PageRequestUtil.of(page, size))),
                CorrelationIdFilter.current());
    }

    @GetMapping("/{userId}")
    public ApiResponse<CandidateDetail> get(@AuthenticationPrincipal CurrentUser admin, @PathVariable String userId) {
        CandidateProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> ApiException.notFound("PROFILE_NOT_FOUND", "Candidate not found."));
        auditService.record(admin.getUserId(), admin.getRole().name(), "CANDIDATE_DETAIL_VIEW", "CandidateProfile",
                userId, null);
        var referrals = referralRepository.findByCandidateUserId(userId);
        return ApiResponse.ok(new CandidateDetail(profile, referrals), CorrelationIdFilter.current());
    }

    @GetMapping("/privacy-requests")
    public ApiResponse<PageResponse<PrivacyRequest>> privacyQueue(@RequestParam(required = false) PrivacyRequestStatus status,
                                                                     @RequestParam(required = false) Integer page,
                                                                     @RequestParam(required = false) Integer size) {
        var pageable = PageRequestUtil.of(page, size);
        var result = status != null ? privacyRequestRepository.findByStatus(status, pageable)
                : privacyRequestRepository.findAll(pageable);
        return ApiResponse.ok(PageResponse.from(result), CorrelationIdFilter.current());
    }

    @PatchMapping("/privacy-requests/{id}")
    public ApiResponse<PrivacyRequest> updatePrivacyRequest(@AuthenticationPrincipal CurrentUser admin,
                                                              @PathVariable String id,
                                                              @RequestParam PrivacyRequestStatus status,
                                                              @RequestParam(required = false) String note) {
        PrivacyRequest request = privacyRequestRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("PRIVACY_REQUEST_NOT_FOUND", "Request not found."));
        request.setStatus(status);
        request.setResolutionNote(note);
        PrivacyRequest saved = privacyRequestRepository.save(request);
        auditService.record(admin.getUserId(), admin.getRole().name(), "PRIVACY_REQUEST_" + status, "PrivacyRequest",
                id, note);
        return ApiResponse.ok(saved, CorrelationIdFilter.current());
    }

    public record CandidateDetail(CandidateProfile profile, java.util.List<com.talentbridge.referral.CandidateReferral> referrals) {
    }
}
