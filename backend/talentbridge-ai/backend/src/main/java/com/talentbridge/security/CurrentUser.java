package com.talentbridge.security;

import com.talentbridge.auth.Role;
import java.io.Serializable;

/** What we store in the HTTP session (Spring Session / MongoDB-backed). */
public class CurrentUser implements Serializable {
    private final String userId;
    private final String email;
    private final Role role;

    public CurrentUser(String userId, String email, Role role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
