# Database Schema Documentation - School Management System

## Overview

This document provides comprehensive documentation of the PostgreSQL database schema for the School Management System.

**Database**: PostgreSQL 18+
**Schema Version**: 1.0.0
**Migration Tool**: Flyway
**Total Tables**: 12 core tables + 1 audit log table

---

## Table of Contents

1. [Entity Relationship Overview](#entity-relationship-overview)
2. [Table Definitions](#table-definitions)
3. [Custom Types (Enums)](#custom-types-enums)
4. [Indexes](#indexes)
5. [Triggers and Functions](#triggers-and-functions)
6. [Business Rules Implementation](#business-rules-implementation)
7. [Security and Encryption](#security-and-encryption)
8. [Migration Scripts](#migration-scripts)
9. [Seed Data](#seed-data)

---

## Entity Relationship Overview

```
users (1) ──┬── (created_by, updated_by) ──> (*) academic_years
            ├── (created_by, updated_by) ──> (*) classes
            ├── (created_by, updated_by) ──> (*) students
            ├── (created_by, updated_by) ──> (*) guardians
            ├── (created_by, updated_by) ──> (*) enrollments
            ├── (created_by, updated_by) ──> (*) fee_structures
            ├── (created_by, updated_by) ──> (*) fee_journals
            ├── (created_by, updated_by) ──> (*) payments
            ├── (created_by, updated_by) ──> (*) receipts
            └── (created_by, updated_by) ──> (*) configurations

academic_years (1) ──> (*) classes
                   └──> (*) fee_structures
                   └──> (*) fee_journals
                   └──> (*) receipts

classes (1) ──> (*) enrollments

students (1) ──┬──> (*) guardians
               ├──> (*) enrollments
               ├──> (*) fee_journals
               └──> (*) receipts

fee_structures (1) ──> (*) fee_journals

fee_journals (1) ──> (*) payments

payments (1) ──> (1) receipts

audit_log ──> (changed_by) ──> users
```

---

## Table Definitions

### 1. users

**Purpose**: System users with role-based access control

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| user_id | BIGSERIAL | PRIMARY KEY | Unique user identifier |
| username | VARCHAR(50) | NOT NULL, UNIQUE | Login username |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| full_name | VARCHAR(100) | NOT NULL | User's full name |
| email | VARCHAR(100) | UNIQUE, CHECK (email format) | Email address |
| mobile | VARCHAR(20) | CHECK (10 digits) | Mobile number |
| role | user_role | NOT NULL | User role (ADMIN, PRINCIPAL, etc.) |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Account status |
| last_login_at | TIMESTAMP WITH TIME ZONE | | Last successful login |
| password_changed_at | TIMESTAMP WITH TIME ZONE | | Last password change |
| failed_login_attempts | INT | NOT NULL, DEFAULT 0 | Failed login counter |
| account_locked_until | TIMESTAMP WITH TIME ZONE | | Account lockout expiry |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Creation timestamp |
| created_by | BIGINT | FK → users(user_id) | Creator user |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Last update timestamp |
| updated_by | BIGINT | FK → users(user_id) | Last updater user |

**Indexes**:
- `idx_users_username` - Fast authentication lookup
- `idx_users_role_active` - Composite index for role-based queries

**Business Rules**:
- BR-2: Email format validation via CHECK constraint
- Account lockout after 5 failed attempts (configurable)

---

### 2. academic_years

**Purpose**: Academic year definitions (e.g., 2024-2025)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| academic_year_id | BIGSERIAL | PRIMARY KEY | Unique academic year ID |
| year_code | VARCHAR(10) | NOT NULL, UNIQUE, CHECK (YYYY-YYYY format) | Year code |
| start_date | DATE | NOT NULL | Academic year start date |
| end_date | DATE | NOT NULL, CHECK (end_date > start_date) | Academic year end date |
| is_current | BOOLEAN | NOT NULL, DEFAULT FALSE | Current academic year flag |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Creation timestamp |
| created_by | BIGINT | FK → users(user_id) | Creator user |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Last update timestamp |
| updated_by | BIGINT | FK → users(user_id) | Last updater user |

**Indexes**:
- `idx_academic_years_current_only` - Partial index for current year

**Business Rules**:
- Only ONE academic year can be current at a time (enforced by trigger)
- Year code must match format YYYY-YYYY

---

### 3. classes

**Purpose**: Class sections for each academic year (e.g., Class 5-A)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| class_id | BIGSERIAL | PRIMARY KEY | Unique class identifier |
| class_name | class_name | NOT NULL | Class level (1-10) |
| section | VARCHAR(1) | NOT NULL, CHECK (A-Z) | Section letter |
| academic_year_id | BIGINT | FK → academic_years, NOT NULL | Academic year reference |
| max_capacity | INT | NOT NULL, CHECK (1-100) | Maximum student capacity |
| current_enrollment | INT | NOT NULL, DEFAULT 0, CHECK (0 <= enrollment <= capacity) | Current number of students (BR-3) |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Class active status |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Creation timestamp |
| created_by | BIGINT | FK → users(user_id) | Creator user |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Last update timestamp |
| updated_by | BIGINT | FK → users(user_id) | Last updater user |

**Unique Constraints**:
- `uq_classes_name_section_year` - (class_name, section, academic_year_id)

**Indexes**:
- `idx_classes_capacity_utilization` - For checking available capacity (BR-3)

**Business Rules**:
- BR-3: Cannot exceed max class capacity
- Unique class-section per academic year

---

### 4. students

**Purpose**: Student master data with encrypted PII fields

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| student_id | BIGSERIAL | PRIMARY KEY | Unique student identifier |
| student_code | VARCHAR(20) | NOT NULL, UNIQUE, CHECK (STU-YYYY-NNNNN) | Student code |
| first_name_encrypted | BYTEA | NOT NULL | AES-256-GCM encrypted first name |
| last_name_encrypted | BYTEA | NOT NULL | AES-256-GCM encrypted last name |
| date_of_birth_encrypted | BYTEA | NOT NULL | AES-256-GCM encrypted DOB |
| gender | gender | | Gender (MALE, FEMALE, OTHER) |
| email_encrypted | BYTEA | | Encrypted email address |
| mobile_encrypted | BYTEA | NOT NULL | Encrypted mobile number |
| mobile_hash | VARCHAR(64) | NOT NULL, UNIQUE | SHA-256 hash of mobile (BR-2) |
| address_encrypted | BYTEA | | Encrypted address |
| blood_group | VARCHAR(5) | CHECK (A+, A-, ...) | Blood group |
| status | student_status | NOT NULL, DEFAULT 'ACTIVE' | Student status |
| admission_date | DATE | NOT NULL | Date of admission |
| photo_url | VARCHAR(500) | | Photo URL/path |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Creation timestamp |
| created_by | BIGINT | FK → users(user_id) | Creator user |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Last update timestamp |
| updated_by | BIGINT | FK → users(user_id) | Last updater user |

**Indexes**:
- `idx_students_student_code` - Fast student lookup
- `idx_students_mobile_hash` - For mobile uniqueness check (BR-2)
- `idx_students_active_only` - Partial index for active students

**Business Rules**:
- BR-1: Age validation (3-18 years) enforced in application layer
- BR-2: Mobile number uniqueness via mobile_hash
- Student code format: STU-YYYY-NNNNN

---

### 5. guardians

**Purpose**: Guardian information for students

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| guardian_id | BIGSERIAL | PRIMARY KEY | Unique guardian identifier |
| student_id | BIGINT | FK → students, NOT NULL | Associated student |
| relationship | relationship_type | NOT NULL | Relationship to student |
| first_name_encrypted | BYTEA | NOT NULL | Encrypted first name |
| last_name_encrypted | BYTEA | NOT NULL | Encrypted last name |
| mobile_encrypted | BYTEA | NOT NULL | Encrypted mobile number |
| mobile_hash | VARCHAR(64) | NOT NULL | SHA-256 hash of mobile |
| email_encrypted | BYTEA | | Encrypted email |
| occupation | VARCHAR(100) | | Guardian's occupation |
| is_primary | BOOLEAN | NOT NULL, DEFAULT FALSE | Primary contact flag |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Creation timestamp |
| created_by | BIGINT | FK → users(user_id) | Creator user |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Last update timestamp |
| updated_by | BIGINT | FK → users(user_id) | Last updater user |

**Unique Constraints**:
- `uq_guardians_student_relationship` - (student_id, relationship)

**Business Rules**:
- Only ONE primary guardian per student (enforced by trigger)
- At least one guardian required per student (enforced in application layer)

---

### 6. enrollments

**Purpose**: Student enrollment in classes

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| enrollment_id | BIGSERIAL | PRIMARY KEY | Unique enrollment identifier |
| student_id | BIGINT | FK → students, NOT NULL | Enrolled student |
| class_id | BIGINT | FK → classes, NOT NULL | Enrolled class |
| enrollment_date | DATE | NOT NULL | Date of enrollment |
| status | enrollment_status | NOT NULL, DEFAULT 'ENROLLED' | Enrollment status |
| withdrawal_date | DATE | CHECK (required if WITHDRAWN) | Date of withdrawal |
| withdrawal_reason | TEXT | | Reason for withdrawal |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Creation timestamp |
| created_by | BIGINT | FK → users(user_id) | Creator user |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Last update timestamp |
| updated_by | BIGINT | FK → users(user_id) | Last updater user |

**Unique Constraints**:
- `uq_enrollments_student_class` - (student_id, class_id)

**Business Rules**:
- Withdrawal date required when status is WITHDRAWN
- Class capacity validated before enrollment (BR-3)

---

### 7. fee_structures

**Purpose**: Fee structure configuration by class and type

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| fee_structure_id | BIGSERIAL | PRIMARY KEY | Unique fee structure ID |
| fee_type | fee_type | NOT NULL | Type of fee (TUITION, LIBRARY, etc.) |
| class_name | class_name | NOT NULL | Applicable class level |
| academic_year_id | BIGINT | FK → academic_years, NOT NULL | Academic year |
| amount | DECIMAL(10, 2) | NOT NULL, CHECK (amount > 0) | Fee amount in INR (BR-5) |
| frequency | fee_frequency | NOT NULL | Payment frequency |
| is_mandatory | BOOLEAN | NOT NULL, DEFAULT TRUE | Mandatory fee flag |
| effective_from | DATE | NOT NULL | Effective start date |
| effective_to | DATE | CHECK (effective_to >= effective_from) | Effective end date |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Active status |
| description | TEXT | | Fee description |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Creation timestamp |
| created_by | BIGINT | FK → users(user_id) | Creator user |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Last update timestamp |
| updated_by | BIGINT | FK → users(user_id) | Last updater user |

**Indexes**:
- `idx_fee_structures_active_only` - Partial index for active fee structures

**Business Rules**:
- BR-5: Fee calculation based on class level and add-ons
- Amount must be positive
- Date range validation

---

### 8. fee_journals

**Purpose**: Monthly fee journal entries for students

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| fee_journal_id | BIGSERIAL | PRIMARY KEY | Unique fee journal ID |
| student_id | BIGINT | FK → students, NOT NULL | Associated student |
| fee_structure_id | BIGINT | FK → fee_structures, NOT NULL | Fee structure reference |
| academic_year_id | BIGINT | FK → academic_years, NOT NULL | Academic year |
| due_date | DATE | NOT NULL | Payment due date |
| due_amount | DECIMAL(10, 2) | NOT NULL, CHECK (> 0) | Total amount due |
| paid_amount | DECIMAL(10, 2) | NOT NULL, DEFAULT 0, CHECK (>= 0) | Amount paid |
| balance_amount | DECIMAL(10, 2) | NOT NULL, CHECK (= due - paid) | Outstanding balance |
| status | payment_status | NOT NULL, DEFAULT 'PENDING' | Payment status |
| month | INT | CHECK (1-12) | Month (for monthly fees) |
| quarter | INT | CHECK (1-4) | Quarter (for quarterly fees) |
| is_overdue | BOOLEAN | NOT NULL, DEFAULT FALSE | Overdue flag |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Creation timestamp |
| created_by | BIGINT | FK → users(user_id) | Creator user |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Last update timestamp |
| updated_by | BIGINT | FK → users(user_id) | Last updater user |

**Indexes**:
- `idx_fee_journals_pending` - Partial index for pending payments
- `idx_fee_journals_overdue_only` - Partial index for overdue reports

**Business Rules**:
- Balance automatically calculated: balance_amount = due_amount - paid_amount
- Status automatically updated based on paid amount
- Overdue flag set when due_date < current_date AND balance > 0

---

### 9. payments

**Purpose**: Individual payment transactions

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| payment_id | BIGSERIAL | PRIMARY KEY | Unique payment ID |
| fee_journal_id | BIGINT | FK → fee_journals, NOT NULL | Associated fee journal |
| payment_date | DATE | NOT NULL, CHECK (<= current_date) | Date of payment |
| amount | DECIMAL(10, 2) | NOT NULL, CHECK (> 0) | Payment amount (BR-9) |
| payment_mode | payment_mode | NOT NULL | Payment method |
| transaction_reference | VARCHAR(100) | | Transaction ID/Cheque number |
| remarks | TEXT | | Additional remarks |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Creation timestamp |
| created_by | BIGINT | FK → users(user_id) | Creator user |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Last update timestamp |
| updated_by | BIGINT | FK → users(user_id) | Last updater user |

**Business Rules**:
- BR-9: Payment amount cannot exceed fee journal balance (validated by trigger)
- Payment date cannot be future date
- After payment, fee journal paid_amount automatically updated

---

### 10. receipts

**Purpose**: Fee payment receipts

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| receipt_id | BIGSERIAL | PRIMARY KEY | Unique receipt ID |
| receipt_number | VARCHAR(20) | NOT NULL, UNIQUE, CHECK (REC-YYYY-NNNNN) | Receipt number (BR-11) |
| student_id | BIGINT | FK → students, NOT NULL | Associated student |
| payment_id | BIGINT | FK → payments, NOT NULL | Associated payment |
| receipt_date | DATE | NOT NULL | Receipt date |
| total_amount | DECIMAL(10, 2) | NOT NULL, CHECK (> 0) | Total receipt amount |
| payment_mode | payment_mode | NOT NULL | Payment method |
| academic_year_id | BIGINT | FK → academic_years, NOT NULL | Academic year |
| months_covered | VARCHAR(100) | | Months/periods covered |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Creation timestamp |
| created_by | BIGINT | FK → users(user_id) | Creator user |

**Business Rules**:
- BR-11: Sequential unique receipt numbers (REC-YYYY-NNNNN format)
- One receipt per payment
- Receipt number generated automatically

---

### 11. configurations

**Purpose**: System configuration key-value pairs

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| config_id | BIGSERIAL | PRIMARY KEY | Unique config ID |
| config_key | VARCHAR(100) | NOT NULL, UNIQUE | Configuration key |
| config_value | TEXT | NOT NULL | Configuration value |
| category | config_category | NOT NULL | Configuration category |
| description | TEXT | | Description of setting |
| is_editable | BOOLEAN | NOT NULL, DEFAULT TRUE | Prevents editing critical settings |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Creation timestamp |
| created_by | BIGINT | FK → users(user_id) | Creator user |
| updated_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Last update timestamp |
| updated_by | BIGINT | FK → users(user_id) | Last updater user |

**Categories**:
- GENERAL: School-level settings
- ACADEMIC: Academic rules and limits
- FINANCIAL: Fee and payment settings
- SYSTEM: System-level configuration

---

### 12. audit_log

**Purpose**: Immutable audit trail for all table modifications

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| audit_id | BIGSERIAL | PRIMARY KEY | Unique audit log ID |
| table_name | VARCHAR(50) | NOT NULL | Table that was modified |
| record_id | BIGINT | NOT NULL | ID of modified record |
| action | audit_action | NOT NULL | Action type (INSERT, UPDATE, DELETE) |
| old_values | JSONB | | JSON of old values |
| new_values | JSONB | | JSON of new values |
| changed_by | BIGINT | FK → users, NOT NULL | User who made change |
| changed_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT NOW() | Timestamp of change |
| ip_address | VARCHAR(45) | | IP address of user |
| user_agent | TEXT | | User agent string |

**Indexes**:
- `idx_audit_log_table_record` - Composite index for audit trail queries
- `idx_audit_log_old_values_gin` - GIN index for JSONB queries
- `idx_audit_log_new_values_gin` - GIN index for JSONB queries

---

## Custom Types (Enums)

### user_role
```sql
'ADMIN', 'PRINCIPAL', 'OFFICE_STAFF', 'ACCOUNTS_MANAGER', 'AUDITOR'
```

### student_status
```sql
'ACTIVE', 'INACTIVE', 'GRADUATED', 'TRANSFERRED', 'WITHDRAWN'
```

### gender
```sql
'MALE', 'FEMALE', 'OTHER'
```

### relationship_type
```sql
'FATHER', 'MOTHER', 'GUARDIAN', 'OTHER'
```

### class_name
```sql
'1', '2', '3', '4', '5', '6', '7', '8', '9', '10'
```

### enrollment_status
```sql
'ENROLLED', 'PROMOTED', 'WITHDRAWN'
```

### fee_type
```sql
'TUITION', 'LIBRARY', 'COMPUTER', 'SPORTS', 'TRANSPORT',
'EXAM', 'ADMISSION', 'ANNUAL', 'DEVELOPMENT', 'OTHER'
```

### fee_frequency
```sql
'MONTHLY', 'QUARTERLY', 'ANNUAL', 'ONE_TIME'
```

### payment_status
```sql
'PENDING', 'PARTIAL', 'PAID', 'OVERDUE', 'WAIVED'
```

### payment_mode
```sql
'CASH', 'CHEQUE', 'ONLINE', 'UPI', 'CARD'
```

### config_category
```sql
'GENERAL', 'ACADEMIC', 'FINANCIAL', 'SYSTEM'
```

### audit_action
```sql
'INSERT', 'UPDATE', 'DELETE'
```

---

## Indexes

Total indexes: 50+

**Performance Optimization Strategy**:
- B-tree indexes for equality and range queries
- Partial indexes for filtered queries (e.g., active records only)
- Composite indexes for multi-column queries
- GIN indexes for JSONB columns in audit_log

---

## Triggers and Functions

### 1. Audit Trigger (`audit_trigger_function`)

**Applied to**: users, students, guardians, enrollments, fee_structures, fee_journals, payments, receipts

**Purpose**: Automatically logs all INSERT, UPDATE, DELETE operations to audit_log table

### 2. Balance Calculation (`update_fee_journal_balance`)

**Applied to**: fee_journals (BEFORE INSERT OR UPDATE)

**Purpose**:
- Calculates balance_amount = due_amount - paid_amount
- Updates payment status based on paid amount
- Sets is_overdue flag if past due date

### 3. Updated At Timestamp (`update_updated_at_column`)

**Applied to**: All tables with updated_at column (BEFORE UPDATE)

**Purpose**: Automatically updates updated_at timestamp on record modification

### 4. Single Current Academic Year (`enforce_single_current_academic_year`)

**Applied to**: academic_years (BEFORE INSERT OR UPDATE)

**Purpose**: Ensures only one academic year has is_current = TRUE at any time

### 5. Single Primary Guardian (`enforce_single_primary_guardian`)

**Applied to**: guardians (BEFORE INSERT OR UPDATE)

**Purpose**: Ensures only one guardian is marked as primary per student

### 6. Payment Validation (`validate_payment_amount`)

**Applied to**: payments (BEFORE INSERT)

**Purpose**: Validates payment amount does not exceed fee journal balance (BR-9)

### 7. Update Fee Journal Paid Amount (`update_fee_journal_paid_amount`)

**Applied to**: payments (AFTER INSERT)

**Purpose**: Automatically updates fee_journals.paid_amount after payment insertion

---

## Business Rules Implementation

| Rule ID | Rule | Implementation |
|---------|------|----------------|
| BR-1 | Student age 3-18 years | Application layer validation |
| BR-2 | Mobile uniqueness | Unique constraint on mobile_hash (SHA-256) |
| BR-3 | Class capacity limit | CHECK constraint + application validation |
| BR-5 | Fee calculation | fee_structures table with Drools engine |
| BR-9 | Payment ≤ due amount | Trigger: validate_payment_amount |
| BR-11 | Sequential receipt numbers | Application layer generation (REC-YYYY-NNNNN) |

---

## Security and Encryption

### PII Encryption

**Encrypted Fields**:
- students: first_name, last_name, date_of_birth, email, mobile, address
- guardians: first_name, last_name, email, mobile

**Encryption Method**: AES-256-GCM

**Hash Fields**:
- students.mobile_hash: SHA-256 hash for uniqueness check
- guardians.mobile_hash: SHA-256 hash for lookups

**Key Management**: AWS KMS or HashiCorp Vault (production)

---

## Migration Scripts

| Script | Description | Tables Created |
|--------|-------------|----------------|
| V1__Create_lookup_tables.sql | Enums and types | 12 custom types |
| V2__Create_core_tables.sql | Core tables | 12 tables |
| V3__Create_indexes.sql | Performance indexes | 50+ indexes |
| V4__Create_audit_triggers.sql | Audit and business logic triggers | 15+ triggers |
| V5__Insert_seed_data.sql | Default data | 2 users, 1 academic year, 10 classes, 30+ configurations, 30 fee structures |

---

## Seed Data

### Default Users
- **admin** / Admin@123 (Role: ADMIN)
- **principal** / Principal@123 (Role: PRINCIPAL)

⚠️ **IMPORTANT**: Change default passwords immediately after installation!

### Default Academic Year
- 2024-2025 (April 1, 2024 - March 31, 2025)

### Default Classes
- Class 1-A through 10-A (Section A for all classes)
- Default capacity: 50 students per class

### Default Fee Structures
- **Tuition Fees**:
  - Classes 1-5: ₹5,000/month
  - Classes 6-8: ₹6,000/month
  - Classes 9-10: ₹7,000/month
- **One-time Fees**:
  - Admission: ₹10,000
- **Annual Fees**:
  - Development: ₹5,000

### System Configurations
- 35+ pre-configured settings for school, academic, financial, and system categories

---

## Database Backup and Recovery

### Backup Strategy

```bash
# Daily full backup
pg_dump -U school_user -d school_management_db -F c -f backup_$(date +%Y%m%d).dump

# Restore
pg_restore -U school_user -d school_management_db backup_20250115.dump
```

### Point-in-Time Recovery (PITR)

Configure PostgreSQL WAL archiving:
```
wal_level = replica
archive_mode = on
archive_command = 'cp %p /path/to/archive/%f'
```

---

## Maintenance

### Regular Tasks

1. **Vacuum and Analyze** (Weekly):
```sql
VACUUM ANALYZE students;
VACUUM ANALYZE fee_journals;
VACUUM ANALYZE payments;
```

2. **Reindex** (Monthly):
```sql
REINDEX TABLE students;
REINDEX TABLE fee_journals;
```

3. **Update Statistics** (Daily):
```sql
ANALYZE;
```

4. **Check Table Bloat** (Weekly):
```sql
SELECT schemaname, tablename,
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

---

## Performance Monitoring

### Key Queries

**Slow Query Identification**:
```sql
SELECT query, calls, total_exec_time, mean_exec_time
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 10;
```

**Index Usage**:
```sql
SELECT schemaname, tablename, indexname, idx_scan
FROM pg_stat_user_indexes
WHERE idx_scan = 0
ORDER BY schemaname, tablename;
```

**Table Size**:
```sql
SELECT
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

---

**Last Updated**: November 11, 2025
**Version**: 1.0.0
**Author**: Database Administrator
