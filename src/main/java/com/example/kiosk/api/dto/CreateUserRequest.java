package com.example.kiosk.api.dto;

import java.util.List;

public class CreateUserRequest {
	public String username;
	public String password;
	public List<String> roles; // ["ROLE_USER"] or ["ROLE_ADMIN","ROLE_USER"]
	public Boolean enabled;
}
