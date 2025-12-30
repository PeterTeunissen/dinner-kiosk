package com.example.kiosk.service;

import com.example.kiosk.api.dto.AssignIdeaRequest;
import com.example.kiosk.api.dto.UpdateDayRequest;
import com.example.kiosk.domain.*;
import com.example.kiosk.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlanService {

  private final WeeklyPlanRepository weeklyPlanRepo;
  private final DinnerIdeaRepository ideaRepo;
  private final PlanAssignmentRepository assignmentRepo;

  public PlanService(WeeklyPlanRepository weeklyPlanRepo,
                     DinnerIdeaRepository ideaRepo,
                     PlanAssignmentRepository assignmentRepo) {
    this.weeklyPlanRepo = weeklyPlanRepo;
    this.ideaRepo = ideaRepo;
    this.assignmentRepo = assignmentRepo;
  }

  @Transactional(readOnly = true)
  public List<WeeklyPlan> getWeek(Long profileId) {
    List<WeeklyPlan> list = weeklyPlanRepo.findByProfileIdOrderByDayOfWeekAsc(profileId);
    for (WeeklyPlan wp : list){
      // initialize idea proxy within transaction so JSON serialization won't fail later
      if (wp.getIdea() != null){
        DinnerIdea idea = wp.getIdea();
        // touch common fields and ingredients collection so Jackson can serialize cleanly
        idea.getId();
        idea.getTitle();
        idea.getDescription();
        if (idea.getIngredients() != null) idea.getIngredients().size();
      }
    }
    return list;
  }

  @Transactional
  public WeeklyPlan updateDay(Long profileId, UpdateDayRequest req) {
    WeeklyPlan day = weeklyPlanRepo.findByProfileIdAndDayOfWeek(profileId, req.dayOfWeek)
      .orElseThrow(() -> new IllegalArgumentException("Day not found. Seed weekly_plan for 1..7 first."));

    if (day.isLocked()) throw new IllegalStateException("Day is locked.");

    day.setMealTitle(req.mealTitle);
    day.setMealDetails(req.mealDetails);
    day.setNotes(req.notes);
    day.setServings(req.servings);
    day.setPrepMinutes(req.prepMinutes);
    day.setCookMinutes(req.cookMinutes);
    day.setTagsJson(req.tagsJson);

    day.setIdea(null);

    return weeklyPlanRepo.save(day);
  }

  @Transactional
  public WeeklyPlan clearDay(Long profileId, int dayOfWeek) {
    WeeklyPlan day = weeklyPlanRepo.findByProfileIdAndDayOfWeek(profileId, dayOfWeek)
      .orElseThrow(() -> new IllegalArgumentException("Day not found."));
    if (day.isLocked()) throw new IllegalStateException("Day is locked.");

    day.setMealTitle(null);
    day.setMealDetails(null);
    day.setNotes(null);
    day.setServings(null);
    day.setPrepMinutes(null);
    day.setCookMinutes(null);
    day.setTagsJson(null);
    day.setIdea(null);

    return weeklyPlanRepo.save(day);
  }

  @Transactional
  public WeeklyPlan assignIdeaToDay(Long profileId, AssignIdeaRequest req) {
    WeeklyPlan day = weeklyPlanRepo.findByProfileIdAndDayOfWeek(profileId, req.dayOfWeek)
      .orElseThrow(() -> new IllegalArgumentException("Day not found."));
    if (day.isLocked()) throw new IllegalStateException("Day is locked.");

    DinnerIdea idea = ideaRepo.findById(req.ideaId)
      .filter(i -> i.getProfileId().equals(profileId))
      .orElseThrow(() -> new IllegalArgumentException("Idea not found."));

    // apply idea -> day
    day.setMealTitle(idea.getTitle());
    day.setMealDetails(idea.getDescription());
    day.setServings(idea.getDefaultServings());
    day.setTagsJson(idea.getTagsJson());
    day.setIdea(idea);

    WeeklyPlan saved = weeklyPlanRepo.save(day);

    // history record (optional)
    PlanAssignment a = new PlanAssignment();
    a.setProfileId(profileId);
    a.setDayOfWeek(req.dayOfWeek);
    a.setIdea(idea);
    a.setMealTitleSnapshot(idea.getTitle());
    assignmentRepo.save(a);

    // optionally archive the idea after use
    if (Boolean.TRUE.equals(req.archiveIdea)) {
      idea.setArchived(true);
      ideaRepo.save(idea);
    }

    // initialize idea fields/collections within transaction so controller's JSON
    // serialization does not trigger LazyInitializationException
    if (saved.getIdea() != null){
      DinnerIdea si = saved.getIdea();
      si.getId();
      si.getTitle();
      si.getDescription();
      if (si.getIngredients() != null) si.getIngredients().size();
    }

    return saved;
  }
}
