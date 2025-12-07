@echo off
REM ==============================================================================
REM School Management System - Run All Services (Windows)
REM ==============================================================================
REM Purpose: Start both Student and Configuration services
REM ==============================================================================

setlocal

set STUDENT_SERVICE_DIR=..\backend\student-service
set CONFIG_SERVICE_DIR=..\backend\configuration-service

REM Print header
echo ================================================================
echo   School Management System - Starting All Services
echo ================================================================
echo.

REM Check if service directories exist
if not exist "%STUDENT_SERVICE_DIR%" (
    echo [ERROR] Student service directory not found: %STUDENT_SERVICE_DIR%
    exit /b 1
)

if not exist "%CONFIG_SERVICE_DIR%" (
    echo [ERROR] Configuration service directory not found: %CONFIG_SERVICE_DIR%
    exit /b 1
)

echo --- Starting Services ---
echo.

REM Start Student Service in new window
echo [INFO] Starting Student Service on port 8081...
start "Student Service" cmd /k "cd /d %STUDENT_SERVICE_DIR% && mvn spring-boot:run"
timeout /t 5 /nobreak >nul
echo [INFO] Student Service window opened
echo.

REM Start Configuration Service in new window
echo [INFO] Starting Configuration Service on port 8082...
start "Configuration Service" cmd /k "cd /d %CONFIG_SERVICE_DIR% && mvn spring-boot:run"
timeout /t 2 /nobreak >nul
echo [INFO] Configuration Service window opened
echo.

echo ================================================================
echo   All services are starting in separate windows...
echo ================================================================
echo.
echo Service URLs:
echo   Student Service API:        http://localhost:8081/api/v1/students
echo   Student Service Swagger:    http://localhost:8081/swagger-ui/index.html
echo   Configuration Service API:  http://localhost:8082/api/v1/configurations
echo   Configuration Service Swagger: http://localhost:8082/swagger-ui/index.html
echo.
echo Note: Each service is running in its own command window
echo Note: Close the command windows to stop the services
echo.

pause

endlocal
