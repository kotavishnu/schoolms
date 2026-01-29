-- ========================================
-- School Management System - Database Schema
-- Complete DDL Script for Student and Configuration Modules
-- Version: 1.0.0
-- Generated: 2026-01-28
-- ========================================

-- Drop existing databases if they exist (for clean re-runs)
DROP DATABASE IF EXISTS student_db;
DROP DATABASE IF EXISTS config_db;

-- ========================================
-- STUDENT DATABASE
-- ========================================

CREATE DATABASE student_db ENCODING 'UTF8' LC_COLLATE 'en_US.UTF-8' LC_CTYPE 'en_US.UTF-8' TEMPLATE template0;

\c student_db

-- Drop existing tables if they exist
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS students CASCADE;

-- ========================================
-- TABLE: students
-- Purpose: Core student records with personal information
-- ========================================

CREATE TABLE students (
    -- Primary Key
    id                     BIGSERIAL PRIMARY KEY,

    -- Business Identifier
    student_id             VARCHAR(20) NOT NULL UNIQUE,

    -- Personal Information (Required)
    first_name             VARCHAR(100) NOT NULL CHECK (length(first_name) >= 2),
    last_name              VARCHAR(100) NOT NULL CHECK (length(last_name) >= 2),
    date_of_birth          DATE NOT NULL CHECK (
        date_of_birth <= CURRENT_DATE - INTERVAL '3 years' AND
        date_of_birth >= CURRENT_DATE - INTERVAL '18 years'
    ),
    mobile                 VARCHAR(10) NOT NULL UNIQUE CHECK (mobile ~ '^\d{10}$'),

    -- Personal Information (Optional)
    email                  VARCHAR(255) UNIQUE CHECK (
        email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$'
    ),
    address                TEXT,
    fathers_name           VARCHAR(100),
    mothers_name           VARCHAR(100),
    identification_mark    VARCHAR(200),
    aadhaar_number         VARCHAR(12) UNIQUE CHECK (
        aadhaar_number IS NULL OR aadhaar_number ~ '^\d{12}$'
    ),

    -- Status
    status                 VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),

    -- Concurrency Control
    version                BIGINT NOT NULL DEFAULT 0,

    -- Audit Columns
    created_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes for students table
CREATE INDEX idx_students_last_name ON students(last_name);
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_mobile ON students(mobile);
CREATE INDEX idx_students_aadhaar ON students(aadhaar_number) WHERE aadhaar_number IS NOT NULL;
CREATE INDEX idx_students_created_at ON students(created_at);

-- Comments for students table
COMMENT ON TABLE students IS 'Core student records with personal and contact information';
COMMENT ON COLUMN students.student_id IS 'Human-readable ID format: STD-YYYYMMDD-NNNN';
COMMENT ON COLUMN students.date_of_birth IS 'Enforces age between 3 and 18 years via CHECK constraint';
COMMENT ON COLUMN students.mobile IS '10-digit phone number, must be unique';
COMMENT ON COLUMN students.aadhaar_number IS '12-digit Aadhaar ID (India), optional but unique if provided';
COMMENT ON COLUMN students.version IS 'Optimistic locking version for concurrent updates';
COMMENT ON COLUMN students.status IS 'Student status: ACTIVE or INACTIVE';

-- ========================================
-- TABLE: enrollments
-- Purpose: Historical record of student class enrollments by academic year
-- ========================================

CREATE TABLE enrollments (
    -- Primary Key
    id                 BIGSERIAL PRIMARY KEY,

    -- Foreign Key (application-level, no database FK constraint)
    student_id         BIGINT NOT NULL,

    -- Enrollment Details
    academic_year      VARCHAR(20) NOT NULL,  -- e.g., "2024-2025"
    grade_class        VARCHAR(50) NOT NULL,  -- e.g., "Grade-10"
    section            VARCHAR(10) NOT NULL,  -- e.g., "Section-A"
    enrollment_date    DATE NOT NULL,
    withdrawal_date    DATE,

    -- Status
    status             VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (
        status IN ('ACTIVE', 'WITHDRAWN', 'TRANSFERRED', 'COMPLETED')
    ),

    -- Additional Information
    remarks            TEXT,

    -- Audit Columns
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT unique_student_academic_year UNIQUE (student_id, academic_year),
    CONSTRAINT check_withdrawal_after_enrollment CHECK (
        withdrawal_date IS NULL OR withdrawal_date >= enrollment_date
    )
);

-- Indexes for enrollments table
CREATE INDEX idx_enrollments_student_id ON enrollments(student_id);
CREATE INDEX idx_enrollments_academic_year ON enrollments(academic_year);
CREATE INDEX idx_enrollments_status ON enrollments(status);
CREATE INDEX idx_enrollments_enrollment_date ON enrollments(enrollment_date);

-- Comments for enrollments table
COMMENT ON TABLE enrollments IS 'Student class enrollment history by academic year';
COMMENT ON COLUMN enrollments.student_id IS 'References students.id (application-level FK)';
COMMENT ON COLUMN enrollments.academic_year IS 'Format: YYYY-YYYY (e.g., 2024-2025)';
COMMENT ON CONSTRAINT unique_student_academic_year ON enrollments IS 'One active enrollment per student per academic year';

-- ========================================
-- CONFIGURATION DATABASE
-- ========================================

\c postgres

CREATE DATABASE config_db ENCODING 'UTF8' LC_COLLATE 'en_US.UTF-8' LC_CTYPE 'en_US.UTF-8' TEMPLATE template0;

\c config_db

-- Drop existing table if it exists
DROP TABLE IF EXISTS configuration_settings CASCADE;

-- ========================================
-- TABLE: configuration_settings
-- Purpose: Store key-value configuration pairs grouped by category
-- ========================================

CREATE TABLE configuration_settings (
    -- Primary Key
    id                 BIGSERIAL PRIMARY KEY,

    -- Configuration Identity
    category           VARCHAR(50) NOT NULL CHECK (
        category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL')
    ),
    key                VARCHAR(100) NOT NULL,

    -- Configuration Value
    value              TEXT NOT NULL,
    description        TEXT,
    data_type          VARCHAR(20) NOT NULL CHECK (
        data_type IN ('STRING', 'NUMBER', 'BOOLEAN', 'JSON')
    ),
    is_encrypted       BOOLEAN NOT NULL DEFAULT FALSE,

    -- Concurrency Control
    version            BIGINT NOT NULL DEFAULT 0,

    -- Audit Columns
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT unique_category_key UNIQUE (category, key)
);

-- Indexes for configuration_settings table
CREATE INDEX idx_config_category ON configuration_settings(category);
CREATE INDEX idx_config_key ON configuration_settings(key);
CREATE INDEX idx_config_updated_at ON configuration_settings(updated_at);

-- Comments for configuration_settings table
COMMENT ON TABLE configuration_settings IS 'School-wide configuration key-value store';
COMMENT ON COLUMN configuration_settings.category IS 'Logical grouping: GENERAL, ACADEMIC, or FINANCIAL';
COMMENT ON COLUMN configuration_settings.data_type IS 'Hint for frontend parsing (STRING, NUMBER, BOOLEAN, JSON)';
COMMENT ON COLUMN configuration_settings.is_encrypted IS 'Flag indicating if value is stored encrypted';
COMMENT ON CONSTRAINT unique_category_key ON configuration_settings IS 'Prevents duplicate keys within a category';

-- ========================================
-- SEED DATA (Development Only)
-- ========================================

-- Sample student records
\c student_db

INSERT INTO students (
    student_id, first_name, last_name, date_of_birth, mobile,
    email, fathers_name, mothers_name, status
) VALUES
    ('STD-20260128-0001', 'John', 'Doe', '2013-05-15', '9876543210',
     'john.doe@example.com', 'James Doe', 'Jane Doe', 'ACTIVE'),
    ('STD-20260128-0002', 'Alice', 'Smith', '2014-08-22', '9876543211',
     'alice.smith@example.com', 'Robert Smith', 'Mary Smith', 'ACTIVE'),
    ('STD-20260128-0003', 'Bob', 'Johnson', '2012-03-10', '9876543212',
     'bob.johnson@example.com', 'Michael Johnson', 'Linda Johnson', 'INACTIVE');

-- Sample enrollment records
INSERT INTO enrollments (
    student_id, academic_year, grade_class, section, enrollment_date, status
) VALUES
    ((SELECT id FROM students WHERE student_id = 'STD-20260128-0001'),
     '2025-2026', 'Grade-7', 'Section-A', '2025-06-01', 'ACTIVE'),
    ((SELECT id FROM students WHERE student_id = 'STD-20260128-0002'),
     '2025-2026', 'Grade-6', 'Section-B', '2025-06-01', 'ACTIVE'),
    ((SELECT id FROM students WHERE student_id = 'STD-20260128-0003'),
     '2024-2025', 'Grade-8', 'Section-A', '2024-06-01', 'COMPLETED');

-- Sample configuration records
\c config_db

INSERT INTO configuration_settings (category, key, value, description, data_type) VALUES
    -- General Settings
    ('GENERAL', 'school_name', 'Springfield Elementary', 'Official school name', 'STRING'),
    ('GENERAL', 'school_code', 'SPFD-001', 'Unique school identifier', 'STRING'),
    ('GENERAL', 'academic_year', '2025-2026', 'Current academic year', 'STRING'),
    ('GENERAL', 'school_address', '123 Main Street, Springfield', 'School physical address', 'STRING'),
    ('GENERAL', 'contact_email', 'admin@springfield.edu', 'School contact email', 'STRING'),
    ('GENERAL', 'contact_phone', '5551234567', 'School contact phone', 'STRING'),

    -- Academic Settings
    ('ACADEMIC', 'max_class_size', '40', 'Maximum students per class', 'NUMBER'),
    ('ACADEMIC', 'passing_percentage', '40', 'Minimum passing marks percentage', 'NUMBER'),
    ('ACADEMIC', 'enable_grading', 'true', 'Enable grade-based evaluation', 'BOOLEAN'),
    ('ACADEMIC', 'term_system', 'semester', 'Academic term system (semester/trimester)', 'STRING'),
    ('ACADEMIC', 'working_days_per_week', '5', 'Number of working days per week', 'NUMBER'),

    -- Financial Settings
    ('FINANCIAL', 'tuition_fee', '25000', 'Annual tuition fee in local currency', 'NUMBER'),
    ('FINANCIAL', 'late_fee_penalty', '500', 'Penalty for late fee payment', 'NUMBER'),
    ('FINANCIAL', 'payment_gateway_enabled', 'false', 'Enable online payment gateway', 'BOOLEAN'),
    ('FINANCIAL', 'currency_code', 'INR', 'Currency code (ISO 4217)', 'STRING'),
    ('FINANCIAL', 'tax_rate', '0', 'Tax rate percentage', 'NUMBER');

-- ========================================
-- VERIFICATION QUERIES
-- ========================================

-- Verify students table
\c student_db
SELECT 'Students Table Count: ' || COUNT(*) FROM students;

-- Verify enrollments table
SELECT 'Enrollments Table Count: ' || COUNT(*) FROM enrollments;

-- Verify configuration_settings table
\c config_db
SELECT 'Configuration Settings Count: ' || COUNT(*) FROM configuration_settings;

-- ========================================
-- END OF SCRIPT
-- ========================================

-- Display success message
\c student_db
SELECT 'Database schema created successfully for Student Service!' AS status;

\c config_db
SELECT 'Database schema created successfully for Configuration Service!' AS status;
