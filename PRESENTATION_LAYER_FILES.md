# Presentation Layer Implementation - File Reference

## Complete File Listing with Absolute Paths

### Application Services (4 files)

1. **StudentManagementService.java**
   - Path: `D:\wks-sms-specs\backend\student-service\src\main\java\com\school\sms\student\application\service\StudentManagementService.java`
   - Purpose: Student update and delete operations
   - Methods: updateStudent(), deleteStudent()

2. **StudentSearchService.java**
   - Path: `D:\wks-sms-specs\backend\student-service\src\main\java\com\school\sms\student\application\service\StudentSearchService.java`
   - Purpose: Student search and retrieval
   - Methods: findById(), findAll(), findByStatus(), findByLastName()

3. **StudentStatusService.java**
   - Path: `D:\wks-sms-specs\backend\student-service\src\main\java\com\school\sms\student\application\service\StudentStatusService.java`
   - Purpose: Student status management
   - Methods: updateStatus()

4. **StudentStatisticsService.java**
   - Path: `D:\wks-sms-specs\backend\student-service\src\main\java\com\school\sms\student\application\service\StudentStatisticsService.java`
   - Purpose: Student statistics and analytics
   - Methods: getStatistics()

### Presentation Layer DTOs (2 files)

5. **UpdateStatusRequest.java**
   - Path: `D:\wks-sms-specs\backend\student-service\src\main\java\com\school\sms\student\presentation\dto\UpdateStatusRequest.java`
   - Purpose: Request DTO for status updates
   - Fields: status

6. **StudentPageResponse.java**
   - Path: `D:\wks-sms-specs\backend\student-service\src\main\java\com\school\sms\student\presentation\dto\StudentPageResponse.java`
   - Purpose: Paginated response wrapper
   - Fields: content, page, size, totalElements, totalPages, first, last

### Exception Handling (1 file)

7. **GlobalExceptionHandler.java**
   - Path: `D:\wks-sms-specs\backend\student-service\src\main\java\com\school\sms\student\presentation\exception\GlobalExceptionHandler.java`
   - Purpose: Centralized exception handling with RFC 7807
   - Handlers: 6 exception types

### REST Controllers (2 files)

8. **StudentController.java**
   - Path: `D:\wks-sms-specs\backend\student-service\src\main\java\com\school\sms\student\presentation\controller\StudentController.java`
   - Purpose: Main CRUD controller
   - Endpoints: 6 (POST, GET, PUT, DELETE, GET-search, PATCH-status)

9. **StudentStatisticsController.java**
   - Path: `D:\wks-sms-specs\backend\student-service\src\main\java\com\school\sms\student\presentation\controller\StudentStatisticsController.java`
   - Purpose: Statistics controller
   - Endpoints: 1 (GET statistics)

### Configuration (1 file)

10. **OpenApiConfig.java**
    - Path: `D:\wks-sms-specs\backend\student-service\src\main\java\com\school\sms\student\infrastructure\config\OpenApiConfig.java`
    - Purpose: Swagger/OpenAPI configuration
    - Features: API info, servers configuration

### Integration Tests (2 files)

11. **StudentControllerIntegrationTest.java**
    - Path: `D:\wks-sms-specs\backend\student-service\src\test\java\com\school\sms\student\presentation\controller\StudentControllerIntegrationTest.java`
    - Purpose: Controller integration tests
    - Tests: 11 test cases

12. **StudentStatisticsControllerTest.java**
    - Path: `D:\wks-sms-specs\backend\student-service\src\test\java\com\school\sms\student\presentation\controller\StudentStatisticsControllerTest.java`
    - Purpose: Statistics controller tests
    - Tests: 1 test case

### Modified Files (1 file)

13. **pom.xml**
    - Path: `D:\wks-sms-specs\backend\student-service\pom.xml`
    - Change: Fixed Flyway dependency issue

---

## Code Snippets from Key Files

### StudentController Example Endpoint

```java
@PostMapping
@Operation(summary = "Create a new student", description = "Register a new student with validation")
public ResponseEntity<StudentDTO> createStudent(
        @Valid @RequestBody CreateStudentRequest request,
        @RequestHeader(value = "X-User-ID", defaultValue = "SYSTEM") String userId) {

    log.info("Creating student: {} {}", request.getFirstName(), request.getLastName());
    StudentDTO student = registrationService.registerStudent(request, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(student);
}
```

### GlobalExceptionHandler Example

```java
@ExceptionHandler(StudentNotFoundException.class)
public ProblemDetail handleStudentNotFoundException(StudentNotFoundException ex) {
    log.error("Student not found: {}", ex.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
    );
    problemDetail.setType(URI.create(PROBLEM_BASE_URI + "not-found"));
    problemDetail.setTitle("Student Not Found");
    problemDetail.setProperty("timestamp", Instant.now());

    return problemDetail;
}
```

### StudentSearchService Example

```java
public StudentDTO findById(String studentId) {
    log.debug("Finding student by ID: {}", studentId);

    Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new StudentNotFoundException("Student not found: " + studentId));

    return studentMapper.toDTO(student);
}
```

---

## Directory Structure

```
D:\wks-sms-specs\backend\student-service\
├── src\
│   ├── main\
│   │   ├── java\com\school\sms\student\
│   │   │   ├── application\
│   │   │   │   ├── dto\
│   │   │   │   │   ├── AddressDTO.java (existing)
│   │   │   │   │   └── StudentDTO.java (existing)
│   │   │   │   ├── mapper\
│   │   │   │   │   └── StudentMapper.java (existing)
│   │   │   │   └── service\
│   │   │   │       ├── StudentRegistrationService.java (existing)
│   │   │   │       ├── StudentManagementService.java ✨ NEW
│   │   │   │       ├── StudentSearchService.java ✨ NEW
│   │   │   │       ├── StudentStatusService.java ✨ NEW
│   │   │   │       └── StudentStatisticsService.java ✨ NEW
│   │   │   ├── domain\
│   │   │   │   ├── entity\
│   │   │   │   ├── exception\
│   │   │   │   ├── repository\
│   │   │   │   └── valueobject\
│   │   │   ├── infrastructure\
│   │   │   │   ├── config\
│   │   │   │   │   ├── DatabaseConfig.java (existing)
│   │   │   │   │   └── OpenApiConfig.java ✨ NEW
│   │   │   │   ├── persistence\
│   │   │   │   └── util\
│   │   │   └── presentation\
│   │   │       ├── controller\
│   │   │       │   ├── StudentController.java ✨ NEW
│   │   │       │   └── StudentStatisticsController.java ✨ NEW
│   │   │       ├── dto\
│   │   │       │   ├── CreateStudentRequest.java (existing)
│   │   │       │   ├── UpdateStudentRequest.java (existing)
│   │   │       │   ├── UpdateStatusRequest.java ✨ NEW
│   │   │       │   └── StudentPageResponse.java ✨ NEW
│   │   │       └── exception\
│   │   │           └── GlobalExceptionHandler.java ✨ NEW
│   │   └── resources\
│   │       ├── application.yml
│   │       ├── application-docker.yml
│   │       └── db\migration\
│   └── test\
│       └── java\com\school\sms\student\
│           ├── domain\entity\
│           │   └── StudentTest.java (existing)
│           ├── domain\valueobject\
│           │   └── AddressTest.java (existing)
│           ├── infrastructure\
│           │   └── StudentRepositoryIntegrationTest.java (existing)
│           └── presentation\controller\
│               ├── StudentControllerIntegrationTest.java ✨ NEW
│               └── StudentStatisticsControllerTest.java ✨ NEW
└── pom.xml (modified)
```

Legend:
- ✨ NEW - Newly created file
- (existing) - Previously existing file
- (modified) - Modified existing file

---

## Quick Access Commands

### View a specific file:
```bash
# Example: View StudentController
cat "D:\wks-sms-specs\backend\student-service\src\main\java\com\school\sms\student\presentation\controller\StudentController.java"
```

### Compile the project:
```bash
cd "D:\wks-sms-specs\backend\student-service"
mvn clean compile
```

### Run tests:
```bash
cd "D:\wks-sms-specs\backend\student-service"
mvn test
```

### Run the service:
```bash
cd "D:\wks-sms-specs\backend\student-service"
mvn spring-boot:run
```

### Access Swagger UI:
```
http://localhost:8081/swagger-ui.html
```

---

**Total New Files**: 13
**Total Modified Files**: 1
**Total Lines of Code (approx)**: 1,500+
