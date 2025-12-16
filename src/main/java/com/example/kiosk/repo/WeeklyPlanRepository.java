package com.example.kiosk.repo;

import com.example.kiosk.domain.WeeklyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface WeeklyPlanRepository extends JpaRepository<WeeklyPlan, Long> {
  List<WeeklyPlan> findByProfileIdOrderByDayOfWeekAsc(Long profileId);
  Optional<WeeklyPlan> findByProfileIdAndDayOfWeek(Long profileId, Integer dayOfWeek);
}
