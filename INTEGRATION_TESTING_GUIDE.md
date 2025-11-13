# Frontend-Backend Integration Testing Guide

**Date**: 2025-11-12
**Audience**: QA Engineers, Developers, Testers

---

## Overview

This guide provides step-by-step instructions for testing the School Management System frontend integration with the backend. Currently, **only authentication is fully functional**. Other modules can be tested for UI/UX only.

---

## Prerequisites

### Software Requirements

1. **Node.js**: v18 or higher
2. **Java**: JDK 21
3. **Maven**: 3.9+
4. **PostgreSQL**: 14 or higher
5. **Redis**: 7.0 or higher
6. **Git**: Latest version

### System Configuration

**PostgreSQL Database**:
```sql
CREATE DATABASE school_management;
CREATE USER school_admin WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE school_management TO school_admin;
```

**Redis Server**:
```bash
# Start Redis (default port 6379)
redis-server
```

---

## Setup Instructions

### Step 1: Clone and Setup Backend

```bash
# Navigate to project root
cd D:\wks-sms

# Verify Java version
java -version  # Should be Java 21

# Install dependencies
mvn clean install -DskipTests

# Verify application.yml configuration
# Check: src/main/resources/application-dev.yml
```

**Verify Backend Configuration** (`application-dev.yml`):
- Database URL: `jdbc:postgresql://localhost:5432/school_management`
- Redis host: `localhost:6379`
- Server port: `8080`
- Context path: `/api`

### Step 2: Setup Frontend

```bash
# Navigate to frontend directory
cd D:\wks-sms\frontend

# Install dependencies
npm install

# Verify .env configuration
cat .env  # Should show VITE_API_BASE_URL=http://localhost:8080/api/v1
```

### Step 3: Start Services

**Terminal 1 - PostgreSQL** (if not running as service):
```bash
# Start PostgreSQL server
pg_ctl -D /path/to/data start
```

**Terminal 2 - Redis**:
```bash
# Start Redis server
redis-server
```

**Terminal 3 - Backend**:
```bash
cd D:\wks-sms
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Wait for:
```
Started SchoolManagementApplication in X.XXX seconds
```

**Terminal 4 - Frontend**:
```bash
cd D:\wks-sms\frontend
npm run dev
```

Wait for:
```
  VITE v7.x.x  ready in XXX ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

---

## Testing Scenarios

### Scenario 1: Authentication Flow (Fully Functional) ✅

#### 1.1 Successful Login

**Steps**:
1. Open browser: `http://localhost:3000/login`
2. Observe login form renders correctly
3. Enter valid credentials:
   - Username: `admin` (or test user from backend)
   - Password: `admin123` (or test password)
4. Click "Login" button

**Expected Results**:
- ✅ Loading spinner appears briefly
- ✅ Success toast notification: "Login successful"
- ✅ Redirect to dashboard: `http://localhost:3000/`
- ✅ Sidebar menu appears
- ✅ User info displayed in header
- ✅ Browser localStorage contains:
  - `accessToken`: JWT token
  - `refreshToken`: Refresh token

**Verification**:
```javascript
// Open Browser DevTools (F12) -> Console
console.log(localStorage.getItem('accessToken'));
console.log(localStorage.getItem('refreshToken'));
```

**Backend Verification**:
```bash
# Check backend logs for successful authentication
# Should see: "Login successful for username: admin"
```

---

#### 1.2 Failed Login - Invalid Credentials

**Steps**:
1. Navigate to `http://localhost:3000/login`
2. Enter invalid credentials:
   - Username: `invalid_user`
   - Password: `wrong_password`
3. Click "Login"

**Expected Results**:
- ✅ Error toast: "Invalid username or password"
- ✅ Form remains on login page
- ✅ No tokens in localStorage

---

#### 1.3 Protected Route Access Without Auth

**Steps**:
1. Clear localStorage: `localStorage.clear()`
2. Navigate directly to: `http://localhost:3000/students`

**Expected Results**:
- ✅ Automatic redirect to `/login`
- ✅ Toast message: "Please login to continue"

---

#### 1.4 Token Refresh on Expiration

**Steps**:
1. Login successfully
2. Wait 15 minutes (access token expires)
3. Navigate to any page or trigger an API call
4. Observe network requests (DevTools -> Network tab)

**Expected Results**:
- ✅ Original request fails with 401 Unauthorized
- ✅ Automatic POST request to `/api/v1/auth/refresh`
- ✅ New access token received and stored
- ✅ Original request retried with new token
- ✅ Page loads successfully

**Manual Testing** (accelerated):
1. Login successfully
2. Open DevTools -> Application -> Local Storage
3. Manually expire access token (shorten expiration in backend config)
4. Trigger API call
5. Verify refresh flow

---

#### 1.5 Logout Flow

**Steps**:
1. Login successfully
2. Click user profile menu (top-right)
3. Click "Logout" button

**Expected Results**:
- ✅ POST request to `/api/v1/auth/logout`
- ✅ Tokens removed from localStorage
- ✅ Redirect to `/login`
- ✅ Toast: "Logout successful"

**Backend Verification**:
```bash
# Backend logs should show:
# "Logout successful for user: admin"
```

---

### Scenario 2: UI/UX Testing (No Backend Data) 🎨

Since Student, Class, Fee, and Payment REST APIs are not yet implemented, test the UI components without data.

#### 2.1 Student Management UI

**Test: Student List Page**

**Steps**:
1. Login successfully
2. Navigate to `http://localhost:3000/students`

**Expected Results**:
- ✅ Page loads with header "Students"
- ✅ "Add New Student" button visible
- ✅ Search bar present
- ✅ Filter dropdowns (Status, Class) render
- ✅ Empty state message: "No students found"
- ✅ Loading skeleton NOT shown (API fails quickly)

**Test: Student Registration Form**

**Steps**:
1. Navigate to `http://localhost:3000/students/new`
2. Fill out the form:
   - First Name: `John`
   - Last Name: `Doe`
   - Date of Birth: `2010-05-15`
   - Gender: `Male`
   - Mobile: `9876543210`
3. Try uploading a photo (any JPG/PNG)
4. Click "Register Student"

**Expected Results**:
- ✅ Form renders correctly with all fields
- ✅ Client-side validation works:
  - Required fields marked with red asterisk
  - Age validation (3-18 years)
  - Mobile number format validation
- ✅ Photo preview shows uploaded image
- ✅ Submit button shows "Registering..." loading state
- ❌ API call fails with error toast (expected - API not implemented)
- ✅ Error toast: "Network error" or "Failed to create student"

**UI Elements to Verify**:
- [ ] All form labels are readable
- [ ] Input fields have proper focus states
- [ ] Validation errors display in red below fields
- [ ] Date picker works correctly
- [ ] Photo upload button accepts JPG/PNG only
- [ ] Cancel button navigates back to student list

---

#### 2.2 Class Management UI

**Test: Class List Page**

**Steps**:
1. Navigate to `http://localhost:3000/classes`

**Expected Results**:
- ✅ Card grid layout renders
- ✅ Filter dropdowns (Academic Year, Grade) present
- ✅ "Create New Class" button visible
- ✅ Empty state: "No classes found"

**Test: Class Creation Form**

**Steps**:
1. Navigate to `http://localhost:3000/classes/new`
2. Fill form:
   - Class Name: `Class 5`
   - Section: `A`
   - Academic Year: (select any)
   - Max Capacity: `40`
   - Room Number: `A-101`
3. Click "Create Class"

**Expected Results**:
- ✅ Form validation works
- ✅ Capacity input only accepts 1-100
- ❌ Submit fails (API not implemented)
- ✅ Error handling UI displays

---

#### 2.3 Fee Management UI

**Test: Fee Structure List**

**Steps**:
1. Navigate to `http://localhost:3000/fees/structures`

**Expected Results**:
- ✅ Table layout with columns: Name, Year, Frequency, Amount, Status
- ✅ Filter dropdowns render
- ✅ "Create Fee Structure" button present
- ✅ Empty state displays

**Test: Fee Structure Creation**

**Steps**:
1. Navigate to `http://localhost:3000/fees/structures/new`
2. Fill basic info:
   - Structure Name: `Annual Fees 2024-2025`
   - Academic Year: (select)
   - Frequency: `ANNUAL`
3. Add fee components:
   - Click "Add Component"
   - Fee Name: `Tuition Fee`
   - Amount: `50000`
   - Fee Type: `TUITION`
4. Configure due date
5. Click "Create Fee Structure"

**Expected Results**:
- ✅ Multi-step form works
- ✅ Dynamic component addition works
- ✅ Total amount calculates automatically
- ✅ Form validation enforces required fields
- ❌ Submit fails (expected)

---

#### 2.4 Payment Recording UI

**Test: Payment Form**

**Steps**:
1. Navigate to `http://localhost:3000/payments/record`

**Expected Results**:
- ✅ Student search box renders
- ✅ Fee selection dropdowns present
- ✅ Payment method radio buttons work
- ✅ Amount input accepts numbers only
- ✅ Payment summary section displays
- ❌ Student search returns no results (API missing)

---

### Scenario 3: Responsive Design Testing 📱

Test UI responsiveness across devices.

**Steps**:
1. Open any page (e.g., `/students`)
2. Open DevTools (F12) -> Toggle device toolbar (Ctrl+Shift+M)
3. Test different viewports:
   - Mobile: 375px width (iPhone SE)
   - Tablet: 768px width (iPad)
   - Desktop: 1920px width

**Expected Results for Each Viewport**:

**Mobile (< 768px)**:
- ✅ Sidebar collapses to hamburger menu
- ✅ Tables scroll horizontally
- ✅ Form fields stack vertically
- ✅ Buttons are full-width
- ✅ Text is readable without zoom

**Tablet (768px - 1024px)**:
- ✅ Sidebar shows icons only or collapses
- ✅ Grid layouts adapt to 2 columns
- ✅ Forms remain usable

**Desktop (> 1024px)**:
- ✅ Full sidebar with labels visible
- ✅ Grid layouts show 3-4 columns
- ✅ Optimal spacing and typography

---

### Scenario 4: Accessibility Testing ♿

**Keyboard Navigation**:

**Steps**:
1. Navigate to `/students/new`
2. Use keyboard only (no mouse):
   - Press `Tab` to move between fields
   - Press `Enter` to submit
   - Press `Esc` to close modals

**Expected Results**:
- ✅ Tab order is logical (top to bottom, left to right)
- ✅ Focus indicators visible on all interactive elements
- ✅ All buttons accessible via keyboard
- ✅ Form can be submitted with Enter key

**Screen Reader Testing** (NVDA/JAWS):

**Steps**:
1. Enable screen reader
2. Navigate through student registration form

**Expected Results**:
- ✅ All labels read aloud
- ✅ Required fields announced
- ✅ Validation errors announced
- ✅ Button purposes clear

---

## API Testing with Postman/cURL

Since only authentication works, test the auth endpoints:

### Test Login Endpoint

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected Response**:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "refresh_token_here",
    "user": {
      "userId": 1,
      "username": "admin",
      "firstName": "Admin",
      "lastName": "User",
      "role": "ADMIN"
    }
  },
  "timestamp": "2025-11-12T10:30:00Z"
}
```

### Test Protected Endpoint (Should Fail - Not Implemented)

```bash
curl -X GET http://localhost:8080/api/v1/students \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Expected Response**:
```
404 Not Found (endpoint doesn't exist)
```

---

## Common Issues & Troubleshooting

### Issue 1: Backend Won't Start

**Symptoms**:
- `mvn spring-boot:run` fails
- Database connection errors

**Solution**:
```bash
# Check PostgreSQL is running
psql -U postgres -c "SELECT version();"

# Verify database exists
psql -U postgres -c "\l" | grep school_management

# Check application-dev.yml has correct DB credentials
cat src/main/resources/application-dev.yml | grep datasource
```

---

### Issue 2: Frontend Can't Connect to Backend

**Symptoms**:
- Network errors in browser console
- CORS errors

**Solution**:
1. Verify backend is running: `curl http://localhost:8080/api/actuator/health`
2. Check `.env` file: `VITE_API_BASE_URL=http://localhost:8080/api/v1`
3. Verify CORS in `application-dev.yml` allows `http://localhost:3000`

---

### Issue 3: Login Fails with 401

**Symptoms**:
- "Invalid username or password" error
- Correct credentials used

**Solution**:
```bash
# Check if test users exist in database
psql -U school_admin -d school_management -c "SELECT username, role FROM users;"

# If no users, you may need to run migrations or seed data
```

---

### Issue 4: Token Refresh Not Working

**Symptoms**:
- Redirected to login after 15 minutes
- Token refresh request fails

**Solution**:
1. Check Redis is running: `redis-cli ping` (should return `PONG`)
2. Verify refresh token exists in Redis:
   ```bash
   redis-cli
   KEYS refresh_token:*
   ```
3. Check browser localStorage has `refreshToken`

---

## Test Checklist

### Authentication Module ✅

- [ ] Login with valid credentials succeeds
- [ ] Login with invalid credentials fails
- [ ] Protected routes redirect to login when not authenticated
- [ ] Access token auto-refreshes on expiration
- [ ] Logout clears tokens and redirects to login
- [ ] Rate limiting prevents brute force (100 attempts/minute)
- [ ] Account lockout after 5 failed attempts

### Student Module (UI Only) 🎨

- [ ] Student list page renders correctly
- [ ] Search and filter UI works
- [ ] Registration form validation works
- [ ] Photo upload UI functions
- [ ] Form reset on cancel works
- [ ] Empty state displays when no data
- [ ] Loading states show appropriately

### Class Module (UI Only) 🎨

- [ ] Class list displays in card grid
- [ ] Filters render and change
- [ ] Create class form validates input
- [ ] Capacity input enforces 1-100 range
- [ ] Section dropdown works
- [ ] Empty state displays

### Fee Module (UI Only) 🎨

- [ ] Fee structure list table renders
- [ ] Create form with multiple steps works
- [ ] Dynamic fee component addition works
- [ ] Total amount calculates correctly
- [ ] Due date configuration UI functions

### Payment Module (UI Only) 🎨

- [ ] Payment recording form renders
- [ ] Payment method selection works
- [ ] Amount input validation works
- [ ] Payment history table displays
- [ ] Receipt page layout correct
- [ ] Print functionality works (layout only)

### Responsive Design 📱

- [ ] Mobile view (375px) displays correctly
- [ ] Tablet view (768px) displays correctly
- [ ] Desktop view (1920px) displays correctly
- [ ] Navigation adapts to screen size
- [ ] Tables scroll or adapt on small screens

### Accessibility ♿

- [ ] Keyboard navigation works on all pages
- [ ] Focus indicators visible
- [ ] ARIA labels present on interactive elements
- [ ] Form errors announced to screen readers
- [ ] Color contrast meets WCAG 2.1 AA

---

## Next Steps After Backend API Implementation

Once backend REST controllers are implemented for Student, Class, Fee, and Payment modules:

1. **Re-run all tests** with full backend integration
2. **Test CRUD operations**:
   - Create, Read, Update, Delete for each entity
   - Verify data persistence
3. **Test search and filtering** with real data
4. **Test pagination** with 50+ records
5. **Test file uploads** (student photos)
6. **Test data validation** end-to-end
7. **Performance testing** with large datasets
8. **Security testing** (XSS, SQL injection, CSRF)

---

## Success Metrics

### Phase 1: Authentication Only (Current)
- ✅ Login success rate > 99%
- ✅ Token refresh success rate > 99%
- ✅ Zero unauthorized access to protected routes

### Phase 2: Full Integration (After Backend APIs)
- [ ] All CRUD operations succeed
- [ ] Search returns correct results in < 500ms
- [ ] Page load time < 2 seconds
- [ ] Zero data loss on operations
- [ ] API response time < 200ms (95th percentile)

---

## Reporting Bugs

When reporting issues, include:

1. **Environment**:
   - OS: Windows/Linux/Mac
   - Browser: Chrome/Firefox/Safari (with version)
   - Node.js version
   - Java version

2. **Steps to Reproduce**:
   - Exact actions taken
   - URL accessed
   - Data entered

3. **Expected vs Actual**:
   - What should happen
   - What actually happened

4. **Evidence**:
   - Screenshots
   - Browser console errors
   - Network tab showing failed requests
   - Backend logs (if applicable)

5. **Additional Context**:
   - User role (Admin, Teacher, Staff)
   - Time of occurrence
   - Any error messages displayed

---

## Conclusion

Currently, the frontend is **fully ready for testing**, but **only authentication works end-to-end** with the backend. All other modules can be tested for:
- UI/UX quality
- Client-side validation
- Responsive design
- Accessibility
- Navigation flow

**Full integration testing** will be possible once backend REST APIs are implemented for Student, Class, Fee, and Payment modules.

---

**Document Version**: 1.0
**Last Updated**: 2025-11-12
**Next Review**: After Sprint 2 Backend REST APIs completion
