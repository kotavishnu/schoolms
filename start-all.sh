#!/bin/bash
# School Management System - Start All Services Script (Linux/Mac)

echo "========================================"
echo "School Management System - Starting All Services"
echo "========================================"
echo

# Check if Docker is running
if ! docker ps > /dev/null 2>&1; then
    echo "ERROR: Docker is not running. Please start Docker first."
    exit 1
fi

# Start Docker infrastructure
echo "Step 1: Starting Docker infrastructure (PostgreSQL, Redis, Zipkin)..."
docker-compose up -d
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to start Docker infrastructure"
    exit 1
fi

echo "Waiting for infrastructure to initialize (30 seconds)..."
sleep 30

# Build all services
echo
echo "Step 2: Building all Maven modules..."
mvn clean install -DskipTests
if [ $? -ne 0 ]; then
    echo "ERROR: Maven build failed"
    exit 1
fi

# Create logs directory
mkdir -p logs

# Start configuration-service in background
echo
echo "Step 3: Starting Configuration Service on port 8888..."
cd configuration-service
mvn spring-boot:run > ../logs/configuration-service.log 2>&1 &
CONFIG_PID=$!
echo "Configuration Service PID: $CONFIG_PID"
cd ..

echo "Waiting for Configuration Service to start (20 seconds)..."
sleep 20

# Start student-service in background
echo
echo "Step 4: Starting Student Service on port 8081..."
cd student-service
mvn spring-boot:run > ../logs/student-service.log 2>&1 &
STUDENT_PID=$!
echo "Student Service PID: $STUDENT_PID"
cd ..

# Save PIDs for later shutdown
echo $CONFIG_PID > logs/configuration-service.pid
echo $STUDENT_PID > logs/student-service.pid

echo
echo "========================================"
echo "All services are starting!"
echo "========================================"
echo
echo "Services:"
echo "- Configuration Service: http://localhost:8888"
echo "- Student Service: http://localhost:8081"
echo "- Student API: http://localhost:8081/api/v1/students"
echo "- Swagger UI: http://localhost:8081/swagger-ui.html"
echo "- PostgreSQL: localhost:5432"
echo "- Redis: localhost:6379"
echo "- Zipkin: http://localhost:9411"
echo
echo "Logs are in ./logs/ directory"
echo "PIDs saved in ./logs/*.pid files"
echo
echo "To stop services, run: ./stop-all.sh"
echo

# Wait a bit and check health
sleep 10
echo "Checking Student Service health..."
curl -s http://localhost:8081/actuator/health || echo "Service not ready yet, please wait..."
echo
