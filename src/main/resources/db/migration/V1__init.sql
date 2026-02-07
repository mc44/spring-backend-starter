-- ===============================
-- Authorities
-- ===============================
INSERT INTO authorities (id, name)
VALUES
    (UUID(), 'USER_CREATE'),
    (UUID(), 'USER_READ'),
    (UUID(), 'USER_UPDATE'),
    (UUID(), 'USER_DELETE'),
    (UUID(), 'ROLE_CREATE'),
    (UUID(), 'ROLE_ASSIGN'),
    (UUID(), 'ROLE_UPDATE')
ON DUPLICATE KEY UPDATE name = name; -- does nothing if already exists

-- ===============================
-- Roles
-- ===============================
INSERT INTO role (id, name)
VALUES
    (UUID(), 'ADMIN'),
    (UUID(), 'USER')
ON DUPLICATE KEY UPDATE name = name;

-- ===============================
-- Assign all authorities to ADMIN
-- ===============================
INSERT INTO role_authorities (role_id, authority_id)
SELECT r.id, a.id
FROM role r
CROSS JOIN authorities a
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1
      FROM role_authorities ra
      WHERE ra.role_id = r.id AND ra.authority_id = a.id
  );

-- ===============================
-- Initial admin user
-- password: admin123 (BCrypt!)
-- ===============================
INSERT INTO user (id, username, password, email)
SELECT UUID(), 'admin', '$2a$10$fInuwZrHLQ1rRq1xm.U56eUmcOtg0aIXUXcmhXgi8hB2bPX8OC0Bm', 'admin@local'
WHERE NOT EXISTS (
    SELECT 1 FROM user WHERE username = 'admin'
);

-- ===============================
-- Assign ADMIN role to admin user
-- ===============================
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM user u
JOIN role r ON r.name = 'ADMIN'
WHERE u.username = 'admin'
  AND NOT EXISTS (
      SELECT 1
      FROM user_roles ur
      WHERE ur.user_id = u.id AND ur.role_id = r.id
  );
