package com.example.kiosk.api;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kiosk.api.dto.AssignIdeaRequest;
import com.example.kiosk.api.dto.UpdateDayRequest;
import com.example.kiosk.domain.WeeklyPlan;
import com.example.kiosk.service.PlanService;
import com.example.kiosk.ws.UiUpdatePublisher;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PlanController {

	private final PlanService planService;
	private final UiUpdatePublisher publisher;

	public PlanController(PlanService planService, UiUpdatePublisher publisher) {
		this.planService = planService;
		this.publisher = publisher;
	}

	// For kiosk, you can keep profileId fixed (1).
	private Long profileId() {
		return 1L;
	}

	@GetMapping("/week")
	public List<WeeklyPlan> getWeek() {
		return planService.getWeek(profileId());
	}

	@PutMapping("/day")
	public WeeklyPlan updateDay(@Valid @RequestBody UpdateDayRequest req) {
		WeeklyPlan wp = planService.updateDay(profileId(), req);
		publisher.publishRefresh("DAY_UPDATED");
		return wp;
	}

	@DeleteMapping("/day/{dow}")
	public WeeklyPlan clearDay(@PathVariable("dow") int dayOfWeek) {
		WeeklyPlan wp = planService.clearDay(profileId(), dayOfWeek);
		publisher.publishRefresh("DAY_CLEARED");
		return wp;
	}

	// drag-drop idea -> day
	@PostMapping("/assign")
	public WeeklyPlan assignIdea(@Valid @RequestBody AssignIdeaRequest req) {
		WeeklyPlan wp = planService.assignIdeaToDay(profileId(), req);
		publisher.publishRefresh("IDEA_ASSIGNED");
		return wp;
	}
}
