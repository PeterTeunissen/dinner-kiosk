CREATE TABLE IF NOT EXISTS app_user_role (
  user_id BIGINT NOT NULL,
  role VARCHAR(40) NOT NULL,
  PRIMARY KEY (user_id, role),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX  idx_user_role_role ON app_user_role(role);

INSERT INTO app_user_role (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM app_user WHERE username='admin'
ON DUPLICATE KEY UPDATE role=role;

INSERT INTO app_user_role (user_id, role)
SELECT id, 'ROLE_USER' FROM app_user WHERE username='admin'
ON DUPLICATE KEY UPDATE role=role;

