package com.talentbridge.security;

import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class SessionAuthenticationToken extends AbstractAuthenticationToken {

    private final CurrentUser principal;

    public SessionAuthenticationToken(CurrentUser principal) {
        super(List.of(new SimpleGrantedAuthority("ROLE_" + principal.getRole().name())));
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
