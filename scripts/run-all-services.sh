#!/bin/bash

# ==============================================================================
# School Management System - Run All Services (Linux/Mac)
# ==============================================================================
# Purpose: Start both Student and Configuration services
# ==============================================================================

set -e

# Color codes for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

STUDENT_SERVICE_DIR="../backend/student-service"
CONFIG_SERVICE_DIR="../backend/configuration-service"

# Print header
echo -e "${BLUE}=================================================================${NC}"
echo -e "${BLUE}  School Management System - Starting All Services${NC}"
echo -e "${BLUE}=================================================================${NC}"
echo ""

# Function to check if a port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        return 0  # Port is in use
    else
        return 1  # Port is free
    fi
}

# Function to start a service in the background
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3

    echo -e "${GREEN}[INFO]${NC} Starting $service_name on port $port..."

    if check_port $port; then
        echo -e "${YELLOW}[WARNING]${NC} Port $port is already in use!"
        echo -e "${YELLOW}[INFO]${NC} $service_name may already be running."
    else
        cd "$service_dir"
        mvn spring-boot:run > "/tmp/sms-$service_name.log" 2>&1 &
        local pid=$!
        echo -e "${GREEN}[INFO]${NC} $service_name started with PID: $pid"
        echo -e "${GREEN}[INFO]${NC} Logs: /tmp/sms-$service_name.log"
        cd - > /dev/null
    fi

    echo ""
}

# Check if service directories exist
if [ ! -d "$STUDENT_SERVICE_DIR" ]; then
    echo -e "${RED}[ERROR]${NC} Student service directory not found: $STUDENT_SERVICE_DIR"
    exit 1
fi

if [ ! -d "$CONFIG_SERVICE_DIR" ]; then
    echo -e "${RED}[ERROR]${NC} Configuration service directory not found: $CONFIG_SERVICE_DIR"
    exit 1
fi

# Start services
echo -e "${BLUE}--- Starting Services ---${NC}"
echo ""

start_service "student-service" "$STUDENT_SERVICE_DIR" 8081
sleep 2

start_service "configuration-service" "$CONFIG_SERVICE_DIR" 8082

echo -e "${BLUE}=================================================================${NC}"
echo -e "${GREEN}  All services are starting...${NC}"
echo -e "${BLUE}=================================================================${NC}"
echo ""
echo -e "${GREEN}Service URLs:${NC}"
echo -e "  Student Service API:        http://localhost:8081/api/v1/students"
echo -e "  Student Service Swagger:    http://localhost:8081/swagger-ui/index.html"
echo -e "  Configuration Service API:  http://localhost:8082/api/v1/configurations"
echo -e "  Configuration Service Swagger: http://localhost:8082/swagger-ui/index.html"
echo ""
echo -e "${YELLOW}Note:${NC} Services are running in the background."
echo -e "${YELLOW}Note:${NC} Check logs in /tmp/sms-*.log"
echo -e "${YELLOW}Note:${NC} Use 'ps aux | grep spring-boot' to see running processes"
echo -e "${YELLOW}Note:${NC} Use 'kill <PID>' to stop a service"
echo ""
