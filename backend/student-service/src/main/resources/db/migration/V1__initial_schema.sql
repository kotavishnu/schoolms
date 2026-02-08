-- =====================================================
-- School Management System - Student Service Schema
-- PostgreSQL 18+
-- =====================================================
-- Created: 2026-02-03
-- Description: Initial schema for students and enrollments tables
-- =====================================================

-- Clean slate for re-runs
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS students CASCADE;

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

    -- Business Key (Auto-generated via application layer)
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
    CONSTRAINT check_status_values CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT check_guardian_required CHECK (
        fathers_name IS NOT NULL OR mothers_name IS NOT NULL
    )
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

    -- Optimistic Locking
    version               BIGINT NOT NULL DEFAULT 0,

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
        CHECK (status IN ('ACTIVE', 'WITHDRAWN', 'COMPLETED')),
    CONSTRAINT check_academic_year_format
        CHECK (academic_year ~ '^\d{4}-\d{4}$')
);

-- =====================================================
-- INDEXES FOR QUERY OPTIMIZATION
-- =====================================================

-- Student indexes
CREATE INDEX idx_students_last_name ON students(last_name);
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_date_of_birth ON students(date_of_birth);
CREATE INDEX idx_students_created_at ON students(created_at);

-- Enrollment indexes
CREATE INDEX idx_enrollments_student_id ON enrollments(student_id);
CREATE INDEX idx_enrollments_academic_year ON enrollments(academic_year);
CREATE INDEX idx_enrollments_status ON enrollments(status);
CREATE INDEX idx_enrollments_academic_year_status ON enrollments(academic_year, status);

-- =====================================================
-- COMMENTS (Documentation)
-- =====================================================

COMMENT ON TABLE students IS 'Core student profile information for School Management System';
COMMENT ON COLUMN students.student_id IS 'Auto-generated format: STD-YYYYMMDD-NNNN (e.g., STD-20260203-0001)';
COMMENT ON COLUMN students.version IS 'Optimistic locking version for concurrent update detection';
COMMENT ON COLUMN students.aadhaar_number IS 'Indian national ID number (12 digits)';
COMMENT ON CONSTRAINT check_student_age ON students IS 'Business Rule BR-1: Age must be between 3 and 18 years';
COMMENT ON CONSTRAINT check_guardian_required ON students IS 'Business Rule BR-5: At least one guardian name required';

COMMENT ON TABLE enrollments IS 'Student enrollment tracking for academic years';
COMMENT ON CONSTRAINT uk_student_academic_year ON enrollments IS 'Business Rule BR-3: One enrollment per student per academic year';

-- =====================================================
-- END OF SCHEMA
-- =====================================================
