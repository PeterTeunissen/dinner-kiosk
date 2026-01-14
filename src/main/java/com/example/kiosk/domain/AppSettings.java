package com.example.kiosk.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "app_settings")
public class AppSettings {

	public enum WeekStart {
		SUN, MON
	}

	@Id
	private Long id = 1L;

	@Enumerated(EnumType.STRING)
	@Column(name = "week_start", nullable = false)
	private WeekStart weekStart = WeekStart.SUN;

	@Column(name = "dim_enabled", nullable = false)
	private boolean dimEnabled = true;

	@Column(name = "dim_start", nullable = false)
	private LocalTime dimStart = LocalTime.of(22, 30);

	@Column(name = "dim_end", nullable = false)
	private LocalTime dimEnd = LocalTime.of(7, 30);

	@Column(name = "dim_opacity", nullable = false, precision = 3, scale = 2)
	private BigDecimal dimOpacity = new BigDecimal("0.55");

	@Column(name = "dim_inactivity_minutes", nullable = false)
	private int dimInactivityMinutes = 5;

	@Column(name = "pin_hash")
	private String pinHash;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Version
	@Column(name = "version", nullable = false)
	private long version;

	// getters/setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public WeekStart getWeekStart() {
		return weekStart;
	}

	public void setWeekStart(WeekStart weekStart) {
		this.weekStart = weekStart;
	}

	public boolean isDimEnabled() {
		return dimEnabled;
	}

	public void setDimEnabled(boolean dimEnabled) {
		this.dimEnabled = dimEnabled;
	}

	public LocalTime getDimStart() {
		return dimStart;
	}

	public void setDimStart(LocalTime dimStart) {
		this.dimStart = dimStart;
	}

	public LocalTime getDimEnd() {
		return dimEnd;
	}

	public void setDimEnd(LocalTime dimEnd) {
		this.dimEnd = dimEnd;
	}

	public BigDecimal getDimOpacity() {
		return dimOpacity;
	}

	public void setDimOpacity(BigDecimal dimOpacity) {
		this.dimOpacity = dimOpacity;
	}

	public int getDimInactivityMinutes() {
		return dimInactivityMinutes;
	}

	public void setDimInactivityMinutes(int dimInactivityMinutes) {
		this.dimInactivityMinutes = dimInactivityMinutes;
	}

	public String getPinHash() {
		return pinHash;
	}

	public void setPinHash(String pinHash) {
		this.pinHash = pinHash;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
