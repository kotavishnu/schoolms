-- Create sequence for student key generation
CREATE SEQUENCE IF NOT EXISTS student_key_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

COMMENT ON SEQUENCE student_key_seq IS 'Sequence for generating student key numbers (STU-YYYY-NNNN format)';
