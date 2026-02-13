-- =============================================================================
-- School Management System - Single PostgreSQL DDL Script
-- Scope: Student Service (student_db) + Configuration Service (config_db)
-- Constraints:
--   - Tables, Primary Keys, Foreign Keys, and Basic Constraints only
--   - Indexes included for search correctness
--   - Triggers EXCLUDED per planning constraints
--   - No Flyway / migration tooling references
--   - Clean re-run: all DROP IF EXISTS statements at top
-- =============================================================================

-- -----------------------------------------------------------------------------
-- STUDENT SERVICE DATABASE  (student_db, port 5432)
-- Run this block against the student_db database
-- -----------------------------------------------------------------------------

DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP SEQUENCE IF EXISTS student_id_seq;

-- Enable UUID extension (reserved for future use, harmless if already present)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Sequence used by the Application Layer to generate the STD-YYYYMMDD-NNNN suffix
CREATE SEQUENCE student_id_seq
    START 1
    INCREMENT 1
    NO CYCLE;

-- Students table
-- Stores the aggregate root for the Student bounded context.
-- student_id is the business key; id is the internal surrogate PK.
CREATE TABLE students (
    id                   BIGSERIAL       PRIMARY KEY,
    student_id           VARCHAR(20)     NOT NULL,
    first_name           VARCHAR(100)    NOT NULL,
    last_name            VARCHAR(100)    NOT NULL,
    date_of_birth        DATE            NOT NULL,
    mobile               CHAR(10)        NOT NULL,
    email                VARCHAR(255),
    address              TEXT,
    fathers_name         VARCHAR(255),
    mothers_name         VARCHAR(255),
    identification_mark  VARCHAR(255),
    aadhaar_number       CHAR(12),
    status               VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    version              INTEGER         NOT NULL DEFAULT 0,
    created_at           TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    -- Business key uniqueness
    CONSTRAINT uq_students_student_id      UNIQUE (student_id),

    -- BR-2: Mobile must be unique across all students
    CONSTRAINT uq_students_mobile          UNIQUE (mobile),

    -- Aadhaar uniqueness when provided
    CONSTRAINT uq_students_aadhaar         UNIQUE (aadhaar_number),

    -- Status domain values
    CONSTRAINT chk_students_status         CHECK (status IN ('ACTIVE', 'INACTIVE')),

    -- Name format: letters and spaces only
    CONSTRAINT chk_students_first_name     CHECK (first_name ~ '^[a-zA-Z ]+$'),
    CONSTRAINT chk_students_last_name      CHECK (last_name  ~ '^[a-zA-Z ]+$'),

    -- Mobile: exactly 10 digits
    CONSTRAINT chk_students_mobile_digits  CHECK (mobile ~ '^\d{10}$'),

    -- Aadhaar: exactly 12 digits when provided
    CONSTRAINT chk_students_aadhaar_digits CHECK (
        aadhaar_number IS NULL OR aadhaar_number ~ '^\d{12}$'
    ),

    -- BR-1: Student age must be at least 3 years at registration
    CONSTRAINT chk_students_dob_min_age    CHECK (
        date_of_birth <= CURRENT_DATE - INTERVAL '3 years'
    ),

    -- BR-1: Student age must be at most 18 years at registration
    CONSTRAINT chk_students_dob_max_age    CHECK (
        date_of_birth >= CURRENT_DATE - INTERVAL '18 years'
    )
);

-- Indexes support search predicates defined in StudentQueryService
CREATE INDEX idx_students_last_name  ON students (last_name);
CREATE INDEX idx_students_status     ON students (status);
CREATE INDEX idx_students_student_id ON students (student_id);


-- Enrollments table
-- Tracks academic year enrollment history per student (BR-7: one record per year).
CREATE TABLE enrollments (
    id               BIGSERIAL       PRIMARY KEY,
    student_fk       BIGINT          NOT NULL,
    student_id       VARCHAR(20)     NOT NULL,
    academic_year    VARCHAR(10)     NOT NULL,
    grade_class      VARCHAR(50)     NOT NULL,
    section          VARCHAR(10)     NOT NULL,
    enrollment_date  DATE            NOT NULL,
    withdrawal_date  DATE,
    status           VARCHAR(20)     NOT NULL DEFAULT 'ENROLLED',
    remarks          TEXT,
    created_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    -- Referential integrity to the students table
    CONSTRAINT fk_enrollments_student
        FOREIGN KEY (student_fk)
        REFERENCES students (id)
        ON DELETE CASCADE,

    -- BR-7: A student may only have one enrollment per academic year
    CONSTRAINT uq_enrollments_student_year
        UNIQUE (student_fk, academic_year),

    -- Status domain values
    CONSTRAINT chk_enrollments_status
        CHECK (status IN ('ENROLLED', 'WITHDRAWN', 'COMPLETED')),

    -- Withdrawal date must be on or after enrollment date
    CONSTRAINT chk_enrollments_dates
        CHECK (withdrawal_date IS NULL OR withdrawal_date >= enrollment_date)
);

-- Indexes support enrollment history queries by student
CREATE INDEX idx_enrollments_student_fk ON enrollments (student_fk);
CREATE INDEX idx_enrollments_student_id ON enrollments (student_id);


-- =============================================================================
-- CONFIGURATION SERVICE DATABASE  (config_db, port 5433)
-- Run this block against the config_db database
-- =============================================================================

DROP TABLE IF EXISTS configuration_settings CASCADE;

-- Configuration settings table
-- Stores school-wide key-value settings grouped by category.
-- Upsert pattern: INSERT ... ON CONFLICT (category, key) DO UPDATE
CREATE TABLE configuration_settings (
    id           BIGSERIAL       PRIMARY KEY,
    category     VARCHAR(20)     NOT NULL,
    key          VARCHAR(100)    NOT NULL,
    value        TEXT            NOT NULL,
    description  TEXT,
    data_type    VARCHAR(10)     NOT NULL DEFAULT 'STRING',
    is_encrypted BOOLEAN         NOT NULL DEFAULT FALSE,
    version      INTEGER         NOT NULL DEFAULT 0,
    updated_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    -- (category, key) composite uniqueness enforces upsert semantics
    CONSTRAINT uq_config_category_key
        UNIQUE (category, key),

    -- Category domain values
    CONSTRAINT chk_config_category
        CHECK (category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL')),

    -- Data type domain values
    CONSTRAINT chk_config_data_type
        CHECK (data_type IN ('STRING', 'NUMBER', 'BOOLEAN', 'JSON'))
);

-- Indexes support category-filtered queries and grouped retrieval
CREATE INDEX idx_config_category ON configuration_settings (category);
CREATE INDEX idx_config_key      ON configuration_settings (key);
