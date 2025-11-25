# Testing Guide - School Management System Frontend

## Quick Start

### 1. Start Backend Services
Ensure the backend is running on `http://localhost:8081`

### 2. Start Frontend Development Server

```bash
cd D:\wks-sms-specs-itr2\sms-frontend
npm run dev
```

Open your browser to: **http://localhost:5173**

---

## Testing Scenarios

### Home Page
1. Navigate to `/`
2. Verify statistics cards display (Total Students, Active Students)
3. Click on "Manage Students" - should navigate to `/students`
4. Click on "Register Student" - should navigate to `/students/create`
5. Click on "System Configuration" - should navigate to `/configurations`

### Student Management

#### Create Student
1. Navigate to `/students/create`
2. Fill in all required fields:
   - First Name: "John"
   - Last Name: "Doe"
   - Date of Birth: (3-18 years old)
   - Mobile: "9876543210"
   - Address: "123 Main Street, City"
   - Father's Name: "Richard Doe"
3. Optional fields:
   - Email: "john.doe@example.com"
   - Aadhaar: "123456789012"
   - Mother's Name: "Jane Doe"
4. Click "Register Student"
5. Verify success notification appears
6. Verify redirect to student detail page

#### View Students List
1. Navigate to `/students`
2. Verify student cards display in grid layout
3. Verify pagination controls at bottom (if > 20 students)
4. Verify total count displays correctly

#### Search and Filter
1. On `/students` page
2. Enter last name in search box - wait 500ms for debounce
3. Verify filtered results appear
4. Select status filter (Active, Inactive, etc.)
5. Verify filtered results
6. Click "Clear Filters" - verify all students show again

#### View Student Details
1. From student list, click "View Details" on a card
2. Verify all student information displays:
   - Personal Information section
   - Guardian Information section
   - System Information section
3. Verify status badge shows correct color
4. Verify action buttons present (Edit, Delete, Back)

#### Edit Student
1. From student detail page, click "Edit Student"
2. Form should be pre-filled with current data
3. Modify First Name
4. Click "Update Student"
5. Verify success notification
6. Verify redirect to detail page with updated info

#### Delete Student
1. From student detail page, click "Delete Student"
2. Verify confirmation dialog appears
3. Click "Cancel" - dialog closes, student remains
4. Click "Delete Student" again
5. Click "Delete" in dialog
6. Verify success notification
7. Verify redirect to students list
8. Verify student no longer appears in list

### Configuration Management

#### Create Configuration
1. Navigate to `/configurations`
2. Click "Add New Configuration"
3. Modal should open
4. Fill in form:
   - Category: Select from dropdown
   - Config Key: "TEST_KEY"
   - Config Value: "TEST_VALUE"
   - Description: "Test configuration"
5. Click "Create Configuration"
6. Verify modal closes
7. Verify success notification
8. Verify new config appears in table

#### Edit Configuration
1. Find a configuration in the table
2. Click "Edit" button
3. Modal opens with pre-filled data
4. Modify Config Value
5. Click "Update Configuration"
6. Verify modal closes
7. Verify success notification
8. Verify updated value in table

#### Delete Configuration
1. Find a configuration in the table
2. Click "Delete" button
3. Confirmation dialog appears
4. Click "Delete"
5. Verify success notification
6. Verify configuration removed from table

### UI/UX Features

#### Theme Toggle
1. Click sun/moon icon in header
2. Verify page switches between light and dark mode
3. Verify all components render correctly in both modes
4. Verify theme persists on page reload

#### Notifications
1. Perform any CRUD operation
2. Verify toast notification appears in top-right
3. Verify notification auto-dismisses after 5 seconds
4. Click X button - verify notification dismisses immediately
5. Multiple notifications should stack vertically

#### Error Handling
1. Stop backend server
2. Try to load `/students` page
3. Verify error message displays
4. Verify "Retry" button appears
5. Start backend
6. Click "Retry"
7. Verify data loads successfully

#### Responsive Design
1. Resize browser window to mobile size (< 768px)
2. Verify:
   - Header collapses to mobile menu (hamburger icon)
   - Student cards stack vertically
   - Tables scroll horizontally
   - Forms stack fields vertically
   - All buttons remain accessible

#### Navigation
1. Test all navigation links in header
2. Use browser back/forward buttons
3. Test breadcrumb navigation (if present)
4. Try invalid URL like `/invalid-route`
5. Verify 404 page appears
6. Click "Go Home" - returns to home page

---

## Validation Testing

### Student Form Validation

#### Test Required Fields
1. Try to submit form with empty fields
2. Verify error messages appear below each required field

#### Test Field Constraints
- **First Name**: Min 1, Max 100 characters
- **Last Name**: Min 1, Max 100 characters
- **Address**: Min 10, Max 500 characters
- **Mobile**: Must be 10-15 digits
- **Date of Birth**: Must result in age 3-18
- **Father Name**: Required, Max 100
- **Email**: Must be valid email format (optional)
- **Aadhaar**: Must be exactly 12 digits (optional)

#### Test Invalid Data
1. Enter "abc" in mobile field - verify error
2. Enter date that results in age 2 - verify error
3. Enter date that results in age 19 - verify error
4. Enter invalid email - verify error
5. Enter 11-digit Aadhaar - verify error

### Configuration Form Validation
- **Category**: Must select from dropdown
- **Config Key**: Required, Max 100 characters
- **Config Value**: Required
- **Description**: Optional, Max 500 characters

---

## Performance Testing

1. Load page with 100+ students
2. Verify pagination works smoothly
3. Test search/filter with large dataset
4. Verify no significant lag

---

## Accessibility Testing

1. Navigate using keyboard only (Tab, Enter, Esc)
2. Verify all interactive elements are focusable
3. Verify focus visible (blue outline)
4. Test screen reader compatibility (if available)
5. Verify form labels are properly associated
6. Check contrast ratios in both themes

---

## Browser Compatibility

Test in:
- [ ] Chrome (latest)
- [ ] Firefox (latest)
- [ ] Edge (latest)
- [ ] Safari (if available)

---

## Common Issues & Solutions

### Issue: Page shows "Loading..." indefinitely
**Solution**: Check if backend is running on localhost:8081

### Issue: "Network Error" in console
**Solution**: Verify backend API is accessible

### Issue: Form submits but shows error
**Solution**: Check backend logs for validation errors

### Issue: Dark mode doesn't work
**Solution**: Clear browser cache and reload

### Issue: Search doesn't work
**Solution**: Wait for 500ms debounce delay

---

## Test Data

### Sample Student Data

```
First Name: Alice
Last Name: Johnson
DOB: 2010-05-15 (Age: 13-14)
Mobile: 9876543210
Address: 123 Elm Street, Springfield
Father Name: Robert Johnson
Mother Name: Mary Johnson
Email: alice.johnson@school.com
Aadhaar: 123456789012
```

### Sample Configuration Data

```
Category: Academic
Key: MAX_CLASS_SIZE
Value: 30
Description: Maximum number of students per class
```

---

## Bug Reporting

If you find bugs, please document:
1. Steps to reproduce
2. Expected behavior
3. Actual behavior
4. Browser and version
5. Console errors (if any)
6. Screenshots (if applicable)

---

## Success Criteria

Frontend is considered ready for production when:
- [ ] All CRUD operations work correctly
- [ ] All validation rules work correctly
- [ ] No TypeScript compilation errors
- [ ] No console errors during normal operation
- [ ] Responsive design works on mobile, tablet, desktop
- [ ] Light and dark themes work correctly
- [ ] All error scenarios handled gracefully
- [ ] Loading states display correctly
- [ ] Notifications work correctly
- [ ] Navigation works smoothly
- [ ] Performance is acceptable

---

**Happy Testing!**
