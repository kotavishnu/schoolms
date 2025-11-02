# Comprehensive Unit Test Plan for 80% Coverage

**Target**: Achieve 80% code coverage across all backend layers
**Current Coverage**: 4%
**Required Test Classes**: ~30-35 test classes
**Estimated Test Cases**: ~250-300 test methods

---

## Table of Contents
1. [Service Layer Tests (Priority 1)](#service-layer-tests)
2. [Controller Layer Tests (Priority 2)](#controller-layer-tests)
3. [Repository Layer Tests (Priority 3)](#repository-layer-tests)
4. [Mapper Tests (Priority 4)](#mapper-tests)
5. [DTO Validation Tests (Priority 5)](#dto-validation-tests)
6. [Exception Handler Tests (Priority 6)](#exception-handler-tests)
7. [Test Implementation Order](#test-implementation-order)

---

## Service Layer Tests (Priority 1)

### ✅ StudentServiceTest (COMPLETED - 7 tests)
**Coverage**: Service logic fully tested
**Status**: Reference implementation for other services

---

### 1. ClassServiceTest (Required - ~10 tests)

**File**: `src/test/java/com/school/management/service/ClassServiceTest.java`

#### Test Cases:

**Create Operations:**
```java
@Test
@DisplayName("Should create class successfully")
void shouldCreateClassSuccessfully()
// Given: Valid ClassRequestDTO
// When: createClass() called
// Then: Class created, returns ClassResponseDTO

@Test
@DisplayName("Should throw exception when duplicate class exists")
void shouldThrowExceptionWhenDuplicateClass()
// Given: Class with same number, section, year exists
// When: createClass() called
// Then: DuplicateResourceException thrown

@Test
@DisplayName("Should throw exception when capacity is invalid")
void shouldThrowExceptionWhenInvalidCapacity()
// Given: Capacity < 1
// When: createClass() called
// Then: ValidationException thrown
```

**Read Operations:**
```java
@Test
@DisplayName("Should get class by ID successfully")
void shouldGetClassByIdSuccessfully()

@Test
@DisplayName("Should throw exception when class not found")
void shouldThrowExceptionWhenClassNotFound()

@Test
@DisplayName("Should get all classes successfully")
void shouldGetAllClassesSuccessfully()

@Test
@DisplayName("Should get classes by academic year")
void shouldGetClassesByAcademicYear()

@Test
@DisplayName("Should check if class exists")
void shouldCheckIfClassExists()
```

**Update/Delete Operations:**
```java
@Test
@DisplayName("Should update class successfully")
void shouldUpdateClassSuccessfully()

@Test
@DisplayName("Should delete class successfully")
void shouldDeleteClassSuccessfully()
```

**Mocks Required:**
- `ClassRepository`
- `ClassMapper`

---

### 2. FeeMasterServiceTest (Required - ~15 tests)

**File**: `src/test/java/com/school/management/service/FeeMasterServiceTest.java`

#### Test Cases:

**Create Operations:**
```java
@Test
@DisplayName("Should create fee master successfully")
void shouldCreateFeeMasterSuccessfully()

@Test
@DisplayName("Should throw exception when applicableTo is before applicableFrom")
void shouldThrowExceptionWhenInvalidDateRange()

@Test
@DisplayName("Should create fee master with different fee types")
@ParameterizedTest
@EnumSource(FeeType.class)
void shouldCreateFeeMasterForAllFeeTypes(FeeType feeType)
// Test for: TUITION, LIBRARY, COMPUTER, SPORTS, SPECIAL, etc.

@Test
@DisplayName("Should throw exception when amount is negative")
void shouldThrowExceptionWhenNegativeAmount()
```

**Read Operations:**
```java
@Test
@DisplayName("Should get fee master by ID")
void shouldGetFeeMasterById()

@Test
@DisplayName("Should get all fee masters")
void shouldGetAllFeeMasters()

@Test
@DisplayName("Should get fee masters by academic year")
void shouldGetFeeMastersByAcademicYear()

@Test
@DisplayName("Should get fee masters by type")
void shouldGetFeeMastersByType()

@Test
@DisplayName("Should get active fee masters only")
void shouldGetActiveFeeMastersOnly()

@Test
@DisplayName("Should get currently applicable fee masters")
void shouldGetCurrentlyApplicableFeeMasters()

@Test
@DisplayName("Should get latest fee master by type")
void shouldGetLatestFeeMasterByType()
```

**Update/Activate Operations:**
```java
@Test
@DisplayName("Should update fee master successfully")
void shouldUpdateFeeMasterSuccessfully()

@Test
@DisplayName("Should activate fee master")
void shouldActivateFeeMaster()

@Test
@DisplayName("Should deactivate fee master")
void shouldDeactivateFeeMaster()

@Test
@DisplayName("Should delete fee master successfully")
void shouldDeleteFeeMasterSuccessfully()
```

**Mocks Required:**
- `FeeMasterRepository`
- `FeeMasterMapper`

---

### 3. FeeJournalServiceTest (Required - ~18 tests)

**File**: `src/test/java/com/school/management/service/FeeJournalServiceTest.java`

#### Test Cases:

**Create Operations:**
```java
@Test
@DisplayName("Should create fee journal successfully")
void shouldCreateFeeJournalSuccessfully()

@Test
@DisplayName("Should throw exception when student not found")
void shouldThrowExceptionWhenStudentNotFound()

@Test
@DisplayName("Should throw exception when duplicate journal entry exists")
void shouldThrowExceptionWhenDuplicateEntry()
// Given: Journal entry for same student, month, year exists
// Then: DuplicateResourceException thrown

@Test
@DisplayName("Should set payment status to PENDING for new journal")
void shouldSetStatusToPendingForNewJournal()

@Test
@DisplayName("Should throw exception when amount due is negative")
void shouldThrowExceptionWhenNegativeAmountDue()
```

**Read Operations:**
```java
@Test
@DisplayName("Should get fee journal by ID")
void shouldGetFeeJournalById()

@Test
@DisplayName("Should get all journals for student")
void shouldGetAllJournalsForStudent()

@Test
@DisplayName("Should get pending journals for student")
void shouldGetPendingJournalsForStudent()

@Test
@DisplayName("Should get journals by month and year")
void shouldGetJournalsByMonthAndYear()

@Test
@DisplayName("Should get journals by status")
void shouldGetJournalsByStatus()

@Test
@DisplayName("Should get overdue journals")
void shouldGetOverdueJournals()

@Test
@DisplayName("Should calculate student dues summary")
void shouldCalculateStudentDuesSummary()
// Returns: { totalDue, totalPaid, pendingCount }
```

**Payment Recording:**
```java
@Test
@DisplayName("Should record full payment successfully")
void shouldRecordFullPaymentSuccessfully()
// Given: Payment amount = amount due
// Then: Status = PAID, amount paid updated

@Test
@DisplayName("Should record partial payment successfully")
void shouldRecordPartialPaymentSuccessfully()
// Given: Payment amount < amount due
// Then: Status = PARTIAL, amount paid updated

@Test
@DisplayName("Should throw exception when payment exceeds due amount")
void shouldThrowExceptionWhenPaymentExceedsDue()

@Test
@DisplayName("Should update payment status automatically")
void shouldUpdatePaymentStatusAutomatically()

@Test
@DisplayName("Should set status to OVERDUE when past due date")
void shouldSetStatusToOverdueWhenPastDueDate()
```

**Update/Delete:**
```java
@Test
@DisplayName("Should update fee journal successfully")
void shouldUpdateFeeJournalSuccessfully()

@Test
@DisplayName("Should delete fee journal successfully")
void shouldDeleteFeeJournalSuccessfully()
```

**Mocks Required:**
- `FeeJournalRepository`
- `StudentRepository`
- `FeeJournalMapper`

---

### 4. FeeReceiptServiceTest (Required - ~20 tests)

**File**: `src/test/java/com/school/management/service/FeeReceiptServiceTest.java`

#### Test Cases:

**Receipt Generation:**
```java
@Test
@DisplayName("Should generate receipt successfully with CASH payment")
void shouldGenerateReceiptWithCashPayment()

@Test
@DisplayName("Should generate receipt successfully with ONLINE payment")
void shouldGenerateReceiptWithOnlinePayment()
// Requires: transactionId

@Test
@DisplayName("Should generate receipt successfully with CHEQUE payment")
void shouldGenerateReceiptWithChequePayment()
// Requires: chequeNumber, bankName

@Test
@DisplayName("Should generate receipt successfully with CARD payment")
void shouldGenerateReceiptWithCardPayment()

@Test
@DisplayName("Should auto-generate receipt number in format REC-YYYY-NNNNN")
void shouldAutoGenerateReceiptNumber()

@Test
@DisplayName("Should throw exception when student not found")
void shouldThrowExceptionWhenStudentNotFound()

@Test
@DisplayName("Should throw exception when payment date is in future")
void shouldThrowExceptionWhenFutureDatePayment()

@Test
@DisplayName("Should validate transactionId required for ONLINE payment")
void shouldValidateTransactionIdForOnlinePayment()

@Test
@DisplayName("Should validate cheque details required for CHEQUE payment")
void shouldValidateChequeDetailsForChequePayment()

@Test
@DisplayName("Should process multiple months paid")
void shouldProcessMultipleMonthsPaid()
```

**Read Operations:**
```java
@Test
@DisplayName("Should get receipt by ID")
void shouldGetReceiptById()

@Test
@DisplayName("Should get receipt by receipt number")
void shouldGetReceiptByReceiptNumber()

@Test
@DisplayName("Should get receipts for student")
void shouldGetReceiptsForStudent()

@Test
@DisplayName("Should get receipts by date range")
void shouldGetReceiptsByDateRange()

@Test
@DisplayName("Should get receipts by payment method")
void shouldGetReceiptsByPaymentMethod()

@Test
@DisplayName("Should get today's receipts")
void shouldGetTodaysReceipts()
```

**Collection Reports:**
```java
@Test
@DisplayName("Should calculate total collection for date range")
void shouldCalculateTotalCollectionForDateRange()

@Test
@DisplayName("Should calculate collection by payment method")
void shouldCalculateCollectionByPaymentMethod()

@Test
@DisplayName("Should get collection summary")
void shouldGetCollectionSummary()
// Returns: { receiptCount, grandTotal, cashCollection, onlineCollection, etc. }

@Test
@DisplayName("Should count receipts for student")
void shouldCountReceiptsForStudent()
```

**Mocks Required:**
- `FeeReceiptRepository`
- `StudentRepository`
- `FeeJournalRepository`
- `FeeReceiptMapper`

---

### 5. SchoolConfigServiceTest (Required - ~12 tests)

**File**: `src/test/java/com/school/management/service/SchoolConfigServiceTest.java`

#### Test Cases:

**Create Operations:**
```java
@Test
@DisplayName("Should create school config successfully")
void shouldCreateSchoolConfigSuccessfully()

@Test
@DisplayName("Should throw exception when duplicate config key exists")
void shouldThrowExceptionWhenDuplicateConfigKey()

@Test
@DisplayName("Should create config with different data types")
@ParameterizedTest
@ValueSource(strings = {"STRING", "INTEGER", "BOOLEAN", "JSON"})
void shouldCreateConfigWithDifferentDataTypes(String dataType)
```

**Read Operations:**
```java
@Test
@DisplayName("Should get config by ID")
void shouldGetConfigById()

@Test
@DisplayName("Should get config by key")
void shouldGetConfigByKey()

@Test
@DisplayName("Should get config value only")
void shouldGetConfigValueOnly()

@Test
@DisplayName("Should get all configs")
void shouldGetAllConfigs()

@Test
@DisplayName("Should get configs by category")
void shouldGetConfigsByCategory()

@Test
@DisplayName("Should get editable configs only")
void shouldGetEditableConfigsOnly()

@Test
@DisplayName("Should check if config exists by key")
void shouldCheckIfConfigExistsByKey()
```

**Update/Delete:**
```java
@Test
@DisplayName("Should update config value")
void shouldUpdateConfigValue()

@Test
@DisplayName("Should delete config successfully")
void shouldDeleteConfigSuccessfully()
```

**Mocks Required:**
- `SchoolConfigRepository`
- `SchoolConfigMapper`

---

## Controller Layer Tests (Priority 2)

### Controller Test Pattern (Using @WebMvcTest)

All controllers should follow this pattern:

```java
@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    // Test cases...
}
```

---

### 1. StudentControllerTest (Required - ~10 tests)

**File**: `src/test/java/com/school/management/controller/StudentControllerTest.java`

#### Test Cases:

```java
@Test
@DisplayName("POST /api/students should create student and return 201")
void shouldCreateStudentSuccessfully() throws Exception

@Test
@DisplayName("POST /api/students should return 400 when validation fails")
void shouldReturn400WhenValidationFails() throws Exception

@Test
@DisplayName("GET /api/students/{id} should return student")
void shouldGetStudentById() throws Exception

@Test
@DisplayName("GET /api/students/{id} should return 404 when not found")
void shouldReturn404WhenStudentNotFound() throws Exception

@Test
@DisplayName("GET /api/students should return all students")
void shouldGetAllStudents() throws Exception

@Test
@DisplayName("GET /api/students?classId=1 should return students by class")
void shouldGetStudentsByClass() throws Exception

@Test
@DisplayName("GET /api/students/search?q=John should search students")
void shouldSearchStudents() throws Exception

@Test
@DisplayName("PUT /api/students/{id} should update student")
void shouldUpdateStudent() throws Exception

@Test
@DisplayName("DELETE /api/students/{id} should delete student")
void shouldDeleteStudent() throws Exception

@Test
@DisplayName("GET /api/students/pending-fees should return students with pending fees")
void shouldGetStudentsWithPendingFees() throws Exception
```

---

### 2. ClassControllerTest (Required - ~10 tests)

**File**: `src/test/java/com/school/management/controller/ClassControllerTest.java`

Similar pattern covering:
- POST /api/classes
- GET /api/classes/{id}
- GET /api/classes
- GET /api/classes?academicYear=2024-2025
- PUT /api/classes/{id}
- DELETE /api/classes/{id}
- GET /api/classes/available
- GET /api/classes/exists

---

### 3. FeeMasterControllerTest (Required - ~12 tests)

**File**: `src/test/java/com/school/management/controller/FeeMasterControllerTest.java`

Covering all 12 endpoints from documentation.

---

### 4. FeeJournalControllerTest (Required - ~12 tests)

**File**: `src/test/java/com/school/management/controller/FeeJournalControllerTest.java`

Covering all 12 endpoints from documentation.

---

### 5. FeeReceiptControllerTest (Required - ~13 tests)

**File**: `src/test/java/com/school/management/controller/FeeReceiptControllerTest.java`

Covering all 13 endpoints from documentation.

---

### 6. SchoolConfigControllerTest (Required - ~10 tests)

**File**: `src/test/java/com/school/management/controller/SchoolConfigControllerTest.java`

Covering all 10 endpoints from documentation.

---

## Repository Layer Tests (Priority 3)

### Repository Test Pattern (Using @DataJpaTest)

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private TestEntityManager entityManager;

    // Test cases...
}
```

---

### 1. StudentRepositoryTest (Required - ~8 tests)

**File**: `src/test/java/com/school/management/repository/StudentRepositoryTest.java`

#### Test Cases:

```java
@Test
@DisplayName("Should find students by class ID")
void shouldFindStudentsByClassId()

@Test
@DisplayName("Should find student by mobile")
void shouldFindStudentByMobile()

@Test
@DisplayName("Should find students by status")
void shouldFindStudentsByStatus()

@Test
@DisplayName("Should search students by name")
void shouldSearchStudentsByName()

@Test
@DisplayName("Should find active students by class using native query")
void shouldFindActiveStudentsByClass()

@Test
@DisplayName("Should check if mobile exists")
void shouldCheckIfMobileExists()

@Test
@DisplayName("Should count students by class ID")
void shouldCountStudentsByClassId()

@Test
@DisplayName("Should return empty list when no students found")
void shouldReturnEmptyListWhenNoStudentsFound()
```

---

### 2. ClassRepositoryTest (Required - ~6 tests)

**File**: `src/test/java/com/school/management/repository/ClassRepositoryTest.java`

#### Test Cases:

```java
@Test
@DisplayName("Should find classes by academic year")
void shouldFindClassesByAcademicYear()

@Test
@DisplayName("Should check if class exists by number, section, and year")
void shouldCheckIfClassExists()

@Test
@DisplayName("Should find available classes")
void shouldFindAvailableClasses()
// currentEnrollment < capacity

@Test
@DisplayName("Should find classes with capacity")
void shouldFindClassesWithCapacity()

@Test
@DisplayName("Should count classes by academic year")
void shouldCountClassesByAcademicYear()

@Test
@DisplayName("Should order classes by class number")
void shouldOrderClassesByNumber()
```

---

### 3. FeeMasterRepositoryTest (Required - ~8 tests)

**File**: `src/test/java/com/school/management/repository/FeeMasterRepositoryTest.java`

Test custom queries for:
- findByAcademicYear
- findByFeeType
- findByIsActiveTrue
- findCurrentlyApplicable (date range)
- findLatestByFeeType
- findByFeeTypeAndIsActiveTrue

---

### 4. FeeJournalRepositoryTest (Required - ~8 tests)

**File**: `src/test/java/com/school/management/repository/FeeJournalRepositoryTest.java`

Test custom queries for:
- findByStudentId
- findByStudentIdAndPaymentStatus
- findByMonthAndYear
- findByPaymentStatus
- findOverdueJournals
- existsByStudentIdAndMonthAndYear

---

### 5. FeeReceiptRepositoryTest (Required - ~8 tests)

**File**: `src/test/java/com/school/management/repository/FeeReceiptRepositoryTest.java`

Test custom queries for:
- findByStudentId
- findByReceiptNumber
- findByPaymentDateBetween
- findByPaymentMethod
- findByPaymentDate (today)
- calculateTotalByDateRange
- calculateTotalByPaymentMethod

---

### 6. SchoolConfigRepositoryTest (Required - ~5 tests)

**File**: `src/test/java/com/school/management/repository/SchoolConfigRepositoryTest.java`

Test custom queries for:
- findByConfigKey
- findByCategory
- findByIsEditableTrue
- existsByConfigKey

---

## Mapper Tests (Priority 4)

### Mapper Test Pattern

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MapperTestConfig.class})
class StudentMapperTest {

    @Autowired
    private StudentMapper studentMapper;

    // Test cases...
}
```

---

### 1. StudentMapperTest (Required - ~5 tests)

**File**: `src/test/java/com/school/management/mapper/StudentMapperTest.java`

#### Test Cases:

```java
@Test
@DisplayName("Should map entity to response DTO")
void shouldMapEntityToResponseDTO()
// Verify: All fields mapped correctly, computed fields calculated

@Test
@DisplayName("Should map request DTO to entity")
void shouldMapRequestDTOToEntity()

@Test
@DisplayName("Should handle null values")
void shouldHandleNullValues()

@Test
@DisplayName("Should map list of entities to list of DTOs")
void shouldMapListOfEntitiesToListOfDTOs()

@Test
@DisplayName("Should update entity from DTO")
void shouldUpdateEntityFromDTO()
```

---

### Similar mapper tests needed for:
- ClassMapperTest
- FeeMasterMapperTest
- FeeJournalMapperTest
- FeeReceiptMapperTest
- SchoolConfigMapperTest

Each: ~5 tests = **30 tests total**

---

## DTO Validation Tests (Priority 5)

### DTO Validation Test Pattern

```java
class StudentRequestDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should validate successfully with valid data")
    void shouldValidateSuccessfully()

    @Test
    @DisplayName("Should fail when firstName is blank")
    void shouldFailWhenFirstNameIsBlank()

    // More validation tests...
}
```

---

### 1. StudentRequestDTOValidationTest (Required - ~15 tests)

**File**: `src/test/java/com/school/management/dto/request/StudentRequestDTOValidationTest.java`

Test all validation constraints:
- @NotBlank fields (firstName, lastName, address, mobile, motherName, fatherName)
- @Size constraints
- @Pattern for mobile (10 digits)
- @Past for dateOfBirth
- @NotNull for classId
- @Positive for classId

---

### Similar validation tests needed for:
- ClassRequestDTOValidationTest (~8 tests)
- FeeMasterRequestDTOValidationTest (~10 tests)
- FeeJournalRequestDTOValidationTest (~8 tests)
- FeeReceiptRequestDTOValidationTest (~12 tests)
- SchoolConfigRequestDTOValidationTest (~8 tests)

**Total**: ~60 validation tests

---

## Exception Handler Tests (Priority 6)

### GlobalExceptionHandlerTest (Required - ~6 tests)

**File**: `src/test/java/com/school/management/config/GlobalExceptionHandlerTest.java`

#### Test Cases:

```java
@Test
@DisplayName("Should handle ResourceNotFoundException and return 404")
void shouldHandleResourceNotFoundException()

@Test
@DisplayName("Should handle ValidationException and return 400")
void shouldHandleValidationException()

@Test
@DisplayName("Should handle DuplicateResourceException and return 400")
void shouldHandleDuplicateResourceException()

@Test
@DisplayName("Should handle MethodArgumentNotValidException and return 400 with field errors")
void shouldHandleMethodArgumentNotValidException()

@Test
@DisplayName("Should handle generic Exception and return 500")
void shouldHandleGenericException()

@Test
@DisplayName("Should include timestamp in error response")
void shouldIncludeTimestampInErrorResponse()
```

---

## Test Implementation Order

### Phase 1: Service Layer (Week 1-2)
**Priority: CRITICAL**
**Estimated Coverage Gain: +30%**

1. ✅ StudentServiceTest (DONE)
2. ClassServiceTest (10 tests)
3. FeeMasterServiceTest (15 tests)
4. FeeJournalServiceTest (18 tests)
5. FeeReceiptServiceTest (20 tests)
6. SchoolConfigServiceTest (12 tests)

**Total**: 75 tests

---

### Phase 2: Controller Layer (Week 3)
**Priority: HIGH**
**Estimated Coverage Gain: +20%**

1. StudentControllerTest (10 tests)
2. ClassControllerTest (10 tests)
3. FeeMasterControllerTest (12 tests)
4. FeeJournalControllerTest (12 tests)
5. FeeReceiptControllerTest (13 tests)
6. SchoolConfigControllerTest (10 tests)

**Total**: 67 tests

---

### Phase 3: Repository Layer (Week 4)
**Priority: MEDIUM**
**Estimated Coverage Gain: +15%**

1. StudentRepositoryTest (8 tests)
2. ClassRepositoryTest (6 tests)
3. FeeMasterRepositoryTest (8 tests)
4. FeeJournalRepositoryTest (8 tests)
5. FeeReceiptRepositoryTest (8 tests)
6. SchoolConfigRepositoryTest (5 tests)

**Total**: 43 tests

---

### Phase 4: Mapper & DTO Validation (Week 5)
**Priority: LOW-MEDIUM**
**Estimated Coverage Gain: +10%**

**Mappers**: 6 mapper classes × 5 tests = 30 tests
**DTO Validations**: 60 tests

**Total**: 90 tests

---

### Phase 5: Exception Handlers (Week 5)
**Priority: LOW**
**Estimated Coverage Gain: +5%**

1. GlobalExceptionHandlerTest (6 tests)

**Total**: 6 tests

---

## Summary

### Test Count by Category

| Category | Test Classes | Test Methods | Priority | Coverage Gain |
|----------|-------------|--------------|----------|---------------|
| ✅ Service (Done) | 1 | 7 | CRITICAL | 5% |
| Service (Remaining) | 5 | 75 | CRITICAL | 30% |
| Controller | 6 | 67 | HIGH | 20% |
| Repository | 6 | 43 | MEDIUM | 15% |
| Mapper | 6 | 30 | LOW-MEDIUM | 5% |
| DTO Validation | 6 | 60 | LOW-MEDIUM | 5% |
| Exception Handler | 1 | 6 | LOW | 5% |
| **TOTAL** | **31** | **288** | - | **85%** |

---

## Quick Start Command

```bash
# Run all tests with coverage
cd backend
mvn clean test jacoco:report

# View coverage report
start target/site/jacoco/index.html  # Windows
open target/site/jacoco/index.html   # Mac
xdg-open target/site/jacoco/index.html  # Linux
```

---

## Test Template Files

### Service Test Template
Located at: `docs/templates/ServiceTestTemplate.java`

### Controller Test Template
Located at: `docs/templates/ControllerTestTemplate.java`

### Repository Test Template
Located at: `docs/templates/RepositoryTestTemplate.java`

---

## Notes

1. **Follow StudentServiceTest** as the reference implementation
2. **Use Given-When-Then** structure for all tests
3. **Test both success and failure** scenarios
4. **Use @DisplayName** for readable test descriptions
5. **Parameterized tests** for similar scenarios with different inputs
6. **Verify mock interactions** to ensure correct behavior
7. **Test edge cases** (null values, empty lists, boundary conditions)
8. **Integration tests** should use real database with @DataJpaTest
9. **Controller tests** should use MockMvc and not call real services
10. **Maintain test isolation** - each test should be independent

---

## Expected Timeline

**5 weeks to 80% coverage** (assuming 1 developer, part-time)

- Week 1: Service tests (ClassService, FeeMasterService)
- Week 2: Service tests (FeeJournalService, FeeReceiptService, SchoolConfigService)
- Week 3: All controller tests
- Week 4: All repository tests
- Week 5: Mapper tests + DTO validation tests + Exception handler tests

**Accelerated timeline**: 2-3 weeks with full-time effort or multiple developers

---

## Success Criteria

✅ Minimum 80% instruction coverage
✅ All critical business logic tested
✅ All services have comprehensive unit tests
✅ All controllers have @WebMvcTest tests
✅ All custom repository queries tested
✅ All validation rules tested
✅ Exception handling tested
✅ Zero test failures
✅ Tests run in < 30 seconds

---

**Document Version**: 1.0
**Last Updated**: November 2, 2025
**Author**: School Management Team
