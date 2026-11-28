package com.talentbridge.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Promotes the CurrentUser stored in the HTTP session (by LoginService) into
 * the Spring Security context for this request, so @PreAuthorize works.
 */
@Component
@Order(2)
public class SessionUserFilter extends OncePerRequestFilter {

    public static final String SESSION_KEY = "TB_CURRENT_USER";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object value = session.getAttribute(SESSION_KEY);
            if (value instanceof CurrentUser currentUser) {
                SecurityContextHolder.getContext().setAuthentication(new SessionAuthenticationToken(currentUser));
            }
        }
        chain.doFilter(request, response);
    }
}
