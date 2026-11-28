package com.talentbridge.opportunity.support;

import com.talentbridge.common.ApiException;
import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.security.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final ListingReportRepository listingReportRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final RateLimiterService rateLimiterService;

    public SupportController(ListingReportRepository listingReportRepository,
                              ContactMessageRepository contactMessageRepository,
                              RateLimiterService rateLimiterService) {
        this.listingReportRepository = listingReportRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.rateLimiterService = rateLimiterService;
    }

    public record ListingReportRequest(@NotBlank String opportunityId, @NotBlank @Size(max = 1000) String reason,
                                        @Email String reporterEmail) {
    }

    public record ContactRequest(@NotBlank @Size(max = 120) String name, @NotBlank @Email String email,
                                  @NotBlank @Size(max = 2000) String message) {
    }

    @PostMapping("/listing-reports")
    public ApiResponse<Void> reportListing(@Valid @RequestBody ListingReportRequest request, HttpServletRequest httpRequest) {
        limit("report:" + clientKey(httpRequest));
        listingReportRepository.save(new ListingReport(request.opportunityId(), request.reason(), request.reporterEmail()));
        return ApiResponse.ok(null, CorrelationIdFilter.current());
    }

    @PostMapping("/contact")
    public ApiResponse<Void> contact(@Valid @RequestBody ContactRequest request, HttpServletRequest httpRequest) {
        limit("contact:" + clientKey(httpRequest));
        contactMessageRepository.save(new ContactMessage(request.name(), request.email(), request.message()));
        return ApiResponse.ok(null, CorrelationIdFilter.current());
    }

    private void limit(String key) {
        if (!rateLimiterService.tryConsume(key, 10, Duration.ofMinutes(15))) {
            throw ApiException.tooManyRequests("RATE_LIMITED", "Too many requests. Please try again later.");
        }
    }

    private String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return forwarded != null ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }
}
