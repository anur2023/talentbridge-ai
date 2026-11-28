package com.talentbridge.opportunity;

import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.common.PageRequestUtil;
import com.talentbridge.common.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/opportunities")
public class PublicOpportunityController {

    private final OpportunityService opportunityService;

    public PublicOpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping
    public ApiResponse<PageResponse<Opportunity>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) OpportunityCategory category,
            @RequestParam(required = false) WorkType workType,
            @RequestParam(required = false) Boolean remote,
            @RequestParam(required = false) String experienceBand,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Boolean compensationDisclosed,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        var pageable = PageRequestUtil.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        var result = opportunityService.publicSearch(keyword, category, workType, remote, experienceBand, country,
                language, compensationDisclosed, pageable);
        return ApiResponse.ok(PageResponse.from(result), CorrelationIdFilter.current());
    }

    @GetMapping("/filters")
    public ApiResponse<java.util.Map<String, Object>> filters() {
        return ApiResponse.ok(java.util.Map.of(
                "categories", OpportunityCategory.values(),
                "workTypes", WorkType.values()
        ), CorrelationIdFilter.current());
    }

    @GetMapping("/{slug}")
    public ApiResponse<Opportunity> getBySlug(@PathVariable String slug) {
        return ApiResponse.ok(opportunityService.requirePublicBySlug(slug), CorrelationIdFilter.current());
    }
}
