package com.example.kiosk.service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kiosk.api.dto.SaveSettingsRequest;
import com.example.kiosk.api.dto.SettingsDto;
import com.example.kiosk.domain.AppSettings;
import com.example.kiosk.repo.AppSettingsRepository;

@Service
public class AppSettingsService {

	private static final long SETTINGS_ID = 1L;
	private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");

	private final AppSettingsRepository repo;
	private final PasswordEncoder encoder;
	private final SimpMessagingTemplate ws;

	public AppSettingsService(AppSettingsRepository repo, PasswordEncoder encoder, SimpMessagingTemplate ws) {
		this.repo = repo;
		this.encoder = encoder;
		this.ws = ws;
	}

	@Transactional(readOnly = true)
	public SettingsDto getSettings() {
		AppSettings s = getOrCreate();
		return toDto(s);
	}

	@Transactional
	public SettingsDto saveSettings(SaveSettingsRequest req) {
		AppSettings s = getOrCreate();

		// weekStart
		if (req.weekStart == null || (!req.weekStart.equals("SUN") && !req.weekStart.equals("MON"))) {
			throw new IllegalArgumentException("weekStart must be SUN or MON");
		}
		s.setWeekStart(AppSettings.WeekStart.valueOf(req.weekStart));

		// dim
		if (req.dim == null)
			throw new IllegalArgumentException("dim is required");
		s.setDimEnabled(req.dim.enabled);

		LocalTime start = parseHHMM(req.dim.start, "dim.start");
		LocalTime end = parseHHMM(req.dim.end, "dim.end");
		s.setDimStart(start);
		s.setDimEnd(end);

		BigDecimal opacity = req.dim.opacity;
		if (opacity == null)
			throw new IllegalArgumentException("dim.opacity is required");
		if (opacity.compareTo(new BigDecimal("0.10")) < 0 || opacity.compareTo(new BigDecimal("0.90")) > 0) {
			throw new IllegalArgumentException("dim.opacity must be between 0.10 and 0.90");
		}
		s.setDimOpacity(opacity);

		int idle = req.dim.inactivityMinutes;
		if (idle < 1 || idle > 60)
			throw new IllegalArgumentException("dim.inactivityMinutes must be 1..60");
		s.setDimInactivityMinutes(idle);

		// optional PIN change
		if (req.pin != null && !req.pin.isBlank()) {
			String pin = req.pin.trim();
			if (!pin.matches("^\\d{4}$"))
				throw new IllegalArgumentException("pin must be 4 digits");
			s.setPinHash(encoder.encode(pin));
		}

		repo.save(s);

		// Broadcast to all clients (same topic your UI uses)
		ws.convertAndSend("/topic/ui",
				(Map<String, String>) Map.of("type", "settings", "ts", String.valueOf(System.currentTimeMillis())));

		return toDto(s);
	}

	@Transactional(readOnly = true)
	public boolean verifyPin(String pin) {
		AppSettings s = getOrCreate();

		// If no PIN configured, allow (or return false if you prefer)
		if (s.getPinHash() == null || s.getPinHash().isBlank()) {
			return true;
		}
		if (pin == null)
			return false;
		String p = pin.trim();
		if (!p.matches("^\\d{4}$"))
			return false;

		return encoder.matches(p, s.getPinHash());
	}

	private AppSettings getOrCreate() {
		return repo.findById(SETTINGS_ID).orElseGet(() -> {
			AppSettings s = new AppSettings();
			s.setId(SETTINGS_ID);
			return repo.save(s);
		});
	}

	private SettingsDto toDto(AppSettings s) {
		SettingsDto dto = new SettingsDto();
		dto.weekStart = s.getWeekStart().name();

		SettingsDto.DimDto dim = new SettingsDto.DimDto();
		dim.enabled = s.isDimEnabled();
		dim.start = s.getDimStart().format(HHMM);
		dim.end = s.getDimEnd().format(HHMM);
		dim.opacity = s.getDimOpacity();
		dim.inactivityMinutes = s.getDimInactivityMinutes();

		dto.dim = dim;
		dto.pinConfigured = (s.getPinHash() != null && !s.getPinHash().isBlank());
		return dto;
	}

	private LocalTime parseHHMM(String s, String field) {
		if (s == null || s.isBlank())
			throw new IllegalArgumentException(field + " is required");
		try {
			return LocalTime.parse(s.trim(), HHMM);
		} catch (Exception e) {
			throw new IllegalArgumentException(field + " must be HH:mm");
		}
	}
}
