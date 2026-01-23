# Business Rules Strategy - School Management System

**Version:** 1.0
**Date:** January 22, 2026
**Status:** Active
**Phase:** 1 - Student Management & Configuration

---

## Table of Contents

1. [Overview](#1-overview)
2. [Drools Architecture](#2-drools-architecture)
3. [Business Rule Definitions](#3-business-rule-definitions)
4. [Rule Implementation](#4-rule-implementation)
5. [Integration with Service Layer](#5-integration-with-service-layer)
6. [Testing Strategy](#6-testing-strategy)
7. [Rule Maintenance](#7-rule-maintenance)

---

## 1. Overview

### 1.1 Purpose

Business rules are externalized from application code using **Drools 9.44.0.Final** to enable:
- Non-technical users to modify rules (via decision tables in future)
- Rule versioning and audit trail
- Complex rule composition without code changes
- Centralized validation logic

### 1.2 Rule Categories

| Category | Purpose | Examples |
|----------|---------|----------|
| **Validation Rules** | Data integrity checks | Age range, mobile uniqueness |
| **Business Constraints** | Domain-specific policies | Class capacity limits |
| **Derived Values** | Calculated fields | Student ID generation pattern |
| **Workflow Rules** | State transitions | Active → Inactive status change rules |

### 1.3 Technology Stack

- **Rules Engine:** Drools 9.44.0.Final
- **Rule Format:** DRL (Drools Rule Language) files
- **Future Enhancement:** Excel-based decision tables
- **Integration:** Spring Boot with KieContainer bean

---

## 2. Drools Architecture

### 2.1 Component Diagram

```mermaid
graph TB
    subgraph "Application Layer"
        Service[StudentService<br/>@Service]
        ValidationService[DroolsValidationService<br/>@Service]
    end

    subgraph "Drools Engine"
        KieContainer[KieContainer<br/>Rule Repository]
        KieSession[KieSession<br/>Working Memory]
        Rules[DRL Files<br/>student-validation.drl]
    end

    subgraph "Domain Layer"
        Student[Student Entity]
        Facts[Validation Facts<br/>StudentValidationRequest]
        Results[Validation Results<br/>ValidationError]
    end

    Service --> ValidationService
    ValidationService --> KieSession
    KieSession --> Rules
    ValidationService --> Facts
    KieSession --> Results
    Facts --> Student
```

### 2.2 File Structure

```
src/main/resources/
└── rules/
    ├── student-validation.drl         # Student validation rules
    ├── enrollment-validation.drl      # Enrollment rules (future)
    └── configuration-validation.drl   # Configuration rules (future)
```

### 2.3 Drools Configuration (Spring Boot)

**DroolsConfig.java:**
```java
package com.school.student.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class DroolsConfig {

    @Bean
    public KieContainer kieContainer() throws Exception {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        // Load all .drl files from classpath
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:rules/**/*.drl");

        for (Resource resource : resources) {
            kieFileSystem.write("src/main/resources/" + resource.getFilename(),
                               kieServices.getResources().newInputStreamResource(resource.getInputStream()));
        }

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        KieModule kieModule = kieBuilder.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }
}
```

---

## 3. Business Rule Definitions

### 3.1 Student Registration Rules

**BR-1: Age Constraint**
- **Rule ID:** `BR-STU-001`
- **Description:** Student must be between 3 and 18 years old at registration
- **Source:** REQUIREMENTS.md Section 5 (Functional Requirements)
- **Severity:** ERROR
- **Drools Rule Name:** `student-age-range-check`

**BR-2: Mobile Uniqueness**
- **Rule ID:** `BR-STU-002`
- **Description:** Mobile number must be unique across all students
- **Source:** REQUIREMENTS.md Section 5 (Functional Requirements)
- **Severity:** ERROR
- **Drools Rule Name:** `student-mobile-unique-check`

**BR-3: Email Format Validation**
- **Rule ID:** `BR-STU-003`
- **Description:** Email must be in valid format (regex: `^[^@\s]+@[^@\s]+\.[^@\s]+$`)
- **Source:** Frontend Data Model (FRONTEND_DESIGN_SPECIFICATION.md)
- **Severity:** ERROR
- **Drools Rule Name:** `student-email-format-check`

**BR-4: Aadhaar Format Validation**
- **Rule ID:** `BR-STU-004`
- **Description:** Aadhaar number must be exactly 12 digits (if provided)
- **Source:** Frontend Data Model
- **Severity:** ERROR
- **Drools Rule Name:** `student-aadhaar-format-check`

**BR-5: Name Pattern Validation**
- **Rule ID:** `BR-STU-005`
- **Description:** First/Last name must contain only letters and spaces
- **Source:** Frontend Validation Rules
- **Severity:** ERROR
- **Drools Rule Name:** `student-name-pattern-check`

**BR-6: Student ID Generation**
- **Rule ID:** `BR-STU-006`
- **Description:** Auto-generate Student ID in format `STD-YYYYMMDD-NNNN`
- **Source:** REQUIREMENTS.md Section 4.1.1
- **Severity:** INFO (Derived value)
- **Implementation:** Service layer (not Drools - uses database sequence)

### 3.2 Student Update Rules

**BR-7: Editable Fields Constraint**
- **Rule ID:** `BR-STU-007`
- **Description:** Only firstName, lastName, mobile, status can be edited
- **Source:** REQUIREMENTS.md Section 5 (Validation Rules)
- **Severity:** ERROR
- **Implementation:** API contract enforcement + Drools validation

### 3.3 Enrollment Rules (Future)

**BR-8: Class Capacity Constraint**
- **Rule ID:** `BR-ENR-001`
- **Description:** Class enrollment cannot exceed configured capacity
- **Source:** REQUIREMENTS.md Section 5 (Business Rule BR-3)
- **Severity:** ERROR
- **Status:** Phase 2 (requires class capacity configuration)

---

## 4. Rule Implementation

### 4.1 Fact Objects

**StudentValidationRequest.java:**
```java
package com.school.student.domain.validation;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StudentValidationRequest {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String mobile;
    private String email;
    private String aadhaarNumber;

    // Metadata for uniqueness checks
    private String studentId;  // Null for new registrations
    private boolean checkMobileUniqueness;
}
```

**ValidationResult.java:**
```java
package com.school.student.domain.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationResult {
    private boolean valid = true;
    private List<ValidationError> errors = new ArrayList<>();

    public void addError(String field, String code, String message) {
        this.valid = false;
        this.errors.add(new ValidationError(field, code, message));
    }

    @Data
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String code;
        private String message;
    }
}
```

### 4.2 DRL File: student-validation.drl

```drl
package com.school.student.rules;

import com.school.student.domain.validation.StudentValidationRequest;
import com.school.student.domain.validation.ValidationResult;
import java.time.LocalDate;
import java.time.Period;

global ValidationResult validationResult;

// =====================================================
// Rule: BR-STU-001 - Age Range Check (3-18 years)
// =====================================================
rule "student-age-range-check"
    salience 100  // High priority
    when
        $request : StudentValidationRequest(
            dateOfBirth != null,
            calculateAge(dateOfBirth) < 3 || calculateAge(dateOfBirth) > 18
        )
    then
        validationResult.addError(
            "dateOfBirth",
            "AGE_OUT_OF_RANGE",
            "Student age must be between 3 and 18 years at registration"
        );
end

// =====================================================
// Rule: BR-STU-003 - Email Format Validation
// =====================================================
rule "student-email-format-check"
    salience 90
    when
        $request : StudentValidationRequest(
            email != null,
            !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
        )
    then
        validationResult.addError(
            "email",
            "INVALID_EMAIL_FORMAT",
            "Email must be in valid format (e.g., user@example.com)"
        );
end

// =====================================================
// Rule: BR-STU-004 - Aadhaar Format Validation
// =====================================================
rule "student-aadhaar-format-check"
    salience 90
    when
        $request : StudentValidationRequest(
            aadhaarNumber != null,
            !aadhaarNumber.matches("^\\d{12}$")
        )
    then
        validationResult.addError(
            "aadhaarNumber",
            "INVALID_AADHAAR_FORMAT",
            "Aadhaar number must be exactly 12 digits"
        );
end

// =====================================================
// Rule: BR-STU-005 - First Name Pattern Validation
// =====================================================
rule "student-firstname-pattern-check"
    salience 80
    when
        $request : StudentValidationRequest(
            firstName != null,
            !firstName.matches("^[a-zA-Z\\s]+$")
        )
    then
        validationResult.addError(
            "firstName",
            "INVALID_NAME_PATTERN",
            "First name must contain only letters and spaces"
        );
end

// =====================================================
// Rule: BR-STU-005 - Last Name Pattern Validation
// =====================================================
rule "student-lastname-pattern-check"
    salience 80
    when
        $request : StudentValidationRequest(
            lastName != null,
            !lastName.matches("^[a-zA-Z\\s]+$")
        )
    then
        validationResult.addError(
            "lastName",
            "INVALID_NAME_PATTERN",
            "Last name must contain only letters and spaces"
        );
end

// =====================================================
// Rule: BR-STU-002 - Mobile Format Validation
// =====================================================
rule "student-mobile-format-check"
    salience 85
    when
        $request : StudentValidationRequest(
            mobile != null,
            !mobile.matches("^\\d{10}$")
        )
    then
        validationResult.addError(
            "mobile",
            "INVALID_MOBILE_FORMAT",
            "Mobile number must be exactly 10 digits"
        );
end

// =====================================================
// Helper Function: Calculate Age
// =====================================================
function int calculateAge(LocalDate dateOfBirth) {
    if (dateOfBirth == null) {
        return 0;
    }
    LocalDate today = LocalDate.now();
    Period period = Period.between(dateOfBirth, today);
    return period.getYears();
}
```

### 4.3 Mobile Uniqueness Check (Database + Drools)

**Note:** Mobile uniqueness requires database query, so it's handled in service layer, not pure Drools.

**DroolsValidationService.java:**
```java
package com.school.student.service;

import com.school.student.domain.validation.StudentValidationRequest;
import com.school.student.domain.validation.ValidationResult;
import com.school.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DroolsValidationService {

    private final KieContainer kieContainer;
    private final StudentRepository studentRepository;

    public ValidationResult validateStudent(StudentValidationRequest request) {
        ValidationResult result = new ValidationResult();

        // Execute Drools rules
        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.setGlobal("validationResult", result);
            kieSession.insert(request);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }

        // BR-STU-002: Mobile uniqueness check (database)
        if (request.isCheckMobileUniqueness() && request.getMobile() != null) {
            boolean mobileExists = studentRepository.existsByMobileAndStudentIdNot(
                request.getMobile(),
                request.getStudentId() != null ? request.getStudentId() : ""
            );

            if (mobileExists) {
                result.addError(
                    "mobile",
                    "MOBILE_ALREADY_EXISTS",
                    "Mobile number is already registered to another student"
                );
            }
        }

        log.debug("Validation result: valid={}, errors={}", result.isValid(), result.getErrors());
        return result;
    }
}
```

---

## 5. Integration with Service Layer

### 5.1 Service Layer Usage

**StudentService.java:**
```java
package com.school.student.service;

import com.school.student.domain.Student;
import com.school.student.domain.validation.StudentValidationRequest;
import com.school.student.domain.validation.ValidationResult;
import com.school.student.dto.StudentRequest;
import com.school.student.dto.StudentResponse;
import com.school.student.exception.ValidationException;
import com.school.student.mapper.StudentMapper;
import com.school.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository repository;
    private final StudentMapper mapper;
    private final DroolsValidationService validationService;

    public StudentResponse registerStudent(StudentRequest request) {
        log.info("Registering new student: {}", request.getFirstName());

        // Step 1: Build validation request
        StudentValidationRequest validationRequest = StudentValidationRequest.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .dateOfBirth(request.getDateOfBirth())
            .mobile(request.getMobile())
            .email(request.getEmail())
            .aadhaarNumber(request.getAadhaarNumber())
            .checkMobileUniqueness(true)
            .build();

        // Step 2: Execute Drools validation
        ValidationResult validationResult = validationService.validateStudent(validationRequest);

        // Step 3: Throw exception if validation fails
        if (!validationResult.isValid()) {
            log.warn("Validation failed for student registration: {}", validationResult.getErrors());
            throw new ValidationException("Student validation failed", validationResult.getErrors());
        }

        // Step 4: Map DTO to Entity
        Student student = mapper.toEntity(request);

        // Step 5: Generate Student ID (business logic)
        student.generateStudentId();  // Format: STD-20260122-NNNN

        // Step 6: Save to database
        Student saved = repository.save(student);
        log.info("Student registered successfully with ID: {}", saved.getStudentId());

        // Step 7: Map Entity to Response DTO
        return mapper.toResponse(saved);
    }

    public StudentResponse updateStudent(String studentId, StudentUpdateRequest request) {
        // Similar validation flow for updates
        // Only validate editable fields (firstName, lastName, mobile, status)
    }
}
```

### 5.2 Exception Handling

**ValidationException.java:**
```java
package com.school.student.exception;

import com.school.student.domain.validation.ValidationResult;
import lombok.Getter;
import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private final List<ValidationResult.ValidationError> errors;

    public ValidationException(String message, List<ValidationResult.ValidationError> errors) {
        super(message);
        this.errors = errors;
    }
}
```

**GlobalExceptionHandler.java:**
```java
@ExceptionHandler(ValidationException.class)
public ResponseEntity<ProblemDetail> handleValidationException(ValidationException ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        "Validation failed"
    );

    problemDetail.setTitle("Business Rule Violation");
    problemDetail.setProperty("errors", ex.getErrors());

    return ResponseEntity.badRequest().body(problemDetail);
}
```

---

## 6. Testing Strategy

### 6.1 Rule Unit Testing

**StudentValidationRulesTest.java:**
```java
package com.school.student.rules;

import com.school.student.config.DroolsConfig;
import com.school.student.domain.validation.StudentValidationRequest;
import com.school.student.domain.validation.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = DroolsConfig.class)
class StudentValidationRulesTest {

    @Autowired
    private KieContainer kieContainer;

    private ValidationResult validationResult;

    @BeforeEach
    void setUp() {
        validationResult = new ValidationResult();
    }

    @Test
    @DisplayName("BR-STU-001: Should reject student below 3 years")
    void shouldRejectStudentBelowMinAge() {
        // Arrange
        StudentValidationRequest request = StudentValidationRequest.builder()
            .firstName("TestChild")
            .lastName("Sharma")
            .dateOfBirth(LocalDate.now().minusYears(2))  // 2 years old
            .mobile("9876543210")
            .email("test@example.com")
            .build();

        // Act
        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.setGlobal("validationResult", validationResult);
            kieSession.insert(request);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }

        // Assert
        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getErrors()).hasSize(1);
        assertThat(validationResult.getErrors().get(0).getCode()).isEqualTo("AGE_OUT_OF_RANGE");
        assertThat(validationResult.getErrors().get(0).getField()).isEqualTo("dateOfBirth");
    }

    @Test
    @DisplayName("BR-STU-001: Should reject student above 18 years")
    void shouldRejectStudentAboveMaxAge() {
        StudentValidationRequest request = StudentValidationRequest.builder()
            .dateOfBirth(LocalDate.now().minusYears(19))
            .build();

        KieSession kieSession = kieContainer.newKieSession();
        kieSession.setGlobal("validationResult", validationResult);
        kieSession.insert(request);
        kieSession.fireAllRules();
        kieSession.dispose();

        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getErrors().get(0).getCode()).isEqualTo("AGE_OUT_OF_RANGE");
    }

    @Test
    @DisplayName("BR-STU-003: Should reject invalid email format")
    void shouldRejectInvalidEmailFormat() {
        StudentValidationRequest request = StudentValidationRequest.builder()
            .email("invalid-email")  // Missing @ and domain
            .build();

        KieSession kieSession = kieContainer.newKieSession();
        kieSession.setGlobal("validationResult", validationResult);
        kieSession.insert(request);
        kieSession.fireAllRules();
        kieSession.dispose();

        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getErrors())
            .anyMatch(e -> e.getCode().equals("INVALID_EMAIL_FORMAT"));
    }

    @Test
    @DisplayName("BR-STU-004: Should reject invalid Aadhaar format")
    void shouldRejectInvalidAadhaarFormat() {
        StudentValidationRequest request = StudentValidationRequest.builder()
            .aadhaarNumber("12345")  // Only 5 digits instead of 12
            .build();

        KieSession kieSession = kieContainer.newKieSession();
        kieSession.setGlobal("validationResult", validationResult);
        kieSession.insert(request);
        kieSession.fireAllRules();
        kieSession.dispose();

        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getErrors())
            .anyMatch(e -> e.getCode().equals("INVALID_AADHAAR_FORMAT"));
    }

    @Test
    @DisplayName("Should pass validation for valid student data")
    void shouldPassValidationForValidData() {
        StudentValidationRequest request = StudentValidationRequest.builder()
            .firstName("Aarav")
            .lastName("Sharma")
            .dateOfBirth(LocalDate.now().minusYears(10))  // 10 years old
            .mobile("9876543210")
            .email("aarav.sharma@example.com")
            .aadhaarNumber("123456789012")
            .build();

        KieSession kieSession = kieContainer.newKieSession();
        kieSession.setGlobal("validationResult", validationResult);
        kieSession.insert(request);
        kieSession.fireAllRules();
        kieSession.dispose();

        assertThat(validationResult.isValid()).isTrue();
        assertThat(validationResult.getErrors()).isEmpty();
    }
}
```

### 6.2 Integration Testing with Service Layer

**StudentServiceIntegrationTest.java:**
```java
@Test
@DisplayName("Should reject registration with duplicate mobile")
void shouldRejectDuplicateMobile() {
    // Pre-condition: Save student with mobile 9876543210
    saveStudentWithMobile("9876543210");

    // Attempt to register another student with same mobile
    StudentRequest request = StudentRequest.builder()
        .firstName("NewStudent")
        .lastName("Test")
        .mobile("9876543210")  // Duplicate
        .build();

    // Assert
    assertThatThrownBy(() -> studentService.registerStudent(request))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Mobile number is already registered");
}
```

---

## 7. Rule Maintenance

### 7.1 Rule Versioning

**Strategy:**
- Store `.drl` files in version control (Git)
- Tag releases with rule version (e.g., `v1.0-rules`)
- Document rule changes in `CHANGELOG-RULES.md`

**Example Changelog:**
```markdown
## [1.1.0] - 2026-02-15
### Added
- BR-ENR-001: Class capacity constraint rule

### Modified
- BR-STU-001: Changed age range from 5-18 to 3-18

### Deprecated
- None
```

### 7.2 Rule Audit Trail

**Future Enhancement:** Store rule execution history in database.

```sql
CREATE TABLE rule_execution_log (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50),
    entity_id VARCHAR(50),
    rule_name VARCHAR(100),
    rule_version VARCHAR(20),
    execution_status VARCHAR(20),  -- SUCCESS/FAILED
    error_message TEXT,
    executed_at TIMESTAMPTZ DEFAULT NOW()
);
```

### 7.3 Decision Tables (Future)

**Phase 2 Enhancement:** Convert DRL to Excel-based decision tables for non-technical users.

**Example: Age Validation Decision Table (Excel format)**

| Condition | Condition | Action | Action |
|-----------|-----------|--------|--------|
| Date of Birth < Current Date - 3 years | Date of Birth > Current Date - 18 years | Error Code | Error Message |
| TRUE | FALSE | AGE_TOO_LOW | Student must be at least 3 years old |
| FALSE | TRUE | AGE_TOO_HIGH | Student must be at most 18 years old |

**Load Decision Table:**
```java
Resource dt = ResourceFactory.newClassPathResource("rules/StudentAgeValidation.xls");
kieFileSystem.write(dt);
```

### 7.4 Rule Performance Monitoring

**Metrics to Track:**
- Rule execution time (target: <50ms per validation)
- Rule hit frequency (which rules fire most often)
- Failed validation distribution (most common errors)

**Micrometer Integration:**
```java
@Service
public class DroolsValidationService {
    private final MeterRegistry meterRegistry;

    public ValidationResult validateStudent(StudentValidationRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            // ... rule execution
        } finally {
            sample.stop(meterRegistry.timer("drools.validation.time", "entity", "student"));
        }
    }
}
```

---

## Appendix

### A. Business Rule Traceability Matrix

| Rule ID | Rule Name | Source | DRL File | Status |
|---------|-----------|--------|----------|--------|
| BR-STU-001 | Age Range Check | REQUIREMENTS.md Sec 5 | student-validation.drl | Implemented |
| BR-STU-002 | Mobile Uniqueness | REQUIREMENTS.md Sec 5 | DroolsValidationService.java | Implemented |
| BR-STU-003 | Email Format | FRONTEND_DESIGN_SPEC.md | student-validation.drl | Implemented |
| BR-STU-004 | Aadhaar Format | FRONTEND_DESIGN_SPEC.md | student-validation.drl | Implemented |
| BR-STU-005 | Name Pattern | FRONTEND_DESIGN_SPEC.md | student-validation.drl | Implemented |
| BR-STU-006 | Student ID Generation | REQUIREMENTS.md Sec 4.1.1 | Student.java (service layer) | Implemented |
| BR-STU-007 | Editable Fields | REQUIREMENTS.md Sec 5 | API Contract | Implemented |
| BR-ENR-001 | Class Capacity | REQUIREMENTS.md Sec 5 | enrollment-validation.drl | Phase 2 |

### B. Sample Rule Execution Flow

```
1. Frontend submits StudentRequest
      ↓
2. StudentService.registerStudent(request)
      ↓
3. Build StudentValidationRequest from request
      ↓
4. DroolsValidationService.validateStudent(validationRequest)
      ↓
5. Create KieSession from KieContainer
      ↓
6. Set global ValidationResult
      ↓
7. Insert StudentValidationRequest as fact
      ↓
8. fireAllRules()
      ↓
9. Rules evaluate conditions and add errors to ValidationResult
      ↓
10. Database uniqueness checks (mobile)
      ↓
11. Return ValidationResult
      ↓
12. If valid → proceed to save
    If invalid → throw ValidationException
      ↓
13. GlobalExceptionHandler returns RFC 7807 error response
```

### C. Cross-References

- **System Architecture:** See `01-system-architecture.md` (Section 9.1 - Design Patterns)
- **Database Design:** See `02-database-design.md` (Section 5.2 - CHECK constraints)
- **Backend Implementation:** See `05-backend-implementation-guide.md` (Service Layer patterns)

---

**Document Control:**
**Created:** January 22, 2026
**Last Updated:** January 22, 2026
**Approved By:** Software Architect Agent
**Next Review:** After Drools Rule Testing Complete
