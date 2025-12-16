CREATE TABLE IF NOT EXISTS dinner_idea (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  profile_id BIGINT UNSIGNED NOT NULL DEFAULT 1,

  title VARCHAR(120) NOT NULL,
  description TEXT NULL,
  tags_json JSON NULL,                                  -- ["healthy","kid-friendly"]
  default_servings SMALLINT UNSIGNED NULL,
  source_url VARCHAR(500) NULL,

  is_archived TINYINT(1) NOT NULL DEFAULT 0,            -- hide without deleting

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  KEY idx_idea_profile_archived (profile_id, is_archived),
  KEY idx_idea_title (title)
) ENGINE=InnoDB;

