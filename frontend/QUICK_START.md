# Quick Start Guide - School Management System Frontend

## Prerequisites Check

Before starting, ensure you have:
- [ ] Node.js 18+ installed (`node --version`)
- [ ] npm installed (`npm --version`)
- [ ] Backend API running on http://localhost:8081
- [ ] Backend health check passing: http://localhost:8081/actuator/health

## Setup (5 minutes)

### 1. Navigate to Frontend Directory
```bash
cd D:/wks-sms-autonomous/frontend
```

### 2. Install Dependencies (if not already done)
```bash
npm install
```

### 3. Verify Environment Configuration
Check that `.env.local` exists with:
```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8081/api/v1
```

### 4. Start Development Server
```bash
npm run dev
```

The application will start on http://localhost:3000

## First-Time Usage

### 1. Access the Application
Open your browser and navigate to:
```
http://localhost:3000
```

### 2. Test Backend Connectivity
The home page will load. If you see the homepage successfully, the frontend is working.

### 3. Register Your First Student

**Step-by-step:**
1. Click "Students" in the navigation
2. Click "Add Student" button
3. Fill in the required fields:
   - First Name: John
   - Last Name: Doe
   - Date of Birth: 2015-05-15 (must be 3-18 years old)
   - Mobile: +919876543210
4. Optional fields: Email, Address, Guardian names, etc.
5. Click "Create Student"
6. You'll be redirected to the student detail page

**If you get an error:**
- Check that the backend is running
- Verify the API URL in `.env.local`
- Check browser console for network errors
- Verify backend CORS allows http://localhost:3000

### 4. Search and Filter Students

1. Go to Students page
2. Use the search box to find by last name
3. Filter by status (Active/Inactive)
4. Click on a student to view details
5. Click Edit icon to update name/mobile/status
6. Click Delete icon to remove (with confirmation)

### 5. Manage Configuration Settings

1. Click "Settings" in the navigation
2. Click "Add Setting" button
3. Fill in:
   - Category: Academic
   - Setting Key: academic.year.current
   - Setting Value: 2025-2026
   - Description: Current academic year
4. Click "Create Setting"
5. Edit or delete settings as needed
6. Filter by category to organize settings

## Common Tasks

### Adding a Student
```
Home → Students → Add Student → Fill form → Create Student
```

### Editing a Student
```
Students → Click student → Edit → Update fields → Update Student
```

### Deleting a Student
```
Students → Click trash icon → Confirm deletion
```

### Adding Configuration
```
Home → Settings → Add Setting → Fill form → Create Setting
```

## Troubleshooting

### "Unable to connect to server"
**Solution:**
1. Check backend is running: http://localhost:8081/actuator/health
2. Verify `.env.local` has correct API URL
3. Check browser console for CORS errors
4. Restart frontend server

### "Validation errors" on form submission
**Common issues:**
- Age must be 3-18 years (check date of birth)
- Mobile number format: +919876543210 (10-15 digits)
- Email must be valid format
- Required fields marked with * must be filled

### Page not loading / Blank screen
**Solution:**
1. Check browser console for errors
2. Clear browser cache
3. Restart development server
4. Check Node.js version (18+)

### Build errors
**Solution:**
```bash
# Delete node_modules and reinstall
rm -rf node_modules
npm install
```

## Development Tips

### Hot Reload
The app supports hot reload. Any code changes will automatically refresh the page.

### API Response Format
All errors follow RFC 7807 format:
```json
{
  "type": "validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "Request validation failed",
  "errors": [...]
}
```

### Date Format
All dates use ISO 8601 format: `YYYY-MM-DD`

### Mobile Format
Accepts formats:
- +919876543210 (with country code)
- 9876543210 (without country code)
- Must be 10-15 digits total

## Testing the Full Workflow

### Complete Student Lifecycle:
1. **Create**: Register student John Doe (born 2015-05-15)
2. **Read**: View student details page
3. **Update**: Change status to Inactive
4. **Search**: Search by last name "Doe"
5. **Delete**: Remove student with confirmation

### Configuration Management:
1. **Create**: Add academic.year.current = "2025-2026"
2. **Read**: View all settings filtered by Academic
3. **Update**: Change value to "2026-2027"
4. **Delete**: Remove setting

## Next Steps

1. Explore all pages and features
2. Try validation edge cases
3. Test pagination with 20+ students
4. Test search with various filters
5. Check responsive design on mobile

## Support

**Documentation:**
- Full README: `D:/wks-sms-autonomous/frontend/README.md`
- API Spec: `D:/wks-sms-autonomous/specs/architecture/API_DESIGN.md`

**Common URLs:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8081/api/v1
- Swagger UI: http://localhost:8081/swagger-ui.html
- Backend Health: http://localhost:8081/actuator/health

## Production Checklist

Before deploying to production:
- [ ] Update `.env.local` with production API URL
- [ ] Run `npm run build` to check for build errors
- [ ] Test all features in production build
- [ ] Verify CORS configuration for production domain
- [ ] Check responsive design on various devices
- [ ] Test error handling scenarios
- [ ] Verify all validations work correctly

## Success Indicators

You know everything is working when:
- ✓ Homepage loads without errors
- ✓ Can create a new student successfully
- ✓ Student list shows pagination
- ✓ Search filters results correctly
- ✓ Can edit student name/mobile/status
- ✓ Delete confirmation works
- ✓ Configuration settings CRUD works
- ✓ Toast notifications appear on actions
- ✓ Form validations show helpful errors
- ✓ No console errors in browser DevTools

Happy coding! 🎓
