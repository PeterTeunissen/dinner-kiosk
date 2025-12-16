package com.example.kiosk.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.kiosk.domain.AppUser;
import com.example.kiosk.repo.AppUserRepository;
import com.example.kiosk.repo.AppUserRoleRepository;

@Service
public class DbUserDetailsService implements UserDetailsService {

	private final AppUserRepository users;
	private final AppUserRoleRepository roles;

	public DbUserDetailsService(AppUserRepository users, AppUserRoleRepository roles) {
		this.users = users;
		this.roles = roles;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser u = users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

		List<SimpleGrantedAuthority> auths = roles.findByUserId(u.getId()).stream()
				.map(r -> new SimpleGrantedAuthority(r.getRole())).toList();

		return User.withUsername(u.getUsername()).password(u.getPasswordHash()).disabled(!u.isEnabled())
				.authorities(auths).build();
	}
}
