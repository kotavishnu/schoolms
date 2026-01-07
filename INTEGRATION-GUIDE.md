# School Management System - Integration Guide

## ✅ System Status

**Both servers are now running and integrated!**

- 🟢 **Backend**: http://localhost:8080/api
- 🟢 **Frontend**: http://localhost:3000

## 🚀 Quick Start

### Access the Application

Open your browser and navigate to:
```
http://localhost:3000
```

You'll see the home page with six main feature cards.

## 📋 Testing the Features

### 1. School Configuration (Start Here)
**URL**: http://localhost:3000/school-config

**What to do**:
- Fill in school details:
  - School Name (e.g., "ABC High School")
  - Principal Name
  - Contact details
  - Address
  - Fee Frequency (Monthly/Quarterly/Yearly)
- Click "Save Configuration"

**Backend Endpoint**: `POST /api/school-config`

---

### 2. Class Management
**URL**: http://localhost:3000/classes

**What to do**:
- View all classes (1-10) that are pre-configured
- Click "View Students" on any class card
- Initially, all classes will have 0 students

**Backend Endpoint**: `GET /api/classes`

---

### 3. Student Registration
**URL**: http://localhost:3000/students

**What to do**:
- Fill in student details:
  - First Name & Last Name (required)
  - Date of Birth (required)
  - Mobile Number (required, 10 digits)
  - Father's & Mother's Name
  - Caste, Religion
  - Moles on Body (identification marks)
  - Address
  - Select Class (required)
- Click "Register Student"
- Success message will appear on top-right

**Backend Endpoint**: `POST /api/students`

**Test Data Example**:
```
First Name: Rajesh
Last Name: Kumar
DOB: 2010-05-15
Mobile: 9876543210
Father's Name: Ramesh Kumar
Mother's Name: Sunita Devi
Class: Class 5
Address: 123 Main Street, Delhi
```

---

### 4. Fee Master Configuration
**URL**: http://localhost:3000/fee-master

**What to do**:
- Create fee structures for different classes:
  - Select Class (e.g., Class 5)
  - Enter Amount (e.g., 5000)
  - Select Fee Type (Tuition, Library, Computer, etc.)
  - Select Frequency (Monthly, Quarterly, Yearly)
  - Add optional description
- Click "Create Fee Master"
- View all configured fee masters in the table below
- Edit or Delete existing fee masters

**Backend Endpoints**:
- `GET /api/fee-masters`
- `POST /api/fee-masters`
- `PUT /api/fee-masters/{id}`
- `DELETE /api/fee-masters/{id}`

---

### 5. Fee Receipt
**URL**: http://localhost:3000/fee-receipt

**What to do**:
1. Type student name in search box (minimum 3 characters)
2. Select student from dropdown suggestions
3. System automatically calculates fees
4. Review fee breakdown
5. Enter payment amount
6. Click "Record Payment & Generate Receipt"
7. Receipt PDF will download automatically

**Backend Endpoints**:
- `GET /api/students/search?q={query}`
- `POST /api/fee-receipts`

---

### 6. Parent Portal
**URL**: http://localhost:3000/parent-portal

**What to do**:
1. Login with:
   - Student ID (get from registration)
   - Registered Mobile Number
2. View student information
3. Check pending fees
4. View payment history
5. Online payment (placeholder button)

**Backend Endpoints**:
- `GET /api/students/{id}`
- `GET /api/fee-receipts/student/{studentId}`

---

## 🧪 Complete Test Workflow

Follow this order for a complete test:

1. **Configure School** → Go to School Config, set up school details
2. **Check Classes** → Verify Classes 1-10 are available
3. **Register Students** → Add 2-3 students in different classes
4. **Set Fee Structure** → Create fee masters for the classes where you added students
5. **Generate Receipt** → Search for a student and generate a fee receipt
6. **Test Parent Portal** → Login as a parent and view student fee details

## 🔍 API Testing (Optional)

You can also test the backend directly using curl or Postman:

### Get All Classes
```bash
curl http://localhost:8080/api/classes
```

### Get All Students
```bash
curl http://localhost:8080/api/students
```

### Create Student
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Rajesh",
    "lastName": "Kumar",
    "dateOfBirth": "2010-05-15",
    "mobileNumber": "9876543210",
    "fatherName": "Ramesh Kumar",
    "motherName": "Sunita Devi",
    "address": "123 Main Street, Delhi",
    "classId": 5
  }'
```

### Get Fee Masters
```bash
curl http://localhost:8080/api/fee-masters
```

## 🛠️ Development Tools

### Hot Module Replacement (HMR)
The frontend uses Vite with HMR enabled. Any changes to frontend code will automatically reload in the browser.

### Dev Server Logs
- Frontend logs: Check the terminal where `npm run dev` is running
- Backend logs: Check `backend/server.log` or backend terminal

### Stop Servers

**Frontend**:
```bash
# Press Ctrl+C in the terminal running npm run dev
# Or find and kill the process
netstat -ano | findstr :3000
taskkill /PID <process_id> /F
```

**Backend**:
```bash
# Press Ctrl+C in the terminal running mvn spring-boot:run
# Or find and kill the process
netstat -ano | findstr :8080
taskkill /PID <process_id> /F
```

## 📊 API Endpoint Summary

| Feature | Method | Endpoint | Frontend Service |
|---------|--------|----------|------------------|
| List Classes | GET | `/api/classes` | `classService.getAllClasses()` |
| Create Student | POST | `/api/students` | `studentService.createStudent()` |
| Search Students | GET | `/api/students/search?q=` | `studentService.searchStudents()` |
| Get Student | GET | `/api/students/{id}` | `studentService.getStudent()` |
| List Fee Masters | GET | `/api/fee-masters` | `feeService.getFeeMaster()` |
| Create Fee Master | POST | `/api/fee-masters` | `feeService.createFeeMaster()` |
| Update Fee Master | PUT | `/api/fee-masters/{id}` | `feeService.updateFeeMaster()` |
| Delete Fee Master | DELETE | `/api/fee-masters/{id}` | `feeService.deleteFeeMaster()` |
| Generate Receipt | POST | `/api/fee-receipts` | `feeService.recordPayment()` |
| Get Receipt | GET | `/api/fee-receipts/{id}` | `feeService.generateReceipt()` |
| Get School Config | GET | `/api/school-config` | `schoolService.getSchoolConfig()` |
| Save School Config | POST | `/api/school-config` | `schoolService.saveSchoolConfig()` |

## ✨ Features Verified

✅ Frontend-Backend Proxy Configuration
✅ API Endpoint Integration
✅ Axios Interceptors (Auth & Error Handling)
✅ React Router Navigation
✅ Tailwind CSS Styling
✅ React Hot Toast Notifications
✅ Form Validation
✅ Responsive Design

## 🐛 Troubleshooting

### Frontend not loading?
1. Check if port 3000 is available: `netstat -ano | findstr :3000`
2. Check for errors in terminal
3. Clear browser cache and reload

### API calls failing?
1. Verify backend is running: `curl http://localhost:8080/api/classes`
2. Check browser Developer Tools → Network tab
3. Verify proxy configuration in `vite.config.js`

### CORS errors?
- The backend should have CORS enabled for `http://localhost:3000`
- Check backend CORS configuration

## 📝 Next Steps

1. **Add Authentication**: Implement login/logout functionality
2. **Add Tests**: Write unit tests for components and services
3. **Add Validation**: Enhance form validation rules
4. **Improve UX**: Add loading states, better error messages
5. **Add Reports**: Generate fee collection reports
6. **Deploy**: Prepare for production deployment

---

**Happy Testing!** 🎉

For issues or questions, check the backend logs at `backend/server.log` or frontend dev server output.
