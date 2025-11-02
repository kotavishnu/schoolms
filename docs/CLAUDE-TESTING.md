# CLAUDE-TESTING.md

**Tier 2: Testing Component Agent**

This agent provides comprehensive guidance for implementing Test-Driven Development (TDD) methodology across the School Management System.

---

## Agent Role

**Purpose**: Guide implementation of TDD practices, test automation, and quality assurance for both backend (Spring Boot) and frontend (React).

**Scope**: Application-wide testing concerns, test infrastructure, and CI/CD integration.

---

## Testing Philosophy

### Test-Driven Development (TDD)

**Core Principle**: Write tests before writing implementation code.

**TDD Cycle**:
```
1. RED: Write a failing test
   ↓
2. GREEN: Write minimal code to pass
   ↓
3. REFACTOR: Improve code quality
   ↓
4. REPEAT
```

**Benefits**:
- Higher code quality
- Better design decisions
- Living documentation
- Confidence in refactoring
- Fewer bugs in production

---

## Test Coverage Requirements

### Backend Coverage Targets
- **Service Layer**: 80%+ coverage (business logic)
- **Repository Layer**: 70%+ coverage (data access)
- **Controller Layer**: 75%+ coverage (API endpoints)
- **Critical Paths** (fee calculation, payments): 100% coverage
- **Utility Classes**: 90%+ coverage

### Frontend Coverage Targets
- **Components**: 70%+ coverage
- **Services (API layer)**: 80%+ coverage
- **Custom Hooks**: 75%+ coverage
- **Utilities**: 85%+ coverage
- **Critical User Flows**: 100% coverage

---

## Backend Testing Stack

### Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Test Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- JUnit 5 (included in spring-boot-starter-test) -->
    <!-- Mockito (included in spring-boot-starter-test) -->

    <!-- H2 In-Memory Database for Testing -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- AssertJ (for fluent assertions) -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- REST Assured (for API testing) -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<!-- JaCoCo for Code Coverage -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## Backend Testing Patterns

### 1. Unit Tests (Service Layer)

**Purpose**: Test business logic in isolation

**Pattern**: Use Mockito to mock dependencies

```java
package com.school.management.service;

import com.school.management.dto.request.StudentRequestDTO;
import com.school.management.dto.response.StudentResponseDTO;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.mapper.StudentMapper;
import com.school.management.model.SchoolClass;
import com.school.management.model.Student;
import com.school.management.model.StudentStatus;
import com.school.management.repository.ClassRepository;
import com.school.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Unit Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ClassRepository classRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private StudentRequestDTO requestDTO;
    private Student student;
    private SchoolClass schoolClass;

    @BeforeEach
    void setUp() {
        // Arrange - Setup test data
        requestDTO = new StudentRequestDTO();
        requestDTO.setFirstName("John");
        requestDTO.setLastName("Doe");
        requestDTO.setDateOfBirth(LocalDate.of(2010, 1, 15));
        requestDTO.setMobile("9876543210");
        requestDTO.setAddress("123 Main St");
        requestDTO.setMotherName("Jane Doe");
        requestDTO.setFatherName("Jim Doe");
        requestDTO.setClassId(1L);

        schoolClass = SchoolClass.builder()
            .id(1L)
            .classNumber(5)
            .section("A")
            .capacity(50)
            .build();

        student = Student.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .mobile("9876543210")
            .schoolClass(schoolClass)
            .status(StudentStatus.ACTIVE)
            .build();
    }

    @Test
    @DisplayName("Should create student successfully when valid data provided")
    void shouldCreateStudentSuccessfully() {
        // Arrange
        when(studentRepository.existsByMobile(anyString())).thenReturn(false);
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentRepository.countBySchoolClassId(1L)).thenReturn(20L);
        when(studentMapper.toEntity(any(StudentRequestDTO.class))).thenReturn(student);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toResponseDTO(any(Student.class)))
            .thenReturn(new StudentResponseDTO());

        // Act
        StudentResponseDTO result = studentService.createStudent(requestDTO);

        // Assert
        assertThat(result).isNotNull();
        verify(studentRepository).existsByMobile("9876543210");
        verify(classRepository).findById(1L);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when mobile already exists")
    void shouldThrowExceptionWhenMobileExists() {
        // Arrange
        when(studentRepository.existsByMobile("9876543210")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> studentService.createStudent(requestDTO))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("mobile");

        verify(studentRepository).existsByMobile("9876543210");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when class not found")
    void shouldThrowExceptionWhenClassNotFound() {
        // Arrange
        when(studentRepository.existsByMobile(anyString())).thenReturn(false);
        when(classRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.createStudent(requestDTO))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Class not found");

        verify(classRepository).findById(1L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when class capacity exceeded")
    void shouldThrowExceptionWhenCapacityExceeded() {
        // Arrange
        when(studentRepository.existsByMobile(anyString())).thenReturn(false);
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(studentRepository.countBySchoolClassId(1L)).thenReturn(50L); // At capacity

        // Act & Assert
        assertThatThrownBy(() -> studentService.createStudent(requestDTO))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("capacity exceeded");
    }

    @Test
    @DisplayName("Should get student by id successfully")
    void shouldGetStudentById() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.toResponseDTO(student)).thenReturn(new StudentResponseDTO());

        // Act
        StudentResponseDTO result = studentService.getStudent(1L);

        // Assert
        assertThat(result).isNotNull();
        verify(studentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when student not found by id")
    void shouldThrowExceptionWhenStudentNotFoundById() {
        // Arrange
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.getStudent(999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Student not found");
    }
}
```

---

### 2. Integration Tests (Repository Layer)

**Purpose**: Test database operations with real database

**Pattern**: Use `@DataJpaTest` with H2 in-memory database

**Test Configuration** (src/test/resources/application-test.properties):
```properties
# H2 In-Memory Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.org.hibernate.SQL=DEBUG
```

**Repository Test Example**:
```java
package com.school.management.repository;

import com.school.management.model.SchoolClass;
import com.school.management.model.Student;
import com.school.management.model.StudentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("StudentRepository Integration Tests")
class StudentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    private SchoolClass schoolClass;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        // Create test class
        schoolClass = SchoolClass.builder()
            .classNumber(5)
            .section("A")
            .academicYear("2024-2025")
            .capacity(50)
            .build();
        entityManager.persist(schoolClass);

        // Create test students
        student1 = Student.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2010, 1, 15))
            .mobile("9876543210")
            .address("123 Main St")
            .motherName("Jane Doe")
            .fatherName("Jim Doe")
            .schoolClass(schoolClass)
            .enrollmentDate(LocalDate.now())
            .status(StudentStatus.ACTIVE)
            .build();

        student2 = Student.builder()
            .firstName("Alice")
            .lastName("Smith")
            .dateOfBirth(LocalDate.of(2010, 5, 20))
            .mobile("9876543211")
            .address("456 Oak Ave")
            .motherName("Mary Smith")
            .fatherName("Bob Smith")
            .schoolClass(schoolClass)
            .enrollmentDate(LocalDate.now())
            .status(StudentStatus.ACTIVE)
            .build();

        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find students by class id")
    void shouldFindStudentsByClassId() {
        // Act
        List<Student> students = studentRepository.findBySchoolClassId(schoolClass.getId());

        // Assert
        assertThat(students).hasSize(2);
        assertThat(students).extracting(Student::getFirstName)
            .containsExactlyInAnyOrder("John", "Alice");
    }

    @Test
    @DisplayName("Should find student by mobile")
    void shouldFindStudentByMobile() {
        // Act
        Optional<Student> found = studentRepository.findByMobile("9876543210");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should return empty when mobile not found")
    void shouldReturnEmptyWhenMobileNotFound() {
        // Act
        Optional<Student> found = studentRepository.findByMobile("0000000000");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should search students by name")
    void shouldSearchStudentsByName() {
        // Act
        List<Student> foundByFirst = studentRepository.searchByName("joh");
        List<Student> foundByLast = studentRepository.searchByName("smi");

        // Assert
        assertThat(foundByFirst).hasSize(1);
        assertThat(foundByFirst.get(0).getFirstName()).isEqualTo("John");

        assertThat(foundByLast).hasSize(1);
        assertThat(foundByLast.get(0).getLastName()).isEqualTo("Smith");
    }

    @Test
    @DisplayName("Should check if mobile exists")
    void shouldCheckMobileExists() {
        // Act
        boolean exists = studentRepository.existsByMobile("9876543210");
        boolean notExists = studentRepository.existsByMobile("0000000000");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should count students by class")
    void shouldCountStudentsByClass() {
        // Act
        long count = studentRepository.countBySchoolClassId(schoolClass.getId());

        // Assert
        assertThat(count).isEqualTo(2);
    }
}
```

---

### 3. Controller Tests (API Layer)

**Purpose**: Test REST endpoints with MockMvc

**Pattern**: Use `@WebMvcTest` to test controllers in isolation

```java
package com.school.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.management.dto.request.StudentRequestDTO;
import com.school.management.dto.response.StudentResponseDTO;
import com.school.management.exception.ResourceNotFoundException;
import com.school.management.exception.ValidationException;
import com.school.management.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@DisplayName("StudentController API Tests")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    @DisplayName("POST /api/students - Should create student successfully")
    void shouldCreateStudentSuccessfully() throws Exception {
        // Arrange
        StudentRequestDTO requestDTO = new StudentRequestDTO();
        requestDTO.setFirstName("John");
        requestDTO.setLastName("Doe");
        requestDTO.setDateOfBirth(LocalDate.of(2010, 1, 15));
        requestDTO.setMobile("9876543210");
        requestDTO.setAddress("123 Main St");
        requestDTO.setMotherName("Jane Doe");
        requestDTO.setFatherName("Jim Doe");
        requestDTO.setClassId(1L);

        StudentResponseDTO responseDTO = new StudentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setFirstName("John");
        responseDTO.setLastName("Doe");

        when(studentService.createStudent(any(StudentRequestDTO.class)))
            .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Student created successfully"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.firstName").value("John"));

        verify(studentService).createStudent(any(StudentRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/students - Should return 400 when validation fails")
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        // Arrange
        StudentRequestDTO invalidDTO = new StudentRequestDTO();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/students/{id} - Should return student")
    void shouldGetStudentById() throws Exception {
        // Arrange
        StudentResponseDTO responseDTO = new StudentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setFirstName("John");

        when(studentService.getStudent(1L)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/students/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    @DisplayName("GET /api/students/{id} - Should return 404 when not found")
    void shouldReturn404WhenStudentNotFound() throws Exception {
        // Arrange
        when(studentService.getStudent(999L))
            .thenThrow(new ResourceNotFoundException("Student not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/students/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("GET /api/students - Should return all students")
    void shouldGetAllStudents() throws Exception {
        // Arrange
        StudentResponseDTO student1 = new StudentResponseDTO();
        student1.setId(1L);
        student1.setFirstName("John");

        StudentResponseDTO student2 = new StudentResponseDTO();
        student2.setId(2L);
        student2.setFirstName("Alice");

        List<StudentResponseDTO> students = Arrays.asList(student1, student2);

        when(studentService.getAllStudents()).thenReturn(students);

        // Act & Assert
        mockMvc.perform(get("/api/students"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("DELETE /api/students/{id} - Should delete student")
    void shouldDeleteStudent() throws Exception {
        // Arrange
        doNothing().when(studentService).deleteStudent(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/students/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Student deleted successfully"));

        verify(studentService).deleteStudent(1L);
    }
}
```

---

## Frontend Testing Stack

### Dependencies (package.json)

```json
{
  "devDependencies": {
    "vitest": "^1.0.0",
    "@testing-library/react": "^14.0.0",
    "@testing-library/jest-dom": "^6.1.0",
    "@testing-library/user-event": "^14.5.0",
    "@vitest/ui": "^1.0.0",
    "jsdom": "^23.0.0"
  }
}
```

### Vitest Configuration (vitest.config.js)

```javascript
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.js',
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'src/test/',
      ],
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@components': path.resolve(__dirname, './src/components'),
      '@pages': path.resolve(__dirname, './src/pages'),
      '@services': path.resolve(__dirname, './src/services'),
      '@hooks': path.resolve(__dirname, './src/hooks'),
      '@utils': path.resolve(__dirname, './src/utils'),
    },
  },
});
```

### Test Setup (src/test/setup.js)

```javascript
import { expect, afterEach } from 'vitest';
import { cleanup } from '@testing-library/react';
import '@testing-library/jest-dom';

// Cleanup after each test
afterEach(() => {
  cleanup();
});
```

---

## Frontend Testing Patterns

### 1. Component Tests

```javascript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import StudentForm from '@pages/StudentRegistration';
import * as studentService from '@services/studentService';

// Mock the service module
vi.mock('@services/studentService');

describe('StudentForm Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render all required form fields', () => {
    render(<StudentForm />);

    expect(screen.getByLabelText(/first name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/last name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/date of birth/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/mobile/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/address/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /register/i })).toBeInTheDocument();
  });

  it('should show validation errors on empty submit', async () => {
    render(<StudentForm />);

    const submitButton = screen.getByRole('button', { name: /register/i });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/first name is required/i)).toBeInTheDocument();
      expect(screen.getByText(/last name is required/i)).toBeInTheDocument();
    });
  });

  it('should submit form with valid data', async () => {
    const mockCreate = vi.fn().mockResolvedValue({
      data: { id: 1, firstName: 'John', lastName: 'Doe' }
    });
    studentService.createStudent = mockCreate;

    render(<StudentForm />);

    // Fill form
    fireEvent.change(screen.getByLabelText(/first name/i), {
      target: { value: 'John' }
    });
    fireEvent.change(screen.getByLabelText(/last name/i), {
      target: { value: 'Doe' }
    });
    fireEvent.change(screen.getByLabelText(/mobile/i), {
      target: { value: '9876543210' }
    });

    // Submit
    const submitButton = screen.getByRole('button', { name: /register/i });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockCreate).toHaveBeenCalledWith(
        expect.objectContaining({
          firstName: 'John',
          lastName: 'Doe',
          mobile: '9876543210'
        })
      );
    });
  });

  it('should display error message on API failure', async () => {
    const mockCreate = vi.fn().mockRejectedValue({
      response: { data: { message: 'Mobile number already exists' } }
    });
    studentService.createStudent = mockCreate;

    render(<StudentForm />);

    // Fill and submit form
    fireEvent.change(screen.getByLabelText(/first name/i), {
      target: { value: 'John' }
    });
    fireEvent.click(screen.getByRole('button', { name: /register/i }));

    await waitFor(() => {
      expect(screen.getByText(/mobile number already exists/i)).toBeInTheDocument();
    });
  });
});
```

### 2. Custom Hook Tests

```javascript
import { renderHook, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { useDebounce } from '@hooks/useDebounce';

describe('useDebounce Hook', () => {
  it('should debounce value changes', async () => {
    const { result, rerender } = renderHook(
      ({ value, delay }) => useDebounce(value, delay),
      { initialProps: { value: 'initial', delay: 500 } }
    );

    expect(result.current).toBe('initial');

    // Change value
    rerender({ value: 'updated', delay: 500 });

    // Should still be initial (not debounced yet)
    expect(result.current).toBe('initial');

    // Wait for debounce
    await waitFor(() => {
      expect(result.current).toBe('updated');
    }, { timeout: 600 });
  });
});
```

### 3. Service Tests (API Layer)

```javascript
import { describe, it, expect, vi, beforeEach } from 'vitest';
import axios from 'axios';
import { studentService } from '@services/studentService';

vi.mock('axios');

describe('StudentService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch all students', async () => {
    const mockData = [
      { id: 1, firstName: 'John', lastName: 'Doe' },
      { id: 2, firstName: 'Alice', lastName: 'Smith' }
    ];

    axios.get.mockResolvedValue({ data: mockData });

    const result = await studentService.getAllStudents();

    expect(axios.get).toHaveBeenCalledWith('/students');
    expect(result.data).toEqual(mockData);
  });

  it('should create student', async () => {
    const studentData = { firstName: 'John', lastName: 'Doe' };
    const mockResponse = { id: 1, ...studentData };

    axios.post.mockResolvedValue({ data: mockResponse });

    const result = await studentService.createStudent(studentData);

    expect(axios.post).toHaveBeenCalledWith('/students', studentData);
    expect(result.data).toEqual(mockResponse);
  });
});
```

---

## Test Data Builders

### Backend Test Data Builder Pattern

```java
package com.school.management.test.builders;

import com.school.management.model.Student;
import com.school.management.model.SchoolClass;
import com.school.management.model.StudentStatus;

import java.time.LocalDate;

public class StudentTestBuilder {

    private Long id = 1L;
    private String firstName = "John";
    private String lastName = "Doe";
    private LocalDate dateOfBirth = LocalDate.of(2010, 1, 15);
    private String mobile = "9876543210";
    private String address = "123 Main St";
    private String motherName = "Jane Doe";
    private String fatherName = "Jim Doe";
    private SchoolClass schoolClass;
    private StudentStatus status = StudentStatus.ACTIVE;

    public StudentTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public StudentTestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public StudentTestBuilder withMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public StudentTestBuilder withSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
        return this;
    }

    public Student build() {
        return Student.builder()
            .id(id)
            .firstName(firstName)
            .lastName(lastName)
            .dateOfBirth(dateOfBirth)
            .mobile(mobile)
            .address(address)
            .motherName(motherName)
            .fatherName(fatherName)
            .schoolClass(schoolClass)
            .enrollmentDate(LocalDate.now())
            .status(status)
            .build();
    }

    public static StudentTestBuilder aStudent() {
        return new StudentTestBuilder();
    }
}

// Usage:
// Student student = aStudent().withMobile("9999999999").build();
```

---

## Running Tests

### Backend Test Commands

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=StudentServiceTest

# Run specific test method
mvn test -Dtest=StudentServiceTest#shouldCreateStudentSuccessfully

# Run with coverage report
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html

# Run integration tests only
mvn verify

# Skip tests during build
mvn clean package -DskipTests
```

### Frontend Test Commands

```bash
# Run all tests
npm run test

# Run in watch mode
npm run test:watch

# Run with coverage
npm run test:coverage

# Run with UI
npm run test:ui

# Run specific test file
npm run test StudentForm.test.jsx
```

---

## CI/CD Integration

### GitHub Actions Workflow (.github/workflows/ci.yml)

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  backend-tests:
    name: Backend Tests
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Run tests with Maven
        run: mvn clean test
        working-directory: ./backend

      - name: Generate coverage report
        run: mvn jacoco:report
        working-directory: ./backend

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./backend/target/site/jacoco/jacoco.xml
          flags: backend
          name: backend-coverage

  frontend-tests:
    name: Frontend Tests
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: ./frontend/package-lock.json

      - name: Install dependencies
        run: npm ci
        working-directory: ./frontend

      - name: Run tests
        run: npm run test:coverage
        working-directory: ./frontend

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./frontend/coverage/coverage-final.json
          flags: frontend
          name: frontend-coverage

  build:
    name: Build Application
    needs: [backend-tests, frontend-tests]
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Build backend
        run: mvn clean package -DskipTests
        working-directory: ./backend

      - name: Set up Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '20'

      - name: Build frontend
        run: |
          npm ci
          npm run build
        working-directory: ./frontend
```

---

## Test Quality Guidelines

### Writing Good Tests

**DO:**
- ✅ Follow AAA pattern (Arrange, Act, Assert)
- ✅ Use descriptive test names
- ✅ Test one thing per test
- ✅ Use test builders for complex objects
- ✅ Mock external dependencies
- ✅ Test edge cases and error scenarios
- ✅ Keep tests fast and independent

**DON'T:**
- ❌ Test implementation details
- ❌ Have tests depend on each other
- ❌ Use hard-coded values (use constants)
- ❌ Skip cleanup in tests
- ❌ Ignore failing tests
- ❌ Write tests after implementation (violates TDD)

### Test Naming Conventions

**Backend (JUnit)**:
```java
@Test
@DisplayName("Should {expected behavior} when {condition}")
void should{ExpectedBehavior}When{Condition}() {
    // Test implementation
}
```

**Frontend (Vitest)**:
```javascript
it('should {expected behavior} when {condition}', () => {
  // Test implementation
});
```

---

## Code Coverage Reports

### Viewing Coverage

**Backend (JaCoCo)**:
```bash
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

**Frontend (Vitest)**:
```bash
npm run test:coverage
open coverage/index.html
```

### Coverage Thresholds

Configure minimum coverage in `pom.xml`:
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

---

## Best Practices Summary

1. **Write tests first** (TDD methodology)
2. **Keep tests isolated** (no dependencies between tests)
3. **Use meaningful test data** (realistic scenarios)
4. **Test behavior, not implementation**
5. **Maintain high coverage** (80%+ for critical paths)
6. **Run tests frequently** (pre-commit, CI/CD)
7. **Review test failures immediately**
8. **Refactor tests** alongside production code
9. **Document complex test scenarios**
10. **Use test builders** for complex setup

---

## Next Steps

- For backend patterns, see **CLAUDE-BACKEND.md**
- For frontend patterns, see **CLAUDE-FRONTEND.md**
- For Git workflow, see **CLAUDE-GIT.md**
- For feature specifications, see **docs/features/CLAUDE-FEATURE-*.md**

---

**Agent Directive**: This is a Tier 2 component agent. Apply these testing patterns across all features. Always write tests before implementation code.
