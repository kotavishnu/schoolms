#!/bin/bash

# ==============================================================================
# School Management System - Database Initialization Script (Linux/Mac)
# ==============================================================================
# Purpose: Initialize PostgreSQL databases for Student and Configuration services
# Author: SMS Backend Team
# Date: 2025-12-06
# ==============================================================================

set -e  # Exit on error

# Color codes for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
POSTGRES_HOST="${POSTGRES_HOST:-localhost}"
POSTGRES_PORT="${POSTGRES_PORT:-5432}"
POSTGRES_USER="${POSTGRES_USER:-postgres}"
POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-postgres}"
DB_STUDENT="sms_student_db"
DB_CONFIG="sms_config_db"
SCHEMA_FILE="../specs/planning/school_management.sql"

# ==============================================================================
# Functions
# ==============================================================================

print_header() {
    echo -e "${BLUE}=================================================================${NC}"
    echo -e "${BLUE}  School Management System - Database Initialization${NC}"
    echo -e "${BLUE}=================================================================${NC}"
}

print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

check_postgres() {
    print_info "Checking PostgreSQL connection..."
    export PGPASSWORD="$POSTGRES_PASSWORD"

    if ! psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -c '\q' 2>/dev/null; then
        print_error "Cannot connect to PostgreSQL at $POSTGRES_HOST:$POSTGRES_PORT"
        print_error "Please ensure PostgreSQL is running and credentials are correct."
        exit 1
    fi

    print_info "PostgreSQL connection successful!"
}

check_schema_file() {
    print_info "Checking schema file..."
    if [ ! -f "$SCHEMA_FILE" ]; then
        print_error "Schema file not found: $SCHEMA_FILE"
        exit 1
    fi
    print_info "Schema file found: $SCHEMA_FILE"
}

create_database() {
    local db_name=$1
    print_info "Creating database: $db_name"

    # Check if database already exists
    db_exists=$(psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -tAc "SELECT 1 FROM pg_database WHERE datname='$db_name'")

    if [ "$db_exists" = "1" ]; then
        print_warning "Database '$db_name' already exists. Dropping and recreating..."
        psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -c "DROP DATABASE $db_name;" 2>/dev/null || true
    fi

    psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -c "CREATE DATABASE $db_name;"

    if [ $? -eq 0 ]; then
        print_info "Database '$db_name' created successfully!"
    else
        print_error "Failed to create database '$db_name'"
        exit 1
    fi
}

apply_schema() {
    local db_name=$1
    print_info "Applying schema to database: $db_name"

    psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$db_name" -f "$SCHEMA_FILE"

    if [ $? -eq 0 ]; then
        print_info "Schema applied successfully to '$db_name'!"
    else
        print_error "Failed to apply schema to '$db_name'"
        exit 1
    fi
}

set_permissions() {
    local db_name=$1
    print_info "Setting permissions for database: $db_name"

    psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$db_name" -c "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $POSTGRES_USER;"
    psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$db_name" -c "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO $POSTGRES_USER;"

    print_info "Permissions set successfully!"
}

verify_tables() {
    local db_name=$1
    print_info "Verifying tables in database: $db_name"

    local table_count=$(psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$db_name" -tAc "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='public';")

    print_info "Found $table_count tables in '$db_name'"

    # List all tables
    psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$db_name" -c "\dt"
}

# ==============================================================================
# Main Execution
# ==============================================================================

print_header

print_info "Configuration:"
print_info "  Host: $POSTGRES_HOST"
print_info "  Port: $POSTGRES_PORT"
print_info "  User: $POSTGRES_USER"
print_info "  Student DB: $DB_STUDENT"
print_info "  Config DB: $DB_CONFIG"
echo ""

# Pre-flight checks
check_postgres
check_schema_file

echo ""

# Initialize Student Database
echo -e "${BLUE}--- Initializing Student Service Database ---${NC}"
create_database "$DB_STUDENT"
apply_schema "$DB_STUDENT"
set_permissions "$DB_STUDENT"
verify_tables "$DB_STUDENT"

echo ""

# Initialize Configuration Database
echo -e "${BLUE}--- Initializing Configuration Service Database ---${NC}"
create_database "$DB_CONFIG"
apply_schema "$DB_CONFIG"
set_permissions "$DB_CONFIG"
verify_tables "$DB_CONFIG"

echo ""
echo -e "${GREEN}=================================================================${NC}"
echo -e "${GREEN}  Database initialization completed successfully!${NC}"
echo -e "${GREEN}=================================================================${NC}"
echo ""
echo -e "${GREEN}Summary:${NC}"
echo -e "  ${GREEN}✓${NC} Database '$DB_STUDENT' ready for Student Service"
echo -e "  ${GREEN}✓${NC} Database '$DB_CONFIG' ready for Configuration Service"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo -e "  1. Start Student Service: cd backend/student-service && mvn spring-boot:run"
echo -e "  2. Start Configuration Service: cd backend/configuration-service && mvn spring-boot:run"
echo ""

# Clean up
unset PGPASSWORD
