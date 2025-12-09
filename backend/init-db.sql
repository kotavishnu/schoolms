-- Initialize databases for School Management System

-- Create config database
CREATE DATABASE config_db;

-- Set password for user (ensure it's properly set)
ALTER USER sms_user WITH PASSWORD 'sms_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE student_db TO sms_user;
GRANT ALL PRIVILEGES ON DATABASE config_db TO sms_user;
