package com.example.kiosk.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
  name = "weekly_plan",
  uniqueConstraints = @UniqueConstraint(name="uq_weekly_plan_profile_day", columnNames={"profile_id","day_of_week"})
)
public class WeeklyPlan {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="profile_id", nullable=false)
  private Long profileId = 1L;

  @Column(name="day_of_week", nullable=false)
  private Integer dayOfWeek; // 1..7

  @Column(name="meal_title")
  private String mealTitle;

  @Lob
  @Column(name="meal_details")
  private String mealDetails;

  @Lob
  @Column(name="notes")
  private String notes;

  @Column(name="servings")
  private Integer servings;

  @Column(name="prep_minutes")
  private Integer prepMinutes;

  @Column(name="cook_minutes")
  private Integer cookMinutes;

  // store JSON as String (simple + portable)
  @Column(name="tags_json", columnDefinition="json")
  private String tagsJson;

  @Column(name="is_locked", nullable=false)
  private boolean locked = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "idea_id")
  private DinnerIdea idea;

  @Column(name="created_at", updatable=false, insertable=false)
  private Instant createdAt;

  @Column(name="updated_at", insertable=false, updatable=false)
  private Instant updatedAt;

  // getters/setters omitted for brevity — generate in IDE

  public Long getId() { return id; }
  public Long getProfileId() { return profileId; }
  public void setProfileId(Long profileId) { this.profileId = profileId; }
  public Integer getDayOfWeek() { return dayOfWeek; }
  public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
  public String getMealTitle() { return mealTitle; }
  public void setMealTitle(String mealTitle) { this.mealTitle = mealTitle; }
  public String getMealDetails() { return mealDetails; }
  public void setMealDetails(String mealDetails) { this.mealDetails = mealDetails; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
  public Integer getServings() { return servings; }
  public void setServings(Integer servings) { this.servings = servings; }
  public Integer getPrepMinutes() { return prepMinutes; }
  public void setPrepMinutes(Integer prepMinutes) { this.prepMinutes = prepMinutes; }
  public Integer getCookMinutes() { return cookMinutes; }
  public void setCookMinutes(Integer cookMinutes) { this.cookMinutes = cookMinutes; }
  public String getTagsJson() { return tagsJson; }
  public void setTagsJson(String tagsJson) { this.tagsJson = tagsJson; }
  public boolean isLocked() { return locked; }
  public void setLocked(boolean locked) { this.locked = locked; }
  public DinnerIdea getIdea() { return idea; }
  public void setIdea(DinnerIdea idea) { this.idea = idea; }
}
