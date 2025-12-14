-- Configuration Service Database Schema
-- PostgreSQL 18+

-- Create school_configurations table
CREATE TABLE IF NOT EXISTS school_configurations (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    config_key VARCHAR(100) NOT NULL,
    config_value VARCHAR(500) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uk_category_key UNIQUE (category, config_key)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_configurations_category
    ON school_configurations(category);

-- Insert sample configuration data
INSERT INTO school_configurations (category, config_key, config_value, description, created_by) VALUES
('GENERAL', 'school_name', 'ABC International School', 'Official school name', 'system'),
('GENERAL', 'school_code', 'ABC-001', 'Unique school identification code', 'system'),
('GENERAL', 'school_address', '123 Education Street, City, State', 'School physical address', 'system'),
('ACADEMIC', 'academic_year_start', '2025-04-01', 'Start date of academic year', 'system'),
('ACADEMIC', 'academic_year_end', '2026-03-31', 'End date of academic year', 'system'),
('ACADEMIC', 'grading_system', 'A-F', 'Grading system used (A-F, Percentage, etc.)', 'system'),
('FINANCIAL', 'currency', 'USD', 'Default currency for fees', 'system'),
('FINANCIAL', 'late_fee_percentage', '5', 'Late payment penalty percentage', 'system'),
('SYSTEM', 'max_students_per_class', '40', 'Maximum students allowed per class', 'system'),
('SYSTEM', 'session_timeout_minutes', '30', 'User session timeout in minutes', 'system')
ON CONFLICT (category, config_key) DO NOTHING;

-- Add comments to table and columns
COMMENT ON TABLE school_configurations IS 'Stores school configuration settings grouped by category';
COMMENT ON COLUMN school_configurations.category IS 'Configuration category: GENERAL, ACADEMIC, FINANCIAL, SYSTEM';
COMMENT ON COLUMN school_configurations.config_key IS 'Unique configuration key within a category';
COMMENT ON COLUMN school_configurations.config_value IS 'Configuration value (max 500 characters)';
COMMENT ON COLUMN school_configurations.version IS 'Version number for optimistic locking';
