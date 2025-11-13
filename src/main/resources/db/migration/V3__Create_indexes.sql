-- =========================================================
-- School Management System - Database Schema V3
-- Description: Create indexes for performance optimization
-- Author: Database Administrator
-- Date: 2025-11-11
-- =========================================================

-- ======================
-- USERS TABLE INDEXES
-- ======================
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_role_active ON users(role, is_active);

COMMENT ON INDEX idx_users_username IS 'Fast lookup for authentication';
COMMENT ON INDEX idx_users_role_active IS 'Composite index for filtering active users by role';

-- ======================
-- ACADEMIC YEARS TABLE INDEXES
-- ======================
CREATE INDEX idx_academic_years_year_code ON academic_years(year_code);
CREATE INDEX idx_academic_years_is_current ON academic_years(is_current);
CREATE INDEX idx_academic_years_current_only ON academic_years(academic_year_id) WHERE is_current = TRUE;

COMMENT ON INDEX idx_academic_years_current_only IS 'Partial index for current academic year queries';

-- ======================
-- CLASSES TABLE INDEXES
-- ======================
CREATE INDEX idx_classes_academic_year ON classes(academic_year_id);
CREATE INDEX idx_classes_name_section ON classes(class_name, section);
CREATE INDEX idx_classes_is_active ON classes(is_active);
CREATE INDEX idx_classes_active_only ON classes(class_id) WHERE is_active = TRUE;
CREATE INDEX idx_classes_capacity_utilization ON classes(max_capacity, current_enrollment);

COMMENT ON INDEX idx_classes_capacity_utilization IS 'For checking class capacity availability (BR-3)';

-- ======================
-- STUDENTS TABLE INDEXES
-- ======================
CREATE INDEX idx_students_student_code ON students(student_code);
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_admission_date ON students(admission_date);
CREATE INDEX idx_students_mobile_hash ON students(mobile_hash);
CREATE INDEX idx_students_active_only ON students(student_id) WHERE status = 'ACTIVE';
CREATE INDEX idx_students_created_at ON students(created_at DESC);

COMMENT ON INDEX idx_students_mobile_hash IS 'For mobile uniqueness validation (BR-2)';
COMMENT ON INDEX idx_students_active_only IS 'Partial index for active students queries';

-- ======================
-- GUARDIANS TABLE INDEXES
-- ======================
CREATE INDEX idx_guardians_student_id ON guardians(student_id);
CREATE INDEX idx_guardians_is_primary ON guardians(is_primary);
CREATE INDEX idx_guardians_mobile_hash ON guardians(mobile_hash);
CREATE INDEX idx_guardians_primary_only ON guardians(guardian_id) WHERE is_primary = TRUE;

COMMENT ON INDEX idx_guardians_primary_only IS 'Partial index for primary guardian lookups';

-- ======================
-- ENROLLMENTS TABLE INDEXES
-- ======================
CREATE INDEX idx_enrollments_student_id ON enrollments(student_id);
CREATE INDEX idx_enrollments_class_id ON enrollments(class_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);
CREATE INDEX idx_enrollments_enrollment_date ON enrollments(enrollment_date);
CREATE INDEX idx_enrollments_active_only ON enrollments(enrollment_id) WHERE status = 'ENROLLED';

COMMENT ON INDEX idx_enrollments_active_only IS 'Partial index for currently enrolled students';

-- ======================
-- FEE STRUCTURES TABLE INDEXES
-- ======================
CREATE INDEX idx_fee_structures_class_name ON fee_structures(class_name);
CREATE INDEX idx_fee_structures_academic_year ON fee_structures(academic_year_id);
CREATE INDEX idx_fee_structures_fee_type ON fee_structures(fee_type);
CREATE INDEX idx_fee_structures_is_active ON fee_structures(is_active);
CREATE INDEX idx_fee_structures_effective_dates ON fee_structures(effective_from, effective_to);
CREATE INDEX idx_fee_structures_active_only ON fee_structures(fee_structure_id) WHERE is_active = TRUE;

COMMENT ON INDEX idx_fee_structures_effective_dates IS 'For date range queries on fee validity';

-- ======================
-- FEE JOURNALS TABLE INDEXES
-- ======================
CREATE INDEX idx_fee_journals_student_id ON fee_journals(student_id);
CREATE INDEX idx_fee_journals_fee_structure_id ON fee_journals(fee_structure_id);
CREATE INDEX idx_fee_journals_academic_year_id ON fee_journals(academic_year_id);
CREATE INDEX idx_fee_journals_status ON fee_journals(status);
CREATE INDEX idx_fee_journals_due_date ON fee_journals(due_date);
CREATE INDEX idx_fee_journals_is_overdue ON fee_journals(is_overdue);
CREATE INDEX idx_fee_journals_month ON fee_journals(month);
CREATE INDEX idx_fee_journals_pending ON fee_journals(fee_journal_id) WHERE status IN ('PENDING', 'PARTIAL');
CREATE INDEX idx_fee_journals_overdue_only ON fee_journals(fee_journal_id) WHERE is_overdue = TRUE;
CREATE INDEX idx_fee_journals_student_status ON fee_journals(student_id, status);

COMMENT ON INDEX idx_fee_journals_pending IS 'Partial index for pending payments queries';
COMMENT ON INDEX idx_fee_journals_overdue_only IS 'Partial index for overdue report generation';

-- ======================
-- PAYMENTS TABLE INDEXES
-- ======================
CREATE INDEX idx_payments_fee_journal_id ON payments(fee_journal_id);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);
CREATE INDEX idx_payments_payment_mode ON payments(payment_mode);
CREATE INDEX idx_payments_created_at ON payments(created_at DESC);
CREATE INDEX idx_payments_transaction_ref ON payments(transaction_reference);

COMMENT ON INDEX idx_payments_payment_date IS 'For daily collection reports';

-- ======================
-- RECEIPTS TABLE INDEXES
-- ======================
CREATE INDEX idx_receipts_receipt_number ON receipts(receipt_number);
CREATE INDEX idx_receipts_student_id ON receipts(student_id);
CREATE INDEX idx_receipts_payment_id ON receipts(payment_id);
CREATE INDEX idx_receipts_receipt_date ON receipts(receipt_date);
CREATE INDEX idx_receipts_academic_year_id ON receipts(academic_year_id);
CREATE INDEX idx_receipts_created_at ON receipts(created_at DESC);

COMMENT ON INDEX idx_receipts_receipt_number IS 'Fast receipt lookup (BR-11)';

-- ======================
-- CONFIGURATIONS TABLE INDEXES
-- ======================
CREATE INDEX idx_configurations_config_key ON configurations(config_key);
CREATE INDEX idx_configurations_category ON configurations(category);
CREATE INDEX idx_configurations_is_editable ON configurations(is_editable);

-- ======================
-- AUDIT LOG TABLE INDEXES
-- ======================
CREATE INDEX idx_audit_log_table_name ON audit_log(table_name);
CREATE INDEX idx_audit_log_record_id ON audit_log(record_id);
CREATE INDEX idx_audit_log_changed_by ON audit_log(changed_by);
CREATE INDEX idx_audit_log_changed_at ON audit_log(changed_at DESC);
CREATE INDEX idx_audit_log_table_record ON audit_log(table_name, record_id);
CREATE INDEX idx_audit_log_action ON audit_log(action);

COMMENT ON INDEX idx_audit_log_table_record IS 'Composite index for audit trail queries';
COMMENT ON INDEX idx_audit_log_changed_at IS 'For recent changes queries';

-- ======================
-- GIN Indexes for JSONB Columns
-- ======================
CREATE INDEX idx_audit_log_old_values_gin ON audit_log USING GIN (old_values);
CREATE INDEX idx_audit_log_new_values_gin ON audit_log USING GIN (new_values);

COMMENT ON INDEX idx_audit_log_old_values_gin IS 'For querying within JSONB old values';
COMMENT ON INDEX idx_audit_log_new_values_gin IS 'For querying within JSONB new values';

-- ======================
-- Success Message
-- ======================
DO $$
BEGIN
    RAISE NOTICE 'V3 Migration: Indexes created successfully';
    RAISE NOTICE 'Total Indexes Created: 50+';
END $$;
