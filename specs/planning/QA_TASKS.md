# Quality Assurance & Testing Tasks - School Management System

**Version**: 1.0
**Date**: November 11, 2025
**Target Audience**: QA & Test Automation Team

---

## Testing Strategy Overview

The QA approach follows the **Testing Pyramid** with:
- **60% Unit Tests** - Fast, isolated component testing
- **30% Integration Tests** - API contracts, database interactions
- **10% E2E Tests** - Critical user journeys and workflows

---

## Task Naming Convention

Tasks follow this format: `QA-S[Sprint]-[TaskNumber]` (e.g., QA-S1-01)

Story points use Fibonacci scale: 1, 2, 3, 5, 8, 13

---

## Sprint 1-2: Test Infrastructure & Planning
**Duration**: Nov 11 - Dec 9, 2025
**Sprint Goal**: Set up testing environments, frameworks, and create test plans
**Team Capacity**: 25 story points

---

### QA-S1-01: Set Up Testing Infrastructure

**Story Points**: 3
**Assigned To**: Test Automation Engineer
**Description**: Configure testing frameworks, CI/CD integration, and test environments.

**Acceptance Criteria**:
- [ ] JUnit 5 and Mockito configured for backend unit tests
- [ ] Vitest and React Testing Library configured for frontend
- [ ] TestContainers configured for containerized database testing
- [ ] Postman/REST Assured configured for API testing
- [ ] Test database (PostgreSQL) isolated from dev database
- [ ] CI/CD pipeline runs tests on each commit
- [ ] Test reports generated and uploaded
- [ ] Code coverage reports (JaCoCo for backend, LCOV for frontend)
- [ ] Test failure notifications configured
- [ ] Performance test environment set up (JMeter)

**Technical Requirements**:
```xml
<!-- pom.xml for Backend Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>

<!-- Maven Plugins for Test Reporting -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
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

**Frontend Testing Setup**:
```javascript
// vitest.config.ts
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov'],
      exclude: [
        'node_modules/',
        'src/test/',
        '**/*.d.ts',
      ],
    },
  },
});

// src/test/setup.ts
import { expect, afterEach } from 'vitest';
import { cleanup } from '@testing-library/react';
import '@testing-library/jest-dom';

afterEach(() => {
  cleanup();
});
```

**CI/CD Pipeline Integration**:
```yaml
# .github/workflows/test.yml
name: Tests

on: [push, pull_request]

jobs:
  test-backend:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:18
        env:
          POSTGRES_PASSWORD: postgres
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Run Tests
        run: mvn clean test

      - name: Upload Coverage
        uses: codecov/codecov-action@v3
        with:
          files: ./target/site/jacoco/jacoco.xml

  test-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '18'

      - name: Install
        run: npm ci

      - name: Run Tests
        run: npm run test

      - name: Upload Coverage
        uses: codecov/codecov-action@v3
```

**Implementation Guidance**:
1. Use TestContainers for real database testing
2. Configure separate test databases
3. Set up test data factories
4. Implement test environment variables
5. Configure test result reporting
6. Set up code coverage monitoring
7. Create CI/CD test gates

**Definition of Done**:
- [ ] All testing frameworks installed and working
- [ ] CI/CD pipeline executing tests
- [ ] Code coverage reports generating
- [ ] Test failures blocking builds
- [ ] Documentation for running tests locally

**Dependencies**: BE-S1-01, FE-S1-01

---

### QA-S1-02: Create Test Plan & Strategy Document

**Story Points**: 3
**Assigned To**: QA Lead
**Description**: Document comprehensive test plan covering all modules with test cases and scenarios.

**Acceptance Criteria**:
- [ ] Overall test strategy documented
- [ ] Test scope and out-of-scope items defined
- [ ] Test case templates created
- [ ] Entry and exit criteria for testing
- [ ] Defect severity and priority levels defined
- [ ] Test data management strategy documented
- [ ] Test environment requirements documented
- [ ] Test schedule and timeline created
- [ ] Risk assessment for testing completed
- [ ] Resource allocation and responsibilities documented
- [ ] Traceability matrix (Requirements → Test Cases) created
- [ ] Regression test strategy documented

**Test Plan Content**:
```markdown
# Test Plan - School Management System

## 1. Scope
- Student Management Module
- Class Management Module
- Fee Management Module
- Payment & Receipt Module
- Configuration Module
- Reporting Module
- Authentication & Authorization

## 2. Test Strategy
- Unit Testing: 60%
- Integration Testing: 30%
- E2E Testing: 10%

## 3. Entry Criteria
- Requirements document approved
- Architecture document approved
- Development environment ready
- Test framework installed

## 4. Exit Criteria
- All critical bugs fixed
- Code coverage >= 80% (backend), >= 70% (frontend)
- All test cases executed
- UAT successful

## 5. Test Cases by Module

### 5.1 Student Management
| ID | Test Case | Precondition | Steps | Expected Result |
|---|-----------|--------------|-------|-----------------|
| TC-STU-01 | Create valid student | User logged in | 1. Go to Add Student 2. Fill form 3. Click Submit | Student created successfully |
| TC-STU-02 | Validate age (3-18 years) | Student form open | Enter DOB | Error message for age < 3 or > 18 |
| TC-STU-03 | Validate mobile uniqueness | Student form open | Enter duplicate mobile | Error: Mobile already exists |

### 5.2 Class Management
| ID | Test Case | Precondition | Steps | Expected Result |
|---|-----------|--------------|-------|-----------------|
| TC-CLS-01 | Create class | Admin logged in | 1. Go to Classes 2. Add Class 3. Submit | Class created |
| TC-CLS-02 | Validate class capacity | Class with 40 students | Enroll 41st student | Error: Class at capacity |

## 6. Test Data Requirements
- 2500 active students
- 100 classes (10 levels × 10 sections)
- Various fee structures (tuition, library, computer, sports)
- Payment history for past 2 years
- Multiple user roles (admin, principal, office staff, accounts manager, auditor)

## 7. Test Environment
- Test Database: PostgreSQL 18 (isolated)
- Test API: Spring Boot on localhost:8080
- Test Frontend: React dev server on localhost:3000
- Postman/REST Assured for API testing
- Selenium/Playwright for E2E testing

## 8. Defect Severity Levels
- Critical: System crash, data loss, security breach
- High: Major feature not working, user cannot perform key task
- Medium: Feature partially working, workaround exists
- Low: Minor UI issue, typo, non-critical functionality

## 9. Risks & Mitigation
| Risk | Impact | Probability | Mitigation |
|------|--------|------------|-----------|
| Test data setup takes too long | Delays testing | High | Automate data setup |
| Database performance issues | Tests timeout | Medium | Use test database optimized for tests |
| API changes break tests | Tests fail unexpectedly | Medium | Implement API contract testing |
```

**Traceability Matrix**:
```
Requirement ID → Feature → Test Case IDs
REQ-STU-001: Student Registration
├── Create Student
│   └── TC-STU-01, TC-STU-02, TC-STU-03, TC-STU-04
├── Validate Age
│   └── TC-STU-02, TC-STU-05
└── Store Guardian Info
    └── TC-STU-06, TC-STU-07
```

**Implementation Guidance**:
1. Create test cases for every requirement
2. Include positive, negative, and edge case scenarios
3. Define test data requirements clearly
4. Document test environment setup
5. Create risk assessment for testing
6. Define metrics for test success
7. Plan regression testing strategy

**Definition of Done**:
- [ ] Test plan document created and reviewed
- [ ] Test cases written for all modules
- [ ] Traceability matrix created
- [ ] Test data strategy documented
- [ ] Risk assessment completed
- [ ] Team trained on test plan

**Dependencies**: Requirements approved

---

### QA-S1-03: Create Test Data Factory & Fixtures

**Story Points**: 3
**Assigned To**: Test Automation Engineer
**Description**: Build test data generation utilities and reusable test fixtures for consistent test data.

**Acceptance Criteria**:
- [ ] Backend test data factory created (TestDataBuilder pattern)
- [ ] Frontend mock data generators created
- [ ] Test fixtures for common scenarios
- [ ] Seed data SQL scripts for test database
- [ ] API test data setup endpoints
- [ ] Utility methods for creating entities
- [ ] Reset test database between test runs
- [ ] Consistent test data across all tests
- [ ] Performance optimized data setup

**Technical Requirements**:
```java
// Backend Test Data Factory
@Component
public class StudentTestDataFactory {

    public StudentBuilder createStudentBuilder() {
        return new StudentBuilder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2010, 1, 15))
            .gender(Gender.MALE)
            .mobile("9876543210")
            .email("john@example.com")
            .status(StudentStatus.ACTIVE)
            .admissionDate(LocalDate.now());
    }

    public Student createValidStudent() {
        return createStudentBuilder().build();
    }

    public Student createStudentWithGuardians() {
        Student student = createValidStudent();
        Guardian father = Guardian.builder()
            .student(student)
            .relationship(Relationship.FATHER)
            .firstName("Robert")
            .lastName("Doe")
            .mobile("9876543211")
            .isPrimary(true)
            .build();
        student.getGuardians().add(father);
        return student;
    }

    public Student createGraduatedStudent() {
        Student student = createValidStudent();
        student.setStatus(StudentStatus.GRADUATED);
        return student;
    }
}

// Builder Pattern
public class StudentBuilder {
    private String firstName = "John";
    private String lastName = "Doe";
    private LocalDate dateOfBirth = LocalDate.of(2010, 1, 15);
    private Gender gender = Gender.MALE;
    private String mobile = "9876543210";
    private String email = "john@example.com";
    private StudentStatus status = StudentStatus.ACTIVE;
    private LocalDate admissionDate = LocalDate.now();

    public StudentBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    // ... other builder methods ...

    public Student build() {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        // ... set other fields ...
        return student;
    }
}

// Test Base Class
@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseTestClass {
    @Autowired
    protected StudentRepository studentRepository;

    @Autowired
    protected StudentTestDataFactory testDataFactory;

    @BeforeEach
    public void setUp() {
        // Clear data before each test
        studentRepository.deleteAll();
    }

    protected void createTestStudents(int count) {
        for (int i = 0; i < count; i++) {
            Student student = testDataFactory.createStudentBuilder()
                .mobile(String.format("987654321%d", i))
                .email(String.format("student%d@example.com", i))
                .build();
            studentRepository.save(student);
        }
    }
}
```

**Frontend Test Fixtures**:
```typescript
// test/fixtures/student.ts
export const createMockStudent = (overrides?: Partial<Student>): Student => ({
  studentId: 1,
  studentCode: 'STU-2025-00001',
  firstName: 'John',
  lastName: 'Doe',
  dateOfBirth: '2010-01-15',
  gender: 'MALE',
  mobile: '9876543210',
  email: 'john@example.com',
  status: 'ACTIVE',
  admissionDate: '2025-04-01',
  ...overrides,
});

export const createMockStudentList = (count: number): Student[] => {
  return Array.from({ length: count }, (_, i) =>
    createMockStudent({
      studentId: i + 1,
      firstName: `Student${i + 1}`,
      mobile: `987654321${i}`,
    })
  );
};

// test/setup.ts
export const mockApiResponses = {
  getStudents: (overrides?: any) => ({
    content: createMockStudentList(10),
    page: {
      number: 0,
      size: 10,
      totalElements: 100,
      totalPages: 10,
    },
    ...overrides,
  }),

  createStudent: (data: any) => ({
    ...data,
    studentId: 1,
    studentCode: 'STU-2025-00001',
  }),
};
```

**Test Database Seed Script**:
```sql
-- test/data/seed.sql
TRUNCATE TABLE students CASCADE;
TRUNCATE TABLE guardians CASCADE;
TRUNCATE TABLE classes CASCADE;
TRUNCATE TABLE enrollments CASCADE;
TRUNCATE TABLE fee_structures CASCADE;
TRUNCATE TABLE fee_journals CASCADE;

-- Insert test users
INSERT INTO users (username, password_hash, full_name, email, role)
VALUES ('admin', '$2a$10$...', 'Admin User', 'admin@test.com', 'ADMIN');

-- Insert academic year
INSERT INTO academic_years (year_code, start_date, end_date, is_current)
VALUES ('2025-2026', '2025-04-01', '2026-03-31', true);

-- Insert classes (10 classes × 5 sections)
INSERT INTO classes (class_name, section, academic_year_id, max_capacity, created_by)
SELECT generate_subscripts(ARRAY[1,2,3,4,5,6,7,8,9,10], 1) as class_num,
       generate_subscripts(ARRAY['A','B','C','D','E'], 1)::text as section,
       1 as academic_year_id,
       40 as max_capacity,
       1 as created_by;

-- Insert test data
-- ...
```

**Implementation Guidance**:
1. Use builder pattern for flexible test data
2. Create reusable fixtures for common scenarios
3. Automate test data setup in CI/CD
4. Use factories for database entities
5. Create API endpoints for test data management
6. Reset database between test runs
7. Document all test data assumptions

**Definition of Done**:
- [ ] Test data factories created and documented
- [ ] Test fixtures working for all modules
- [ ] Seed scripts functional
- [ ] Data setup optimized for performance
- [ ] Utilities easy to use and extend
- [ ] Unit tests for data builders

**Dependencies**: BE-S1-03, FE-S1-01

---

## Sprint 3+: Module-Specific Testing

### QA-S3-01: Backend Unit Tests - Student Module

**Story Points**: 5
**Assigned To**: Test Automation Engineer
**Description**: Write comprehensive unit tests for Student entity, repository, and service layer.

**Acceptance Criteria**:
- [ ] Entity validation tests (age, mobile, etc.)
- [ ] Repository query tests
- [ ] Service layer business logic tests
- [ ] Encryption/decryption tests
- [ ] Student code generation tests
- [ ] Status change validation tests
- [ ] Edge case handling tests
- [ ] 80%+ code coverage for module
- [ ] All tests passing
- [ ] Tests run in < 10 seconds

**Example Unit Tests**:
```java
// Student Entity Tests
@ExtendWith(MockitoExtension.class)
public class StudentEntityTest {

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
    }

    @Test
    void shouldValidateAge3to18() {
        // Valid ages
        assertDoesNotThrow(() -> validateAge(LocalDate.of(2010, 1, 1)));
        assertDoesNotThrow(() -> validateAge(LocalDate.of(2022, 1, 1)));

        // Invalid ages
        assertThrows(ValidationException.class, () -> validateAge(LocalDate.of(2024, 1, 1))); // Too young
        assertThrows(ValidationException.class, () -> validateAge(LocalDate.of(2000, 1, 1))); // Too old
    }

    @Test
    void shouldRejectDuplicateMobile() {
        student.setMobile("9876543210");
        // Validation should reject duplicate
    }

    @Test
    void shouldGenerateUniqueStudentCode() {
        Student s1 = new Student();
        Student s2 = new Student();

        assertNotEquals(s1.getStudentCode(), s2.getStudentCode());
    }
}

// Student Repository Tests
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindStudentByCode() {
        Student student = StudentTestDataFactory.createValidStudent();
        entityManager.persistAndFlush(student);

        Optional<Student> found = repository.findByStudentCode(student.getStudentCode());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(student.getId());
    }

    @Test
    void shouldFindActiveStudents() {
        StudentTestDataFactory factory = new StudentTestDataFactory();

        Student active1 = factory.createValidStudent();
        Student active2 = factory.createValidStudent();
        Student inactive = factory.createValidStudent();
        inactive.setStatus(StudentStatus.INACTIVE);

        entityManager.persistAndFlush(active1);
        entityManager.persistAndFlush(active2);
        entityManager.persistAndFlush(inactive);

        List<Student> activeStudents = repository.findByStatusIn(
            List.of(StudentStatus.ACTIVE));

        assertThat(activeStudents).hasSize(2);
        assertThat(activeStudents).extracting("status")
            .containsOnly(StudentStatus.ACTIVE);
    }

    @Test
    void shouldEnforceMobileUniqueness() {
        Student s1 = StudentTestDataFactory.createValidStudent();
        Student s2 = StudentTestDataFactory.createValidStudent();
        s2.setMobile(s1.getMobile());

        entityManager.persistAndFlush(s1);
        assertThrows(DataIntegrityViolationException.class, () ->
            entityManager.persistAndFlush(s2));
    }
}

// Student Service Tests
@SpringBootTest
@AutoConfigureMockMvc
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private EncryptionService encryptionService;

    @Test
    void shouldCreateStudentWithValidData() {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(2010, 1, 1));
        request.setMobile("9876543210");
        request.setAdmissionDate(LocalDate.now());

        Student createdStudent = studentService.createStudent(request);

        assertNotNull(createdStudent.getStudentCode());
        assertTrue(createdStudent.getStudentCode().startsWith("STU-"));
        verify(studentRepository).save(any());
    }

    @Test
    void shouldRejectAgeOutOfRange() {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setDateOfBirth(LocalDate.of(2024, 1, 1)); // Age < 3

        assertThrows(ValidationException.class, () ->
            studentService.createStudent(request));
    }

    @Test
    void shouldPublishStudentCreatedEvent() {
        // Use ArgumentCaptor to verify event was published
        ArgumentCaptor<StudentCreatedEvent> captor =
            ArgumentCaptor.forClass(StudentCreatedEvent.class);

        Student student = studentService.createStudent(request);

        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().getStudent()).isEqualTo(student);
    }
}
```

**Implementation Guidance**:
1. Test all public methods
2. Test edge cases and boundary conditions
3. Test error scenarios
4. Mock external dependencies
5. Use parameterized tests for multiple scenarios
6. Test concurrency issues
7. Verify audit logging
8. Test transaction boundaries

**Definition of Done**:
- [ ] All tests passing
- [ ] 80%+ code coverage for module
- [ ] Tests run quickly (< 10 seconds)
- [ ] Tests documented
- [ ] Coverage report generated
- [ ] CI/CD integration verified

**Dependencies**: BE-S3-03

---

### QA-S3-02: Frontend Component Tests - Student Module

**Story Points**: 3
**Assigned To**: Test Automation Engineer
**Description**: Write comprehensive tests for Student React components.

**Acceptance Criteria**:
- [ ] StudentList component tests (render, filter, pagination)
- [ ] StudentForm component tests (validation, submission)
- [ ] StudentDetail component tests
- [ ] Hook tests for API integration
- [ ] Error handling tests
- [ ] Loading state tests
- [ ] 70%+ code coverage for components
- [ ] All tests passing
- [ ] Snapshot tests for UI components

**Example Component Tests**:
```typescript
// tests/components/StudentList.test.tsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import StudentList from '../../pages/student/StudentList';
import * as api from '../../api/hooks/useStudents';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: false },
  },
});

const renderStudentList = () => {
  return render(
    <QueryClientProvider client={queryClient}>
      <StudentList />
    </QueryClientProvider>
  );
};

describe('StudentList', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('should render student list with data', async () => {
    const mockStudents = [
      createMockStudent({ studentId: 1, firstName: 'John' }),
      createMockStudent({ studentId: 2, firstName: 'Jane' }),
    ];

    vi.spyOn(api, 'useStudents').mockReturnValue({
      data: { content: mockStudents, page: { totalElements: 2 } },
      isLoading: false,
      isError: false,
    } as any);

    renderStudentList();

    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('Jane Doe')).toBeInTheDocument();
  });

  test('should show loading state', () => {
    vi.spyOn(api, 'useStudents').mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
    } as any);

    renderStudentList();

    expect(screen.getByText('Loading...')).toBeInTheDocument();
  });

  test('should show error state with retry', async () => {
    const mockRefetch = vi.fn();

    vi.spyOn(api, 'useStudents').mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
      refetch: mockRefetch,
    } as any);

    renderStudentList();

    expect(screen.getByText(/Failed to load students/i)).toBeInTheDocument();

    const retryButton = screen.getByText('Retry');
    await userEvent.click(retryButton);

    expect(mockRefetch).toHaveBeenCalled();
  });

  test('should filter students by status', async () => {
    const mockStudents = [
      createMockStudent({ status: 'ACTIVE' }),
      createMockStudent({ status: 'INACTIVE' }),
    ];

    const mockUseStudents = vi.spyOn(api, 'useStudents').mockReturnValue({
      data: { content: mockStudents },
      isLoading: false,
      isError: false,
    } as any);

    renderStudentList();

    const statusFilter = screen.getByDisplayValue('All Status');
    await userEvent.selectOption(statusFilter, 'ACTIVE');

    await waitFor(() => {
      expect(mockUseStudents).toHaveBeenCalledWith(
        expect.objectContaining({ status: 'ACTIVE' })
      );
    });
  });

  test('should paginate students', async () => {
    const mockUseStudents = vi.spyOn(api, 'useStudents').mockReturnValue({
      data: {
        content: createMockStudentList(20),
        page: { totalElements: 100, totalPages: 5 },
      },
      isLoading: false,
      isError: false,
    } as any);

    renderStudentList();

    const nextButton = screen.getByText('Next');
    await userEvent.click(nextButton);

    await waitFor(() => {
      expect(mockUseStudents).toHaveBeenCalledWith(
        expect.objectContaining({ page: 1 })
      );
    });
  });

  test('should navigate to student detail on row click', async () => {
    const mockNavigate = vi.fn();
    vi.mock('react-router-dom', () => ({
      useNavigate: () => mockNavigate,
    }));

    const mockStudents = [createMockStudent({ studentId: 1 })];

    vi.spyOn(api, 'useStudents').mockReturnValue({
      data: { content: mockStudents },
      isLoading: false,
      isError: false,
    } as any);

    renderStudentList();

    const row = screen.getByText('John Doe');
    await userEvent.click(row);

    // Navigation should be triggered
  });
});

// tests/components/StudentForm.test.tsx
describe('StudentForm', () => {
  test('should validate required fields', async () => {
    render(<StudentForm />);

    const submitButton = screen.getByText(/Create Student/i);
    await userEvent.click(submitButton);

    expect(screen.getByText('First name is required')).toBeInTheDocument();
    expect(screen.getByText('Last name is required')).toBeInTheDocument();
  });

  test('should validate age (3-18 years)', async () => {
    render(<StudentForm />);

    const dobInput = screen.getByLabelText('Date of Birth');
    await userEvent.type(dobInput, '2024-01-01'); // Less than 3 years old

    await userEvent.click(screen.getByText(/Create Student/i));

    expect(screen.getByText('Age must be between 3 and 18 years')).toBeInTheDocument();
  });

  test('should submit valid form', async () => {
    const mockCreateStudent = vi.fn();
    vi.spyOn(api, 'useCreateStudent').mockReturnValue({
      mutateAsync: mockCreateStudent,
    } as any);

    render(<StudentForm />);

    // Fill form
    await userEvent.type(screen.getByLabelText('First Name'), 'John');
    await userEvent.type(screen.getByLabelText('Last Name'), 'Doe');
    await userEvent.type(screen.getByLabelText('Date of Birth'), '2010-01-01');
    await userEvent.selectOption(screen.getByLabelText('Gender'), 'MALE');
    await userEvent.type(screen.getByLabelText('Mobile Number'), '9876543210');
    await userEvent.type(screen.getByLabelText('Admission Date'), '2025-04-01');

    await userEvent.click(screen.getByText(/Create Student/i));

    await waitFor(() => {
      expect(mockCreateStudent).toHaveBeenCalled();
    });
  });
});

// tests/hooks/useStudents.test.ts
describe('useStudents', () => {
  test('should fetch students', async () => {
    const mockData = { content: createMockStudentList(10) };

    vi.spyOn(api, 'client').get = vi.fn().mockResolvedValue(mockData);

    const { result } = renderHook(() =>
      useStudents({ search: '', page: 0, size: 20 }),
      { wrapper: QueryClientProvider }
    );

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
      expect(result.current.data).toEqual(mockData);
    });
  });

  test('should handle errors gracefully', async () => {
    vi.spyOn(api, 'client').get = vi.fn()
      .mockRejectedValue(new Error('Network error'));

    const { result } = renderHook(() =>
      useStudents({ page: 0 }),
      { wrapper: QueryClientProvider }
    );

    await waitFor(() => {
      expect(result.current.isError).toBe(true);
    });
  });
});
```

**Implementation Guidance**:
1. Test component rendering with different props
2. Test user interactions (clicks, typing, etc.)
3. Mock API calls using vi.mock or MSW
4. Test loading, error, and success states
5. Test form validation and submission
6. Test navigation and routing
7. Use accessibility queries for element selection
8. Test accessibility features

**Definition of Done**:
- [ ] Component tests written and passing
- [ ] 70%+ code coverage for components
- [ ] Mocks properly configured
- [ ] Async operations handled correctly
- [ ] User interactions tested
- [ ] Error scenarios covered
- [ ] Tests run quickly (< 30 seconds)

**Dependencies**: FE-S3-01, FE-S3-02

---

### QA-S3-03: API Integration Tests - Student Endpoints

**Story Points**: 5
**Assigned To**: Test Automation Engineer
**Description**: Write comprehensive integration tests for Student REST API endpoints.

**Acceptance Criteria**:
- [ ] POST /api/v1/students endpoint tests
- [ ] GET /api/v1/students endpoint tests
- [ ] GET /api/v1/students/{id} endpoint tests
- [ ] PUT /api/v1/students/{id} endpoint tests
- [ ] PATCH /api/v1/students/{id}/status endpoint tests
- [ ] Authentication/authorization tests
- [ ] Request validation tests
- [ ] Response format verification
- [ ] Error handling tests
- [ ] Performance under load tests
- [ ] All tests passing

**Example Integration Tests**:
```java
// StudentControllerIntegrationTest.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class StudentControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "OFFICE_STAFF")
    void shouldCreateStudent() {
        CreateStudentRequest request = CreateStudentRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2010, 1, 15))
            .gender(Gender.MALE)
            .mobile("9876543210")
            .email("john@example.com")
            .admissionDate(LocalDate.now())
            .guardians(Set.of(
                GuardianRequest.builder()
                    .firstName("Robert")
                    .lastName("Doe")
                    .relationship(Relationship.FATHER)
                    .mobile("9876543211")
                    .isPrimary(true)
                    .build()
            ))
            .build();

        webTestClient.post()
            .uri("/api/v1/students")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated()
            .expectBodyList(StudentResponseDTO.class)
            .hasSize(1)
            .consumeWith(response -> {
                StudentResponseDTO student = response.getResponseBody().get(0);
                assertThat(student.getStudentCode()).startsWith("STU-");
                assertThat(student.getFirstName()).isEqualTo("John");
            });
    }

    @Test
    void shouldRejectInvalidAge() {
        CreateStudentRequest request = CreateStudentRequest.builder()
            .firstName("Baby")
            .lastName("Person")
            .dateOfBirth(LocalDate.now().minusYears(1)) // Age < 3
            .gender(Gender.MALE)
            .mobile("9876543210")
            .admissionDate(LocalDate.now())
            .build();

        webTestClient.post()
            .uri("/api/v1/students")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.errors[0].message").isEqualTo("Age must be between 3 and 18 years");
    }

    @Test
    void shouldRejectDuplicateMobile() {
        Student existingStudent = StudentTestDataFactory.createValidStudent();
        studentRepository.save(existingStudent);

        CreateStudentRequest request = CreateStudentRequest.builder()
            .firstName("Jane")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2010, 1, 15))
            .gender(Gender.FEMALE)
            .mobile(existingStudent.getMobile()) // Duplicate
            .admissionDate(LocalDate.now())
            .build();

        webTestClient.post()
            .uri("/api/v1/students")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isConflict();
    }

    @Test
    void shouldGetStudentById() {
        Student student = StudentTestDataFactory.createValidStudent();
        Student saved = studentRepository.save(student);

        webTestClient.get()
            .uri("/api/v1/students/{id}", saved.getId())
            .exchange()
            .expectStatus().isOk()
            .expectBody(StudentResponseDTO.class)
            .consumeWith(response -> {
                StudentResponseDTO dto = response.getResponseBody();
                assertThat(dto.getStudentId()).isEqualTo(saved.getId());
                assertThat(dto.getFirstName()).isEqualTo(saved.getFirstName());
            });
    }

    @Test
    void shouldUpdateStudent() {
        Student student = StudentTestDataFactory.createValidStudent();
        Student saved = studentRepository.save(student);

        UpdateStudentRequest request = UpdateStudentRequest.builder()
            .firstName("Updated")
            .lastName("Name")
            .mobile("9999999999")
            .status(StudentStatus.ACTIVE)
            .build();

        webTestClient.put()
            .uri("/api/v1/students/{id}", saved.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void shouldSearchStudentsByStatus() {
        createTestStudents(5, StudentStatus.ACTIVE);
        createTestStudents(3, StudentStatus.INACTIVE);

        webTestClient.get()
            .uri("/api/v1/students?status=ACTIVE&page=0&size=10")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.content.length()").isEqualTo(5);
    }

    @Test
    void shouldPaginateResults() {
        createTestStudents(30, StudentStatus.ACTIVE);

        webTestClient.get()
            .uri("/api/v1/students?page=0&size=10")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.page.totalElements").isEqualTo(30)
            .jsonPath("$.page.totalPages").isEqualTo(3)
            .jsonPath("$.content.length()").isEqualTo(10);
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    void shouldDenyWriteAccessToAuditor() {
        CreateStudentRequest request = CreateStudentRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .dateOfBirth(LocalDate.of(2010, 1, 15))
            .mobile("9876543210")
            .admissionDate(LocalDate.now())
            .build();

        webTestClient.post()
            .uri("/api/v1/students")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void shouldRequireAuthentication() {
        webTestClient.get()
            .uri("/api/v1/students")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    private void createTestStudents(int count, StudentStatus status) {
        for (int i = 0; i < count; i++) {
            Student student = StudentTestDataFactory.createStudentBuilder()
                .mobile(String.format("987654321%d", i))
                .status(status)
                .build();
            studentRepository.save(student);
        }
    }
}
```

**REST Assured API Tests**:
```java
// StudentApiTest.java (using REST Assured)
public class StudentApiTest {

    private static final String BASE_URL = "http://localhost:8080/api/v1";
    private String token;

    @BeforeEach
    void setUp() {
        // Get authentication token
        token = given()
            .contentType(ContentType.JSON)
            .body(new LoginRequest("admin", "password"))
            .when()
            .post(BASE_URL + "/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path("accessToken");
    }

    @Test
    void testCreateStudent() {
        CreateStudentRequest request = createValidStudentRequest();

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(request)
            .when()
            .post(BASE_URL + "/students")
            .then()
            .statusCode(201)
            .body("studentCode", notNullValue())
            .body("firstName", equalTo("John"));
    }

    @Test
    void testGetStudents() {
        given()
            .header("Authorization", "Bearer " + token)
            .queryParam("status", "ACTIVE")
            .queryParam("page", 0)
            .queryParam("size", 10)
            .when()
            .get(BASE_URL + "/students")
            .then()
            .statusCode(200)
            .body("content", notNullValue())
            .body("page.totalElements", greaterThan(0));
    }

    @Test
    void testResponseTimeUnder200ms() {
        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get(BASE_URL + "/students/1")
            .then()
            .statusCode(200)
            .time(lessThan(200L), TimeUnit.MILLISECONDS);
    }
}
```

**Implementation Guidance**:
1. Test all HTTP methods (GET, POST, PUT, PATCH, DELETE)
2. Verify response status codes
3. Validate response body structure
4. Test authentication and authorization
5. Test request validation
6. Test error scenarios
7. Test pagination and filtering
8. Test performance/response times
9. Use containers for test database
10. Clean up data between tests

**Definition of Done**:
- [ ] All API endpoints tested
- [ ] Happy path and error paths covered
- [ ] Authentication/authorization verified
- [ ] Response validation complete
- [ ] Performance targets verified
- [ ] Tests repeatable and isolated
- [ ] CI/CD integration working

**Dependencies**: BE-S3-01, BE-S3-02, BE-S3-03

---

## Remaining Sprint Testing Tasks

The QA team continues with similar patterns for:
- **Sprints 4-7**: Testing for Class, Fee, Payment, and Receipt modules
- **Sprints 8-10**: E2E tests, performance testing, security testing
- **Sprints 11-13**: UAT support, production verification, post-launch monitoring

---

## Testing Metrics & Targets

| Metric | Target | Measurement |
|--------|--------|-------------|
| Unit Test Coverage | 80%+ | JaCoCo/LCOV |
| Integration Test Coverage | 60%+ | TestContainers |
| E2E Test Coverage | Key flows | Playwright |
| Code Coverage Overall | 75%+ | Combined |
| Test Execution Time | <60 sec | CI/CD |
| Defect Detection Rate | >90% | Pre-production |
| Regression Test Coverage | 100% | Critical paths |

---

## Document Control

| Version | Date | Author | Status |
|---------|------|--------|--------|
| 1.0 | Nov 11, 2025 | QA Lead | Draft |

