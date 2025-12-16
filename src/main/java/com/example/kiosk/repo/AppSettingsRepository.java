package com.example.kiosk.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kiosk.domain.AppSettings;

public interface AppSettingsRepository extends JpaRepository<AppSettings, Long> {
}
