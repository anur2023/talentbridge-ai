package com.talent.backend.config;

import com.talent.backend.common.CorrelationIdFilter;
import com.talent.backend.user.AccountUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AdminMfaFilter extends OncePerRequestFilter {

	public static final String MFA_VERIFIED = "MFA_VERIFIED";

	private final AppProperties properties;

	public AdminMfaFilter(AppProperties properties) {
		this.properties = properties;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String path = request.getRequestURI();
		if (!properties.getSecurity().isMfaRequired() || !path.startsWith("/api/v1/admin/")) {
			filterChain.doFilter(request, response);
			return;
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof AccountUserDetails user && user.isAdmin()) {
			HttpSession session = request.getSession(false);
			boolean verified = session != null && Boolean.TRUE.equals(session.getAttribute(MFA_VERIFIED));
			if (!user.isMfaEnabled()) {
				reject(response, "MFA_ENROLLMENT_REQUIRED", "Enrol an authenticator before using admin tools.");
				return;
			}
			if (!verified) {
				reject(response, "MFA_REQUIRED", "Enter your authenticator code before using admin tools.");
				return;
			}
		}
		filterChain.doFilter(request, response);
	}

	private void reject(HttpServletResponse response, String code, String message) throws IOException {
		response.setStatus(403);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
		if (correlationId == null) {
			correlationId = "";
		}
		response.getWriter().write("""
				{"code":"%s","message":"%s","fieldErrors":[],"correlationId":"%s"}
				""".formatted(code, message, correlationId.replace("\"", "")));
	}
}
