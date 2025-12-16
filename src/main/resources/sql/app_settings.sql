
-- app_settings: single-row settings table
CREATE TABLE IF NOT EXISTS app_settings (
  id BIGINT NOT NULL PRIMARY KEY,
  week_start ENUM('SUN','MON') NOT NULL DEFAULT 'SUN',

  dim_enabled bit NOT NULL DEFAULT 1,
  dim_start TIME NOT NULL DEFAULT '22:30:00',
  dim_end TIME NOT NULL DEFAULT '07:30:00',
  dim_opacity DECIMAL(3,2) NOT NULL DEFAULT 0.55,
  dim_inactivity_minutes INT NOT NULL DEFAULT 5,

  pin_hash VARCHAR(255) NULL,

  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  version BIGINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Ensure the singleton row exists
INSERT INTO app_settings (id)
VALUES (1)
ON DUPLICATE KEY UPDATE id=id;



