package com.talent.backend.user;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class AccountUserDetails implements UserDetails {

	private final String id;
	private final String email;
	private final String passwordHash;
	private final Role role;
	private final boolean mfaEnabled;
	private final AccountStatus status;

	public AccountUserDetails(UserAccount account) {
		this.id = account.getId();
		this.email = account.getEmail();
		this.passwordHash = account.getPasswordHash();
		this.role = account.getRole();
		this.mfaEnabled = account.isMfaEnabled();
		this.status = account.getStatus();
	}

	public boolean isAdmin() {
		return role != null && role.isAdmin();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isEnabled() {
		return status != AccountStatus.DISABLED;
	}
}
