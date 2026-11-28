package com.talentbridge.opportunity;

import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.common.PageRequestUtil;
import com.talentbridge.common.PageResponse;
import com.talentbridge.opportunity.dto.UpsertPlatformRequest;
import com.talentbridge.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/platforms")
@PreAuthorize("hasAnyRole('EDITOR', 'SUPER_ADMIN')")
public class AdminPlatformController {

    private final PlatformService platformService;

    public AdminPlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping
    public ApiResponse<PageResponse<Platform>> list(@RequestParam(required = false) Integer page,
                                                      @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(PageResponse.from(platformService.list(PageRequestUtil.of(page, size))),
                CorrelationIdFilter.current());
    }

    @PostMapping
    public ApiResponse<Platform> create(@AuthenticationPrincipal CurrentUser user,
                                         @Valid @RequestBody UpsertPlatformRequest request) {
        return ApiResponse.ok(platformService.create(request, user.getUserId(), user.getRole().name()),
                CorrelationIdFilter.current());
    }

    @PutMapping("/{id}")
    public ApiResponse<Platform> update(@AuthenticationPrincipal CurrentUser user, @PathVariable String id,
                                         @Valid @RequestBody UpsertPlatformRequest request) {
        return ApiResponse.ok(platformService.update(id, request, user.getUserId(), user.getRole().name()),
                CorrelationIdFilter.current());
    }
}
