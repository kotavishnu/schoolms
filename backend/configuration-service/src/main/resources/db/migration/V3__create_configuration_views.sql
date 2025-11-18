-- Create view for settings grouped by category
CREATE OR REPLACE VIEW v_settings_by_category AS
SELECT
    category,
    COUNT(*) as total_settings,
    MAX(updated_at) as last_updated
FROM configuration_settings
GROUP BY category
ORDER BY category;

-- Create view for recent configuration changes
CREATE OR REPLACE VIEW v_recent_configuration_changes AS
SELECT
    id,
    category,
    key,
    value,
    updated_at,
    updated_by
FROM configuration_settings
ORDER BY updated_at DESC
LIMIT 50;

-- Add comments
COMMENT ON VIEW v_settings_by_category IS 'Summary of settings grouped by category';
COMMENT ON VIEW v_recent_configuration_changes IS 'Recent 50 configuration changes for audit';
