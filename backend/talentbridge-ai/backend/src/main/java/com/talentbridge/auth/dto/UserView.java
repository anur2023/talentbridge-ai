package com.talentbridge.auth.dto;

import com.talentbridge.auth.Role;
import com.talentbridge.auth.User;
import com.talentbridge.auth.UserStatus;

public record UserView(String id, String email, Role role, UserStatus status, boolean mfaEnabled) {
    public static UserView from(User user) {
        return new UserView(user.getId(), user.getEmail(), user.getRole(), user.getStatus(), user.isMfaEnabled());
    }
}
