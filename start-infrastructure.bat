@echo off
REM Start Docker infrastructure only
echo Starting Docker infrastructure (PostgreSQL, Redis, Zipkin)...
docker-compose up -d
echo.
echo Infrastructure started!
echo - PostgreSQL: localhost:5432
echo - Redis: localhost:6379
echo - Zipkin: http://localhost:9411
echo.
echo Checking status...
docker-compose ps
pause
