package com.example.kiosk.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kiosk.api.dto.CreateUserRequest;
import com.example.kiosk.api.dto.ResetPasswordRequest;
import com.example.kiosk.api.dto.SetEnabledRequest;
import com.example.kiosk.api.dto.UserDto;
import com.example.kiosk.service.UserAdminService;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')") // ROLE_ADMIN required
public class AdminUsersController {

	private final UserAdminService svc;

	public AdminUsersController(UserAdminService svc) {
		this.svc = svc;
	}

	@GetMapping
	public List<UserDto> list() {
		return svc.listUsers();
	}

	@PostMapping
	public UserDto create(@RequestBody CreateUserRequest req) {
		return svc.createUser(req);
	}

	@PutMapping("/{id}/enabled")
	public ResponseEntity<?> setEnabled(@PathVariable long id, @RequestBody SetEnabledRequest req) {
		boolean enabled = req != null && Boolean.TRUE.equals(req.enabled);
		svc.setEnabled(id, enabled);
		return ResponseEntity.ok(Map.of("ok", true));
	}

	@PutMapping("/{id}/password")
	public ResponseEntity<?> resetPassword(@PathVariable long id, @RequestBody ResetPasswordRequest req) {
		if (req == null || req.password == null) {
			return ResponseEntity.badRequest().body(Map.of("error", "password required"));
		}
		svc.adminResetPassword(id, req.password);
		return ResponseEntity.ok(Map.of("ok", true));
	}
}
