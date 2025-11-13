-- =========================================================
-- School Management System - Database Schema V2
-- Description: Create core application tables
-- Author: Database Administrator
-- Date: 2025-11-11
-- =========================================================

-- ======================
-- 1. USERS TABLE
-- ======================
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    mobile VARCHAR(20),
    role user_role NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    password_changed_at TIMESTAMP WITH TIME ZONE,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    account_locked_until TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    CONSTRAINT fk_users_created_by FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    CONSTRAINT fk_users_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE SET NULL,
    CONSTRAINT chk_users_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$'),
    CONSTRAINT chk_users_mobile CHECK (mobile ~ '^[0-9]{10}$' OR mobile IS NULL)
);

COMMENT ON TABLE users IS 'System users with role-based access control';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';
COMMENT ON COLUMN users.failed_login_attempts IS 'Track failed login attempts for account lockout';
COMMENT ON COLUMN users.account_locked_until IS 'Account lockout expiry timestamp';

-- ======================
-- 2. ACADEMIC YEARS TABLE
-- ======================
CREATE TABLE academic_years (
    academic_year_id BIGSERIAL PRIMARY KEY,
    year_code VARCHAR(10) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL,
    CONSTRAINT fk_academic_years_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT fk_academic_years_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id),
    CONSTRAINT chk_academic_years_year_code CHECK (year_code ~ '^\d{4}-\d{4}$'),
    CONSTRAINT chk_academic_years_date_range CHECK (end_date > start_date)
);

COMMENT ON TABLE academic_years IS 'Academic year definitions';
COMMENT ON COLUMN academic_years.year_code IS 'Format: YYYY-YYYY (e.g., 2024-2025)';
COMMENT ON COLUMN academic_years.is_current IS 'Only one academic year should be current at a time';

-- ======================
-- 3. CLASSES TABLE
-- ======================
CREATE TABLE classes (
    class_id BIGSERIAL PRIMARY KEY,
    class_name class_name NOT NULL,
    section VARCHAR(1) NOT NULL,
    academic_year_id BIGINT NOT NULL,
    max_capacity INT NOT NULL,
    current_enrollment INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL,
    CONSTRAINT fk_classes_academic_year FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) ON DELETE RESTRICT,
    CONSTRAINT fk_classes_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT fk_classes_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id),
    CONSTRAINT uq_classes_name_section_year UNIQUE (class_name, section, academic_year_id),
    CONSTRAINT chk_classes_section CHECK (section ~ '^[A-Z]$'),
    CONSTRAINT chk_classes_capacity CHECK (max_capacity > 0 AND max_capacity <= 100),
    CONSTRAINT chk_classes_enrollment CHECK (current_enrollment >= 0 AND current_enrollment <= max_capacity)
);

COMMENT ON TABLE classes IS 'Class sections for each academic year';
COMMENT ON COLUMN classes.section IS 'Section letter (A-Z)';
COMMENT ON COLUMN classes.current_enrollment IS 'Current number of enrolled students (BR-3)';

-- ======================
-- 4. STUDENTS TABLE
-- ======================
CREATE TABLE students (
    student_id BIGSERIAL PRIMARY KEY,
    student_code VARCHAR(20) NOT NULL UNIQUE,
    first_name_encrypted BYTEA NOT NULL,
    last_name_encrypted BYTEA NOT NULL,
    date_of_birth_encrypted BYTEA NOT NULL,
    gender gender,
    email_encrypted BYTEA,
    mobile_encrypted BYTEA NOT NULL,
    mobile_hash VARCHAR(64) NOT NULL UNIQUE,
    address_encrypted BYTEA,
    blood_group VARCHAR(5),
    status student_status NOT NULL DEFAULT 'ACTIVE',
    admission_date DATE NOT NULL,
    photo_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL,
    CONSTRAINT fk_students_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT fk_students_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id),
    CONSTRAINT chk_students_student_code CHECK (student_code ~ '^STU-\d{4}-\d{5}$'),
    CONSTRAINT chk_students_blood_group CHECK (blood_group IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-') OR blood_group IS NULL)
);

COMMENT ON TABLE students IS 'Student master data with encrypted PII fields';
COMMENT ON COLUMN students.student_code IS 'Format: STU-YYYY-NNNNN';
COMMENT ON COLUMN students.first_name_encrypted IS 'AES-256-GCM encrypted first name';
COMMENT ON COLUMN students.mobile_hash IS 'SHA-256 hash of mobile number for uniqueness check (BR-2)';
COMMENT ON COLUMN students.admission_date IS 'Date of admission to school';

-- ======================
-- 5. GUARDIANS TABLE
-- ======================
CREATE TABLE guardians (
    guardian_id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    relationship relationship_type NOT NULL,
    first_name_encrypted BYTEA NOT NULL,
    last_name_encrypted BYTEA NOT NULL,
    mobile_encrypted BYTEA NOT NULL,
    mobile_hash VARCHAR(64) NOT NULL,
    email_encrypted BYTEA,
    occupation VARCHAR(100),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL,
    CONSTRAINT fk_guardians_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_guardians_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT fk_guardians_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id),
    CONSTRAINT uq_guardians_student_relationship UNIQUE (student_id, relationship)
);

COMMENT ON TABLE guardians IS 'Guardian information for students';
COMMENT ON COLUMN guardians.is_primary IS 'Indicates primary contact guardian';
COMMENT ON COLUMN guardians.mobile_hash IS 'SHA-256 hash of mobile number';

-- ======================
-- 6. ENROLLMENTS TABLE
-- ======================
CREATE TABLE enrollments (
    enrollment_id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    enrollment_date DATE NOT NULL,
    status enrollment_status NOT NULL DEFAULT 'ENROLLED',
    withdrawal_date DATE,
    withdrawal_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL,
    CONSTRAINT fk_enrollments_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE RESTRICT,
    CONSTRAINT fk_enrollments_class FOREIGN KEY (class_id) REFERENCES classes(class_id) ON DELETE RESTRICT,
    CONSTRAINT fk_enrollments_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT fk_enrollments_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id),
    CONSTRAINT uq_enrollments_student_class UNIQUE (student_id, class_id),
    CONSTRAINT chk_enrollments_withdrawal CHECK (
        (status = 'WITHDRAWN' AND withdrawal_date IS NOT NULL) OR
        (status <> 'WITHDRAWN' AND withdrawal_date IS NULL)
    )
);

COMMENT ON TABLE enrollments IS 'Student enrollment in classes';
COMMENT ON COLUMN enrollments.withdrawal_date IS 'Required when status is WITHDRAWN';

-- ======================
-- 7. FEE STRUCTURES TABLE
-- ======================
CREATE TABLE fee_structures (
    fee_structure_id BIGSERIAL PRIMARY KEY,
    fee_type fee_type NOT NULL,
    class_name class_name NOT NULL,
    academic_year_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    frequency fee_frequency NOT NULL,
    is_mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    effective_from DATE NOT NULL,
    effective_to DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL,
    CONSTRAINT fk_fee_structures_academic_year FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) ON DELETE RESTRICT,
    CONSTRAINT fk_fee_structures_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT fk_fee_structures_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id),
    CONSTRAINT chk_fee_structures_amount CHECK (amount > 0),
    CONSTRAINT chk_fee_structures_dates CHECK (effective_to IS NULL OR effective_to >= effective_from)
);

COMMENT ON TABLE fee_structures IS 'Fee structure configuration by class and type';
COMMENT ON COLUMN fee_structures.amount IS 'Fee amount in INR (BR-5)';
COMMENT ON COLUMN fee_structures.frequency IS 'Payment frequency';

-- ======================
-- 8. FEE JOURNALS TABLE
-- ======================
CREATE TABLE fee_journals (
    fee_journal_id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    fee_structure_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    due_date DATE NOT NULL,
    due_amount DECIMAL(10, 2) NOT NULL,
    paid_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    balance_amount DECIMAL(10, 2) NOT NULL,
    status payment_status NOT NULL DEFAULT 'PENDING',
    month INT,
    quarter INT,
    is_overdue BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL,
    CONSTRAINT fk_fee_journals_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE RESTRICT,
    CONSTRAINT fk_fee_journals_fee_structure FOREIGN KEY (fee_structure_id) REFERENCES fee_structures(fee_structure_id) ON DELETE RESTRICT,
    CONSTRAINT fk_fee_journals_academic_year FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) ON DELETE RESTRICT,
    CONSTRAINT fk_fee_journals_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT fk_fee_journals_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id),
    CONSTRAINT chk_fee_journals_amounts CHECK (due_amount > 0 AND paid_amount >= 0 AND balance_amount >= 0),
    CONSTRAINT chk_fee_journals_balance CHECK (balance_amount = due_amount - paid_amount),
    CONSTRAINT chk_fee_journals_month CHECK (month IS NULL OR (month >= 1 AND month <= 12)),
    CONSTRAINT chk_fee_journals_quarter CHECK (quarter IS NULL OR (quarter >= 1 AND quarter <= 4))
);

COMMENT ON TABLE fee_journals IS 'Monthly fee journal entries for students';
COMMENT ON COLUMN fee_journals.balance_amount IS 'Calculated as due_amount - paid_amount';
COMMENT ON COLUMN fee_journals.is_overdue IS 'Automatically set when due_date < current_date AND balance > 0';

-- ======================
-- 9. PAYMENTS TABLE
-- ======================
CREATE TABLE payments (
    payment_id BIGSERIAL PRIMARY KEY,
    fee_journal_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_mode payment_mode NOT NULL,
    transaction_reference VARCHAR(100),
    remarks TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL,
    CONSTRAINT fk_payments_fee_journal FOREIGN KEY (fee_journal_id) REFERENCES fee_journals(fee_journal_id) ON DELETE RESTRICT,
    CONSTRAINT fk_payments_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT fk_payments_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id),
    CONSTRAINT chk_payments_amount CHECK (amount > 0),
    CONSTRAINT chk_payments_date CHECK (payment_date <= CURRENT_DATE)
);

COMMENT ON TABLE payments IS 'Individual payment transactions';
COMMENT ON COLUMN payments.transaction_reference IS 'Cheque number, transaction ID, etc.';

-- ======================
-- 10. RECEIPTS TABLE
-- ======================
CREATE TABLE receipts (
    receipt_id BIGSERIAL PRIMARY KEY,
    receipt_number VARCHAR(20) NOT NULL UNIQUE,
    student_id BIGINT NOT NULL,
    payment_id BIGINT NOT NULL,
    receipt_date DATE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_mode payment_mode NOT NULL,
    academic_year_id BIGINT NOT NULL,
    months_covered VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    CONSTRAINT fk_receipts_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE RESTRICT,
    CONSTRAINT fk_receipts_payment FOREIGN KEY (payment_id) REFERENCES payments(payment_id) ON DELETE RESTRICT,
    CONSTRAINT fk_receipts_academic_year FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) ON DELETE RESTRICT,
    CONSTRAINT fk_receipts_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT chk_receipts_receipt_number CHECK (receipt_number ~ '^REC-\d{4}-\d{5}$'),
    CONSTRAINT chk_receipts_amount CHECK (total_amount > 0)
);

COMMENT ON TABLE receipts IS 'Fee payment receipts';
COMMENT ON COLUMN receipts.receipt_number IS 'Format: REC-YYYY-NNNNN (BR-11)';
COMMENT ON COLUMN receipts.months_covered IS 'Comma-separated months/periods covered by payment';

-- ======================
-- 11. CONFIGURATIONS TABLE
-- ======================
CREATE TABLE configurations (
    config_id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    category config_category NOT NULL,
    description TEXT,
    is_editable BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL,
    CONSTRAINT fk_configurations_created_by FOREIGN KEY (created_by) REFERENCES users(user_id),
    CONSTRAINT fk_configurations_updated_by FOREIGN KEY (updated_by) REFERENCES users(user_id)
);

COMMENT ON TABLE configurations IS 'System configuration key-value pairs';
COMMENT ON COLUMN configurations.is_editable IS 'Prevents modification of system-critical settings';

-- ======================
-- 12. AUDIT LOG TABLE
-- ======================
CREATE TABLE audit_log (
    audit_id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    record_id BIGINT NOT NULL,
    action audit_action NOT NULL,
    old_values JSONB,
    new_values JSONB,
    changed_by BIGINT NOT NULL,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    CONSTRAINT fk_audit_log_changed_by FOREIGN KEY (changed_by) REFERENCES users(user_id)
);

COMMENT ON TABLE audit_log IS 'Immutable audit trail for all table modifications';
COMMENT ON COLUMN audit_log.old_values IS 'JSON representation of old values (for UPDATE/DELETE)';
COMMENT ON COLUMN audit_log.new_values IS 'JSON representation of new values (for INSERT/UPDATE)';

-- ======================
-- Success Message
-- ======================
DO $$
BEGIN
    RAISE NOTICE 'V2 Migration: Core tables created successfully';
END $$;
