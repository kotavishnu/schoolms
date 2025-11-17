# Database Design - School Management System

## 1. Overview

### 1.1 Database Selection

**Database**: PostgreSQL 18
**Rationale**:
- ACID compliance for financial transactions
- Advanced features (JSON, window functions, CTE)
- Excellent performance with proper indexing
- Robust replication and backup capabilities
- Open-source with no licensing costs

### 1.2 Design Principles

1. **Normalization**: 3rd Normal Form (3NF) to reduce redundancy
2. **Referential Integrity**: Foreign key constraints enforced
3. **Data Types**: Appropriate types for data (NUMERIC for currency, TIMESTAMP WITH TIME ZONE for dates)

### 1.3 Schema Structure

```
school_sms_db
│
├── public (application tables)
│   ├── students
│   ├── configurations
│   └── users
```

## 3. Table Definitions

### 3.1 User Management

#### 3.1.1 USERS Table

```sql
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    mobile VARCHAR(20),
    role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'PRINCIPAL', 'OFFICE_STAFF', 'ACCOUNTS_MANAGER', 'AUDITOR')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    password_changed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

CREATE INDEX idx_users_username ON users(username);

COMMENT ON TABLE users IS 'System users with role-based access';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';
COMMENT ON COLUMN users.role IS 'User role for RBAC';
```

---

### 3.2 Student Management

#### 3.2.1 STUDENTS Table

```sql
CREATE TABLE students (
    student_id BIGSERIAL PRIMARY KEY,
    student_code VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    date_of_birth VARCHAR(20) NOT NULL,
    gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    email VARCHAR(50),
    mobile VARCHAR(20) NOT NULL,
    father_name VARCHAR(50),
    mother_name VARCHAR(50),
    guardians VARCHAR(50),
    caste VARCHAR(20),
    mole_on_body VARCHAR(100),
    address VARCHAR(20),
    blood_group VARCHAR(5) CHECK (blood_group IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'GRADUATED', 'TRANSFERRED', 'WITHDRAWN')),
    admission_date DATE NOT NULL,
    photo_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL REFERENCES users(user_id),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL REFERENCES users(user_id)
);

CREATE INDEX idx_students_student_code ON students(student_code);

COMMENT ON TABLE students IS 'Student master table with encrypted PII';
COMMENT ON COLUMN students.student_code IS 'Unique student identifier (format: STU-YYYY-NNNNN)';

```
### 3.3 Academic Management

#### 3.3.1 ACADEMIC_YEARS Table

```sql
CREATE TABLE academic_years (
    academic_year_id BIGSERIAL PRIMARY KEY,
    year_code VARCHAR(10) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_date_range CHECK (end_date > start_date),
    CONSTRAINT year_code_format CHECK (year_code ~ '^\d{4}-\d{4}$')
);

CREATE INDEX idx_academic_years_year_code ON academic_years(year_code);
```

---

### 3.4 Configuration Management

#### 3.4.1 CONFIGURATIONS Table

```sql
CREATE TABLE configurations (
    config_id BIGSERIAL PRIMARY KEY,
    config_category VARCHAR(50) NOT NULL CHECK (config_category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL', 'NOTIFICATION', 'SECURITY')),
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    data_type VARCHAR(20) NOT NULL CHECK (data_type IN ('STRING', 'NUMBER', 'BOOLEAN', 'JSON', 'DATE')),
    description TEXT,
    is_editable BOOLEAN NOT NULL DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL REFERENCES users(user_id),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL REFERENCES users(user_id)
);

CREATE INDEX idx_configurations_category ON configurations(config_category);
CREATE INDEX idx_configurations_key ON configurations(config_key);

COMMENT ON TABLE configurations IS 'System-wide configuration key-value pairs';
COMMENT ON COLUMN configurations.is_editable IS 'False for system-managed configurations';
```

---


## 13. Sample Data

### 13.1 Seed Data Script

```sql
-- Insert default admin user
INSERT INTO users (username, password_hash, full_name, email, role, is_active)
VALUES ('admin', '$2a$10$...', 'System Administrator', 'admin@school.com', 'ADMIN', TRUE);

-- Insert current academic year
INSERT INTO academic_years (year_code, start_date, end_date, is_current)
VALUES ('2025-2026', '2025-04-01', '2026-03-31', TRUE);

-- Insert default configurations
INSERT INTO configurations (config_category, config_key, config_value, data_type, description, is_editable)
VALUES
('GENERAL', 'school_name', 'ABC Public School', 'STRING', 'School name', TRUE),
('GENERAL', 'school_address', '123 Main St, City, State', 'STRING', 'School address', TRUE),
('ACADEMIC', 'default_class_capacity', '40', 'NUMBER', 'Default class capacity', TRUE),
('FINANCIAL', 'late_fee_percentage', '2.00', 'NUMBER', 'Late fee percentage per month', TRUE),
('FINANCIAL', 'payment_due_day', '5', 'NUMBER', 'Payment due date (day of month)', TRUE);
```