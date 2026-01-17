CREATE TABLE IF NOT EXISTS plan_assignment (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  profile_id BIGINT UNSIGNED NOT NULL DEFAULT 1,

  day_of_week INT UNSIGNED NOT NULL,                -- 1..7
  idea_id BIGINT UNSIGNED NULL,                         -- nullable if user typed custom meal
  meal_title_snapshot VARCHAR(120) NULL,                -- store what was planned at that time

  assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  KEY idx_assign_profile_day (profile_id, day_of_week, assigned_at),
  CONSTRAINT fk_assignment_idea
    FOREIGN KEY (idea_id) REFERENCES dinner_idea(id)
    ON DELETE SET NULL,
  CONSTRAINT chk_assignment_day CHECK (day_of_week BETWEEN 1 AND 7)
) ENGINE=InnoDB;
