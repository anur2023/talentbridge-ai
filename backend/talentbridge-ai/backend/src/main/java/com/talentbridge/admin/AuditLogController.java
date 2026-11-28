package com.talentbridge.admin;

import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.AuditLogRepository;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.common.PageRequestUtil;
import com.talentbridge.common.PageResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Super Admin only, per the security checklist. */
@RestController
@RequestMapping("/api/admin/audit-logs")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AuditLogController {

    private final AuditLogRepository repository;

    public AuditLogController(AuditLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ApiResponse<PageResponse<com.talentbridge.common.AuditLog>> list(@RequestParam(required = false) Integer page,
                                                                              @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(PageResponse.from(repository.findAll(PageRequestUtil.of(page, size))),
                CorrelationIdFilter.current());
    }

    @GetMapping("/by-target")
    public ApiResponse<PageResponse<com.talentbridge.common.AuditLog>> byTarget(@RequestParam String targetType,
                                                                                  @RequestParam String targetId,
                                                                                  @RequestParam(required = false) Integer page,
                                                                                  @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(
                PageResponse.from(repository.findByTargetTypeAndTargetId(targetType, targetId, PageRequestUtil.of(page, size))),
                CorrelationIdFilter.current());
    }
}
