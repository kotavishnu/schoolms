# School Management System (SMS)

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-336791.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)]()

Comprehensive web-based platform for automating school administrative workflows including student management, class organization, fee structures, and payment tracking.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Build & Run](#build--run)
- [Testing](#testing)
- [Code Quality](#code-quality)
- [Documentation](#documentation)
- [Contributing](#contributing)

## Overview

The **School Management System (SMS)** is designed to automate up to 80% of manual administrative tasks, providing real-time data accuracy and transparency for school operations supporting up to 2,500 students and 100+ concurrent users.

### Key Objectives

- **Efficiency**: Automate manual workflows
- **Transparency**: Real-time fee and student data
- **Accuracy**: 99%+ data accuracy with validation
- **Scalability**: Support large student populations

## Features

### Phase 1 (Current Scope)

- **Student Management**: Registration, profiles, guardians, status tracking
- **Class Management**: Academic structure (Classes 1-10), sections, year rollover
- **Fee Management**: Flexible fee structures, rule-based calculations
- **Payment Tracking**: Fee journals, payment status, dues reporting
- **Receipt Management**: Auto-generated receipts with audit trail
- **Configuration**: Centralized school settings

## Technology Stack

### Backend

- **Java 21** (LTS) - Latest long-term support with virtual threads
- **Spring Boot 3.5.0** - Enterprise application framework
- **Spring Data JPA 3.x** - Data persistence layer
- **Spring Security 6.x** - Authentication & authorization
- **PostgreSQL 18** - ACID-compliant relational database
- **Redis 7.x** - High-performance caching
- **Drools 9.44.0** - Business rules engine
- **Flyway 10.x** - Database migration management
- **MapStruct 1.5.5** - Type-safe bean mapping
- **JWT** - Token-based authentication

### Testing & Quality

- **JUnit 5** - Unit testing framework
- **Mockito 5** - Mocking framework
- **TestContainers** - Integration testing with real PostgreSQL
- **REST Assured** - API testing
- **JaCoCo** - Code coverage (80%+ target)
- **SonarQube** - Static code analysis

### Build & DevOps

- **Maven 3.9+** - Build automation
- **Docker & Docker Compose** - Containerization
- **GitHub Actions** - CI/CD pipelines
- **Prometheus & Grafana** - Monitoring

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
  ```bash
  java -version
  # Expected: openjdk version "21.x.x"
  ```

- **Apache Maven 3.9.0** or higher
  ```bash
  mvn -version
  # Expected: Apache Maven 3.9.x
  ```

- **PostgreSQL 18** (for local development)
  ```bash
  psql --version
  # Expected: psql (PostgreSQL) 18.x
  ```

- **Redis 7.x** (for caching)
  ```bash
  redis-cli --version
  # Expected: redis-cli 7.x
  ```

- **Git** for version control
  ```bash
  git --version
  ```

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/school-management-system.git
cd school-management-system
```

### 2. Configure Database

Create a PostgreSQL database:

```sql
CREATE DATABASE school_management_db;
CREATE USER school_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE school_management_db TO school_user;
```

### 3. Configure Environment Variables

Create a `.env` file in the project root (or set system environment variables):

```properties
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=school_management_db
DB_USERNAME=school_user
DB_PASSWORD=your_password

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-here
JWT_EXPIRATION=900000

# Application Profile
SPRING_PROFILES_ACTIVE=dev
```

### 4. Build the Project

```bash
mvn clean install
```

This will:
- Compile source code
- Run unit tests
- Generate code coverage reports
- Create executable JAR

### 5. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/school-management-system-1.0.0-SNAPSHOT.jar
```

The application will start on `http://localhost:8080/api`

## Project Structure

```
school-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/school/management/
│   │   │   ├── config/              # Spring configuration classes
│   │   │   ├── domain/              # Domain entities and value objects
│   │   │   │   ├── student/         # Student domain
│   │   │   │   ├── class_/          # Class domain
│   │   │   │   ├── fee/             # Fee domain
│   │   │   │   └── shared/          # Shared domain concepts
│   │   │   ├── application/         # Application services (use cases)
│   │   │   │   ├── student/         # Student use cases
│   │   │   │   ├── commands/        # Command objects
│   │   │   │   ├── queries/         # Query objects
│   │   │   │   ├── dto/             # Data transfer objects
│   │   │   │   └── mapper/          # MapStruct mappers
│   │   │   ├── infrastructure/      # Technical implementations
│   │   │   │   ├── persistence/     # JPA repositories
│   │   │   │   ├── cache/           # Redis cache
│   │   │   │   └── rules/           # Drools rules engine
│   │   │   ├── presentation/        # REST controllers
│   │   │   │   ├── rest/            # Controller classes
│   │   │   │   ├── request/         # Request DTOs
│   │   │   │   └── response/        # Response DTOs
│   │   │   ├── shared/              # Cross-cutting concerns
│   │   │   │   ├── exception/       # Custom exceptions
│   │   │   │   ├── validation/      # Validation utilities
│   │   │   │   └── util/            # Utility classes
│   │   │   └── SchoolManagementApplication.java
│   │   └── resources/
│   │       ├── db/migration/        # Flyway migration scripts
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-test.yml
│   │       └── application-prod.yml
│   └── test/
│       ├── java/                    # Test classes (mirrors main structure)
│       └── resources/
│           └── application-test.yml
├── docs/                            # Additional documentation
├── .github/
│   └── workflows/                   # CI/CD pipelines
├── pom.xml
└── README.md
```

## Build & Run

### Maven Profiles

The project supports multiple profiles:

- **dev** (default): Development environment
- **test**: Testing environment with H2/TestContainers
- **prod**: Production-optimized build with shaded JAR

```bash
# Run with specific profile
mvn spring-boot:run -Pdev
mvn spring-boot:run -Pprod

# Build with specific profile
mvn clean install -Ptest
mvn clean package -Pprod
```

### Running Tests

```bash
# Run all tests (unit + integration)
mvn test

# Run only unit tests
mvn test -DexcludedGroups=integration

# Run only integration tests
mvn verify -DskipUnitTests

# Run with coverage report
mvn clean test jacoco:report
```

Coverage reports are generated in `target/site/jacoco/index.html`

### Code Quality Checks

```bash
# Run SonarQube analysis
mvn clean verify sonar:sonar

# View coverage report
open target/site/jacoco/index.html
```

## Testing

### Test Categories

- **Unit Tests**: Fast, isolated tests with mocking
  - Naming: `*Test.java`
  - Location: Same package as source class

- **Integration Tests**: Tests with real database (TestContainers)
  - Naming: `*IntegrationTest.java`
  - Run with: `mvn verify`

### Coverage Targets

| Layer | Minimum Coverage |
|-------|-----------------|
| Domain | 90% |
| Application | 85% |
| Presentation | 75% |
| Infrastructure | 70% |
| **Overall** | **80%** |

### Writing Tests

Follow TDD methodology:
1. Write failing test first (RED)
2. Write minimal code to pass (GREEN)
3. Refactor while keeping tests green (REFACTOR)

Example:
```java
@Test
@DisplayName("Should validate student age between 3 and 18 years")
void shouldValidateStudentAge_WhenCreatingStudent() {
    // Arrange
    LocalDate invalidDob = LocalDate.now().minusYears(2);

    // Act & Assert
    assertThatThrownBy(() -> studentService.create(invalidDob))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("age must be between 3 and 18");
}
```

## Code Quality

### Standards

- **Java Style**: Follow Google Java Style Guide
- **SOLID Principles**: Mandatory for all classes
- **Clean Code**: Meaningful names, small methods, single responsibility
- **Documentation**: JavaDoc for all public APIs

### Pre-commit Checks

Before committing code, ensure:
- [ ] All tests pass: `mvn test`
- [ ] Code coverage ≥ 80%: Check JaCoCo report
- [ ] No SonarQube critical/major issues
- [ ] Code formatted correctly
- [ ] No unused imports or variables

## Documentation

### API Documentation

Once the application is running, access:
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api/v3/api-docs

### Architecture Documentation

Detailed architecture documents are in `specs/architecture/`:
- System Architecture
- Database Design
- API Specification
- Security Architecture
- Backend Implementation Guide
- Testing Strategy
- DevOps & Deployment Guide

## Contributing

### Branching Strategy

- `main`: Production-ready code
- `develop`: Integration branch for features
- `feature/*`: Feature branches (e.g., `feature/student-management`)
- `bugfix/*`: Bug fix branches
- `hotfix/*`: Production hotfixes

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Example:
```
feat(student): implement student registration with age validation

- Add Student entity with encrypted PII fields
- Implement age validation (3-18 years) in domain layer
- Add unit tests with 95% coverage

Closes #123
```

### Pull Request Process

1. Create feature branch from `develop`
2. Write tests first (TDD)
3. Implement feature
4. Ensure all tests pass and coverage targets met
5. Create PR with descriptive title and description
6. Address code review comments
7. Merge after approval

## License

Proprietary - All rights reserved

## Contact

- **Project Manager**: [Name] - [email]
- **Backend Team Lead**: [Name] - [email]
- **Support**: support@school-sms.com

---

**Version**: 1.0.0 - Sprint 1 Complete ✅
**Last Updated**: November 11, 2025
**Sprint Status**: Infrastructure & Foundation Setup (21/21 story points completed)
