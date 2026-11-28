package com.talentbridge.opportunity;

import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.common.PageRequestUtil;
import com.talentbridge.common.PageResponse;
import com.talentbridge.opportunity.dto.UpsertOpportunityRequest;
import com.talentbridge.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/opportunities")
@PreAuthorize("hasAnyRole('EDITOR', 'SUPER_ADMIN')")
public class AdminOpportunityController {

    private final OpportunityService opportunityService;

    public AdminOpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    public ApiResponse<PageResponse<Opportunity>> list(@RequestParam(required = false) OpportunityStatus status,
                                                         @RequestParam(required = false) Integer page,
                                                         @RequestParam(required = false) Integer size) {
        var result = opportunityService.adminList(status, PageRequestUtil.of(page, size));
        return ApiResponse.ok(PageResponse.from(result), CorrelationIdFilter.current());
    }

    @GetMapping("/{id}")
    public ApiResponse<Opportunity> get(@PathVariable String id) {
        return ApiResponse.ok(opportunityService.require(id), CorrelationIdFilter.current());
    }

    @PostMapping
    public ApiResponse<Opportunity> create(@AuthenticationPrincipal CurrentUser user,
                                            @Valid @RequestBody UpsertOpportunityRequest request) {
        return ApiResponse.ok(opportunityService.create(request, user.getUserId(), user.getRole().name()),
                CorrelationIdFilter.current());
    }

    @PutMapping("/{id}")
    public ApiResponse<Opportunity> update(@AuthenticationPrincipal CurrentUser user, @PathVariable String id,
                                            @Valid @RequestBody UpsertOpportunityRequest request) {
        return ApiResponse.ok(opportunityService.update(id, request, user.getUserId(), user.getRole().name()),
                CorrelationIdFilter.current());
    }

    @PostMapping("/{id}/transition")
    public ApiResponse<Opportunity> transition(@AuthenticationPrincipal CurrentUser user, @PathVariable String id,
                                                @RequestParam OpportunityStatus to) {
        return ApiResponse.ok(opportunityService.transition(id, to, user.getUserId(), user.getRole().name()),
                CorrelationIdFilter.current());
    }
}
