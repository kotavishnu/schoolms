# Testing Strategy: School Management System (AI-Agentic Optimized)
## 1. Overview & Test Pyramid
This strategy follows the industry-standard test pyramid to balance execution speed with system confidence, specifically tuned for an AI-driven development lifecycle.
**Distribution Rationale:**
* **Unit Tests (60%)**: Fast, isolated, and low-token context requirement.
* **Integration Tests (30%)**: Verifies component interactions and API contracts using real infrastructure mocks.
* **E2E Tests (10%)**: Validates critical user journeys; kept minimal to reduce AI-generation "hallucination-bloat".
---
## 2. Strict Token Budget Adherence
To ensure the AI agent operates within optimal context windows (reducing latency and cost), the following rules apply:
* **Signature-Based Prompting**: When generating tests, agents must be provided with method signatures and DTO definitions rather than full implementation files.
* **Context Pruning**: Large test suites must be split into functional modules (e.g., `StudentValidationTest.java` vs. `StudentPersistenceTest.java`) to keep individual prompt sizes under 2k tokens.
* **Shared Test Data Builders**: Centralized builders are mandatory to reduce the repetitive boilerplate tokens required in every test case.
* **Lean Locators**: Frontend E2E tests must use stable `data-testid` attributes to keep Playwright scripts concise and resistant to UI changes.

---

## 3. Backend Strategy

### 3.1 Unit Testing (Domain & Logic)

**Focus**: Business invariants and Value Objects.

```java
// Example: Student Age Logic - Optimized for minimal token context
@Test
@DisplayName("Should reject student below 3 years")
void shouldRejectUnderage() {
    LocalDate invalidBirthDate = LocalDate.now().minusYears(2);
    assertThatThrownBy(() -> Student.register(..., invalidBirthDate, ...))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("between 3 and 18 years");
}

```

### 3.2 Integration Testing (Persistence & Rules)

**Focus**: Database interactions and Drools rule execution.

* **Tooling**: TestContainers (PostgreSQL, Redis).
* **Agent Instruction**: Reuse the `@TestConfiguration` to avoid re-defining container setup in every prompt.

---

## 4. Frontend Strategy

### 4.1 Component & Hook Testing

**Focus**: Individual UI components and custom React hooks using **Vitest**.

* **MSW Integration**: Agents should mock all API responses using **Mock Service Worker** to test frontend logic without a running backend.

### 4.2 Playwright E2E Flows

**Focus**: High-value "Happy Path" registration and search.

* **Constraint**: E2E scripts must be kept under 50 lines. Use helper functions for form filling to conserve tokens.

---

## 5. Quality Gates & Coverage Targets

The CI/CD pipeline enforces these thresholds to ensure the AI agent maintains architectural integrity.

| Layer | Coverage Threshold | Metric |
| --- | --- | --- |
| **Domain Layer** | 95% | Line Coverage |
| **Application Layer** | 85% | Line Coverage |
| **Infrastructure** | 70% | Branch Coverage |
| **Frontend** | 70% | Statement Coverage |

---

## 6. Automated Data Management

### 6.1 Test Data Builders (Java Example)

```java
public class StudentTestDataBuilder {
    private String mobile = "+919876543210";
    // Build method abstracts 20+ lines of object setup into 1 line in the test
    public Student build() {
        return Student.register(..., Mobile.of(mobile), ...);
    }
}

```