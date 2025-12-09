# Project Structure - School Management System (SMS)

## 1. Overview

This document defines the complete project structure for both backend (Spring Boot microservices) and frontend (React/Next.js) applications. The structure follows industry best practices for maintainability, scalability, and clear separation of concerns.

### 1.1 Repository Structure

```
wks-sms-autonomous/
├── backend/
│   ├── api-gateway/
│   ├── student-service/
│   ├── configuration-service/
│   ├── shared-lib/
│   └── pom.xml (parent POM)
├── frontend/
│   └── sms-web/
├── infrastructure/
│   ├── docker/
│   ├── kubernetes/
│   └── terraform/
├── specs/
│   └── architecture/
├── docs/
└── README.md
```

---

## 2. Backend Structure

### 2.1 Parent POM Structure

**Location:** `backend/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.0</version>
    </parent>

    <groupId>com.sms</groupId>
    <artifactId>sms-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>shared-lib</module>
        <module>student-service</module>
        <module>configuration-service</module>
        <module>api-gateway</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
        <mapstruct.version>1.6.0</mapstruct.version>
        <lombok.version>1.18.30</lombok.version>
        <drools.version>9.44.0.Final</drools.version>
    </properties>

    <dependencyManagement>
        <!-- Centralized dependency versions -->
    </dependencyManagement>
</project>
```

---

### 2.2 Student Service Structure

**Location:** `backend/student-service/`

```
student-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── sms/
│   │   │           └── student/
│   │   │               ├── StudentServiceApplication.java
│   │   │               │
│   │   │               ├── presentation/        # REST Controllers
│   │   │               │   ├── controller/
│   │   │               │   │   ├── StudentController.java
│   │   │               │   │   └── EnrollmentHistoryController.java
│   │   │               │   ├── dto/
│   │   │               │   │   ├── request/
│   │   │               │   │   │   ├── CreateStudentRequest.java
│   │   │               │   │   │   └── UpdateStudentRequest.java
│   │   │               │   │   └── response/
│   │   │               │   │       ├── StudentDTO.java
│   │   │               │   │       └── EnrollmentHistoryDTO.java
│   │   │               │   └── exception/
│   │   │               │       ├── GlobalExceptionHandler.java
│   │   │               │       ├── ProblemDetail.java
│   │   │               │       └── ErrorCode.java
│   │   │               │
│   │   │               ├── application/          # Application Services
│   │   │               │   ├── service/
│   │   │               │   │   ├── StudentService.java
│   │   │               │   │   ├── StudentServiceImpl.java
│   │   │               │   │   ├── EnrollmentHistoryService.java
│   │   │               │   │   └── StudentSearchService.java
│   │   │               │   ├── mapper/
│   │   │               │   │   ├── StudentMapper.java
│   │   │               │   │   └── EnrollmentHistoryMapper.java
│   │   │               │   ├── command/         # CQRS Commands
│   │   │               │   │   ├── CreateStudentCommand.java
│   │   │               │   │   ├── UpdateStudentCommand.java
│   │   │               │   │   └── DeleteStudentCommand.java
│   │   │               │   └── query/           # CQRS Queries
│   │   │               │       ├── GetStudentQuery.java
│   │   │               │       └── SearchStudentsQuery.java
│   │   │               │
│   │   │               ├── domain/               # Domain Layer
│   │   │               │   ├── model/
│   │   │               │   │   ├── Student.java
│   │   │               │   │   ├── EnrollmentHistory.java
│   │   │               │   │   └── StudentStatus.java (enum)
│   │   │               │   ├── repository/
│   │   │               │   │   ├── StudentRepository.java (interface)
│   │   │               │   │   └── EnrollmentHistoryRepository.java
│   │   │               │   ├── service/         # Domain Services
│   │   │               │   │   ├── StudentKeyGenerator.java
│   │   │               │   │   └── StudentValidator.java
│   │   │               │   ├── valueobject/     # Value Objects
│   │   │               │   │   ├── Mobile.java
│   │   │               │   │   ├── Email.java
│   │   │               │   │   └── AdhaarNumber.java
│   │   │               │   └── exception/
│   │   │               │       ├── StudentNotFoundException.java
│   │   │               │       ├── DuplicateMobileException.java
│   │   │               │       └── AgeValidationException.java
│   │   │               │
│   │   │               └── infrastructure/       # Infrastructure Layer
│   │   │                   ├── persistence/
│   │   │                   │   ├── entity/      # JPA Entities
│   │   │                   │   │   ├── StudentEntity.java
│   │   │                   │   │   └── EnrollmentHistoryEntity.java
│   │   │                   │   ├── repository/  # JPA Repository Implementations
│   │   │                   │   │   ├── JpaStudentRepository.java
│   │   │                   │   │   └── StudentRepositoryImpl.java
│   │   │                   │   └── specification/
│   │   │                   │       └── StudentSpecification.java
│   │   │                   ├── config/
│   │   │                   │   ├── DatabaseConfig.java
│   │   │                   │   ├── RedisConfig.java
│   │   │                   │   ├── DroolsConfig.java
│   │   │                   │   ├── SecurityConfig.java (Phase 2)
│   │   │                   │   └── ObservabilityConfig.java
│   │   │                   ├── cache/
│   │   │                   │   └── StudentCacheService.java
│   │   │                   ├── rules/
│   │   │                   │   └── StudentValidationRules.java
│   │   │                   └── audit/
│   │   │                       ├── AuditLogger.java
│   │   │                       └── AuditLogEntity.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── db/
│   │       │   └── migration/
│   │       │       ├── V1.0.0__Create_student_table.sql
│   │       │       ├── V1.0.1__Create_enrollment_history_table.sql
│   │       │       └── V1.0.2__Create_audit_log_table.sql
│   │       ├── rules/
│   │       │   └── student-validation.drl
│   │       ├── static/
│   │       └── templates/
│   │
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── sms/
│       │           └── student/
│       │               ├── presentation/
│       │               │   └── controller/
│       │               │       └── StudentControllerTest.java
│       │               ├── application/
│       │               │   └── service/
│       │               │       └── StudentServiceTest.java
│       │               ├── domain/
│       │               │   └── service/
│       │               │       └── StudentValidatorTest.java
│       │               └── infrastructure/
│       │                   ├── persistence/
│       │                   │   └── StudentRepositoryTest.java
│       │                   └── integration/
│       │                       ├── StudentApiIntegrationTest.java
│       │                       └── DatabaseIntegrationTest.java
│       │
│       └── resources/
│           ├── application-test.yml
│           └── test-data.sql
│
├── pom.xml
├── Dockerfile
├── .dockerignore
└── README.md
```

---

### 2.3 Configuration Service Structure

**Location:** `backend/configuration-service/`

```
configuration-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── sms/
│   │   │           └── config/
│   │   │               ├── ConfigurationServiceApplication.java
│   │   │               │
│   │   │               ├── presentation/
│   │   │               │   ├── controller/
│   │   │               │   │   ├── SchoolProfileController.java
│   │   │               │   │   └── ConfigurationSettingController.java
│   │   │               │   └── dto/
│   │   │               │       ├── SchoolProfileDTO.java
│   │   │               │       ├── ConfigurationSettingDTO.java
│   │   │               │       └── CreateSettingRequest.java
│   │   │               │
│   │   │               ├── application/
│   │   │               │   ├── service/
│   │   │               │   │   ├── SchoolProfileService.java
│   │   │               │   │   └── ConfigurationSettingService.java
│   │   │               │   └── mapper/
│   │   │               │       ├── SchoolProfileMapper.java
│   │   │               │       └── ConfigurationSettingMapper.java
│   │   │               │
│   │   │               ├── domain/
│   │   │               │   ├── model/
│   │   │               │   │   ├── SchoolProfile.java
│   │   │               │   │   ├── ConfigurationSetting.java
│   │   │               │   │   └── SettingCategory.java (enum)
│   │   │               │   ├── repository/
│   │   │               │   │   ├── SchoolProfileRepository.java
│   │   │               │   │   └── ConfigurationSettingRepository.java
│   │   │               │   └── exception/
│   │   │               │       └── SettingNotFoundException.java
│   │   │               │
│   │   │               └── infrastructure/
│   │   │                   ├── persistence/
│   │   │                   │   ├── entity/
│   │   │                   │   │   ├── SchoolProfileEntity.java
│   │   │                   │   │   └── ConfigurationSettingEntity.java
│   │   │                   │   └── repository/
│   │   │                   │       ├── JpaSchoolProfileRepository.java
│   │   │                   │       └── JpaConfigurationSettingRepository.java
│   │   │                   ├── config/
│   │   │                   │   ├── DatabaseConfig.java
│   │   │                   │   └── CacheConfig.java
│   │   │                   └── cache/
│   │   │                       └── ConfigurationCacheService.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/
│   │           └── migration/
│   │               ├── V1.0.0__Create_school_profile_table.sql
│   │               ├── V1.0.1__Create_configuration_setting_table.sql
│   │               └── V1.1.0__Insert_default_settings.sql
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── sms/
│                   └── config/
│                       ├── presentation/
│                       ├── application/
│                       └── infrastructure/
│
├── pom.xml
├── Dockerfile
└── README.md
```

---

### 2.4 API Gateway Structure

**Location:** `backend/api-gateway/`

```
api-gateway/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── sms/
│   │   │           └── gateway/
│   │   │               ├── ApiGatewayApplication.java
│   │   │               ├── config/
│   │   │               │   ├── GatewayConfig.java
│   │   │               │   ├── CorsConfig.java
│   │   │               │   ├── RateLimitConfig.java
│   │   │               │   └── SecurityConfig.java (Phase 2)
│   │   │               ├── filter/
│   │   │               │   ├── CorrelationIdFilter.java
│   │   │               │   ├── LoggingFilter.java
│   │   │               │   └── AuthenticationFilter.java (Phase 2)
│   │   │               └── exception/
│   │   │                   └── GlobalErrorHandler.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── sms/
│                   └── gateway/
│                       └── filter/
│                           └── CorrelationIdFilterTest.java
│
├── pom.xml
├── Dockerfile
└── README.md
```

---

### 2.5 Shared Library Structure

**Location:** `backend/shared-lib/`

```
shared-lib/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── sms/
│   │               └── shared/
│   │                   ├── exception/
│   │                   │   ├── BaseException.java
│   │                   │   ├── ResourceNotFoundException.java
│   │                   │   ├── ValidationException.java
│   │                   │   └── BusinessRuleViolationException.java
│   │                   ├── dto/
│   │                   │   ├── ProblemDetail.java
│   │                   │   └── ErrorResponse.java
│   │                   ├── util/
│   │                   │   ├── DateTimeUtils.java
│   │                   │   └── ValidationUtils.java
│   │                   ├── constant/
│   │                   │   ├── ErrorCodes.java
│   │                   │   └── HttpHeaders.java
│   │                   └── annotation/
│   │                       ├── ValidMobile.java
│   │                       └── ValidAdhaar.java
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── sms/
│                   └── shared/
│                       └── util/
│                           └── ValidationUtilsTest.java
│
├── pom.xml
└── README.md
```

---

## 3. Frontend Structure

### 3.1 React/Next.js Application Structure

**Location:** `frontend/sms-web/`

```
sms-web/
├── public/
│   ├── favicon.ico
│   ├── logo.png
│   └── images/
│
├── src/
│   ├── app/                          # Next.js 15 App Router
│   │   ├── layout.tsx                # Root layout
│   │   ├── page.tsx                  # Home page
│   │   ├── error.tsx                 # Error boundary
│   │   ├── loading.tsx               # Loading UI
│   │   │
│   │   ├── students/
│   │   │   ├── layout.tsx
│   │   │   ├── page.tsx              # Student list
│   │   │   ├── loading.tsx
│   │   │   ├── new/
│   │   │   │   └── page.tsx          # Create student
│   │   │   └── [studentKey]/
│   │   │       ├── page.tsx          # Student detail
│   │   │       ├── edit/
│   │   │       │   └── page.tsx      # Edit student
│   │   │       └── history/
│   │   │           └── page.tsx      # Enrollment history
│   │   │
│   │   ├── configurations/
│   │   │   ├── layout.tsx
│   │   │   ├── page.tsx              # Configuration management
│   │   │   └── [category]/
│   │   │       └── page.tsx          # Category-specific settings
│   │   │
│   │   └── school/
│   │       └── profile/
│   │           └── page.tsx          # School profile
│   │
│   ├── components/                   # Reusable components
│   │   ├── ui/                       # UI primitives
│   │   │   ├── Button/
│   │   │   │   ├── Button.tsx
│   │   │   │   ├── Button.test.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Input/
│   │   │   │   ├── Input.tsx
│   │   │   │   └── index.ts
│   │   │   ├── Card/
│   │   │   ├── Modal/
│   │   │   ├── Table/
│   │   │   ├── Pagination/
│   │   │   └── Spinner/
│   │   │
│   │   ├── layout/                   # Layout components
│   │   │   ├── Header/
│   │   │   │   └── Header.tsx
│   │   │   ├── Sidebar/
│   │   │   │   └── Sidebar.tsx
│   │   │   ├── Footer/
│   │   │   │   └── Footer.tsx
│   │   │   └── PageLayout/
│   │   │       └── PageLayout.tsx
│   │   │
│   │   ├── student/                  # Student-specific components
│   │   │   ├── StudentForm/
│   │   │   │   ├── StudentForm.tsx
│   │   │   │   ├── StudentForm.test.tsx
│   │   │   │   └── index.ts
│   │   │   ├── StudentList/
│   │   │   │   ├── StudentList.tsx
│   │   │   │   ├── StudentListItem.tsx
│   │   │   │   └── index.ts
│   │   │   ├── StudentSearch/
│   │   │   │   └── StudentSearch.tsx
│   │   │   ├── StudentCard/
│   │   │   └── EnrollmentHistoryTable/
│   │   │
│   │   ├── configuration/            # Configuration components
│   │   │   ├── ConfigurationForm/
│   │   │   ├── ConfigurationList/
│   │   │   └── SchoolProfileForm/
│   │   │
│   │   └── common/                   # Common components
│   │       ├── ErrorBoundary/
│   │       ├── LoadingSpinner/
│   │       ├── ErrorMessage/
│   │       ├── SuccessMessage/
│   │       └── ConfirmDialog/
│   │
│   ├── features/                     # Feature-based modules
│   │   ├── student/
│   │   │   ├── hooks/
│   │   │   │   ├── useStudent.ts
│   │   │   │   ├── useStudents.ts
│   │   │   │   ├── useCreateStudent.ts
│   │   │   │   ├── useUpdateStudent.ts
│   │   │   │   └── useDeleteStudent.ts
│   │   │   ├── api/
│   │   │   │   └── studentApi.ts
│   │   │   ├── types/
│   │   │   │   └── student.types.ts
│   │   │   └── schemas/
│   │   │       └── studentSchema.ts
│   │   │
│   │   └── configuration/
│   │       ├── hooks/
│   │       │   ├── useSchoolProfile.ts
│   │       │   ├── useConfigurations.ts
│   │       │   └── useConfiguration.ts
│   │       ├── api/
│   │       │   └── configurationApi.ts
│   │       ├── types/
│   │       │   └── configuration.types.ts
│   │       └── schemas/
│   │           └── configurationSchema.ts
│   │
│   ├── lib/                          # Shared utilities
│   │   ├── api/
│   │   │   ├── client.ts             # Axios instance
│   │   │   ├── interceptors.ts       # Request/response interceptors
│   │   │   └── types.ts              # API types
│   │   ├── utils/
│   │   │   ├── formatters.ts         # Date, number formatting
│   │   │   ├── validators.ts         # Validation helpers
│   │   │   └── constants.ts          # App constants
│   │   ├── errors/
│   │   │   ├── ApiError.ts
│   │   │   └── errorHandler.ts
│   │   └── queryClient.ts            # React Query configuration
│   │
│   ├── hooks/                        # Global hooks
│   │   ├── useDebounce.ts
│   │   ├── useLocalStorage.ts
│   │   ├── useMediaQuery.ts
│   │   └── usePagination.ts
│   │
│   ├── context/                      # React Context providers
│   │   ├── ThemeContext.tsx
│   │   └── NotificationContext.tsx
│   │
│   ├── styles/                       # Global styles
│   │   ├── globals.css
│   │   └── tailwind.css
│   │
│   └── types/                        # Global TypeScript types
│       ├── api.types.ts
│       └── common.types.ts
│
├── tests/                            # E2E tests
│   ├── e2e/
│   │   ├── student/
│   │   │   ├── student-registration.spec.ts
│   │   │   ├── student-search.spec.ts
│   │   │   └── student-update.spec.ts
│   │   └── configuration/
│   │       └── configuration-management.spec.ts
│   └── fixtures/
│       └── testData.ts
│
├── .env.local                        # Local environment variables
├── .env.development                  # Development environment
├── .env.production                   # Production environment
├── .eslintrc.js                      # ESLint configuration
├── .prettierrc                       # Prettier configuration
├── tailwind.config.js                # Tailwind CSS configuration
├── tsconfig.json                     # TypeScript configuration
├── next.config.js                    # Next.js configuration
├── package.json
├── pnpm-lock.yaml
├── playwright.config.ts              # Playwright configuration
├── vitest.config.ts                  # Vitest configuration
├── Dockerfile
├── .dockerignore
└── README.md
```

---

## 4. Infrastructure Structure

### 4.1 Docker Configuration

**Location:** `infrastructure/docker/`

```
docker/
├── docker-compose.yml                # Development environment
├── docker-compose.prod.yml           # Production environment
├── docker-compose.test.yml           # Test environment
├── nginx/
│   ├── nginx.conf
│   └── ssl/
│       ├── certificate.crt
│       └── private.key
└── postgres/
    └── init/
        └── init-databases.sql
```

---

### 4.2 Kubernetes Configuration

**Location:** `infrastructure/kubernetes/`

```
kubernetes/
├── namespaces/
│   ├── development.yaml
│   └── production.yaml
│
├── configmaps/
│   ├── student-service-config.yaml
│   ├── config-service-config.yaml
│   └── api-gateway-config.yaml
│
├── secrets/
│   ├── database-secrets.yaml
│   ├── redis-secrets.yaml
│   └── jwt-secrets.yaml (Phase 2)
│
├── deployments/
│   ├── student-service-deployment.yaml
│   ├── config-service-deployment.yaml
│   ├── api-gateway-deployment.yaml
│   ├── postgres-deployment.yaml
│   └── redis-deployment.yaml
│
├── services/
│   ├── student-service-service.yaml
│   ├── config-service-service.yaml
│   ├── api-gateway-service.yaml
│   ├── postgres-service.yaml
│   └── redis-service.yaml
│
├── ingress/
│   └── api-gateway-ingress.yaml
│
├── persistent-volumes/
│   ├── postgres-pv.yaml
│   └── redis-pv.yaml
│
├── hpa/                              # Horizontal Pod Autoscaler
│   ├── student-service-hpa.yaml
│   └── config-service-hpa.yaml
│
└── monitoring/
    ├── prometheus-deployment.yaml
    ├── grafana-deployment.yaml
    └── zipkin-deployment.yaml
```

---

### 4.3 Terraform Configuration

**Location:** `infrastructure/terraform/`

```
terraform/
├── main.tf
├── variables.tf
├── outputs.tf
├── terraform.tfvars
│
├── modules/
│   ├── vpc/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── eks/                          # AWS EKS cluster
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── rds/                          # PostgreSQL RDS
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   └── elasticache/                  # Redis ElastiCache
│       ├── main.tf
│       ├── variables.tf
│       └── outputs.tf
│
└── environments/
    ├── development/
    │   ├── main.tf
    │   └── terraform.tfvars
    ├── staging/
    │   ├── main.tf
    │   └── terraform.tfvars
    └── production/
        ├── main.tf
        └── terraform.tfvars
```

---

## 5. Configuration Files

### 5.1 Backend Configuration (application.yml)

**Location:** `backend/student-service/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: student-service

  profiles:
    active: ${SPRING_PROFILE:dev}

  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:student_db}
    username: ${DB_USERNAME:sms_user}
    password: ${DB_PASSWORD:secure_password}
    driver-class-name: org.postgresql.Driver
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      pool-name: StudentServiceHikariPool

  jpa:
    hibernate:
      ddl-auto: validate
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
    show-sql: false

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5

  cache:
    type: redis
    redis:
      time-to-live: 300000
      cache-null-values: false

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true

  zipkin:
    base-url: http://${ZIPKIN_HOST:localhost}:9411

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

server:
  port: 8081
  compression:
    enabled: true
  error:
    include-message: always
    include-binding-errors: always

logging:
  level:
    com.sms.student: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

---

### 5.2 Frontend Configuration (next.config.js)

**Location:** `frontend/sms-web/next.config.js`

```javascript
/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,

  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL,
  },

  images: {
    domains: ['cdn.sms.com', 'localhost'],
    formats: ['image/webp', 'image/avif'],
  },

  experimental: {
    appDir: true,
  },

  // Webpack configuration
  webpack: (config, { isServer }) => {
    if (!isServer) {
      config.resolve.fallback = {
        ...config.resolve.fallback,
        fs: false,
      };
    }
    return config;
  },

  // Headers for security
  async headers() {
    return [
      {
        source: '/(.*)',
        headers: [
          {
            key: 'X-Content-Type-Options',
            value: 'nosniff',
          },
          {
            key: 'X-Frame-Options',
            value: 'DENY',
          },
          {
            key: 'X-XSS-Protection',
            value: '1; mode=block',
          },
        ],
      },
    ];
  },
};

module.exports = nextConfig;
```

---

### 5.3 Environment Variables

#### Backend (.env)
```properties
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=student_db
DB_USERNAME=sms_user
DB_PASSWORD=secure_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# Zipkin
ZIPKIN_HOST=localhost

# Spring Profile
SPRING_PROFILE=dev
```

#### Frontend (.env.local)
```properties
NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

---

## 6. Build & Deployment Scripts

### 6.1 Maven Build Scripts

**Location:** `backend/build.sh`

```bash
#!/bin/bash

echo "Building SMS Backend Services..."

# Build parent POM
mvn clean install -DskipTests

# Build individual services
cd student-service
mvn clean package -DskipTests
cd ..

cd configuration-service
mvn clean package -DskipTests
cd ..

cd api-gateway
mvn clean package -DskipTests
cd ..

echo "Backend build completed successfully!"
```

---

### 6.2 Frontend Build Scripts

**Location:** `frontend/sms-web/package.json`

```json
{
  "name": "sms-web",
  "version": "1.0.0",
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint",
    "test": "vitest",
    "test:e2e": "playwright test",
    "test:coverage": "vitest --coverage",
    "format": "prettier --write \"src/**/*.{ts,tsx}\"",
    "type-check": "tsc --noEmit"
  }
}
```

---

## 7. CI/CD Pipeline Structure

### 7.1 GitHub Actions Workflow

**Location:** `.github/workflows/ci-cd.yml`

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  backend-build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Build with Maven
        run: |
          cd backend
          mvn clean package
      - name: Run Tests
        run: |
          cd backend
          mvn test
      - name: Upload Coverage
        uses: codecov/codecov-action@v3

  frontend-build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
      - name: Install Dependencies
        run: |
          cd frontend/sms-web
          npm ci
      - name: Build
        run: |
          cd frontend/sms-web
          npm run build
      - name: Run Tests
        run: |
          cd frontend/sms-web
          npm test
```

---

## 8. Documentation Structure

**Location:** `docs/`

```
docs/
├── architecture/
│   ├── system-design.md
│   ├── api-documentation.md
│   └── database-schema.md
├── development/
│   ├── setup-guide.md
│   ├── coding-standards.md
│   └── git-workflow.md
├── deployment/
│   ├── docker-deployment.md
│   ├── kubernetes-deployment.md
│   └── production-checklist.md
├── user-guides/
│   ├── student-registration.md
│   ├── configuration-management.md
│   └── search-functionality.md
└── api/
    ├── openapi-spec.yaml
    └── postman-collection.json
```

---

## 9. Naming Conventions

### 9.1 Backend Naming Conventions

| Component | Convention | Example |
|-----------|-----------|---------|
| Package | lowercase, dot-separated | com.sms.student.application |
| Class | PascalCase | StudentService |
| Interface | PascalCase (no I prefix) | StudentRepository |
| Method | camelCase | findStudentByKey() |
| Variable | camelCase | studentKey |
| Constant | UPPER_SNAKE_CASE | MAX_AGE_LIMIT |
| DTO | PascalCase + DTO suffix | StudentDTO |
| Entity | PascalCase + Entity suffix | StudentEntity |
| Test | PascalCase + Test suffix | StudentServiceTest |

### 9.2 Frontend Naming Conventions

| Component | Convention | Example |
|-----------|-----------|---------|
| Component | PascalCase | StudentForm.tsx |
| Hook | camelCase + use prefix | useStudent.ts |
| Utility | camelCase | formatDate.ts |
| Type/Interface | PascalCase | Student, StudentFormData |
| Variable | camelCase | studentData |
| Constant | UPPER_SNAKE_CASE | API_BASE_URL |
| CSS Class | kebab-case | student-card |

---

## 10. Code Quality Standards

### 10.1 Backend Quality Gates

- **Code Coverage**: Minimum 80% (unit + integration)
- **Cyclomatic Complexity**: Maximum 10 per method
- **Method Length**: Maximum 50 lines
- **Class Length**: Maximum 500 lines
- **Code Smells**: Zero critical/major issues

### 10.2 Frontend Quality Gates

- **Code Coverage**: Minimum 80%
- **Bundle Size**: Maximum 500KB (initial load)
- **Performance**: Lighthouse score > 90
- **Accessibility**: WCAG 2.1 Level AA

---

## 11. Conclusion

This project structure provides:

1. **Clear Separation**: Domain, application, and infrastructure layers
2. **Scalability**: Microservices can be developed independently
3. **Testability**: Test-friendly structure with isolated layers
4. **Maintainability**: Consistent naming and organization
5. **DevOps Ready**: Docker, Kubernetes, and CI/CD configurations

All developers should follow this structure strictly to ensure consistency and maintainability across the codebase.

---

**Document Version**: 1.0
**Last Updated**: 2025-12-08
**Status**: Approved for Implementation
