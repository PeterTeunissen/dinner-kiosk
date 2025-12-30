package com.example.kiosk.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="dinner_idea", indexes = {
  @Index(name="idx_idea_profile_archived", columnList="profile_id,is_archived"),
  @Index(name="idx_idea_title", columnList="title")
})
public class DinnerIdea {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="profile_id", nullable=false)
  private Long profileId = 1L;

  @Column(name="title", nullable=false, length=120)
  private String title;

  @Lob
  @Column(name="description")
  private String description;

  @Column(name="tags_json", columnDefinition="json")
  private String tagsJson;

  @Column(name="default_servings")
  private Integer defaultServings;

  @Column(name="source_url", length=500)
  private String sourceUrl;

  @Column(name="is_archived", nullable=false)
  private boolean archived = false;

  @Column(name="created_at", updatable=false, insertable=false)
  private Instant createdAt;

  @Column(name="updated_at", insertable=false, updatable=false)
  private Instant updatedAt;

  @OneToMany(mappedBy = "dinnerIdea", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private List<Ingredient> ingredients = new ArrayList<>();

  // getters/setters
  public Long getId() { return id; }
  public Long getProfileId() { return profileId; }
  public void setProfileId(Long profileId) { this.profileId = profileId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getTagsJson() { return tagsJson; }
  public void setTagsJson(String tagsJson) { this.tagsJson = tagsJson; }
  public Integer getDefaultServings() { return defaultServings; }
  public void setDefaultServings(Integer defaultServings) { this.defaultServings = defaultServings; }
  public String getSourceUrl() { return sourceUrl; }
  public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
  public boolean isArchived() { return archived; }
  public void setArchived(boolean archived) { this.archived = archived; }

  public List<Ingredient> getIngredients() { return ingredients; }
  public void setIngredients(List<Ingredient> ingredients) {
    this.ingredients.clear();
    if (ingredients != null) {
      ingredients.forEach(this::addIngredient);
    }
  }

  public void addIngredient(Ingredient ing) {
    ing.setDinnerIdea(this);
    this.ingredients.add(ing);
  }

  public void removeIngredient(Ingredient ing) {
    ing.setDinnerIdea(null);
    this.ingredients.remove(ing);
  }
}
