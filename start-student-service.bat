@echo off
REM Start Student Service only
echo Starting Student Service...
cd student-service
mvn spring-boot:run
