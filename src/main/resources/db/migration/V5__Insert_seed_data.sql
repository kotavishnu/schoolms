-- =========================================================
-- School Management System - Database Schema V5
-- Description: Insert seed/default data
-- Author: Database Administrator
-- Date: 2025-11-11
-- =========================================================

-- ======================
-- 1. INSERT DEFAULT ADMIN USER
-- ======================
-- Password: Admin@123 (BCrypt hashed with 12 rounds)
-- Note: MUST change on first login in production

-- Temporarily disable the audit trigger for initial user insert
DROP TRIGGER IF EXISTS audit_trigger ON users;

-- Insert admin user with created_by/updated_by = 1 (will self-reference)
INSERT INTO users (
    user_id,
    username,
    password_hash,
    full_name,
    email,
    mobile,
    role,
    is_active,
    password_changed_at,
    created_by,
    updated_by
) VALUES (
    1,
    'admin',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIg8TvQZjS',  -- Admin@123
    'System Administrator',
    'admin@school-sms.com',
    '9999999999',
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP,
    1,  -- Self-reference for audit
    1   -- Self-reference for audit
);

-- Reset the sequence to continue from 2
SELECT setval('users_id_seq', 1, true);

-- Re-enable the audit trigger
CREATE TRIGGER audit_trigger
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW
    EXECUTE FUNCTION audit_trigger_function();

-- ======================
-- 2. INSERT DEFAULT PRINCIPAL USER
-- ======================
-- Password: Principal@123
INSERT INTO users (
    username,
    password_hash,
    full_name,
    email,
    mobile,
    role,
    is_active,
    password_changed_at,
    created_by,
    updated_by
) VALUES (
    'principal',
    '$2a$12$p4C7QaVxT5qvKZjJKF.K5OGx5L2QzB8N9wYvZqXjKv2L3M8NpQrXW',  -- Principal@123
    'School Principal',
    'principal@school-sms.com',
    '9999999998',
    'PRINCIPAL',
    TRUE,
    CURRENT_TIMESTAMP,
    1,  -- Created by admin
    1
);

-- ======================
-- 3. INSERT CURRENT ACADEMIC YEAR
-- ======================
INSERT INTO academic_years (
    year_code,
    start_date,
    end_date,
    is_current,
    created_by,
    updated_by
) VALUES (
    '2024-2025',
    '2024-04-01',
    '2025-03-31',
    TRUE,
    1,
    1
);

-- ======================
-- 4. INSERT DEFAULT SYSTEM CONFIGURATIONS
-- ======================
INSERT INTO configurations (config_key, config_value, category, description, is_editable, created_by, updated_by)
VALUES
    -- General Configuration
    ('school.name', 'School Management System', 'GENERAL', 'School name displayed throughout the application', TRUE, 1, 1),
    ('school.code', 'SMS001', 'GENERAL', 'Unique school code', FALSE, 1, 1),
    ('school.address', '123 School Street, City, State - 123456', 'GENERAL', 'School physical address', TRUE, 1, 1),
    ('school.phone', '0123-456789', 'GENERAL', 'School contact number', TRUE, 1, 1),
    ('school.email', 'info@school-sms.com', 'GENERAL', 'School email address', TRUE, 1, 1),
    ('school.website', 'https://school-sms.com', 'GENERAL', 'School website URL', TRUE, 1, 1),

    -- Academic Configuration
    ('academic.student_code_prefix', 'STU', 'ACADEMIC', 'Prefix for student codes', FALSE, 1, 1),
    ('academic.student_code_length', '5', 'ACADEMIC', 'Number of digits in student code sequence', FALSE, 1, 1),
    ('academic.min_student_age', '3', 'ACADEMIC', 'Minimum age for student admission (BR-1)', FALSE, 1, 1),
    ('academic.max_student_age', '18', 'ACADEMIC', 'Maximum age for student admission (BR-1)', FALSE, 1, 1),
    ('academic.max_class_capacity', '100', 'ACADEMIC', 'Maximum capacity per class (BR-3)', TRUE, 1, 1),
    ('academic.default_class_capacity', '50', 'ACADEMIC', 'Default class capacity when creating new classes', TRUE, 1, 1),

    -- Financial Configuration
    ('financial.currency', 'INR', 'FINANCIAL', 'Default currency code', FALSE, 1, 1),
    ('financial.currency_symbol', '₹', 'FINANCIAL', 'Currency symbol for display', FALSE, 1, 1),
    ('financial.receipt_prefix', 'REC', 'FINANCIAL', 'Prefix for receipt numbers (BR-11)', FALSE, 1, 1),
    ('financial.receipt_number_length', '5', 'FINANCIAL', 'Number of digits in receipt sequence', FALSE, 1, 1),
    ('financial.late_payment_days', '15', 'FINANCIAL', 'Days after due date to mark as overdue', TRUE, 1, 1),
    ('financial.late_payment_penalty_percent', '2', 'FINANCIAL', 'Percentage penalty for late payments', TRUE, 1, 1),
    ('financial.tax_applicable', 'false', 'FINANCIAL', 'Whether tax is applicable on fees', TRUE, 1, 1),
    ('financial.tax_percentage', '0', 'FINANCIAL', 'Tax percentage if applicable', TRUE, 1, 1),

    -- System Configuration
    ('system.session_timeout_minutes', '30', 'SYSTEM', 'Session timeout in minutes', TRUE, 1, 1),
    ('system.max_login_attempts', '5', 'SYSTEM', 'Maximum failed login attempts before lockout', TRUE, 1, 1),
    ('system.account_lockout_duration_minutes', '30', 'SYSTEM', 'Duration of account lockout after max failed attempts', TRUE, 1, 1),
    ('system.password_expiry_days', '90', 'SYSTEM', 'Days until password expiry', TRUE, 1, 1),
    ('system.password_min_length', '8', 'SYSTEM', 'Minimum password length', TRUE, 1, 1),
    ('system.backup_enabled', 'true', 'SYSTEM', 'Enable automated database backups', TRUE, 1, 1),
    ('system.backup_time', '02:00', 'SYSTEM', 'Time for daily backup (HH:mm format)', TRUE, 1, 1),
    ('system.maintenance_mode', 'false', 'SYSTEM', 'Enable maintenance mode (blocks normal users)', TRUE, 1, 1),

    -- Notification Configuration
    ('notification.email_enabled', 'false', 'SYSTEM', 'Enable email notifications', TRUE, 1, 1),
    ('notification.sms_enabled', 'false', 'SYSTEM', 'Enable SMS notifications', TRUE, 1, 1),
    ('notification.send_fee_reminders', 'true', 'FINANCIAL', 'Send fee payment reminders', TRUE, 1, 1),
    ('notification.reminder_days_before_due', '7', 'FINANCIAL', 'Days before due date to send reminder', TRUE, 1, 1),

    -- File Upload Configuration
    ('upload.max_file_size_mb', '10', 'SYSTEM', 'Maximum file size for uploads in MB', TRUE, 1, 1),
    ('upload.allowed_photo_types', 'image/jpeg,image/png', 'SYSTEM', 'Allowed MIME types for student photos', TRUE, 1, 1),
    ('upload.allowed_document_types', 'application/pdf,image/jpeg,image/png', 'SYSTEM', 'Allowed MIME types for documents', TRUE, 1, 1);

-- ======================
-- 5. INSERT DEFAULT CLASS SECTIONS FOR CURRENT YEAR
-- ======================
-- Create default class sections (Class 1-10, Section A) for current academic year
DO $$
DECLARE
    current_year_id BIGINT;
    class_num INT;
BEGIN
    -- Get current academic year ID
    SELECT academic_year_id INTO current_year_id
    FROM academic_years
    WHERE is_current = TRUE
    LIMIT 1;

    -- Insert default sections for classes 1-10
    FOR class_num IN 1..10 LOOP
        INSERT INTO classes (
            class_name,
            section,
            academic_year_id,
            max_capacity,
            current_enrollment,
            is_active,
            created_by,
            updated_by
        ) VALUES (
            class_num::VARCHAR::class_name,
            'A',
            current_year_id,
            50,  -- Default capacity
            0,   -- No enrollments yet
            TRUE,
            1,   -- Created by admin
            1
        );
    END LOOP;

    RAISE NOTICE 'Created default class sections (1A - 10A) for academic year 2024-2025';
END $$;

-- ======================
-- 6. INSERT DEFAULT FEE STRUCTURES
-- ======================
-- Insert default tuition fee structure for all classes
DO $$
DECLARE
    current_year_id BIGINT;
    class_num INT;
BEGIN
    -- Get current academic year ID
    SELECT academic_year_id INTO current_year_id
    FROM academic_years
    WHERE is_current = TRUE
    LIMIT 1;

    -- Insert tuition fees for classes 1-5 (Primary)
    FOR class_num IN 1..5 LOOP
        INSERT INTO fee_structures (
            fee_type,
            class_name,
            academic_year_id,
            amount,
            frequency,
            is_mandatory,
            effective_from,
            is_active,
            description,
            created_by,
            updated_by
        ) VALUES (
            'TUITION'::fee_type,
            class_num::VARCHAR::class_name,
            current_year_id,
            5000.00,  -- ₹5,000 per month for primary classes
            'MONTHLY'::fee_frequency,
            TRUE,
            '2024-04-01',
            TRUE,
            'Monthly tuition fee for primary classes (1-5)',
            1,
            1
        );
    END LOOP;

    -- Insert tuition fees for classes 6-8 (Middle)
    FOR class_num IN 6..8 LOOP
        INSERT INTO fee_structures (
            fee_type,
            class_name,
            academic_year_id,
            amount,
            frequency,
            is_mandatory,
            effective_from,
            is_active,
            description,
            created_by,
            updated_by
        ) VALUES (
            'TUITION'::fee_type,
            class_num::VARCHAR::class_name,
            current_year_id,
            6000.00,  -- ₹6,000 per month for middle classes
            'MONTHLY'::fee_frequency,
            TRUE,
            '2024-04-01',
            TRUE,
            'Monthly tuition fee for middle classes (6-8)',
            1,
            1
        );
    END LOOP;

    -- Insert tuition fees for classes 9-10 (Secondary)
    FOR class_num IN 9..10 LOOP
        INSERT INTO fee_structures (
            fee_type,
            class_name,
            academic_year_id,
            amount,
            frequency,
            is_mandatory,
            effective_from,
            is_active,
            description,
            created_by,
            updated_by
        ) VALUES (
            'TUITION'::fee_type,
            class_num::VARCHAR::class_name,
            current_year_id,
            7000.00,  -- ₹7,000 per month for secondary classes
            'MONTHLY'::fee_frequency,
            TRUE,
            '2024-04-01',
            TRUE,
            'Monthly tuition fee for secondary classes (9-10)',
            1,
            1
        );
    END LOOP;

    RAISE NOTICE 'Created default tuition fee structures for all classes';
END $$;

-- ======================
-- 7. INSERT DEFAULT ANNUAL FEES
-- ======================
DO $$
DECLARE
    current_year_id BIGINT;
    class_num INT;
BEGIN
    -- Get current academic year ID
    SELECT academic_year_id INTO current_year_id
    FROM academic_years
    WHERE is_current = TRUE
    LIMIT 1;

    -- Insert annual fees (admission, development) for all classes
    FOR class_num IN 1..10 LOOP
        -- Admission Fee (One-time)
        INSERT INTO fee_structures (
            fee_type,
            class_name,
            academic_year_id,
            amount,
            frequency,
            is_mandatory,
            effective_from,
            is_active,
            description,
            created_by,
            updated_by
        ) VALUES (
            'ADMISSION'::fee_type,
            class_num::VARCHAR::class_name,
            current_year_id,
            10000.00,
            'ONE_TIME'::fee_frequency,
            TRUE,
            '2024-04-01',
            TRUE,
            'One-time admission fee',
            1,
            1
        );

        -- Annual Development Fee
        INSERT INTO fee_structures (
            fee_type,
            class_name,
            academic_year_id,
            amount,
            frequency,
            is_mandatory,
            effective_from,
            is_active,
            description,
            created_by,
            updated_by
        ) VALUES (
            'DEVELOPMENT'::fee_type,
            class_num::VARCHAR::class_name,
            current_year_id,
            5000.00,
            'ANNUAL'::fee_frequency,
            TRUE,
            '2024-04-01',
            TRUE,
            'Annual development fee',
            1,
            1
        );
    END LOOP;

    RAISE NOTICE 'Created default annual and one-time fee structures';
END $$;

-- ======================
-- Success Message and Statistics
-- ======================
DO $$
DECLARE
    user_count INT;
    year_count INT;
    class_count INT;
    config_count INT;
    fee_structure_count INT;
BEGIN
    SELECT COUNT(*) INTO user_count FROM users;
    SELECT COUNT(*) INTO year_count FROM academic_years;
    SELECT COUNT(*) INTO class_count FROM classes;
    SELECT COUNT(*) INTO config_count FROM configurations;
    SELECT COUNT(*) INTO fee_structure_count FROM fee_structures;

    RAISE NOTICE '==============================================';
    RAISE NOTICE 'V5 Migration: Seed data inserted successfully';
    RAISE NOTICE '==============================================';
    RAISE NOTICE 'Users created: % (admin, principal)', user_count;
    RAISE NOTICE 'Academic years created: % (2024-2025)', year_count;
    RAISE NOTICE 'Class sections created: % (1A - 10A)', class_count;
    RAISE NOTICE 'System configurations created: %', config_count;
    RAISE NOTICE 'Fee structures created: %', fee_structure_count;
    RAISE NOTICE '==============================================';
    RAISE NOTICE 'Default Credentials:';
    RAISE NOTICE '  Admin: admin / Admin@123';
    RAISE NOTICE '  Principal: principal / Principal@123';
    RAISE NOTICE 'IMPORTANT: Change default passwords immediately!';
    RAISE NOTICE '==============================================';
END $$;
