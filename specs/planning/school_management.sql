-- ============================================================================
-- School Management System - Database Schema
-- PostgreSQL 18+
-- Generated: 2025-11-21
-- ============================================================================
-- Purpose: Complete DDL for Student and Configuration modules
-- Includes: Tables, Primary Keys, Foreign Keys, Basic Constraints only
-- Excludes: Indexes, Triggers, Stored Functions, Migration tools
-- ============================================================================

-- Clean up existing tables (for re-runs)
DROP TABLE IF EXISTS enrollment_history CASCADE;
DROP TABLE IF EXISTS school_configurations CASCADE;
DROP TABLE IF EXISTS students CASCADE;

-- ============================================================================
-- STUDENTS TABLE
-- ============================================================================
-- Purpose: Stores comprehensive student profile information with strict validation

CREATE TABLE students (
    -- Primary Key
    id                    BIGSERIAL PRIMARY KEY,

    -- Business Identifier (Auto-generated, Read-only)
    student_id            VARCHAR(20) NOT NULL UNIQUE,

    -- Personal Information (Required)
    first_name            VARCHAR(100) NOT NULL,
    last_name             VARCHAR(100) NOT NULL,
    address               TEXT NOT NULL,
    mobile                VARCHAR(15) NOT NULL UNIQUE,
    date_of_birth         DATE NOT NULL,

    -- Guardian Information
    father_name           VARCHAR(100) NOT NULL,  -- Guardian name
    mother_name           VARCHAR(100),

    -- Additional Information
    identification_mark   VARCHAR(200),
    email                 VARCHAR(100),
    aadhaar_number        VARCHAR(12),  -- Optional as per requirements

    -- Status and Lifecycle
    status                VARCHAR(20) NOT NULL DEFAULT 'Active',

    -- Audit Columns (Auto-managed)
    created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by            VARCHAR(100),
    updated_by            VARCHAR(100),

    -- Optimistic Locking
    version               INTEGER NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT chk_students_age_range CHECK (
        date_of_birth >= CURRENT_DATE - INTERVAL '18 years' AND
        date_of_birth <= CURRENT_DATE - INTERVAL '3 years'
    ),
    CONSTRAINT chk_students_status CHECK (
        status IN ('Active', 'Inactive', 'Graduated', 'Transferred')
    ),
    CONSTRAINT chk_students_mobile_format CHECK (
        mobile ~ '^\+?[0-9]{10,15}$'
    ),
    CONSTRAINT chk_students_email_format CHECK (
        email IS NULL OR email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
    ),
    CONSTRAINT chk_students_aadhaar_format CHECK (
        aadhaar_number IS NULL OR aadhaar_number ~ '^[0-9]{12}$'
    ),
    CONSTRAINT chk_students_student_id_format CHECK (
        student_id ~ '^STU-[0-9]{4}-[0-9]{5}$'
    )
);

-- Table comment
COMMENT ON TABLE students IS 'Stores student profile information with comprehensive validation';

-- Column comments
COMMENT ON COLUMN students.student_id IS 'System-generated identifier in format STU-YYYY-XXXXX';
COMMENT ON COLUMN students.mobile IS 'Unique mobile number, primary contact method';
COMMENT ON COLUMN students.date_of_birth IS 'Must satisfy age range 3-18 years at registration';
COMMENT ON COLUMN students.status IS 'Active (enrolled), Inactive (suspended), Graduated, Transferred';
COMMENT ON COLUMN students.aadhaar_number IS 'Optional 12-digit Indian identification number';
COMMENT ON COLUMN students.version IS 'Optimistic locking version for concurrent update handling';

-- ============================================================================
-- SCHOOL_CONFIGURATIONS TABLE
-- ============================================================================
-- Purpose: Stores flexible key-value configuration settings grouped by category

CREATE TABLE school_configurations (
    -- Primary Key
    id                    BIGSERIAL PRIMARY KEY,

    -- Configuration Grouping
    category              VARCHAR(50) NOT NULL,

    -- Configuration Key-Value
    config_key            VARCHAR(100) NOT NULL,
    config_value          TEXT NOT NULL,
    description           TEXT,

    -- Audit Columns
    created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by            VARCHAR(100),
    updated_by            VARCHAR(100),

    -- Optimistic Locking
    version               INTEGER NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT uq_config_category_key UNIQUE (category, config_key),
    CONSTRAINT chk_config_category CHECK (
        category IN ('General', 'Academic', 'Financial', 'System')
    )
);

-- Table comment
COMMENT ON TABLE school_configurations IS 'Stores school-wide configuration settings grouped by category';

-- Column comments
COMMENT ON COLUMN school_configurations.category IS 'Configuration grouping: General, Academic, Financial, System';
COMMENT ON COLUMN school_configurations.config_key IS 'Unique key within category';
COMMENT ON COLUMN school_configurations.config_value IS 'Configuration value (supports text, numbers, URLs, JSON)';

-- ============================================================================
-- ENROLLMENT_HISTORY TABLE (Future Enhancement)
-- ============================================================================
-- Purpose: Tracks student enrollment across academic years and classes
-- Note: This is designed for future use but included for database completeness

CREATE TABLE enrollment_history (
    -- Primary Key
    id                    BIGSERIAL PRIMARY KEY,

    -- Foreign Key to Students
    student_id            BIGINT NOT NULL,

    -- Enrollment Details
    academic_year         VARCHAR(10) NOT NULL,
    class_name            VARCHAR(50) NOT NULL,
    section               VARCHAR(10),
    enrollment_date       DATE NOT NULL,
    status                VARCHAR(20) NOT NULL DEFAULT 'Active',

    -- Audit Columns
    created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Optimistic Locking
    version               INTEGER NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id)
        REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT chk_enrollment_status CHECK (
        status IN ('Active', 'Transferred', 'Graduated', 'Withdrawn')
    ),
    CONSTRAINT uq_student_academic_year UNIQUE (student_id, academic_year)
);

-- Table comment
COMMENT ON TABLE enrollment_history IS 'Tracks student enrollment history across academic years (future use)';

-- ============================================================================
-- SEED DATA - Default School Configurations
-- ============================================================================
-- Purpose: Insert default configuration values for initial setup

INSERT INTO school_configurations (category, config_key, config_value, description, created_by) VALUES
('General', 'school_name', 'Demo School', 'Official school name displayed on reports', 'system'),
('General', 'school_code', 'DEMO-001', 'Unique school identifier code', 'system'),
('General', 'school_logo_url', 'https://cdn.example.com/logos/school.png', 'School logo URL for reports and UI', 'system'),
('Academic', 'academic_year', '2024-2025', 'Current academic year', 'system'),
('Academic', 'default_class_capacity', '40', 'Maximum students per class', 'system'),
('Financial', 'default_currency', 'INR', 'Default currency for fees and transactions', 'system'),
('System', 'date_format', 'DD-MM-YYYY', 'Display date format for UI', 'system'),
('System', 'timezone', 'Asia/Kolkata', 'School timezone for scheduling', 'system');

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================
-- Purpose: Verify schema creation and constraints

-- Verify tables exist
SELECT
    schemaname,
    tablename,
    tableowner
FROM pg_tables
WHERE schemaname = 'public'
AND tablename IN ('students', 'school_configurations', 'enrollment_history');

-- Verify constraints
SELECT
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type
FROM information_schema.table_constraints tc
WHERE tc.table_schema = 'public'
AND tc.table_name IN ('students', 'school_configurations', 'enrollment_history')
ORDER BY tc.table_name, tc.constraint_type;

-- Verify seed data
SELECT
    category,
    config_key,
    config_value
FROM school_configurations
ORDER BY category, config_key;

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================
-- This schema is ready for execution in PostgreSQL 18+
-- Execute with: psql -U postgres -d sms_db -f school_management.sql
-- ============================================================================
