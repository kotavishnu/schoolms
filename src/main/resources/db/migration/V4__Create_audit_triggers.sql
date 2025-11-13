-- =========================================================
-- School Management System - Database Schema V4
-- Description: Create audit triggers for critical tables
-- Author: Database Administrator
-- Date: 2025-11-11
-- =========================================================

-- ======================
-- AUDIT TRIGGER FUNCTION
-- ======================
CREATE OR REPLACE FUNCTION audit_trigger_function()
RETURNS TRIGGER AS $$
DECLARE
    old_data JSONB;
    new_data JSONB;
    changed_by_id BIGINT;
    action_type audit_action;
    record_id_value BIGINT;
BEGIN
    -- Determine action type and extract record ID using JSONB field access
    IF (TG_OP = 'INSERT') THEN
        action_type := 'INSERT'::audit_action;
        old_data := NULL;
        new_data := to_jsonb(NEW);
        changed_by_id := (new_data->>'created_by')::BIGINT;
        -- Extract primary key based on table name using JSONB
        record_id_value := (new_data->>CASE TG_TABLE_NAME
            WHEN 'users' THEN 'user_id'
            WHEN 'students' THEN 'student_id'
            WHEN 'guardians' THEN 'guardian_id'
            WHEN 'enrollments' THEN 'enrollment_id'
            WHEN 'fee_structures' THEN 'fee_structure_id'
            WHEN 'fee_journals' THEN 'fee_journal_id'
            WHEN 'payments' THEN 'payment_id'
            WHEN 'receipts' THEN 'receipt_id'
            ELSE NULL
        END)::BIGINT;
    ELSIF (TG_OP = 'UPDATE') THEN
        action_type := 'UPDATE'::audit_action;
        old_data := to_jsonb(OLD);
        new_data := to_jsonb(NEW);
        changed_by_id := (new_data->>'updated_by')::BIGINT;
        record_id_value := (new_data->>CASE TG_TABLE_NAME
            WHEN 'users' THEN 'user_id'
            WHEN 'students' THEN 'student_id'
            WHEN 'guardians' THEN 'guardian_id'
            WHEN 'enrollments' THEN 'enrollment_id'
            WHEN 'fee_structures' THEN 'fee_structure_id'
            WHEN 'fee_journals' THEN 'fee_journal_id'
            WHEN 'payments' THEN 'payment_id'
            WHEN 'receipts' THEN 'receipt_id'
            ELSE NULL
        END)::BIGINT;
    ELSIF (TG_OP = 'DELETE') THEN
        action_type := 'DELETE'::audit_action;
        old_data := to_jsonb(OLD);
        new_data := NULL;
        changed_by_id := (old_data->>'updated_by')::BIGINT;
        record_id_value := (old_data->>CASE TG_TABLE_NAME
            WHEN 'users' THEN 'user_id'
            WHEN 'students' THEN 'student_id'
            WHEN 'guardians' THEN 'guardian_id'
            WHEN 'enrollments' THEN 'enrollment_id'
            WHEN 'fee_structures' THEN 'fee_structure_id'
            WHEN 'fee_journals' THEN 'fee_journal_id'
            WHEN 'payments' THEN 'payment_id'
            WHEN 'receipts' THEN 'receipt_id'
            ELSE NULL
        END)::BIGINT;
    END IF;

    -- Insert audit log entry
    INSERT INTO audit_log (
        table_name,
        record_id,
        action,
        old_values,
        new_values,
        changed_by,
        changed_at
    ) VALUES (
        TG_TABLE_NAME,
        record_id_value,
        action_type,
        old_data,
        new_data,
        changed_by_id,
        CURRENT_TIMESTAMP
    );

    -- Return appropriate value
    IF (TG_OP = 'DELETE') THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION audit_trigger_function() IS 'Generic audit trigger function for all tables';

-- ======================
-- CREATE AUDIT TRIGGERS
-- ======================

-- Drop existing triggers if they exist
DROP TRIGGER IF EXISTS audit_users_trigger ON users;
DROP TRIGGER IF EXISTS audit_students_trigger ON students;
DROP TRIGGER IF EXISTS audit_guardians_trigger ON guardians;
DROP TRIGGER IF EXISTS audit_enrollments_trigger ON enrollments;
DROP TRIGGER IF EXISTS audit_fee_structures_trigger ON fee_structures;
DROP TRIGGER IF EXISTS audit_fee_journals_trigger ON fee_journals;
DROP TRIGGER IF EXISTS audit_payments_trigger ON payments;
DROP TRIGGER IF EXISTS audit_receipts_trigger ON receipts;

-- Users audit trigger
CREATE TRIGGER audit_users_trigger
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- Students audit trigger
CREATE TRIGGER audit_students_trigger
    AFTER INSERT OR UPDATE OR DELETE ON students
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- Guardians audit trigger
CREATE TRIGGER audit_guardians_trigger
    AFTER INSERT OR UPDATE OR DELETE ON guardians
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- Enrollments audit trigger
CREATE TRIGGER audit_enrollments_trigger
    AFTER INSERT OR UPDATE OR DELETE ON enrollments
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- Fee structures audit trigger
CREATE TRIGGER audit_fee_structures_trigger
    AFTER INSERT OR UPDATE OR DELETE ON fee_structures
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- Fee journals audit trigger
CREATE TRIGGER audit_fee_journals_trigger
    AFTER INSERT OR UPDATE OR DELETE ON fee_journals
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- Payments audit trigger
CREATE TRIGGER audit_payments_trigger
    AFTER INSERT OR UPDATE OR DELETE ON payments
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- Receipts audit trigger
CREATE TRIGGER audit_receipts_trigger
    AFTER INSERT OR UPDATE OR DELETE ON receipts
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

-- ======================
-- FUNCTION: Update balance amount
-- ======================
CREATE OR REPLACE FUNCTION update_fee_journal_balance()
RETURNS TRIGGER AS $$
BEGIN
    NEW.balance_amount := NEW.due_amount - NEW.paid_amount;

    -- Update status based on paid amount
    IF NEW.paid_amount = 0 THEN
        NEW.status := 'PENDING'::payment_status;
    ELSIF NEW.paid_amount < NEW.due_amount THEN
        NEW.status := 'PARTIAL'::payment_status;
    ELSIF NEW.paid_amount >= NEW.due_amount THEN
        NEW.status := 'PAID'::payment_status;
    END IF;

    -- Check for overdue
    IF NEW.due_date < CURRENT_DATE AND NEW.balance_amount > 0 THEN
        NEW.is_overdue := TRUE;
    ELSE
        NEW.is_overdue := FALSE;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_fee_journal_balance_trigger
    BEFORE INSERT OR UPDATE ON fee_journals
    FOR EACH ROW EXECUTE FUNCTION update_fee_journal_balance();

COMMENT ON FUNCTION update_fee_journal_balance() IS 'Automatically calculates balance and updates status';

-- ======================
-- FUNCTION: Update updated_at timestamp
-- ======================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply updated_at trigger to all tables with updated_at column
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_academic_years_updated_at
    BEFORE UPDATE ON academic_years
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_classes_updated_at
    BEFORE UPDATE ON classes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_students_updated_at
    BEFORE UPDATE ON students
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_guardians_updated_at
    BEFORE UPDATE ON guardians
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_enrollments_updated_at
    BEFORE UPDATE ON enrollments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_fee_structures_updated_at
    BEFORE UPDATE ON fee_structures
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_fee_journals_updated_at
    BEFORE UPDATE ON fee_journals
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_payments_updated_at
    BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_configurations_updated_at
    BEFORE UPDATE ON configurations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ======================
-- FUNCTION: Enforce single current academic year
-- ======================
CREATE OR REPLACE FUNCTION enforce_single_current_academic_year()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_current = TRUE THEN
        -- Set all other academic years to non-current
        UPDATE academic_years
        SET is_current = FALSE
        WHERE is_current = TRUE AND academic_year_id <> NEW.academic_year_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER enforce_single_current_academic_year_trigger
    BEFORE INSERT OR UPDATE ON academic_years
    FOR EACH ROW
    WHEN (NEW.is_current = TRUE)
    EXECUTE FUNCTION enforce_single_current_academic_year();

COMMENT ON FUNCTION enforce_single_current_academic_year() IS 'Ensures only one academic year is marked as current';

-- ======================
-- FUNCTION: Enforce single primary guardian per student
-- ======================
CREATE OR REPLACE FUNCTION enforce_single_primary_guardian()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_primary = TRUE THEN
        -- Set all other guardians of the same student to non-primary
        UPDATE guardians
        SET is_primary = FALSE
        WHERE student_id = NEW.student_id
          AND is_primary = TRUE
          AND guardian_id <> COALESCE(NEW.guardian_id, -1);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER enforce_single_primary_guardian_trigger
    BEFORE INSERT OR UPDATE ON guardians
    FOR EACH ROW
    WHEN (NEW.is_primary = TRUE)
    EXECUTE FUNCTION enforce_single_primary_guardian();

COMMENT ON FUNCTION enforce_single_primary_guardian() IS 'Ensures only one guardian is marked as primary per student';

-- ======================
-- FUNCTION: Validate payment amount
-- ======================
CREATE OR REPLACE FUNCTION validate_payment_amount()
RETURNS TRIGGER AS $$
DECLARE
    journal_balance DECIMAL(10, 2);
BEGIN
    -- Get current balance from fee journal
    SELECT balance_amount INTO journal_balance
    FROM fee_journals
    WHERE fee_journal_id = NEW.fee_journal_id;

    -- Validate payment amount doesn't exceed balance (BR-9)
    IF NEW.amount > journal_balance THEN
        RAISE EXCEPTION 'Payment amount (%) exceeds fee journal balance (%). BR-9 violation.',
            NEW.amount, journal_balance;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER validate_payment_amount_trigger
    BEFORE INSERT ON payments
    FOR EACH ROW EXECUTE FUNCTION validate_payment_amount();

COMMENT ON FUNCTION validate_payment_amount() IS 'Validates payment amount does not exceed due amount (BR-9)';

-- ======================
-- FUNCTION: Update paid amount in fee journal after payment
-- ======================
CREATE OR REPLACE FUNCTION update_fee_journal_paid_amount()
RETURNS TRIGGER AS $$
BEGIN
    -- Update paid amount in fee journal
    UPDATE fee_journals
    SET paid_amount = paid_amount + NEW.amount
    WHERE fee_journal_id = NEW.fee_journal_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_fee_journal_paid_amount_trigger
    AFTER INSERT ON payments
    FOR EACH ROW EXECUTE FUNCTION update_fee_journal_paid_amount();

COMMENT ON FUNCTION update_fee_journal_paid_amount() IS 'Updates fee journal paid amount after payment insertion';

-- ======================
-- Success Message
-- ======================
DO $$
BEGIN
    RAISE NOTICE 'V4 Migration: Audit triggers and business logic triggers created successfully';
    RAISE NOTICE 'Audit triggers applied to: users, students, guardians, enrollments, fee_structures, fee_journals, payments, receipts';
    RAISE NOTICE 'Business logic triggers: balance calculation, single current academic year, single primary guardian, payment validation';
END $$;
