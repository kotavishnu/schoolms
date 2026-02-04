-- =====================================================
-- School Management System - Database Schema
-- Phase 1: Student and Configuration Modules
-- PostgreSQL 18+
-- =====================================================
-- Generated: 2026-02-03
-- Waterfall Execution: Single-Pass Implementation
-- =====================================================

-- Clean slate for re-runs
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS configurations CASCADE;

-- =====================================================
-- STUDENT SERVICE DATABASE (student_db)
-- =====================================================

-- -----------------------------------------------------
-- Table: students
-- Purpose: Core student profile storage
-- -----------------------------------------------------
CREATE TABLE students (
    -- Primary Key
    id                    BIGSERIAL PRIMARY KEY,

    -- Business Key (Auto-generated via trigger)
    student_id            VARCHAR(20) NOT NULL UNIQUE,

    -- Personal Information (Mandatory)
    first_name            VARCHAR(100) NOT NULL,
    last_name             VARCHAR(100) NOT NULL,
    date_of_birth         DATE NOT NULL,

    -- Contact Information
    mobile                VARCHAR(10) NOT NULL UNIQUE,
    email                 VARCHAR(255) UNIQUE,
    address               TEXT,

    -- Family Information
    fathers_name          VARCHAR(100),
    mothers_name          VARCHAR(100),

    -- Identification
    identification_mark   VARCHAR(200),
    aadhaar_number        VARCHAR(12) UNIQUE,

    -- Status Management
    status                VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Optimistic Locking
    version               BIGINT NOT NULL DEFAULT 0,

    -- Audit Columns
    created_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by            VARCHAR(100),
    updated_by            VARCHAR(100),

    -- Constraints
    CONSTRAINT check_first_name_format CHECK (first_name ~ '^[a-zA-Z\s]+$'),
    CONSTRAINT check_last_name_format CHECK (last_name ~ '^[a-zA-Z\s]+$'),
    CONSTRAINT check_student_age CHECK (
        EXTRACT(YEAR FROM AGE(CURRENT_DATE, date_of_birth)) BETWEEN 3 AND 18
    ),
    CONSTRAINT check_mobile_format CHECK (mobile ~ '^\d{10}$'),
    CONSTRAINT check_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$' OR email IS NULL),
    CONSTRAINT check_aadhaar_format CHECK (aadhaar_number ~ '^\d{12}$' OR aadhaar_number IS NULL),
    CONSTRAINT check_status_values CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- -----------------------------------------------------
-- Table: enrollments
-- Purpose: Track student enrollment history across academic years
-- -----------------------------------------------------
CREATE TABLE enrollments (
    -- Primary Key
    id                    BIGSERIAL PRIMARY KEY,

    -- Foreign Key
    student_id            BIGINT NOT NULL,

    -- Academic Details
    academic_year         VARCHAR(10) NOT NULL,
    grade_class           VARCHAR(20) NOT NULL,
    section               VARCHAR(10) NOT NULL,

    -- Enrollment Period
    enrollment_date       DATE NOT NULL,
    withdrawal_date       DATE,

    -- Status
    status                VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Additional Info
    remarks               TEXT,

    -- Audit
    created_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key Constraint
    CONSTRAINT fk_enrollment_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    -- Business Constraints
    CONSTRAINT uk_student_academic_year
        UNIQUE (student_id, academic_year),
    CONSTRAINT check_enrollment_dates
        CHECK (withdrawal_date IS NULL OR withdrawal_date >= enrollment_date),
    CONSTRAINT check_enrollment_status
        CHECK (status IN ('ACTIVE', 'WITHDRAWN', 'COMPLETED'))
);

-- =====================================================
-- CONFIGURATION SERVICE DATABASE (config_db)
-- =====================================================

-- -----------------------------------------------------
-- Table: configurations
-- Purpose: Store school-wide configuration settings
-- -----------------------------------------------------
CREATE TABLE configurations (
    -- Primary Key
    id                    BIGSERIAL PRIMARY KEY,

    -- Configuration Identity
    category              VARCHAR(50) NOT NULL,
    key                   VARCHAR(100) NOT NULL,

    -- Configuration Value
    value                 TEXT NOT NULL,
    description           TEXT,

    -- Metadata
    data_type             VARCHAR(20) NOT NULL,
    is_encrypted          BOOLEAN NOT NULL DEFAULT FALSE,

    -- Optimistic Locking
    version               BIGINT NOT NULL DEFAULT 0,

    -- Audit
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by            VARCHAR(100),

    -- Constraints
    CONSTRAINT uk_configuration_category_key
        UNIQUE (category, key),
    CONSTRAINT check_category_values
        CHECK (category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL')),
    CONSTRAINT check_data_type_values
        CHECK (data_type IN ('STRING', 'NUMBER', 'BOOLEAN', 'JSON'))
);

-- =====================================================
-- SAMPLE CONFIGURATION DATA (Seed Data)
-- =====================================================

-- General Settings
INSERT INTO configurations (category, key, value, data_type, description) VALUES
('GENERAL', 'school_name', 'Springfield Elementary', 'STRING', 'Official school name'),
('GENERAL', 'school_code', 'SPR-ELEM-001', 'STRING', 'Unique school identifier'),
('GENERAL', 'school_email', 'contact@springfield.edu', 'STRING', 'Primary contact email'),
('GENERAL', 'school_phone', '5551234567', 'STRING', 'Primary contact phone'),
('GENERAL', 'school_address', '742 Evergreen Terrace, Springfield', 'STRING', 'Physical address');

-- Academic Settings
INSERT INTO configurations (category, key, value, data_type, description) VALUES
('ACADEMIC', 'current_academic_year', '2025-2026', 'STRING', 'Current active academic year'),
('ACADEMIC', 'min_student_age', '3', 'NUMBER', 'Minimum enrollment age'),
('ACADEMIC', 'max_student_age', '18', 'NUMBER', 'Maximum enrollment age'),
('ACADEMIC', 'class_capacity', '30', 'NUMBER', 'Maximum students per class'),
('ACADEMIC', 'available_grades', '["Pre-K", "K", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5"]', 'JSON', 'List of grade levels'),
('ACADEMIC', 'sections', '["A", "B", "C"]', 'JSON', 'Available class sections');

-- Financial Settings
INSERT INTO configurations (category, key, value, data_type, description) VALUES
('FINANCIAL', 'currency', 'INR', 'STRING', 'Currency code (ISO 4217)'),
('FINANCIAL', 'registration_fee', '5000', 'NUMBER', 'One-time registration fee'),
('FINANCIAL', 'annual_tuition_fee', '50000', 'NUMBER', 'Annual tuition fee'),
('FINANCIAL', 'late_payment_penalty', '500', 'NUMBER', 'Late payment penalty amount');

-- =====================================================
-- COMMENTS (Documentation)
-- =====================================================

COMMENT ON TABLE students IS 'Core student profile information for School Management System';
COMMENT ON COLUMN students.student_id IS 'Auto-generated format: STD-YYYYMMDD-NNNN (e.g., STD-20260203-0001)';
COMMENT ON COLUMN students.version IS 'Optimistic locking version for concurrent update detection';
COMMENT ON COLUMN students.aadhaar_number IS 'Indian national ID number (12 digits)';
COMMENT ON CONSTRAINT check_student_age ON students IS 'Business Rule BR-1: Age must be between 3 and 18 years';
COMMENT ON CONSTRAINT uk_student_academic_year ON enrollments IS 'Business Rule BR-3: One enrollment per student per academic year';

COMMENT ON TABLE configurations IS 'School-wide configuration key-value store grouped by category';
COMMENT ON COLUMN configurations.is_encrypted IS 'Indicates if value is encrypted for sensitive data (e.g., API keys)';
COMMENT ON COLUMN configurations.data_type IS 'Guides frontend parsing/validation (STRING, NUMBER, BOOLEAN, JSON)';

-- =====================================================
-- END OF SCHEMA
-- =====================================================
-- Note: Indexes, Triggers, and Stored Functions are excluded per implementation constraints
-- StudentID auto-generation will be handled by backend application layer
-- =====================================================
