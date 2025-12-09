-- Create enrollment_history table
CREATE TABLE IF NOT EXISTS enrollment_history (
    -- Primary Key
    enrollment_id       BIGSERIAL PRIMARY KEY,

    -- Foreign Key to Student
    student_id          BIGINT NOT NULL,

    -- Status Change Information
    status              VARCHAR(20) NOT NULL,
    changed_by          VARCHAR(100),
    changed_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remarks             TEXT,

    -- Foreign Key Constraint
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id)
        REFERENCES student(student_id)
        ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_enrollment_status CHECK (status IN ('Active', 'Inactive'))
);

-- Indexes
CREATE INDEX idx_enrollment_student_id ON enrollment_history(student_id);
CREATE INDEX idx_enrollment_changed_at ON enrollment_history(changed_at DESC);

-- Comments
COMMENT ON TABLE enrollment_history IS 'Audit trail for student status changes';
COMMENT ON COLUMN enrollment_history.status IS 'New status after change';
COMMENT ON COLUMN enrollment_history.changed_by IS 'User who made the change';
