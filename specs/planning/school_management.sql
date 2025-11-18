-- ============================================================================
-- School Management System - Database Schema
-- Version: 1.0.0
-- Date: 2025-11-18
-- Description: Complete PostgreSQL schema for Student and Configuration services
-- ============================================================================

-- ============================================================================
-- STUDENT DATABASE SCHEMA
-- ============================================================================

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS students CASCADE;
DROP SEQUENCE IF EXISTS student_id_seq CASCADE;

-- Create sequence for student ID generation
CREATE SEQUENCE student_id_seq
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;

-- Students Table
CREATE TABLE students (
    -- Primary Key
    student_id VARCHAR(50) PRIMARY KEY,

    -- Personal Information
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,

    -- Address Information
    street VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pin_code VARCHAR(6) NOT NULL,
    country VARCHAR(50) NOT NULL DEFAULT 'India',

    -- Contact Information
    mobile VARCHAR(10) NOT NULL,
    email VARCHAR(100),

    -- Family Information
    father_name_guardian VARCHAR(100) NOT NULL,
    mother_name VARCHAR(100),

    -- Additional Information
    caste VARCHAR(50),
    moles TEXT,
    aadhaar_number VARCHAR(12),

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50) NOT NULL,

    -- Optimistic Locking
    version BIGINT NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT chk_student_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT chk_mobile_format CHECK (mobile ~ '^[0-9]{10}$'),
    CONSTRAINT chk_aadhaar_format CHECK (
        aadhaar_number IS NULL OR aadhaar_number ~ '^[0-9]{12}$'
    ),
    CONSTRAINT chk_email_format CHECK (
        email IS NULL OR email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$'
    ),
    CONSTRAINT chk_pin_code_format CHECK (pin_code ~ '^[0-9]{6}$'),
    CONSTRAINT chk_name_length CHECK (
        LENGTH(first_name) >= 2 AND LENGTH(last_name) >= 2
    ),
    CONSTRAINT chk_date_of_birth CHECK (
        date_of_birth < CURRENT_DATE AND
        date_of_birth >= CURRENT_DATE - INTERVAL '18 years' AND
        date_of_birth <= CURRENT_DATE - INTERVAL '3 years'
    )
);

-- Unique Constraints on Students
CREATE UNIQUE INDEX idx_students_mobile ON students(mobile);
CREATE UNIQUE INDEX idx_students_aadhaar ON students(aadhaar_number)
    WHERE aadhaar_number IS NOT NULL;
CREATE UNIQUE INDEX idx_students_email ON students(email)
    WHERE email IS NOT NULL;

-- Performance Indexes on Students
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_created_at ON students(created_at DESC);
CREATE INDEX idx_students_last_name ON students(last_name);
CREATE INDEX idx_students_first_name ON students(first_name);
CREATE INDEX idx_students_dob ON students(date_of_birth);
CREATE INDEX idx_students_caste ON students(caste) WHERE caste IS NOT NULL;
CREATE INDEX idx_students_name_status ON students(last_name, first_name, status);

-- Function to generate next student ID
CREATE OR REPLACE FUNCTION generate_student_id()
RETURNS VARCHAR(50) AS $$
DECLARE
    next_seq BIGINT;
    current_year INT;
    new_student_id VARCHAR(50);
BEGIN
    next_seq := nextval('student_id_seq');
    current_year := EXTRACT(YEAR FROM CURRENT_DATE);
    new_student_id := 'STU-' || current_year || '-' || LPAD(next_seq::TEXT, 5, '0');
    RETURN new_student_id;
END;
$$ LANGUAGE plpgsql;

-- View: Active Students
CREATE OR REPLACE VIEW v_active_students AS
SELECT
    student_id,
    first_name,
    last_name,
    CONCAT(first_name, ' ', last_name) AS full_name,
    date_of_birth,
    EXTRACT(YEAR FROM AGE(date_of_birth)) AS age,
    mobile,
    email,
    CONCAT(street, ', ', city, ', ', state, ' - ', pin_code) AS full_address,
    created_at,
    updated_at
FROM students
WHERE status = 'ACTIVE';

-- Comments for Students Table
COMMENT ON TABLE students IS 'Stores student profile information';
COMMENT ON COLUMN students.student_id IS 'Auto-generated unique student ID (format: STU-YYYY-NNNNN)';
COMMENT ON COLUMN students.mobile IS 'Unique 10-digit mobile number';
COMMENT ON COLUMN students.aadhaar_number IS 'Optional 12-digit Aadhaar number';
COMMENT ON COLUMN students.status IS 'Student status: ACTIVE or INACTIVE';
COMMENT ON COLUMN students.version IS 'Optimistic locking version number';

-- Sample Student Data
INSERT INTO students (
    student_id, first_name, last_name, date_of_birth,
    street, city, state, pin_code, country,
    mobile, email, father_name_guardian, mother_name,
    caste, moles, aadhaar_number, status,
    created_by, updated_by
) VALUES
(
    generate_student_id(),
    'Rajesh',
    'Kumar',
    '2015-05-15',
    '123 MG Road',
    'Bangalore',
    'Karnataka',
    '560001',
    'India',
    '9876543210',
    'rajesh.kumar@example.com',
    'Suresh Kumar',
    'Lakshmi Kumar',
    'General',
    'Small mole on left cheek',
    '123456789012',
    'ACTIVE',
    'SYSTEM',
    'SYSTEM'
),
(
    generate_student_id(),
    'Priya',
    'Sharma',
    '2016-08-20',
    '456 Brigade Road',
    'Bangalore',
    'Karnataka',
    '560025',
    'India',
    '9876543211',
    NULL,
    'Ramesh Sharma',
    'Sunita Sharma',
    'OBC',
    NULL,
    NULL,
    'ACTIVE',
    'SYSTEM',
    'SYSTEM'
),
(
    generate_student_id(),
    'Arun',
    'Patel',
    '2014-03-10',
    '789 Church Street',
    'Mumbai',
    'Maharashtra',
    '400001',
    'India',
    '9876543212',
    'arun.patel@example.com',
    'Kiran Patel',
    'Meera Patel',
    'General',
    'Birthmark on right hand',
    '123456789013',
    'ACTIVE',
    'SYSTEM',
    'SYSTEM'
);

-- ============================================================================
-- CONFIGURATION DATABASE SCHEMA
-- ============================================================================

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS configuration_settings CASCADE;
DROP TABLE IF EXISTS school_profile CASCADE;

-- School Profile Table
CREATE TABLE school_profile (
    -- Primary Key (always 1 for single school)
    id BIGINT PRIMARY KEY DEFAULT 1,

    -- School Information
    school_name VARCHAR(200) NOT NULL,
    school_code VARCHAR(20) NOT NULL,
    logo_path VARCHAR(500),

    -- Contact Information
    address VARCHAR(500),
    phone VARCHAR(15),
    email VARCHAR(100),

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL,

    -- Constraints
    CONSTRAINT chk_school_id CHECK (id = 1),
    CONSTRAINT chk_school_code_format CHECK (school_code ~ '^[A-Z0-9]{3,20}$'),
    CONSTRAINT chk_school_email_format CHECK (
        email IS NULL OR email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$'
    ),
    CONSTRAINT chk_school_phone_format CHECK (
        phone IS NULL OR phone ~ '^[0-9+()-]{10,15}$'
    )
);

-- Unique constraint on school code
CREATE UNIQUE INDEX idx_school_profile_code ON school_profile(school_code);

-- Comments for School Profile
COMMENT ON TABLE school_profile IS 'School profile information (single record)';
COMMENT ON COLUMN school_profile.id IS 'Always 1 for single school deployment';
COMMENT ON COLUMN school_profile.school_code IS 'Unique school identifier code';
COMMENT ON COLUMN school_profile.logo_path IS 'File path or URL to school logo';

-- Insert default school profile
INSERT INTO school_profile (id, school_name, school_code, updated_by)
VALUES (1, 'Sample School', 'SCH001', 'SYSTEM')
ON CONFLICT (id) DO NOTHING;

-- Configuration Settings Table
CREATE TABLE configuration_settings (
    -- Primary Key
    setting_id BIGSERIAL PRIMARY KEY,

    -- Setting Information
    category VARCHAR(20) NOT NULL,
    key VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    description VARCHAR(500),

    -- Audit Fields
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL,

    -- Optimistic Locking
    version BIGINT NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT chk_setting_category CHECK (
        category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL')
    ),
    CONSTRAINT chk_setting_key_format CHECK (
        key ~ '^[A-Z][A-Z0-9_]*$'
    )
);

-- Unique constraint on category + key combination
CREATE UNIQUE INDEX idx_config_category_key ON configuration_settings(category, key);

-- Performance indexes
CREATE INDEX idx_config_category ON configuration_settings(category);
CREATE INDEX idx_config_updated_at ON configuration_settings(updated_at DESC);

-- Comments for Configuration Settings
COMMENT ON TABLE configuration_settings IS 'Key-value configuration settings grouped by category';
COMMENT ON COLUMN configuration_settings.category IS 'Setting category: GENERAL, ACADEMIC, or FINANCIAL';
COMMENT ON COLUMN configuration_settings.key IS 'Setting key in UPPERCASE_SNAKE_CASE format';
COMMENT ON COLUMN configuration_settings.value IS 'Setting value (stored as text)';
COMMENT ON COLUMN configuration_settings.version IS 'Optimistic locking version number';

-- General Settings
INSERT INTO configuration_settings (category, key, value, description, updated_by)
VALUES
    ('GENERAL', 'SCHOOL_TIMEZONE', 'Asia/Kolkata', 'Default timezone for the school', 'SYSTEM'),
    ('GENERAL', 'DATE_FORMAT', 'dd-MM-yyyy', 'Default date display format', 'SYSTEM'),
    ('GENERAL', 'LANGUAGE', 'en', 'Default language (ISO 639-1 code)', 'SYSTEM'),
    ('GENERAL', 'SESSION_TIMEOUT_MINUTES', '30', 'User session timeout in minutes', 'SYSTEM'),
    ('GENERAL', 'MAX_FILE_UPLOAD_MB', '10', 'Maximum file upload size in MB', 'SYSTEM')
ON CONFLICT (category, key) DO NOTHING;

-- Academic Settings
INSERT INTO configuration_settings (category, key, value, description, updated_by)
VALUES
    ('ACADEMIC', 'CURRENT_ACADEMIC_YEAR', '2025-2026', 'Current academic year', 'SYSTEM'),
    ('ACADEMIC', 'ACADEMIC_YEAR_START_MONTH', '6', 'Academic year start month (1-12)', 'SYSTEM'),
    ('ACADEMIC', 'MIN_STUDENT_AGE', '3', 'Minimum student age for admission', 'SYSTEM'),
    ('ACADEMIC', 'MAX_STUDENT_AGE', '18', 'Maximum student age for admission', 'SYSTEM'),
    ('ACADEMIC', 'WORKING_DAYS_PER_WEEK', '6', 'Number of working days per week', 'SYSTEM')
ON CONFLICT (category, key) DO NOTHING;

-- Financial Settings
INSERT INTO configuration_settings (category, key, value, description, updated_by)
VALUES
    ('FINANCIAL', 'CURRENCY_CODE', 'INR', 'Currency code (ISO 4217)', 'SYSTEM'),
    ('FINANCIAL', 'CURRENCY_SYMBOL', '₹', 'Currency symbol', 'SYSTEM'),
    ('FINANCIAL', 'TAX_RATE_PERCENT', '0', 'Default tax rate percentage', 'SYSTEM'),
    ('FINANCIAL', 'LATE_FEE_GRACE_DAYS', '7', 'Grace period for late fee in days', 'SYSTEM')
ON CONFLICT (category, key) DO NOTHING;

-- View: Settings by Category
CREATE OR REPLACE VIEW v_settings_by_category AS
SELECT
    category,
    json_agg(
        json_build_object(
            'settingId', setting_id,
            'key', key,
            'value', value,
            'description', description,
            'updatedAt', updated_at,
            'updatedBy', updated_by
        )
        ORDER BY key
    ) AS settings
FROM configuration_settings
GROUP BY category;

COMMENT ON VIEW v_settings_by_category IS 'Configuration settings grouped by category as JSON';

-- View: All Settings as JSON
CREATE OR REPLACE VIEW v_all_settings_json AS
SELECT
    json_build_object(
        'schoolProfile', (
            SELECT json_build_object(
                'schoolName', school_name,
                'schoolCode', school_code,
                'logoPath', logo_path,
                'address', address,
                'phone', phone,
                'email', email
            )
            FROM school_profile WHERE id = 1
        ),
        'settings', (
            SELECT json_object_agg(
                category,
                settings
            )
            FROM v_settings_by_category
        )
    ) AS configuration;

-- ============================================================================
-- DATABASE INFORMATION
-- ============================================================================

-- Display database setup information
DO $$
DECLARE
    student_count INTEGER;
    config_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO student_count FROM students;
    SELECT COUNT(*) INTO config_count FROM configuration_settings;

    RAISE NOTICE '============================================';
    RAISE NOTICE 'School Management System Database Setup';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Student Service Database: READY';
    RAISE NOTICE '  - Students table created';
    RAISE NOTICE '  - Sample students inserted: %', student_count;
    RAISE NOTICE '  - Indexes created: 9';
    RAISE NOTICE '  - Views created: 1';
    RAISE NOTICE '';
    RAISE NOTICE 'Configuration Service Database: READY';
    RAISE NOTICE '  - School profile table created';
    RAISE NOTICE '  - Configuration settings table created';
    RAISE NOTICE '  - Settings inserted: %', config_count;
    RAISE NOTICE '  - Indexes created: 3';
    RAISE NOTICE '  - Views created: 2';
    RAISE NOTICE '============================================';
    RAISE NOTICE 'Database setup completed successfully!';
    RAISE NOTICE '============================================';
END $$;
