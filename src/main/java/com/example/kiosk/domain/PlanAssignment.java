package com.example.kiosk.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="plan_assignment", indexes = {
  @Index(name="idx_assign_profile_day", columnList="profile_id,day_of_week,assigned_at")
})
public class PlanAssignment {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="profile_id", nullable=false)
  private Long profileId = 1L;

  @Column(name="day_of_week", nullable=false)
  private Integer dayOfWeek;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="idea_id")
  private DinnerIdea idea;

  @Column(name="meal_title_snapshot", length=120)
  private String mealTitleSnapshot;

  @Column(name="assigned_at", insertable=false, updatable=false)
  private Instant assignedAt;

  public Long getId() { return id; }
  public Long getProfileId() { return profileId; }
  public void setProfileId(Long profileId) { this.profileId = profileId; }
  public Integer getDayOfWeek() { return dayOfWeek; }
  public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
  public DinnerIdea getIdea() { return idea; }
  public void setIdea(DinnerIdea idea) { this.idea = idea; }
  public String getMealTitleSnapshot() { return mealTitleSnapshot; }
  public void setMealTitleSnapshot(String mealTitleSnapshot) { this.mealTitleSnapshot = mealTitleSnapshot; }
}
