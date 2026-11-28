package com.talentbridge.analytics;

import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.security.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class AnalyticsController {

    private final AnalyticsEventRepository repository;
    private final RateLimiterService rateLimiterService;

    public AnalyticsController(AnalyticsEventRepository repository, RateLimiterService rateLimiterService) {
        this.repository = repository;
        this.rateLimiterService = rateLimiterService;
    }

    public record BeaconRequest(AnalyticsEventType type, String opportunityId, String utmSource) {
    }

    /** Public, cookieless beacon. No personal identifiers are accepted or stored. */
    @PostMapping("/api/public/analytics/beacon")
    public ApiResponse<Void> beacon(@RequestBody BeaconRequest request, HttpServletRequest httpRequest) {
        String key = "beacon:" + (httpRequest.getHeader("X-Forwarded-For") != null
                ? httpRequest.getHeader("X-Forwarded-For") : httpRequest.getRemoteAddr());
        if (rateLimiterService.tryConsume(key, 60, Duration.ofMinutes(1))) {
            repository.save(new AnalyticsEvent(request.type(), request.opportunityId(), request.utmSource()));
        }
        return ApiResponse.ok(null, CorrelationIdFilter.current());
    }

    @GetMapping("/api/admin/analytics/overview")
    @PreAuthorize("hasAnyRole('EDITOR', 'REFERRAL_ADMIN', 'FINANCE', 'SUPER_ADMIN')")
    public ApiResponse<Map<String, Object>> overview() {
        Map<AnalyticsEventType, Long> funnel = java.util.Arrays.stream(AnalyticsEventType.values())
                .collect(Collectors.toMap(t -> t, t -> (long) repository.findByType(t).size()));
        return ApiResponse.ok(Map.of("funnel", funnel), CorrelationIdFilter.current());
    }
}
