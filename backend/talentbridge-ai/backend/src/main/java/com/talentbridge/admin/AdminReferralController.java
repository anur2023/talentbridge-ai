package com.talentbridge.admin;

import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.common.PageRequestUtil;
import com.talentbridge.common.PageResponse;
import com.talentbridge.referral.CandidateReferral;
import com.talentbridge.referral.CandidateReferralRepository;
import com.talentbridge.referral.ReferralEvent;
import com.talentbridge.referral.ReferralService;
import com.talentbridge.referral.ReferralStatus;
import com.talentbridge.security.CurrentUser;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/referrals")
@PreAuthorize("hasAnyRole('REFERRAL_ADMIN', 'SUPER_ADMIN')")
public class AdminReferralController {

    private final ReferralService referralService;
    private final CandidateReferralRepository referralRepository;

    public AdminReferralController(ReferralService referralService, CandidateReferralRepository referralRepository) {
        this.referralService = referralService;
        this.referralRepository = referralRepository;
    }

    public record StatusChangeRequest(@NotBlank String evidenceReference, String note) {
    }

    @GetMapping
    public ApiResponse<PageResponse<CandidateReferral>> list(@RequestParam(required = false) String platformId,
                                                               @RequestParam(required = false) ReferralStatus status,
                                                               @RequestParam(required = false) Integer page,
                                                               @RequestParam(required = false) Integer size) {
        var pageable = PageRequestUtil.of(page, size);
        var result = platformId != null ? referralRepository.findByPlatformId(platformId, pageable)
                : status != null ? referralRepository.findByStatus(status, pageable)
                : referralRepository.findAll(pageable);
        return ApiResponse.ok(PageResponse.from(result), CorrelationIdFilter.current());
    }

    @GetMapping("/{id}/timeline")
    public ApiResponse<java.util.List<ReferralEvent>> timeline(@PathVariable String id) {
        return ApiResponse.ok(referralService.timeline(id), CorrelationIdFilter.current());
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<CandidateReferral> changeStatus(@AuthenticationPrincipal CurrentUser user, @PathVariable String id,
                                                         @RequestParam ReferralStatus to,
                                                         @org.springframework.web.bind.annotation.RequestBody StatusChangeRequest request) {
        return ApiResponse.ok(referralService.adminChangeStatus(id, to, request.evidenceReference(),
                user.getUserId(), request.note()), CorrelationIdFilter.current());
    }

    @PostMapping("/{id}/evidence")
    public ApiResponse<CandidateReferral> addEvidence(@PathVariable String id, @RequestParam String evidenceReference) {
        return ApiResponse.ok(referralService.addEvidence(id, evidenceReference), CorrelationIdFilter.current());
    }
}
