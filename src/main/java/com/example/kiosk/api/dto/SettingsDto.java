package com.example.kiosk.api.dto;

import java.math.BigDecimal;

public class SettingsDto {

	public String weekStart; // "SUN" or "MON"
	public DimDto dim;
	public boolean pinConfigured;

	public static class DimDto {
		public boolean enabled;
		public String start; // "HH:mm"
		public String end; // "HH:mm"
		public BigDecimal opacity; // 0.10..0.90
		public int inactivityMinutes; // 1..60
	}
}
