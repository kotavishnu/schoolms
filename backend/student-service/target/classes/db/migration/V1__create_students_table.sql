-- ============================================================================
-- Student Service Migration V1: Create students table
-- Description: Creates the students table with all necessary constraints
-- ============================================================================

-- Create sequence for student ID generation
CREATE SEQUENCE IF NOT EXISTS student_id_seq
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;

-- Create students table
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

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to automatically update updated_at
CREATE TRIGGER trg_students_updated_at
    BEFORE UPDATE ON students
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

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

-- Comments
COMMENT ON TABLE students IS 'Stores student profile information';
COMMENT ON COLUMN students.student_id IS 'Auto-generated unique student ID (format: STU-YYYY-NNNNN)';
COMMENT ON COLUMN students.mobile IS 'Unique 10-digit mobile number';
COMMENT ON COLUMN students.aadhaar_number IS 'Optional 12-digit Aadhaar number';
COMMENT ON COLUMN students.status IS 'Student status: ACTIVE or INACTIVE';
COMMENT ON COLUMN students.version IS 'Optimistic locking version number';
