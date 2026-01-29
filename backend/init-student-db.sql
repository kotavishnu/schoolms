-- ========================================
-- Student Service Database Initialization
-- ========================================

-- Drop existing tables if they exist
DROP TABLE IF EXISTS enrollment_history CASCADE;
DROP TABLE IF EXISTS students CASCADE;

-- ========================================
-- TABLE: students
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

-- ========================================
-- TABLE: enrollment_history
-- ========================================

CREATE TABLE enrollment_history (
    -- Primary Key
    id                 BIGSERIAL PRIMARY KEY,

    -- Foreign Key (application-level, no database FK constraint)
    student_id         BIGINT NOT NULL,

    -- Enrollment Details
    academic_year      VARCHAR(20) NOT NULL,
    grade_class        VARCHAR(50) NOT NULL,
    section            VARCHAR(10) NOT NULL,
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

-- Indexes for enrollment_history table
CREATE INDEX idx_enrollments_student_id ON enrollment_history(student_id);
CREATE INDEX idx_enrollments_academic_year ON enrollment_history(academic_year);
CREATE INDEX idx_enrollments_status ON enrollment_history(status);
CREATE INDEX idx_enrollments_enrollment_date ON enrollment_history(enrollment_date);

-- ========================================
-- SEED DATA
-- ========================================

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

INSERT INTO enrollment_history (
    student_id, academic_year, grade_class, section, enrollment_date, status
) VALUES
    ((SELECT id FROM students WHERE student_id = 'STD-20260128-0001'),
     '2025-2026', 'Grade-7', 'Section-A', '2025-06-01', 'ACTIVE'),
    ((SELECT id FROM students WHERE student_id = 'STD-20260128-0002'),
     '2025-2026', 'Grade-6', 'Section-B', '2025-06-01', 'ACTIVE'),
    ((SELECT id FROM students WHERE student_id = 'STD-20260128-0003'),
     '2024-2025', 'Grade-8', 'Section-A', '2024-06-01', 'COMPLETED');
