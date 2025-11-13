# Database Schema Design - School Management System (SMS)

**Version**: 1.0
**Date**: November 11, 2025
**Database**: PostgreSQL 18

---

## 1. Overview

This document defines the complete database schema for the School Management System, including:
- Entity-Relationship Diagram (conceptual)
- Table definitions with column specifications
- Constraints and validations
- Indexes for performance optimization
- Data types and storage considerations

### Database Sizing
- **Max Students**: 2,500
- **Max Classes**: ~50 (Classes 1-10 with A-Z sections = 10 × 26 = 260 max)
- **Estimated Rows by Year**: Fee Journal entries = 2,500 × 12 months = 30,000 rows/year
- **Retention**: 7 years of historical data

---

## 2. Entity-Relationship Diagram (Conceptual)

```
┌─────────────────────┐
│     STUDENT         │
├─────────────────────┤
│ StudentID (PK)      │
│ Name                │
│ DOB                 │
│ Mobile              │
│ Email               │
│ Address             │
│ StatusID (FK)       │
│ ClassID (FK)        │
│ AcademicYearID (FK) │
│ CreatedAt           │
│ UpdatedAt           │
│ DeletedAt           │
└────────┬────────────┘
         │
         ├─ FK → CLASS
         ├─ FK → ACADEMIC_YEAR
         └─ FK → STUDENT_STATUS

┌──────────────────────┐
│      GUARDIAN        │
├──────────────────────┤
│ GuardianID (PK)      │
│ StudentID (FK)       │
│ Name                 │
│ Relation             │
│ Mobile               │
│ Email                │
│ Address              │
└──────────┬───────────┘
           └─ FK → STUDENT

┌────────────────────┐
│      CLASS         │
├────────────────────┤
│ ClassID (PK)       │
│ ClassName          │
│ Section            │
│ AcademicYearID(FK) │
│ MaxCapacity        │
│ CreatedAt          │
│ UpdatedAt          │
└────────┬───────────┘
         └─ FK → ACADEMIC_YEAR

┌──────────────────────┐
│   ACADEMIC_YEAR      │
├──────────────────────┤
│ AcademicYearID (PK)  │
│ StartYear            │
│ EndYear              │
│ IsActive             │
│ CreatedAt            │
└──────────────────────┘

┌────────────────────────┐
│     FEE_STRUCTURE      │
├────────────────────────┤
│ FeeStructureID (PK)    │
│ FeeTypeID (FK)         │
│ ClassID (FK)           │
│ AcademicYearID (FK)    │
│ Amount                 │
│ Version                │
│ EffectiveDate          │
│ CreatedAt              │
└──────┬─────────────────┘
       ├─ FK → FEE_TYPE
       ├─ FK → CLASS
       └─ FK → ACADEMIC_YEAR

┌──────────────────────┐
│      FEE_TYPE        │
├──────────────────────┤
│ FeeTypeID (PK)       │
│ TypeName             │
│ Frequency            │
│ Description          │
│ IsActive             │
│ CreatedAt            │
└──────────────────────┘

┌──────────────────────────┐
│    FEE_JOURNAL           │
├──────────────────────────┤
│ FeeJournalID (PK)        │
│ StudentID (FK)           │
│ FeeStructureID (FK)      │
│ Month                    │
│ Year                     │
│ DueAmount                │
│ PaidAmount               │
│ BalanceAmount            │
│ StatusID (FK)            │
│ AcademicYearID (FK)      │
│ CreatedAt                │
│ UpdatedAt                │
└──────┬───────────────────┘
       ├─ FK → STUDENT
       ├─ FK → FEE_STRUCTURE
       ├─ FK → PAYMENT_STATUS
       └─ FK → ACADEMIC_YEAR

┌──────────────────────────┐
│      PAYMENT            │
├──────────────────────────┤
│ PaymentID (PK)           │
│ FeeJournalID (FK)        │
│ StudentID (FK)           │
│ Amount                   │
│ PaymentDate              │
│ PaymentMethodID (FK)     │
│ ReferenceNo              │
│ CreatedAt                │
└──────┬───────────────────┘
       ├─ FK → FEE_JOURNAL
       ├─ FK → STUDENT
       └─ FK → PAYMENT_METHOD

┌──────────────────────────┐
│      RECEIPT             │
├──────────────────────────┤
│ ReceiptID (PK)           │
│ ReceiptNo                │
│ PaymentID (FK)           │
│ StudentID (FK)           │
│ Amount                   │
│ ReceiptDate              │
│ PaymentMethodID (FK)     │
│ IssuedBy                 │
│ IsVoid                   │
│ VoidReason               │
│ CreatedAt                │
│ UpdatedAt                │
└──────┬───────────────────┘
       ├─ FK → PAYMENT
       ├─ FK → STUDENT
       └─ FK → PAYMENT_METHOD

┌──────────────────────────┐
│   PAYMENT_METHOD         │
├──────────────────────────┤
│ PaymentMethodID (PK)     │
│ MethodName (Cash/Online) │
│ IsActive                 │
│ CreatedAt                │
└──────────────────────────┘

┌──────────────────────────┐
│    STUDENT_STATUS        │
├──────────────────────────┤
│ StatusID (PK)            │
│ StatusName               │
│ Description              │
└──────────────────────────┘

┌──────────────────────────┐
│   PAYMENT_STATUS         │
├──────────────────────────┤
│ StatusID (PK)            │
│ StatusName               │
│ Description              │
└──────────────────────────┘

┌──────────────────────────┐
│    CONFIGURATION         │
├──────────────────────────┤
│ ConfigID (PK)            │
│ ConfigKey                │
│ ConfigValue              │
│ ConfigType               │
│ CreatedAt                │
│ UpdatedAt                │
└──────────────────────────┘

┌──────────────────────────┐
│      AUDIT_LOG           │
├──────────────────────────┤
│ AuditID (PK)             │
│ TableName                │
│ RecordID                 │
│ Action                   │
│ OldValue                 │
│ NewValue                 │
│ ChangedBy                │
│ ChangedAt                │
└──────────────────────────┘
```

---

## 3. Detailed Table Specifications

### 3.1 STUDENT
```sql
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
    class_id INTEGER REFERENCES class(class_id),
    academic_year_id INTEGER NOT NULL REFERENCES academic_year(academic_year_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT age_validation CHECK (
        date_of_birth <= CURRENT_DATE - INTERVAL '3 years'
        AND date_of_birth >= CURRENT_DATE - INTERVAL '18 years'
    ),
    CONSTRAINT valid_email CHECK (email_address ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$' OR email_address IS NULL)
);

-- Indexes
CREATE INDEX idx_student_mobile ON student(mobile_number);
CREATE INDEX idx_student_status ON student(status_id);
CREATE INDEX idx_student_class ON student(class_id);
CREATE INDEX idx_student_academic_year ON student(academic_year_id);
CREATE INDEX idx_student_created_at ON student(created_at);
```

### 3.2 GUARDIAN
```sql
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
    CONSTRAINT valid_relation CHECK (relation IN ('Father', 'Mother', 'Brother', 'Sister', 'Uncle', 'Aunt', 'Other'))
);

-- Indexes
CREATE INDEX idx_guardian_student ON guardian(student_id);
CREATE INDEX idx_guardian_mobile ON guardian(mobile_number);
```

### 3.3 ACADEMIC_YEAR
```sql
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

-- Indexes
CREATE INDEX idx_academic_year_active ON academic_year(is_active);
CREATE INDEX idx_academic_year_code ON academic_year(year_code);
```

### 3.4 CLASS
```sql
CREATE TABLE class (
    class_id SERIAL PRIMARY KEY,
    class_name VARCHAR(20) NOT NULL,
    section_name VARCHAR(5) NOT NULL,
    academic_year_id INTEGER NOT NULL REFERENCES academic_year(academic_year_id),
    max_capacity INTEGER NOT NULL DEFAULT 50,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_capacity CHECK (max_capacity > 0),
    CONSTRAINT unique_class_section_year UNIQUE (class_name, section_name, academic_year_id)
);

-- Indexes
CREATE INDEX idx_class_academic_year ON class(academic_year_id);
CREATE INDEX idx_class_name ON class(class_name);
```

### 3.5 FEE_TYPE
```sql
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

-- Indexes
CREATE INDEX idx_fee_type_active ON fee_type(is_active);
CREATE INDEX idx_fee_type_frequency ON fee_type(frequency);
```

### 3.6 FEE_STRUCTURE
```sql
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

-- Indexes
CREATE INDEX idx_fee_structure_class ON fee_structure(class_id);
CREATE INDEX idx_fee_structure_academic_year ON fee_structure(academic_year_id);
CREATE INDEX idx_fee_structure_effective_date ON fee_structure(effective_date);
```

### 3.7 FEE_JOURNAL
```sql
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
    CONSTRAINT valid_amounts CHECK (due_amount > 0 AND paid_amount >= 0 AND paid_amount <= due_amount),
    CONSTRAINT valid_month CHECK (fee_month >= 1 AND fee_month <= 12),
    CONSTRAINT unique_journal_entry UNIQUE (student_id, fee_structure_id, fee_month, fee_year, academic_year_id)
);

-- Indexes
CREATE INDEX idx_fee_journal_student ON fee_journal(student_id);
CREATE INDEX idx_fee_journal_status ON fee_journal(payment_status_id);
CREATE INDEX idx_fee_journal_month_year ON fee_journal(fee_month, fee_year);
CREATE INDEX idx_fee_journal_academic_year ON fee_journal(academic_year_id);
```

### 3.8 PAYMENT_METHOD
```sql
CREATE TABLE payment_method (
    payment_method_id SERIAL PRIMARY KEY,
    method_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_method CHECK (method_name IN ('Cash', 'Online', 'Cheque', 'DD', 'Bank Transfer'))
);

-- Insert standard payment methods
INSERT INTO payment_method (method_name, is_active) VALUES
    ('Cash', TRUE),
    ('Online', TRUE),
    ('Cheque', TRUE),
    ('DD', TRUE),
    ('Bank Transfer', TRUE);
```

### 3.9 PAYMENT
```sql
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

-- Indexes
CREATE INDEX idx_payment_student ON payment(student_id);
CREATE INDEX idx_payment_fee_journal ON payment(fee_journal_id);
CREATE INDEX idx_payment_date ON payment(payment_date);
CREATE INDEX idx_payment_method ON payment(payment_method_id);
```

### 3.10 RECEIPT
```sql
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

-- Indexes
CREATE INDEX idx_receipt_number ON receipt(receipt_number);
CREATE INDEX idx_receipt_student ON receipt(student_id);
CREATE INDEX idx_receipt_date ON receipt(receipt_date);
CREATE INDEX idx_receipt_payment ON receipt(payment_id);
```

### 3.11 STUDENT_STATUS
```sql
CREATE TABLE student_status (
    status_id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert standard statuses
INSERT INTO student_status (status_name, description) VALUES
    ('Active', 'Currently enrolled'),
    ('Inactive', 'Not currently enrolled'),
    ('Graduated', 'Completed all courses'),
    ('Transferred', 'Transferred to another school'),
    ('On Leave', 'Temporary leave of absence'),
    ('Suspended', 'Disciplinary suspension');
```

### 3.12 PAYMENT_STATUS
```sql
CREATE TABLE payment_status (
    status_id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert standard payment statuses
INSERT INTO payment_status (status_name, description) VALUES
    ('Pending', 'Fee not yet paid'),
    ('Partial', 'Partial payment received'),
    ('Paid', 'Full payment received'),
    ('Overdue', 'Payment past due date'),
    ('Waived', 'Fee waived by administration'),
    ('Cancelled', 'Fee cancelled');
```

### 3.13 CONFIGURATION
```sql
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

-- Insert default configurations
INSERT INTO configuration (config_key, config_value, config_type, description, editable) VALUES
    ('SCHOOL_NAME', 'Sample School', 'General', 'Name of the school', TRUE),
    ('SCHOOL_ADDRESS', '', 'General', 'School address', TRUE),
    ('SCHOOL_PHONE', '', 'General', 'School contact phone', TRUE),
    ('SCHOOL_EMAIL', '', 'General', 'School email address', TRUE),
    ('MAX_CLASS_CAPACITY', '50', 'Academic', 'Default maximum class capacity', TRUE),
    ('ACADEMIC_YEAR_FORMAT', 'YYYY-YYYY', 'Academic', 'Format for academic year', FALSE),
    ('FEE_DUE_DAY', '1', 'Financial', 'Day of month fees are due', TRUE),
    ('CURRENCY_CODE', 'INR', 'Financial', 'Currency code for fees', TRUE),
    ('DATA_RETENTION_YEARS', '7', 'General', 'Years to retain historical data', FALSE),
    ('ENCRYPTION_ENABLED', 'true', 'Security', 'Enable PII encryption', FALSE);
```

### 3.14 AUDIT_LOG
```sql
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

-- Indexes
CREATE INDEX idx_audit_table_record ON audit_log(table_name, record_id);
CREATE INDEX idx_audit_action ON audit_log(action);
CREATE INDEX idx_audit_changed_at ON audit_log(changed_at);
```

---

## 4. Views for Reporting

### 4.1 Student Fee Summary View
```sql
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
```

### 4.2 Monthly Collection Report View
```sql
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
```

### 4.3 Class Enrollment View
```sql
CREATE VIEW v_class_enrollment AS
SELECT
    c.class_id,
    c.class_name || '-' || c.section_name AS class_section,
    ay.start_year || '-' || ay.end_year AS academic_year,
    c.max_capacity,
    COUNT(DISTINCT s.student_id) AS current_enrollment,
    ROUND(100.0 * COUNT(DISTINCT s.student_id) / c.max_capacity, 2) AS occupancy_percentage
FROM class c
JOIN academic_year ay ON c.academic_year_id = ay.academic_year_id
LEFT JOIN student s ON c.class_id = s.class_id AND s.deleted_at IS NULL
GROUP BY c.class_id, c.class_name, c.section_name, ay.start_year, ay.end_year, c.max_capacity;
```

---

## 5. Indexes Summary

| Table | Index Name | Columns | Purpose |
|-------|-----------|---------|---------|
| student | idx_student_mobile | mobile_number | Fast lookup by mobile |
| student | idx_student_status | status_id | Filter by status |
| student | idx_student_class | class_id | Find students in class |
| fee_journal | idx_fee_journal_student | student_id | Quick student fee lookup |
| fee_journal | idx_fee_journal_status | payment_status_id | Filter by payment status |
| payment | idx_payment_student | student_id | Find payments by student |
| payment | idx_payment_date | payment_date | Range queries by date |
| receipt | idx_receipt_number | receipt_number | Fast receipt lookup |

---

## 6. Backup and Recovery Strategy

### Backup Schedule
- **Daily**: Incremental backups (automated)
- **Weekly**: Full database backup (every Sunday 2:00 AM)
- **Monthly**: Archive backup (retained for 7 years)

### Retention Policy
- Daily incremental: 7 days
- Weekly full: 4 weeks
- Monthly archive: 7 years

### Recovery Point Objective (RPO): 1 hour
### Recovery Time Objective (RTO): 4 hours

---

## 7. Performance Optimization

### Query Optimization Tips
1. Always use indexed columns in WHERE clauses
2. Use EXPLAIN ANALYZE to verify index usage
3. Archive old audit logs (>2 years) to separate table
4. Partition fee_journal by academic_year for large datasets

### Connection Pooling
- Min pool size: 5
- Max pool size: 20
- Idle timeout: 15 minutes

---

## 8. Data Migration Considerations

### Phase 1: Setup
1. Create all tables with constraints
2. Insert reference data (statuses, fee types, payment methods)
3. Create indexes and views

### Phase 2: Data Import
1. Import academic years
2. Import students and guardians
3. Import classes
4. Import fee structures
5. Import historical fee journals and payments

### Phase 3: Validation
1. Verify data integrity
2. Validate constraints
3. Check referential integrity
4. Generate audit trail

---

## 9. Security Considerations

### Encryption
- PII fields encrypted at rest (AES-256)
- TLS 1.3 for data in transit
- Secure password hashing (bcrypt)

### Access Control
- Role-based database access
- Row-level security for multi-school deployments
- Audit trail for all modifications

### Compliance
- GDPR compliance for data retention
- SOC 2 compliance for financial data
- Data privacy by design

---

**End of Database Schema Document**
