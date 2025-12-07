#!/bin/bash

# ==============================================================================
# School Management System - Run Configuration Service (Linux/Mac)
# ==============================================================================
# Purpose: Start the Configuration Service application
# Port: 8082
# ==============================================================================

set -e

# Color codes for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

SERVICE_NAME="Configuration Service"
SERVICE_DIR="../backend/configuration-service"
SERVICE_PORT=8082

# Print header
echo -e "${BLUE}=================================================================${NC}"
echo -e "${BLUE}  Starting $SERVICE_NAME${NC}"
echo -e "${BLUE}=================================================================${NC}"
echo ""

# Check if directory exists
if [ ! -d "$SERVICE_DIR" ]; then
    echo -e "${RED}[ERROR]${NC} Service directory not found: $SERVICE_DIR"
    exit 1
fi

# Navigate to service directory
echo -e "${GREEN}[INFO]${NC} Navigating to $SERVICE_DIR"
cd "$SERVICE_DIR"

# Display configuration
echo -e "${GREEN}[INFO]${NC} Configuration:"
echo -e "  Service: $SERVICE_NAME"
echo -e "  Port: $SERVICE_PORT"
echo -e "  Directory: $(pwd)"
echo ""

# Check if port is already in use
if lsof -Pi :$SERVICE_PORT -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo -e "${YELLOW}[WARNING]${NC} Port $SERVICE_PORT is already in use!"
    echo -e "${YELLOW}[WARNING]${NC} Another instance may be running. Kill it? (y/n)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        PID=$(lsof -t -i:$SERVICE_PORT)
        kill -9 $PID
        echo -e "${GREEN}[INFO]${NC} Killed process on port $SERVICE_PORT (PID: $PID)"
        sleep 2
    else
        echo -e "${RED}[ERROR]${NC} Cannot start service. Port $SERVICE_PORT is occupied."
        exit 1
    fi
fi

# Start the service
echo -e "${GREEN}[INFO]${NC} Starting $SERVICE_NAME..."
echo -e "${BLUE}=================================================================${NC}"
echo ""

mvn spring-boot:run

# This line is only reached if mvn spring-boot:run exits
echo ""
echo -e "${YELLOW}[WARNING]${NC} $SERVICE_NAME has stopped."
