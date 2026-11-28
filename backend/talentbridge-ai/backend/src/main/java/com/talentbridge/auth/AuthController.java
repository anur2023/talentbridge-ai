package com.talentbridge.auth;

import com.talentbridge.auth.dto.ForgotPasswordRequest;
import com.talentbridge.auth.dto.LoginRequest;
import com.talentbridge.auth.dto.RegisterRequest;
import com.talentbridge.auth.dto.ResetPasswordRequest;
import com.talentbridge.auth.dto.UserView;
import com.talentbridge.auth.dto.VerifyEmailRequest;
import com.talentbridge.common.ApiException;
import com.talentbridge.common.ApiResponse;
import com.talentbridge.common.CorrelationIdFilter;
import com.talentbridge.security.CurrentUser;
import com.talentbridge.security.RateLimiterService;
import com.talentbridge.security.SessionUserFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RateLimiterService rateLimiterService;

    public AuthController(AuthService authService, RateLimiterService rateLimiterService) {
        this.authService = authService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserView> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        checkRateLimit("register:" + clientKey(httpRequest), 5, Duration.ofMinutes(15));
        User user = authService.register(request.email(), request.password());
        return ApiResponse.ok(UserView.from(user), CorrelationIdFilter.current());
    }

    @PostMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request.token());
        return ApiResponse.ok(null, CorrelationIdFilter.current());
    }

    @PostMapping("/login")
    public ApiResponse<UserView> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        checkRateLimit("login:" + clientKey(httpRequest), 10, Duration.ofMinutes(15));
        User user = authService.authenticate(request.email(), request.password());
        httpRequest.getSession(true).setAttribute(SessionUserFilter.SESSION_KEY,
                new CurrentUser(user.getId(), user.getEmail(), user.getRole()));
        return ApiResponse.ok(UserView.from(user), CorrelationIdFilter.current());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest httpRequest) {
        var session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ApiResponse.ok(null, CorrelationIdFilter.current());
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUser> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser currentUser)) {
            throw ApiException.unauthorized("NOT_AUTHENTICATED", "You are not logged in.");
        }
        return ApiResponse.ok(currentUser, CorrelationIdFilter.current());
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, HttpServletRequest httpRequest) {
        checkRateLimit("forgot:" + clientKey(httpRequest), 5, Duration.ofMinutes(15));
        // Always the same response for known and unknown emails, to avoid enumeration.
        authService.forgotPassword(request.email());
        return ApiResponse.ok(null, CorrelationIdFilter.current());
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.token(), request.newPassword());
        return ApiResponse.ok(null, CorrelationIdFilter.current());
    }

    private void checkRateLimit(String key, int capacity, Duration window) {
        if (!rateLimiterService.tryConsume(key, capacity, window)) {
            throw ApiException.tooManyRequests("RATE_LIMITED", "Too many requests. Please try again later.");
        }
    }

    private String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return forwarded != null ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }
}
