# Frontend Testing Guide

This guide provides step-by-step instructions to test all features of the School Management System frontend.

## Prerequisites

1. **Backend must be running**
   - Start the backend server: It should be accessible at http://localhost:8080
   - Verify by visiting: http://localhost:8080/api/students

2. **Frontend must be running**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   - Access at: http://localhost:3000

## Testing Checklist

### 1. Dashboard Testing

**Steps:**
1. Navigate to http://localhost:3000
2. Verify dashboard loads successfully
3. Check that all stat cards display:
   - Total Classes
   - Total Students
   - Active Fee Masters
   - Today's Receipts
   - Today's Collection
4. Click on each stat card - should navigate to respective page
5. Click on Quick Action buttons - should navigate correctly
6. Verify system information shows current academic year

**Expected Results:**
- All stats display correctly (may be 0 if database is empty)
- Navigation works for all cards and buttons
- No console errors

---

### 2. Class Management Testing

**Steps:**

**2.1 Create Class**
1. Click "Classes" in sidebar
2. Click "Add New Class" button
3. Fill in form:
   - Class Number: 5
   - Section: A
   - Academic Year: 2024-2025
   - Capacity: 40
   - Room Number: 101
4. Click "Create Class"
5. Verify success notification appears
6. Verify class appears in table

**2.2 Validation Testing**
1. Try creating class without required fields - should show errors
2. Try invalid class number (11) - should show error
3. Try invalid academic year format - should show error
4. Try zero or negative capacity - should show error

**2.3 Filter Testing**
1. Select academic year from filter dropdown
2. Verify only classes for that year are shown
3. Change academic year - table should update

**2.4 Edit Class**
1. Click "Edit" on any class
2. Modify capacity to 45
3. Click "Update Class"
4. Verify success notification
5. Verify updated capacity in table

**2.5 Delete Class**
1. Click "Delete" on a class
2. Verify confirmation dialog appears
3. Click "Cancel" - dialog should close, class not deleted
4. Click "Delete" again, then "Delete" in dialog
5. Verify success notification
6. Verify class removed from table

**Expected Results:**
- All CRUD operations work correctly
- Validation prevents invalid data
- Filters work properly
- Notifications appear for all actions

---

### 3. Student Management Testing

**Steps:**

**3.1 Create Student**
1. Click "Students" in sidebar
2. Click "Add New Student" button
3. Fill in form:
   - First Name: John
   - Last Name: Doe
   - Date of Birth: 2010-01-15 (should be 3-18 years old)
   - Mobile: 9876543210
   - Email: john@example.com (optional)
   - Class: Select any class
   - Enrollment Date: Today or past date
   - Status: ACTIVE
   - Address: Optional
4. Click "Create Student"
5. Verify success notification
6. Verify student appears in table

**3.2 Validation Testing**
1. Try mobile number starting with 5 - should show error
2. Try 9-digit mobile - should show error
3. Try age < 3 or > 18 - should show error
4. Try future enrollment date - should show error
5. Try invalid email format - should show error

**3.3 Search Testing**
1. Type "Jo" in search box - should not trigger (min 3 chars)
2. Type "Joh" - should trigger search after 500ms delay
3. Verify matching students appear
4. Clear search - all students reappear

**3.4 Filter Testing**
1. Select a class from filter dropdown
2. Verify only students from that class are shown
3. Combine with search - both filters should apply

**3.5 Edit Student**
1. Click "Edit" on a student
2. Change status to "INACTIVE"
3. Update details
4. Verify success notification
5. Verify badge shows "INACTIVE"

**3.6 Delete Student**
1. Click "Delete" on a student
2. Confirm deletion
3. Verify student removed

**Expected Results:**
- Search debounces correctly
- All validations work
- Status badges show correct colors
- Filters work independently and together

---

### 4. Fee Master Management Testing

**Steps:**

**4.1 Create Fee Master**
1. Click "Fee Masters" in sidebar
2. Click "Add Fee Master" button
3. Fill in form:
   - Fee Type: TUITION
   - Amount: 5000
   - Frequency: MONTHLY
   - Applicable From: Today or past date
   - Applicable To: Leave empty or future date
   - Active: Checked
   - Description: Optional
4. Click "Create Fee Master"
5. Verify success notification
6. Verify fee master appears in table

**4.2 Validation Testing**
1. Try amount = 0 or negative - should show error
2. Try "Applicable To" before "Applicable From" - should show error
3. Try past "Applicable From" date - should work
4. Try future "Applicable From" date - should show error

**4.3 Filter Testing**
1. Select fee type from filter - only that type shown
2. Check "Show active only" - only active fee masters shown
3. Test both filters together

**4.4 Activate/Deactivate**
1. Click "Deactivate" on an active fee master
2. Verify success notification
3. Verify status badge changes to "Inactive"
4. Click "Activate" - should reactivate

**4.5 Edit and Delete**
1. Edit fee master - change amount
2. Verify update works
3. Delete fee master
4. Verify deletion works

**Expected Results:**
- Amount displays in currency format
- Status toggles correctly
- Filters work properly
- All fee types are available

---

### 5. Fee Journal Management Testing

**Steps:**

**5.1 Create Fee Journal**
1. Click "Fee Journals" in sidebar
2. Click "Add Fee Journal" button
3. Fill in form:
   - Student: Select a student
   - Month: December
   - Year: 2025
   - Amount Due: 5000
   - Due Date: Future date
   - Remarks: Optional
4. Click "Create Fee Journal"
5. Verify success notification
6. Verify journal appears in table

**5.2 Validation Testing**
1. Try year < 2000 or > 2100 - should show error
2. Try past due date - should show error
3. Try negative amount - should show error

**5.3 Filter Testing**
1. Select month from filter - only that month shown
2. Change year - journals update
3. Select status filter - only matching status shown
4. Test combinations of filters

**5.4 Record Payment**
1. Click "Pay" on a pending journal
2. Enter payment amount (full or partial)
3. Click "Record Payment"
4. Verify success notification
5. Verify:
   - Amount Paid updated
   - Balance calculated correctly
   - Status updated (PARTIAL or PAID)
6. For partial payment:
   - "Pay" button still visible
   - Can record additional payments

**5.5 Edit and Delete**
1. Edit journal - change amount
2. Verify update works
3. Delete journal
4. Verify deletion works

**Expected Results:**
- Payment status updates correctly
- Balance calculated accurately
- OVERDUE status shows for past due dates
- Filters work correctly

---

### 6. Fee Receipt Management Testing

**Steps:**

**6.1 Generate Receipt - Cash Payment**
1. Click "Fee Receipts" in sidebar
2. Click "Generate Receipt" button
3. Fill in form:
   - Student: Select student
   - Amount: 10000
   - Payment Date: Today or past date
   - Payment Method: CASH
   - Months Paid: Check multiple months (Jan, Feb, Mar)
   - Remarks: Optional
4. Click "Generate Receipt"
5. Verify success notification
6. Verify receipt appears with auto-generated receipt number

**6.2 Generate Receipt - Online Payment**
1. Click "Generate Receipt"
2. Select student
3. Enter amount
4. Select Payment Method: ONLINE
5. Verify "Transaction ID" field appears
6. Enter transaction ID: TXN123456
7. Select months
8. Generate receipt
9. Verify transaction ID shown in table

**6.3 Generate Receipt - Cheque Payment**
1. Generate new receipt
2. Select Payment Method: CHEQUE
3. Verify both "Cheque Number" and "Bank Name" fields appear
4. Fill both fields
5. Generate receipt
6. Verify cheque number shown in table

**6.4 Validation Testing**
1. Try future payment date - should show error
2. Try amount = 0 - should show error
3. Try ONLINE without transaction ID - should show error
4. Try CHEQUE without cheque number - should show error
5. Try CHEQUE without bank name - should show error
6. Try without selecting months - should show error

**6.5 Filter Testing**
1. Select payment method filter
2. Verify only matching receipts shown
3. Select start and end date
4. Verify only receipts in date range shown
5. Test both filters together

**Expected Results:**
- Receipt number auto-generated and unique
- Payment method validation works
- Conditional fields appear based on payment method
- Date range filter works correctly
- Amounts display in currency format

---

### 7. School Configuration Testing

**Steps:**

**7.1 Create Configuration**
1. Click "Configuration" in sidebar
2. Click "Add Configuration" button
3. Fill in form:
   - Key: SCHOOL_NAME
   - Data Type: STRING
   - Category: GENERAL
   - Value: "ABC School"
   - Editable: Checked
   - Description: "School name for reports"
4. Click "Create Configuration"
5. Verify success notification
6. Verify config appears in table

**7.2 Create Different Data Types**
1. Create INTEGER config:
   - Key: MAX_STUDENTS
   - Data Type: INTEGER
   - Value: 1000
2. Create BOOLEAN config:
   - Key: ENABLE_NOTIFICATIONS
   - Data Type: BOOLEAN
   - Value: true
3. Create JSON config:
   - Key: THEME_SETTINGS
   - Data Type: JSON
   - Value: {"color": "blue"}
4. Verify all created successfully

**7.3 Filter Testing**
1. Select category filter
2. Verify only configs from that category shown
3. Check "Show editable only"
4. Verify only editable configs shown

**7.4 Edit Configuration**
1. Edit a config
2. Change value
3. Verify key field is disabled (can't change)
4. Verify update works

**7.5 Delete Configuration**
1. Try deleting non-editable config - button should not appear
2. Delete editable config
3. Verify deletion works

**Expected Results:**
- All data types supported
- Key cannot be edited
- Only editable configs can be deleted
- Filters work correctly

---

## Cross-Feature Testing

### 1. Navigation Testing
1. Click through all sidebar menu items
2. Verify each page loads correctly
3. Use browser back/forward buttons
4. Verify navigation history works
5. Refresh page on each route
6. Verify page state maintained

### 2. Notification Testing
1. Trigger success action (create, update)
2. Verify green notification appears
3. Verify auto-dismiss after 5 seconds
4. Trigger error (validation failure)
5. Verify red notification appears
6. Click X to dismiss manually
7. Verify notification removed

### 3. Loading State Testing
1. Observe loading spinner on initial page load
2. Observe loading during form submission
3. Observe loading during deletion
4. Verify UI is disabled during loading

### 4. Modal Testing
1. Open any modal (Add/Edit)
2. Press ESC key - modal should close
3. Click outside modal - modal should close
4. Click X button - modal should close
5. Fill form partially, close modal
6. Reopen modal - form should be reset

### 5. Responsive Design Testing
1. Resize browser to mobile width (< 768px)
2. Verify sidebar adapts
3. Verify tables scroll horizontally
4. Verify forms stack vertically
5. Verify all buttons accessible

---

## Error Scenarios Testing

### 1. Backend Down
1. Stop backend server
2. Try any operation
3. Verify error notification: "No response from server"
4. Restart backend
5. Retry operation - should work

### 2. Network Error
1. Disable internet (or throttle to offline)
2. Try any operation
3. Verify appropriate error message
4. Re-enable internet
5. Retry - should work

### 3. Invalid Data from Backend
1. Check browser console for errors
2. Verify app doesn't crash
3. Verify error messages displayed

---

## Data Consistency Testing

### 1. Create Dependencies
1. Create a class (Class 5-A)
2. Create students for that class
3. Try deleting the class
4. Backend should prevent (FK constraint)
5. Verify error message shown

### 2. Update Cascades
1. Update class details
2. Verify students still linked correctly
3. Verify fee journals reference correct class

### 3. Filter Consistency
1. Filter students by class
2. Navigate to another page
3. Return to students
4. Verify filter cleared or maintained correctly

---

## Performance Testing

### 1. Search Performance
1. Create 50+ students
2. Type in search box
3. Verify debouncing works (no request until 500ms pause)
4. Verify results appear quickly

### 2. Large Dataset
1. Create 100+ records in any table
2. Verify table renders without lag
3. Verify filters work quickly

---

## Browser Compatibility

Test in different browsers:
1. Chrome (recommended)
2. Firefox
3. Edge
4. Safari (if available)

Verify:
- All features work
- Styling is consistent
- No console errors

---

## Common Issues and Solutions

### Issue: "No response from server"
**Solution:** Ensure backend is running on http://localhost:8080

### Issue: CORS errors
**Solution:** Vite proxy should handle this. Check vite.config.js

### Issue: Page blank after navigation
**Solution:** Check browser console for errors. Verify all imports correct.

### Issue: Form submission doesn't work
**Solution:** Check validation errors. Check network tab for API errors.

### Issue: Search not working
**Solution:** Type at least 3 characters. Wait for 500ms debounce.

---

## Reporting Bugs

When reporting bugs, include:
1. Steps to reproduce
2. Expected behavior
3. Actual behavior
4. Browser console errors (F12 > Console)
5. Network tab info (F12 > Network)
6. Screenshots if applicable

---

## Test Results Checklist

- [ ] Dashboard loads and displays stats
- [ ] Can create, edit, delete classes
- [ ] Can create, edit, delete students
- [ ] Student search works with debouncing
- [ ] Can create, edit, delete fee masters
- [ ] Can activate/deactivate fee masters
- [ ] Can create, edit, delete fee journals
- [ ] Can record payments on journals
- [ ] Can generate receipts for all payment methods
- [ ] Can filter receipts by date and method
- [ ] Can create, edit, delete configurations
- [ ] All validations work correctly
- [ ] All notifications appear and dismiss
- [ ] All modals open and close correctly
- [ ] Navigation works throughout app
- [ ] Responsive design works on mobile
- [ ] No console errors during normal use
- [ ] Backend errors handled gracefully

---

## Success Criteria

The frontend is considered fully functional if:
1. All items in test checklist pass
2. All CRUD operations work for all entities
3. All validations prevent invalid data
4. All filters and searches work correctly
5. No critical console errors
6. Responsive design works on different screen sizes
7. User experience is smooth and intuitive
8. Error handling is user-friendly
