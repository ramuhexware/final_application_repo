-- Drop existing tables to start clean
DROP TABLE IF EXISTS auth_user_roles CASCADE;
DROP TABLE IF EXISTS auth_roles CASCADE;
DROP TABLE IF EXISTS auth_users CASCADE;

-- Create auth_users table
CREATE TABLE auth_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- Create auth_roles table
CREATE TABLE auth_roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create auth_user_roles junction table
CREATE TABLE auth_user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES auth_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES auth_roles(id) ON DELETE CASCADE
);
