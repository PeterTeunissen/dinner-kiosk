package com.example.kiosk.service;

import com.example.kiosk.domain.DinnerIdea;
import com.example.kiosk.domain.Ingredient;
import com.example.kiosk.repo.DinnerIdeaRepository;
import com.example.kiosk.repo.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngredientService {
  private final IngredientRepository repo;
  private final DinnerIdeaRepository ideaRepo;

  public IngredientService(IngredientRepository repo, DinnerIdeaRepository ideaRepo) {
    this.repo = repo;
    this.ideaRepo = ideaRepo;
  }

  public List<Ingredient> listForIdea(Long profileId, Long ideaId) {
    DinnerIdea idea = ideaRepo.findById(ideaId)
      .filter(i -> i.getProfileId().equals(profileId))
      .orElseThrow(() -> new IllegalArgumentException("Idea not found."));
    return repo.findByDinnerIdeaId(ideaId);
  }

  @Transactional
  public Ingredient create(Long profileId, Long ideaId, Ingredient payload) {
    DinnerIdea idea = ideaRepo.findById(ideaId)
      .filter(i -> i.getProfileId().equals(profileId))
      .orElseThrow(() -> new IllegalArgumentException("Idea not found."));

    payload.setDinnerIdea(idea);
    Ingredient saved = repo.save(payload);
    return saved;
  }

  @Transactional
  public Ingredient update(Long profileId, Long ideaId, Long ingredientId, Ingredient patch) {
    DinnerIdea idea = ideaRepo.findById(ideaId)
      .filter(i -> i.getProfileId().equals(profileId))
      .orElseThrow(() -> new IllegalArgumentException("Idea not found."));

    Ingredient ing = repo.findById(ingredientId)
      .filter(i -> i.getDinnerIdea() != null && i.getDinnerIdea().getId().equals(ideaId))
      .orElseThrow(() -> new IllegalArgumentException("Ingredient not found."));

    if (patch.getAmount() != null) ing.setAmount(patch.getAmount());
    if (patch.getUnit() != null) ing.setUnit(patch.getUnit());
    if (patch.getDescription() != null) ing.setDescription(patch.getDescription());

    return repo.save(ing);
  }

  @Transactional
  public void delete(Long profileId, Long ideaId, Long ingredientId) {
    DinnerIdea idea = ideaRepo.findById(ideaId)
      .filter(i -> i.getProfileId().equals(profileId))
      .orElseThrow(() -> new IllegalArgumentException("Idea not found."));

    Ingredient ing = repo.findById(ingredientId)
      .filter(i -> i.getDinnerIdea() != null && i.getDinnerIdea().getId().equals(ideaId))
      .orElseThrow(() -> new IllegalArgumentException("Ingredient not found."));

    repo.delete(ing);
  }
}
