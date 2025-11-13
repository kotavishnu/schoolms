-- School Management System (SMS) - PostgreSQL Initialization Script
-- Version: 1.0
-- Date: November 11, 2025
-- Database: PostgreSQL 18
-- This script creates all tables, constraints, indexes, and initial data

-- ============================================================================
-- 1. DROP EXISTING OBJECTS (if any - for clean reinstall)
-- ============================================================================

DROP VIEW IF EXISTS v_student_fee_summary CASCADE;
DROP VIEW IF EXISTS v_monthly_collection_report CASCADE;
DROP VIEW IF EXISTS v_class_enrollment CASCADE;

DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS receipt CASCADE;
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS fee_journal CASCADE;
DROP TABLE IF EXISTS fee_structure CASCADE;
DROP TABLE IF EXISTS fee_type CASCADE;
DROP TABLE IF EXISTS class CASCADE;
DROP TABLE IF EXISTS guardian CASCADE;
DROP TABLE IF EXISTS student CASCADE;
DROP TABLE IF EXISTS configuration CASCADE;
DROP TABLE IF EXISTS payment_status CASCADE;
DROP TABLE IF EXISTS student_status CASCADE;
DROP TABLE IF EXISTS payment_method CASCADE;
DROP TABLE IF EXISTS academic_year CASCADE;

-- ============================================================================
-- 2. CREATE LOOKUP/REFERENCE TABLES (No Foreign Keys)
-- ============================================================================

CREATE TABLE academic_year (
    academic_year_id SERIAL PRIMARY KEY,
    start_year INTEGER NOT NULL,
    end_year INTEGER NOT NULL,
    year_code VARCHAR(10) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT FALSE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_years CHECK (end_year = start_year + 1)
);

CREATE INDEX idx_academic_year_active ON academic_year(is_active);
CREATE INDEX idx_academic_year_code ON academic_year(year_code);

-- Insert current and next academic year
INSERT INTO academic_year (start_year, end_year, year_code, is_active, description)
VALUES
    (2024, 2025, '2024-2025', TRUE, 'Current Academic Year'),
    (2025, 2026, '2025-2026', FALSE, 'Next Academic Year'),
    (2026, 2027, '2026-2027', FALSE, 'Future Academic Year');

-- ============================================================================

CREATE TABLE student_status (
    status_id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO student_status (status_name, description)
VALUES
    ('Active', 'Currently enrolled'),
    ('Inactive', 'Not currently enrolled'),
    ('Graduated', 'Completed all courses'),
    ('Transferred', 'Transferred to another school'),
    ('On Leave', 'Temporary leave of absence'),
    ('Suspended', 'Disciplinary suspension');

-- ============================================================================

CREATE TABLE payment_status (
    status_id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO payment_status (status_name, description)
VALUES
    ('Pending', 'Fee not yet paid'),
    ('Partial', 'Partial payment received'),
    ('Paid', 'Full payment received'),
    ('Overdue', 'Payment past due date'),
    ('Waived', 'Fee waived by administration'),
    ('Cancelled', 'Fee cancelled');

-- ============================================================================

CREATE TABLE payment_method (
    payment_method_id SERIAL PRIMARY KEY,
    method_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_method CHECK (method_name IN ('Cash', 'Online', 'Cheque', 'DD', 'Bank Transfer'))
);

INSERT INTO payment_method (method_name, description, is_active)
VALUES
    ('Cash', 'Direct cash payment', TRUE),
    ('Online', 'Online payment via bank transfer', TRUE),
    ('Cheque', 'Payment via cheque', TRUE),
    ('DD', 'Demand Draft payment', TRUE),
    ('Bank Transfer', 'Direct bank transfer', TRUE);

-- ============================================================================

CREATE TABLE fee_type (
    fee_type_id SERIAL PRIMARY KEY,
    type_name VARCHAR(100) NOT NULL UNIQUE,
    frequency VARCHAR(20) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_frequency CHECK (frequency IN ('Monthly', 'Quarterly', 'Annual', 'One-Time'))
);

INSERT INTO fee_type (type_name, frequency, description, is_active)
VALUES
    ('Tuition Fee', 'Monthly', 'Regular tuition fees', TRUE),
    ('Library Fee', 'Quarterly', 'Library usage and maintenance', TRUE),
    ('Computer Lab Fee', 'Quarterly', 'Computer lab access', TRUE),
    ('Sports Fee', 'Annual', 'Sports activities and equipment', TRUE),
    ('Transport Fee', 'Monthly', 'Transportation cost', TRUE),
    ('Activity Fee', 'Annual', 'Co-curricular activities', TRUE),
    ('Examination Fee', 'One-Time', 'Annual exam fees', TRUE),
    ('Development Fund', 'Annual', 'School development and infrastructure', TRUE);

-- ============================================================================

CREATE TABLE configuration (
    config_id SERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    config_type VARCHAR(50) NOT NULL,
    description TEXT,
    editable BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_type CHECK (config_type IN ('General', 'Academic', 'Financial', 'Security'))
);

INSERT INTO configuration (config_key, config_value, config_type, description, editable)
VALUES
    ('SCHOOL_NAME', 'Sample School', 'General', 'Name of the school', TRUE),
    ('SCHOOL_ADDRESS', '123 Education Lane, City, State 12345', 'General', 'School address', TRUE),
    ('SCHOOL_PHONE', '+91-XXX-XXXX-XXXX', 'General', 'School contact phone', TRUE),
    ('SCHOOL_EMAIL', 'info@school.edu', 'General', 'School email address', TRUE),
    ('SCHOOL_PRINCIPAL', 'Dr. John Doe', 'General', 'Principal name', TRUE),
    ('MAX_CLASS_CAPACITY', '50', 'Academic', 'Default maximum class capacity', TRUE),
    ('ACADEMIC_YEAR_FORMAT', 'YYYY-YYYY', 'Academic', 'Format for academic year', FALSE),
    ('FEE_DUE_DAY', '1', 'Financial', 'Day of month fees are due', TRUE),
    ('FEE_DUE_DATE', '5', 'Financial', 'Last day to pay without penalty', TRUE),
    ('CURRENCY_CODE', 'INR', 'Financial', 'Currency code for fees', TRUE),
    ('CURRENCY_SYMBOL', '₹', 'Financial', 'Currency symbol', TRUE),
    ('DATA_RETENTION_YEARS', '7', 'General', 'Years to retain historical data', FALSE),
    ('ENCRYPTION_ENABLED', 'true', 'Security', 'Enable PII encryption', FALSE),
    ('AUDIT_LOGGING_ENABLED', 'true', 'Security', 'Enable audit logging', FALSE);

-- ============================================================================
-- 3. CREATE DIMENSION TABLES (Classes depends on Academic Year)
-- ============================================================================

CREATE TABLE class (
    class_id SERIAL PRIMARY KEY,
    class_name VARCHAR(20) NOT NULL,
    section_name VARCHAR(5) NOT NULL,
    academic_year_id INTEGER NOT NULL REFERENCES academic_year(academic_year_id) ON DELETE CASCADE,
    max_capacity INTEGER NOT NULL DEFAULT 50,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_capacity CHECK (max_capacity > 0),
    CONSTRAINT valid_class_name CHECK (class_name IN ('1', '2', '3', '4', '5', '6', '7', '8', '9', '10')),
    CONSTRAINT valid_section CHECK (section_name IN ('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')),
    CONSTRAINT unique_class_section_year UNIQUE (class_name, section_name, academic_year_id)
);

CREATE INDEX idx_class_academic_year ON class(academic_year_id);
CREATE INDEX idx_class_name ON class(class_name);
CREATE INDEX idx_class_section ON class(section_name);

-- Insert standard classes for current academic year
INSERT INTO class (class_name, section_name, academic_year_id, max_capacity)
SELECT '1', section, academic_year_id, 50 FROM academic_year WHERE is_active = TRUE, (SELECT ARRAY['A','B','C'] AS sections) AS t(section)
UNION ALL
SELECT '2', section, academic_year_id, 50 FROM academic_year WHERE is_active = TRUE, (SELECT ARRAY['A','B','C'] AS sections) AS t(section)
UNION ALL
SELECT '3', section, academic_year_id, 50 FROM academic_year WHERE is_active = TRUE, (SELECT ARRAY['A','B','C'] AS sections) AS t(section)
UNION ALL
SELECT '4', section, academic_year_id, 50 FROM academic_year WHERE is_active = TRUE, (SELECT ARRAY['A','B','C'] AS sections) AS t(section)
UNION ALL
SELECT '5', section, academic_year_id, 50 FROM academic_year WHERE is_active = TRUE, (SELECT ARRAY['A','B','C'] AS sections) AS t(section)
UNION ALL
SELECT '6', section, academic_year_id, 50 FROM academic_year WHERE is_active = TRUE, (SELECT ARRAY['A','B','C'] AS sections) AS t(section)
UNION ALL
SELECT '7', section, academic_year_id, 50 FROM academic_year WHERE is_active = TRUE, (SELECT ARRAY['A','B','C'] AS sections) AS t(section)
UNION ALL
SELECT '8', section, academic_year_id, 50 FROM academic_year WHERE is_active = TRUE, (SELECT ARRAY['A','B','C'] AS sections) AS t(section)
UNION ALL
SELECT '9', section, academic_year_id, 50 FROM academic_year WHERE is_active = TRUE, (SELECT ARRAY['A','B','C'] AS sections) AS t(section)
UNION ALL
SELECT '10', section, academic_year_id, 50 FROM academic_year WHERE is_active = TRUE, (SELECT ARRAY['A','B','C'] AS sections) AS t(section);

-- ============================================================================

CREATE TABLE fee_structure (
    fee_structure_id SERIAL PRIMARY KEY,
    fee_type_id INTEGER NOT NULL REFERENCES fee_type(fee_type_id),
    class_id INTEGER NOT NULL REFERENCES class(class_id),
    academic_year_id INTEGER NOT NULL REFERENCES academic_year(academic_year_id),
    amount DECIMAL(10, 2) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    effective_date DATE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_amount CHECK (amount > 0),
    CONSTRAINT unique_fee_version UNIQUE (fee_type_id, class_id, academic_year_id, version)
);

CREATE INDEX idx_fee_structure_class ON fee_structure(class_id);
CREATE INDEX idx_fee_structure_academic_year ON fee_structure(academic_year_id);
CREATE INDEX idx_fee_structure_fee_type ON fee_structure(fee_type_id);
CREATE INDEX idx_fee_structure_effective_date ON fee_structure(effective_date);

-- ============================================================================
-- 4. CREATE FACT TABLES (Student, Guardian, Fee Journal)
-- ============================================================================

CREATE TABLE student (
    student_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    mobile_number VARCHAR(20) UNIQUE NOT NULL,
    email_address VARCHAR(100),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    postal_code VARCHAR(10),
    status_id INTEGER NOT NULL REFERENCES student_status(status_id),
    class_id INTEGER REFERENCES class(class_id) ON DELETE SET NULL,
    academic_year_id INTEGER NOT NULL REFERENCES academic_year(academic_year_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT age_validation CHECK (
        date_of_birth <= CURRENT_DATE - INTERVAL '3 years'
        AND date_of_birth >= CURRENT_DATE - INTERVAL '18 years'
    ),
    CONSTRAINT valid_email CHECK (
        email_address IS NULL
        OR email_address ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$'
    )
);

CREATE INDEX idx_student_mobile ON student(mobile_number);
CREATE INDEX idx_student_status ON student(status_id);
CREATE INDEX idx_student_class ON student(class_id);
CREATE INDEX idx_student_academic_year ON student(academic_year_id);
CREATE INDEX idx_student_created_at ON student(created_at);
CREATE INDEX idx_student_deleted_at ON student(deleted_at);

-- ============================================================================

CREATE TABLE guardian (
    guardian_id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES student(student_id) ON DELETE CASCADE,
    guardian_name VARCHAR(100) NOT NULL,
    relation VARCHAR(50) NOT NULL,
    mobile_number VARCHAR(20) NOT NULL,
    email_address VARCHAR(100),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    postal_code VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_relation CHECK (
        relation IN ('Father', 'Mother', 'Brother', 'Sister', 'Uncle', 'Aunt', 'Cousin', 'Other')
    )
);

CREATE INDEX idx_guardian_student ON guardian(student_id);
CREATE INDEX idx_guardian_mobile ON guardian(mobile_number);
CREATE INDEX idx_guardian_email ON guardian(email_address);

-- ============================================================================

CREATE TABLE fee_journal (
    fee_journal_id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES student(student_id),
    fee_structure_id INTEGER NOT NULL REFERENCES fee_structure(fee_structure_id),
    fee_month INTEGER NOT NULL,
    fee_year INTEGER NOT NULL,
    due_amount DECIMAL(10, 2) NOT NULL,
    paid_amount DECIMAL(10, 2) DEFAULT 0,
    balance_amount DECIMAL(10, 2) GENERATED ALWAYS AS (due_amount - paid_amount) STORED,
    payment_status_id INTEGER NOT NULL REFERENCES payment_status(status_id),
    academic_year_id INTEGER NOT NULL REFERENCES academic_year(academic_year_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_amounts CHECK (
        due_amount > 0
        AND paid_amount >= 0
        AND paid_amount <= due_amount
    ),
    CONSTRAINT valid_month CHECK (fee_month >= 1 AND fee_month <= 12),
    CONSTRAINT unique_journal_entry UNIQUE (student_id, fee_structure_id, fee_month, fee_year, academic_year_id)
);

CREATE INDEX idx_fee_journal_student ON fee_journal(student_id);
CREATE INDEX idx_fee_journal_status ON fee_journal(payment_status_id);
CREATE INDEX idx_fee_journal_month_year ON fee_journal(fee_month, fee_year);
CREATE INDEX idx_fee_journal_academic_year ON fee_journal(academic_year_id);
CREATE INDEX idx_fee_journal_balance ON fee_journal(balance_amount);

-- ============================================================================
-- 5. CREATE TRANSACTION TABLES (Payment, Receipt)
-- ============================================================================

CREATE TABLE payment (
    payment_id SERIAL PRIMARY KEY,
    fee_journal_id INTEGER NOT NULL REFERENCES fee_journal(fee_journal_id),
    student_id INTEGER NOT NULL REFERENCES student(student_id),
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method_id INTEGER NOT NULL REFERENCES payment_method(payment_method_id),
    reference_number VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_amount CHECK (amount > 0),
    CONSTRAINT payment_not_future CHECK (payment_date <= CURRENT_DATE)
);

CREATE INDEX idx_payment_student ON payment(student_id);
CREATE INDEX idx_payment_fee_journal ON payment(fee_journal_id);
CREATE INDEX idx_payment_date ON payment(payment_date);
CREATE INDEX idx_payment_method ON payment(payment_method_id);

-- ============================================================================

CREATE TABLE receipt (
    receipt_id SERIAL PRIMARY KEY,
    receipt_number VARCHAR(20) NOT NULL UNIQUE,
    payment_id INTEGER NOT NULL REFERENCES payment(payment_id),
    student_id INTEGER NOT NULL REFERENCES student(student_id),
    amount DECIMAL(10, 2) NOT NULL,
    receipt_date DATE NOT NULL,
    payment_method_id INTEGER NOT NULL REFERENCES payment_method(payment_method_id),
    issued_by VARCHAR(100) NOT NULL,
    is_void BOOLEAN DEFAULT FALSE,
    void_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_amount CHECK (amount > 0),
    CONSTRAINT receipt_not_future CHECK (receipt_date <= CURRENT_DATE),
    CONSTRAINT valid_void_reason CHECK (is_void = FALSE OR void_reason IS NOT NULL)
);

CREATE INDEX idx_receipt_number ON receipt(receipt_number);
CREATE INDEX idx_receipt_student ON receipt(student_id);
CREATE INDEX idx_receipt_date ON receipt(receipt_date);
CREATE INDEX idx_receipt_payment ON receipt(payment_id);

-- ============================================================================
-- 6. CREATE AUDIT TABLE
-- ============================================================================

CREATE TABLE audit_log (
    audit_id SERIAL PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    record_id INTEGER NOT NULL,
    action VARCHAR(20) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    changed_by VARCHAR(100) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_action CHECK (action IN ('INSERT', 'UPDATE', 'DELETE'))
);

CREATE INDEX idx_audit_table_record ON audit_log(table_name, record_id);
CREATE INDEX idx_audit_action ON audit_log(action);
CREATE INDEX idx_audit_changed_at ON audit_log(changed_at);

-- ============================================================================
-- 7. CREATE VIEWS FOR REPORTING
-- ============================================================================

CREATE VIEW v_student_fee_summary AS
SELECT
    s.student_id,
    CONCAT(s.first_name, ' ', s.last_name) AS student_name,
    c.class_name || '-' || c.section_name AS class_section,
    SUM(fj.due_amount) AS total_due,
    SUM(fj.paid_amount) AS total_paid,
    SUM(fj.balance_amount) AS total_balance,
    COUNT(DISTINCT CASE WHEN ps.status_name = 'Pending' THEN fj.fee_journal_id END) AS pending_count,
    COUNT(DISTINCT CASE WHEN ps.status_name = 'Overdue' THEN fj.fee_journal_id END) AS overdue_count
FROM student s
JOIN class c ON s.class_id = c.class_id
JOIN fee_journal fj ON s.student_id = fj.student_id
JOIN payment_status ps ON fj.payment_status_id = ps.status_id
WHERE s.deleted_at IS NULL
GROUP BY s.student_id, s.first_name, s.last_name, c.class_name, c.section_name;

-- ============================================================================

CREATE VIEW v_monthly_collection_report AS
SELECT
    p.payment_date,
    pm.method_name,
    COUNT(DISTINCT p.payment_id) AS transaction_count,
    SUM(p.amount) AS total_amount,
    COUNT(DISTINCT p.student_id) AS student_count
FROM payment p
JOIN payment_method pm ON p.payment_method_id = pm.payment_method_id
GROUP BY p.payment_date, pm.method_name
ORDER BY p.payment_date DESC, pm.method_name;

-- ============================================================================

CREATE VIEW v_class_enrollment AS
SELECT
    c.class_id,
    c.class_name || '-' || c.section_name AS class_section,
    ay.start_year || '-' || ay.end_year AS academic_year,
    c.max_capacity,
    COUNT(DISTINCT s.student_id) AS current_enrollment,
    ROUND(100.0 * COUNT(DISTINCT s.student_id) / NULLIF(c.max_capacity, 0), 2) AS occupancy_percentage
FROM class c
JOIN academic_year ay ON c.academic_year_id = ay.academic_year_id
LEFT JOIN student s ON c.class_id = s.class_id AND s.deleted_at IS NULL
GROUP BY c.class_id, c.class_name, c.section_name, ay.start_year, ay.end_year, c.max_capacity;

-- ============================================================================

CREATE VIEW v_pending_fees_report AS
SELECT
    s.student_id,
    CONCAT(s.first_name, ' ', s.last_name) AS student_name,
    c.class_name || '-' || c.section_name AS class_section,
    fj.fee_month,
    fj.fee_year,
    ft.type_name AS fee_type,
    fj.due_amount,
    fj.paid_amount,
    fj.balance_amount,
    ps.status_name
FROM student s
JOIN class c ON s.class_id = c.class_id
JOIN fee_journal fj ON s.student_id = fj.student_id
JOIN fee_structure fs ON fj.fee_structure_id = fs.fee_structure_id
JOIN fee_type ft ON fs.fee_type_id = ft.fee_type_id
JOIN payment_status ps ON fj.payment_status_id = ps.status_id
WHERE s.deleted_at IS NULL
    AND ps.status_name IN ('Pending', 'Partial', 'Overdue')
ORDER BY fj.fee_year DESC, fj.fee_month DESC, s.student_id;

-- ============================================================================
-- 8. CREATE FUNCTIONS FOR COMMON OPERATIONS
-- ============================================================================

-- Function to generate sequential receipt number
CREATE OR REPLACE FUNCTION generate_receipt_number()
RETURNS VARCHAR AS $$
DECLARE
    next_sequence INTEGER;
    receipt_number VARCHAR(20);
BEGIN
    SELECT COALESCE(MAX(CAST(SUBSTRING(receipt_number FROM 9) AS INTEGER)), 0) + 1
    INTO next_sequence
    FROM receipt
    WHERE receipt_number LIKE CONCAT('REC-', TO_CHAR(CURRENT_DATE, 'YYYY'), '-%');

    receipt_number := CONCAT('REC-', TO_CHAR(CURRENT_DATE, 'YYYY'), '-', LPAD(next_sequence::TEXT, 5, '0'));

    RETURN receipt_number;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================

-- Function to update fee journal status based on payment
CREATE OR REPLACE FUNCTION update_fee_journal_status()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE fee_journal
    SET payment_status_id = CASE
        WHEN NEW.paid_amount >= NEW.due_amount THEN (SELECT status_id FROM payment_status WHERE status_name = 'Paid')
        WHEN NEW.paid_amount > 0 THEN (SELECT status_id FROM payment_status WHERE status_name = 'Partial')
        ELSE payment_status_id
    END,
    updated_at = CURRENT_TIMESTAMP
    WHERE fee_journal_id = NEW.fee_journal_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to update fee journal status
CREATE TRIGGER trg_update_fee_journal_status
AFTER UPDATE ON fee_journal
FOR EACH ROW
WHEN (OLD.paid_amount IS DISTINCT FROM NEW.paid_amount)
EXECUTE FUNCTION update_fee_journal_status();

-- ============================================================================

-- Function for audit logging
CREATE OR REPLACE FUNCTION audit_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log (table_name, record_id, action, new_value, changed_by, changed_at)
        VALUES (TG_TABLE_NAME, NEW.student_id, 'INSERT', ROW(NEW.*)::TEXT, CURRENT_USER, CURRENT_TIMESTAMP);
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_log (table_name, record_id, action, old_value, new_value, changed_by, changed_at)
        VALUES (TG_TABLE_NAME, NEW.student_id, 'UPDATE', ROW(OLD.*)::TEXT, ROW(NEW.*)::TEXT, CURRENT_USER, CURRENT_TIMESTAMP);
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO audit_log (table_name, record_id, action, old_value, changed_by, changed_at)
        VALUES (TG_TABLE_NAME, OLD.student_id, 'DELETE', ROW(OLD.*)::TEXT, CURRENT_USER, CURRENT_TIMESTAMP);
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create audit triggers for student table
CREATE TRIGGER trg_student_audit
AFTER INSERT OR UPDATE OR DELETE ON student
FOR EACH ROW
EXECUTE FUNCTION audit_changes();

-- ============================================================================
-- 9. CREATE SAMPLE DATA (for testing)
-- ============================================================================

-- Sample students (10 students)
INSERT INTO student (first_name, last_name, date_of_birth, mobile_number, email_address, address, city, state, postal_code, status_id, class_id, academic_year_id)
SELECT
    name_part1,
    name_part2,
    CURRENT_DATE - (RANDOM() * 5475 + 1095)::INTEGER,
    '90000000' || LPAD((ROW_NUMBER() OVER ())::TEXT, 8, '0'),
    LOWER(name_part1 || '.' || name_part2 || '@example.com'),
    '123 School Street',
    'City',
    'State',
    '12345',
    1,
    (SELECT class_id FROM class ORDER BY RANDOM() LIMIT 1),
    (SELECT academic_year_id FROM academic_year WHERE is_active = TRUE LIMIT 1)
FROM (
    SELECT 'Ramesh' AS name_part1, 'Kumar' AS name_part2
    UNION ALL SELECT 'Priya', 'Singh'
    UNION ALL SELECT 'Arun', 'Patel'
    UNION ALL SELECT 'Anjali', 'Gupta'
    UNION ALL SELECT 'Vikram', 'Reddy'
    UNION ALL SELECT 'Neha', 'Sharma'
    UNION ALL SELECT 'Arjun', 'Nair'
    UNION ALL SELECT 'Divya', 'Iyer'
    UNION ALL SELECT 'Rohan', 'Sinha'
    UNION ALL SELECT 'Sneha', 'Malhotra'
) AS names;

-- Sample guardians (2 per student)
INSERT INTO guardian (student_id, guardian_name, relation, mobile_number, email_address, address, city, state, postal_code)
SELECT
    student_id,
    'Parent ' || student_id,
    'Father',
    '91000000' || LPAD((student_id * 2)::TEXT, 7, '0'),
    LOWER('parent' || student_id || '@example.com'),
    '123 School Street',
    'City',
    'State',
    '12345'
FROM student;

-- ============================================================================
-- 10. GRANT PERMISSIONS (adjust as needed for your user roles)
-- ============================================================================

-- GRANT SELECT ON ALL TABLES IN SCHEMA public TO app_read_only;
-- GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO app_read_write;
-- GRANT ALL ON ALL TABLES IN SCHEMA public TO app_admin;

-- ============================================================================
-- 11. PRINT SUMMARY
-- ============================================================================

-- Summary of created objects
SELECT 'Database initialization completed successfully!' AS message;

SELECT COUNT(*) AS table_count FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE';
SELECT COUNT(*) AS view_count FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'VIEW';
SELECT COUNT(*) AS student_count FROM student;
SELECT COUNT(*) AS academic_year_count FROM academic_year;
SELECT COUNT(*) AS class_count FROM class;

-- End of initialization script
