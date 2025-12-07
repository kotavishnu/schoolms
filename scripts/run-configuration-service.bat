@echo off
REM ==============================================================================
REM School Management System - Run Configuration Service (Windows)
REM ==============================================================================
REM Purpose: Start the Configuration Service application
REM Port: 8082
REM ==============================================================================

setlocal

set SERVICE_NAME=Configuration Service
set SERVICE_DIR=..\backend\configuration-service
set SERVICE_PORT=8082

REM Print header
echo ================================================================
echo   Starting %SERVICE_NAME%
echo ================================================================
echo.

REM Check if directory exists
if not exist "%SERVICE_DIR%" (
    echo [ERROR] Service directory not found: %SERVICE_DIR%
    exit /b 1
)

REM Navigate to service directory
echo [INFO] Navigating to %SERVICE_DIR%
cd /d "%SERVICE_DIR%"

REM Display configuration
echo [INFO] Configuration:
echo   Service: %SERVICE_NAME%
echo   Port: %SERVICE_PORT%
echo   Directory: %CD%
echo.

REM Check if port is already in use (Windows)
netstat -ano | findstr :%SERVICE_PORT% | findstr LISTENING >nul
if %errorlevel% equ 0 (
    echo [WARNING] Port %SERVICE_PORT% is already in use!
    echo [WARNING] Another instance may be running.
    echo [WARNING] Please kill the existing process manually or use a different port.
    echo.
    pause
)

REM Start the service
echo [INFO] Starting %SERVICE_NAME%...
echo ================================================================
echo.

mvn spring-boot:run

REM This line is only reached if mvn spring-boot:run exits
echo.
echo [WARNING] %SERVICE_NAME% has stopped.
pause

endlocal
