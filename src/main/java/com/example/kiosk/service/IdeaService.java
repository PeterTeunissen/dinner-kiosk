package com.example.kiosk.service;

import com.example.kiosk.domain.DinnerIdea;
import com.example.kiosk.repo.DinnerIdeaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IdeaService {
  private final DinnerIdeaRepository repo;

  public IdeaService(DinnerIdeaRepository repo) {
    this.repo = repo;
  }

  @Transactional(readOnly = true)
  public List<DinnerIdea> list(Long profileId, boolean archived) {
    List<DinnerIdea> ideas = repo.findByProfileIdAndArchivedOrderByUpdatedAtDesc(profileId, archived);
    // Ensure ingredients collection is loaded while within transactional context to avoid
    // LazyInitializationException during JSON serialization in the controller.
    ideas.forEach(i -> {
      if (i.getIngredients() != null) {
        i.getIngredients().size();
      }
    });
    return ideas;
  }

  @Transactional
  public DinnerIdea create(Long profileId, DinnerIdea idea) {
    idea.setProfileId(profileId);
    if (idea.getIngredients() != null) {
      idea.getIngredients().forEach(ing -> ing.setDinnerIdea(idea));
    }
    return repo.save(idea);
  }

  @Transactional
  public DinnerIdea update(Long profileId, Long id, DinnerIdea patch) {
    DinnerIdea idea = repo.findById(id)
      .filter(i -> i.getProfileId().equals(profileId))
      .orElseThrow(() -> new IllegalArgumentException("Idea not found."));

    if (patch.getTitle() != null) idea.setTitle(patch.getTitle());
    idea.setDescription(patch.getDescription());
    idea.setTagsJson(patch.getTagsJson());
    idea.setDefaultServings(patch.getDefaultServings());
    idea.setSourceUrl(patch.getSourceUrl());
    idea.setArchived(patch.isArchived());

    return repo.save(idea);
  }

  @Transactional
  public void delete(Long profileId, Long id) {
    DinnerIdea idea = repo.findById(id)
      .filter(i -> i.getProfileId().equals(profileId))
      .orElseThrow(() -> new IllegalArgumentException("Idea not found."));
    repo.delete(idea);
  }
}
