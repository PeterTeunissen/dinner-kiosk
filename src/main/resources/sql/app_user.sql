CREATE TABLE IF NOT EXISTS app_user (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(80) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  enabled bit NOT NULL DEFAULT 1,

  failed_attempts INT NOT NULL DEFAULT 0,
  locked_until DATETIME NULL,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Optional: seed an admin user by running BcryptGen
-- Generate a bcrypt hash first (see Java snippet below), then paste it here.
-- Example hash format: $2a$10$...
INSERT INTO app_user (username, password_hash, enabled)
VALUES ('admin', '$2a$10$REPLACE_WITH_REAL_BCRYPT_HASH', 1);
