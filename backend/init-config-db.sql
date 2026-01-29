-- ========================================
-- Configuration Service Database Initialization
-- ========================================

-- Drop existing table if it exists
DROP TABLE IF EXISTS configuration_settings CASCADE;

-- ========================================
-- TABLE: configuration_settings
-- ========================================

CREATE TABLE configuration_settings (
    -- Primary Key
    id                 BIGSERIAL PRIMARY KEY,

    -- Configuration Identity
    category           VARCHAR(50) NOT NULL CHECK (
        category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL')
    ),
    config_key         VARCHAR(100) NOT NULL,

    -- Configuration Value
    config_value       TEXT NOT NULL,
    description        TEXT,
    data_type          VARCHAR(20) NOT NULL CHECK (
        data_type IN ('STRING', 'NUMBER', 'BOOLEAN', 'JSON')
    ),
    is_encrypted       BOOLEAN NOT NULL DEFAULT FALSE,

    -- Concurrency Control
    version            BIGINT NOT NULL DEFAULT 0,

    -- Audit Columns
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT unique_category_key UNIQUE (category, config_key)
);

-- Indexes for configuration_settings table
CREATE INDEX idx_config_category ON configuration_settings(category);
CREATE INDEX idx_config_key ON configuration_settings(config_key);
CREATE INDEX idx_config_updated_at ON configuration_settings(updated_at);

-- ========================================
-- SEED DATA
-- ========================================

INSERT INTO configuration_settings (category, config_key, config_value, description, data_type) VALUES
    -- General Settings
    ('GENERAL', 'school_name', 'Springfield Elementary', 'Official school name', 'STRING'),
    ('GENERAL', 'school_code', 'SPFD-001', 'Unique school identifier', 'STRING'),
    ('GENERAL', 'academic_year', '2025-2026', 'Current academic year', 'STRING'),
    ('GENERAL', 'school_address', '123 Main Street, Springfield', 'School physical address', 'STRING'),
    ('GENERAL', 'contact_email', 'admin@springfield.edu', 'School contact email', 'STRING'),
    ('GENERAL', 'contact_phone', '5551234567', 'School contact phone', 'STRING'),

    -- Academic Settings
    ('ACADEMIC', 'max_class_size', '40', 'Maximum students per class', 'NUMBER'),
    ('ACADEMIC', 'passing_percentage', '40', 'Minimum passing marks percentage', 'NUMBER'),
    ('ACADEMIC', 'enable_grading', 'true', 'Enable grade-based evaluation', 'BOOLEAN'),
    ('ACADEMIC', 'term_system', 'semester', 'Academic term system (semester/trimester)', 'STRING'),
    ('ACADEMIC', 'working_days_per_week', '5', 'Number of working days per week', 'NUMBER'),

    -- Financial Settings
    ('FINANCIAL', 'tuition_fee', '25000', 'Annual tuition fee in local currency', 'NUMBER'),
    ('FINANCIAL', 'late_fee_penalty', '500', 'Penalty for late fee payment', 'NUMBER'),
    ('FINANCIAL', 'payment_gateway_enabled', 'false', 'Enable online payment gateway', 'BOOLEAN'),
    ('FINANCIAL', 'currency_code', 'INR', 'Currency code (ISO 4217)', 'STRING'),
    ('FINANCIAL', 'tax_rate', '0', 'Tax rate percentage', 'NUMBER');
