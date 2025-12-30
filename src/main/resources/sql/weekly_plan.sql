CREATE TABLE `weekly_plan` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `profile_id` bigint unsigned NOT NULL DEFAULT '1',
  `day_of_week` int unsigned NOT NULL,
  `idea_id` bigint unsigned DEFAULT NULL,
  `meal_title` varchar(120) DEFAULT NULL,
  `meal_details` tinytext,
  `notes` tinytext,
  `servings` int unsigned DEFAULT NULL,
  `prep_minutes` int unsigned DEFAULT NULL,
  `cook_minutes` int unsigned DEFAULT NULL,
  `tags_json` json DEFAULT NULL,
  `is_locked` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_weekly_plan_profile_day` (`profile_id`,`day_of_week`),
  KEY `idx_weekly_plan_idea` (`idea_id`),
  CONSTRAINT `fk_weekly_plan_idea` FOREIGN KEY (`idea_id`) REFERENCES `dinner_idea` (`id`) ON DELETE SET NULL,
  CONSTRAINT `chk_day_of_week` CHECK ((`day_of_week` between 1 and 7))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO weekly_plan (profile_id, day_of_week)
VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7)
ON DUPLICATE KEY UPDATE day_of_week = VALUES(day_of_week);
