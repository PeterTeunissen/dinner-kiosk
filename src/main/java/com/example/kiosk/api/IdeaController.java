package com.example.kiosk.api;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kiosk.domain.DinnerIdea;
import com.example.kiosk.service.IdeaService;
import com.example.kiosk.ws.UiUpdatePublisher;

@RestController
@RequestMapping("/api/ideas")
public class IdeaController {

	private final IdeaService ideaService;
	private final UiUpdatePublisher publisher;

	public IdeaController(IdeaService ideaService, UiUpdatePublisher publisher) {
		this.ideaService = ideaService;
		this.publisher = publisher;
	}

	private Long profileId() {
		return 1L;
	}

	@GetMapping
	public List<DinnerIdea> list(@RequestParam(defaultValue = "false") boolean archived) {
		return ideaService.list(profileId(), archived);
	}

	@PostMapping
	public DinnerIdea create(@RequestBody DinnerIdea idea) {
		DinnerIdea created = ideaService.create(profileId(), idea);
		publisher.publishRefresh("IDEA_CREATED");
		return created;
	}

	@PutMapping("/{id}")
	public DinnerIdea update(@PathVariable Long id, @RequestBody DinnerIdea patch) {
		DinnerIdea updated = ideaService.update(profileId(), id, patch);
		publisher.publishRefresh("IDEA_UPDATED");
		return updated;
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		ideaService.delete(profileId(), id);
		publisher.publishRefresh("IDEA_DELETED");
	}
}
