package com.example.kiosk.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public class IngredientRequest {

  @DecimalMin(value = "0.0", inclusive = true, message = "Amount must be >= 0")
  private Float amount;

  @Size(max = 80, message = "Unit too long")
  private String unit;

  @Size(max = 2000, message = "Description too long")
  private String description;

  public IngredientRequest() {}

  public Float getAmount() { return amount; }
  public void setAmount(Float amount) { this.amount = amount; }

  public String getUnit() { return unit; }
  public void setUnit(String unit) { this.unit = unit; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
