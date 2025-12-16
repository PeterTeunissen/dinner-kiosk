package com.example.kiosk.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kiosk.api.dto.CreateUserRequest;
import com.example.kiosk.api.dto.UserDto;
import com.example.kiosk.domain.AppUser;
import com.example.kiosk.domain.AppUserRole;
import com.example.kiosk.repo.AppUserRepository;
import com.example.kiosk.repo.AppUserRoleRepository;

@Service
public class UserAdminService {

	private final AppUserRepository users;
	private final AppUserRoleRepository roles;
	private final PasswordEncoder encoder;

	// Lockout policy (tweak to taste)
	private static final int MAX_FAILS = 5;
	private static final Duration LOCK_DURATION = Duration.ofMinutes(5);

	public UserAdminService(AppUserRepository users, AppUserRoleRepository roles, PasswordEncoder encoder) {
		this.users = users;
		this.roles = roles;
		this.encoder = encoder;
	}

	@Transactional(readOnly = true)
	public boolean isLocked(AppUser u) {
		return u.getLockedUntil() != null && u.getLockedUntil().isAfter(Instant.now());
	}

	@Transactional
	public void onLoginSuccess(AppUser u) {
		if (u.getFailedAttempts() != 0 || u.getLockedUntil() != null) {
			u.setFailedAttempts(0);
			u.setLockedUntil(null);
			users.save(u);
		}
	}

	@Transactional
	public void onLoginFailure(AppUser u) {
		int fails = u.getFailedAttempts() + 1;
		u.setFailedAttempts(fails);

		if (fails >= MAX_FAILS) {
			u.setLockedUntil(Instant.now().plus(LOCK_DURATION));
		}
		users.save(u);
	}

	/* ---------------- Admin endpoints ---------------- */

	@Transactional(readOnly = true)
	public List<UserDto> listUsers() {
		return users.findAll().stream().map(this::toDto).toList();
	}

	@Transactional
	public UserDto createUser(CreateUserRequest req) {
		String username = (req.username == null) ? "" : req.username.trim();
		if (username.isEmpty())
			throw new IllegalArgumentException("username required");
		if (req.password == null || req.password.length() < 8)
			throw new IllegalArgumentException("password min 8 chars");

		users.findByUsername(username).ifPresent(u -> {
			throw new IllegalArgumentException("username exists");
		});

		AppUser u = new AppUser();
		u.setUsername(username);
		u.setPasswordHash(encoder.encode(req.password));
		u.setEnabled(req.enabled == null || req.enabled);

		u = users.save(u);

		// roles
		List<String> rs = (req.roles == null || req.roles.isEmpty()) ? List.of("ROLE_USER")
				: req.roles.stream().map(String::trim).filter(s -> !s.isEmpty()).toList();

		for (String r : rs)
			roles.save(new AppUserRole(u.getId(), r));

		return toDto(u);
	}

	@Transactional
	public void setEnabled(long id, boolean enabled) {
		AppUser u = users.findById(id).orElseThrow();
		u.setEnabled(enabled);
		users.save(u);
	}

	@Transactional
	public void adminResetPassword(long id, String newPassword) {
		if (newPassword == null || newPassword.trim().length() < 8) {
			throw new IllegalArgumentException("password min 8 chars");
		}
		AppUser u = users.findById(id).orElseThrow();
		u.setPasswordHash(encoder.encode(newPassword.trim()));
		// reset lock/fails as well
		u.setFailedAttempts(0);
		u.setLockedUntil(null);
		users.save(u);
	}

	@Transactional(readOnly = true)
	public List<String> rolesForUsername(String username) {
		AppUser u = users.findByUsername(username).orElseThrow();
		return roles.findByUserId(u.getId()).stream().map(AppUserRole::getRole).toList();
	}

	private UserDto toDto(AppUser u) {
		UserDto dto = new UserDto();
		dto.id = u.getId();
		dto.username = u.getUsername();
		dto.enabled = u.isEnabled();
		dto.failedAttempts = u.getFailedAttempts();
		dto.lockedUntil = u.getLockedUntil();
		dto.roles = roles.findByUserId(u.getId()).stream().map(AppUserRole::getRole).toList();
		return dto;
	}
}
