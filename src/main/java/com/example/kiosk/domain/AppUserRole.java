package com.example.kiosk.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user_role")
@IdClass(AppUserRole.Key.class)
public class AppUserRole {

	@Id
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Id
	@Column(nullable = false, length = 40)
	private String role; // e.g. ROLE_USER / ROLE_ADMIN

	public AppUserRole() {
	}

	public AppUserRole(Long userId, String role) {
		this.userId = userId;
		this.role = role;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public static class Key implements Serializable {
		public Long userId;
		public String role;

		public Key() {
		}

		public Key(Long userId, String role) {
			this.userId = userId;
			this.role = role;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof Key k))
				return false;
			return Objects.equals(userId, k.userId) && Objects.equals(role, k.role);
		}

		@Override
		public int hashCode() {
			return Objects.hash(userId, role);
		}
	}
}
