# Test Coverage Quick Reference

**Current Coverage**: 4% → **Target Coverage**: 80%

---

## Test Gap Analysis

### 📊 Coverage by Layer

| Layer | Current | Target | Gap | Tests Needed |
|-------|---------|--------|-----|--------------|
| **Service** | 7% | 80% | 73% | 75 tests |
| **Controller** | 0% | 80% | 80% | 67 tests |
| **Repository** | 0% | 80% | 80% | 43 tests |
| **Mapper** | 0% | 80% | 80% | 30 tests |
| **DTO** | 3-4% | 80% | 76% | 60 tests |
| **Exception** | 9% | 80% | 71% | 6 tests |
| **TOTAL** | **4%** | **80%** | **76%** | **288 tests** |

---

## Priority Matrix

### 🔴 CRITICAL (Week 1-2)
**Service Layer Tests - 75 tests**

- [ ] `ClassServiceTest.java` (10 tests)
- [ ] `FeeMasterServiceTest.java` (15 tests)
- [ ] `FeeJournalServiceTest.java` (18 tests)
- [ ] `FeeReceiptServiceTest.java` (20 tests)
- [ ] `SchoolConfigServiceTest.java` (12 tests)

**Impact**: +30% coverage

---

### 🟠 HIGH (Week 3)
**Controller Layer Tests - 67 tests**

- [ ] `StudentControllerTest.java` (10 tests)
- [ ] `ClassControllerTest.java` (10 tests)
- [ ] `FeeMasterControllerTest.java` (12 tests)
- [ ] `FeeJournalControllerTest.java` (12 tests)
- [ ] `FeeReceiptControllerTest.java` (13 tests)
- [ ] `SchoolConfigControllerTest.java` (10 tests)

**Impact**: +20% coverage

---

### 🟡 MEDIUM (Week 4)
**Repository Layer Tests - 43 tests**

- [ ] `StudentRepositoryTest.java` (8 tests)
- [ ] `ClassRepositoryTest.java` (6 tests)
- [ ] `FeeMasterRepositoryTest.java` (8 tests)
- [ ] `FeeJournalRepositoryTest.java` (8 tests)
- [ ] `FeeReceiptRepositoryTest.java` (8 tests)
- [ ] `SchoolConfigRepositoryTest.java` (5 tests)

**Impact**: +15% coverage

---

### 🟢 LOW-MEDIUM (Week 5)
**Mapper & DTO Validation Tests - 90 tests**

**Mappers** (30 tests):
- [ ] `StudentMapperTest.java` (5 tests)
- [ ] `ClassMapperTest.java` (5 tests)
- [ ] `FeeMasterMapperTest.java` (5 tests)
- [ ] `FeeJournalMapperTest.java` (5 tests)
- [ ] `FeeReceiptMapperTest.java` (5 tests)
- [ ] `SchoolConfigMapperTest.java` (5 tests)

**DTO Validations** (60 tests):
- [ ] `StudentRequestDTOValidationTest.java` (15 tests)
- [ ] `ClassRequestDTOValidationTest.java` (8 tests)
- [ ] `FeeMasterRequestDTOValidationTest.java` (10 tests)
- [ ] `FeeJournalRequestDTOValidationTest.java` (8 tests)
- [ ] `FeeReceiptRequestDTOValidationTest.java` (12 tests)
- [ ] `SchoolConfigRequestDTOValidationTest.java` (8 tests)

**Impact**: +10% coverage

---

### ⚪ LOW (Week 5)
**Exception Handler Tests - 6 tests**

- [ ] `GlobalExceptionHandlerTest.java` (6 tests)

**Impact**: +5% coverage

---

## Test Types Breakdown

### Unit Tests (Mockito)
**Location**: `src/test/java/.../service/`
**Framework**: JUnit 5 + Mockito
**Pattern**: Mock all dependencies

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock private Repository repository;
    @InjectMocks private Service service;
}
```

**Files**: 6 service test classes (75 tests)

---

### Controller Tests (MockMvc)
**Location**: `src/test/java/.../controller/`
**Framework**: JUnit 5 + Spring MockMvc
**Pattern**: Mock service layer

```java
@WebMvcTest(Controller.class)
class ControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private Service service;
}
```

**Files**: 6 controller test classes (67 tests)

---

### Integration Tests (DataJpaTest)
**Location**: `src/test/java/.../repository/`
**Framework**: JUnit 5 + Spring Data JPA
**Pattern**: Real database (H2)

```java
@DataJpaTest
class RepositoryTest {
    @Autowired private Repository repository;
    @Autowired private TestEntityManager entityManager;
}
```

**Files**: 6 repository test classes (43 tests)

---

### Mapper Tests (MapStruct)
**Location**: `src/test/java/.../mapper/`
**Framework**: JUnit 5 + Spring
**Pattern**: Test actual mapping

```java
@ExtendWith(SpringExtension.class)
class MapperTest {
    @Autowired private Mapper mapper;
}
```

**Files**: 6 mapper test classes (30 tests)

---

### Validation Tests (Bean Validation)
**Location**: `src/test/java/.../dto/request/`
**Framework**: JUnit 5 + Hibernate Validator
**Pattern**: Test @Valid constraints

```java
class DTOValidationTest {
    private Validator validator;
}
```

**Files**: 6 DTO validation test classes (60 tests)

---

## Daily Progress Tracker

### Week 1: Service Tests (Part 1)
- [ ] **Day 1**: ClassServiceTest (10 tests) ⏱️ 4 hours
- [ ] **Day 2**: FeeMasterServiceTest (15 tests) ⏱️ 5 hours
- [ ] **Day 3**: FeeMasterServiceTest completion + Review
- [ ] **Day 4**: FeeJournalServiceTest (18 tests) ⏱️ 6 hours
- [ ] **Day 5**: FeeJournalServiceTest completion

**Week 1 Goal**: 43 tests, +15% coverage

---

### Week 2: Service Tests (Part 2)
- [ ] **Day 1**: FeeReceiptServiceTest (20 tests) ⏱️ 6 hours
- [ ] **Day 2**: FeeReceiptServiceTest completion
- [ ] **Day 3**: SchoolConfigServiceTest (12 tests) ⏱️ 4 hours
- [ ] **Day 4**: SchoolConfigServiceTest completion + Review
- [ ] **Day 5**: Buffer/Refinement

**Week 2 Goal**: 32 tests, +15% coverage
**Cumulative**: 75 tests, 35% coverage ✅

---

### Week 3: Controller Tests
- [ ] **Day 1**: StudentControllerTest + ClassControllerTest (20 tests)
- [ ] **Day 2**: FeeMasterControllerTest + FeeJournalControllerTest (24 tests)
- [ ] **Day 3**: FeeReceiptControllerTest + SchoolConfigControllerTest (23 tests)
- [ ] **Day 4**: Review and fixes
- [ ] **Day 5**: Buffer/Documentation

**Week 3 Goal**: 67 tests, +20% coverage
**Cumulative**: 142 tests, 55% coverage ✅

---

### Week 4: Repository Tests
- [ ] **Day 1**: StudentRepositoryTest + ClassRepositoryTest (14 tests)
- [ ] **Day 2**: FeeMasterRepositoryTest + FeeJournalRepositoryTest (16 tests)
- [ ] **Day 3**: FeeReceiptRepositoryTest + SchoolConfigRepositoryTest (13 tests)
- [ ] **Day 4**: Review and fixes
- [ ] **Day 5**: Buffer/Integration test refinement

**Week 4 Goal**: 43 tests, +15% coverage
**Cumulative**: 185 tests, 70% coverage ✅

---

### Week 5: Mappers, DTOs, Exception Handlers
- [ ] **Day 1**: All Mapper tests (30 tests)
- [ ] **Day 2**: DTO Validation tests (Part 1 - 30 tests)
- [ ] **Day 3**: DTO Validation tests (Part 2 - 30 tests)
- [ ] **Day 4**: GlobalExceptionHandlerTest (6 tests)
- [ ] **Day 5**: Final review, documentation, coverage verification

**Week 5 Goal**: 96 tests, +15% coverage
**Cumulative**: 281 tests, 85% coverage ✅✅

---

## Commands Reference

### Run Tests
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=StudentServiceTest

# Specific test method
mvn test -Dtest=StudentServiceTest#shouldCreateStudentSuccessfully

# With coverage
mvn clean test jacoco:report
```

### View Coverage
```bash
# Open coverage report
start target/site/jacoco/index.html  # Windows
open target/site/jacoco/index.html   # Mac

# View in terminal
mvn jacoco:report
cat target/site/jacoco/index.html
```

### Watch Mode (Auto-run tests)
```bash
# Install: npm install -g nodemon
nodemon --watch src/main --watch src/test --ext java --exec "mvn test"
```

---

## Coverage Milestones

| Milestone | Coverage | Tests | Status |
|-----------|----------|-------|--------|
| Current | 4% | 7 | ✅ Done |
| Phase 1 | 35% | 82 | 🔴 In Progress |
| Phase 2 | 55% | 149 | 🟠 Pending |
| Phase 3 | 70% | 192 | 🟡 Pending |
| Phase 4 | 85% | 288 | 🟢 Target |

---

## Test Quality Checklist

For each test class, ensure:

- [ ] Follows Given-When-Then structure
- [ ] Uses `@DisplayName` for clear descriptions
- [ ] Tests both success and failure scenarios
- [ ] Includes edge cases (null, empty, boundary)
- [ ] Verifies mock interactions with `verify()`
- [ ] Tests are independent (no shared state)
- [ ] Fast execution (< 1 second per test)
- [ ] Clear assertion messages
- [ ] Proper cleanup in `@AfterEach` if needed
- [ ] Uses parameterized tests for similar scenarios

---

## Success Metrics

### Quantitative
- ✅ 80%+ instruction coverage
- ✅ 288+ test methods
- ✅ Zero test failures
- ✅ Tests run in < 30 seconds
- ✅ All services covered
- ✅ All controllers covered
- ✅ All repositories covered

### Qualitative
- ✅ Critical business logic tested
- ✅ Edge cases covered
- ✅ Exception handling verified
- ✅ Validation rules tested
- ✅ Data integrity maintained
- ✅ Tests are maintainable
- ✅ Tests serve as documentation

---

## Resources

- **Full Test Plan**: [COMPREHENSIVE-UNIT-TEST-PLAN.md](COMPREHENSIVE-UNIT-TEST-PLAN.md)
- **Backend Guide**: [CLAUDE-BACKEND.md](CLAUDE-BACKEND.md)
- **Reference Implementation**: `StudentServiceTest.java`
- **JaCoCo Documentation**: https://www.jacoco.org/jacoco/trunk/doc/
- **JUnit 5 Guide**: https://junit.org/junit5/docs/current/user-guide/
- **Mockito Documentation**: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html

---

**Last Updated**: November 2, 2025
**Status**: Ready for Implementation
**Next Action**: Start with ClassServiceTest
