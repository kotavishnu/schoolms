# Flyway Database Migration Guide

## Overview

This document provides comprehensive guidance for managing database migrations using Flyway in the School Management System. Flyway is a database version control tool that enables automated, repeatable, and reliable database schema evolution.

## Table of Contents

1. [Architecture](#architecture)
2. [Migration Structure](#migration-structure)
3. [Naming Conventions](#naming-conventions)
4. [Current Migrations](#current-migrations)
5. [Configuration](#configuration)
6. [Development Workflow](#development-workflow)
7. [Testing Migrations](#testing-migrations)
8. [Production Deployment](#production-deployment)
9. [Rollback Strategy](#rollback-strategy)
10. [Troubleshooting](#troubleshooting)
11. [Best Practices](#best-practices)

---

## Architecture

### Flyway Integration

Flyway is integrated into the Spring Boot application and executes migrations automatically on application startup.

```
Application Startup
    |
    ├── Flyway Migration Check
    |     ├── Scan db/migration directory
    |     ├── Compare with flyway_schema_history table
    |     ├── Execute pending migrations in order
    |     └── Update schema history
    |
    └── Application Ready
```

### Migration Storage

All migration scripts are stored in:
```
src/main/resources/db/migration/
```

### Schema History

Flyway tracks applied migrations in the `flyway_schema_history` table:

| Column | Description |
|--------|-------------|
| `installed_rank` | Order of migration execution |
| `version` | Migration version (e.g., "1", "2") |
| `description` | Migration description from filename |
| `type` | Type of migration (SQL, Java) |
| `script` | Filename of migration script |
| `checksum` | SHA-256 checksum for validation |
| `installed_by` | Database user who ran migration |
| `installed_on` | Timestamp of execution |
| `execution_time` | Duration in milliseconds |
| `success` | Whether migration succeeded |

---

## Migration Structure

### Current Migration Files

```
src/main/resources/db/migration/
├── V1__Create_lookup_tables.sql       (Enums and custom types)
├── V2__Create_core_tables.sql         (All application tables)
├── V3__Create_indexes.sql             (Performance indexes)
├── V4__Create_audit_triggers.sql      (Audit and business triggers)
└── V5__Insert_seed_data.sql           (Default data)
```

### Migration Dependencies

```
V1: Lookup Tables & Types
  └─> V2: Core Tables (depends on V1 types)
       └─> V3: Indexes (depends on V2 tables)
            └─> V4: Triggers (depends on V2 tables)
                 └─> V5: Seed Data (depends on V2 tables)
```

---

## Naming Conventions

### Versioned Migrations

**Format**: `V{VERSION}__{DESCRIPTION}.sql`

- **Prefix**: Always start with capital `V`
- **Version**: Numeric version (1, 2, 3, ..., 1.1, 2.1, etc.)
- **Separator**: Double underscore `__`
- **Description**: Snake_case description with underscores

**Examples**:
```
✓ V1__Create_lookup_tables.sql
✓ V2__Create_core_tables.sql
✓ V3__Create_indexes.sql
✓ V6__Add_student_email_column.sql
✓ V7__Create_sms_notification_table.sql
✓ V2.1__Hotfix_student_constraints.sql

✗ v1__create_lookup_tables.sql        (lowercase v)
✗ V1_Create_lookup_tables.sql         (single underscore)
✗ V1__create-lookup-tables.sql        (hyphens instead of underscores)
```

### Repeatable Migrations

**Format**: `R__{DESCRIPTION}.sql`

- Used for views, stored procedures, functions that can be re-applied
- Executed after all versioned migrations
- Re-run whenever checksum changes

**Examples**:
```
R__Create_student_summary_view.sql
R__Update_fee_calculation_function.sql
```

---

## Current Migrations

### V1: Create Lookup Tables (Version 1)

**Purpose**: Define PostgreSQL custom types and enums

**Created Types**:
- `user_role`: ADMIN, PRINCIPAL, OFFICE_STAFF, ACCOUNTS_MANAGER, AUDITOR
- `student_status`: ACTIVE, INACTIVE, GRADUATED, TRANSFERRED, WITHDRAWN
- `gender`: MALE, FEMALE, OTHER
- `relationship_type`: FATHER, MOTHER, GUARDIAN, OTHER
- `class_name`: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
- `enrollment_status`: ENROLLED, PROMOTED, WITHDRAWN
- `fee_type`: TUITION, LIBRARY, COMPUTER, SPORTS, TRANSPORT, EXAM, ADMISSION, ANNUAL, DEVELOPMENT, OTHER
- `fee_frequency`: MONTHLY, QUARTERLY, ANNUAL, ONE_TIME
- `payment_status`: PENDING, PARTIAL, PAID, OVERDUE, WAIVED
- `payment_mode`: CASH, CHEQUE, ONLINE, UPI, CARD
- `config_category`: GENERAL, ACADEMIC, FINANCIAL, SYSTEM
- `audit_action`: INSERT, UPDATE, DELETE

**Extensions Enabled**:
- `uuid-ossp`: UUID generation functions
- `pgcrypto`: Encryption functions for PII data

**Rollback Strategy**:
```sql
DROP TYPE IF EXISTS user_role CASCADE;
DROP TYPE IF EXISTS student_status CASCADE;
-- ... (drop all types)
DROP EXTENSION IF EXISTS uuid-ossp;
DROP EXTENSION IF EXISTS pgcrypto;
```

---

### V2: Create Core Tables (Version 2)

**Purpose**: Create all application database tables

**Tables Created**:

1. **users** - System users with RBAC
   - Columns: user_id, username, password_hash, full_name, email, mobile, role, is_active
   - Constraints: Unique username, email format validation, mobile format validation
   - Self-referencing foreign keys: created_by, updated_by

2. **academic_years** - Academic year definitions
   - Columns: academic_year_id, year_code, start_date, end_date, is_current
   - Constraints: year_code format (YYYY-YYYY), date range validation
   - Business Rule: Only one current academic year

3. **classes** - Class sections per academic year
   - Columns: class_id, class_name, section, academic_year_id, max_capacity, current_enrollment
   - Constraints: Unique (class_name, section, academic_year), capacity validation (BR-3)
   - Business Rule: current_enrollment <= max_capacity

4. **students** - Student master data (PII encrypted)
   - Columns: student_id, student_code, encrypted fields (first_name, last_name, dob, mobile), mobile_hash
   - Constraints: Student code format (STU-YYYY-NNNNN), unique mobile_hash (BR-2)
   - Encryption: AES-256-GCM for PII fields

5. **guardians** - Student guardian information
   - Columns: guardian_id, student_id, relationship, encrypted fields, is_primary
   - Constraints: One primary guardian per student
   - Cascade delete: Delete guardians when student is deleted

6. **enrollments** - Student class enrollments
   - Columns: enrollment_id, student_id, class_id, enrollment_date, status, withdrawal_date
   - Constraints: Unique (student_id, class_id), withdrawal date required when withdrawn
   - Business Rule: Validates class capacity on enrollment

7. **fee_structures** - Fee configuration by class
   - Columns: fee_structure_id, fee_type, class_name, academic_year_id, amount, frequency
   - Constraints: Amount > 0, effective date range validation
   - Business Rule: Implements BR-5 (fee calculation rules)

8. **fee_journals** - Monthly fee entries for students
   - Columns: fee_journal_id, student_id, fee_structure_id, due_date, due_amount, paid_amount, balance_amount
   - Constraints: Balance = due_amount - paid_amount, overdue calculation
   - Auto-calculated: balance_amount, status, is_overdue

9. **payments** - Individual payment transactions
   - Columns: payment_id, fee_journal_id, payment_date, amount, payment_mode, transaction_reference
   - Constraints: Amount > 0, payment_date <= today (BR-9)
   - Business Rule: Payment amount <= fee journal balance

10. **receipts** - Fee payment receipts
    - Columns: receipt_id, receipt_number, student_id, payment_id, receipt_date, total_amount
    - Constraints: Receipt number format (REC-YYYY-NNNNN) (BR-11)
    - Business Rule: Sequential receipt numbers

11. **configurations** - System configuration key-value pairs
    - Columns: config_id, config_key, config_value, category, is_editable
    - Constraints: Unique config_key
    - Purpose: Runtime configurable settings

12. **audit_log** - Immutable audit trail
    - Columns: audit_id, table_name, record_id, action, old_values (JSONB), new_values (JSONB)
    - Purpose: Complete audit history for compliance
    - Storage: JSONB for flexible change tracking

**Rollback Strategy**:
```sql
-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS configurations CASCADE;
DROP TABLE IF EXISTS receipts CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS fee_journals CASCADE;
DROP TABLE IF EXISTS fee_structures CASCADE;
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS guardians CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS classes CASCADE;
DROP TABLE IF EXISTS academic_years CASCADE;
DROP TABLE IF EXISTS users CASCADE;
```

---

### V3: Create Indexes (Version 3)

**Purpose**: Create performance-optimized indexes

**Index Types**:

1. **B-Tree Indexes** (default)
   - Standard indexes for equality and range queries
   - Example: `idx_students_student_code`, `idx_users_username`

2. **Partial Indexes**
   - Indexes with WHERE clause for specific data subsets
   - Example: `idx_students_active_only WHERE status = 'ACTIVE'`
   - Benefit: Smaller index size, faster queries on active data

3. **Composite Indexes**
   - Multi-column indexes for combined queries
   - Example: `idx_users_role_active ON users(role, is_active)`
   - Benefit: Optimizes queries filtering by multiple columns

4. **GIN Indexes** (Generalized Inverted Index)
   - For JSONB column queries
   - Example: `idx_audit_log_old_values_gin USING GIN (old_values)`
   - Benefit: Fast queries within JSONB data

**Key Indexes for Business Rules**:
- `idx_students_mobile_hash` - Mobile uniqueness validation (BR-2)
- `idx_classes_capacity_utilization` - Class capacity checks (BR-3)
- `idx_fee_journals_pending` - Pending payment queries
- `idx_receipts_receipt_number` - Receipt lookup (BR-11)

**Total Indexes**: 50+ indexes across all tables

**Rollback Strategy**:
```sql
-- Drop all indexes (can be recreated easily)
DROP INDEX IF EXISTS idx_students_student_code;
-- ... (drop all indexes)
```

---

### V4: Create Audit Triggers (Version 4)

**Purpose**: Implement audit trails and business logic triggers

**Trigger Functions**:

1. **audit_trigger_function()**
   - Generic audit function for all tables
   - Captures INSERT, UPDATE, DELETE operations
   - Stores old and new values as JSONB
   - Applied to: users, students, guardians, enrollments, fee_structures, fee_journals, payments, receipts

2. **update_updated_at_column()**
   - Automatically updates `updated_at` timestamp on UPDATE
   - Applied to all tables with `updated_at` column

3. **update_fee_journal_balance()**
   - Calculates balance_amount = due_amount - paid_amount
   - Updates payment status (PENDING, PARTIAL, PAID)
   - Checks for overdue status

4. **enforce_single_current_academic_year()**
   - Ensures only one academic year is marked as current
   - Automatically sets other years to non-current

5. **enforce_single_primary_guardian()**
   - Ensures only one guardian is marked as primary per student

6. **validate_payment_amount()**
   - Validates payment amount does not exceed fee journal balance (BR-9)
   - Raises exception if violation

7. **update_fee_journal_paid_amount()**
   - Updates fee journal paid_amount after payment insertion
   - Triggers balance recalculation

**Business Rules Implemented**:
- BR-9: Payment amount validation
- BR-11: Sequential receipt numbers (enforced in application)
- Auto-calculation of fee balances and status

**Rollback Strategy**:
```sql
-- Drop triggers
DROP TRIGGER IF EXISTS audit_users_trigger ON users;
-- ... (drop all triggers)

-- Drop functions
DROP FUNCTION IF EXISTS audit_trigger_function();
DROP FUNCTION IF EXISTS update_updated_at_column();
-- ... (drop all functions)
```

---

### V5: Insert Seed Data (Version 5)

**Purpose**: Insert default data for development and testing

**Data Inserted**:

1. **Default Users**:
   ```
   Username: admin
   Password: Admin@123
   Role: ADMIN

   Username: principal
   Password: Principal@123
   Role: PRINCIPAL
   ```

   **IMPORTANT**: Change these passwords immediately in production!

2. **Current Academic Year**:
   ```
   Year Code: 2024-2025
   Start Date: 2024-04-01
   End Date: 2025-03-31
   Is Current: TRUE
   ```

3. **Default Class Sections**:
   - Classes 1A to 10A
   - Max Capacity: 50 students each
   - Current Enrollment: 0

4. **System Configurations** (30+ settings):
   - General: school name, code, address, contact
   - Academic: student age limits (BR-1), class capacity (BR-3)
   - Financial: currency, receipt format (BR-11), late payment rules
   - System: session timeout, login attempts, password policies

5. **Default Fee Structures**:
   - **Tuition Fees (Monthly)**:
     - Classes 1-5: Rs. 5,000/month
     - Classes 6-8: Rs. 6,000/month
     - Classes 9-10: Rs. 7,000/month
   - **One-time Admission Fee**: Rs. 10,000 (all classes)
   - **Annual Development Fee**: Rs. 5,000 (all classes)

**Total Records**:
- Users: 2
- Academic Years: 1
- Classes: 10
- Configurations: 30+
- Fee Structures: 30 (tuition + admission + development for 10 classes)

**Rollback Strategy**:
```sql
-- Delete seed data in reverse order
DELETE FROM fee_structures WHERE created_by = 1;
DELETE FROM classes WHERE created_by = 1;
DELETE FROM configurations WHERE created_by = 1;
DELETE FROM academic_years WHERE year_code = '2024-2025';
DELETE FROM users WHERE username IN ('admin', 'principal');
```

---

## Configuration

### Application Properties

**Development** (`application-dev.yml`):
```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    baseline-version: 0
    locations: classpath:db/migration
    schemas: public
    validate-on-migrate: true
    out-of-order: false
    placeholder-replacement: true
    placeholders:
      schema: public
```

**Test** (`application-test.yml`):
```yaml
spring:
  flyway:
    enabled: true
    clean-disabled: false  # Allow clean for tests
    baseline-on-migrate: true
```

**Production** (`application-prod.yml`):
```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: false  # Don't baseline in prod
    validate-on-migrate: true
    out-of-order: false
    clean-disabled: true  # NEVER clean production!
```

### Environment Variables

```bash
# Database connection
DB_HOST=localhost
DB_PORT=5432
DB_NAME=school_management_db
DB_USERNAME=school_user
DB_PASSWORD=school_password

# Flyway settings
FLYWAY_BASELINE_VERSION=0
FLYWAY_LOCATIONS=classpath:db/migration
```

---

## Development Workflow

### Creating a New Migration

**Step 1: Determine Version Number**
```bash
# List current migrations
ls -la src/main/resources/db/migration/

# Current highest version is V5
# Next version will be V6
```

**Step 2: Create Migration File**
```bash
# Naming: V{VERSION}__{DESCRIPTION}.sql
touch src/main/resources/db/migration/V6__Add_student_email_column.sql
```

**Step 3: Write Migration SQL**
```sql
-- V6__Add_student_email_column.sql
-- Description: Add email column to students table
-- Author: Your Name
-- Date: 2025-11-11

-- Add column
ALTER TABLE students
ADD COLUMN email VARCHAR(100);

-- Add constraint
ALTER TABLE students
ADD CONSTRAINT chk_students_email
CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$');

-- Add index
CREATE INDEX idx_students_email ON students(email);

-- Success message
DO $$
BEGIN
    RAISE NOTICE 'V6 Migration: Added email column to students table';
END $$;
```

**Step 4: Test Migration Locally**
```bash
# Clean build
mvn clean

# Run migration tests
mvn test -Dtest=FlywayMigrationTest

# Start application (migrations run on startup)
mvn spring-boot:run -Dspring.profiles.active=dev
```

**Step 5: Verify Migration**
```sql
-- Check flyway_schema_history
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC;

-- Verify new column exists
\d students
```

**Step 6: Commit Migration**
```bash
git add src/main/resources/db/migration/V6__Add_student_email_column.sql
git commit -m "feat: Add email column to students table (V6 migration)"
```

### Migration Checklist

Before creating a migration, ensure:

- [ ] Version number is sequential and not already used
- [ ] File naming follows `V{VERSION}__{DESCRIPTION}.sql` format
- [ ] SQL is idempotent where possible
- [ ] Constraints and indexes are included
- [ ] Comments explain the purpose
- [ ] Rollback SQL is documented (in comments or separate file)
- [ ] Migration is tested on clean database
- [ ] Migration is tested with existing data
- [ ] No hardcoded values that differ between environments
- [ ] Success message is included

---

## Testing Migrations

### Automated Tests

Run the comprehensive Flyway migration test suite:

```bash
# Run all Flyway tests
mvn test -Dtest=FlywayMigrationTest

# Run specific test
mvn test -Dtest=FlywayMigrationTest#shouldExecuteAllMigrations
```

**Test Coverage**:
- All migrations execute successfully
- Migrations are in correct order
- All database types/enums created
- All tables created
- All indexes created
- All triggers created
- Seed data inserted correctly
- Business rules enforced (BR-1, BR-2, BR-3, BR-9, BR-11)
- No pending migrations
- Migration checksums valid

### Manual Testing

**Test on Clean Database**:
```bash
# 1. Drop and recreate database
psql -U postgres -c "DROP DATABASE IF EXISTS school_test_db;"
psql -U postgres -c "CREATE DATABASE school_test_db;"

# 2. Run application (migrations execute)
mvn spring-boot:run -Dspring.profiles.active=dev

# 3. Verify schema
psql -U school_user -d school_test_db -c "\dt"
psql -U school_user -d school_test_db -c "\dT"
```

**Test on Existing Database**:
```bash
# 1. Backup existing database
pg_dump -U school_user school_dev_db > backup_before_migration.sql

# 2. Run new migration
mvn spring-boot:run -Dspring.profiles.active=dev

# 3. Verify changes
psql -U school_user -d school_dev_db

# 4. Rollback if needed (restore backup)
psql -U school_user -d school_dev_db < backup_before_migration.sql
```

### Integration Testing with TestContainers

The project uses TestContainers for integration testing:

```java
@Container
static PostgreSQLContainer<?> postgresContainer =
    new PostgreSQLContainer<>("postgres:18")
        .withDatabaseName("school_test_db")
        .withUsername("test_user")
        .withPassword("test_password");
```

Benefits:
- Isolated test database
- Consistent test environment
- PostgreSQL 18 (same as production)
- Automatic cleanup after tests

---

## Production Deployment

### Pre-Deployment Checklist

- [ ] All migrations tested on staging environment
- [ ] Database backup taken
- [ ] Rollback plan prepared
- [ ] Downtime scheduled (if required)
- [ ] Migration execution time estimated
- [ ] Team notified of deployment
- [ ] Monitoring alerts configured

### Deployment Process

**Step 1: Backup Production Database**
```bash
# Full database backup
pg_dump -U school_user -h prod-db-host -F c school_production_db \
  > school_production_db_backup_$(date +%Y%m%d_%H%M%S).dump

# Verify backup
pg_restore --list school_production_db_backup_*.dump
```

**Step 2: Deploy Application**
```bash
# Build production artifact
mvn clean package -Pprod -DskipTests

# Deploy JAR to production server
scp target/school-management-system-1.0.0-SNAPSHOT.jar prod-server:/app/

# Restart application (migrations run on startup)
ssh prod-server "systemctl restart school-management-app"
```

**Step 3: Monitor Migration Execution**
```bash
# Tail application logs
ssh prod-server "tail -f /var/log/school-management/application.log"

# Check Flyway history
psql -U school_user -h prod-db-host -d school_production_db \
  -c "SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;"
```

**Step 4: Verify Deployment**
```bash
# Health check
curl https://prod-api.school.com/actuator/health

# Database schema check
psql -U school_user -h prod-db-host -d school_production_db -c "\dt"

# Application smoke tests
curl https://prod-api.school.com/api/actuator/info
```

### Blue-Green Deployment

For zero-downtime deployments:

1. **Green environment** deploys new version with migrations
2. **Migration runs** on shared database
3. **Blue and Green** can coexist (backward compatible migrations)
4. **Traffic switches** to Green
5. **Blue environment** retired

**Requirement**: Migrations must be backward compatible with previous application version.

---

## Rollback Strategy

### When to Rollback

- Migration fails during execution
- Migration causes application errors
- Data corruption detected
- Performance degradation after migration

### Rollback Options

**Option 1: Application Rollback (Preferred)**
```bash
# 1. Restore previous application version
ssh prod-server "systemctl stop school-management-app"
scp target/school-management-system-previous-version.jar prod-server:/app/
ssh prod-server "systemctl start school-management-app"

# 2. Database remains in new state
# Application works with new schema (backward compatible design)
```

**Option 2: Database Rollback (Last Resort)**
```bash
# 1. Stop application
ssh prod-server "systemctl stop school-management-app"

# 2. Restore database backup
pg_restore -U school_user -h prod-db-host -d school_production_db \
  -c school_production_db_backup_20251111.dump

# 3. Clear Flyway history for rolled-back migration
psql -U school_user -h prod-db-host -d school_production_db \
  -c "DELETE FROM flyway_schema_history WHERE version >= '6';"

# 4. Start application
ssh prod-server "systemctl start school-management-app"
```

**Option 3: Forward Fix (Recommended for Minor Issues)**
```bash
# Create compensating migration
touch src/main/resources/db/migration/V7__Rollback_V6_changes.sql

# Example content:
ALTER TABLE students DROP COLUMN email;
DROP INDEX IF EXISTS idx_students_email;
```

### Backward Compatibility Guidelines

To minimize rollback needs, migrations should:

1. **Add columns with defaults**: `ALTER TABLE students ADD COLUMN email VARCHAR(100) DEFAULT NULL;`
2. **Make columns nullable initially**: Don't add NOT NULL in same migration as column creation
3. **Use multi-step migrations**:
   - V6: Add column
   - V7: Populate data
   - V8: Add NOT NULL constraint
4. **Avoid renaming**: Create new column, copy data, deprecate old column, remove in later migration
5. **Test with old app version**: Ensure old app still works with new schema

---

## Troubleshooting

### Common Issues

**Issue 1: Migration Fails with "Checksum Mismatch"**

**Cause**: Migration file was modified after being applied

**Solution**:
```sql
-- Option A: Repair checksum (if change was intentional and safe)
-- Use Flyway CLI or application property
spring.flyway.repair=true

-- Option B: Revert file to original content
git checkout src/main/resources/db/migration/V3__Create_indexes.sql
```

**Issue 2: "Found non-empty schema without schema history table"**

**Cause**: Existing database without Flyway history

**Solution**:
```yaml
# Enable baseline in application.yml
spring:
  flyway:
    baseline-on-migrate: true
    baseline-version: 0
```

**Issue 3: Migration Fails Mid-Execution**

**Cause**: SQL syntax error or constraint violation

**Solution**:
```bash
# 1. Check Flyway history
SELECT * FROM flyway_schema_history WHERE success = false;

# 2. Fix migration file

# 3. Mark migration as fixed (or use repair)
UPDATE flyway_schema_history SET success = true WHERE version = '6';

# OR use Flyway repair
mvn flyway:repair
```

**Issue 4: Out-of-Order Migration**

**Cause**: Added migration with version number lower than latest applied

**Solution**:
```yaml
# Allow out-of-order migrations (not recommended for production)
spring:
  flyway:
    out-of-order: true
```

**Best Practice**: Always use next sequential version number

**Issue 5: Migration Too Slow**

**Cause**: Large data migration or index creation

**Solution**:
```sql
-- Use CREATE INDEX CONCURRENTLY (doesn't lock table)
CREATE INDEX CONCURRENTLY idx_students_email ON students(email);

-- Split large data migrations into batches
DO $$
DECLARE
    batch_size INT := 1000;
BEGIN
    LOOP
        UPDATE students SET email = CONCAT(student_code, '@temp.com')
        WHERE student_id IN (
            SELECT student_id FROM students WHERE email IS NULL LIMIT batch_size
        );

        EXIT WHEN NOT FOUND;
        COMMIT;
    END LOOP;
END $$;
```

**Issue 6: Permissions Error**

**Cause**: Database user lacks necessary privileges

**Solution**:
```sql
-- Grant required permissions
GRANT CREATE ON DATABASE school_production_db TO school_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO school_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO school_user;
```

---

## Best Practices

### Migration Design

1. **One Logical Change Per Migration**
   - Bad: V6 adds column, creates table, modifies constraint
   - Good: V6 adds column, V7 creates table, V8 modifies constraint

2. **Idempotent Operations**
   ```sql
   -- Use IF NOT EXISTS
   CREATE TABLE IF NOT EXISTS new_table (...);

   -- Use IF EXISTS for drops
   DROP TABLE IF EXISTS old_table;

   -- Check before adding column
   DO $$
   BEGIN
       IF NOT EXISTS (
           SELECT 1 FROM information_schema.columns
           WHERE table_name = 'students' AND column_name = 'email'
       ) THEN
           ALTER TABLE students ADD COLUMN email VARCHAR(100);
       END IF;
   END $$;
   ```

3. **Include Rollback Comments**
   ```sql
   -- Migration: V6__Add_student_email_column.sql
   -- Rollback: ALTER TABLE students DROP COLUMN email;

   ALTER TABLE students ADD COLUMN email VARCHAR(100);
   ```

4. **Use Transactions Wisely**
   - PostgreSQL wraps each migration in a transaction by default
   - For long-running migrations, consider splitting
   - Some DDL operations cannot be rolled back

5. **Document Business Rules**
   ```sql
   -- BR-2: Mobile number uniqueness validation
   CREATE UNIQUE INDEX idx_students_mobile_hash ON students(mobile_hash);

   -- BR-3: Class capacity enforcement
   ALTER TABLE classes ADD CONSTRAINT chk_classes_enrollment
   CHECK (current_enrollment <= max_capacity);
   ```

### Performance Optimization

1. **Create Indexes in Separate Migration** (V3)
2. **Use Partial Indexes** for filtered queries
3. **Use Composite Indexes** for multi-column queries
4. **Create Indexes CONCURRENTLY** in production
5. **Analyze Tables** after large data changes
   ```sql
   ANALYZE students;
   ```

### Security

1. **Encrypt PII Fields** (implemented in V2)
2. **Hash Sensitive Data** (mobile_hash for uniqueness)
3. **Use Prepared Statements** (handled by JPA)
4. **Limit Database User Permissions**
5. **Audit All Changes** (implemented in V4)

### Testing

1. **Test on Clean Database**
2. **Test with Sample Data**
3. **Test Rollback Procedure**
4. **Test Performance Impact**
5. **Automate Tests** (FlywayMigrationTest)

### Version Control

1. **Never Modify Applied Migrations**
2. **Use Descriptive Commit Messages**
   ```
   feat: Add email column to students table (V6)
   fix: Correct student age constraint (V7)
   ```
3. **Tag Releases**
   ```bash
   git tag -a v1.0.0 -m "Release 1.0.0 with migrations V1-V5"
   ```
4. **Branch Strategy**
   ```
   main (production migrations)
     └── develop (new migrations)
          └── feature/add-email-field (V6)
   ```

---

## Additional Resources

### Flyway Documentation
- [Official Flyway Docs](https://flywaydb.org/documentation/)
- [Flyway with Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)

### PostgreSQL Resources
- [PostgreSQL 18 Documentation](https://www.postgresql.org/docs/18/)
- [PostgreSQL Best Practices](https://wiki.postgresql.org/wiki/Don%27t_Do_This)

### Project Documentation
- [Database Schema Design](./DATABASE_SCHEMA.md)
- [Business Rules](../../REQUIREMENTS.md)
- [Backend Architecture](../../specs/architecture/docs/backend-implementation-guide.md)

---

## Maintenance

### Regular Tasks

**Weekly**:
- Review Flyway schema history for anomalies
- Check for failed migrations in logs

**Monthly**:
- Vacuum and analyze database
- Review index usage statistics
- Archive old audit logs

**Quarterly**:
- Review and optimize slow migrations
- Update migration documentation
- Test backup/restore procedure

### Monitoring

**Metrics to Track**:
- Migration execution time
- Number of pending migrations
- Failed migration count
- Database size growth

**Alerts to Configure**:
- Migration failure
- Migration timeout (> 5 minutes)
- Checksum mismatch
- Out-of-order migration detected

---

## Contact

For questions or issues with database migrations:

- **Email**: devteam@school-sms.com
- **Documentation**: https://school-sms.com/docs/database
- **Issue Tracker**: https://github.com/school-sms/backend/issues

---

**Document Version**: 1.0.0
**Last Updated**: 2025-11-11
**Maintained By**: School Management System Development Team
