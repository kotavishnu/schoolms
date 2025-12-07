# Database Design

## 1. Overview

This document defines the complete database schema for the School Management System (SMS). The design follows PostgreSQL best practices and supports the microservices architecture with separate databases for Student and Configuration services.

## 2. Database Standards

### 2.1 Naming Conventions
```yaml
Tables: snake_case, plural (e.g., students, configuration_settings)
Columns: snake_case (e.g., first_name, date_of_birth)
Primary Keys: BIGSERIAL type, named 'id'
Foreign Keys: referenced_table_id (e.g., student_id)
Indexes: idx_table_column (e.g., idx_students_mobile)
Constraints:
  - Primary Key: pk_table_name
  - Foreign Key: fk_table_referenced_table
  - Unique: uk_table_column
  - Check: ck_table_column
```

### 2.2 Audit Columns (Standard for All Tables)
```sql
created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
created_by    VARCHAR(100)
updated_by    VARCHAR(100)
version       BIGINT NOT NULL DEFAULT 0  -- For Optimistic Locking
```

### 2.3 Data Types Standards
```yaml
IDs: BIGSERIAL
Names: VARCHAR with appropriate length
Mobile: VARCHAR(15)
Email: VARCHAR(255)
Dates: DATE
Timestamps: TIMESTAMP WITH TIME ZONE
Status/Enums: VARCHAR(20) with CHECK constraint
Large Text: TEXT
```

## 3. Entity Relationship Diagram

```mermaid
erDiagram
    STUDENTS ||--o{ ENROLLMENT_HISTORY : has
    STUDENTS {
        BIGSERIAL id PK
        VARCHAR student_id UK "Auto-generated unique ID"
        VARCHAR first_name "NOT NULL"
        VARCHAR last_name "NOT NULL"
        DATE date_of_birth "NOT NULL, Age 3-18"
        VARCHAR mobile UK "NOT NULL, Unique"
        VARCHAR fathers_name
        VARCHAR mothers_name
        VARCHAR identification_mark
        VARCHAR aadhaar_number UK "Unique if provided"
        VARCHAR email
        VARCHAR address TEXT
        VARCHAR status "DEFAULT 'ACTIVE', CHECK IN ('ACTIVE','INACTIVE')"
        BIGINT version "Optimistic locking"
        TIMESTAMP created_at
        TIMESTAMP updated_at
        VARCHAR created_by
        VARCHAR updated_by
    }

    ENROLLMENT_HISTORY {
        BIGSERIAL id PK
        BIGINT student_id FK "NOT NULL"
        VARCHAR academic_year "NOT NULL"
        VARCHAR grade_class "NOT NULL"
        VARCHAR section
        DATE enrollment_date "NOT NULL"
        DATE withdrawal_date
        VARCHAR status "CHECK IN ('ENROLLED','COMPLETED','WITHDRAWN')"
        TEXT remarks
        BIGINT version
        TIMESTAMP created_at
        TIMESTAMP updated_at
        VARCHAR created_by
        VARCHAR updated_by
    }

    CONFIGURATION_SETTINGS {
        BIGSERIAL id PK
        VARCHAR category "NOT NULL, CHECK IN ('GENERAL','ACADEMIC','FINANCIAL')"
        VARCHAR config_key UK "NOT NULL, Unique per category"
        VARCHAR config_value "NOT NULL"
        VARCHAR description
        VARCHAR data_type "CHECK IN ('STRING','NUMBER','BOOLEAN','JSON')"
        BOOLEAN is_encrypted "DEFAULT FALSE"
        BIGINT version
        TIMESTAMP created_at
        TIMESTAMP updated_at
        VARCHAR created_by
        VARCHAR updated_by
    }
```

## 4. Student Service Database Schema

### 4.1 Students Table

```sql
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
    CONSTRAINT ck_students_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT ck_students_dob CHECK (
        date_of_birth >= CURRENT_DATE - INTERVAL '18 years' AND
        date_of_birth <= CURRENT_DATE - INTERVAL '3 years'
    )
);

-- Indexes for Performance
CREATE INDEX idx_students_last_name ON students(last_name);
CREATE INDEX idx_students_fathers_name ON students(fathers_name);
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_mobile ON students(mobile);
CREATE INDEX idx_students_created_at ON students(created_at DESC);

-- Comments
COMMENT ON TABLE students IS 'Stores student registration and profile information';
COMMENT ON COLUMN students.student_id IS 'Auto-generated unique student identifier (format: STD-YYYYMMDD-NNNN)';
COMMENT ON COLUMN students.date_of_birth IS 'Student date of birth - must be 3-18 years old at registration';
COMMENT ON COLUMN students.mobile IS 'Unique mobile number per student';
COMMENT ON COLUMN students.status IS 'Student status: ACTIVE or INACTIVE';
COMMENT ON COLUMN students.version IS 'Version for optimistic locking to prevent concurrent update conflicts';
```

### 4.2 Enrollment History Table

```sql
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

-- Indexes for Performance
CREATE INDEX idx_enrollment_student_id ON enrollment_history(student_id);
CREATE INDEX idx_enrollment_academic_year ON enrollment_history(academic_year);
CREATE INDEX idx_enrollment_status ON enrollment_history(status);
CREATE INDEX idx_enrollment_date ON enrollment_history(enrollment_date DESC);

-- Comments
COMMENT ON TABLE enrollment_history IS 'Tracks student enrollment across academic years and grade levels';
COMMENT ON COLUMN enrollment_history.academic_year IS 'Academic year in format YYYY-YYYY (e.g., 2024-2025)';
COMMENT ON COLUMN enrollment_history.status IS 'Enrollment status: ENROLLED, COMPLETED, or WITHDRAWN';
```

### 4.3 Student ID Generation Sequence

```sql
-- ============================================================================
-- Sequence: student_id_sequence
-- Purpose: Generate unique student IDs
-- ============================================================================

CREATE SEQUENCE student_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;

COMMENT ON SEQUENCE student_id_sequence IS 'Sequence for generating student IDs';

-- Function to generate student ID
CREATE OR REPLACE FUNCTION generate_student_id()
RETURNS VARCHAR(20) AS $$
DECLARE
    next_id BIGINT;
    student_id VARCHAR(20);
BEGIN
    next_id := nextval('student_id_sequence');
    student_id := 'STD-' || TO_CHAR(CURRENT_DATE, 'YYYYMMDD') || '-' || LPAD(next_id::TEXT, 4, '0');
    RETURN student_id;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION generate_student_id() IS 'Generates unique student ID in format STD-YYYYMMDD-NNNN';
```

### 4.4 Trigger for Updated Timestamp

```sql
-- ============================================================================
-- Function: update_updated_at_column
-- Purpose: Automatically update updated_at timestamp
-- ============================================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply to students table
CREATE TRIGGER tr_students_updated_at
    BEFORE UPDATE ON students
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Apply to enrollment_history table
CREATE TRIGGER tr_enrollment_updated_at
    BEFORE UPDATE ON enrollment_history
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

## 5. Configuration Service Database Schema

### 5.1 Configuration Settings Table

```sql
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

-- Indexes for Performance
CREATE INDEX idx_config_category ON configuration_settings(category);
CREATE INDEX idx_config_key ON configuration_settings(config_key);
CREATE INDEX idx_config_category_key ON configuration_settings(category, config_key);

-- Trigger for updated_at
CREATE TRIGGER tr_config_updated_at
    BEFORE UPDATE ON configuration_settings
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE configuration_settings IS 'Stores school configuration as categorized key-value pairs';
COMMENT ON COLUMN configuration_settings.category IS 'Configuration category: GENERAL, ACADEMIC, or FINANCIAL';
COMMENT ON COLUMN configuration_settings.data_type IS 'Data type: STRING, NUMBER, BOOLEAN, or JSON';
COMMENT ON COLUMN configuration_settings.is_encrypted IS 'Indicates if the value is stored encrypted';
```

## 6. Business Rules Enforcement

### 6.1 Age Validation (BR-1)

```sql
-- Enforced via CHECK constraint on students table
CONSTRAINT ck_students_dob CHECK (
    date_of_birth >= CURRENT_DATE - INTERVAL '18 years' AND
    date_of_birth <= CURRENT_DATE - INTERVAL '3 years'
)

-- Query to validate age at registration
SELECT
    id,
    student_id,
    first_name,
    last_name,
    date_of_birth,
    EXTRACT(YEAR FROM AGE(CURRENT_DATE, date_of_birth)) AS age
FROM students
WHERE
    EXTRACT(YEAR FROM AGE(CURRENT_DATE, date_of_birth)) NOT BETWEEN 3 AND 18;
```

### 6.2 Mobile Uniqueness (BR-2)

```sql
-- Enforced via UNIQUE constraint
CONSTRAINT uk_students_mobile UNIQUE (mobile)

-- Query to check for duplicate mobiles
SELECT mobile, COUNT(*) as count
FROM students
GROUP BY mobile
HAVING COUNT(*) > 1;
```

### 6.3 Class Capacity (BR-3)

```sql
-- Future implementation - Class capacity tracking
-- This will be implemented when class management is added in future phases

CREATE TABLE classes (
    id              BIGSERIAL PRIMARY KEY,
    grade_class     VARCHAR(20) NOT NULL,
    section         VARCHAR(10) NOT NULL,
    academic_year   VARCHAR(9) NOT NULL,
    capacity        INTEGER NOT NULL DEFAULT 30,
    current_count   INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uk_class_year_section UNIQUE (grade_class, section, academic_year),
    CONSTRAINT ck_class_capacity CHECK (current_count <= capacity)
);
```

## 7. Database Queries - Common Operations

### 7.1 Student Registration

```sql
-- Insert new student with generated student_id
INSERT INTO students (
    student_id,
    first_name,
    last_name,
    date_of_birth,
    mobile,
    email,
    address,
    fathers_name,
    mothers_name,
    identification_mark,
    aadhaar_number,
    status,
    created_by
) VALUES (
    generate_student_id(),
    'John',
    'Doe',
    '2010-05-15',
    '9876543210',
    'john.doe@example.com',
    '123 Main St, City',
    'Richard Doe',
    'Jane Doe',
    'Mole on left arm',
    '123456789012',
    'ACTIVE',
    'admin'
) RETURNING id, student_id;
```

### 7.2 Student Search Queries

```sql
-- Search by last name
SELECT
    id,
    student_id,
    first_name,
    last_name,
    mobile,
    status,
    created_at
FROM students
WHERE last_name ILIKE '%doe%'
ORDER BY last_name, first_name
LIMIT 20 OFFSET 0;

-- Search by father's/guardian's name
SELECT
    id,
    student_id,
    first_name,
    last_name,
    fathers_name,
    mobile,
    status
FROM students
WHERE fathers_name ILIKE '%richard%'
ORDER BY last_name, first_name
LIMIT 20 OFFSET 0;

-- Search by student ID
SELECT
    id,
    student_id,
    first_name,
    last_name,
    date_of_birth,
    mobile,
    email,
    address,
    fathers_name,
    mothers_name,
    identification_mark,
    aadhaar_number,
    status,
    version,
    created_at,
    updated_at
FROM students
WHERE student_id = 'STD-20241206-0001';

-- Active students only
SELECT
    id,
    student_id,
    first_name,
    last_name,
    mobile,
    status
FROM students
WHERE status = 'ACTIVE'
ORDER BY created_at DESC
LIMIT 50;
```

### 7.3 Student Update

```sql
-- Update allowed fields only (name, mobile, status)
UPDATE students
SET
    first_name = 'John',
    last_name = 'Smith',
    mobile = '9876543211',
    status = 'INACTIVE',
    updated_by = 'admin',
    version = version + 1
WHERE
    id = 1
    AND version = 0  -- Optimistic locking check
RETURNING id, student_id, version;

-- If no rows affected, version mismatch occurred (409 Conflict)
```

### 7.4 Enrollment History

```sql
-- Get student enrollment history
SELECT
    e.id,
    e.academic_year,
    e.grade_class,
    e.section,
    e.enrollment_date,
    e.withdrawal_date,
    e.status,
    e.remarks
FROM enrollment_history e
WHERE e.student_id = 1
ORDER BY e.academic_year DESC;

-- Insert new enrollment
INSERT INTO enrollment_history (
    student_id,
    academic_year,
    grade_class,
    section,
    enrollment_date,
    status,
    created_by
) VALUES (
    1,
    '2024-2025',
    'Grade 5',
    'A',
    CURRENT_DATE,
    'ENROLLED',
    'admin'
);
```

### 7.5 Configuration Management

```sql
-- Get all configurations by category
SELECT
    id,
    category,
    config_key,
    config_value,
    description,
    data_type,
    updated_at
FROM configuration_settings
WHERE category = 'GENERAL'
ORDER BY config_key;

-- Insert or update configuration (UPSERT)
INSERT INTO configuration_settings (
    category,
    config_key,
    config_value,
    description,
    data_type,
    created_by
) VALUES (
    'GENERAL',
    'school.name',
    'ABC High School',
    'Official school name',
    'STRING',
    'admin'
)
ON CONFLICT (category, config_key)
DO UPDATE SET
    config_value = EXCLUDED.config_value,
    updated_by = EXCLUDED.created_by,
    updated_at = CURRENT_TIMESTAMP,
    version = configuration_settings.version + 1;

-- Get single configuration value
SELECT config_value
FROM configuration_settings
WHERE category = 'GENERAL'
  AND config_key = 'school.name';
```

## 8. Database Constraints Summary

### 8.1 Students Table Constraints

| Constraint Type | Name | Description |
|----------------|------|-------------|
| PRIMARY KEY | pk_students | id column |
| UNIQUE | uk_students_student_id | student_id must be unique |
| UNIQUE | uk_students_mobile | mobile must be unique |
| UNIQUE | uk_students_aadhaar | aadhaar_number must be unique (if provided) |
| CHECK | ck_students_status | status IN ('ACTIVE', 'INACTIVE') |
| CHECK | ck_students_dob | Age between 3 and 18 years |
| NOT NULL | - | id, student_id, first_name, last_name, date_of_birth, mobile, status, version, created_at, updated_at |

### 8.2 Enrollment History Constraints

| Constraint Type | Name | Description |
|----------------|------|-------------|
| PRIMARY KEY | pk_enrollment_history | id column |
| FOREIGN KEY | fk_enrollment_student | student_id references students(id) ON DELETE CASCADE |
| UNIQUE | uk_enrollment_student_year | One enrollment per student per academic year |
| CHECK | ck_enrollment_status | status IN ('ENROLLED', 'COMPLETED', 'WITHDRAWN') |
| CHECK | ck_enrollment_dates | withdrawal_date >= enrollment_date (if set) |
| NOT NULL | - | id, student_id, academic_year, grade_class, enrollment_date, status, version, created_at, updated_at |

### 8.3 Configuration Settings Constraints

| Constraint Type | Name | Description |
|----------------|------|-------------|
| PRIMARY KEY | pk_configuration_settings | id column |
| UNIQUE | uk_config_category_key | Unique config_key per category |
| CHECK | ck_config_category | category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL') |
| CHECK | ck_config_data_type | data_type IN ('STRING', 'NUMBER', 'BOOLEAN', 'JSON') |
| NOT NULL | - | id, category, config_key, config_value, data_type, is_encrypted, version, created_at, updated_at |

## 9. Indexes Strategy

### 9.1 Performance Indexes

```sql
-- Student Service Indexes
CREATE INDEX idx_students_last_name ON students(last_name);
CREATE INDEX idx_students_fathers_name ON students(fathers_name);
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_mobile ON students(mobile);
CREATE INDEX idx_students_created_at ON students(created_at DESC);

CREATE INDEX idx_enrollment_student_id ON enrollment_history(student_id);
CREATE INDEX idx_enrollment_academic_year ON enrollment_history(academic_year);
CREATE INDEX idx_enrollment_status ON enrollment_history(status);
CREATE INDEX idx_enrollment_date ON enrollment_history(enrollment_date DESC);

-- Configuration Service Indexes
CREATE INDEX idx_config_category ON configuration_settings(category);
CREATE INDEX idx_config_key ON configuration_settings(config_key);
CREATE INDEX idx_config_category_key ON configuration_settings(category, config_key);
```

### 9.2 Index Rationale

| Index | Purpose | Expected Usage |
|-------|---------|----------------|
| idx_students_last_name | Student search by last name | High - primary search method |
| idx_students_fathers_name | Student search by guardian | Medium - secondary search method |
| idx_students_status | Filter active/inactive students | High - frequent filtering |
| idx_students_mobile | Student lookup by mobile | Medium - verification/login |
| idx_students_created_at | Recent registrations | Medium - reporting |
| idx_enrollment_student_id | Student enrollment history | High - frequent joins |
| idx_enrollment_academic_year | Current year enrollments | High - academic year filtering |
| idx_config_category | Category-based config retrieval | High - primary access pattern |

## 10. Database Migration Strategy

### 10.1 Version Control
```yaml
Tool: Flyway
Location: src/main/resources/db/migration/
Naming: V{version}__{description}.sql
  - V1.0__create_students_table.sql
  - V1.1__create_enrollment_history_table.sql
  - V1.2__create_configuration_settings_table.sql
  - V1.3__add_indexes.sql
  - V1.4__create_functions_triggers.sql
```

### 10.2 Rollback Strategy
```yaml
Approach: Separate rollback scripts (optional)
Naming: U{version}__{description}.sql
Testing: All migrations tested in dev environment first
Backup: Full database backup before production migration
```

## 11. Sample Data

### 11.1 Sample Students

```sql
-- Sample student data for testing
INSERT INTO students (student_id, first_name, last_name, date_of_birth, mobile, fathers_name, mothers_name, email, address, status, created_by)
VALUES
    (generate_student_id(), 'John', 'Doe', '2010-05-15', '9876543210', 'Richard Doe', 'Jane Doe', 'john.doe@example.com', '123 Main St', 'ACTIVE', 'system'),
    (generate_student_id(), 'Jane', 'Smith', '2012-08-22', '9876543211', 'Robert Smith', 'Mary Smith', 'jane.smith@example.com', '456 Oak Ave', 'ACTIVE', 'system'),
    (generate_student_id(), 'Michael', 'Johnson', '2011-03-10', '9876543212', 'William Johnson', 'Patricia Johnson', 'michael.j@example.com', '789 Pine Rd', 'ACTIVE', 'system'),
    (generate_student_id(), 'Emily', 'Williams', '2013-11-30', '9876543213', 'James Williams', 'Linda Williams', 'emily.w@example.com', '321 Elm St', 'INACTIVE', 'system');
```

### 11.2 Sample Configuration

```sql
-- Sample configuration data
INSERT INTO configuration_settings (category, config_key, config_value, description, data_type, created_by)
VALUES
    ('GENERAL', 'school.name', 'ABC High School', 'Official school name', 'STRING', 'system'),
    ('GENERAL', 'school.code', 'ABC-001', 'School identification code', 'STRING', 'system'),
    ('GENERAL', 'school.address', '123 Education Lane, City, State 12345', 'School physical address', 'STRING', 'system'),
    ('ACADEMIC', 'current.academic.year', '2024-2025', 'Current academic year', 'STRING', 'system'),
    ('ACADEMIC', 'class.capacity.default', '30', 'Default class capacity', 'NUMBER', 'system'),
    ('FINANCIAL', 'currency', 'INR', 'Currency code', 'STRING', 'system'),
    ('FINANCIAL', 'fee.payment.deadline', '10', 'Fee payment deadline (day of month)', 'NUMBER', 'system');
```

## 12. Performance Optimization

### 12.1 Query Optimization Guidelines

```sql
-- Use EXPLAIN ANALYZE to check query performance
EXPLAIN ANALYZE
SELECT s.*, e.academic_year, e.grade_class
FROM students s
LEFT JOIN enrollment_history e ON s.id = e.student_id
WHERE s.status = 'ACTIVE'
  AND e.academic_year = '2024-2025';

-- Expected: Index scans, not sequential scans
-- Target: <50ms for most queries
```

### 12.2 Connection Pooling (HikariCP)

```yaml
# Application configuration
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      pool-name: SMS-HikariCP
```

### 12.3 N+1 Query Prevention

```java
// Use EntityGraph to fetch related data in single query
@EntityGraph(attributePaths = {"enrollmentHistory"})
@Query("SELECT s FROM Student s WHERE s.status = :status")
List<Student> findActiveStudentsWithEnrollments(@Param("status") String status);
```

## 13. Backup and Recovery

### 13.1 Backup Strategy

```yaml
Full Backup:
  Frequency: Daily at 2:00 AM
  Retention: 30 days
  Tool: pg_dump

Incremental Backup:
  Frequency: Every 6 hours
  Retention: 7 days
  Tool: WAL archiving

Point-in-Time Recovery:
  Enabled: Yes
  WAL Archive Location: /backup/wal_archive
```

### 13.2 Backup Commands

```bash
# Full database backup
pg_dump -h localhost -U postgres -d sms_student_db -F c -f sms_student_$(date +%Y%m%d).dump

pg_dump -h localhost -U postgres -d sms_config_db -F c -f sms_config_$(date +%Y%m%d).dump

# Restore from backup
pg_restore -h localhost -U postgres -d sms_student_db -c sms_student_20241206.dump
```

## 14. Monitoring and Maintenance

### 14.1 Database Health Checks

```sql
-- Check table sizes
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Check index usage
SELECT
    schemaname,
    tablename,
    indexname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes
ORDER BY idx_scan ASC;

-- Check slow queries
SELECT
    query,
    calls,
    total_time,
    mean_time,
    max_time
FROM pg_stat_statements
ORDER BY mean_time DESC
LIMIT 10;
```

### 14.2 Maintenance Tasks

```sql
-- Vacuum and analyze (run weekly)
VACUUM ANALYZE students;
VACUUM ANALYZE enrollment_history;
VACUUM ANALYZE configuration_settings;

-- Reindex (run monthly)
REINDEX TABLE students;
REINDEX TABLE enrollment_history;
REINDEX TABLE configuration_settings;
```

## 15. Security Considerations

### 15.1 Database Users and Roles

```sql
-- Create application user with limited privileges
CREATE ROLE sms_app_user WITH LOGIN PASSWORD 'secure_password';

-- Grant only necessary privileges
GRANT CONNECT ON DATABASE sms_student_db TO sms_app_user;
GRANT USAGE ON SCHEMA public TO sms_app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO sms_app_user;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO sms_app_user;

-- Revoke dangerous privileges
REVOKE CREATE ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
```

### 15.2 Sensitive Data

```yaml
Encrypted Columns:
  - aadhaar_number (PII)
  - configuration_settings.config_value (when is_encrypted = true)

Access Control:
  - Use application-level encryption for sensitive data
  - Audit logs for all access to sensitive columns
  - Mask data in non-production environments
```

## 16. Compliance and Audit

### 16.1 Audit Trail

```sql
-- All tables include audit columns:
created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
created_by    VARCHAR(100)
updated_by    VARCHAR(100)
version       BIGINT NOT NULL DEFAULT 0

-- Optional: Create audit log table for detailed tracking
CREATE TABLE audit_log (
    id              BIGSERIAL PRIMARY KEY,
    table_name      VARCHAR(100) NOT NULL,
    record_id       BIGINT NOT NULL,
    operation       VARCHAR(10) NOT NULL, -- INSERT, UPDATE, DELETE
    old_values      JSONB,
    new_values      JSONB,
    changed_by      VARCHAR(100),
    changed_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## 17. Future Schema Enhancements

### 17.1 Phase 2 Additions

```sql
-- Classes table for capacity management
CREATE TABLE classes (
    id              BIGSERIAL PRIMARY KEY,
    grade_class     VARCHAR(20) NOT NULL,
    section         VARCHAR(10) NOT NULL,
    academic_year   VARCHAR(9) NOT NULL,
    capacity        INTEGER NOT NULL DEFAULT 30,
    current_count   INTEGER NOT NULL DEFAULT 0,
    -- ... audit columns
);

-- Teachers table
CREATE TABLE teachers (
    id              BIGSERIAL PRIMARY KEY,
    teacher_id      VARCHAR(20) NOT NULL UNIQUE,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    mobile          VARCHAR(15) NOT NULL UNIQUE,
    -- ... audit columns
);

-- User authentication table
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(20) NOT NULL,
    -- ... audit columns
);
```

---

**Document Version:** 1.0
**Last Updated:** 2025-12-06
**Status:** APPROVED
