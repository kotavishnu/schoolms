-- Student Service Database Schema
-- PostgreSQL 18+

-- Drop table if exists (for development only)
DROP TABLE IF EXISTS students CASCADE;

-- Create students table
CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    mobile VARCHAR(15) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    father_name VARCHAR(100) NOT NULL,
    mother_name VARCHAR(100),
    identification_mark VARCHAR(100),
    aadhaar_number VARCHAR(12),
    email VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE,
    version BIGINT DEFAULT 0,
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'GRADUATED', 'TRANSFERRED'))
);

-- Create indexes for performance
CREATE INDEX idx_student_id ON students(student_id);
CREATE INDEX idx_mobile ON students(mobile);
CREATE INDEX idx_last_name ON students(last_name);
CREATE INDEX idx_father_name ON students(father_name);
CREATE INDEX idx_status ON students(status);
CREATE INDEX idx_date_of_birth ON students(date_of_birth);

-- Sample data for testing (optional)
-- INSERT INTO students (student_id, first_name, last_name, date_of_birth, mobile, address, father_name, mother_name, email, status)
-- VALUES ('STU-2024-00001', 'John', 'Doe', '2015-05-15', '9876543210', '123 Main St', 'James Doe', 'Jane Doe', 'john.doe@example.com', 'ACTIVE');
