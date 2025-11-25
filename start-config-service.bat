@echo off
REM Start Configuration Service only
echo Starting Configuration Service...
cd configuration-service
mvn spring-boot:run
