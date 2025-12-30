package com.example.kiosk.api;

import com.example.kiosk.api.dto.IngredientDto;
import com.example.kiosk.api.dto.IngredientRequest;
import com.example.kiosk.domain.Ingredient;
import com.example.kiosk.service.IngredientService;
import com.example.kiosk.ws.UiUpdatePublisher;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ideas/{ideaId}/ingredients")
public class IngredientController {

  private final IngredientService service;
  private final UiUpdatePublisher publisher;

  public IngredientController(IngredientService service, UiUpdatePublisher publisher) {
    this.service = service;
    this.publisher = publisher;
  }

  private Long profileId() { return 1L; }

  @GetMapping
  public List<IngredientDto> list(@PathVariable Long ideaId) {
    return service.listForIdea(profileId(), ideaId).stream()
      .map(this::toDto)
      .collect(Collectors.toList());
  }

  @PostMapping
  public IngredientDto create(@PathVariable Long ideaId, @Valid @RequestBody IngredientRequest payload) {
    Ingredient ent = fromRequest(payload);
    Ingredient created = service.create(profileId(), ideaId, ent);
    publisher.publishRefresh("IDEA_UPDATED");
    return toDto(created);
  }

  @PutMapping("/{id}")
  public IngredientDto update(@PathVariable Long ideaId, @PathVariable Long id, @Valid @RequestBody IngredientRequest patch) {
    Ingredient ent = fromRequest(patch);
    Ingredient updated = service.update(profileId(), ideaId, id, ent);
    publisher.publishRefresh("IDEA_UPDATED");
    return toDto(updated);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long ideaId, @PathVariable Long id) {
    service.delete(profileId(), ideaId, id);
    publisher.publishRefresh("IDEA_UPDATED");
  }

  private IngredientDto toDto(Ingredient i){
    if (i == null) return null;
    return new IngredientDto(i.getId(), i.getAmount(), i.getUnit(), i.getDescription());
  }

  private Ingredient fromRequest(IngredientRequest r){
    Ingredient i = new Ingredient();
    i.setAmount(r.getAmount());
    i.setUnit(r.getUnit());
    i.setDescription(r.getDescription());
    return i;
  }
}
