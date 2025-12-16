package com.example.kiosk.api.dto;

import java.time.Instant;
import java.util.List;

public class UserDto {
	public Long id;
	public String username;
	public Boolean enabled;
	public List<String> roles;
	public Integer failedAttempts;
	public Instant lockedUntil;
}
