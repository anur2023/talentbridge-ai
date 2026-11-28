package com.talentbridge.consent;

import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.consent.dto.ConsentActionRequest;
import com.talentbridge.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consents")
@PreAuthorize("hasRole('CANDIDATE')")
public class ConsentController {

    private final ConsentService consentService;

    public ConsentController(ConsentService consentService) {
        this.consentService = consentService;
    }

    @PostMapping
    public ApiResponse<ConsentRecord> grant(@AuthenticationPrincipal CurrentUser user,
                                             @Valid @RequestBody ConsentActionRequest request) {
        ConsentRecord record = consentService.grant(user.getUserId(), request.purpose(), request.recipientPlatformId(),
                request.source() != null ? request.source() : "candidate-app", user.getUserId());
        return ApiResponse.ok(record, CorrelationIdFilter.current());
    }

    @PostMapping("/withdraw")
    public ApiResponse<ConsentRecord> withdraw(@AuthenticationPrincipal CurrentUser user,
                                                @Valid @RequestBody ConsentActionRequest request) {
        ConsentRecord record = consentService.withdraw(user.getUserId(), request.purpose(), request.recipientPlatformId(),
                request.source() != null ? request.source() : "candidate-app", user.getUserId());
        return ApiResponse.ok(record, CorrelationIdFilter.current());
    }

    @GetMapping("/me")
    public ApiResponse<java.util.List<ConsentRecord>> history(@AuthenticationPrincipal CurrentUser user) {
        return ApiResponse.ok(consentService.history(user.getUserId()), CorrelationIdFilter.current());
    }
}
