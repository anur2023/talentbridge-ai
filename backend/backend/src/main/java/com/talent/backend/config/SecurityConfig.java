package com.talent.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talent.backend.common.ApiError;
import com.talent.backend.common.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(AppProperties.class)
public class SecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
		http
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
						.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.headers(headers -> headers
						.contentTypeOptions(content -> {})
						.frameOptions(frame -> frame.deny())
						.referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
						.contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; frame-ancestors 'none'; object-src 'none'; base-uri 'self'"))
						.httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31_536_000)))
				.exceptionHandling(errors -> errors
						.authenticationEntryPoint((request, response, ex) -> write(response, objectMapper, 401, "UNAUTHENTICATED", "Authentication is required."))
						.accessDeniedHandler((request, response, ex) -> write(response, objectMapper, 403, "FORBIDDEN", "You do not have access to this resource.")))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST,
								"/api/v1/auth/register",
								"/api/v1/auth/login",
								"/api/v1/auth/verify-email",
								"/api/v1/auth/forgot-password",
								"/api/v1/auth/reset-password",
								"/api/v1/auth/resend-verification").permitAll()
						.requestMatchers(HttpMethod.GET,
								"/api/v1/auth/csrf",
								"/api/v1/opportunities",
								"/api/v1/opportunities/**",
								"/api/v1/guides",
								"/api/v1/guides/**",
								"/api/v1/seo/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/support/**", "/api/v1/analytics/events").permitAll()
						.requestMatchers(HttpMethod.GET,
								"/sitemap.xml",
								"/robots.txt",
								"/opportunities/**",
								"/career-guides/**",
								"/actuator/health",
								"/actuator/health/**").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						.anyRequest().authenticated());
		return http.build();
	}

	private void write(HttpServletResponse response, ObjectMapper objectMapper, int status, String code, String message) throws java.io.IOException {
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(), new ApiError(code, message, List.of(), MDC.get(CorrelationIdFilter.MDC_KEY)));
	}
}
