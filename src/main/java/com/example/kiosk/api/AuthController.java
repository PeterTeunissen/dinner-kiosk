package com.example.kiosk.api;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kiosk.api.dto.LoginRequest;
import com.example.kiosk.repo.AppUserRepository;
import com.example.kiosk.service.UserAdminService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationManager authManager;
	private final AppUserRepository userRepo;
	private final UserAdminService userAdminService;

	public AuthController(AuthenticationManager authManager, AppUserRepository userRepo,
			UserAdminService userAdminService) {
		this.authManager = authManager;
		this.userRepo = userRepo;
		this.userAdminService = userAdminService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest request,
			HttpServletResponse response) {

		String username = req.username == null ? "" : req.username.trim();
		String password = req.password;

		if (username.isEmpty() || password == null) {
			return ResponseEntity.badRequest().body(Map.of("error", "username and password required"));
		}

		Authentication authentication = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		// 1️⃣ Create SecurityContext
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);

		// 2️⃣ Ensure session exists
		HttpSession session = request.getSession(true);

		// 3️⃣ Persist SecurityContext into the session ✅
		HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
		repo.saveContext(context, request, response);

		return ResponseEntity.ok(Map.of("ok", true, "username", username));
	}

	@GetMapping("/me")
	public ResponseEntity<?> me(Principal principal) {
		if (principal == null) {
			return ResponseEntity.ok(Map.of("authenticated", false));
		}
		String username = principal.getName();
		List<String> roles = userAdminService.rolesForUsername(username);
		return ResponseEntity.ok(Map.of("authenticated", true, "username", username, "roles", roles));
	}
}
