-- Insert Roles
INSERT INTO auth_roles (name) VALUES ('ROLE_USER');
INSERT INTO auth_roles (name) VALUES ('ROLE_ADMIN');

-- Insert default Admin user (password: admin123)
INSERT INTO auth_users (username, password, email) VALUES (
    'admin',
    '$2a$10$8.Nf49e.aE/vP9n81uX.uexQYp2wT43Vb5zI14R1.yU6tS9fQkF4G',
    'admin@test_microservices.com'
);

-- Insert default User user (password: user123)
INSERT INTO auth_users (username, password, email) VALUES (
    'user',
    '$2a$10$Uu2n25kS91oM1Lcr4k3M5.aD88mYgXzH4u8P/r1i9Z/b4sO0x6F4G',
    'user@test_microservices.com'
);

-- Assign Roles
INSERT INTO auth_user_roles (user_id, role_id) VALUES (1, 2); -- admin -> ROLE_ADMIN
INSERT INTO auth_user_roles (user_id, role_id) VALUES (2, 1); -- user -> ROLE_USER
