@echo off
REM ==============================================================================
REM School Management System - Database Initialization Script (Windows)
REM ==============================================================================
REM Purpose: Initialize PostgreSQL databases for Student and Configuration services
REM Author: SMS Backend Team
REM Date: 2025-12-06
REM ==============================================================================

setlocal enabledelayedexpansion

REM Configuration
set POSTGRES_HOST=%POSTGRES_HOST%
if "%POSTGRES_HOST%"=="" set POSTGRES_HOST=localhost

set POSTGRES_PORT=%POSTGRES_PORT%
if "%POSTGRES_PORT%"=="" set POSTGRES_PORT=5432

set POSTGRES_USER=%POSTGRES_USER%
if "%POSTGRES_USER%"=="" set POSTGRES_USER=postgres

set POSTGRES_PASSWORD=%POSTGRES_PASSWORD%
if "%POSTGRES_PASSWORD%"=="" set POSTGRES_PASSWORD=postgres

set DB_STUDENT=sms_student_db
set DB_CONFIG=sms_config_db
set SCHEMA_FILE=..\specs\planning\school_management.sql

set PGPASSWORD=%POSTGRES_PASSWORD%

REM ==============================================================================
REM Print Header
REM ==============================================================================

echo ================================================================
echo   School Management System - Database Initialization
echo ================================================================
echo.

REM ==============================================================================
REM Display Configuration
REM ==============================================================================

echo [INFO] Configuration:
echo   Host: %POSTGRES_HOST%
echo   Port: %POSTGRES_PORT%
echo   User: %POSTGRES_USER%
echo   Student DB: %DB_STUDENT%
echo   Config DB: %DB_CONFIG%
echo.

REM ==============================================================================
REM Check PostgreSQL Connection
REM ==============================================================================

echo [INFO] Checking PostgreSQL connection...
psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -c "\q" 2>nul

if errorlevel 1 (
    echo [ERROR] Cannot connect to PostgreSQL at %POSTGRES_HOST%:%POSTGRES_PORT%
    echo [ERROR] Please ensure PostgreSQL is running and credentials are correct.
    exit /b 1
)

echo [INFO] PostgreSQL connection successful!
echo.

REM ==============================================================================
REM Check Schema File
REM ==============================================================================

echo [INFO] Checking schema file...
if not exist "%SCHEMA_FILE%" (
    echo [ERROR] Schema file not found: %SCHEMA_FILE%
    exit /b 1
)

echo [INFO] Schema file found: %SCHEMA_FILE%
echo.

REM ==============================================================================
REM Create Student Database
REM ==============================================================================

echo --- Initializing Student Service Database ---
echo.

echo [INFO] Creating database: %DB_STUDENT%

REM Check if database exists
psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -tAc "SELECT 1 FROM pg_database WHERE datname='%DB_STUDENT%'" > temp_check.txt 2>nul
set /p db_exists=<temp_check.txt
del temp_check.txt 2>nul

if "%db_exists%"=="1" (
    echo [WARNING] Database '%DB_STUDENT%' already exists. Dropping and recreating...
    psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -c "DROP DATABASE %DB_STUDENT%;" 2>nul
)

psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -c "CREATE DATABASE %DB_STUDENT%;"

if errorlevel 1 (
    echo [ERROR] Failed to create database '%DB_STUDENT%'
    exit /b 1
)

echo [INFO] Database '%DB_STUDENT%' created successfully!

REM Apply schema to student database
echo [INFO] Applying schema to database: %DB_STUDENT%
psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -d %DB_STUDENT% -f "%SCHEMA_FILE%"

if errorlevel 1 (
    echo [ERROR] Failed to apply schema to '%DB_STUDENT%'
    exit /b 1
)

echo [INFO] Schema applied successfully to '%DB_STUDENT%'!

REM Set permissions
echo [INFO] Setting permissions for database: %DB_STUDENT%
psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -d %DB_STUDENT% -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO %POSTGRES_USER%;"
psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -d %DB_STUDENT% -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO %POSTGRES_USER%;"

echo [INFO] Permissions set successfully!

REM Verify tables
echo [INFO] Verifying tables in database: %DB_STUDENT%
psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -d %DB_STUDENT% -c "\dt"

echo.

REM ==============================================================================
REM Create Configuration Database
REM ==============================================================================

echo --- Initializing Configuration Service Database ---
echo.

echo [INFO] Creating database: %DB_CONFIG%

REM Check if database exists
psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -tAc "SELECT 1 FROM pg_database WHERE datname='%DB_CONFIG%'" > temp_check.txt 2>nul
set /p db_exists=<temp_check.txt
del temp_check.txt 2>nul

if "%db_exists%"=="1" (
    echo [WARNING] Database '%DB_CONFIG%' already exists. Dropping and recreating...
    psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -c "DROP DATABASE %DB_CONFIG%;" 2>nul
)

psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -c "CREATE DATABASE %DB_CONFIG%;"

if errorlevel 1 (
    echo [ERROR] Failed to create database '%DB_CONFIG%'
    exit /b 1
)

echo [INFO] Database '%DB_CONFIG%' created successfully!

REM Apply schema to config database
echo [INFO] Applying schema to database: %DB_CONFIG%
psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -d %DB_CONFIG% -f "%SCHEMA_FILE%"

if errorlevel 1 (
    echo [ERROR] Failed to apply schema to '%DB_CONFIG%'
    exit /b 1
)

echo [INFO] Schema applied successfully to '%DB_CONFIG%'!

REM Set permissions
echo [INFO] Setting permissions for database: %DB_CONFIG%
psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -d %DB_CONFIG% -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO %POSTGRES_USER%;"
psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -d %DB_CONFIG% -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO %POSTGRES_USER%;"

echo [INFO] Permissions set successfully!

REM Verify tables
echo [INFO] Verifying tables in database: %DB_CONFIG%
psql -h %POSTGRES_HOST% -p %POSTGRES_PORT% -U %POSTGRES_USER% -d %DB_CONFIG% -c "\dt"

echo.

REM ==============================================================================
REM Success Summary
REM ==============================================================================

echo ================================================================
echo   Database initialization completed successfully!
echo ================================================================
echo.
echo Summary:
echo   [OK] Database '%DB_STUDENT%' ready for Student Service
echo   [OK] Database '%DB_CONFIG%' ready for Configuration Service
echo.
echo Next steps:
echo   1. Start Student Service: cd backend\student-service ^&^& mvn spring-boot:run
echo   2. Start Configuration Service: cd backend\configuration-service ^&^& mvn spring-boot:run
echo.

REM Clean up
set PGPASSWORD=

endlocal
