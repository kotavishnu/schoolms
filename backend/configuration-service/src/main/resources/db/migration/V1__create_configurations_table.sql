-- Configuration Management Schema
-- Database: config_db
-- Version: 1.0.0
-- Date: 2026-02-04

CREATE TABLE IF NOT EXISTS configurations (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL CHECK (category IN ('GENERAL', 'ACADEMIC', 'FINANCIAL')),
    config_key VARCHAR(100) NOT NULL,
    config_value VARCHAR(1000) NOT NULL,
    description VARCHAR(500),
    data_type VARCHAR(20) NOT NULL CHECK (data_type IN ('STRING', 'NUMBER', 'BOOLEAN', 'JSON')),
    is_encrypted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_category_key UNIQUE (category, config_key)
);

-- Indexes for performance
CREATE INDEX idx_configurations_category ON configurations(category);
CREATE INDEX idx_configurations_key ON configurations(config_key);

-- Comments for documentation
COMMENT ON TABLE configurations IS 'Stores school configuration settings grouped by category';
COMMENT ON COLUMN configurations.category IS 'Configuration category: GENERAL, ACADEMIC, or FINANCIAL';
COMMENT ON COLUMN configurations.config_key IS 'Unique key within category (alphanumeric and underscore only)';
COMMENT ON COLUMN configurations.config_value IS 'Configuration value as string';
COMMENT ON COLUMN configurations.data_type IS 'Data type for value interpretation';
COMMENT ON COLUMN configurations.is_encrypted IS 'Whether value is encrypted';
COMMENT ON COLUMN configurations.version IS 'Optimistic locking version';

-- Seed data for initial configuration
INSERT INTO configurations (category, config_key, config_value, description, data_type, is_encrypted) VALUES
('GENERAL', 'school_name', 'ABC International School', 'Official school name', 'STRING', FALSE),
('GENERAL', 'school_code', 'ABC-2025', 'Unique school identification code', 'STRING', FALSE),
('GENERAL', 'school_email', 'admin@abcschool.edu', 'Primary school contact email', 'STRING', FALSE),
('GENERAL', 'school_phone', '+1-555-0100', 'Primary school contact phone', 'STRING', FALSE),
('ACADEMIC', 'current_academic_year', '2025-2026', 'Current active academic year', 'STRING', FALSE),
('ACADEMIC', 'class_capacity_default', '30', 'Default maximum students per class', 'NUMBER', FALSE),
('ACADEMIC', 'minimum_attendance_percent', '75', 'Minimum attendance percentage required', 'NUMBER', FALSE),
('FINANCIAL', 'currency', 'USD', 'School fee currency', 'STRING', FALSE),
('FINANCIAL', 'late_fee_enabled', 'true', 'Whether late fees are charged', 'BOOLEAN', FALSE),
('FINANCIAL', 'late_fee_amount', '50', 'Late payment fee amount', 'NUMBER', FALSE);
