-- Create configuration_settings table for storing application configuration

CREATE TYPE setting_category AS ENUM ('GENERAL', 'ACADEMIC', 'FINANCIAL');

CREATE TABLE configuration_settings (
    id BIGSERIAL PRIMARY KEY,
    category setting_category NOT NULL,
    key VARCHAR(100) NOT NULL,
    value TEXT NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'SYSTEM',
    updated_by VARCHAR(50) DEFAULT 'SYSTEM',
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uq_category_key UNIQUE (category, key)
);

-- Create indexes for better query performance
CREATE INDEX idx_configuration_settings_category ON configuration_settings(category);
CREATE INDEX idx_configuration_settings_key ON configuration_settings(key);
CREATE INDEX idx_configuration_settings_category_key ON configuration_settings(category, key);

-- Add comments
COMMENT ON TABLE configuration_settings IS 'Application configuration settings grouped by category';
COMMENT ON COLUMN configuration_settings.category IS 'Setting category: GENERAL, ACADEMIC, or FINANCIAL';
COMMENT ON COLUMN configuration_settings.key IS 'Setting key in UPPERCASE_SNAKE_CASE format';
COMMENT ON COLUMN configuration_settings.value IS 'Setting value as text';
COMMENT ON CONSTRAINT uq_category_key ON configuration_settings IS 'Ensures unique setting within category';
