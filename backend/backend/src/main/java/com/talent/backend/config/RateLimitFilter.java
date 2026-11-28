package com.talent.backend.config;

import com.talent.backend.common.CorrelationIdFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class RateLimitFilter extends OncePerRequestFilter {

	private final RateLimitService limits;
	private final AppProperties properties;

	public RateLimitFilter(RateLimitService limits, AppProperties properties) {
		this.limits = limits;
		this.properties = properties;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Rule rule = match(request);
		if (rule != null) {
			String ip = clientIp(request);
			if (!limits.allow(rule.name() + ":" + ip, rule.limit(), rule.period())) {
				response.setStatus(429);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setHeader("Retry-After", String.valueOf(rule.period().toSeconds()));
				String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
				if (correlationId == null) {
					correlationId = "";
				}
				response.getWriter().write("""
						{"code":"RATE_LIMITED","message":"Too many requests. Try again later.","fieldErrors":[],"correlationId":"%s"}
						""".formatted(correlationId.replace("\"", "")));
				return;
			}
		}
		filterChain.doFilter(request, response);
	}

	private Rule match(HttpServletRequest request) {
		String path = request.getRequestURI();
		String method = request.getMethod();
		AppProperties.RateLimit configured = properties.getRateLimit();
		if (HttpMethod.POST.matches(method) && path.endsWith("/api/v1/auth/register")) {
			return new Rule("register", configured.getRegister(), Duration.ofHours(1));
		}
		if (HttpMethod.POST.matches(method) && path.endsWith("/api/v1/auth/login")) {
			return new Rule("login", configured.getLogin(), Duration.ofMinutes(1));
		}
		if (HttpMethod.POST.matches(method) && (path.endsWith("/api/v1/auth/forgot-password") || path.endsWith("/api/v1/auth/resend-verification"))) {
			return new Rule("forgot", configured.getForgotPassword(), Duration.ofHours(1));
		}
		if (HttpMethod.POST.matches(method) && path.endsWith("/api/v1/referrals")) {
			return new Rule("referral", configured.getReferral(), Duration.ofHours(1));
		}
		if (HttpMethod.GET.matches(method) && (path.endsWith("/api/v1/opportunities") || path.contains("/api/v1/opportunities?"))) {
			return new Rule("search", configured.getSearch(), Duration.ofMinutes(1));
		}
		if (HttpMethod.POST.matches(method) && path.contains("/api/v1/support/")) {
			return new Rule("support", configured.getSupport(), Duration.ofHours(1));
		}
		return null;
	}

	private String clientIp(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");
		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

	private record Rule(String name, int limit, Duration period) {
	}
}
