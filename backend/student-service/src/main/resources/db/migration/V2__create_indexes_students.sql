-- ============================================================================
-- Student Service Migration V2: Create indexes
-- Description: Creates all necessary indexes for performance optimization
-- ============================================================================

-- Unique Constraints
CREATE UNIQUE INDEX idx_students_mobile ON students(mobile);
CREATE UNIQUE INDEX idx_students_aadhaar ON students(aadhaar_number)
    WHERE aadhaar_number IS NOT NULL;
CREATE UNIQUE INDEX idx_students_email ON students(email)
    WHERE email IS NOT NULL;

-- Performance Indexes
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_created_at ON students(created_at DESC);
CREATE INDEX idx_students_last_name ON students(last_name);
CREATE INDEX idx_students_first_name ON students(first_name);
CREATE INDEX idx_students_dob ON students(date_of_birth);
CREATE INDEX idx_students_caste ON students(caste) WHERE caste IS NOT NULL;

-- Composite index for common searches
CREATE INDEX idx_students_name_status ON students(last_name, first_name, status);
