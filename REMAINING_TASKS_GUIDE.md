# Remaining Implementation Tasks Guide

## Current Progress Summary

### Completed (BE-001 to BE-023)
- Multi-module Maven project structure
- Common module with exceptions and utilities
- Student Service domain layer (Student, StudentId, Mobile, StudentStatus, Events)
- Student Service application layer (DTOs, Validators, Mapper, Service)
- Student Service infrastructure layer (JPA, Drools, Redis, Repository Adapter)
- All with TDD approach (tests written for domain layer)

### Remaining Tasks Overview

## Presentation Layer (BE-024 to BE-028)

### BE-024: REST Controller

Create: `student-service/src/main/java/com/school/sms/student/presentation/controller/StudentController.java`

```java
package com.school.sms.student.presentation.controller;

import com.school.sms.student.application.dto.*;
import com.school.sms.student.application.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Student Management", description = "APIs for managing students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @Operation(summary = "Create a new student")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all students with pagination and search")
    public ResponseEntity<StudentPageResponse> listStudents(
            @ModelAttribute StudentSearchCriteria criteria,
            Pageable pageable) {
        StudentPageResponse response = studentService.searchStudents(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by database ID")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student-id/{studentId}")
    @Operation(summary = "Get student by Student ID (STU-YYYY-XXXXX)")
    public ResponseEntity<StudentResponse> getStudentByStudentId(@PathVariable String studentId) {
        StudentResponse response = studentService.getStudentByStudentId(studentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student profile")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request) {
        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update student status")
    public ResponseEntity<StudentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        StudentResponse response = studentService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student (soft delete)")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
```

### BE-025: Global Exception Handler

Create: `student-service/src/main/java/com/school/sms/student/presentation/exception/GlobalExceptionHandler.java`

```java
package com.school.sms.student.presentation.exception;

import com.school.sms.common.exception.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("https://api.sms.com/errors/not-found"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateResource(
            DuplicateResourceException ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Duplicate Resource");
        problem.setType(URI.create("https://api.sms.com/errors/duplicate"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRuleViolation(
            BusinessRuleViolationException ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Business Rule Violation");
        problem.setType(URI.create("https://api.sms.com/errors/business-rule"));
        problem.setProperty("violations", ex.getViolations());
        problem.setProperty("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation Failed");
        problem.setDetail("One or more fields have validation errors");
        problem.setType(URI.create("https://api.sms.com/errors/validation"));
        problem.setProperty("errors", ex.getBindingResult().getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                error -> error.getField(),
                error -> error.getDefaultMessage()
            )));
        problem.setProperty("timestamp", LocalDateTime.now());
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("https://api.sms.com/errors/internal"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
```

### BE-026: OpenAPI Configuration

Create: `student-service/src/main/java/com/school/sms/student/infrastructure/config/OpenApiConfig.java`

```java
package com.school.sms.student.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Student Service API")
                .description("REST API for Student Management System")
                .version("1.0.0")
                .contact(new Contact()
                    .name("SMS Team")
                    .email("support@sms.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .servers(List.of(
                new Server().url("http://localhost:8081").description("Development Server"),
                new Server().url("https://api.sms.com").description("Production Server")
            ));
    }
}
```

### BE-027 & BE-028: Web Configuration with CORS and Correlation ID

Create: `student-service/src/main/java/com/school/sms/student/infrastructure/config/WebConfig.java`

```java
package com.school.sms.student.infrastructure.config;

import com.school.sms.student.presentation.interceptor.CorrelationIdInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:5173")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CorrelationIdInterceptor());
    }
}
```

Create: `student-service/src/main/java/com/school/sms/student/presentation/interceptor/CorrelationIdInterceptor.java`

```java
package com.school.sms.student.presentation.interceptor;

import jakarta.servlet.http.*;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.UUID;

public class CorrelationIdInterceptor implements HandlerInterceptor {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) {
        MDC.remove(CORRELATION_ID_MDC_KEY);
    }
}
```

## Database Schema SQL

Create: `student-service/src/main/resources/schema.sql`

```sql
-- Student Service Database Schema

CREATE TABLE IF NOT EXISTS students (
    id BIGSERIAL PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    mobile VARCHAR(15) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    father_name VARCHAR(100) NOT NULL,
    mother_name VARCHAR(100),
    identification_mark VARCHAR(100),
    aadhaar_number VARCHAR(12),
    email VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_student_id ON students(student_id);
CREATE INDEX idx_mobile ON students(mobile);
CREATE INDEX idx_last_name ON students(last_name);
CREATE INDEX idx_father_name ON students(father_name);
CREATE INDEX idx_status ON students(status);
```

## Docker Compose (BE-047)

Create: `D:\wks-sms-specs-itr2\docker-compose.yml`

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:18-alpine
    container_name: sms-postgres
    environment:
      POSTGRES_DB: sms_student_db
      POSTGRES_USER: sms_user
      POSTGRES_PASSWORD: sms_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./student-service/src/main/resources/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    networks:
      - sms-network

  redis:
    image: redis:7.2-alpine
    container_name: sms-redis
    ports:
      - "6379:6379"
    networks:
      - sms-network

  zipkin:
    image: openzipkin/zipkin:latest
    container_name: sms-zipkin
    ports:
      - "9411:9411"
    networks:
      - sms-network

volumes:
  postgres_data:

networks:
  sms-network:
    driver: bridge
```

## Application Profiles (BE-048)

Create: `student-service/src/main/resources/application-dev.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sms_student_db
    username: sms_user
    password: sms_password

logging:
  level:
    com.school.sms: DEBUG
    org.hibernate.SQL: DEBUG
```

Create: `student-service/src/main/resources/application-prod.yml`

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}

logging:
  level:
    com.school.sms: INFO
    org.hibernate: WARN

management:
  tracing:
    sampling:
      probability: 0.1
```

## Build Instructions (BE-049)

Run from root directory:
```bash
mvn clean package
```

Run individual service:
```bash
java -jar student-service/target/student-service-1.0.0-SNAPSHOT.jar
```

## Next Implementation Priority

1. Create Presentation Layer files (BE-024 to BE-028)
2. Create database schema SQL
3. Create Docker Compose setup
4. Write integration tests
5. Create Configuration Service (similar pattern to Student Service)
6. Add observability components
7. Create comprehensive README

## Testing Commands

```bash
# Build entire project
mvn clean install

# Run tests
mvn test

# Run with coverage
mvn clean verify

# Run specific service
cd student-service && mvn spring-boot:run
```

## API Endpoints

Once complete, the following endpoints will be available:

- POST /api/v1/students - Create student
- GET /api/v1/students - List students (paginated, searchable)
- GET /api/v1/students/{id} - Get by ID
- GET /api/v1/students/student-id/{studentId} - Get by StudentId
- PUT /api/v1/students/{id} - Update student
- PATCH /api/v1/students/{id}/status - Update status
- DELETE /api/v1/students/{id} - Soft delete

Swagger UI: http://localhost:8081/swagger-ui.html
API Docs: http://localhost:8081/api/v1/api-docs
