-- Create school_profile table for storing school information
-- Only one record should exist in this table

CREATE TABLE school_profile (
    id BIGSERIAL PRIMARY KEY,
    school_name VARCHAR(200) NOT NULL,
    school_code VARCHAR(20) NOT NULL UNIQUE,
    logo_path VARCHAR(500),
    address VARCHAR(500),
    phone VARCHAR(15),
    email VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) DEFAULT 'SYSTEM',
    updated_by VARCHAR(50) DEFAULT 'SYSTEM',
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT check_single_record CHECK (id = 1)
);

-- Create index
CREATE INDEX idx_school_profile_school_code ON school_profile(school_code);

-- Add comment
COMMENT ON TABLE school_profile IS 'School profile information - single record table';
COMMENT ON CONSTRAINT check_single_record ON school_profile IS 'Ensures only one school profile record exists';
