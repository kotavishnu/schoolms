@echo off
REM School Management System - Start All Services Script (Windows)
echo ========================================
echo School Management System - Starting All Services
echo ========================================
echo.

REM Check if Docker is running
docker ps >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker is not running. Please start Docker Desktop first.
    echo Starting Docker Desktop...
    start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    echo Waiting for Docker to start (60 seconds)...
    timeout /t 60 /nobreak
)

REM Start Docker infrastructure
echo Step 1: Starting Docker infrastructure (PostgreSQL, Redis, Zipkin)...
docker-compose up -d
if errorlevel 1 (
    echo ERROR: Failed to start Docker infrastructure
    exit /b 1
)

echo Waiting for infrastructure to initialize (30 seconds)...
timeout /t 30 /nobreak

REM Build all services
echo.
echo Step 2: Building all Maven modules...
call mvn clean install -DskipTests
if errorlevel 1 (
    echo ERROR: Maven build failed
    exit /b 1
)

REM Start configuration-service in new window
echo.
echo Step 3: Starting Configuration Service on port 8888...
start "Configuration Service" cmd /k "cd configuration-service && mvn spring-boot:run"

echo Waiting for Configuration Service to start (20 seconds)...
timeout /t 20 /nobreak

REM Start student-service in new window
echo.
echo Step 4: Starting Student Service on port 8081...
start "Student Service" cmd /k "cd student-service && mvn spring-boot:run"

echo.
echo ========================================
echo All services are starting!
echo ========================================
echo.
echo Services:
echo - Configuration Service: http://localhost:8888
echo - Student Service: http://localhost:8081
echo - Student API: http://localhost:8081/api/v1/students
echo - Swagger UI: http://localhost:8081/swagger-ui.html
echo - PostgreSQL: localhost:5432
echo - Redis: localhost:6379
echo - Zipkin: http://localhost:9411
echo.
echo Press any key to view service logs...
pause
echo.
echo Opening service status...
curl -s http://localhost:8081/actuator/health
echo.
echo.
echo Press any key to exit...
pause
