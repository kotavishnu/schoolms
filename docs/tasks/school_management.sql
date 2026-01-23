-- School Management System - Database Schema
-- PostgreSQL 18+ Compatible
-- Date: 2026-01-22
-- Purpose: Production-ready DDL for Student and Configuration databases

-- ============================================================
-- STUDENT DATABASE (student_db)
-- ============================================================

-- Drop existing tables (if any)
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS students CASCADE;

-- Students Table
CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL UNIQUE,

    -- Personal Information
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    aadhaar_number VARCHAR(12) UNIQUE,
    identification_marks VARCHAR(200),

    -- Guardian Information
    guardian_name VARCHAR(100) NOT NULL,
    mother_name VARCHAR(100) NOT NULL,

    -- Contact Information
    mobile VARCHAR(10) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(500) NOT NULL,

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Optimistic Locking
    version INTEGER NOT NULL DEFAULT 0,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Constraints for students table
ALTER TABLE students
    ADD CONSTRAINT chk_students_status CHECK (status IN ('ACTIVE', 'INACTIVE'));

ALTER TABLE students
    ADD CONSTRAINT chk_students_mobile CHECK (mobile ~ '^\d{10}$');

ALTER TABLE students
    ADD CONSTRAINT chk_students_email CHECK (email ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$');

ALTER TABLE students
    ADD CONSTRAINT chk_students_aadhaar CHECK (aadhaar_number IS NULL OR aadhaar_number ~ '^\d{12}$');

ALTER TABLE students
    ADD CONSTRAINT chk_students_age CHECK (
        date_of_birth <= CURRENT_DATE - INTERVAL '3 years' AND
        date_of_birth >= CURRENT_DATE - INTERVAL '18 years'
    );

-- Enrollments Table
CREATE TABLE enrollments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,

    -- Enrollment Details
    academic_year VARCHAR(20) NOT NULL,
    grade_class VARCHAR(50) NOT NULL,
    section VARCHAR(10) NOT NULL,
    enrollment_date DATE NOT NULL,
    withdrawal_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    remarks TEXT,

    -- Optimistic Locking
    version INTEGER NOT NULL DEFAULT 0,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key
    CONSTRAINT fk_enrollments_student FOREIGN KEY (student_id)
        REFERENCES students(id) ON DELETE CASCADE
);

-- Constraints for enrollments table
ALTER TABLE enrollments
    ADD CONSTRAINT chk_enrollments_status CHECK (status IN ('ACTIVE', 'WITHDRAWN', 'COMPLETED'));

-- Indexes for students table
CREATE INDEX idx_students_last_name ON students(last_name);
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_mobile ON students(mobile);
CREATE INDEX idx_students_email ON students(email);
CREATE INDEX idx_students_created_at ON students(created_at);

-- Indexes for enrollments table
CREATE INDEX idx_enrollments_student_id ON enrollments(student_id);
CREATE INDEX idx_enrollments_academic_year ON enrollments(academic_year);
CREATE INDEX idx_enrollments_status ON enrollments(status);

-- Trigger for updated_at auto-update (students)
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_students_updated_at
    BEFORE UPDATE ON students
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- CONFIGURATION DATABASE (config_db)
-- ============================================================

-- Drop existing tables (if any)
DROP TABLE IF EXISTS configurations CASCADE;

-- Configurations Table
CREATE TABLE configurations (
    id BIGSERIAL PRIMARY KEY,

    -- Configuration Details
    category VARCHAR(50) NOT NULL,
    key VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    description VARCHAR(500),
    data_type VARCHAR(20) NOT NULL DEFAULT 'STRING',
    is_encrypted BOOLEAN NOT NULL DEFAULT FALSE,

    -- Optimistic Locking
    version INTEGER NOT NULL DEFAULT 0,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Unique Constraint
    CONSTRAINT uniq_configurations_category_key UNIQUE (category, key)
);

-- Constraints for configurations table
ALTER TABLE configurations
    ADD CONSTRAINT chk_configurations_category CHECK (category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL', 'SYSTEM'));

ALTER TABLE configurations
    ADD CONSTRAINT chk_configurations_data_type CHECK (data_type IN ('STRING', 'NUMBER', 'BOOLEAN', 'JSON'));

ALTER TABLE configurations
    ADD CONSTRAINT chk_configurations_key CHECK (key ~ '^[A-Z0-9_]+$');

-- Indexes for configurations table
CREATE INDEX idx_configurations_category ON configurations(category);
CREATE INDEX idx_configurations_key ON configurations(key);

-- Trigger for updated_at auto-update (configurations)
CREATE TRIGGER trigger_configurations_updated_at
    BEFORE UPDATE ON configurations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================================
-- DEFAULT DATA SEEDS (OPTIONAL)
-- ============================================================

-- Insert default system configurations
INSERT INTO configurations (category, key, value, description, data_type) VALUES
('SYSTEM', 'APP_NAME', 'School Management System', 'Application Name', 'STRING'),
('SYSTEM', 'APP_VERSION', '1.0.0', 'Application Version', 'STRING'),
('SYSTEM', 'MAX_UPLOAD_SIZE_MB', '10', 'Maximum file upload size in MB', 'NUMBER'),
('GENERAL', 'SCHOOL_NAME', 'Example School', 'Name of the school', 'STRING'),
('GENERAL', 'SCHOOL_CODE', 'SCH001', 'School identification code', 'STRING'),
('GENERAL', 'CONTACT_EMAIL', 'contact@school.edu', 'School contact email', 'STRING'),
('GENERAL', 'CONTACT_PHONE', '1234567890', 'School contact phone', 'STRING'),
('ACADEMIC', 'CURRENT_ACADEMIC_YEAR', '2025-2026', 'Current academic year', 'STRING'),
('ACADEMIC', 'MIN_AGE', '3', 'Minimum student age', 'NUMBER'),
('ACADEMIC', 'MAX_AGE', '18', 'Maximum student age', 'NUMBER'),
('FINANCIAL', 'CURRENCY', 'INR', 'Currency code', 'STRING'),
('FINANCIAL', 'TAX_RATE', '0.18', 'Tax rate (GST)', 'NUMBER')
ON CONFLICT (category, key) DO NOTHING;

-- ============================================================
-- VERIFICATION QUERIES
-- ============================================================

-- Verify students table structure
-- SELECT column_name, data_type, is_nullable, column_default
-- FROM information_schema.columns
-- WHERE table_name = 'students'
-- ORDER BY ordinal_position;

-- Verify enrollments table structure
-- SELECT column_name, data_type, is_nullable, column_default
-- FROM information_schema.columns
-- WHERE table_name = 'enrollments'
-- ORDER BY ordinal_position;

-- Verify configurations table structure
-- SELECT column_name, data_type, is_nullable, column_default
-- FROM information_schema.columns
-- WHERE table_name = 'configurations'
-- ORDER BY ordinal_position;

-- Verify all constraints
-- SELECT constraint_name, constraint_type
-- FROM information_schema.table_constraints
-- WHERE table_name IN ('students', 'enrollments', 'configurations');

-- Verify all indexes
-- SELECT indexname, indexdef
-- FROM pg_indexes
-- WHERE tablename IN ('students', 'enrollments', 'configurations');

-- ============================================================
-- NOTES
-- ============================================================
-- 1. This script is idempotent (can be run multiple times)
-- 2. All tables have optimistic locking via version column
-- 3. Audit timestamps (created_at, updated_at) auto-managed
-- 4. All constraints enforced at database level
-- 5. Indexes optimized for common query patterns
-- 6. Foreign keys configured with CASCADE delete
-- 7. Triggers ensure updated_at is always current
-- 8. Compatible with PostgreSQL 18+ (UTC timezone required)
