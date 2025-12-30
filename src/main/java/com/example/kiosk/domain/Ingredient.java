package com.example.kiosk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "ingredient", indexes = {
  @Index(name = "idx_ing_idea", columnList = "dinner_idea_id")
})
public class Ingredient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dinner_idea_id", nullable = false)
  @JsonBackReference
  private DinnerIdea dinnerIdea;

  @Column(name = "amount")
  private Float amount;

  @Column(name = "unit", length = 80)
  private String unit;

  @Column(name = "description")
  private String description;

  public Ingredient() {}

  public Long getId() { return id; }

  public DinnerIdea getDinnerIdea() { return dinnerIdea; }
  public void setDinnerIdea(DinnerIdea dinnerIdea) { this.dinnerIdea = dinnerIdea; }

  public Float getAmount() { return amount; }
  public void setAmount(Float amount) { this.amount = amount; }

  public String getUnit() { return unit; }
  public void setUnit(String unit) { this.unit = unit; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
