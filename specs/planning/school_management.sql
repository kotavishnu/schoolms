-- =====================================================
-- School Management System - Complete Database Schema
-- Version: 1.0
-- Date: 2026-01-15
--
-- CRITICAL CONSTRAINTS:
-- - No Indexes (except implicit from PK/UNIQUE)
-- - No Triggers
-- - No Stored Functions
-- - No Migration Tools
-- Tables, Primary Keys, Foreign Keys, Basic Constraints ONLY
-- =====================================================

-- Drop existing tables (clean re-run)
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS configurations CASCADE;

-- =====================================================
-- STUDENT SERVICE DATABASE (students_db)
-- =====================================================

-- =====================================================
-- Table: students
-- Description: Core student profile and personal information
-- =====================================================
CREATE TABLE students (
    -- Primary Key
    id                      BIGSERIAL PRIMARY KEY,

    -- Business Key (Auto-generated via application or sequence)
    student_id              VARCHAR(50) NOT NULL UNIQUE,

    -- Personal Information
    first_name              VARCHAR(100) NOT NULL,
    last_name               VARCHAR(100) NOT NULL,
    date_of_birth           DATE NOT NULL,
    aadhaar_number          VARCHAR(12) UNIQUE,
    identification_mark     TEXT,
    address                 TEXT NOT NULL,

    -- Guardian Information
    fathers_name            VARCHAR(100),
    mothers_name            VARCHAR(100),

    -- Contact Information
    mobile                  VARCHAR(10) NOT NULL UNIQUE,
    email                   VARCHAR(255) NOT NULL UNIQUE,

    -- Status
    status                  VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Optimistic Locking
    version                 INTEGER NOT NULL DEFAULT 0,

    -- Audit Columns
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),

    -- Constraints
    CONSTRAINT chk_students_status
        CHECK (status IN ('ACTIVE', 'INACTIVE')),

    CONSTRAINT chk_students_mobile
        CHECK (mobile ~ '^\d{10}$'),

    CONSTRAINT chk_students_aadhaar
        CHECK (aadhaar_number IS NULL OR aadhaar_number ~ '^\d{12}$'),

    CONSTRAINT chk_students_email
        CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$'),

    CONSTRAINT chk_students_name_length
        CHECK (LENGTH(first_name) >= 2 AND LENGTH(last_name) >= 2),

    CONSTRAINT chk_students_address_length
        CHECK (LENGTH(address) >= 10 AND LENGTH(address) <= 500)
);

-- Table Comments
COMMENT ON TABLE students IS 'Core student profile and personal information';
COMMENT ON COLUMN students.student_id IS 'Auto-generated business key in format: STD-YYYYMMDD-XXXX';
COMMENT ON COLUMN students.version IS 'Optimistic locking version field';
COMMENT ON COLUMN students.status IS 'Student status: ACTIVE or INACTIVE';


-- =====================================================
-- Table: enrollments
-- Description: Student enrollment history across academic years
-- =====================================================
CREATE TABLE enrollments (
    -- Primary Key
    id                      BIGSERIAL PRIMARY KEY,

    -- Foreign Key
    student_id              BIGINT NOT NULL,

    -- Enrollment Information
    academic_year           VARCHAR(20) NOT NULL,
    grade_class             VARCHAR(50) NOT NULL,
    section                 VARCHAR(10) NOT NULL,
    enrollment_date         DATE NOT NULL,
    withdrawal_date         DATE,
    status                  VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    remarks                 TEXT,

    -- Audit Columns
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Foreign Key Constraint
    CONSTRAINT fk_enrollments_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,

    -- Business Constraints
    CONSTRAINT chk_enrollments_status
        CHECK (status IN ('ACTIVE', 'WITHDRAWN', 'COMPLETED')),

    CONSTRAINT chk_enrollments_dates
        CHECK (withdrawal_date IS NULL OR withdrawal_date >= enrollment_date),

    CONSTRAINT chk_enrollments_academic_year
        CHECK (academic_year ~ '^\d{4}-\d{4}$'),

    -- Unique constraint: One active enrollment per student per academic year
    CONSTRAINT uq_enrollments_student_year
        UNIQUE (student_id, academic_year, status)
);

-- Table Comments
COMMENT ON TABLE enrollments IS 'Student enrollment history across academic years';
COMMENT ON CONSTRAINT uq_enrollments_student_year ON enrollments IS 'Prevents duplicate active enrollments for same student in same academic year';


-- =====================================================
-- CONFIGURATION SERVICE DATABASE (config_db)
-- =====================================================

-- =====================================================
-- Table: configurations
-- Description: System-wide configuration settings
-- =====================================================
CREATE TABLE configurations (
    -- Primary Key
    id                      BIGSERIAL PRIMARY KEY,

    -- Configuration Data
    category                VARCHAR(20) NOT NULL,
    key                     VARCHAR(100) NOT NULL,
    value                   TEXT NOT NULL,
    description             TEXT,
    data_type               VARCHAR(20) NOT NULL DEFAULT 'STRING',
    is_encrypted            BOOLEAN NOT NULL DEFAULT FALSE,

    -- Optimistic Locking
    version                 INTEGER NOT NULL DEFAULT 0,

    -- Audit Columns
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by              VARCHAR(100),
    updated_by              VARCHAR(100),

    -- Constraints
    CONSTRAINT chk_configurations_category
        CHECK (category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL', 'SYSTEM')),

    CONSTRAINT chk_configurations_data_type
        CHECK (data_type IN ('STRING', 'NUMBER', 'BOOLEAN', 'JSON')),

    CONSTRAINT chk_configurations_key_format
        CHECK (key ~ '^[A-Z0-9_]+$'),

    -- Unique constraint: category + key combination
    CONSTRAINT uq_configurations_category_key
        UNIQUE (category, key)
);

-- Table Comments
COMMENT ON TABLE configurations IS 'System-wide configuration settings with category-based organization';
COMMENT ON COLUMN configurations.data_type IS 'Defines how the value should be parsed: STRING, NUMBER, BOOLEAN, JSON';
COMMENT ON COLUMN configurations.is_encrypted IS 'Indicates if the value is encrypted at rest';


-- =====================================================
-- Sample Data Insertion (Optional - for initial setup)
-- =====================================================

-- Sample Configurations
INSERT INTO configurations (category, key, value, description, data_type) VALUES
('GENERAL', 'SCHOOL_NAME', 'Springfield Public School', 'Official school name', 'STRING'),
('GENERAL', 'SCHOOL_CODE', 'SPS001', 'Unique school identifier', 'STRING'),
('ACADEMIC', 'CURRENT_ACADEMIC_YEAR', '2025-2026', 'Current academic session', 'STRING'),
('ACADEMIC', 'MAX_CLASS_CAPACITY', '40', 'Maximum students per class (BR-3)', 'NUMBER'),
('FINANCIAL', 'CURRENCY', 'INR', 'Default currency', 'STRING'),
('SYSTEM', 'MAINTENANCE_MODE', 'false', 'Enable maintenance mode', 'BOOLEAN');


-- =====================================================
-- Database Schema Summary
-- =====================================================
-- Total Tables: 3
-- - students (with constraints and audit columns)
-- - enrollments (with foreign key to students)
-- - configurations (category-based key-value storage)
--
-- Total Constraints: 20
-- - Primary Keys: 3
-- - Foreign Keys: 1
-- - Unique Constraints: 6
-- - Check Constraints: 10
--
-- Optimistic Locking: Enabled on all aggregate roots (version column)
-- =====================================================
