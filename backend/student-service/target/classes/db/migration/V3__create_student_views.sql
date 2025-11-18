-- ============================================================================
-- Student Service Migration V3: Create views
-- Description: Creates database views for common queries
-- ============================================================================

-- View: Active Students
CREATE OR REPLACE VIEW v_active_students AS
SELECT
    student_id,
    first_name,
    last_name,
    CONCAT(first_name, ' ', last_name) AS full_name,
    date_of_birth,
    EXTRACT(YEAR FROM AGE(date_of_birth)) AS age,
    mobile,
    email,
    CONCAT(street, ', ', city, ', ', state, ' - ', pin_code) AS full_address,
    created_at,
    updated_at
FROM students
WHERE status = 'ACTIVE';

COMMENT ON VIEW v_active_students IS 'View of active students with computed fields';
