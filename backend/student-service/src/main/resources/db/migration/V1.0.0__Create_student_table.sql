-- Create student table
CREATE TABLE IF NOT EXISTS student (
    -- Primary Key
    student_id          BIGSERIAL PRIMARY KEY,

    -- Business Key (Human-readable, indexed)
    student_key         VARCHAR(50) NOT NULL UNIQUE,

    -- Personal Information (Required)
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    date_of_birth       DATE NOT NULL,

    -- Contact Information
    mobile              VARCHAR(15) NOT NULL UNIQUE,
    email               VARCHAR(100) UNIQUE,
    address             TEXT,

    -- Guardian Information
    father_name_or_guardian VARCHAR(100),
    mother_name         VARCHAR(100),

    -- Identification
    identification_mark VARCHAR(200),
    adhaar_number       VARCHAR(12) UNIQUE,

    -- Status Management
    status              VARCHAR(20) NOT NULL DEFAULT 'Active',

    -- Optimistic Locking
    version             INTEGER NOT NULL DEFAULT 0,

    -- Audit Columns
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          VARCHAR(50),
    updated_by          VARCHAR(50),

    -- Constraints
    CONSTRAINT chk_student_status CHECK (status IN ('Active', 'Inactive')),
    CONSTRAINT chk_mobile_format CHECK (mobile ~ '^\+?[0-9]{10,15}$'),
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_adhaar_format CHECK (adhaar_number IS NULL OR adhaar_number ~ '^[0-9]{12}$'),
    CONSTRAINT chk_dob_valid CHECK (date_of_birth <= CURRENT_DATE)
);

-- Indexes for performance
CREATE INDEX idx_student_last_name ON student(last_name);
CREATE INDEX idx_student_guardian ON student(father_name_or_guardian);
CREATE INDEX idx_student_mobile ON student(mobile);
CREATE INDEX idx_student_status ON student(status);
CREATE INDEX idx_student_created_at ON student(created_at DESC);
CREATE INDEX idx_student_composite_search ON student(last_name, first_name);

-- Trigger for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_student_updated_at
    BEFORE UPDATE ON student
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE student IS 'Stores student registration and profile information';
COMMENT ON COLUMN student.student_key IS 'Human-readable business key (e.g., STU-2025-0001)';
COMMENT ON COLUMN student.status IS 'Current enrollment status: Active or Inactive';
COMMENT ON COLUMN student.version IS 'Optimistic locking version for concurrent updates';
