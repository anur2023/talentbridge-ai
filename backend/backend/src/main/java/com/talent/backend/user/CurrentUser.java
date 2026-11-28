package com.talent.backend.user;

import com.talent.backend.common.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

	private CurrentUser() {
	}

	public static AccountUserDetails require() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof AccountUserDetails user)) {
			throw new BusinessException("UNAUTHENTICATED", HttpStatus.UNAUTHORIZED, "Authentication is required.");
		}
		return user;
	}
}
