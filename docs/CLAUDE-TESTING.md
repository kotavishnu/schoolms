# CLAUDE-TESTING.md

Automated testing strategy and agent configuration for the School Management System.

## Testing Philosophy

**Test-Driven Development (TDD)**: Write tests before implementation
1. Write failing test
2. Implement minimum code to pass
3. Refactor
4. Repeat

## Backend Testing

### Unit Tests (Service Layer)

**Location**: `backend/src/test/java/.../service/`

```java
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock private StudentRepository repository;
    @Mock private ClassRepository classRepository;
    @InjectMocks private StudentService service;
    
    @Test
    @DisplayName("Should create student with valid data")
    void shouldCreateStudent() {
        // Given
        StudentDTO dto = StudentDTO.builder()
            .firstName("John").lastName("Doe").build();
        Student entity = new Student();
        when(repository.save(any())).thenReturn(entity);
        
        // When
        Student result = service.createStudent(dto);
        
        // Then
        assertNotNull(result);
        verify(repository, times(1)).save(any());
    }
    
    @Test
    @DisplayName("Should throw exception when class not found")
    void shouldThrowWhenClassNotFound() {
        StudentDTO dto = StudentDTO.builder().classId(99L).build();
        when(classRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, 
            () -> service.createStudent(dto));
    }
}
```

### Integration Tests (Repository)

**Location**: `backend/src/test/java/.../repository/`

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class StudentRepositoryTest {
    @Autowired private StudentRepository repository;
    @Autowired private TestEntityManager entityManager;
    
    @Test
    void shouldFindStudentsByClassId() {
        // Given
        Student student = new Student();
        student.setClassId(1L);
        entityManager.persistAndFlush(student);
        
        // When
        List<Student> found = repository.findByClassId(1L);
        
        // Then
        assertThat(found).hasSize(1);
    }
}
```

### Controller Tests (API)

**Location**: `backend/src/test/java/.../controller/`

```java
@WebMvcTest(StudentController.class)
class StudentControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private StudentService service;
    
    @Test
    void shouldCreateStudent() throws Exception {
        StudentDTO dto = new StudentDTO(/* data */);
        when(service.createStudent(any())).thenReturn(new Student());
        
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }
}
```

### Run Backend Tests

```bash
cd backend

# All tests
mvn test

# Specific test class
mvn test -Dtest=StudentServiceTest

# Test coverage report
mvn test jacoco:report
# Report: target/site/jacoco/index.html

# Integration tests only
mvn test -Dgroups=integration

# Skip tests (build only)
mvn clean install -DskipTests
```

## Frontend Testing

### Component Tests

**Location**: `frontend/src/__tests__/`

```javascript
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import StudentForm from '../pages/StudentRegistration';
import * as studentService from '../services/studentService';

jest.mock('../services/studentService');

describe('StudentForm', () => {
  test('renders all required fields', () => {
    render(<StudentForm />);
    expect(screen.getByLabelText(/first name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/last name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/date of birth/i)).toBeInTheDocument();
  });
  
  test('submits form with valid data', async () => {
    studentService.createStudent.mockResolvedValue({ id: 1 });
    render(<StudentForm />);
    
    await userEvent.type(screen.getByLabelText(/first name/i), 'John');
    await userEvent.type(screen.getByLabelText(/last name/i), 'Doe');
    fireEvent.click(screen.getByRole('button', { name: /submit/i }));
    
    await waitFor(() => {
      expect(studentService.createStudent).toHaveBeenCalledTimes(1);
    });
  });
  
  test('displays error on API failure', async () => {
    studentService.createStudent.mockRejectedValue({
      response: { data: { message: 'Error occurred' } }
    });
    render(<StudentForm />);
    
    fireEvent.click(screen.getByRole('button', { name: /submit/i }));
    
    await waitFor(() => {
      expect(screen.getByText(/error occurred/i)).toBeInTheDocument();
    });
  });
});
```

### Setup Testing Dependencies

```bash
cd frontend
npm install -D @testing-library/react @testing-library/jest-dom \
  @testing-library/user-event vitest jsdom
```

**vitest.config.js**:
```javascript
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.js',
  },
});
```

### Run Frontend Tests

```bash
cd frontend

# Run tests
npm run test

# Watch mode
npm run test:watch

# Coverage
npm run test:coverage
# Report: coverage/index.html

# UI mode
npm run test:ui
```

## Test Automation Agent

### Pre-commit Hook (Husky)

```bash
# Install Husky
npm install -D husky

# Initialize
npx husky init

# Create pre-commit hook
echo "npm run test && cd ../backend && mvn test" > .husky/pre-commit
chmod +x .husky/pre-commit
```

### CI/CD Pipeline (GitHub Actions)

**Location**: `.github/workflows/test.yml`

```yaml
name: Automated Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:18
        env:
          POSTGRES_DB: school_management_test
          POSTGRES_PASSWORD: test123
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
    
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Cache Maven packages
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
      
      - name: Run Backend Tests
        run: |
          cd backend
          mvn clean test
      
      - name: Upload Coverage
        uses: codecov/codecov-action@v3
        with:
          files: ./backend/target/site/jacoco/jacoco.xml

  frontend-tests:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: frontend/package-lock.json
      
      - name: Install Dependencies
        run: |
          cd frontend
          npm ci
      
      - name: Run Frontend Tests
        run: |
          cd frontend
          npm run test:coverage
      
      - name: Upload Coverage
        uses: codecov/codecov-action@v3
        with:
          files: ./frontend/coverage/coverage-final.json
```

## Test Coverage Goals

- **Backend**: Minimum 80% coverage for services
- **Frontend**: Minimum 70% coverage for components
- **Critical Paths**: 100% coverage (fee calculation, payment processing)

## Manual Testing Checklist

Before committing:
- [ ] All unit tests pass
- [ ] Integration tests pass
- [ ] API endpoints tested with Postman/curl
- [ ] Frontend components render correctly
- [ ] Form validations work
- [ ] Error handling displays properly
- [ ] No console errors