# Frontend Implementation Guide - School Management System

**Version:** 1.0
**Date:** January 22, 2026
**Status:** Active - MANDATORY FOR FRONTEND DEVELOPER AGENT
**Phase:** 1 - Student Management & Configuration

---

## Table of Contents

1. [Strict Constraints](#1-strict-constraints)
2. [Architecture Enforcement](#2-architecture-enforcement)
3. [Data Model Alignment](#3-data-model-alignment)
4. [Service Layer Implementation](#4-service-layer-implementation)
5. [Component Patterns](#5-component-patterns)
6. [Form Validation](#6-form-validation)
7. [Performance Optimization](#7-performance-optimization)
8. [Screenshot Validation](#8-screenshot-validation)

---

## 1. Strict Constraints

### 1.1 Critical Rules (NON-NEGOTIABLE)

**RULE 1: NO Custom Styles**
- FORBIDDEN: Creating new CSS, SASS, or styled-components
- MANDATORY: Use ONLY Tailwind CSS utility classes
- MANDATORY: Copy styles EXACTLY from `FRONTEND_DESIGN_SPECIFICATION.md`

**RULE 2: Reference Code Reuse**
- MANDATORY: Reuse UI components from `frontend/reference-code/` (when available)
- If reference code exists for a component, DO NOT recreate from scratch
- Extend reference code for new fields, DO NOT rewrite

**RULE 3: Data Model Alignment**
- ALL field names MUST match `FRONTEND_DESIGN_SPECIFICATION.md` Section "Data Models"
- Frontend `phone` → Backend `mobile` (mapping handled in service layer)
- Frontend `id` → Backend `student_id` (business key, NOT database ID)

**RULE 4: API Contract Strictness**
- Request/Response types MUST match `sms_api_specification.yaml`
- HTTP methods, endpoints, status codes are EXACT
- Error responses follow RFC 7807 format

**RULE 5: Screenshot Fidelity**
- Final UI MUST match screenshots in `screenshots/` directory 1:1
- Colors, spacing, font sizes, layout MUST be pixel-perfect
- Use DevTools to verify CSS values match screenshots

### 1.2 Technology Stack (EXACT Versions)

| Technology | Version | Purpose |
|------------|---------|---------|
| React | 18.3.x | UI framework |
| TypeScript | 5.x | Type safety |
| Vite | 5.x | Build tool |
| Tailwind CSS | 4.x | Styling (utility-first) |
| Shadcn/ui | Latest | Component library |
| React Router | 6.x | Routing |
| React Hook Form | 7.x | Form management |
| Zod | 3.x | Schema validation |
| Axios | 1.x | HTTP client |
| Lucide Icons | Latest | Icon library |

---

## 2. Architecture Enforcement

### 2.1 Directory Structure (MANDATORY)

```
src/
├── components/
│   ├── layout/
│   │   ├── Header.tsx
│   │   ├── Footer.tsx
│   │   └── Layout.tsx
│   ├── students/
│   │   ├── StudentCard.tsx
│   │   ├── StudentDialog.tsx
│   │   ├── ViewStudentDialog.tsx
│   │   └── StudentList.tsx
│   ├── configurations/
│   │   ├── ConfigurationDialog.tsx
│   │   ├── ConfigurationTable.tsx
│   │   └── CategoryFilter.tsx
│   ├── common/
│   │   ├── LoadingSpinner.tsx
│   │   ├── ErrorAlert.tsx
│   │   └── ConfirmDialog.tsx
│   └── ui/  # Shadcn/ui components
│       ├── button.tsx
│       ├── dialog.tsx
│       ├── input.tsx
│       ├── select.tsx
│       └── toast.tsx
├── pages/
│   ├── HomePage.tsx
│   ├── StudentsPage.tsx
│   └── ConfigurationsPage.tsx
├── services/
│   ├── api.ts                 # Axios instance + interceptors
│   ├── studentService.ts      # Student API calls
│   └── configurationService.ts  # Configuration API calls
├── contexts/
│   └── AppContext.tsx         # Global state (user, theme, notifications)
├── hooks/
│   ├── useStudents.ts
│   ├── useConfigurations.ts
│   └── useToast.ts
├── types/
│   ├── student.ts
│   ├── configuration.ts
│   └── api.ts
├── utils/
│   ├── validation.ts
│   ├── formatting.ts
│   └── constants.ts
├── styles/
│   └── globals.css
├── App.tsx
├── main.tsx
└── vite-env.d.ts
```

### 2.2 Service Layer Pattern (STRICT)

**Pattern:** Centralized API layer with type-safe services

**api.ts (Axios Instance):**
```typescript
import axios, { AxiosError, AxiosRequestConfig, AxiosResponse } from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Add correlation ID
apiClient.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const correlationId = crypto.randomUUID();
    config.headers = config.headers || {};
    config.headers['X-Correlation-ID'] = correlationId;
    console.log(`[${correlationId}] ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Error handling
apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error: AxiosError<ApiErrorResponse>) => {
    if (error.response) {
      console.error(`API Error: ${error.response.status}`, error.response.data);
    } else if (error.request) {
      console.error('Network Error:', error.message);
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

**studentService.ts:**
```typescript
import apiClient from './api';
import { Student, StudentCreateRequest, StudentUpdateRequest, StudentListResponse } from '../types/student';
import { ApiResponse } from '../types/api';

export const studentService = {
  /**
   * Get all students with optional filters
   */
  async getAll(filters?: { search?: string; status?: string }): Promise<StudentListResponse> {
    const response = await apiClient.get<StudentListResponse>('/api/v1/students', {
      params: filters,
    });
    return response.data;
  },

  /**
   * Get student by ID
   */
  async getById(id: string): Promise<Student> {
    const response = await apiClient.get<ApiResponse<Student>>(`/api/v1/students/${id}`);
    return response.data.data!;
  },

  /**
   * Create new student
   */
  async create(data: StudentCreateRequest): Promise<Student> {
    const response = await apiClient.post<ApiResponse<Student>>('/api/v1/students', data);
    return response.data.data!;
  },

  /**
   * Update student (only allowed fields: firstName, lastName, phone, status)
   */
  async update(id: string, data: StudentUpdateRequest): Promise<Student> {
    const response = await apiClient.patch<ApiResponse<Student>>(`/api/v1/students/${id}`, data);
    return response.data.data!;
  },

  /**
   * Delete student
   */
  async delete(id: string): Promise<void> {
    await apiClient.delete(`/api/v1/students/${id}`);
  },

  /**
   * Validate phone uniqueness
   */
  async validatePhone(phone: string, excludeId?: string): Promise<boolean> {
    const response = await apiClient.post<{ isUnique: boolean }>('/api/v1/students/validate-phone', {
      phone,
      excludeId,
    });
    return response.data.isUnique;
  },
};
```

---

## 3. Data Model Alignment

### 3.1 Student Type (EXACT from spec)

**types/student.ts:**
```typescript
export interface Student {
  // Identity
  id: string;  // Backend studentId (business key)

  // Personal (All required except identificationMarks)
  firstName: string;
  lastName: string;
  dateOfBirth: string;  // ISO 8601 format
  age: number;  // Calculated from dateOfBirth
  adhaarNumber: string;  // 12 digits, unique
  identificationMarks?: string;
  address: string;

  // Guardian (All required)
  guardianName: string;
  motherName: string;

  // Contact (All required, unique)
  phone: string;  // Maps to backend "mobile"
  email: string;

  // Status
  status: 'ACTIVE' | 'INACTIVE';

  // Metadata (Backend-managed)
  createdAt: string;
  updatedAt: string;
}

export interface StudentCreateRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  adhaarNumber: string;
  identificationMarks?: string;
  address: string;
  guardianName: string;
  motherName: string;
  phone: string;
  email: string;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface StudentUpdateRequest {
  firstName: string;
  lastName: string;
  phone: string;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface StudentListResponse {
  students: Student[];
  totalCount: number;
  activeCount: number;
}
```

### 3.2 Backend Field Mapping

**CRITICAL: Service layer MUST map frontend ↔ backend field names**

| Frontend Field | Backend Field | Service Layer Mapping |
|----------------|---------------|-----------------------|
| `phone` | `mobile` | `{ mobile: data.phone }` |
| `id` | `studentId` | `{ id: response.studentId }` |
| `adhaarNumber` | `aadhaarNumber` | `{ aadhaarNumber: data.adhaarNumber }` |

**Example Mapping in Service:**
```typescript
async create(data: StudentCreateRequest): Promise<Student> {
  // Frontend → Backend mapping
  const backendRequest = {
    firstName: data.firstName,
    lastName: data.lastName,
    dateOfBirth: data.dateOfBirth,
    mobile: data.phone,  // CRITICAL: phone → mobile
    email: data.email,
    aadhaarNumber: data.adhaarNumber,  // Match backend spelling
    // ... other fields
  };

  const response = await apiClient.post('/api/v1/students', backendRequest);

  // Backend → Frontend mapping
  return {
    id: response.data.studentId,  // CRITICAL: studentId → id
    phone: response.data.mobile,  // CRITICAL: mobile → phone
    // ... other fields
  };
}
```

---

## 4. Service Layer Implementation

### 4.1 Custom Hook Pattern (State + Logic)

**hooks/useStudents.ts:**
```typescript
import { useState, useEffect, useCallback } from 'react';
import { studentService } from '../services/studentService';
import { Student, StudentCreateRequest, StudentUpdateRequest } from '../types/student';
import { useToast } from './useToast';

export const useStudents = () => {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { toast } = useToast();

  // Load all students
  const loadStudents = useCallback(async (filters?: { search?: string; status?: string }) => {
    setLoading(true);
    setError(null);
    try {
      const response = await studentService.getAll(filters);
      setStudents(response.students);
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to load students';
      setError(message);
      toast.error(message);
    } finally {
      setLoading(false);
    }
  }, [toast]);

  // Create student
  const createStudent = useCallback(async (data: StudentCreateRequest): Promise<Student | null> => {
    setLoading(true);
    try {
      const newStudent = await studentService.create(data);
      setStudents((prev) => [...prev, newStudent]);
      toast.success('Student registered successfully');
      return newStudent;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to create student';
      toast.error(message);
      return null;
    } finally {
      setLoading(false);
    }
  }, [toast]);

  // Update student
  const updateStudent = useCallback(async (id: string, data: StudentUpdateRequest): Promise<boolean> => {
    setLoading(true);
    try {
      const updated = await studentService.update(id, data);
      setStudents((prev) => prev.map((s) => (s.id === id ? updated : s)));
      toast.success('Student updated successfully');
      return true;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to update student';
      toast.error(message);
      return false;
    } finally {
      setLoading(false);
    }
  }, [toast]);

  // Delete student
  const deleteStudent = useCallback(async (id: string): Promise<boolean> => {
    setLoading(true);
    try {
      await studentService.delete(id);
      setStudents((prev) => prev.filter((s) => s.id !== id));
      toast.success('Student deleted successfully');
      return true;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to delete student';
      toast.error(message);
      return false;
    } finally {
      setLoading(false);
    }
  }, [toast]);

  return {
    students,
    loading,
    error,
    loadStudents,
    createStudent,
    updateStudent,
    deleteStudent,
  };
};
```

---

## 5. Component Patterns

### 5.1 Container/Presentational Pattern

**StudentsPage.tsx (Container):**
```typescript
import React, { useEffect, useState } from 'react';
import { useStudents } from '../hooks/useStudents';
import StudentList from '../components/students/StudentList';
import StudentDialog from '../components/students/StudentDialog';
import { Button } from '../components/ui/button';

const StudentsPage: React.FC = () => {
  const { students, loading, loadStudents, createStudent, updateStudent, deleteStudent } = useStudents();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [filters, setFilters] = useState({ search: '', status: 'ALL' });

  useEffect(() => {
    loadStudents(filters.status !== 'ALL' ? { status: filters.status } : {});
  }, [filters, loadStudents]);

  const handleCreateStudent = async (data: StudentCreateRequest) => {
    const result = await createStudent(data);
    if (result) {
      setDialogOpen(false);
    }
  };

  return (
    <div className="container mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Students</h1>
        <Button onClick={() => setDialogOpen(true)}>Register New Student</Button>
      </div>

      <StudentList
        students={students}
        loading={loading}
        onEdit={updateStudent}
        onDelete={deleteStudent}
      />

      <StudentDialog
        isOpen={dialogOpen}
        onClose={() => setDialogOpen(false)}
        onSubmit={handleCreateStudent}
        mode="create"
      />
    </div>
  );
};

export default StudentsPage;
```

**StudentList.tsx (Presentational):**
```typescript
import React from 'react';
import { Student } from '../../types/student';
import StudentCard from './StudentCard';

interface StudentListProps {
  students: Student[];
  loading: boolean;
  onEdit: (id: string, data: StudentUpdateRequest) => Promise<boolean>;
  onDelete: (id: string) => Promise<boolean>;
}

const StudentList: React.FC<StudentListProps> = ({ students, loading, onEdit, onDelete }) => {
  if (loading) {
    return <div className="text-center py-8">Loading students...</div>;
  }

  if (students.length === 0) {
    return <div className="text-center py-8 text-gray-500">No students found</div>;
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      {students.map((student) => (
        <StudentCard key={student.id} student={student} onEdit={onEdit} onDelete={onDelete} />
      ))}
    </div>
  );
};

export default StudentList;
```

---

## 6. Form Validation

### 6.1 Zod Schema (EXACT from spec)

**utils/validation.ts:**
```typescript
import { z } from 'zod';

// Helper: Calculate age from date of birth
export const calculateAge = (dob: string): number => {
  const birthDate = new Date(dob);
  const today = new Date();
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }

  return age;
};

// Student Create Schema
export const studentCreateSchema = z.object({
  firstName: z
    .string()
    .min(1, 'First name is required')
    .max(50, 'First name must be at most 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'First name must contain only letters and spaces'),

  lastName: z
    .string()
    .min(1, 'Last name is required')
    .max(50, 'Last name must be at most 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Last name must contain only letters and spaces'),

  dateOfBirth: z
    .string()
    .refine((date) => {
      const age = calculateAge(date);
      return age >= 3 && age <= 18;
    }, 'Age must be between 3 and 18 years'),

  adhaarNumber: z
    .string()
    .regex(/^\d{12}$/, 'Aadhaar number must be exactly 12 digits'),

  identificationMarks: z
    .string()
    .max(200, 'Identification marks must be at most 200 characters')
    .optional(),

  address: z
    .string()
    .min(10, 'Address must be at least 10 characters')
    .max(500, 'Address must be at most 500 characters'),

  guardianName: z
    .string()
    .min(1, 'Guardian name is required')
    .max(100, 'Guardian name must be at most 100 characters'),

  motherName: z
    .string()
    .min(1, 'Mother name is required')
    .max(100, 'Mother name must be at most 100 characters'),

  phone: z
    .string()
    .regex(/^\d{10}$/, 'Phone must be exactly 10 digits'),

  email: z
    .string()
    .email('Email must be in valid format'),

  status: z.enum(['ACTIVE', 'INACTIVE']).default('ACTIVE'),
});

// Student Update Schema (only editable fields)
export const studentUpdateSchema = z.object({
  firstName: z.string().min(1).max(50).regex(/^[a-zA-Z\s]+$/),
  lastName: z.string().min(1).max(50).regex(/^[a-zA-Z\s]+$/),
  phone: z.string().regex(/^\d{10}$/),
  status: z.enum(['ACTIVE', 'INACTIVE']),
});

export type StudentCreateFormData = z.infer<typeof studentCreateSchema>;
export type StudentUpdateFormData = z.infer<typeof studentUpdateSchema>;
```

### 6.2 React Hook Form Integration

**components/students/StudentDialog.tsx:**
```typescript
import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { studentCreateSchema, StudentCreateFormData } from '../../utils/validation';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '../ui/dialog';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';

interface StudentDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: StudentCreateFormData) => Promise<void>;
  mode: 'create' | 'edit';
  student?: Student;
}

const StudentDialog: React.FC<StudentDialogProps> = ({ isOpen, onClose, onSubmit, mode, student }) => {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<StudentCreateFormData>({
    resolver: zodResolver(studentCreateSchema),
    defaultValues: student || {},
  });

  const handleFormSubmit = async (data: StudentCreateFormData) => {
    await onSubmit(data);
    reset();
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>{mode === 'create' ? 'Register New Student' : 'Edit Student'}</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
          {/* Personal Information */}
          <div className="space-y-4">
            <h3 className="text-lg font-semibold">Personal Information</h3>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="firstName">First Name *</Label>
                <Input
                  id="firstName"
                  {...register('firstName')}
                  className={errors.firstName ? 'border-red-500' : ''}
                />
                {errors.firstName && (
                  <p className="text-red-500 text-sm mt-1">{errors.firstName.message}</p>
                )}
              </div>

              <div>
                <Label htmlFor="lastName">Last Name *</Label>
                <Input
                  id="lastName"
                  {...register('lastName')}
                  className={errors.lastName ? 'border-red-500' : ''}
                />
                {errors.lastName && (
                  <p className="text-red-500 text-sm mt-1">{errors.lastName.message}</p>
                )}
              </div>
            </div>

            <div>
              <Label htmlFor="dateOfBirth">Date of Birth *</Label>
              <Input
                id="dateOfBirth"
                type="date"
                {...register('dateOfBirth')}
                className={errors.dateOfBirth ? 'border-red-500' : ''}
                disabled={mode === 'edit'}  // Immutable field
              />
              {errors.dateOfBirth && (
                <p className="text-red-500 text-sm mt-1">{errors.dateOfBirth.message}</p>
              )}
            </div>

            {/* ... other fields following same pattern ... */}
          </div>

          {/* Form Actions */}
          <div className="flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={onClose}>
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Submitting...' : mode === 'create' ? 'Register' : 'Update'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default StudentDialog;
```

---

## 7. Performance Optimization

### 7.1 Code Splitting

**App.tsx:**
```typescript
import { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/layout/Layout';

// Lazy load pages
const HomePage = lazy(() => import('./pages/HomePage'));
const StudentsPage = lazy(() => import('./pages/StudentsPage'));
const ConfigurationsPage = lazy(() => import('./pages/ConfigurationsPage'));

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Suspense fallback={<div>Loading...</div>}>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/students" element={<StudentsPage />} />
            <Route path="/configurations" element={<ConfigurationsPage />} />
          </Routes>
        </Suspense>
      </Layout>
    </BrowserRouter>
  );
}

export default App;
```

### 7.2 Debounced Search

**hooks/useDebounce.ts:**
```typescript
import { useEffect, useState } from 'react';

export const useDebounce = <T>(value: T, delay: number = 300): T => {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => clearTimeout(timer);
  }, [value, delay]);

  return debouncedValue;
};
```

**Usage:**
```typescript
const [searchTerm, setSearchTerm] = useState('');
const debouncedSearch = useDebounce(searchTerm, 300);

useEffect(() => {
  loadStudents({ search: debouncedSearch });
}, [debouncedSearch, loadStudents]);
```

### 7.3 Memoization

```typescript
import React, { memo, useMemo } from 'react';

const StudentCard = memo(({ student, onEdit, onDelete }: StudentCardProps) => {
  const statusColor = useMemo(() => {
    return student.status === 'ACTIVE'
      ? 'bg-green-100 text-green-800'
      : 'bg-gray-100 text-gray-800';
  }, [student.status]);

  return (
    <div className="border rounded-lg p-4">
      <span className={`px-2 py-1 rounded text-sm ${statusColor}`}>
        {student.status}
      </span>
      {/* ... */}
    </div>
  );
});

export default StudentCard;
```

---

## 8. Screenshot Validation

### 8.1 Validation Process (MANDATORY)

**Before code approval, MUST verify:**

1. **Start dev server:** `npm run dev`
2. **Navigate to page/open dialog**
3. **Side-by-side comparison** with screenshot
4. **Check using DevTools:**
   - Text content exact match
   - Color values (use color picker): `bg-blue-600` = `#2563eb`
   - Spacing (margin/padding): `p-4` = `16px`
   - Font sizes: `text-3xl` = `30px`
   - Icon sizes: `w-6 h-6` = `24px`
5. **Document deviations** in `SCREENSHOT_VALIDATION.md`
6. **Fix mismatches** before PR approval

### 8.2 Screenshot Checklist

**HomePage (`screenshots/homepage.png`):**
- [ ] Welcome banner text matches
- [ ] 3 stat cards: Total Students, Active, System Status
- [ ] Card layout: 1 col mobile, 3 col desktop
- [ ] Quick action cards: exact text + icons
- [ ] Color scheme: blues (#2563eb), grays (#6b7280)

**StudentsPage (`screenshots/students-page.png`):**
- [ ] Search input placeholder: "Search by ID, name, or guardian..."
- [ ] Status filter dropdown: All / Active / Inactive
- [ ] "Register New Student" button (top-right, blue primary)
- [ ] Student cards: ID, Name, Guardian, Phone, Email, Status badge
- [ ] Status badge colors: green (ACTIVE), gray (INACTIVE)
- [ ] Grid: 1 col (mobile), 2 (tablet), 3 (desktop)

**Student Dialogs:**
- [ ] `screenshots/student-dialog-create.png`: All fields present
- [ ] `screenshots/student-dialog-edit.png`: Disabled fields grayed out
- [ ] `screenshots/student-dialog-view.png`: No form inputs, read-only

**ConfigurationsPage (`screenshots/configurations-page.png`):**
- [ ] Category filter tabs: GENERAL (blue), ACADEMIC (purple), FINANCE (green), SYSTEM (gray)
- [ ] Table columns: Category, Key, Value, Description, Last Updated, Actions
- [ ] "Add New Configuration" button

### 8.3 Tailwind Color Mapping

**EXACT colors from screenshots:**

```typescript
// Primary colors
const colors = {
  primary: '#2563eb',      // bg-blue-600
  primaryHover: '#1d4ed8', // hover:bg-blue-700

  // Status badges
  activeGreen: '#dcfce7',  // bg-green-100
  activeText: '#166534',   // text-green-800
  inactiveGray: '#f3f4f6', // bg-gray-100
  inactiveText: '#374151', // text-gray-800

  // Category badges
  generalBlue: '#dbeafe',  // bg-blue-100
  academicPurple: '#f3e8ff', // bg-purple-100
  financeGreen: '#dcfce7',   // bg-green-100
  systemGray: '#f3f4f6',     // bg-gray-100
};
```

---

## Appendix

### A. Environment Variables

**.env.development:**
```bash
VITE_API_BASE_URL=http://localhost:8080
VITE_STUDENT_API_URL=http://localhost:8081
VITE_CONFIG_API_URL=http://localhost:8082
```

**.env.production:**
```bash
VITE_API_BASE_URL=https://api.school.example.com
```

### B. Build Commands

```bash
# Install dependencies
npm install

# Development server
npm run dev

# Type checking
npm run type-check

# Linting
npm run lint

# Production build
npm run build

# Preview production build
npm run preview
```

### C. Cross-References

- **System Architecture:** See `01-system-architecture.md`
- **Data Models:** See `FRONTEND_DESIGN_SPECIFICATION.md` Section 2
- **API Endpoints:** See `FRONTEND_DESIGN_SPECIFICATION.md` Section 3
- **Screenshots:** See `screenshots/` directory

---

**Document Control:**
**Created:** January 22, 2026
**Last Updated:** January 22, 2026
**Approved By:** Software Architect Agent
**Mandatory For:** Frontend Developer Agent
