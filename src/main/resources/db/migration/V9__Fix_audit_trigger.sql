-- =========================================================
-- School Management System - Database Schema V6
-- Description: Fix audit trigger function to use JSON field access
-- Author: Database Administrator
-- Date: 2025-11-12
-- =========================================================

-- ======================
-- FIX AUDIT TRIGGER FUNCTION
-- ======================
-- Step 1: Make changed_by nullable and set default value
ALTER TABLE audit_log 
ALTER COLUMN changed_by DROP NOT NULL;

ALTER TABLE audit_log 
ALTER COLUMN changed_by SET DEFAULT 0;

-- Step 2: Drop existing trigger and function to ensure clean state
DROP TRIGGER IF EXISTS audit_trigger ON users;
DROP FUNCTION IF EXISTS audit_trigger_function() CASCADE;

-- Step 3: Recreate the audit trigger function with proper changed_by handling
CREATE OR REPLACE FUNCTION audit_trigger_function()
RETURNS TRIGGER AS $$
DECLARE
    record_id_value BIGINT;
    action_type VARCHAR(10);
    old_data JSONB;
    new_data JSONB;
    changed_by_id BIGINT := 0; -- Initialize with default value
BEGIN
    -- Try to get current user from session, use 0 as fallback
    BEGIN
        changed_by_id := COALESCE(
            current_setting('app.current_user_id', true)::BIGINT,
            0
        );
    EXCEPTION
        WHEN OTHERS THEN
            changed_by_id := 0;
    END;

    -- Determine action and set values
    IF (TG_OP = 'DELETE') THEN
        record_id_value := OLD.id;
        action_type := 'DELETE';
        old_data := to_jsonb(OLD);
        new_data := NULL;
    ELSIF (TG_OP = 'UPDATE') THEN
        record_id_value := NEW.id;
        action_type := 'UPDATE';
        old_data := to_jsonb(OLD);
        new_data := to_jsonb(NEW);
    ELSIF (TG_OP = 'INSERT') THEN
        record_id_value := NEW.id;
        action_type := 'INSERT';
        old_data := NULL;
        new_data := to_jsonb(NEW);
    END IF;

    -- Insert audit log entry with explicit changed_by value
    INSERT INTO audit_log (
        table_name,
        record_id,
        action,
        old_values,
        new_values,
        changed_by,
        changed_at
    ) VALUES (
        TG_TABLE_NAME::VARCHAR,
        record_id_value,
        action_type,
        old_data,
        new_data,
        COALESCE(changed_by_id, 0), -- Extra safety
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

-- Step 4: Create trigger for users table
CREATE TRIGGER audit_trigger
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW
    EXECUTE FUNCTION audit_trigger_function();

-- Step 5: Clean up any existing NULL values in audit_log
UPDATE audit_log SET changed_by = 0 WHERE changed_by IS NULL;

-- ======================
-- Success Message
-- ======================
-- Note: Success messages are automatically shown by the migration execution
