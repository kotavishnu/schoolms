# Testing Strategy

## 1. Overview

This document defines the comprehensive testing strategy for the School Management System (SMS). It establishes quality gates, testing pyramid, tools, and specific test scenarios to ensure production-ready software.

## 2. Testing Pyramid

```
        /\
       /  \
      / E2E\     10% - End-to-End Tests
     /______\
    /        \
   /Integration\  30% - Integration Tests
  /____________\
 /              \
/   Unit Tests   \ 60% - Unit Tests
/________________\
```

### 2.1 Distribution Target

```yaml
Unit Tests: 60%
  - Fast execution (<1ms per test)
  - Isolated components/functions
  - No external dependencies
  - High code coverage (>80%)

Integration Tests: 30%
  - Medium execution time (<1s per test)
  - Multiple components working together
  - Database/external services (TestContainers)
  - API contract testing

End-to-End Tests: 10%
  - Slower execution (seconds per test)
  - Full user workflows
  - Real browser automation
  - Critical paths only
```

## 3. Backend Testing

### 3.1 Unit Tests (JUnit 5 + Mockito)

**Target Coverage:** 80%+

**Test Structure:**
```java
package com.school.sms.student.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

/**
 * Unit tests for Student domain entity
 */
@DisplayName("Student Domain Entity")
class StudentTest {

    @Nested
    @DisplayName("Student Creation")
    class StudentCreation {

        @Test
        @DisplayName("Should create student with valid data")
        void shouldCreateStudentWithValidData() {
            // Given
            PersonalInfo personalInfo = new PersonalInfo(
                "John",
                "Doe",
                LocalDate.of(2010, 5, 15),
                "Mole on left arm",
                "123456789012"
            );

            ContactInfo contactInfo = new ContactInfo(
                "9876543210",
                "john.doe@example.com",
                "123 Main St"
            );

            FamilyInfo familyInfo = new FamilyInfo(
                "Richard Doe",
                "Jane Doe"
            );

            // When
            Student student = Student.createNew(personalInfo, contactInfo, familyInfo);

            // Then
            assertNotNull(student);
            assertEquals("John", student.getPersonalInfo().getFirstName());
            assertEquals("Doe", student.getPersonalInfo().getLastName());
            assertEquals(StudentStatus.ACTIVE, student.getStatus());
            assertEquals(0L, student.getVersion());
        }

        @Test
        @DisplayName("Should throw exception when age is below 3")
        void shouldThrowExceptionWhenAgeBelowThree() {
            // Given
            LocalDate futureDate = LocalDate.now().minusYears(2);
            PersonalInfo personalInfo = new PersonalInfo(
                "John",
                "Doe",
                futureDate,
                null,
                null
            );

            ContactInfo contactInfo = new ContactInfo("9876543210", null, null);
            FamilyInfo familyInfo = new FamilyInfo(null, null);

            // When & Then
            assertThrows(InvalidAgeException.class, () ->
                Student.createNew(personalInfo, contactInfo, familyInfo)
            );
        }

        @Test
        @DisplayName("Should throw exception when age is above 18")
        void shouldThrowExceptionWhenAgeAbove18() {
            // Given
            LocalDate oldDate = LocalDate.now().minusYears(19);
            PersonalInfo personalInfo = new PersonalInfo(
                "John",
                "Doe",
                oldDate,
                null,
                null
            );

            ContactInfo contactInfo = new ContactInfo("9876543210", null, null);
            FamilyInfo familyInfo = new FamilyInfo(null, null);

            // When & Then
            assertThrows(InvalidAgeException.class, () ->
                Student.createNew(personalInfo, contactInfo, familyInfo)
            );
        }
    }

    @Nested
    @DisplayName("Student Updates")
    class StudentUpdates {

        @Test
        @DisplayName("Should update personal info successfully")
        void shouldUpdatePersonalInfo() {
            // Given
            Student student = createValidStudent();

            // When
            student.updatePersonalInfo("Jane", "Smith");

            // Then
            assertEquals("Jane", student.getPersonalInfo().getFirstName());
            assertEquals("Smith", student.getPersonalInfo().getLastName());
        }

        @Test
        @DisplayName("Should update contact info successfully")
        void shouldUpdateContactInfo() {
            // Given
            Student student = createValidStudent();

            // When
            student.updateContactInfo("9876543211");

            // Then
            assertEquals("9876543211", student.getContactInfo().getMobile());
        }

        @Test
        @DisplayName("Should activate inactive student")
        void shouldActivateInactiveStudent() {
            // Given
            Student student = createValidStudent();
            student.deactivate();

            // When
            student.activate();

            // Then
            assertEquals(StudentStatus.ACTIVE, student.getStatus());
        }

        @Test
        @DisplayName("Should throw exception when activating already active student")
        void shouldThrowExceptionWhenActivatingActiveStudent() {
            // Given
            Student student = createValidStudent();

            // When & Then
            assertThrows(IllegalStateException.class, student::activate);
        }
    }

    private Student createValidStudent() {
        PersonalInfo personalInfo = new PersonalInfo(
            "John",
            "Doe",
            LocalDate.of(2010, 5, 15),
            null,
            null
        );

        ContactInfo contactInfo = new ContactInfo("9876543210", null, null);
        FamilyInfo familyInfo = new FamilyInfo(null, null);

        return Student.createNew(personalInfo, contactInfo, familyInfo);
    }
}
```

**Service Layer Unit Tests:**

```java
package com.school.sms.student.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Student Application Service")
class StudentApplicationServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentValidator studentValidator;

    @InjectMocks
    private StudentApplicationService studentService;

    @Test
    @DisplayName("Should create student successfully")
    void shouldCreateStudentSuccessfully() {
        // Given
        CreateStudentCommand command = new CreateStudentCommand(
            "John",
            "Doe",
            LocalDate.of(2010, 5, 15),
            "9876543210",
            "john@example.com",
            "123 Main St",
            "Richard Doe",
            "Jane Doe",
            "Mole on left arm",
            "123456789012"
        );

        when(studentRepository.existsByMobile("9876543210")).thenReturn(false);
        when(studentRepository.existsByAadhaar("123456789012")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Student result = studentService.createStudent(command);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getPersonalInfo().getFirstName());
        verify(studentValidator).validateForCreation(command);
        verify(studentRepository).existsByMobile("9876543210");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw exception when mobile already exists")
    void shouldThrowExceptionWhenMobileExists() {
        // Given
        CreateStudentCommand command = new CreateStudentCommand(
            "John",
            "Doe",
            LocalDate.of(2010, 5, 15),
            "9876543210",
            null, null, null, null, null, null
        );

        when(studentRepository.existsByMobile("9876543210")).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () ->
            studentService.createStudent(command)
        );

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when optimistic lock fails")
    void shouldThrowExceptionWhenOptimisticLockFails() {
        // Given
        UpdateStudentCommand command = new UpdateStudentCommand(
            "STD-20241206-0001",
            "John",
            "Smith",
            "9876543210",
            "ACTIVE",
            0L
        );

        Student existingStudent = createMockStudent();
        when(existingStudent.getVersion()).thenReturn(1L); // Different version

        when(studentRepository.findByStudentId(any())).thenReturn(Optional.of(existingStudent));

        // When & Then
        assertThrows(OptimisticLockException.class, () ->
            studentService.updateStudent(command)
        );
    }
}
```

### 3.2 Integration Tests (Spring Boot Test + TestContainers)

**Purpose:** Test integration between layers with real database.

```java
package com.school.sms.student.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests using TestContainers for real database
 */
@SpringBootTest
@Testcontainers
@DisplayName("Student Repository Integration Tests")
class StudentRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
        .withDatabaseName("test_sms_student_db")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("Should save and retrieve student")
    void shouldSaveAndRetrieveStudent() {
        // Given
        Student student = createValidStudent();

        // When
        Student saved = studentRepository.save(student);
        Optional<Student> retrieved = studentRepository.findById(saved.getId());

        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getPersonalInfo().getFirstName())
            .isEqualTo("John");
    }

    @Test
    @DisplayName("Should enforce unique mobile constraint")
    void shouldEnforceUniqueMobileConstraint() {
        // Given
        Student student1 = createValidStudent();
        studentRepository.save(student1);

        Student student2 = createValidStudent(); // Same mobile

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () ->
            studentRepository.save(student2)
        );
    }

    @Test
    @DisplayName("Should find student by last name")
    void shouldFindStudentByLastName() {
        // Given
        Student student = createValidStudent();
        studentRepository.save(student);

        // When
        List<Student> results = studentRepository.search(
            StudentSearchCriteria.builder()
                .lastName("Doe")
                .build()
        );

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPersonalInfo().getLastName())
            .isEqualTo("Doe");
    }

    @Test
    @DisplayName("Should handle optimistic locking correctly")
    void shouldHandleOptimisticLocking() {
        // Given
        Student student = createValidStudent();
        Student saved = studentRepository.save(student);

        // When - Two concurrent updates
        Student instance1 = studentRepository.findById(saved.getId()).get();
        Student instance2 = studentRepository.findById(saved.getId()).get();

        instance1.updatePersonalInfo("Jane", "Doe");
        studentRepository.save(instance1);

        instance2.updatePersonalInfo("John", "Smith");

        // Then - Second save should fail
        assertThrows(OptimisticLockException.class, () ->
            studentRepository.save(instance2)
        );
    }
}
```

### 3.3 REST API Integration Tests

```java
package com.school.sms.student.presentation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Student Controller Integration Tests")
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/v1/students - Should create student successfully")
    void shouldCreateStudentSuccessfully() throws Exception {
        String requestBody = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "dateOfBirth": "2010-05-15",
                "mobile": "9876543210",
                "email": "john.doe@example.com",
                "fathersName": "Richard Doe",
                "mothersName": "Jane Doe"
            }
            """;

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.studentId", notNullValue()))
            .andExpect(jsonPath("$.firstName", is("John")))
            .andExpect(jsonPath("$.lastName", is("Doe")))
            .andExpect(jsonPath("$.mobile", is("9876543210")))
            .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    @DisplayName("POST /api/v1/students - Should return 400 for invalid age")
    void shouldReturn400ForInvalidAge() throws Exception {
        String requestBody = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "dateOfBirth": "2023-01-01",
                "mobile": "9876543210"
            }
            """;

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type", containsString("validation-error")))
            .andExpect(jsonPath("$.detail", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/students - Should return 409 for duplicate mobile")
    void shouldReturn409ForDuplicateMobile() throws Exception {
        // First request
        String requestBody = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "dateOfBirth": "2010-05-15",
                "mobile": "9876543210"
            }
            """;

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());

        // Second request with same mobile
        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.type", containsString("duplicate-resource")));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Should return student")
    void shouldReturnStudent() throws Exception {
        // Create student first
        String studentId = createStudent();

        mockMvc.perform(get("/api/v1/students/" + studentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.studentId", is(studentId)))
            .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Should return 404 for non-existent student")
    void shouldReturn404ForNonExistentStudent() throws Exception {
        mockMvc.perform(get("/api/v1/students/STD-99999999-9999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.type", containsString("not-found")));
    }

    @Test
    @DisplayName("PUT /api/v1/students/{id} - Should update student")
    void shouldUpdateStudent() throws Exception {
        String studentId = createStudent();

        String updateBody = """
            {
                "firstName": "Jane",
                "lastName": "Smith",
                "mobile": "9876543211",
                "status": "INACTIVE",
                "version": 0
            }
            """;

        mockMvc.perform(put("/api/v1/students/" + studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Jane")))
            .andExpect(jsonPath("$.lastName", is("Smith")))
            .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} - Should delete student")
    void shouldDeleteStudent() throws Exception {
        String studentId = createStudent();

        mockMvc.perform(delete("/api/v1/students/" + studentId))
            .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/v1/students/" + studentId))
            .andExpect(status().isNotFound());
    }

    private String createStudent() throws Exception {
        String requestBody = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "dateOfBirth": "2010-05-15",
                "mobile": "9876543210"
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andReturn();

        String response = result.getResponse().getContentAsString();
        return JsonPath.parse(response).read("$.studentId");
    }
}
```

## 4. Frontend Testing

### 4.1 Unit Tests (Vitest + React Testing Library)

```typescript
// tests/unit/components/StudentForm.test.tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { StudentForm } from '@/components/forms/StudentForm';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

describe('StudentForm', () => {
  const queryClient = new QueryClient();

  const renderComponent = () => {
    return render(
      <QueryClientProvider client={queryClient}>
        <StudentForm />
      </QueryClientProvider>
    );
  };

  it('should render all form fields', () => {
    renderComponent();

    expect(screen.getByLabelText(/first name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/last name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/date of birth/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/mobile/i)).toBeInTheDocument();
  });

  it('should show validation errors for required fields', async () => {
    renderComponent();
    const user = userEvent.setup();

    const submitButton = screen.getByRole('button', { name: /create student/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/first name is required/i)).toBeInTheDocument();
      expect(screen.getByText(/last name is required/i)).toBeInTheDocument();
    });
  });

  it('should validate age range (3-18 years)', async () => {
    renderComponent();
    const user = userEvent.setup();

    const dobInput = screen.getByLabelText(/date of birth/i);
    const futureDate = new Date();
    futureDate.setFullYear(futureDate.getFullYear() - 2);

    await user.type(dobInput, futureDate.toISOString().split('T')[0]);
    await user.click(screen.getByRole('button', { name: /create student/i }));

    await waitFor(() => {
      expect(screen.getByText(/must be between 3 and 18/i)).toBeInTheDocument();
    });
  });

  it('should validate mobile format', async () => {
    renderComponent();
    const user = userEvent.setup();

    const mobileInput = screen.getByLabelText(/mobile/i);
    await user.type(mobileInput, '123'); // Invalid format

    await user.click(screen.getByRole('button', { name: /create student/i }));

    await waitFor(() => {
      expect(screen.getByText(/mobile must be exactly 10 digits/i)).toBeInTheDocument();
    });
  });

  it('should submit form with valid data', async () => {
    const mockCreateStudent = vi.fn().mockResolvedValue({ id: 1 });
    vi.mock('@/lib/hooks/useStudents', () => ({
      useCreateStudent: () => ({ mutateAsync: mockCreateStudent }),
    }));

    renderComponent();
    const user = userEvent.setup();

    await user.type(screen.getByLabelText(/first name/i), 'John');
    await user.type(screen.getByLabelText(/last name/i), 'Doe');
    await user.type(screen.getByLabelText(/date of birth/i), '2010-05-15');
    await user.type(screen.getByLabelText(/mobile/i), '9876543210');

    await user.click(screen.getByRole('button', { name: /create student/i }));

    await waitFor(() => {
      expect(mockCreateStudent).toHaveBeenCalledWith(
        expect.objectContaining({
          firstName: 'John',
          lastName: 'Doe',
          mobile: '9876543210',
        })
      );
    });
  });
});
```

### 4.2 Integration Tests (Component + Hooks)

```typescript
// tests/integration/students.test.tsx
import { describe, it, expect, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { rest } from 'msw';
import { setupServer } from 'msw/node';
import StudentsPage from '@/app/students/page';

const server = setupServer(
  rest.get('http://localhost:8081/api/v1/students', (req, res, ctx) => {
    return res(
      ctx.json({
        content: [
          {
            id: 1,
            studentId: 'STD-20241206-0001',
            firstName: 'John',
            lastName: 'Doe',
            mobile: '9876543210',
            status: 'ACTIVE',
            createdAt: '2024-12-06T10:00:00Z',
          },
        ],
        pageable: {
          page: 0,
          size: 20,
          totalElements: 1,
          totalPages: 1,
        },
      })
    );
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('Students Page Integration', () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  });

  it('should fetch and display students', async () => {
    render(
      <QueryClientProvider client={queryClient}>
        <StudentsPage />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByText('STD-20241206-0001')).toBeInTheDocument();
      expect(screen.getByText('John Doe')).toBeInTheDocument();
    });
  });

  it('should display error message on API failure', async () => {
    server.use(
      rest.get('http://localhost:8081/api/v1/students', (req, res, ctx) => {
        return res(ctx.status(500));
      })
    );

    render(
      <QueryClientProvider client={queryClient}>
        <StudentsPage />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByText(/failed to load students/i)).toBeInTheDocument();
    });
  });
});
```

### 4.3 End-to-End Tests (Playwright)

```typescript
// tests/e2e/student-registration.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Student Registration Flow', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3000');
  });

  test('should complete full student registration workflow', async ({ page }) => {
    // Navigate to students page
    await page.click('text=Students');
    await expect(page).toHaveURL(/.*students/);

    // Click Add Student button
    await page.click('text=Add Student');
    await expect(page).toHaveURL(/.*students\/new/);

    // Fill in the form
    await page.fill('input[name="firstName"]', 'John');
    await page.fill('input[name="lastName"]', 'Doe');
    await page.fill('input[name="dateOfBirth"]', '2010-05-15');
    await page.fill('input[name="mobile"]', '9876543210');
    await page.fill('input[name="email"]', 'john.doe@example.com');
    await page.fill('input[name="address"]', '123 Main St, City, State');
    await page.fill('input[name="fathersName"]', 'Richard Doe');
    await page.fill('input[name="mothersName"]', 'Jane Doe');

    // Submit the form
    await page.click('button[type="submit"]');

    // Wait for success and redirect
    await expect(page).toHaveURL(/.*students$/, { timeout: 5000 });

    // Verify student appears in list
    await expect(page.locator('text=John Doe')).toBeVisible();
  });

  test('should show validation errors for invalid data', async ({ page }) => {
    await page.goto('http://localhost:3000/students/new');

    // Fill in invalid age
    await page.fill('input[name="firstName"]', 'John');
    await page.fill('input[name="lastName"]', 'Doe');
    await page.fill('input[name="dateOfBirth"]', '2023-01-01'); // Too young
    await page.fill('input[name="mobile"]', '123'); // Invalid format

    await page.click('button[type="submit"]');

    // Verify validation messages
    await expect(page.locator('text=/must be between 3 and 18/i')).toBeVisible();
    await expect(page.locator('text=/mobile must be exactly 10 digits/i')).toBeVisible();
  });

  test('should update student information', async ({ page }) => {
    // Assume student exists
    await page.goto('http://localhost:3000/students');

    // Click first student's edit button
    await page.click('button:has-text("Edit")').first();

    // Update information
    await page.fill('input[name="firstName"]', 'Jane');
    await page.fill('input[name="lastName"]', 'Smith');

    // Submit update
    await page.click('button[type="submit"]');

    // Verify update
    await expect(page.locator('text=Jane Smith')).toBeVisible();
  });

  test('should search students by last name', async ({ page }) => {
    await page.goto('http://localhost:3000/students');

    // Enter search term
    await page.fill('input[placeholder*="Search"]', 'Doe');

    // Wait for search results
    await page.waitForTimeout(500);

    // Verify only matching students shown
    await expect(page.locator('text=Doe')).toBeVisible();
  });

  test('should delete student with confirmation', async ({ page }) => {
    await page.goto('http://localhost:3000/students');

    // Click delete button
    const initialCount = await page.locator('tbody tr').count();

    // Handle confirmation dialog
    page.on('dialog', dialog => dialog.accept());

    await page.click('button:has-text("Delete")').first();

    // Verify student removed
    await page.waitForTimeout(500);
    const newCount = await page.locator('tbody tr').count();
    expect(newCount).toBe(initialCount - 1);
  });
});
```

## 5. Test Scenarios - Business Requirements

### 5.1 Student Registration (BR-1: Age Validation)

```gherkin
Feature: Student Registration with Age Validation
  As a school administrator
  I want to register students aged 3-18
  So that only eligible students are enrolled

  Scenario: Register student with valid age (3 years)
    Given I am on the student registration page
    When I enter a date of birth 3 years ago
    And I submit the form
    Then the student should be created successfully

  Scenario: Register student with valid age (18 years)
    Given I am on the student registration page
    When I enter a date of birth 18 years ago
    And I submit the form
    Then the student should be created successfully

  Scenario: Reject student younger than 3 years
    Given I am on the student registration page
    When I enter a date of birth 2 years ago
    And I submit the form
    Then I should see an error "Student must be between 3 and 18 years old"

  Scenario: Reject student older than 18 years
    Given I am on the student registration page
    When I enter a date of birth 19 years ago
    And I submit the form
    Then I should see an error "Student must be between 3 and 18 years old"
```

### 5.2 Mobile Uniqueness (BR-2)

```gherkin
Feature: Unique Mobile Number Validation
  As a school administrator
  I want each student to have a unique mobile number
  So that we can identify students unambiguously

  Scenario: Register student with unique mobile
    Given a student with mobile "9876543210" does not exist
    When I register a student with mobile "9876543210"
    Then the student should be created successfully

  Scenario: Reject duplicate mobile number
    Given a student with mobile "9876543210" exists
    When I attempt to register another student with mobile "9876543210"
    Then I should see an error "Mobile number already registered"
    And the student should not be created
```

### 5.3 Student Search

```gherkin
Feature: Student Search
  As a school staff member
  I want to search students by last name or guardian name
  So that I can quickly find student records

  Scenario: Search by last name
    Given students exist with last names "Doe", "Smith", "Johnson"
    When I search for "Doe"
    Then I should see only students with last name containing "Doe"

  Scenario: Search by father's name
    Given students exist with fathers named "Richard Doe", "Robert Smith"
    When I search for "Richard"
    Then I should see only students whose father's name contains "Richard"

  Scenario: No results for invalid search
    Given students exist in the system
    When I search for "XYZ123NonExistent"
    Then I should see "No students found"
```

## 6. Quality Gates

### 6.1 Code Coverage Requirements

```yaml
Overall Coverage: >= 80%

Package-Level Requirements:
  domain: >= 90% (Critical business logic)
  application: >= 85%
  presentation: >= 75%
  infrastructure: >= 70%

Exclusions:
  - Configuration classes
  - DTOs (data classes only)
  - Main application class
  - Generated code (MapStruct)
```

### 6.2 CI/CD Pipeline Quality Gates

```yaml
Stage 1: Compilation
  - Build must succeed
  - No compilation errors

Stage 2: Unit Tests
  - All unit tests must pass
  - Coverage >= 80%
  - Execution time < 2 minutes

Stage 3: Integration Tests
  - All integration tests must pass
  - TestContainers setup successful
  - Execution time < 5 minutes

Stage 4: Code Quality
  - SonarQube Quality Gate: PASS
  - No blocker or critical issues
  - Technical debt ratio < 5%
  - Duplication < 3%

Stage 5: Security Scan
  - OWASP Dependency Check: No high/critical vulnerabilities
  - Static analysis: No security hotspots

Stage 6: E2E Tests (Pre-Production Only)
  - Critical path tests pass
  - Execution time < 10 minutes

Deployment Approval:
  - All stages must pass
  - Code review approved
  - No open blocker bugs
```

## 7. Test Data Management

### 7.1 Test Data Strategy

```yaml
Unit Tests:
  - Use builders/factories for test data
  - No database required
  - Fully isolated

Integration Tests:
  - TestContainers with ephemeral database
  - Flyway migrations applied
  - Fresh database per test class
  - @Transactional rollback after each test

E2E Tests:
  - Dedicated test environment
  - Seed data via API or SQL scripts
  - Reset database before test suite
  - Use unique identifiers to avoid conflicts
```

### 7.2 Test Data Builders

```java
// Test data builder pattern
public class StudentTestBuilder {
    private String firstName = "John";
    private String lastName = "Doe";
    private LocalDate dateOfBirth = LocalDate.now().minusYears(10);
    private String mobile = "9876543210";

    public static StudentTestBuilder aStudent() {
        return new StudentTestBuilder();
    }

    public StudentTestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public StudentTestBuilder withAge(int years) {
        this.dateOfBirth = LocalDate.now().minusYears(years);
        return this;
    }

    public StudentTestBuilder withMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public Student build() {
        PersonalInfo personalInfo = new PersonalInfo(firstName, lastName, dateOfBirth, null, null);
        ContactInfo contactInfo = new ContactInfo(mobile, null, null);
        FamilyInfo familyInfo = new FamilyInfo(null, null);

        return Student.createNew(personalInfo, contactInfo, familyInfo);
    }
}

// Usage
Student student = StudentTestBuilder.aStudent()
    .withFirstName("Jane")
    .withAge(15)
    .withMobile("9876543211")
    .build();
```

## 8. Performance Testing

### 8.1 Load Testing (Gatling or JMeter)

```scala
// Gatling load test scenario
class StudentRegistrationLoadTest extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8081")
    .acceptHeader("application/json")

  val scn = scenario("Student Registration")
    .exec(http("Create Student")
      .post("/api/v1/students")
      .body(StringBody("""
        {
          "firstName": "John",
          "lastName": "Doe",
          "dateOfBirth": "2010-05-15",
          "mobile": "987654${randomMobile}"
        }
      """))
      .check(status.is(201))
      .check(responseTimeInMillis.lte(200)) // 95th percentile < 200ms
    )

  setUp(
    scn.inject(
      rampUsers(100) during (10 seconds),
      constantUsersPerSec(50) during (30 seconds)
    )
  ).protocols(httpProtocol)
   .assertions(
     global.responseTime.percentile3.lt(200),
     global.successfulRequests.percent.gt(99)
   )
}
```

## 9. Test Execution

### 9.1 Running Tests Locally

```bash
# Backend - Unit Tests
mvn clean test

# Backend - Integration Tests
mvn clean verify -P integration-test

# Backend - All Tests with Coverage
mvn clean verify jacoco:report

# Frontend - Unit Tests
npm run test

# Frontend - E2E Tests
npm run test:e2e

# Frontend - Coverage
npm run test -- --coverage
```

### 9.2 CI/CD Test Execution

```yaml
# GitHub Actions / GitLab CI example
name: CI Pipeline

on: [push, pull_request]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
      - name: Run Unit Tests
        run: mvn clean test
      - name: Run Integration Tests
        run: mvn clean verify
      - name: Upload Coverage
        uses: codecov/codecov-action@v3

  frontend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up Node
        uses: actions/setup-node@v3
        with:
          node-version: '20'
      - name: Install Dependencies
        run: npm ci
      - name: Run Tests
        run: npm run test -- --coverage
      - name: Run E2E Tests
        run: npm run test:e2e
```

## 10. Mutation Testing (Optional - Advanced)

```xml
<!-- PIT Mutation Testing -->
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.14.2</version>
    <configuration>
        <targetClasses>
            <param>com.school.sms.student.domain.*</param>
            <param>com.school.sms.student.application.*</param>
        </targetClasses>
        <targetTests>
            <param>com.school.sms.student.*</param>
        </targetTests>
        <mutationThreshold>80</mutationThreshold>
    </configuration>
</plugin>
```

---

**Document Version:** 1.0
**Last Updated:** 2025-12-06
**Status:** APPROVED
