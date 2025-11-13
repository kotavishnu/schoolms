-- Migration V7: Create students and guardians tables
-- Author: School Management System
-- Date: 2025-11-11
-- Description: Creates tables for student management with encrypted PII fields

-- =====================================================
-- Students Table
-- =====================================================

CREATE TABLE IF NOT EXISTS students (
    id BIGSERIAL PRIMARY KEY,
    student_code VARCHAR(20) NOT NULL UNIQUE,

    -- Encrypted PII fields
    first_name_encrypted BYTEA NOT NULL,
    last_name_encrypted BYTEA NOT NULL,
    date_of_birth_encrypted BYTEA NOT NULL,
    gender VARCHAR(10),
    email_encrypted BYTEA,
    mobile_encrypted BYTEA NOT NULL,
    mobile_hash VARCHAR(64) NOT NULL,
    address_encrypted BYTEA,

    -- Non-encrypted fields
    blood_group VARCHAR(5),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    admission_date DATE NOT NULL,
    photo_url VARCHAR(255),

    -- Audit fields (foreign keys to users table)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by BIGINT NOT NULL,

    -- Constraints
    CONSTRAINT fk_students_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_students_updated_by FOREIGN KEY (updated_by) REFERENCES users(id),
    CONSTRAINT chk_students_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'GRADUATED', 'TRANSFERRED', 'WITHDRAWN')),
    CONSTRAINT chk_students_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER'))
);

-- Indexes for students table
--CREATE INDEX idx_students_student_code ON students(student_code);
--CREATE INDEX idx_students_status ON students(status);
--CREATE INDEX idx_students_mobile_hash ON students(mobile_hash);
--CREATE INDEX idx_students_admission_date ON students(admission_date);
--CREATE INDEX idx_students_created_at ON students(created_at);

-- Partial index for active students (frequently queried)
--CREATE INDEX idx_students_active ON students(id) WHERE status = 'ACTIVE';

COMMENT ON TABLE students IS 'Stores student information with encrypted PII data';
COMMENT ON COLUMN students.student_code IS 'Unique student identifier (STU-YYYY-NNNNN)';
COMMENT ON COLUMN students.mobile_hash IS 'SHA-256 hash of mobile number for searchability';
COMMENT ON COLUMN students.status IS 'Current status of the student';

-- =====================================================
-- Guardians Table
-- =====================================================

CREATE TABLE IF NOT EXISTS guardians (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    relationship VARCHAR(20) NOT NULL,

    -- Encrypted PII fields
    first_name_encrypted BYTEA NOT NULL,
    last_name_encrypted BYTEA NOT NULL,
    mobile_encrypted BYTEA NOT NULL,
    mobile_hash VARCHAR(64) NOT NULL,
    email_encrypted BYTEA,

    -- Non-encrypted fields
    occupation VARCHAR(100),
    is_primary BOOLEAN NOT NULL DEFAULT false,

    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by BIGINT NOT NULL,

    -- Constraints
    CONSTRAINT fk_guardians_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_guardians_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_guardians_updated_by FOREIGN KEY (updated_by) REFERENCES users(id),
    CONSTRAINT chk_guardians_relationship CHECK (relationship IN ('FATHER', 'MOTHER', 'GUARDIAN', 'OTHER'))
);

-- Indexes for guardians table
--CREATE INDEX idx_guardians_student_id ON guardians(student_id);
--CREATE INDEX idx_guardians_is_primary ON guardians(is_primary);
--CREATE INDEX idx_guardians_mobile_hash ON guardians(mobile_hash);

COMMENT ON TABLE guardians IS 'Stores guardian/parent information for students';
COMMENT ON COLUMN guardians.is_primary IS 'Indicates if this is the primary contact guardian';
COMMENT ON COLUMN guardians.relationship IS 'Relationship type (FATHER, MOTHER, GUARDIAN, OTHER)';

-- =====================================================
-- Enrollments Table (Placeholder)
-- =====================================================

CREATE TABLE IF NOT EXISTS enrollments (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    enrollment_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ENROLLED',

    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by BIGINT NOT NULL,

    -- Constraints
    CONSTRAINT fk_enrollments_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_enrollments_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_enrollments_updated_by FOREIGN KEY (updated_by) REFERENCES users(id),
    CONSTRAINT chk_enrollments_status CHECK (status IN ('ENROLLED', 'PROMOTED', 'WITHDRAWN'))
);

--CREATE INDEX idx_enrollments_student_id ON enrollments(student_id);
--CREATE INDEX idx_enrollments_status ON enrollments(status);

COMMENT ON TABLE enrollments IS 'Tracks student enrollments in classes';

-- =====================================================
-- Fee Journals Table (Placeholder)
-- =====================================================

CREATE TABLE IF NOT EXISTS fee_journals (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,

    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_by BIGINT NOT NULL,

    -- Constraints
    CONSTRAINT fk_fee_journals_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_fee_journals_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_fee_journals_updated_by FOREIGN KEY (updated_by) REFERENCES users(id),
    CONSTRAINT chk_fee_journals_amount CHECK (amount >= 0)
);

--CREATE INDEX idx_fee_journals_student_id ON fee_journals(student_id);

COMMENT ON TABLE fee_journals IS 'Tracks fee transactions for students';

-- =====================================================
-- Audit Triggers
-- =====================================================

-- Trigger to automatically update updated_at timestamp on students
CREATE OR REPLACE FUNCTION update_students_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_students_updated_at
    BEFORE UPDATE ON students
    FOR EACH ROW
    EXECUTE FUNCTION update_students_updated_at();

-- Trigger to automatically update updated_at timestamp on guardians
CREATE OR REPLACE FUNCTION update_guardians_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_guardians_updated_at
    BEFORE UPDATE ON guardians
    FOR EACH ROW
    EXECUTE FUNCTION update_guardians_updated_at();

-- =====================================================
-- Insert seed data (if needed for development)
-- =====================================================

-- Note: Seed data will be added separately in V8 migration if needed

-- =====================================================
-- Grants (Optional - adjust based on your security model)
-- =====================================================

-- GRANT SELECT, INSERT, UPDATE, DELETE ON students TO school_app_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON guardians TO school_app_user;

-- =====================================================
-- Rollback Script (for reference - not executed)
-- =====================================================

-- To rollback this migration manually:
-- DROP TABLE IF EXISTS fee_journals CASCADE;
-- DROP TABLE IF EXISTS enrollments CASCADE;
-- DROP TABLE IF EXISTS guardians CASCADE;
-- DROP TABLE IF EXISTS students CASCADE;
-- DROP FUNCTION IF EXISTS update_students_updated_at() CASCADE;
-- DROP FUNCTION IF EXISTS update_guardians_updated_at() CASCADE;
