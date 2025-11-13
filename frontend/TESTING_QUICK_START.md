# Testing Quick Start Guide

## Get Started in 3 Steps

### Step 1: Install Dependencies
```bash
cd frontend
npm install
```

### Step 2: Start Development Server
```bash
npm run dev
```

The app will open at **http://localhost:3000**

### Step 3: Start Testing!

## Test Routes

### Without Backend (UI Testing Only)

You can test the UI/UX of all pages without a backend:

1. **Students:**
   - http://localhost:3000/students
   - http://localhost:3000/students/new

2. **Classes:**
   - http://localhost:3000/classes
   - http://localhost:3000/classes/new

3. **Fees:**
   - http://localhost:3000/fees/structures
   - http://localhost:3000/fees/structures/new
   - http://localhost:3000/fees/dashboard

4. **Payments:**
   - http://localhost:3000/payments/record
   - http://localhost:3000/payments/history
   - http://localhost:3000/payments/dashboard

## What to Test

### ✅ Visual & UX
- Forms render correctly
- Buttons work
- Navigation works
- Responsive on mobile/tablet/desktop
- Loading states appear
- Error messages display

### ✅ Validation
- Form validation on Student Registration
- Form validation on Class Creation
- Form validation on Fee Structure Creation
- Form validation on Payment Recording

### ✅ Navigation
- Sidebar menu works
- Back buttons work
- Page transitions smooth
- Breadcrumbs update

### ⚠️ Requires Backend
- Login/Authentication
- Data fetching (lists will be empty)
- Create/Update/Delete operations
- Search and filter (needs data)
- Dashboard statistics (needs data)

## With Mock Backend (Optional)

To test with data, you can:

### Option 1: Use Mock Data
Temporarily modify the API client to return mock data.

### Option 2: Connect to Backend
Set environment variable:
```env
VITE_API_BASE_URL=http://localhost:8080
```

Then start your backend server.

## Quick Test Scenarios

### Scenario 1: Student Registration
1. Navigate to /students/new
2. Fill out the form
3. Upload a photo
4. Click Submit
5. Check validation errors
6. Verify form reset

### Scenario 2: Class Creation
1. Navigate to /classes/new
2. Select class name, section
3. Set capacity to 40
4. Enter room number
5. Click Create
6. Check validation

### Scenario 3: Payment Recording
1. Navigate to /payments/record
2. Search for student (mock)
3. Select fees to pay
4. Choose payment method
5. Enter amount
6. Review summary

### Scenario 4: Responsive Design
1. Open any page
2. Resize browser window
3. Check mobile view (< 768px)
4. Check tablet view (768px - 1024px)
5. Check desktop view (> 1024px)
6. Verify layout adapts

## Known Issues to Ignore

1. **Empty Lists** - Normal without backend
2. **Login Required** - Expected without backend
3. **API Errors** - Expected without backend
4. **No Data in Charts** - Expected without backend

## Report Bugs

Found a UI bug? Report it with:
- Page/Route
- Browser
- Steps to reproduce
- Screenshot

## Need Help?

Check:
1. Console (F12) for errors
2. Network tab for failed requests
3. FRONTEND_READY_FOR_TESTING.md for detailed info

## That's It!

Start testing and enjoy exploring the School Management System frontend! 🎉
