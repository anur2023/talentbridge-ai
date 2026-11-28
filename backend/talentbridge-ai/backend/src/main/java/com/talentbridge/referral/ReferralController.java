package com.talentbridge.referral;

import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.referral.dto.CreateReferralRequest;
import com.talentbridge.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/referrals")
@PreAuthorize("hasRole('CANDIDATE')")
public class ReferralController {

    private final ReferralService referralService;

    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    @GetMapping("/eligibility")
    public ApiResponse<ReferralValidationResult> eligibility(@AuthenticationPrincipal CurrentUser user,
                                                               @RequestParam String opportunityId) {
        return ApiResponse.ok(referralService.checkEligibility(user.getUserId(), opportunityId), CorrelationIdFilter.current());
    }

    @PostMapping
    public ApiResponse<CandidateReferral> create(@AuthenticationPrincipal CurrentUser user,
                                                  @Valid @RequestBody CreateReferralRequest request,
                                                  @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        CandidateReferral referral = referralService.create(user.getUserId(), request.opportunityId(),
                request.disclosureAcknowledged(), idempotencyKey);
        return ApiResponse.ok(referral, CorrelationIdFilter.current());
    }

    @GetMapping("/me")
    public ApiResponse<java.util.List<CandidateReferral>> mine(@AuthenticationPrincipal CurrentUser user) {
        return ApiResponse.ok(referralService.listMine(user.getUserId()), CorrelationIdFilter.current());
    }

    @GetMapping("/{id}/timeline")
    public ApiResponse<java.util.List<ReferralEvent>> timeline(@AuthenticationPrincipal CurrentUser user,
                                                                 @PathVariable String id) {
        referralService.requireOwned(id, user.getUserId());
        return ApiResponse.ok(referralService.timeline(id), CorrelationIdFilter.current());
    }

    @PostMapping("/{id}/withdraw")
    public ApiResponse<CandidateReferral> withdraw(@AuthenticationPrincipal CurrentUser user, @PathVariable String id) {
        return ApiResponse.ok(referralService.withdraw(user.getUserId(), id), CorrelationIdFilter.current());
    }
}
