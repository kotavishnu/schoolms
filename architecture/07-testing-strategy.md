# Testing Strategy
**School Management System - Phase 1**

**Version**: 1.0
**Date**: January 8, 2026
**Status**: Active

---

## Table of Contents

1. [Overview](#overview)
2. [Testing Pyramid](#testing-pyramid)
3. [Backend Testing](#backend-testing)
4. [Frontend Testing](#frontend-testing)
5. [Integration Testing](#integration-testing)
6. [End-to-End Testing](#end-to-end-testing)
7. [Test Scenarios](#test-scenarios)
8. [Quality Gates](#quality-gates)
9. [CI/CD Integration](#cicd-integration)

---

## Overview

### Testing Philosophy

**Shift-Left Testing**: Catch defects early in the development cycle

**Test Pyramid**:
- **60% Unit Tests**: Fast, isolated, focused
- **30% Integration Tests**: API contracts, database interactions
- **10% E2E Tests**: Critical user journeys

### Quality Goals

| Metric | Target | Measurement |
|--------|--------|-------------|
| **Code Coverage** | >80% | JaCoCo (Backend), Istanbul/Vitest (Frontend) |
| **Test Execution Time** | <5 min (unit), <15 min (all) | CI/CD pipeline |
| **Defect Escape Rate** | <5% | Production bugs / Total tests |
| **API Response Time** | <200ms (95th percentile) | Load testing |

---

## Testing Pyramid

```
       /\
      /  \      E2E Tests (10%)
     /____\     - Critical user flows
    /      \    - Browser automation
   /________\
  /          \  Integration Tests (30%)
 /____________\ - API contracts
/              \ - Database tests
/______________\ Unit Tests (60%)
                - Business logic
                - Domain models
```

---

## Backend Testing

### Technology Stack

- **Framework**: JUnit 5 (Jupiter)
- **Mocking**: Mockito
- **Assertions**: AssertJ
- **Test Containers**: TestContainers (PostgreSQL, Redis)
- **API Testing**: RestAssured
- **Coverage**: JaCoCo

---

### 1. Unit Tests (Domain Layer)

**StudentTest.java**:

```java
package com.schoolms.student.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Student Domain Model Tests")
class StudentTest {

    @Test
    @DisplayName("Should create student with valid data")
    void createStudent_ShouldSucceed_WhenValidData() {
        // Given
        LocalDate dob = LocalDate.now().minusYears(10);

        // When
        Student student = Student.register(
            "John",
            "Doe",
            dob,
            "123456789012",
            "9876543210",
            "john.doe@example.com",
            "123 Main St",
            "Robert Doe",
            "Jane Doe",
            "Mole on left cheek"
        );

        // Then
        assertThat(student).isNotNull();
        assertThat(student.getFirstName()).isEqualTo("John");
        assertThat(student.getAge()).isEqualTo(10);
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 19, 25})
    @DisplayName("Should throw exception when age is outside range")
    void createStudent_ShouldThrowException_WhenAgeOutOfRange(int yearsOld) {
        // Given
        LocalDate dob = LocalDate.now().minusYears(yearsOld);

        // When & Then
        assertThatThrownBy(() ->
            Student.register("John", "Doe", dob, "123456789012", "9876543210",
                "john@example.com", "123 Main St", "Robert", "Jane", null)
        )
        .isInstanceOf(InvalidAgeException.class)
        .hasMessageContaining("outside allowed range");
    }

    @Test
    @DisplayName("Should calculate age correctly")
    void getAge_ShouldCalculateCorrectly() {
        // Given
        LocalDate dob = LocalDate.of(2014, 5, 15);
        Student student = Student.register("John", "Doe", dob, "123456789012",
            "9876543210", "john@example.com", "123 Main St", "Robert", "Jane", null);

        // When
        int age = student.getAge();

        // Then
        assertThat(age).isBetween(9, 12); // Age depends on current date
    }

    @Test
    @DisplayName("Should update profile successfully")
    void updateProfile_ShouldSucceed_WhenValidData() {
        // Given
        Student student = createValidStudent();
        String newFirstName = "Jonathan";
        String newPhone = "8888777766";

        // When
        student.updateProfile(newFirstName, null, newPhone);

        // Then
        assertThat(student.getFirstName()).isEqualTo(newFirstName);
        assertThat(student.getPhone()).isEqualTo(newPhone);
    }

    @Test
    @DisplayName("Should activate student")
    void activate_ShouldChangeStatusToActive() {
        // Given
        Student student = createValidStudent();
        student.deactivate();

        // When
        student.activate();

        // Then
        assertThat(student.getStatus()).isEqualTo(StudentStatus.ACTIVE);
    }

    private Student createValidStudent() {
        return Student.register(
            "John", "Doe", LocalDate.now().minusYears(10),
            "123456789012", "9876543210", "john@example.com",
            "123 Main St", "Robert", "Jane", null
        );
    }
}
```

---

### 2. Unit Tests (Application Layer)

**StudentApplicationServiceTest.java**:

```java
package com.schoolms.student.application.service;

import com.schoolms.student.domain.model.Student;
import com.schoolms.student.domain.model.StudentId;
import com.schoolms.student.domain.repository.StudentRepository;
import com.schoolms.student.domain.exception.DuplicatePhoneException;
import com.schoolms.student.domain.exception.StudentNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StudentApplicationServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private RedisCacheManager cacheManager;

    @InjectMocks
    private StudentApplicationService service;

    @Test
    void createStudent_ShouldSucceed_WhenValidRequest() {
        // Given
        CreateStudentRequest request = createValidRequest();
        Student student = createValidStudent();
        StudentResponse response = createValidResponse();

        when(studentRepository.existsByPhone(anyString())).thenReturn(false);
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByAdhaarNumber(anyString())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toResponse(any(Student.class))).thenReturn(response);

        // When
        StudentResponse result = service.createStudent(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("STU-2026-00001");
        verify(studentRepository).save(any(Student.class));
        verify(cacheManager).evictAllStudentCaches();
    }

    @Test
    void createStudent_ShouldThrowException_WhenPhoneDuplicate() {
        // Given
        CreateStudentRequest request = createValidRequest();
        when(studentRepository.existsByPhone(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> service.createStudent(request))
            .isInstanceOf(DuplicatePhoneException.class)
            .hasMessageContaining("already registered");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void updateStudent_ShouldSucceed_WhenValidRequest() {
        // Given
        String studentId = "STU-2026-00001";
        UpdateStudentRequest request = new UpdateStudentRequest("John", "Doe", "9999888877", null);
        Student student = createValidStudent();

        when(studentRepository.findById(any(StudentId.class))).thenReturn(Optional.of(student));
        when(studentRepository.existsByPhone(anyString())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toResponse(any(Student.class))).thenReturn(createValidResponse());

        // When
        StudentResponse result = service.updateStudent(studentId, request);

        // Then
        assertThat(result).isNotNull();
        verify(studentRepository).save(any(Student.class));
        verify(cacheManager).evictStudent(studentId);
    }

    @Test
    void deleteStudent_ShouldSucceed_WhenStudentExists() {
        // Given
        String studentId = "STU-2026-00001";
        Student student = createValidStudent();

        when(studentRepository.findById(any(StudentId.class))).thenReturn(Optional.of(student));

        // When
        service.deleteStudent(studentId);

        // Then
        verify(studentRepository).delete(any(StudentId.class));
        verify(cacheManager).evictStudent(studentId);
    }

    @Test
    void getStudent_ShouldReturnFromCache_WhenCached() {
        // Given
        String studentId = "STU-2026-00001";
        StudentResponse cachedResponse = createValidResponse();

        when(cacheManager.getStudent(studentId)).thenReturn(cachedResponse);

        // When
        StudentResponse result = service.getStudent(studentId);

        // Then
        assertThat(result).isEqualTo(cachedResponse);
        verify(studentRepository, never()).findById(any());
    }

    @Test
    void getStudent_ShouldThrowException_WhenNotFound() {
        // Given
        String studentId = "STU-2026-99999";

        when(cacheManager.getStudent(studentId)).thenReturn(null);
        when(studentRepository.findById(any(StudentId.class))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.getStudent(studentId))
            .isInstanceOf(StudentNotFoundException.class)
            .hasMessageContaining("not found");
    }

    // Helper methods
    private CreateStudentRequest createValidRequest() {
        return new CreateStudentRequest(/* ... */);
    }

    private Student createValidStudent() {
        return Student.register(/* ... */);
    }

    private StudentResponse createValidResponse() {
        return new StudentResponse(/* ... */);
    }
}
```

---

### 3. Integration Tests (Repository Layer)

**StudentRepositoryIntegrationTest.java**:

```java
package com.schoolms.student.infrastructure.persistence.repository;

import com.schoolms.student.infrastructure.persistence.entity.StudentJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudentRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private StudentJpaRepository repository;

    @Test
    void save_ShouldPersistStudent() {
        // Given
        StudentJpaEntity student = createValidStudentEntity();

        // When
        StudentJpaEntity saved = repository.save(student);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStudentId()).isNotNull();
        assertThat(saved.getStudentId()).matches("STU-\\d{4}-\\d{5}");
    }

    @Test
    void findByStudentId_ShouldReturnStudent_WhenExists() {
        // Given
        StudentJpaEntity student = repository.save(createValidStudentEntity());

        // When
        Optional<StudentJpaEntity> found = repository.findByStudentId(student.getStudentId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo(student.getFirstName());
    }

    @Test
    void existsByPhone_ShouldReturnTrue_WhenPhoneExists() {
        // Given
        StudentJpaEntity student = repository.save(createValidStudentEntity());

        // When
        boolean exists = repository.existsByPhone(student.getPhone());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void search_ShouldFindStudents_WhenMatchingQuery() {
        // Given
        StudentJpaEntity student = createValidStudentEntity();
        student.setFirstName("Alexander");
        student.setLastName("Johnson");
        repository.save(student);

        // When
        var results = repository.search("Alexander");

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getFirstName()).isEqualTo("Alexander");
    }

    private StudentJpaEntity createValidStudentEntity() {
        StudentJpaEntity entity = new StudentJpaEntity();
        entity.setFirstName("John");
        entity.setLastName("Doe");
        entity.setDateOfBirth(LocalDate.now().minusYears(10));
        entity.setAdhaarNumber("123456789012");
        entity.setPhone("9876543210");
        entity.setEmail("john.doe@example.com");
        entity.setAddress("123 Main Street");
        entity.setGuardianName("Robert Doe");
        entity.setMotherName("Jane Doe");
        entity.setStatus(StudentStatus.ACTIVE);
        return entity;
    }
}
```

---

### 4. API Integration Tests

**StudentControllerIntegrationTest.java**:

```java
package com.schoolms.student.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolms.student.presentation.dto.request.CreateStudentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createStudent_ShouldReturn201_WhenValidRequest() throws Exception {
        // Given
        CreateStudentRequest request = createValidRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createStudent_ShouldReturn400_WhenInvalidRequest() throws Exception {
        // Given
        CreateStudentRequest request = createInvalidRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("https://api.schoolms.com/problems/validation-error"))
            .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void getStudent_ShouldReturn200_WhenStudentExists() throws Exception {
        // Given
        String studentId = "STU-2026-00001";

        // When & Then
        mockMvc.perform(get("/api/v1/students/{id}", studentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(studentId));
    }

    @Test
    void getStudent_ShouldReturn404_WhenStudentNotFound() throws Exception {
        // Given
        String studentId = "STU-2026-99999";

        // When & Then
        mockMvc.perform(get("/api/v1/students/{id}", studentId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.type").value("https://api.schoolms.com/problems/not-found"));
    }

    private CreateStudentRequest createValidRequest() {
        return new CreateStudentRequest(/* ... */);
    }

    private CreateStudentRequest createInvalidRequest() {
        return new CreateStudentRequest(/* invalid data */);
    }
}
```

---

## Frontend Testing

### Technology Stack

- **Framework**: Vitest
- **Testing Library**: React Testing Library
- **Mocking**: MSW (Mock Service Worker)
- **E2E**: Playwright
- **Coverage**: Istanbul/c8

---

### 1. Unit Tests (Service Layer)

**studentService.test.ts**:

```typescript
// src/services/__tests__/studentService.test.ts
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { studentService } from '../studentService';
import apiClient from '../api';

vi.mock('../api');

describe('studentService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch all students', async () => {
    const mockResponse = {
      data: {
        students: [
          {
            id: 'STU-2026-00001',
            firstName: 'John',
            lastName: 'Doe',
            phone: '9876543210',
            status: 'ACTIVE',
          },
        ],
        totalCount: 1,
        activeCount: 1,
        inactiveCount: 0,
      },
    };

    vi.mocked(apiClient.get).mockResolvedValue(mockResponse);

    const result = await studentService.getAll();

    expect(apiClient.get).toHaveBeenCalledWith('/api/v1/students', { params: undefined });
    expect(result.students).toHaveLength(1);
    expect(result.totalCount).toBe(1);
  });

  it('should create student', async () => {
    const newStudent = {
      firstName: 'Alice',
      lastName: 'Smith',
      dateOfBirth: '2015-05-15',
      adhaarNumber: '123456789012',
      phone: '8888777766',
      email: 'alice@example.com',
      address: '123 Main St',
      guardianName: 'Robert',
      motherName: 'Jane',
      identificationMarks: null,
    };

    const mockResponse = {
      data: {
        id: 'STU-2026-00002',
        ...newStudent,
        age: 10,
        status: 'ACTIVE',
        createdAt: '2026-01-08T10:00:00Z',
        updatedAt: '2026-01-08T10:00:00Z',
      },
    };

    vi.mocked(apiClient.post).mockResolvedValue(mockResponse);

    const result = await studentService.create(newStudent);

    expect(apiClient.post).toHaveBeenCalledWith('/api/v1/students', newStudent);
    expect(result.id).toBe('STU-2026-00002');
  });

  it('should handle API errors', async () => {
    const error = {
      response: {
        status: 409,
        data: {
          type: 'https://api.schoolms.com/problems/duplicate-resource',
          detail: 'Phone number is already registered',
        },
      },
    };

    vi.mocked(apiClient.post).mockRejectedValue(error);

    await expect(studentService.create({} as any)).rejects.toEqual(error);
  });
});
```

---

### 2. Component Tests

**StudentCard.test.tsx**:

```typescript
// src/components/students/__tests__/StudentCard.test.tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import StudentCard from '../StudentCard';
import type { Student } from '@/types/student';

describe('StudentCard', () => {
  const mockStudent: Student = {
    id: 'STU-2026-00001',
    firstName: 'John',
    lastName: 'Doe',
    dateOfBirth: '2015-05-15',
    age: 10,
    adhaarNumber: '123456789012',
    address: '123 Main St',
    identificationMarks: 'Mole on left cheek',
    guardianName: 'Robert Doe',
    motherName: 'Jane Doe',
    phone: '9876543210',
    email: 'john.doe@example.com',
    status: 'ACTIVE',
    createdAt: '2026-01-08T10:00:00Z',
    updatedAt: '2026-01-08T10:00:00Z',
  };

  it('should render student information', () => {
    const onEdit = vi.fn();
    const onDelete = vi.fn();

    render(<StudentCard student={mockStudent} onEdit={onEdit} onDelete={onDelete} />);

    expect(screen.getByText('STU-2026-00001')).toBeInTheDocument();
    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('Robert Doe')).toBeInTheDocument();
    expect(screen.getByText('9876543210')).toBeInTheDocument();
    expect(screen.getByText('ACTIVE')).toBeInTheDocument();
  });

  it('should call onEdit when edit button clicked', () => {
    const onEdit = vi.fn();
    const onDelete = vi.fn();

    render(<StudentCard student={mockStudent} onEdit={onEdit} onDelete={onDelete} />);

    const editButton = screen.getByText(/Edit/i);
    fireEvent.click(editButton);

    expect(onEdit).toHaveBeenCalledWith(mockStudent);
  });

  it('should call onDelete when delete button clicked', () => {
    const onEdit = vi.fn();
    const onDelete = vi.fn();

    render(<StudentCard student={mockStudent} onEdit={onEdit} onDelete={onDelete} />);

    const deleteButton = screen.getByText(/Delete/i);
    fireEvent.click(deleteButton);

    expect(onDelete).toHaveBeenCalledWith('STU-2026-00001');
  });
});
```

---

## Integration Testing

### Backend: TestContainers

**BaseIntegrationTest.java**:

```java
@SpringBootTest
@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }
}
```

---

### Frontend: MSW (Mock Service Worker)

**mocks/handlers.ts**:

```typescript
// src/mocks/handlers.ts
import { http, HttpResponse } from 'msw';

export const handlers = [
  http.get('/api/v1/students', () => {
    return HttpResponse.json({
      students: [
        {
          id: 'STU-2026-00001',
          firstName: 'John',
          lastName: 'Doe',
          status: 'ACTIVE',
        },
      ],
      totalCount: 1,
      activeCount: 1,
      inactiveCount: 0,
    });
  }),

  http.post('/api/v1/students', async ({ request }) => {
    const body = await request.json();
    return HttpResponse.json(
      {
        id: 'STU-2026-00002',
        ...body,
        age: 10,
        status: 'ACTIVE',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      },
      { status: 201 }
    );
  }),

  http.get('/api/v1/students/:id', ({ params }) => {
    const { id } = params;

    if (id === 'STU-2026-99999') {
      return HttpResponse.json(
        {
          type: 'https://api.schoolms.com/problems/not-found',
          detail: `Student with ID '${id}' not found`,
        },
        { status: 404 }
      );
    }

    return HttpResponse.json({
      id,
      firstName: 'John',
      lastName: 'Doe',
      status: 'ACTIVE',
    });
  }),
];
```

---

## End-to-End Testing

### Playwright Configuration

**playwright.config.ts**:

```typescript
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  use: {
    baseURL: 'http://localhost:5173',
    trace: 'on-first-retry',
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
  ],

  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:5173',
    reuseExistingServer: !process.env.CI,
  },
});
```

---

### E2E Test Examples

**student-registration.spec.ts**:

```typescript
// e2e/student-registration.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Student Registration Flow', () => {
  test('should register a new student successfully', async ({ page }) => {
    // Navigate to students page
    await page.goto('/students');

    // Click "Register New Student" button
    await page.click('text=Register New Student');

    // Fill in form
    await page.fill('input[name="firstName"]', 'Alice');
    await page.fill('input[name="lastName"]', 'Johnson');
    await page.fill('input[type="date"]', '2015-05-15');
    await page.fill('input[name="adhaarNumber"]', '123456789012');
    await page.fill('textarea[name="address"]', '123 Main Street, City, State');
    await page.fill('input[name="guardianName"]', 'Robert Johnson');
    await page.fill('input[name="motherName"]', 'Emma Johnson');
    await page.fill('input[name="phone"]', '9876543210');
    await page.fill('input[name="email"]', 'alice.johnson@example.com');

    // Submit form
    await page.click('button[type="submit"]');

    // Verify success toast
    await expect(page.locator('text=Student registered successfully')).toBeVisible();

    // Verify student appears in list
    await expect(page.locator('text=Alice Johnson')).toBeVisible();
  });

  test('should show validation errors for invalid data', async ({ page }) => {
    await page.goto('/students');
    await page.click('text=Register New Student');

    // Fill invalid phone
    await page.fill('input[name="phone"]', '123'); // Invalid: not 10 digits

    // Try to submit
    await page.click('button[type="submit"]');

    // Verify error message
    await expect(page.locator('text=Phone number must be exactly 10 digits')).toBeVisible();
  });

  test('should prevent duplicate phone number', async ({ page }) => {
    await page.goto('/students');
    await page.click('text=Register New Student');

    // Fill form with existing phone
    await page.fill('input[name="firstName"]', 'Bob');
    await page.fill('input[name="lastName"]', 'Smith');
    await page.fill('input[name="phone"]', '9876543210'); // Duplicate

    // Submit
    await page.click('button[type="submit"]');

    // Verify error
    await expect(page.locator('text=Phone number is already registered')).toBeVisible();
  });
});

test.describe('Student Search', () => {
  test('should search students by name', async ({ page }) => {
    await page.goto('/students');

    // Enter search query
    await page.fill('input[placeholder*="Search"]', 'John');

    // Wait for search results
    await page.waitForTimeout(500); // Debounce delay

    // Verify results
    await expect(page.locator('text=John Doe')).toBeVisible();
  });

  test('should filter students by status', async ({ page }) => {
    await page.goto('/students');

    // Select INACTIVE filter
    await page.selectOption('select[name="status"]', 'INACTIVE');

    // Verify only inactive students shown
    await expect(page.locator('text=INACTIVE')).toBeVisible();
    await expect(page.locator('text=ACTIVE')).not.toBeVisible();
  });
});
```

---

## Test Scenarios

### Critical Path: Student Registration

**Test Case**: Register New Student

**Prerequisites**: User is on Students page

**Steps**:
1. Click "Register New Student" button
2. Fill in all required fields:
   - First Name: "Alice"
   - Last Name: "Johnson"
   - Date of Birth: "2015-05-15" (Age: 10)
   - Adhaar: "123456789012"
   - Phone: "9876543210"
   - Email: "alice.johnson@example.com"
   - Address: "123 Main Street"
   - Guardian: "Robert Johnson"
   - Mother: "Emma Johnson"
3. Click "Register Student"

**Expected Result**:
- HTTP 201 response
- Student ID auto-generated (e.g., "STU-2026-00001")
- Success toast: "Student registered successfully"
- Student appears in list with status ACTIVE
- Database record created
- Cache evicted

**Edge Cases**:
- Age 2 years (invalid) → 422 error
- Age 19 years (invalid) → 422 error
- Duplicate phone → 409 error
- Invalid email format → 400 error
- Missing required field → 400 error

---

### Critical Path: Student Search

**Test Case**: Search Student by Guardian Name

**Prerequisites**: At least 5 students in database

**Steps**:
1. Navigate to Students page
2. Enter "Johnson" in search box
3. Wait for debounce (300ms)

**Expected Result**:
- API call: `GET /api/v1/students?search=Johnson`
- Results filtered to students with guardian name containing "Johnson"
- Results displayed in grid
- No results → "No students found" message

---

## Quality Gates

### Definition of Done (DoD)

A feature is considered DONE when:

- [ ] All acceptance criteria met
- [ ] Unit tests written (>80% coverage)
- [ ] Integration tests written for API endpoints
- [ ] E2E test for critical path
- [ ] Code reviewed and approved
- [ ] No critical bugs
- [ ] Documentation updated
- [ ] Performance benchmarks met (<200ms API response)

---

### CI/CD Quality Gates

**Build Stage**:
- [ ] Compile succeeds (zero errors)
- [ ] Linting passes (zero critical issues)

**Test Stage**:
- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] Code coverage >80%

**Quality Stage**:
- [ ] SonarQube quality gate passes
- [ ] No critical security vulnerabilities
- [ ] Performance tests pass

**Deploy Stage**:
- [ ] All stages green
- [ ] Manual approval (production only)

---

## CI/CD Integration

### GitHub Actions Workflow

**.github/workflows/ci.yml**:

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  backend-tests:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:18
        env:
          POSTGRES_DB: testdb
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        ports:
          - 5432:5432
      redis:
        image: redis:7-alpine
        ports:
          - 6379:6379

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Run Unit Tests
        run: mvn test

      - name: Run Integration Tests
        run: mvn verify -Pintegration-tests

      - name: Generate Coverage Report
        run: mvn jacoco:report

      - name: Upload Coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          file: ./target/site/jacoco/jacoco.xml

  frontend-tests:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '20'

      - name: Install dependencies
        run: npm ci

      - name: Run Unit Tests
        run: npm run test:unit

      - name: Run E2E Tests
        run: npm run test:e2e

      - name: Upload Coverage
        uses: codecov/codecov-action@v3
        with:
          file: ./coverage/lcov.info

  sonarqube:
    runs-on: ubuntu-latest
    needs: [backend-tests, frontend-tests]

    steps:
      - uses: actions/checkout@v3

      - name: SonarQube Scan
        uses: sonarsource/sonarqube-scan-action@master
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
```

---

## Summary

This testing strategy provides:

1. **Comprehensive Coverage**: Unit (60%), Integration (30%), E2E (10%)
2. **Test Pyramid**: Fast feedback from unit tests, confidence from E2E
3. **Backend Testing**: JUnit 5, Mockito, TestContainers, RestAssured
4. **Frontend Testing**: Vitest, React Testing Library, MSW, Playwright
5. **Quality Gates**: Clear DoD criteria, CI/CD gates
6. **Test Scenarios**: Detailed student registration and search scenarios
7. **Automation**: GitHub Actions for continuous testing

**Key Principles**:
- Write tests first (TDD where possible)
- Test behavior, not implementation
- Keep tests fast and isolated
- Mock external dependencies
- Use realistic test data
- Run tests in CI/CD pipeline

---

**Testing Complete**: All 7 architectural documents delivered.
