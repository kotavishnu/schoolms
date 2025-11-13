# Quick Fix Guide - School Management System QA Issues

**Date**: November 12, 2025
**Priority**: CRITICAL
**Estimated Time**: 4-8 hours

---

## Critical Issues to Fix Immediately

### Issue 1: Backend Compilation Failures (CRITICAL)

**Problem**: 100+ compilation errors due to missing Lombok-generated methods

**Root Cause**: Missing Lombok annotations on domain entities

**Fix Steps**:

#### Step 1: Add Missing Annotations to AcademicYear.java

**File**: `src/main/java/com/school/management/domain/academic/AcademicYear.java`

Add these annotations:
```java
package com.school.management.domain.academic;

import com.school.management.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;  // ADD THIS
import lombok.Data;     // ADD THIS
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // ADD THIS (if logging needed)

@Entity
@Table(name = "academic_years")
@Data                              // ADD THIS
@Builder                           // ADD THIS
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AcademicYear extends BaseEntity {
    // ... rest of class
}
```

#### Step 2: Add Missing Annotations to SchoolClass.java

**File**: `src/main/java/com/school/management/domain/academic/SchoolClass.java`

```java
@Entity
@Table(name = "classes")
@Data                              // ADD THIS
@Builder                           // ADD THIS
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SchoolClass extends BaseEntity {
    // ... rest of class
}
```

#### Step 3: Add Missing Annotations to FeeStructure.java

**File**: `src/main/java/com/school/management/domain/fee/FeeStructure.java`

```java
@Entity
@Table(name = "fee_structures")
@Data                              // ADD THIS
@Builder                           // ADD THIS
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FeeStructure extends BaseEntity {
    // ... rest of class
}
```

#### Step 4: Add @Slf4j to Service Classes

**Files to Update**:
- `src/main/java/com/school/management/application/service/AcademicYearService.java`
- `src/main/java/com/school/management/application/service/SchoolClassService.java`
- `src/main/java/com/school/management/presentation/exception/GlobalExceptionHandler.java`

Add at class level:
```java
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j  // ADD THIS
public class AcademicYearService {
    // Now 'log' variable will be available
}
```

#### Step 5: Add @Builder to DTOs

**Files to Update**:
- `src/main/java/com/school/management/presentation/dto/PageableRequest.java`

```java
import lombok.Builder;

@Data
@Builder  // ADD THIS
@NoArgsConstructor
@AllArgsConstructor
public class PageableRequest {
    // ... fields
}
```

#### Step 6: Verify Compilation

```bash
cd D:\wks-sms
mvn clean compile
```

**Expected Output**:
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX s
```

**If Still Failing**:
- Check Lombok plugin is installed in your IDE
- Verify `lombok.version=1.18.30` in pom.xml
- Run `mvn dependency:purge-local-repository` to refresh dependencies

---

### Issue 2: Guardian.java Structural Error (FIXED)

**Status**: Already fixed during QA assessment

**What was fixed**: Removed duplicate method definition and code outside class

**Verification**: File compiles correctly now

---

### Issue 3: Frontend Tests Missing (CRITICAL)

**Problem**: Zero test files exist for frontend

**Quick Fix**: Create basic authentication tests

#### Step 1: Create Test for LoginForm

**File**: `frontend/src/features/auth/__tests__/LoginForm.test.tsx`

```typescript
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import LoginForm from '../components/LoginForm';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: false },
  },
});

const renderLoginForm = () => {
  return render(
    <QueryClientProvider client={queryClient}>
      <LoginForm />
    </QueryClientProvider>
  );
};

describe('LoginForm', () => {
  it('should render login form with username and password fields', () => {
    renderLoginForm();

    expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
  });

  it('should show validation errors for empty fields', async () => {
    renderLoginForm();

    const loginButton = screen.getByRole('button', { name: /login/i });
    await userEvent.click(loginButton);

    await waitFor(() => {
      expect(screen.getByText(/username is required/i)).toBeInTheDocument();
      expect(screen.getByText(/password is required/i)).toBeInTheDocument();
    });
  });

  it('should enable login button when form is valid', async () => {
    renderLoginForm();

    const usernameInput = screen.getByLabelText(/username/i);
    const passwordInput = screen.getByLabelText(/password/i);
    const loginButton = screen.getByRole('button', { name: /login/i });

    await userEvent.type(usernameInput, 'admin');
    await userEvent.type(passwordInput, 'password123');

    expect(loginButton).not.toBeDisabled();
  });
});
```

#### Step 2: Create Test Directory Structure

```bash
cd D:\wks-sms\frontend
mkdir -p src/features/auth/__tests__
mkdir -p src/shared/components/__tests__
mkdir -p src/test/utils
```

#### Step 3: Run Frontend Tests

```bash
cd D:\wks-sms\frontend
npm run test
```

**Expected Output**:
```
✓ src/features/auth/__tests__/LoginForm.test.tsx (3)
  ✓ LoginForm (3)
    ✓ should render login form with username and password fields
    ✓ should show validation errors for empty fields
    ✓ should enable login button when form is valid

Test Files  1 passed (1)
     Tests  3 passed (3)
```

---

## Validation Checklist

After applying fixes, verify:

### Backend Validation

```bash
cd D:\wks-sms

# 1. Clean and compile
mvn clean compile
# Expected: BUILD SUCCESS

# 2. Run unit tests
mvn test
# Expected: Tests run (may have some failures, but should execute)

# 3. Generate coverage report
mvn test jacoco:report
# Expected: Report generated at target/site/jacoco/index.html

# 4. Start backend server
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# Expected: "Started SchoolManagementApplication"

# 5. Test health endpoint
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

### Frontend Validation

```bash
cd D:\wks-sms\frontend

# 1. Install dependencies (if needed)
npm install

# 2. Run tests
npm run test
# Expected: X tests passing

# 3. Generate coverage
npm run test:coverage
# Expected: Coverage report generated

# 4. Start dev server
npm run dev
# Expected: "ready in XXX ms"

# 5. Test frontend
curl http://localhost:3000
# Expected: HTML content
```

---

## Common Issues and Solutions

### Issue: Lombok Still Not Working

**Solution**:
```bash
# Verify Lombok is in dependencies
mvn dependency:tree | grep lombok

# Reinstall Lombok
mvn dependency:purge-local-repository -DactOnly=true -DreResolve=false -Dinclude=org.projectlombok:lombok

# Rebuild
mvn clean compile
```

### Issue: Tests Still Failing to Compile

**Solution**:
1. Check if test dependencies are resolved:
   ```bash
   mvn dependency:resolve -Dclassifier=tests
   ```

2. Update Maven indices:
   ```bash
   mvn dependency:resolve -U
   ```

3. Clean Maven cache:
   ```bash
   rm -rf ~/.m2/repository/org/springframework
   mvn clean install
   ```

### Issue: TestContainers Requires Docker

**Solution**:
```bash
# Install Docker Desktop
# Start Docker
docker --version
# Should show: Docker version XX.XX.X

# Verify Docker is running
docker ps
```

### Issue: Frontend Tests Fail with Module Errors

**Solution**:
```bash
cd frontend

# Clear node_modules
rm -rf node_modules package-lock.json

# Reinstall
npm install

# Run tests
npm run test
```

---

## Priority Order for Fixes

**Priority 1 (Do First)**:
1. Fix AcademicYear.java annotations
2. Fix SchoolClass.java annotations
3. Test compilation: `mvn clean compile`

**Priority 2 (Do Second)**:
4. Add @Slf4j to service classes
5. Fix PageableRequest.java
6. Test again: `mvn clean compile`

**Priority 3 (Do Third)**:
7. Run unit tests: `mvn test`
8. Review test failures
9. Fix any remaining issues

**Priority 4 (Do Fourth)**:
10. Create basic frontend tests
11. Run frontend tests: `npm run test`
12. Generate coverage reports

---

## Expected Timeline

| Task | Estimated Time | Status |
|------|---------------|---------|
| Add Lombok annotations to domain entities | 1 hour | ⏳ Pending |
| Add @Slf4j to services | 30 minutes | ⏳ Pending |
| Fix DTO annotations | 30 minutes | ⏳ Pending |
| Verify compilation | 15 minutes | ⏳ Pending |
| Run backend tests | 30 minutes | ⏳ Pending |
| Create basic frontend tests | 2 hours | ⏳ Pending |
| Run frontend tests | 15 minutes | ⏳ Pending |
| Generate reports | 30 minutes | ⏳ Pending |
| **TOTAL** | **5-6 hours** | |

---

## Success Criteria

Mark as complete when:
- [ ] Backend compiles without errors
- [ ] Backend tests execute (even if some fail)
- [ ] Frontend has at least 5 test files
- [ ] Frontend tests execute successfully
- [ ] Both coverage reports can be generated
- [ ] Status document updated

---

## Next Steps After Quick Fixes

Once these critical issues are resolved:

1. **Review Test Results**: Document which tests pass/fail
2. **Analyze Coverage**: Identify gaps in test coverage
3. **Implement Missing Tests**: Work towards 80% backend, 70% frontend
4. **Complete REST APIs**: Implement Student, Class, Fee, Payment controllers
5. **Integration Testing**: Test frontend-backend integration
6. **Performance Testing**: Load test critical endpoints
7. **Security Testing**: Verify authentication, authorization
8. **UAT Preparation**: Prepare for user acceptance testing

---

## Support

If you encounter issues with these fixes:

1. Check the comprehensive QA report: `D:\wks-sms\reports\COMPREHENSIVE_QA_REPORT.md`
2. Review compilation logs: `D:\wks-sms\test-output.log`
3. Check status: `D:\wks-sms\status\qa-status.json`
4. Contact QA team for assistance

---

**Document Version**: 1.0
**Last Updated**: November 12, 2025
**Next Update**: After fixes applied
