# Data Flow Diagrams

## Table of Contents
1. [Student Registration Flow](#student-registration-flow)
2. [Student Update Flow](#student-update-flow)
3. [Student Search Flow](#student-search-flow)
4. [Configuration Management Flow](#configuration-management-flow)
5. [Error Handling Flow](#error-handling-flow)
6. [System Startup Flow](#system-startup-flow)

## Student Registration Flow

### High-Level Process Flow

```mermaid
sequenceDiagram
    actor User as School Admin/Staff
    participant UI as Web Client
    participant GW as API Gateway
    participant Auth as Auth Service
    participant SS as Student Service
    participant Val as Validators
    participant Repo as Repository
    participant DB as PostgreSQL

    User->>UI: Fill student registration form
    UI->>UI: Client-side validation
    UI->>GW: POST /api/v1/students

    GW->>Auth: Validate token
    Auth-->>GW: User context (ID, Role)

    GW->>SS: Forward request + User ID

    Note over SS: Request enters Controller
    SS->>SS: @Valid annotation validation

    alt Validation Fails
        SS-->>GW: 400 Bad Request
        GW-->>UI: Validation errors
        UI-->>User: Show field errors
    end

    SS->>Val: Validate age (3-18 years)

    alt Age Invalid
        Val-->>SS: throw InvalidAgeException
        SS-->>GW: 400 Bad Request
        GW-->>UI: Error: Invalid age
        UI-->>User: Show age error message
    end

    SS->>Repo: Check mobile uniqueness
    Repo->>DB: SELECT EXISTS(mobile)
    DB-->>Repo: Result

    alt Mobile Exists
        Repo-->>SS: true
        SS-->>GW: 409 Conflict
        GW-->>UI: Error: Duplicate mobile
        UI-->>User: Show duplicate error
    end

    SS->>SS: Generate Student ID
    SS->>SS: Create Student entity
    SS->>Repo: save(student)
    Repo->>DB: INSERT INTO students
    DB-->>Repo: Saved entity with ID
    Repo-->>SS: Student entity

    SS->>SS: Map to DTO
    SS-->>GW: 201 Created + StudentDTO
    GW-->>UI: StudentDTO
    UI-->>User: Show success message
```

### Detailed Component Interaction

```mermaid
flowchart TD
    Start([User submits form]) --> ClientVal{Client-side<br/>validation}
    ClientVal -->|Invalid| ShowError1[Show validation errors]
    ClientVal -->|Valid| SendReq[Send POST request]

    SendReq --> Gateway[API Gateway]
    Gateway --> AuthCheck{Auth valid?}
    AuthCheck -->|No| Return401[401 Unauthorized]
    AuthCheck -->|Yes| Controller[Student Controller]

    Controller --> BeanVal{Bean<br/>validation}
    BeanVal -->|Invalid| Return400a[400 Bad Request]
    BeanVal -->|Valid| Service[Registration Service]

    Service --> AgeVal{Age 3-18?}
    AgeVal -->|No| Return400b[400 Invalid Age]
    AgeVal -->|Yes| MobileCheck{Mobile<br/>unique?}

    MobileCheck -->|No| Return409[409 Conflict]
    MobileCheck -->|Yes| GenID[Generate Student ID]

    GenID --> CreateEntity[Create Student entity]
    CreateEntity --> SaveDB[(Save to DB)]
    SaveDB --> MapDTO[Map to DTO]
    MapDTO --> Return201[201 Created]

    Return401 --> End([End])
    Return400a --> End
    Return400b --> End
    Return409 --> End
    Return201 --> End
    ShowError1 --> End
```

### Data Transformation Flow

```mermaid
graph LR
    subgraph "Client Layer"
        FormData[Form Data<br/>firstName: Rajesh<br/>lastName: Kumar<br/>DOB: 2015-05-15]
    end

    subgraph "Presentation Layer"
        DTO1[CreateStudentRequest<br/>Bean Validation]
    end

    subgraph "Application Layer"
        Command[Registration Command<br/>Business Logic]
    end

    subgraph "Domain Layer"
        Entity[Student Entity<br/>Domain Model]
    end

    subgraph "Infrastructure Layer"
        JPAEntity[JPA Entity<br/>with Mappings]
    end

    subgraph "Database Layer"
        DBRow[(Database Row<br/>students table)]
    end

    FormData -->|JSON| DTO1
    DTO1 -->|Map| Command
    Command -->|Create| Entity
    Entity -->|Persist| JPAEntity
    JPAEntity -->|INSERT| DBRow

    DBRow -->|SELECT| JPAEntity2[JPA Entity]
    JPAEntity2 -->|Map| Entity2[Student Entity]
    Entity2 -->|Map| DTO2[StudentDTO]
    DTO2 -->|JSON| Response[API Response]
```

## Student Update Flow

### Update Process with Optimistic Locking

```mermaid
sequenceDiagram
    actor User
    participant UI as Web Client
    participant GW as API Gateway
    participant SS as Student Service
    participant Repo as Repository
    participant DB as PostgreSQL

    User->>UI: Request student details
    UI->>GW: GET /api/v1/students/{id}
    GW->>SS: Get student
    SS->>Repo: findById(studentId)
    Repo->>DB: SELECT * FROM students WHERE id = ?
    DB-->>Repo: Student row (version=0)
    Repo-->>SS: Student entity
    SS-->>GW: StudentDTO (version=0)
    GW-->>UI: StudentDTO
    UI-->>User: Display form with data

    User->>UI: Update fields
    UI->>GW: PUT /api/v1/students/{id}<br/>(includes version=0)

    GW->>SS: Update student
    SS->>Repo: findById(studentId)
    Repo->>DB: SELECT
    DB-->>Repo: Current student (version=0)

    alt Student Not Found
        Repo-->>SS: Optional.empty()
        SS-->>GW: 404 Not Found
        GW-->>UI: Error: Student not found
    end

    Repo-->>SS: Student entity

    SS->>SS: Validate changes<br/>(age, mobile uniqueness)

    alt Validation Fails
        SS-->>GW: 400/409 Error
        GW-->>UI: Validation error
    end

    SS->>SS: Apply updates<br/>version check
    SS->>Repo: save(student)
    Repo->>DB: UPDATE students SET ...<br/>WHERE id = ? AND version = 0

    alt Version Mismatch
        DB-->>Repo: 0 rows updated
        Repo-->>SS: OptimisticLockException
        SS-->>GW: 409 Concurrent Update
        GW-->>UI: Error: Record modified<br/>by another user
        UI-->>User: Show conflict message
    end

    DB-->>Repo: 1 row updated (version=1)
    Repo-->>SS: Updated student
    SS-->>GW: 200 OK + StudentDTO
    GW-->>UI: Updated StudentDTO
    UI-->>User: Show success
```

### Concurrent Update Scenario

```mermaid
sequenceDiagram
    participant User1
    participant User2
    participant Service
    participant DB

    Note over DB: Student (version=0)

    User1->>Service: GET student
    Service->>DB: SELECT (version=0)
    DB-->>User1: Student data (version=0)

    User2->>Service: GET student
    Service->>DB: SELECT (version=0)
    DB-->>User2: Student data (version=0)

    User1->>Service: UPDATE (version=0)
    Service->>DB: UPDATE WHERE version=0
    DB-->>Service: Success (version=1)
    Service-->>User1: 200 OK

    User2->>Service: UPDATE (version=0)
    Service->>DB: UPDATE WHERE version=0
    DB-->>Service: 0 rows (version mismatch)
    Service-->>User2: 409 Conflict<br/>Concurrent modification
```

## Student Search Flow

### Search with Pagination

```mermaid
sequenceDiagram
    actor User
    participant UI as Web Client
    participant GW as API Gateway
    participant SS as Student Service
    participant Repo as Repository
    participant DB as PostgreSQL

    User->>UI: Enter search criteria
    UI->>GW: GET /api/v1/students?<br/>lastName=Kumar&status=ACTIVE<br/>&page=0&size=20

    GW->>SS: Search students
    SS->>SS: Build query specification
    SS->>Repo: findAll(spec, pageable)

    Repo->>DB: SELECT * FROM students<br/>WHERE last_name LIKE 'Kumar%'<br/>AND status = 'ACTIVE'<br/>ORDER BY created_at DESC<br/>LIMIT 20 OFFSET 0

    DB-->>Repo: Result rows + total count
    Repo-->>SS: Page<Student>
    SS->>SS: Map to DTOs
    SS-->>GW: PagedResponse<br/>{content, page, totalElements}
    GW-->>UI: JSON response
    UI-->>User: Display paginated results

    User->>UI: Click page 2
    UI->>GW: GET ?page=1&size=20
    GW->>SS: Search students
    SS->>Repo: findAll(spec, pageable)
    Repo->>DB: LIMIT 20 OFFSET 20
    DB-->>Repo: Next page results
    Repo-->>SS: Page<Student>
    SS-->>GW: PagedResponse
    GW-->>UI: JSON response
    UI-->>User: Display page 2
```

### Query Building Flow

```mermaid
graph TB
    Start([Search Request]) --> Parse[Parse Query Parameters]
    Parse --> BuildSpec[Build JPA Specification]

    BuildSpec --> HasName{lastName<br/>provided?}
    HasName -->|Yes| AddNameFilter[Add name LIKE filter]
    HasName -->|No| CheckStatus

    AddNameFilter --> CheckStatus{status<br/>provided?}
    CheckStatus -->|Yes| AddStatusFilter[Add status = filter]
    CheckStatus -->|No| CheckAge

    AddStatusFilter --> CheckAge{age range<br/>provided?}
    CheckAge -->|Yes| AddAgeFilter[Add age BETWEEN filter]
    CheckAge -->|No| AddPagination

    AddAgeFilter --> AddPagination[Add Pagination<br/>page, size, sort]
    AddPagination --> ExecuteQuery[(Execute Query)]
    ExecuteQuery --> MapResults[Map to DTOs]
    MapResults --> BuildResponse[Build PagedResponse]
    BuildResponse --> End([Return Response])
```

## Configuration Management Flow

### Get Settings by Category

```mermaid
sequenceDiagram
    actor Admin
    participant UI as Web Client
    participant GW as API Gateway
    participant CS as Config Service
    participant Repo as Config Repository
    participant DB as Config DB

    Admin->>UI: Navigate to settings
    UI->>GW: GET /api/v1/configurations/settings/category/GENERAL

    GW->>CS: Get settings by category
    CS->>Repo: findByCategory(GENERAL)
    Repo->>DB: SELECT * FROM configuration_settings<br/>WHERE category = 'GENERAL'<br/>ORDER BY key

    DB-->>Repo: Setting rows
    Repo-->>CS: List<ConfigurationSetting>
    CS->>CS: Map to DTOs
    CS-->>GW: CategorySettingsDTO
    GW-->>UI: JSON response
    UI-->>Admin: Display settings

    Admin->>UI: Update SCHOOL_TIMEZONE value
    UI->>GW: PUT /api/v1/configurations/settings/{id}<br/>{value: "Asia/Calcutta", version: 0}

    GW->>CS: Update setting
    CS->>Repo: findById(settingId)
    Repo->>DB: SELECT
    DB-->>Repo: Setting (version=0)

    CS->>CS: Update value
    CS->>Repo: save(setting)
    Repo->>DB: UPDATE WHERE version=0
    DB-->>Repo: Updated (version=1)
    Repo-->>CS: Updated setting
    CS-->>GW: 200 OK
    GW-->>UI: Success
    UI-->>Admin: Show success message
```

### School Profile Update

```mermaid
sequenceDiagram
    actor Admin
    participant UI as Web Client
    participant GW as API Gateway
    participant CS as Config Service
    participant DB as Config DB

    Admin->>UI: Navigate to school profile
    UI->>GW: GET /api/v1/configurations/school-profile
    GW->>CS: Get school profile
    CS->>DB: SELECT * FROM school_profile WHERE id = 1
    DB-->>CS: School profile row
    CS-->>GW: SchoolProfileDTO
    GW-->>UI: JSON response
    UI-->>Admin: Display profile form

    Admin->>UI: Update school details
    UI->>GW: PUT /api/v1/configurations/school-profile<br/>{schoolName, address, phone, email}

    GW->>CS: Update school profile
    CS->>DB: UPDATE school_profile<br/>SET school_name = ?, address = ?<br/>WHERE id = 1
    DB-->>CS: Updated row
    CS-->>GW: 200 OK + SchoolProfileDTO
    GW-->>UI: Updated profile
    UI-->>Admin: Show success
```

## Error Handling Flow

### Exception Handling Chain

```mermaid
graph TB
    Start([Request]) --> Controller[Controller Layer]
    Controller --> Service[Service Layer]
    Service --> Repository[Repository Layer]
    Repository --> DB[(Database)]

    DB -->|Constraint Violation| RepoEx[SQLException]
    RepoEx -->|Wrap| DomainEx1[Domain Exception]

    Service -->|Business Rule<br/>Violation| DomainEx2[Domain Exception]

    Controller -->|Validation<br/>Failure| ValidationEx[Validation Exception]

    DomainEx1 --> Handler[Global Exception Handler]
    DomainEx2 --> Handler
    ValidationEx --> Handler

    Handler --> Map{Exception Type}

    Map -->|InvalidAgeException| Build400a[Build 400 Response]
    Map -->|DuplicateMobileException| Build409[Build 409 Response]
    Map -->|StudentNotFoundException| Build404[Build 404 Response]
    Map -->|ValidationException| Build400b[Build 400 Response]
    Map -->|OptimisticLockException| Build409b[Build 409 Response]
    Map -->|Other Exception| Build500[Build 500 Response]

    Build400a --> RFC7807[RFC 7807<br/>Problem Details]
    Build409 --> RFC7807
    Build404 --> RFC7807
    Build400b --> RFC7807
    Build409b --> RFC7807
    Build500 --> RFC7807

    RFC7807 --> LogError[Log Error Details]
    LogError --> Return([Return Error Response])
```

### Error Response Flow

```mermaid
sequenceDiagram
    participant Client
    participant Controller
    participant Service
    participant ExHandler as Global Exception Handler
    participant Logger

    Client->>Controller: POST /students<br/>(age = 2 years)
    Controller->>Service: registerStudent()
    Service->>Service: validateAge()

    Service-->>Controller: throw InvalidAgeException
    Controller->>ExHandler: Exception caught

    ExHandler->>ExHandler: Build ProblemDetail<br/>status: 400<br/>title: "Invalid Age"

    ExHandler->>Logger: log.warn("Invalid age: 2")

    ExHandler-->>Client: 400 Bad Request<br/>{<br/>  "type": "...invalid-age",<br/>  "title": "Invalid Age",<br/>  "status": 400,<br/>  "detail": "Age must be 3-18",<br/>  "timestamp": "..."<br/>}
```

## System Startup Flow

### Service Initialization

```mermaid
sequenceDiagram
    participant JVM
    participant SpringBoot as Spring Boot
    participant Flyway
    participant DB as PostgreSQL
    participant App as Application
    participant Eureka

    JVM->>SpringBoot: Start application
    SpringBoot->>SpringBoot: Load application.yml
    SpringBoot->>SpringBoot: Initialize beans

    SpringBoot->>Flyway: Database migration check
    Flyway->>DB: Check schema_version table

    alt Migrations Pending
        DB-->>Flyway: Missing versions
        Flyway->>DB: Execute V1__create_tables.sql
        Flyway->>DB: Execute V2__create_indexes.sql
        DB-->>Flyway: Migrations complete
    else Up to Date
        DB-->>Flyway: All migrations applied
    end

    Flyway-->>SpringBoot: Migration complete

    SpringBoot->>App: Initialize application context
    App->>App: Create @Component beans
    App->>App: Initialize @Service beans
    App->>App: Start web server

    App->>Eureka: Register with Eureka
    Eureka-->>App: Registration successful

    App->>SpringBoot: Health check ready
    SpringBoot-->>JVM: Application started

    Note over JVM: Application ready<br/>to accept requests
```

### Service Discovery Flow

```mermaid
sequenceDiagram
    participant SS as Student Service
    participant CS as Config Service
    participant Eureka
    participant GW as API Gateway

    Note over Eureka: Eureka Server starts

    SS->>Eureka: Register STUDENT-SERVICE<br/>Host: student-service<br/>Port: 8081
    Eureka-->>SS: Registration confirmed

    CS->>Eureka: Register CONFIGURATION-SERVICE<br/>Host: configuration-service<br/>Port: 8082
    Eureka-->>CS: Registration confirmed

    GW->>Eureka: Register API-GATEWAY<br/>Host: api-gateway<br/>Port: 8080
    Eureka-->>GW: Registration confirmed

    loop Every 30 seconds
        SS->>Eureka: Send heartbeat
        CS->>Eureka: Send heartbeat
        GW->>Eureka: Send heartbeat
    end

    GW->>Eureka: Fetch service registry
    Eureka-->>GW: Service instances<br/>{STUDENT-SERVICE: [...],<br/>CONFIGURATION-SERVICE: [...]}

    Note over GW: Gateway can now<br/>route requests
```

### Request Routing Flow

```mermaid
graph LR
    Client([Client]) -->|/api/v1/students| Gateway[API Gateway]

    Gateway -->|Lookup| Eureka[(Eureka<br/>Service Registry)]
    Eureka -->|Instances| Gateway

    Gateway -->|Load Balance| LB{Load Balancer}

    LB -->|Route| SS1[Student Service<br/>Instance 1]
    LB -->|Route| SS2[Student Service<br/>Instance 2]

    SS1 --> DB1[(Student DB)]
    SS2 --> DB1

    SS1 -->|Response| Gateway
    SS2 -->|Response| Gateway
    Gateway -->|Response| Client
```

## Summary

The data flow diagrams illustrate:

1. **Student Registration**: Complete flow from form submission to database persistence
2. **Update Operations**: Optimistic locking and concurrent update handling
3. **Search Operations**: Query building and pagination
4. **Configuration Management**: Settings and school profile updates
5. **Error Handling**: Exception propagation and RFC 7807 responses
6. **System Startup**: Service initialization and discovery registration

These flows provide a clear understanding of how data moves through the system and how different components interact during various operations.

---

**Version**: 1.0
**Last Updated**: 2025-11-17
**Status**: Draft for Review
