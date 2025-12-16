package com.example.kiosk.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kiosk.domain.AppUserRole;

public interface AppUserRoleRepository extends JpaRepository<AppUserRole, AppUserRole.Key> {
	List<AppUserRole> findByUserId(Long userId);

	void deleteByUserId(Long userId);
}
