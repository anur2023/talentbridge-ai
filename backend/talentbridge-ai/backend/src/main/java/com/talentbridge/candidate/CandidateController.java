package com.talentbridge.candidate;

import com.talentbridge.candidate.dto.CandidateProfileView;
import com.talentbridge.candidate.dto.FilePrivacyRequest;
import com.talentbridge.candidate.dto.UpdateCandidateProfileRequest;
import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidates")
@PreAuthorize("hasRole('CANDIDATE')")
public class CandidateController {

    private final CandidateService candidateService;
    private final RecommendationService recommendationService;

    public CandidateController(CandidateService candidateService, RecommendationService recommendationService) {
        this.candidateService = candidateService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/me/recommendations")
    public ApiResponse<java.util.List<com.talentbridge.opportunity.Opportunity>> recommendations(
            @AuthenticationPrincipal CurrentUser user,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        return ApiResponse.ok(recommendationService.recommend(user.getUserId(), limit), CorrelationIdFilter.current());
    }

    @GetMapping("/me")
    public ApiResponse<CandidateProfileView> getMyProfile(@AuthenticationPrincipal CurrentUser user) {
        CandidateProfile profile = candidateService.getOrCreate(user.getUserId());
        return ApiResponse.ok(CandidateProfileView.from(profile), CorrelationIdFilter.current());
    }

    @PutMapping("/me")
    public ApiResponse<CandidateProfileView> updateMyProfile(@AuthenticationPrincipal CurrentUser user,
                                                               @Valid @RequestBody UpdateCandidateProfileRequest request) {
        CandidateProfile profile = candidateService.update(user.getUserId(), request);
        return ApiResponse.ok(CandidateProfileView.from(profile), CorrelationIdFilter.current());
    }

    @GetMapping("/me/saved")
    public ApiResponse<java.util.List<SavedOpportunity>> listSaved(@AuthenticationPrincipal CurrentUser user) {
        return ApiResponse.ok(candidateService.listSaved(user.getUserId()), CorrelationIdFilter.current());
    }

    @PostMapping("/me/saved/{opportunityId}")
    public ApiResponse<SavedOpportunity> save(@AuthenticationPrincipal CurrentUser user,
                                               @PathVariable String opportunityId) {
        return ApiResponse.ok(candidateService.save(user.getUserId(), opportunityId), CorrelationIdFilter.current());
    }

    @DeleteMapping("/me/saved/{opportunityId}")
    public ApiResponse<Void> unsave(@AuthenticationPrincipal CurrentUser user,
                                     @PathVariable String opportunityId) {
        candidateService.unsave(user.getUserId(), opportunityId);
        return ApiResponse.ok(null, CorrelationIdFilter.current());
    }

    @PostMapping("/me/privacy-requests")
    public ApiResponse<PrivacyRequest> fileRequest(@AuthenticationPrincipal CurrentUser user,
                                                    @Valid @RequestBody FilePrivacyRequest request) {
        return ApiResponse.ok(candidateService.fileRequest(user.getUserId(), request.type(), request.details()),
                CorrelationIdFilter.current());
    }
}
