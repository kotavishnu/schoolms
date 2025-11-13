-- V8: Create Academic Year and Classes Tables
-- Author: School Management System
-- Date: November 11, 2025

-- =====================================================
-- Academic Years Table
-- =====================================================
CREATE TABLE IF NOT EXISTS academic_years (
    id BIGSERIAL PRIMARY KEY,
    year_code VARCHAR(10) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT false,

    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL REFERENCES users(id),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL REFERENCES users(id),

    -- Constraints
    CONSTRAINT chk_year_code_format CHECK (year_code ~ '^[0-9]{4}-[0-9]{4}$'),
    CONSTRAINT chk_end_after_start CHECK (end_date > start_date)
);

-- Indexes for academic_years
--CREATE INDEX idx_academic_years_year_code ON academic_years(year_code);
--CREATE INDEX idx_academic_years_current ON academic_years(is_current);
--CREATE INDEX idx_academic_years_date_range ON academic_years(start_date, end_date);

-- Trigger to auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION update_academic_years_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_academic_years_updated_at
    BEFORE UPDATE ON academic_years
    FOR EACH ROW
    EXECUTE FUNCTION update_academic_years_updated_at();

-- =====================================================
-- Classes Table
-- =====================================================
CREATE TABLE IF NOT EXISTS classes (
    id BIGSERIAL PRIMARY KEY,
    class_name VARCHAR(10) NOT NULL,
    section VARCHAR(1) NOT NULL,
    academic_year_id BIGINT NOT NULL REFERENCES academic_years(id) ON DELETE CASCADE,
    max_capacity INTEGER NOT NULL,
    current_enrollment INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,

    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL REFERENCES users(id),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL REFERENCES users(id),

    -- Constraints
    CONSTRAINT chk_class_name CHECK (class_name IN ('CLASS_1', 'CLASS_2', 'CLASS_3', 'CLASS_4', 'CLASS_5',
                                                     'CLASS_6', 'CLASS_7', 'CLASS_8', 'CLASS_9', 'CLASS_10')),
    CONSTRAINT chk_section_format CHECK (section ~ '^[A-Z]$'),
    CONSTRAINT chk_max_capacity CHECK (max_capacity >= 1 AND max_capacity <= 100),
    CONSTRAINT chk_current_enrollment CHECK (current_enrollment >= 0 AND current_enrollment <= max_capacity),
    CONSTRAINT uq_class_section_year UNIQUE (class_name, section, academic_year_id)
);

-- Indexes for classes
--CREATE INDEX idx_classes_academic_year ON classes(academic_year_id);
--CREATE INDEX idx_classes_name_section ON classes(class_name, section);
--CREATE INDEX idx_classes_is_active ON classes(is_active);
--CREATE INDEX idx_classes_capacity ON classes(max_capacity, current_enrollment);

-- Partial index for classes with available capacity
--CREATE INDEX idx_classes_available_capacity
--    ON classes(academic_year_id, class_name, section)
--    WHERE current_enrollment < max_capacity AND is_active = true;

-- Trigger to auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION update_classes_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_classes_updated_at
    BEFORE UPDATE ON classes
    FOR EACH ROW
    EXECUTE FUNCTION update_classes_updated_at();

-- =====================================================
-- Comments for Documentation
-- =====================================================
COMMENT ON TABLE academic_years IS 'Stores academic year information (e.g., 2024-2025)';
COMMENT ON COLUMN academic_years.year_code IS 'Academic year code in format YYYY-YYYY';
COMMENT ON COLUMN academic_years.is_current IS 'Indicates if this is the current active academic year (only one can be true)';

COMMENT ON TABLE classes IS 'Stores class/section information for each academic year';
COMMENT ON COLUMN classes.class_name IS 'Class name from CLASS_1 to CLASS_10';
COMMENT ON COLUMN classes.section IS 'Section letter A-Z';
COMMENT ON COLUMN classes.max_capacity IS 'Maximum number of students allowed (1-100)';
COMMENT ON COLUMN classes.current_enrollment IS 'Current number of enrolled students';

-- =====================================================
-- Rollback Information
-- =====================================================
-- To rollback this migration:
-- DROP TABLE IF EXISTS classes CASCADE;
-- DROP TABLE IF EXISTS academic_years CASCADE;
-- DROP FUNCTION IF EXISTS update_academic_years_updated_at() CASCADE;
-- DROP FUNCTION IF EXISTS update_classes_updated_at() CASCADE;
