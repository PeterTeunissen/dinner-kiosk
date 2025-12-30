package com.example.kiosk.api.dto;

public class IngredientDto {
  private Long id;
  private Float amount;
  private String unit;
  private String description;

  public IngredientDto() {}

  public IngredientDto(Long id, Float amount, String unit, String description) {
    this.id = id;
    this.amount = amount;
    this.unit = unit;
    this.description = description;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Float getAmount() { return amount; }
  public void setAmount(Float amount) { this.amount = amount; }

  public String getUnit() { return unit; }
  public void setUnit(String unit) { this.unit = unit; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
