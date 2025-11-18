-- Insert default school profile (single record)
INSERT INTO school_profile (
    id, school_name, school_code, logo_path, address, phone, email,
    created_by, updated_by
) VALUES (
    1,
    'ABC International School',
    'SCH001',
    '/uploads/logos/default-logo.png',
    '123 Education Street, Bangalore, Karnataka - 560001',
    '+91-80-12345678',
    'info@abcschool.edu.in',
    'SYSTEM',
    'SYSTEM'
) ON CONFLICT (id) DO NOTHING;

-- Insert default GENERAL settings
INSERT INTO configuration_settings (category, key, value, description, created_by, updated_by) VALUES
('GENERAL', 'SCHOOL_TIMEZONE', 'Asia/Kolkata', 'Default timezone for the school', 'SYSTEM', 'SYSTEM'),
('GENERAL', 'DATE_FORMAT', 'dd-MM-yyyy', 'Default date display format', 'SYSTEM', 'SYSTEM'),
('GENERAL', 'TIME_FORMAT', 'HH:mm:ss', 'Default time display format', 'SYSTEM', 'SYSTEM'),
('GENERAL', 'LANGUAGE', 'en', 'Default system language', 'SYSTEM', 'SYSTEM'),
('GENERAL', 'COUNTRY', 'India', 'Default country', 'SYSTEM', 'SYSTEM');

-- Insert default ACADEMIC settings
INSERT INTO configuration_settings (category, key, value, description, created_by, updated_by) VALUES
('ACADEMIC', 'CURRENT_ACADEMIC_YEAR', '2025-2026', 'Current academic year', 'SYSTEM', 'SYSTEM'),
('ACADEMIC', 'MIN_STUDENT_AGE', '3', 'Minimum student age for admission', 'SYSTEM', 'SYSTEM'),
('ACADEMIC', 'MAX_STUDENT_AGE', '18', 'Maximum student age for admission', 'SYSTEM', 'SYSTEM'),
('ACADEMIC', 'ACADEMIC_YEAR_START_MONTH', '04', 'Academic year start month (April)', 'SYSTEM', 'SYSTEM'),
('ACADEMIC', 'ACADEMIC_YEAR_END_MONTH', '03', 'Academic year end month (March)', 'SYSTEM', 'SYSTEM'),
('ACADEMIC', 'WORKING_DAYS_PER_WEEK', '6', 'Number of working days per week', 'SYSTEM', 'SYSTEM'),
('ACADEMIC', 'MINIMUM_ATTENDANCE_PERCENTAGE', '75', 'Minimum attendance percentage required', 'SYSTEM', 'SYSTEM');

-- Insert default FINANCIAL settings
INSERT INTO configuration_settings (category, key, value, description, created_by, updated_by) VALUES
('FINANCIAL', 'CURRENCY_CODE', 'INR', 'Currency code (ISO 4217)', 'SYSTEM', 'SYSTEM'),
('FINANCIAL', 'CURRENCY_SYMBOL', '₹', 'Currency symbol', 'SYSTEM', 'SYSTEM'),
('FINANCIAL', 'TAX_PERCENTAGE', '0', 'Default tax percentage', 'SYSTEM', 'SYSTEM'),
('FINANCIAL', 'LATE_FEE_PERCENTAGE', '2', 'Late payment fee percentage', 'SYSTEM', 'SYSTEM'),
('FINANCIAL', 'FEE_DUE_DAY', '10', 'Day of month for fee payment', 'SYSTEM', 'SYSTEM');
