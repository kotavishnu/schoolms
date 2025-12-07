-- ============================================================================
-- School Management System - Database Schema
-- Version: 1.0
-- Date: 2025-12-06
-- Description: Complete database schema for Student and Configuration services
-- ============================================================================

-- ============================================================================
-- STUDENT SERVICE DATABASE
-- ============================================================================

-- Drop tables if they exist (for clean re-runs)
DROP TABLE IF EXISTS enrollment_history CASCADE;
DROP TABLE IF EXISTS students CASCADE;

-- ============================================================================
-- Table: students
-- Purpose: Store student registration and profile information
-- ============================================================================

CREATE TABLE students (
    -- Primary Key
    id                  BIGSERIAL PRIMARY KEY,

    -- Business Key
    student_id          VARCHAR(20) NOT NULL UNIQUE,

    -- Personal Information
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    date_of_birth       DATE NOT NULL,

    -- Contact Information
    mobile              VARCHAR(15) NOT NULL UNIQUE,
    email               VARCHAR(255),
    address             TEXT,

    -- Family Information
    fathers_name        VARCHAR(100),
    mothers_name        VARCHAR(100),

    -- Identification
    identification_mark VARCHAR(200),
    aadhaar_number      VARCHAR(12) UNIQUE,

    -- Status
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Audit Columns
    version             BIGINT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),

    -- Constraints
    CONSTRAINT pk_students PRIMARY KEY (id),
    CONSTRAINT uk_students_student_id UNIQUE (student_id),
    CONSTRAINT uk_students_mobile UNIQUE (mobile),
    CONSTRAINT uk_students_aadhaar UNIQUE (aadhaar_number),
    CONSTRAINT ck_students_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

-- Table Comments
COMMENT ON TABLE students IS 'Stores student registration and profile information';
COMMENT ON COLUMN students.student_id IS 'Auto-generated unique student identifier (format: STD-YYYYMMDD-NNNN)';
COMMENT ON COLUMN students.date_of_birth IS 'Student date of birth - must be 3-18 years old at registration';
COMMENT ON COLUMN students.mobile IS 'Unique mobile number per student';
COMMENT ON COLUMN students.status IS 'Student status: ACTIVE or INACTIVE';
COMMENT ON COLUMN students.version IS 'Version for optimistic locking to prevent concurrent update conflicts';

-- ============================================================================
-- Table: enrollment_history
-- Purpose: Track student enrollment across academic years
-- ============================================================================

CREATE TABLE enrollment_history (
    -- Primary Key
    id                  BIGSERIAL PRIMARY KEY,

    -- Foreign Key
    student_id          BIGINT NOT NULL,

    -- Academic Information
    academic_year       VARCHAR(9) NOT NULL,  -- Format: 2024-2025
    grade_class         VARCHAR(20) NOT NULL,
    section             VARCHAR(10),

    -- Enrollment Details
    enrollment_date     DATE NOT NULL,
    withdrawal_date     DATE,
    status              VARCHAR(20) NOT NULL DEFAULT 'ENROLLED',

    -- Additional Information
    remarks             TEXT,

    -- Audit Columns
    version             BIGINT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),

    -- Constraints
    CONSTRAINT pk_enrollment_history PRIMARY KEY (id),
    CONSTRAINT fk_enrollment_student
        FOREIGN KEY (student_id)
        REFERENCES students(id)
        ON DELETE CASCADE,
    CONSTRAINT ck_enrollment_status
        CHECK (status IN ('ENROLLED', 'COMPLETED', 'WITHDRAWN')),
    CONSTRAINT ck_enrollment_dates
        CHECK (withdrawal_date IS NULL OR withdrawal_date >= enrollment_date),
    CONSTRAINT uk_enrollment_student_year
        UNIQUE (student_id, academic_year)
);

-- Table Comments
COMMENT ON TABLE enrollment_history IS 'Tracks student enrollment across academic years and grade levels';
COMMENT ON COLUMN enrollment_history.academic_year IS 'Academic year in format YYYY-YYYY (e.g., 2024-2025)';
COMMENT ON COLUMN enrollment_history.status IS 'Enrollment status: ENROLLED, COMPLETED, or WITHDRAWN';

-- ============================================================================
-- CONFIGURATION SERVICE DATABASE
-- ============================================================================

-- Drop table if exists
DROP TABLE IF EXISTS configuration_settings CASCADE;

-- ============================================================================
-- Table: configuration_settings
-- Purpose: Store school configuration as key-value pairs
-- ============================================================================

CREATE TABLE configuration_settings (
    -- Primary Key
    id                  BIGSERIAL PRIMARY KEY,

    -- Configuration Identity
    category            VARCHAR(50) NOT NULL,
    config_key          VARCHAR(100) NOT NULL,
    config_value        TEXT NOT NULL,

    -- Metadata
    description         TEXT,
    data_type           VARCHAR(20) NOT NULL DEFAULT 'STRING',
    is_encrypted        BOOLEAN NOT NULL DEFAULT FALSE,

    -- Audit Columns
    version             BIGINT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),

    -- Constraints
    CONSTRAINT pk_configuration_settings PRIMARY KEY (id),
    CONSTRAINT uk_config_category_key UNIQUE (category, config_key),
    CONSTRAINT ck_config_category
        CHECK (category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL')),
    CONSTRAINT ck_config_data_type
        CHECK (data_type IN ('STRING', 'NUMBER', 'BOOLEAN', 'JSON'))
);

-- Table Comments
COMMENT ON TABLE configuration_settings IS 'Stores school configuration as categorized key-value pairs';
COMMENT ON COLUMN configuration_settings.category IS 'Configuration category: GENERAL, ACADEMIC, or FINANCIAL';
COMMENT ON COLUMN configuration_settings.data_type IS 'Data type: STRING, NUMBER, BOOLEAN, or JSON';
COMMENT ON COLUMN configuration_settings.is_encrypted IS 'Indicates if the value is stored encrypted';

-- ============================================================================
-- SAMPLE DATA (Optional - for testing)
-- ============================================================================

-- Sample Students
-- Note: student_id generation will be handled by backend application
-- INSERT INTO students (student_id, first_name, last_name, date_of_birth, mobile, fathers_name, mothers_name, email, address, status, created_by)
-- VALUES
--     ('STD-20241206-0001', 'John', 'Doe', '2010-05-15', '9876543210', 'Richard Doe', 'Jane Doe', 'john.doe@example.com', '123 Main St', 'ACTIVE', 'system'),
--     ('STD-20241206-0002', 'Jane', 'Smith', '2012-08-22', '9876543211', 'Robert Smith', 'Mary Smith', 'jane.smith@example.com', '456 Oak Ave', 'ACTIVE', 'system');

-- Sample Configuration Settings
INSERT INTO configuration_settings (category, config_key, config_value, description, data_type, created_by)
VALUES
    ('GENERAL', 'school.name', 'ABC High School', 'Official school name', 'STRING', 'system'),
    ('GENERAL', 'school.code', 'ABC-001', 'School identification code', 'STRING', 'system'),
    ('GENERAL', 'school.address', '123 Education Lane, City, State 12345', 'School physical address', 'STRING', 'system'),
    ('ACADEMIC', 'current.academic.year', '2024-2025', 'Current academic year', 'STRING', 'system'),
    ('ACADEMIC', 'class.capacity.default', '30', 'Default class capacity', 'NUMBER', 'system'),
    ('FINANCIAL', 'currency', 'INR', 'Currency code', 'STRING', 'system'),
    ('FINANCIAL', 'fee.payment.deadline', '10', 'Fee payment deadline (day of month)', 'NUMBER', 'system');

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================
