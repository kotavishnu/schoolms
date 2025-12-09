-- Create configuration_setting table
CREATE TABLE configuration_setting (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(100) NOT NULL,
    key VARCHAR(100) NOT NULL,
    value TEXT,
    description TEXT,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_config_category ON configuration_setting(category);
CREATE INDEX idx_config_key ON configuration_setting(key);
CREATE INDEX idx_config_category_key ON configuration_setting(category, key);

-- Add unique constraint for category + key combination
ALTER TABLE configuration_setting ADD CONSTRAINT uk_config_category_key UNIQUE (category, key);
