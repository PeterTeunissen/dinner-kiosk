package com.example.kiosk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kiosk.domain.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
	Optional<AppUser> findByUsername(String username);
}
