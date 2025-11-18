-- ============================================================================
-- Student Service Migration V4: Insert sample data
-- Description: Inserts sample student records for testing
-- ============================================================================

-- Sample Student Data
INSERT INTO students (
    student_id, first_name, last_name, date_of_birth,
    street, city, state, pin_code, country,
    mobile, email, father_name_guardian, mother_name,
    caste, moles, aadhaar_number, status,
    created_by, updated_by
) VALUES
(
    generate_student_id(),
    'Rajesh',
    'Kumar',
    '2015-05-15',
    '123 MG Road',
    'Bangalore',
    'Karnataka',
    '560001',
    'India',
    '9876543210',
    'rajesh.kumar@example.com',
    'Suresh Kumar',
    'Lakshmi Kumar',
    'General',
    'Small mole on left cheek',
    '123456789012',
    'ACTIVE',
    'SYSTEM',
    'SYSTEM'
),
(
    generate_student_id(),
    'Priya',
    'Sharma',
    '2016-08-20',
    '456 Brigade Road',
    'Bangalore',
    'Karnataka',
    '560025',
    'India',
    '9876543211',
    NULL,
    'Ramesh Sharma',
    'Sunita Sharma',
    'OBC',
    NULL,
    NULL,
    'ACTIVE',
    'SYSTEM',
    'SYSTEM'
),
(
    generate_student_id(),
    'Arun',
    'Patel',
    '2014-03-10',
    '789 Church Street',
    'Mumbai',
    'Maharashtra',
    '400001',
    'India',
    '9876543212',
    'arun.patel@example.com',
    'Vijay Patel',
    'Meera Patel',
    'General',
    'Birthmark on right hand',
    '123456789013',
    'ACTIVE',
    'SYSTEM',
    'SYSTEM'
);
