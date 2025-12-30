package com.example.kiosk.repo;

import com.example.kiosk.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
  List<Ingredient> findByDinnerIdeaId(Long dinnerIdeaId);
}
