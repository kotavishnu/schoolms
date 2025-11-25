@echo off
REM School Management System - Stop All Services Script (Windows)
echo ========================================
echo School Management System - Stopping All Services
echo ========================================
echo.

echo Step 1: Stopping Spring Boot services...
echo Looking for Java processes running Spring Boot...

REM Kill Spring Boot processes
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq java.exe" /NH') do (
    echo Stopping process %%i...
    taskkill /PID %%i /F 2>nul
)

echo.
echo Step 2: Stopping Docker containers...
docker-compose down

echo.
echo ========================================
echo All services stopped!
echo ========================================
echo.
pause
