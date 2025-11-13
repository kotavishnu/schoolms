-- ===========================================================================
-- Migration: V6__Create_users_table.sql
-- Description: Creates users table with role-based access control
-- Author: School Management System Development Team
-- Date: 2025-11-11
-- ===========================================================================

-- Create users table
-- This table stores system users with different roles for access control


-- Create indexes for performance
--CREATE INDEX idx_users_username ON users(username);
--CREATE INDEX idx_users_email ON users(email) WHERE email IS NOT NULL;
--CREATE INDEX idx_users_role ON users(role);
--CREATE INDEX idx_users_is_active ON users(is_active);
--CREATE INDEX idx_users_role_is_active ON users(role, is_active);

-- Create functional index for case-insensitive email search
CREATE INDEX idx_users_email_lower ON users(LOWER(email)) WHERE email IS NOT NULL;

-- Add comments for documentation
COMMENT ON TABLE users IS 'System users with role-based access control';
COMMENT ON COLUMN users.user_id IS 'Primary key - auto-generated';
COMMENT ON COLUMN users.username IS 'Unique username for authentication (case-sensitive)';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password with salt >= 12';
COMMENT ON COLUMN users.full_name IS 'Full name of the user for display';
COMMENT ON COLUMN users.email IS 'Email address (unique, optional)';
COMMENT ON COLUMN users.mobile IS 'Mobile phone number (optional)';
COMMENT ON COLUMN users.role IS 'User role: ADMIN, PRINCIPAL, OFFICE_STAFF, ACCOUNTS_MANAGER, AUDITOR';
COMMENT ON COLUMN users.is_active IS 'Account activation status (inactive users cannot authenticate)';
COMMENT ON COLUMN users.last_login_at IS 'Timestamp of last successful login';
COMMENT ON COLUMN users.password_changed_at IS 'Timestamp of last password change';
COMMENT ON COLUMN users.created_at IS 'Timestamp when record was created';
COMMENT ON COLUMN users.created_by IS 'User ID who created this record';
COMMENT ON COLUMN users.updated_at IS 'Timestamp when record was last updated';
COMMENT ON COLUMN users.updated_by IS 'User ID who last updated this record';

-- ===========================================================================
-- Rollback Strategy:
-- To rollback this migration, execute:
-- DROP TABLE IF EXISTS users CASCADE;
-- ===========================================================================
