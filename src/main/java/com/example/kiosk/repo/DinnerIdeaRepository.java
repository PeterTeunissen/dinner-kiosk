package com.example.kiosk.repo;

import com.example.kiosk.domain.DinnerIdea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface DinnerIdeaRepository extends JpaRepository<DinnerIdea, Long> {
  List<DinnerIdea> findByProfileIdAndArchivedOrderByUpdatedAtDesc(Long profileId, boolean archived);
}
