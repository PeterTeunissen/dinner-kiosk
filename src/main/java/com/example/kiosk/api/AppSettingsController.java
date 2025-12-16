package com.example.kiosk.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kiosk.api.dto.SaveSettingsRequest;
import com.example.kiosk.api.dto.SettingsDto;
import com.example.kiosk.api.dto.VerifyPinRequest;
import com.example.kiosk.api.dto.VerifyPinResponse;
import com.example.kiosk.service.AppSettingsService;

@RestController
@RequestMapping("/api")
public class AppSettingsController {

	private final AppSettingsService service;

	public AppSettingsController(AppSettingsService service) {
		this.service = service;
	}

	@GetMapping("/settings")
	public SettingsDto getSettings() {
		return service.getSettings();
	}

	@PutMapping("/settings")
	public SettingsDto saveSettings(@RequestBody SaveSettingsRequest req) {
		return service.saveSettings(req);
	}

	@PostMapping("/settings/verify")
	public VerifyPinResponse verify(@RequestBody VerifyPinRequest req) {
		boolean ok = service.verifyPin(req.pin);
		return new VerifyPinResponse(ok);
	}
}
