#!/bin/bash
# School Management System - Stop All Services Script (Linux/Mac)

echo "========================================"
echo "School Management System - Stopping All Services"
echo "========================================"
echo

# Stop Spring Boot services using PIDs
echo "Step 1: Stopping Spring Boot services..."

if [ -f logs/student-service.pid ]; then
    STUDENT_PID=$(cat logs/student-service.pid)
    echo "Stopping Student Service (PID: $STUDENT_PID)..."
    kill $STUDENT_PID 2>/dev/null || echo "Student Service already stopped"
    rm logs/student-service.pid
fi

if [ -f logs/configuration-service.pid ]; then
    CONFIG_PID=$(cat logs/configuration-service.pid)
    echo "Stopping Configuration Service (PID: $CONFIG_PID)..."
    kill $CONFIG_PID 2>/dev/null || echo "Configuration Service already stopped"
    rm logs/configuration-service.pid
fi

# Fallback: kill any remaining Spring Boot processes
echo "Cleaning up any remaining Spring Boot processes..."
pkill -f "spring-boot:run" 2>/dev/null || echo "No additional processes to stop"

echo
echo "Step 2: Stopping Docker containers..."
docker-compose down

echo
echo "========================================"
echo "All services stopped!"
echo "========================================"
echo
