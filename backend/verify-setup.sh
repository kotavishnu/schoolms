#!/bin/bash

# School Management System - Setup Verification Script
# This script verifies that all prerequisites are installed and the project is properly set up

echo "========================================="
echo "SMS Backend - Setup Verification"
echo "========================================="
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check functions
check_command() {
    if command -v $1 &> /dev/null; then
        echo -e "${GREEN}✓${NC} $1 is installed"
        if [ "$2" != "" ]; then
            version=$($2)
            echo "  Version: $version"
        fi
        return 0
    else
        echo -e "${RED}✗${NC} $1 is not installed"
        return 1
    fi
}

# Check Java
echo "Checking Java..."
if check_command java "java -version 2>&1 | head -n 1"; then
    java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$java_version" -ge 21 ]; then
        echo -e "  ${GREEN}Java 21+ detected${NC}"
    else
        echo -e "  ${RED}Warning: Java 21 is required, but version $java_version is installed${NC}"
    fi
fi
echo ""

# Check Maven
echo "Checking Maven..."
check_command mvn "mvn -version | head -n 1"
echo ""

# Check Docker
echo "Checking Docker..."
check_command docker "docker --version"
if [ $? -eq 0 ]; then
    if docker ps &> /dev/null; then
        echo -e "  ${GREEN}Docker daemon is running${NC}"
    else
        echo -e "  ${YELLOW}Docker daemon is not running. Start it with: sudo systemctl start docker${NC}"
    fi
fi
echo ""

# Check Docker Compose
echo "Checking Docker Compose..."
check_command docker-compose "docker-compose --version"
echo ""

# Check PostgreSQL (Docker)
echo "Checking PostgreSQL container..."
if docker ps | grep -q sms-postgres; then
    echo -e "${GREEN}✓${NC} PostgreSQL container is running"
else
    echo -e "${YELLOW}○${NC} PostgreSQL container is not running"
    echo "  Start with: docker-compose up -d postgres"
fi
echo ""

# Check Redis (Docker)
echo "Checking Redis container..."
if docker ps | grep -q sms-redis; then
    echo -e "${GREEN}✓${NC} Redis container is running"
else
    echo -e "${YELLOW}○${NC} Redis container is not running"
    echo "  Start with: docker-compose up -d redis"
fi
echo ""

# Check Maven build
echo "Checking Maven project..."
if [ -f "pom.xml" ]; then
    echo -e "${GREEN}✓${NC} Parent POM found"
    echo "  Running Maven compile..."
    if mvn clean compile -DskipTests -q; then
        echo -e "  ${GREEN}✓ Project compiles successfully${NC}"
    else
        echo -e "  ${RED}✗ Project compilation failed${NC}"
    fi
else
    echo -e "${RED}✗${NC} pom.xml not found. Are you in the backend directory?"
fi
echo ""

# Check project structure
echo "Checking project structure..."
dirs=("shared-lib" "student-service")
for dir in "${dirs[@]}"; do
    if [ -d "$dir" ]; then
        echo -e "${GREEN}✓${NC} $dir module exists"
    else
        echo -e "${RED}✗${NC} $dir module not found"
    fi
done
echo ""

# Summary
echo "========================================="
echo "Setup Verification Complete"
echo "========================================="
echo ""
echo "Next steps:"
echo "1. Start infrastructure: docker-compose up -d"
echo "2. Build project: mvn clean install"
echo "3. Run service: cd student-service && mvn spring-boot:run"
echo "4. Check API docs: http://localhost:8081/swagger-ui.html"
echo ""
