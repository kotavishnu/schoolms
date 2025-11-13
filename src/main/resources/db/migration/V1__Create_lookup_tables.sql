-- =========================================================
-- School Management System - Database Schema V1
-- Description: Create lookup tables and base types
-- Author: Database Administrator
-- Date: 2025-11-11
-- =========================================================

-- ======================
-- Enable Required Extensions
-- ======================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ======================
-- Create Enums/Types
-- ======================

-- User Roles
CREATE TYPE user_role AS ENUM (
    'ADMIN',
    'PRINCIPAL',
    'OFFICE_STAFF',
    'ACCOUNTS_MANAGER',
    'AUDITOR'
);

-- Student Status
CREATE TYPE student_status AS ENUM (
    'ACTIVE',
    'INACTIVE',
    'GRADUATED',
    'TRANSFERRED',
    'WITHDRAWN'
);

-- Gender
CREATE TYPE gender AS ENUM (
    'MALE',
    'FEMALE',
    'OTHER'
);

-- Guardian Relationship
CREATE TYPE relationship_type AS ENUM (
    'FATHER',
    'MOTHER',
    'GUARDIAN',
    'OTHER'
);

-- Class Names (1-10)
CREATE TYPE class_name AS ENUM (
    '1', '2', '3', '4', '5',
    '6', '7', '8', '9', '10'
);

-- Enrollment Status
CREATE TYPE enrollment_status AS ENUM (
    'ENROLLED',
    'PROMOTED',
    'WITHDRAWN'
);

-- Fee Types
CREATE TYPE fee_type AS ENUM (
    'TUITION',
    'LIBRARY',
    'COMPUTER',
    'SPORTS',
    'TRANSPORT',
    'EXAM',
    'ADMISSION',
    'ANNUAL',
    'DEVELOPMENT',
    'OTHER'
);

-- Fee Frequency
CREATE TYPE fee_frequency AS ENUM (
    'MONTHLY',
    'QUARTERLY',
    'ANNUAL',
    'ONE_TIME'
);

-- Payment Status
CREATE TYPE payment_status AS ENUM (
    'PENDING',
    'PARTIAL',
    'PAID',
    'OVERDUE',
    'WAIVED'
);

-- Payment Mode
CREATE TYPE payment_mode AS ENUM (
    'CASH',
    'CHEQUE',
    'ONLINE',
    'UPI',
    'CARD'
);

-- Configuration Categories
CREATE TYPE config_category AS ENUM (
    'GENERAL',
    'ACADEMIC',
    'FINANCIAL',
    'SYSTEM'
);

-- Audit Action Types
CREATE TYPE audit_action AS ENUM (
    'INSERT',
    'UPDATE',
    'DELETE'
);

-- ======================
-- Comments on Types
-- ======================
COMMENT ON TYPE user_role IS 'User roles for role-based access control';
COMMENT ON TYPE student_status IS 'Student lifecycle statuses';
COMMENT ON TYPE gender IS 'Gender options for students and guardians';
COMMENT ON TYPE relationship_type IS 'Guardian relationship to student';
COMMENT ON TYPE class_name IS 'Class levels from 1 to 10';
COMMENT ON TYPE enrollment_status IS 'Student enrollment statuses';
COMMENT ON TYPE fee_type IS 'Types of fees charged';
COMMENT ON TYPE fee_frequency IS 'Fee payment frequency';
COMMENT ON TYPE payment_status IS 'Payment status for fee journals';
COMMENT ON TYPE payment_mode IS 'Payment methods accepted';
COMMENT ON TYPE config_category IS 'Configuration setting categories';
COMMENT ON TYPE audit_action IS 'Audit trail action types';

-- ======================
-- Success Message
-- ======================
DO $$
BEGIN
    RAISE NOTICE 'V1 Migration: Lookup tables and types created successfully';
END $$;
