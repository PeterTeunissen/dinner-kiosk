package com.example.kiosk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
				// static files
				.requestMatchers("/", "/index.html", "/**/*.css", "/**/*.js", "/favicon.ico").permitAll()

				// websocket handshake
				.requestMatchers("/ws/**").permitAll()

				// auth endpoints (IMPORTANT: include /me)
				.requestMatchers("/api/auth/login", "/api/auth/logout", "/api/auth/me").permitAll()

				// everything else under /api requires login
				.requestMatchers("/api/**").authenticated()

				.anyRequest().permitAll()).formLogin(form -> form.disable()).httpBasic(basic -> basic.disable())
				.logout(logout -> logout.logoutUrl("/api/auth/logout"));

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
		return cfg.getAuthenticationManager();
	}
}
