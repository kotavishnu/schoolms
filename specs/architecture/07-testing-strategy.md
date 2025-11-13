# Testing Strategy - School Management System

**Version**: 1.0
**Date**: November 10, 2025
**Status**: Approved
**Author**: Quality Assurance Team

---

## Table of Contents
1. [Testing Overview](#testing-overview)
2. [Testing Pyramid](#testing-pyramid)
3. [Backend Testing](#backend-testing)
4. [Frontend Testing](#frontend-testing)
5. [Integration Testing](#integration-testing)
6. [End-to-End Testing](#end-to-end-testing)
7. [Performance Testing](#performance-testing)
8. [Security Testing](#security-testing)
9. [Test Data Management](#test-data-management)
10. [Continuous Testing](#continuous-testing)

---

## 1. Testing Overview

### 1.1 Testing Goals

- **Code Coverage**: 80%+ backend, 70%+ frontend
- **Quality**: Catch bugs early in development cycle
- **Confidence**: Deploy with confidence
- **Documentation**: Tests as living documentation
- **Regression Prevention**: Prevent bugs from reoccurring

### 1.2 Testing Principles

1. **Test Early, Test Often**: Shift-left testing approach
2. **Test Automation**: Automate as much as possible
3. **Fast Feedback**: Tests should run quickly
4. **Isolation**: Tests should be independent
5. **Maintainability**: Tests should be easy to understand and maintain
6. **Realistic**: Use realistic test data
7. **Coverage**: Cover happy paths, edge cases, and error scenarios

### 1.3 Testing Tools

| Layer | Tool | Purpose |
|-------|------|---------|
| **Backend Unit** | JUnit 5 + Mockito | Unit testing, mocking |
| **Backend Integration** | TestContainers | Database integration tests |
| **Backend API** | REST Assured | API testing |
| **Frontend Unit** | Vitest + RTL | Component testing |
| **Frontend Integration** | React Query Testing | Hook testing |
| **E2E** | Playwright | End-to-end testing |
| **Performance** | JMeter, k6 | Load testing |
| **Security** | OWASP ZAP | Security testing |

---

## 2. Testing Pyramid

```
           ╱╲
          ╱  ╲         E2E Tests (10%)
         ╱────╲        - Critical user flows
        ╱      ╲       - Cross-browser testing
       ╱────────╲
      ╱          ╲     Integration Tests (30%)
     ╱────────────╲    - API contracts
    ╱              ╲   - Database interactions
   ╱────────────────╲
  ╱                  ╲ Unit Tests (60%)
 ╱────────────────────╲ - Business logic
╱______________________╲ - Pure functions
```

### 2.1 Test Distribution

| Test Type | Percentage | Execution Time | Purpose |
|-----------|------------|----------------|---------|
| **Unit Tests** | 60% | < 5 minutes | Test individual functions, methods, components |
| **Integration Tests** | 30% | < 15 minutes | Test module interactions, API contracts |
| **E2E Tests** | 10% | < 30 minutes | Test critical user journeys |

---

## 3. Backend Testing

### 3.1 Unit Tests (JUnit 5 + Mockito)

#### 3.1.1 Service Layer Tests

```java
@ExtendWith(MockitoExtension.class)
class StudentRegistrationServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private StudentRegistrationService studentRegistrationService;

    @Test
    @DisplayName("Should successfully register student with valid data")
    void shouldRegisterStudentSuccessfully() {
        // Given
        CreateStudentRequest request = createValidStudentRequest();
        Student student = createStudent();
        StudentResponse expectedResponse = createStudentResponse();

        when(studentRepository.existsByMobile(request.getMobile())).thenReturn(false);
        when(studentMapper.toEntity(request)).thenReturn(student);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toResponse(student)).thenReturn(expectedResponse);

        // When
        StudentResponse response = studentRegistrationService.registerStudent(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStudentCode()).startsWith("STU-2025-");
        verify(studentRepository).save(any(Student.class));
        verify(eventPublisher).publish(any(StudentRegisteredEvent.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when age is below 3")
    void shouldThrowExceptionWhenAgeBelowMinimum() {
        // Given
        CreateStudentRequest request = createStudentRequest();
        request.setDateOfBirth(LocalDate.now().minusYears(2));

        // When & Then
        assertThatThrownBy(() -> studentRegistrationService.registerStudent(request))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Student age must be between 3 and 18");
    }

    @Test
    @DisplayName("Should throw ValidationException when mobile already exists")
    void shouldThrowExceptionWhenMobileDuplicate() {
        // Given
        CreateStudentRequest request = createValidStudentRequest();
        when(studentRepository.existsByMobile(request.getMobile())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> studentRegistrationService.registerStudent(request))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Mobile number already exists");
    }

    // Helper methods
    private CreateStudentRequest createValidStudentRequest() {
        return CreateStudentRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.now().minusYears(10))
            .mobile("9876543210")
            .gender(Gender.MALE)
            .admissionDate(LocalDate.now())
            .guardians(List.of(createGuardianRequest()))
            .build();
    }
}
```

#### 3.1.2 Domain Logic Tests

```java
class StudentTest {

    @Test
    @DisplayName("Student should be enrollable when status is ACTIVE")
    void shouldBeEnrollableWhenActive() {
        // Given
        Student student = createActiveStudent();
        Class classEntity = createClassWithCapacity();

        // When & Then
        assertDoesNotThrow(() -> student.enroll(classEntity, LocalDate.now()));
    }

    @Test
    @DisplayName("Should throw exception when enrolling inactive student")
    void shouldThrowExceptionWhenEnrollingInactiveStudent() {
        // Given
        Student student = createInactiveStudent();
        Class classEntity = createClassWithCapacity();

        // When & Then
        assertThatThrownBy(() -> student.enroll(classEntity, LocalDate.now()))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Only active students can be enrolled");
    }

    @Test
    @DisplayName("Should calculate correct age")
    void shouldCalculateCorrectAge() {
        // Given
        LocalDate birthDate = LocalDate.now().minusYears(10).minusMonths(6);
        Student student = createStudent(birthDate);

        // When
        int age = student.getAge();

        // Then
        assertThat(age).isEqualTo(10);
    }
}
```

#### 3.1.3 Fee Calculation Tests

```java
class FeeCalculationDomainServiceTest {

    @Mock
    private FeeRulesEngine feeRulesEngine;

    @InjectMocks
    private FeeCalculationDomainService feeCalculationService;

    @Test
    @DisplayName("Should calculate monthly fee correctly")
    void shouldCalculateMonthlyFeeCorrectly() {
        // Given
        Student student = createStudent();
        AcademicYear academicYear = createAcademicYear();
        YearMonth month = YearMonth.of(2025, 4);

        FeeStructure tuitionFee = createFeeStructure("TUITION", 2000.00, "MONTHLY");
        FeeStructure libraryFee = createFeeStructure("LIBRARY", 100.00, "MONTHLY");

        List<FeeStructure> feeStructures = List.of(tuitionFee, libraryFee);

        // When
        BigDecimal totalFee = feeCalculationService.calculateMonthlyFee(
            student, academicYear, month
        );

        // Then
        assertThat(totalFee).isEqualByComparingTo("2100.00");
        verify(feeRulesEngine).calculate(any(FeeCalculationContext.class));
    }

    @Test
    @DisplayName("Should apply sibling discount for second child")
    void shouldApplySiblingDiscount() {
        // Given
        Student student = createStudentWithSiblings(2);
        FeeStructure tuitionFee = createFeeStructure("TUITION", 2000.00, "MONTHLY");

        // When
        BigDecimal totalFee = feeCalculationService.calculateMonthlyFee(
            student, createAcademicYear(), YearMonth.now()
        );

        // Then
        BigDecimal expectedFee = new BigDecimal("1800.00"); // 10% discount
        assertThat(totalFee).isEqualByComparingTo(expectedFee);
    }
}
```

### 3.2 Repository Tests (TestContainers)

```java
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudentRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
        .withDatabaseName("test_school_sms")
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

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should find student by student code")
    void shouldFindStudentByStudentCode() {
        // Given
        Student student = createAndSaveStudent("STU-2025-00001");

        // When
        Optional<Student> found = studentRepository.findByStudentCode("STU-2025-00001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getStudentCode()).isEqualTo("STU-2025-00001");
    }

    @Test
    @DisplayName("Should check mobile uniqueness")
    void shouldCheckMobileUniqueness() {
        // Given
        Student student = createAndSaveStudent("9876543210");

        // When
        boolean exists = studentRepository.existsByMobile("9876543210");
        boolean notExists = studentRepository.existsByMobile("9876543211");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find students by class")
    void shouldFindStudentsByClass() {
        // Given
        Class classEntity = createAndSaveClass("5", "A");
        Student student1 = createAndEnrollStudent(classEntity);
        Student student2 = createAndEnrollStudent(classEntity);

        // When
        List<Student> students = studentRepository.findByClassId(classEntity.getClassId());

        // Then
        assertThat(students).hasSize(2);
        assertThat(students).extracting(Student::getStudentId)
            .containsExactlyInAnyOrder(student1.getStudentId(), student2.getStudentId());
    }

    @Test
    @DisplayName("Should count students by admission year")
    void shouldCountStudentsByAdmissionYear() {
        // Given
        createAndSaveStudent(LocalDate.of(2025, 4, 1));
        createAndSaveStudent(LocalDate.of(2025, 5, 1));
        createAndSaveStudent(LocalDate.of(2024, 4, 1));

        // When
        long count2025 = studentRepository.countByAdmissionYear(2025);
        long count2024 = studentRepository.countByAdmissionYear(2024);

        // Then
        assertThat(count2025).isEqualTo(2);
        assertThat(count2024).isEqualTo(1);
    }

    private Student createAndSaveStudent(String studentCode) {
        Student student = Student.builder()
            .studentCode(studentCode)
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.now().minusYears(10))
            .mobile("9876543210")
            .status(StudentStatus.ACTIVE)
            .admissionDate(LocalDate.now())
            .build();

        return entityManager.persistAndFlush(student);
    }
}
```

### 3.3 REST Controller Tests

```java
@WebMvcTest(StudentController.class)
@Import(SecurityConfig.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRegistrationService studentRegistrationService;

    @MockBean
    private StudentProfileService studentProfileService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Should register student successfully")
    @WithMockUser(authorities = "student:write")
    void shouldRegisterStudentSuccessfully() throws Exception {
        // Given
        CreateStudentRequest request = createValidStudentRequest();
        StudentResponse response = createStudentResponse();

        when(studentRegistrationService.registerStudent(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.studentId").value(123))
            .andExpect(jsonPath("$.studentCode").value("STU-2025-00123"))
            .andExpect(jsonPath("$.firstName").value("John"));

        verify(studentRegistrationService).registerStudent(any());
    }

    @Test
    @DisplayName("Should return 400 when validation fails")
    @WithMockUser(authorities = "student:write")
    void shouldReturn400WhenValidationFails() throws Exception {
        // Given
        CreateStudentRequest request = createInvalidStudentRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Validation Error"))
            .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/students/123"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 403 when insufficient permissions")
    @WithMockUser(authorities = "student:read")
    void shouldReturn403WhenInsufficientPermissions() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get student by ID")
    @WithMockUser(authorities = "student:read")
    void shouldGetStudentById() throws Exception {
        // Given
        StudentResponse response = createStudentResponse();
        when(studentProfileService.getStudent(123L)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/students/123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.studentId").value(123))
            .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @DisplayName("Should search students with filters")
    @WithMockUser(authorities = "student:read")
    void shouldSearchStudentsWithFilters() throws Exception {
        // Given
        Page<StudentResponse> page = createStudentPage();
        when(studentProfileService.searchStudents(any(), any(), any(), any()))
            .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/students")
                .param("status", "ACTIVE")
                .param("class", "5")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.page.totalElements").value(100));
    }
}
```

### 3.4 Test Coverage Targets

| Layer | Target Coverage | Minimum Coverage |
|-------|-----------------|------------------|
| Domain Entities | 90% | 80% |
| Services | 85% | 75% |
| Repositories | 80% | 70% |
| Controllers | 80% | 70% |
| Utils | 95% | 85% |
| **Overall** | **85%** | **80%** |

**Coverage Configuration (Maven)**:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
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
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
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
        </execution>
    </executions>
</plugin>
```

---

## 4. Frontend Testing

### 4.1 Component Tests (Vitest + RTL)

```typescript
// features/students/components/__tests__/StudentForm.test.tsx
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { StudentForm } from '../StudentForm';
import { vi } from 'vitest';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: false },
    mutations: { retry: false },
  },
});

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
);

describe('StudentForm', () => {
  it('renders all form fields', () => {
    render(<StudentForm />, { wrapper });

    expect(screen.getByLabelText(/first name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/last name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/date of birth/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/mobile/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/gender/i)).toBeInTheDocument();
  });

  it('shows validation errors for invalid input', async () => {
    const user = userEvent.setup();
    render(<StudentForm />, { wrapper });

    const submitButton = screen.getByRole('button', { name: /register student/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/first name must be at least 2 characters/i)).toBeInTheDocument();
      expect(screen.getByText(/mobile must be exactly 10 digits/i)).toBeInTheDocument();
    });
  });

  it('submits form with valid data', async () => {
    const user = userEvent.setup();
    const mockMutate = vi.fn();

    vi.mock('../hooks/useCreateStudent', () => ({
      useCreateStudent: () => ({ mutate: mockMutate, isPending: false }),
    }));

    render(<StudentForm />, { wrapper });

    await user.type(screen.getByLabelText(/first name/i), 'John');
    await user.type(screen.getByLabelText(/last name/i), 'Doe');
    await user.type(screen.getByLabelText(/mobile/i), '9876543210');

    const submitButton = screen.getByRole('button', { name: /register student/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(mockMutate).toHaveBeenCalledWith(
        expect.objectContaining({
          firstName: 'John',
          lastName: 'Doe',
          mobile: '9876543210',
        })
      );
    });
  });

  it('adds and removes guardians dynamically', async () => {
    const user = userEvent.setup();
    render(<StudentForm />, { wrapper });

    // Initially one guardian field
    expect(screen.getAllByText(/guardian/i)).toHaveLength(1);

    // Add guardian
    const addButton = screen.getByRole('button', { name: /add guardian/i });
    await user.click(addButton);

    expect(screen.getAllByText(/guardian/i)).toHaveLength(2);

    // Remove guardian
    const removeButtons = screen.getAllByRole('button', { name: /remove/i });
    await user.click(removeButtons[0]);

    expect(screen.getAllByText(/guardian/i)).toHaveLength(1);
  });
});
```

### 4.2 Hook Tests

```typescript
// features/students/hooks/__tests__/useStudents.test.ts
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useStudents } from '../useStudents';
import { studentsApi } from '../../api/studentsApi';
import { vi } from 'vitest';

vi.mock('../../api/studentsApi');

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: false },
  },
});

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
);

describe('useStudents', () => {
  beforeEach(() => {
    queryClient.clear();
  });

  it('fetches students successfully', async () => {
    const mockStudents = [
      { studentId: 1, firstName: 'John', lastName: 'Doe' },
      { studentId: 2, firstName: 'Jane', lastName: 'Smith' },
    ];

    vi.mocked(studentsApi.getStudents).mockResolvedValue({
      content: mockStudents,
      page: { number: 0, size: 20, totalElements: 2 },
    });

    const { result } = renderHook(() => useStudents({ status: 'ACTIVE' }), {
      wrapper,
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    expect(result.current.data).toEqual(mockStudents);
    expect(studentsApi.getStudents).toHaveBeenCalledWith({ status: 'ACTIVE' });
  });

  it('handles error when fetch fails', async () => {
    vi.mocked(studentsApi.getStudents).mockRejectedValue(new Error('Network error'));

    const { result } = renderHook(() => useStudents({ status: 'ACTIVE' }), {
      wrapper,
    });

    await waitFor(() => expect(result.current.isError).toBe(true));

    expect(result.current.error).toBeDefined();
  });
});
```

### 4.3 Integration Tests

```typescript
// features/students/__tests__/StudentFlow.integration.test.tsx
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import { StudentList } from '../components/StudentList';
import { studentsApi } from '../api/studentsApi';
import { vi } from 'vitest';

vi.mock('../api/studentsApi');

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: false } },
});

const AllProviders = ({ children }: { children: React.ReactNode }) => (
  <BrowserRouter>
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  </BrowserRouter>
);

describe('Student Management Flow', () => {
  it('completes full student registration flow', async () => {
    const user = userEvent.setup();

    const mockStudents = [
      { studentId: 1, firstName: 'Existing', lastName: 'Student', status: 'ACTIVE' },
    ];

    vi.mocked(studentsApi.getStudents).mockResolvedValue({
      content: mockStudents,
      page: { number: 0, size: 20, totalElements: 1 },
    });

    render(<StudentList />, { wrapper: AllProviders });

    await waitFor(() => {
      expect(screen.getByText('Existing Student')).toBeInTheDocument();
    });

    // Click "Add Student" button
    const addButton = screen.getByRole('button', { name: /add student/i });
    await user.click(addButton);

    // Should navigate to form (test navigation in E2E)
  });
});
```

### 4.4 Test Coverage Targets

| Type | Target Coverage | Minimum Coverage |
|------|-----------------|------------------|
| Components | 80% | 70% |
| Hooks | 85% | 75% |
| Utils | 90% | 80% |
| **Overall** | **80%** | **70%** |

---

## 5. Integration Testing

### 5.1 API Contract Tests (REST Assured)

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class StudentApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    @Autowired
    private StudentRepository studentRepository;

    private String baseUrl;
    private String authToken;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1";
        authToken = getAuthToken(); // Get JWT token
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("Should complete full student registration flow")
    void shouldCompleteFullStudentRegistrationFlow() {
        CreateStudentRequest request = createValidStudentRequest();

        // 1. Register student
        StudentResponse createdStudent = given()
            .header("Authorization", "Bearer " + authToken)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(baseUrl + "/students")
        .then()
            .statusCode(201)
            .body("studentCode", startsWith("STU-2025-"))
            .body("firstName", equalTo("John"))
            .body("status", equalTo("ACTIVE"))
            .extract()
            .as(StudentResponse.class);

        Long studentId = createdStudent.getStudentId();

        // 2. Get student by ID
        given()
            .header("Authorization", "Bearer " + authToken)
        .when()
            .get(baseUrl + "/students/" + studentId)
        .then()
            .statusCode(200)
            .body("studentId", equalTo(studentId.intValue()))
            .body("firstName", equalTo("John"));

        // 3. Update student
        UpdateStudentRequest updateRequest = new UpdateStudentRequest();
        updateRequest.setMobile("9999999999");

        given()
            .header("Authorization", "Bearer " + authToken)
            .contentType(ContentType.JSON)
            .body(updateRequest)
        .when()
            .put(baseUrl + "/students/" + studentId)
        .then()
            .statusCode(200)
            .body("mobile", equalTo("9999999999"));

        // 4. Search students
        given()
            .header("Authorization", "Bearer " + authToken)
            .queryParam("status", "ACTIVE")
        .when()
            .get(baseUrl + "/students")
        .then()
            .statusCode(200)
            .body("content.size()", greaterThan(0));
    }

    @Test
    @DisplayName("Should handle validation errors correctly")
    void shouldHandleValidationErrors() {
        CreateStudentRequest request = createInvalidStudentRequest();

        given()
            .header("Authorization", "Bearer " + authToken)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(baseUrl + "/students")
        .then()
            .statusCode(400)
            .body("type", containsString("validation-error"))
            .body("errors", hasSize(greaterThan(0)))
            .body("errors[0].field", notNullValue())
            .body("errors[0].message", notNullValue());
    }

    @Test
    @DisplayName("Should enforce authorization")
    void shouldEnforceAuthorization() {
        // Without token
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post(baseUrl + "/students")
        .then()
            .statusCode(401);

        // With insufficient permissions
        String readOnlyToken = getReadOnlyToken();

        given()
            .header("Authorization", "Bearer " + readOnlyToken)
            .contentType(ContentType.JSON)
            .body(createValidStudentRequest())
        .when()
            .post(baseUrl + "/students")
        .then()
            .statusCode(403);
    }
}
```

### 5.2 Payment Flow Integration Test

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentFlowIntegrationTest {

    @Test
    @DisplayName("Should complete payment recording flow")
    void shouldCompletePaymentRecordingFlow() {
        // 1. Create student
        Long studentId = createStudent();

        // 2. Generate fee journal
        generateFeeJournal(studentId, YearMonth.now());

        // 3. Get student's pending fees
        List<FeeJournal> pendingJournals = given()
            .header("Authorization", "Bearer " + authToken)
            .queryParam("status", "PENDING")
        .when()
            .get(baseUrl + "/students/" + studentId + "/fee-journals")
        .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList("journals", FeeJournal.class);

        assertThat(pendingJournals).isNotEmpty();

        // 4. Record payment
        RecordPaymentRequest paymentRequest = RecordPaymentRequest.builder()
            .studentId(studentId)
            .paymentAmount(new BigDecimal("2000.00"))
            .paymentMethod("CASH")
            .paymentDate(LocalDate.now())
            .journalAllocations(List.of(
                new JournalAllocation(pendingJournals.get(0).getJournalId(), new BigDecimal("2000.00"))
            ))
            .build();

        PaymentResponse payment = given()
            .header("Authorization", "Bearer " + authToken)
            .contentType(ContentType.JSON)
            .body(paymentRequest)
        .when()
            .post(baseUrl + "/payments")
        .then()
            .statusCode(201)
            .body("paymentAmount", equalTo(2000.00f))
            .body("receiptNumber", startsWith("REC-2025-"))
            .extract()
            .as(PaymentResponse.class);

        // 5. Verify receipt generated
        given()
            .header("Authorization", "Bearer " + authToken)
        .when()
            .get(baseUrl + "/receipts/" + payment.getReceiptNumber())
        .then()
            .statusCode(200)
            .body("receiptNumber", equalTo(payment.getReceiptNumber()))
            .body("totalAmount", equalTo(2000.00f));

        // 6. Verify fee journal updated
        given()
            .header("Authorization", "Bearer " + authToken)
        .when()
            .get(baseUrl + "/students/" + studentId + "/fee-journals")
        .then()
            .statusCode(200)
            .body("journals.find { it.status == 'PAID' }", notNullValue());
    }
}
```

---

## 6. End-to-End Testing

### 6.1 Playwright E2E Tests

```typescript
// e2e/student-registration.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Student Registration Flow', () => {
  test.beforeEach(async ({ page }) => {
    // Login
    await page.goto('http://localhost:3000/login');
    await page.fill('input[name="username"]', 'admin');
    await page.fill('input[name="password"]', 'password');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL('http://localhost:3000/');
  });

  test('should register a new student successfully', async ({ page }) => {
    // Navigate to student registration
    await page.click('text=Students');
    await page.click('text=Add Student');
    await expect(page).toHaveURL(/.*\/students\/new/);

    // Fill student information
    await page.fill('input[name="firstName"]', 'John');
    await page.fill('input[name="lastName"]', 'Doe');
    await page.fill('input[name="mobile"]', '9876543210');
    await page.selectOption('select[name="gender"]', 'MALE');
    await page.fill('input[name="email"]', 'john.doe@example.com');
    await page.fill('textarea[name="address"]', '123 Main Street, City, State');

    // Select date of birth
    await page.click('input[name="dateOfBirth"]');
    await page.selectOption('select[name="year"]', '2015');
    await page.selectOption('select[name="month"]', '5');
    await page.click('button:has-text("15")');

    // Fill guardian information
    await page.selectOption('select[name="guardians[0].relationship"]', 'FATHER');
    await page.fill('input[name="guardians[0].firstName"]', 'Robert');
    await page.fill('input[name="guardians[0].lastName"]', 'Doe');
    await page.fill('input[name="guardians[0].mobile"]', '9876543211');
    await page.check('input[name="guardians[0].isPrimary"]');

    // Submit form
    await page.click('button[type="submit"]');

    // Verify success
    await expect(page.locator('text=Student registered successfully')).toBeVisible();
    await expect(page).toHaveURL(/.*\/students\/\d+/);

    // Verify student details page
    await expect(page.locator('text=John Doe')).toBeVisible();
    await expect(page.locator('text=STU-2025-')).toBeVisible();
  });

  test('should show validation errors for invalid input', async ({ page }) => {
    await page.goto('http://localhost:3000/students/new');

    // Submit empty form
    await page.click('button[type="submit"]');

    // Verify validation errors
    await expect(page.locator('text=First name is required')).toBeVisible();
    await expect(page.locator('text=Mobile is required')).toBeVisible();

    // Fill invalid mobile
    await page.fill('input[name="mobile"]', '12345');
    await page.click('button[type="submit"]');
    await expect(page.locator('text=Mobile must be exactly 10 digits')).toBeVisible();
  });
});

test.describe('Payment Recording Flow', () => {
  test('should record payment successfully', async ({ page }) => {
    // Login
    await page.goto('http://localhost:3000/login');
    await page.fill('input[name="username"]', 'admin');
    await page.fill('input[name="password"]', 'password');
    await page.click('button[type="submit"]');

    // Navigate to payments
    await page.click('text=Payments');
    await expect(page).toHaveURL(/.*\/payments/);

    // Select student
    await page.fill('input[placeholder="Search student"]', 'John Doe');
    await page.click('text=John Doe - STU-2025-00001');

    // Fill payment details
    await page.fill('input[name="paymentAmount"]', '2000');
    await page.selectOption('select[name="paymentMethod"]', 'CASH');

    // Select fee allocations
    await page.check('input[type="checkbox"]:near(text="Tuition Fee")');
    await page.fill('input[name="journalAllocations[0].allocationAmount"]', '2000');

    // Submit payment
    await page.click('button:has-text("Record Payment")');

    // Verify success and receipt
    await expect(page.locator('text=Payment recorded successfully')).toBeVisible();
    await expect(page.locator('text=Receipt Number: REC-2025-')).toBeVisible();

    // Download receipt
    await page.click('button:has-text("Download Receipt")');
  });
});
```

### 6.2 Visual Regression Testing

```typescript
// e2e/visual-regression.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Visual Regression Tests', () => {
  test('student list page matches screenshot', async ({ page }) => {
    await page.goto('http://localhost:3000/students');
    await expect(page).toHaveScreenshot('student-list.png');
  });

  test('student form matches screenshot', async ({ page }) => {
    await page.goto('http://localhost:3000/students/new');
    await expect(page).toHaveScreenshot('student-form.png');
  });

  test('dashboard matches screenshot', async ({ page }) => {
    await page.goto('http://localhost:3000/');
    await expect(page).toHaveScreenshot('dashboard.png');
  });
});
```

---

## 7. Performance Testing

### 7.1 JMeter Load Test Plan

**Test Scenario**: 100 concurrent users creating students

```xml
<!-- student-registration-load-test.jmx -->
<jmeterTestPlan version="1.2">
  <hashTree>
    <TestPlan>
      <stringProp name="TestPlan.comments">Student Registration Load Test</stringProp>
      <elementProp name="TestPlan.user_defined_variables">
        <collectionProp name="Arguments.arguments"/>
      </elementProp>
    </TestPlan>

    <ThreadGroup>
      <stringProp name="ThreadGroup.num_threads">100</stringProp>
      <stringProp name="ThreadGroup.ramp_time">60</stringProp>
      <stringProp name="ThreadGroup.duration">300</stringProp>
    </ThreadGroup>

    <!-- Login -->
    <HTTPSamplerProxy>
      <stringProp name="HTTPSampler.domain">api.school.com</stringProp>
      <stringProp name="HTTPSampler.port">443</stringProp>
      <stringProp name="HTTPSampler.protocol">https</stringProp>
      <stringProp name="HTTPSampler.path">/api/v1/auth/login</stringProp>
      <stringProp name="HTTPSampler.method">POST</stringProp>
    </HTTPSamplerProxy>

    <!-- Create Student -->
    <HTTPSamplerProxy>
      <stringProp name="HTTPSampler.path">/api/v1/students</stringProp>
      <stringProp name="HTTPSampler.method">POST</stringProp>
    </HTTPSamplerProxy>
  </hashTree>
</jmeterTestPlan>
```

### 7.2 k6 Performance Test

```javascript
// k6-load-test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '1m', target: 20 },  // Ramp up to 20 users
    { duration: '3m', target: 100 }, // Ramp up to 100 users
    { duration: '5m', target: 100 }, // Hold at 100 users
    { duration: '1m', target: 0 },   // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<200'], // 95% of requests < 200ms
    http_req_failed: ['rate<0.01'],   // Error rate < 1%
  },
};

const BASE_URL = 'https://api.school.com/api/v1';

export function setup() {
  // Login and get token
  const loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
    username: 'test_user',
    password: 'test_password',
  }), {
    headers: { 'Content-Type': 'application/json' },
  });

  return { authToken: loginRes.json('accessToken') };
}

export default function (data) {
  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${data.authToken}`,
  };

  // Get students list
  const getRes = http.get(`${BASE_URL}/students?page=0&size=20`, { headers });
  check(getRes, {
    'students list status is 200': (r) => r.status === 200,
    'students list response time < 200ms': (r) => r.timings.duration < 200,
  });

  sleep(1);

  // Create student
  const createRes = http.post(`${BASE_URL}/students`, JSON.stringify({
    firstName: 'Test',
    lastName: 'Student',
    dateOfBirth: '2015-01-01',
    mobile: `98765${__VU}${__ITER}`.substring(0, 10),
    gender: 'MALE',
    admissionDate: '2025-04-01',
    guardians: [{
      relationship: 'FATHER',
      firstName: 'Test',
      lastName: 'Guardian',
      mobile: `99999${__VU}${__ITER}`.substring(0, 10),
      isPrimary: true,
    }],
  }), { headers });

  check(createRes, {
    'create student status is 201': (r) => r.status === 201,
    'create student response time < 500ms': (r) => r.timings.duration < 500,
  });

  sleep(2);
}
```

**Performance Targets**:
- API response time (P95): < 200ms
- Error rate: < 1%
- Throughput: 100 RPS
- Concurrent users: 100

---

## 8. Security Testing

### 8.1 OWASP ZAP Security Scan

```yaml
# zap-scan-config.yaml
---
env:
  contexts:
    - name: school-sms
      urls:
        - https://api.school.com/api/v1/
      includePaths:
        - https://api.school.com/api/v1/.*
      excludePaths:
        - https://api.school.com/api/v1/health
      authentication:
        method: jwt
        parameters:
          token: ${JWT_TOKEN}
      sessionManagement:
        method: jwt
      technology:
        include:
          - Java
          - Spring Boot
          - PostgreSQL

jobs:
  - type: passiveScan-config
    rules: []

  - type: spider
    url: https://api.school.com/api/v1/
    maxDuration: 5

  - type: activeScan
    context: school-sms
    policy: API-scan
    maxRuleDurationInMins: 5
    maxScanDurationInMins: 20

  - type: report
    template: traditional-json
    reportFile: /zap/wrk/zap-report.json
```

### 8.2 Security Test Checklist

| Security Test | Tool | Frequency |
|---------------|------|-----------|
| SQL Injection | OWASP ZAP | Every sprint |
| XSS | OWASP ZAP | Every sprint |
| CSRF | Manual testing | Every sprint |
| Authentication | Manual testing | Every sprint |
| Authorization | Automated tests | Every commit |
| Sensitive Data Exposure | Code review | Every PR |
| Dependency Vulnerabilities | Snyk, OWASP Dependency-Check | Daily |
| TLS Configuration | SSL Labs | Every release |
| Security Headers | Mozilla Observatory | Every release |

---

## 9. Test Data Management

### 9.1 Test Data Builders

```java
public class StudentTestDataBuilder {

    private String firstName = "John";
    private String lastName = "Doe";
    private LocalDate dateOfBirth = LocalDate.now().minusYears(10);
    private String mobile = "9876543210";
    private Gender gender = Gender.MALE;
    private StudentStatus status = StudentStatus.ACTIVE;

    public StudentTestDataBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public StudentTestDataBuilder withAge(int age) {
        this.dateOfBirth = LocalDate.now().minusYears(age);
        return this;
    }

    public StudentTestDataBuilder inactive() {
        this.status = StudentStatus.INACTIVE;
        return this;
    }

    public Student build() {
        return Student.builder()
            .firstName(firstName)
            .lastName(lastName)
            .dateOfBirth(dateOfBirth)
            .mobile(mobile)
            .gender(gender)
            .status(status)
            .admissionDate(LocalDate.now())
            .build();
    }
}

// Usage
Student student = new StudentTestDataBuilder()
    .withFirstName("Jane")
    .withAge(8)
    .build();
```

### 9.2 Database Fixtures

```java
@Component
public class TestDataFixtures {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Transactional
    public Student createStudent(String firstName, String mobile) {
        Student student = Student.builder()
            .studentCode(generateStudentCode())
            .firstName(firstName)
            .lastName("Test")
            .dateOfBirth(LocalDate.now().minusYears(10))
            .mobile(mobile)
            .status(StudentStatus.ACTIVE)
            .admissionDate(LocalDate.now())
            .build();

        return studentRepository.save(student);
    }

    @Transactional
    public Class createClass(String className, String section) {
        AcademicYear academicYear = createAcademicYear("2025-2026");

        Class classEntity = Class.builder()
            .className(className)
            .section(section)
            .academicYear(academicYear)
            .maxCapacity(40)
            .currentEnrollment(0)
            .isActive(true)
            .build();

        return classRepository.save(classEntity);
    }
}
```

---

## 10. Continuous Testing

### 10.1 CI/CD Pipeline

```yaml
# .github/workflows/ci.yml
name: CI

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

      - name: Cache Maven packages
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}

      - name: Run unit tests
        run: mvn test

      - name: Run integration tests
        run: mvn verify -P integration-tests

      - name: Generate coverage report
        run: mvn jacoco:report

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3

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

      - name: Run unit tests
        run: npm test -- --coverage

      - name: Run E2E tests
        run: npm run test:e2e

  security-scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Run OWASP Dependency Check
        uses: dependency-check/Dependency-Check_Action@main
        with:
          project: 'school-sms'
          path: '.'
          format: 'HTML'

      - name: Run Snyk security scan
        uses: snyk/actions/maven@master
        env:
          SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
```

### 10.2 Test Reporting

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.1.2</version>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
        </includes>
        <reportFormat>html</reportFormat>
    </configuration>
</plugin>

<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>report</id>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## Document Control

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-11-10 | QA Team | Initial version |

