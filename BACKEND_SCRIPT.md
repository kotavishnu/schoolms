# Backend Server Startup Guide

This guide provides commands to start all backend microservices for the School Management System.

## Prerequisites

- Java 21 or higher
- Maven 3.9+
- PostgreSQL 15+ (running on port 5432)
- Ensure PostgreSQL has the following databases created:
  - `student_db` (user: postgres, password: admin)
  - `configuration_db` (user: postgres, password: admin)

## Service Architecture

The system consists of 4 microservices:
1. **Eureka Server** - Service Discovery (Port 8761)
2. **Student Service** - Student Management (Port 8081)
3. **Configuration Service** - Configuration Management (Port 8082)
4. **API Gateway** - Single Entry Point (Port 8080)

## Startup Order

Services must be started in this order to ensure proper registration:

### 1. Start Eureka Server (Service Discovery)

```bash
cd /d/wks-sms-specs/backend/eureka-server
mvn spring-boot:run
```

**Wait for Eureka to fully start** (look for "Started EurekaServerApplication" in logs)

Access Eureka Dashboard: http://localhost:8761

### 2. Start Student Service

```bash
cd /d/wks-sms-specs/backend/student-service
mvn spring-boot:run
```

**Wait for service to register** (look for "Registering application STUDENT-SERVICE with eureka")

Access Student Service: http://localhost:8081
Swagger UI: http://localhost:8081/swagger-ui.html

### 3. Start Configuration Service

```bash
cd /d/wks-sms-specs/backend/configuration-service
mvn spring-boot:run
```

**Wait for service to register** (look for "Registering application CONFIGURATION-SERVICE with eureka")

Access Configuration Service: http://localhost:8082

### 4. Start API Gateway

```bash
cd /d/wks-sms-specs/backend/api-gateway
mvn spring-boot:run
```

**Wait for gateway to register** (look for "Registering application API-GATEWAY with eureka")

Access API Gateway: http://localhost:8080

## Running All Services in Background (Windows)

To run all services in the background simultaneously:

```bash
# Start Eureka Server
start cmd /k "cd /d D:\wks-sms-specs\backend\eureka-server && mvn spring-boot:run"

# Wait 30 seconds for Eureka to start
timeout /t 30 /nobreak

# Start Student Service
start cmd /k "cd /d D:\wks-sms-specs\backend\student-service && mvn spring-boot:run"

# Start Configuration Service
start cmd /k "cd /d D:\wks-sms-specs\backend\configuration-service && mvn spring-boot:run"

# Wait 30 seconds for services to register
timeout /t 30 /nobreak

# Start API Gateway
start cmd /k "cd /d D:\wks-sms-specs\backend\api-gateway && mvn spring-boot:run"
```

## Running All Services in Background (Linux/Mac)

```bash
# Start Eureka Server
cd /d/wks-sms-specs/backend/eureka-server && mvn spring-boot:run &

# Wait for Eureka to start
sleep 30

# Start Student Service
cd /d/wks-sms-specs/backend/student-service && mvn spring-boot:run &

# Start Configuration Service
cd /d/wks-sms-specs/backend/configuration-service && mvn spring-boot:run &

# Wait for services to register
sleep 30

# Start API Gateway
cd /d/wks-sms-specs/backend/api-gateway && mvn spring-boot:run &
```

## Verification

### Check Service Registration

Visit Eureka Dashboard at http://localhost:8761 to verify all services are registered:
- EUREKA-SERVER
- STUDENT-SERVICE
- CONFIGURATION-SERVICE
- API-GATEWAY

### Check Service Health

```bash
# Check Eureka registry
curl http://localhost:8761/eureka/apps

# Check Student Service health
curl http://localhost:8081/actuator/health

# Check Configuration Service health
curl http://localhost:8082/actuator/health

# Check API Gateway health
curl http://localhost:8080/actuator/health
```

## API Gateway Routes

The API Gateway routes requests to the appropriate microservices:

- `/api/v1/students/**` → Student Service (Port 8081)
- `/api/v1/configurations/**` → Configuration Service (Port 8082)

### Example Requests via Gateway

```bash
# Access Student Service through Gateway
curl http://localhost:8080/api/v1/students

# Access Configuration Service through Gateway
curl http://localhost:8080/api/v1/configurations
```

## Stopping Services

### Windows
- Close each command window or press `Ctrl+C` in each terminal

### Linux/Mac
```bash
# Find Java processes
ps aux | grep spring-boot

# Kill by process ID
kill <PID>

# Or kill all Maven processes
pkill -f "spring-boot:run"
```

## Troubleshooting

### Service Not Registering with Eureka
- Ensure Eureka Server is running before starting other services
- Wait at least 30 seconds for initial registration
- Check logs for connection errors

### Port Already in Use
```bash
# Windows - Find process using port
netstat -ano | findstr :8080

# Linux/Mac - Find process using port
lsof -i :8080

# Kill the process
# Windows: taskkill /PID <PID> /F
# Linux/Mac: kill -9 <PID>
```

### Database Connection Issues
- Verify PostgreSQL is running on port 5432
- Check database credentials in `application.yml`
- Ensure databases `student_db` and `configuration_db` exist

### Logs
Application logs are displayed in the terminal/command window where each service was started.

## Quick Reference

| Service | Port | URL |
|---------|------|-----|
| Eureka Server | 8761 | http://localhost:8761 |
| Student Service | 8081 | http://localhost:8081 |
| Configuration Service | 8082 | http://localhost:8082 |
| API Gateway | 8080 | http://localhost:8080 |
| PostgreSQL | 5432 | localhost:5432 |

## API Documentation (Swagger/OpenAPI)

Both Student Service and Configuration Service have interactive Swagger UI documentation enabled.

### Student Service API Documentation

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/api-docs
- **OpenAPI YAML**: http://localhost:8081/api-docs.yaml

**Available Endpoints:**
- Student registration and management
- Student search and filtering
- Student statistics

### Configuration Service API Documentation

- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8082/api-docs
- **OpenAPI YAML**: http://localhost:8082/api-docs.yaml

**Available Endpoints:**
- School profile management
- Configuration settings
- System configurations

### Using Swagger UI

1. Open the Swagger UI URL in your web browser
2. Browse available API endpoints organized by controller
3. Expand any endpoint to see:
   - Request parameters and body schema
   - Response codes and schemas
   - Example values
4. Click "Try it out" to test endpoints directly from the browser
5. Fill in required parameters and click "Execute"
6. View the response body, headers, and status code

### Accessing API Documentation via API Gateway

**Note:** Swagger UI is not exposed through the API Gateway. To access API documentation, connect directly to the services:

- For Student Service APIs: Use http://localhost:8081/swagger-ui.html
- For Configuration Service APIs: Use http://localhost:8082/swagger-ui.html

When calling APIs through the gateway in your application, use:
- `http://localhost:8080/api/v1/students/**` for Student Service endpoints
- `http://localhost:8080/api/v1/configurations/**` for Configuration Service endpoints

### Actuator Endpoints

All services expose actuator endpoints for monitoring and health checks:

```bash
# Student Service
curl http://localhost:8081/actuator/health
curl http://localhost:8081/actuator/info
curl http://localhost:8081/actuator/metrics
curl http://localhost:8081/actuator/prometheus

# Configuration Service
curl http://localhost:8082/actuator/health
curl http://localhost:8082/actuator/info
curl http://localhost:8082/actuator/metrics
curl http://localhost:8082/actuator/prometheus

# API Gateway
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/info
curl http://localhost:8080/actuator/metrics

# Eureka Server
curl http://localhost:8761/actuator/health
curl http://localhost:8761/actuator/info
```

## Notes

- All services use Spring Boot 3.2.0 with Java 21
- Services automatically register with Eureka on startup
- API Gateway provides load balancing and routing
- CORS is enabled on the API Gateway for all origins
- Actuator endpoints are exposed for monitoring
- Swagger/OpenAPI documentation is available for services
- SpringDoc OpenAPI version 2.2.0 is used for API documentation generation
