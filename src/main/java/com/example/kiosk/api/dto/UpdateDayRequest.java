package com.example.kiosk.api.dto;

import jakarta.validation.constraints.*;

public class UpdateDayRequest {
  @NotNull @Min(1) @Max(7)
  public Integer dayOfWeek;

  public String mealTitle;
  public String mealDetails;
  public String notes;

  public Integer servings;
  public Integer prepMinutes;
  public Integer cookMinutes;

  // example: ["quick","crockpot"]
  public String tagsJson;
}
