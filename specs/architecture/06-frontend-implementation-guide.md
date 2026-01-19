# Frontend Implementation Guidelines
**School Management System - Frontend Developer Agent Instructions**

**Version**: 1.0
**Date**: 2026-01-15
**Status**: Active
**Target Agent**: Frontend Developer Agent

---

## Table of Contents

1. [Overview](#overview)
2. [Critical Constraints](#critical-constraints)
3. [Project Setup](#project-setup)
4. [Architecture Patterns](#architecture-patterns)
5. [Component Implementation](#component-implementation)
6. [API Integration](#api-integration)
7. [Form Management](#form-management)
8. [State Management](#state-management)
9. [Styling Guidelines](#styling-guidelines)
10. [Testing Requirements](#testing-requirements)

---

## Overview

### Implementation Mandate

This guide is **MANDATORY** for the Frontend Developer Agent. All code generated must strictly adhere to the Frontend Design Specification (`FRONTEND_DESIGN_SPECIFICATION.md`) and these implementation patterns.

### Technology Stack (Non-Negotiable)

- **React**: 18+ (Function components with Hooks)
- **TypeScript**: 5.x (Strict mode enabled)
- **Build Tool**: Vite 5.x
- **UI Framework**: Tailwind CSS v4 + Shadcn/ui
- **Routing**: React Router v6
- **Forms**: React Hook Form v7 + Zod
- **HTTP Client**: Axios v1
- **Icons**: Lucide React
- **Notifications**: Sonner

### Reference Code Location

**PRIMARY SOURCE**: `frontend/reference-code/`

**CRITICAL**: You MUST reuse UI components, layouts, classes, and themes from the Reference Code. Do NOT create custom styles.

---

## Critical Constraints

### NO NEW STYLES RULE

**YOU ARE STRICTLY FORBIDDEN** from creating custom CSS, SASS, or styled-components.

**Implementation Rules**:
1. Copy/paste UI components from Reference Code EXACTLY
2. Use existing Tailwind classes from Reference Code
3. Use Shadcn/ui components from Reference Code
4. NO custom CSS files (except copying from Reference Code)
5. NO inline styles unless present in Reference Code

**Example - CORRECT**:
```typescript
// Copy from Reference Code
<div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
  <Card className="p-6 shadow-sm border border-gray-200">
    {/* Content */}
  </Card>
</div>
```

**Example - FORBIDDEN**:
```typescript
// ❌ NEVER DO THIS
const customStyles = {
  card: {
    padding: '24px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
  }
};

<div style={customStyles.card}>  // ❌ FORBIDDEN
```

---

## Project Setup

### Directory Structure

```
frontend/
├── src/
│   ├── components/
│   │   ├── layout/
│   │   │   ├── Header.tsx
│   │   │   ├── Footer.tsx
│   │   │   └── Layout.tsx
│   │   ├── students/
│   │   │   ├── StudentCard.tsx
│   │   │   ├── StudentDialog.tsx
│   │   │   ├── StudentForm.tsx
│   │   │   └── ViewStudentDialog.tsx
│   │   ├── configurations/
│   │   │   ├── ConfigurationTable.tsx
│   │   │   └── ConfigurationDialog.tsx
│   │   ├── common/
│   │   │   ├── ErrorBoundary.tsx
│   │   │   ├── LoadingSpinner.tsx
│   │   │   └── SearchBar.tsx
│   │   └── ui/            # Shadcn/ui components (from Reference Code)
│   │       ├── button.tsx
│   │       ├── card.tsx
│   │       ├── dialog.tsx
│   │       ├── input.tsx
│   │       └── ...
│   ├── pages/
│   │   ├── HomePage.tsx
│   │   ├── StudentsPage.tsx
│   │   └── ConfigurationsPage.tsx
│   ├── services/
│   │   ├── api.ts               # HTTP client setup
│   │   ├── studentService.ts    # Student API calls
│   │   └── configurationService.ts
│   ├── contexts/
│   │   └── AppContext.tsx       # Global state
│   ├── hooks/
│   │   ├── useStudents.ts
│   │   ├── useConfigurations.ts
│   │   └── useDebounce.ts
│   ├── types/
│   │   ├── student.ts
│   │   ├── configuration.ts
│   │   └── api.ts
│   ├── utils/
│   │   ├── validation.ts
│   │   ├── formatting.ts
│   │   └── constants.ts
│   ├── styles/
│   │   ├── index.css            # Global styles (from Reference Code)
│   │   └── tailwind.css         # Tailwind directives
│   ├── App.tsx
│   └── main.tsx
├── public/
├── .env.development
├── .env.production
├── vite.config.ts
├── tailwind.config.ts           # Tailwind config (from Reference Code)
├── tsconfig.json
├── package.json
└── Dockerfile
```

### Initial Setup Commands

```bash
# Create Vite project with React + TypeScript
npm create vite@latest frontend -- --template react-ts

# Install core dependencies
npm install react-router-dom@6 axios@1 react-hook-form@7 zod

# Install UI dependencies
npm install tailwindcss@4 @tailwindcss/forms lucide-react sonner

# Install dev dependencies
npm install -D @types/node vitest @testing-library/react @testing-library/jest-dom @testing-library/user-event playwright

# Initialize Tailwind
npx tailwindcss init -p
```

---

## Architecture Patterns

### 1. Service Layer Pattern (MANDATORY)

**CRITICAL**: All API calls MUST go through the service layer. Never call axios directly in components.

**File**: `src/services/api.ts`

```typescript
import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios';

/**
 * Axios instance with interceptors
 * Centralized HTTP client configuration
 */
const apiClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Request Interceptor
 * Add auth tokens, correlation IDs, etc.
 */
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Add correlation ID for tracing
    const correlationId = crypto.randomUUID();
    config.headers['X-Correlation-ID'] = correlationId;

    // Future: Add authentication token
    // const token = localStorage.getItem('authToken');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }

    console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error: AxiosError) => {
    console.error('[API Request Error]', error);
    return Promise.reject(error);
  }
);

/**
 * Response Interceptor
 * Handle errors globally
 */
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    console.log(`[API Response] ${response.status} ${response.config.url}`);
    return response;
  },
  (error: AxiosError) => {
    // Handle specific error status codes
    if (error.response?.status === 401) {
      // Future: Redirect to login
      console.warn('Unauthorized access - authentication required');
    }

    if (error.response?.status === 500) {
      console.error('Server error:', error);
    }

    // Format error for consistent handling
    const apiError = {
      message: error.response?.data?.detail || error.message || 'An unexpected error occurred',
      status: error.response?.status,
      data: error.response?.data,
    };

    return Promise.reject(apiError);
  }
);

export default apiClient;
```

**File**: `src/services/studentService.ts`

```typescript
import apiClient from './api';
import { Student, StudentCreateDto, StudentUpdateDto, StudentFilters } from '../types/student';

/**
 * Student Service
 * All student-related API calls
 */
export const studentService = {
  /**
   * Get all students with optional filters
   */
  getAll: async (filters?: StudentFilters): Promise<Student[]> => {
    const params = new URLSearchParams();
    if (filters?.status) params.append('status', filters.status);
    if (filters?.lastName) params.append('lastName', filters.lastName);

    const response = await apiClient.get<{ content: Student[] }>(
      `/api/v1/students?${params.toString()}`
    );
    return response.data.content;
  },

  /**
   * Get student by ID
   */
  getById: async (id: string): Promise<Student> => {
    const response = await apiClient.get<Student>(`/api/v1/students/${id}`);
    return response.data;
  },

  /**
   * Create new student
   */
  create: async (student: StudentCreateDto): Promise<Student> => {
    const response = await apiClient.post<Student>('/api/v1/students', student);
    return response.data;
  },

  /**
   * Update student (only allowed fields)
   */
  update: async (id: string, updates: StudentUpdateDto): Promise<Student> => {
    const response = await apiClient.put<Student>(`/api/v1/students/${id}`, updates);
    return response.data;
  },

  /**
   * Delete student
   */
  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/api/v1/students/${id}`);
  },

  /**
   * Validate phone uniqueness
   */
  validatePhone: async (phone: string, excludeId?: string): Promise<boolean> => {
    try {
      const response = await apiClient.post<{ isUnique: boolean }>(
        '/api/v1/students/validate-phone',
        { phone, excludeId }
      );
      return response.data.isUnique;
    } catch (error) {
      console.error('Phone validation error:', error);
      return true; // Fail open for validation
    }
  },

  /**
   * Search students
   */
  search: async (query: string): Promise<Student[]> => {
    const response = await apiClient.get<{ content: Student[] }>(
      `/api/v1/students?search=${encodeURIComponent(query)}`
    );
    return response.data.content;
  },
};
```

### 2. Custom Hooks Pattern

**File**: `src/hooks/useStudents.ts`

```typescript
import { useState, useEffect, useCallback } from 'react';
import { studentService } from '../services/studentService';
import { Student, StudentCreateDto, StudentUpdateDto, StudentFilters } from '../types/student';
import { toast } from 'sonner';

/**
 * Custom hook for student data management
 * Encapsulates all student-related state and operations
 */
export function useStudents(initialFilters?: StudentFilters) {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Fetch students with filters
   */
  const fetchStudents = useCallback(async (filters?: StudentFilters) => {
    setLoading(true);
    setError(null);

    try {
      const data = await studentService.getAll(filters);
      setStudents(data);
    } catch (err: any) {
      const errorMessage = err.message || 'Failed to fetch students';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Create new student
   */
  const createStudent = useCallback(async (student: StudentCreateDto): Promise<Student | null> => {
    setLoading(true);
    setError(null);

    try {
      const newStudent = await studentService.create(student);
      setStudents((prev) => [newStudent, ...prev]);
      toast.success('Student registered successfully');
      return newStudent;
    } catch (err: any) {
      const errorMessage = err.message || 'Failed to create student';
      setError(errorMessage);
      toast.error(errorMessage);
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Update existing student
   */
  const updateStudent = useCallback(async (
    id: string,
    updates: StudentUpdateDto
  ): Promise<Student | null> => {
    setLoading(true);
    setError(null);

    try {
      const updatedStudent = await studentService.update(id, updates);
      setStudents((prev) =>
        prev.map((s) => (s.id === id ? updatedStudent : s))
      );
      toast.success('Student updated successfully');
      return updatedStudent;
    } catch (err: any) {
      const errorMessage = err.message || 'Failed to update student';
      setError(errorMessage);
      toast.error(errorMessage);
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Delete student
   */
  const deleteStudent = useCallback(async (id: string): Promise<boolean> => {
    setLoading(true);
    setError(null);

    try {
      await studentService.delete(id);
      setStudents((prev) => prev.filter((s) => s.id !== id));
      toast.success('Student deleted successfully');
      return true;
    } catch (err: any) {
      const errorMessage = err.message || 'Failed to delete student';
      setError(errorMessage);
      toast.error(errorMessage);
      return false;
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Fetch on mount if initial filters provided
   */
  useEffect(() => {
    if (initialFilters) {
      fetchStudents(initialFilters);
    }
  }, [initialFilters, fetchStudents]);

  return {
    students,
    loading,
    error,
    fetchStudents,
    createStudent,
    updateStudent,
    deleteStudent,
  };
}
```

---

## Component Implementation

### Page Component: StudentsPage.tsx

```typescript
import { useState, useEffect } from 'react';
import { useStudents } from '../hooks/useStudents';
import { StudentCard } from '../components/students/StudentCard';
import { StudentDialog } from '../components/students/StudentDialog';
import { SearchBar } from '../components/common/SearchBar';
import { Button } from '../components/ui/button';
import { Select } from '../components/ui/select';
import { Skeleton } from '../components/ui/skeleton';
import { UserPlus } from 'lucide-react';
import { Student, StudentStatus } from '../types/student';

/**
 * Students Page Component
 * Manages student listing, search, filter, and CRUD operations
 */
export function StudentsPage() {
  const { students, loading, error, fetchStudents, deleteStudent } = useStudents();

  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<StudentStatus | 'ALL'>('ALL');
  const [dialogState, setDialogState] = useState<{
    isOpen: boolean;
    mode: 'create' | 'edit' | null;
    student?: Student;
  }>({ isOpen: false, mode: null });

  /**
   * Fetch students on mount and when filters change
   */
  useEffect(() => {
    fetchStudents({
      lastName: searchQuery || undefined,
      status: statusFilter !== 'ALL' ? statusFilter : undefined,
    });
  }, [searchQuery, statusFilter, fetchStudents]);

  /**
   * Handle student deletion with confirmation
   */
  const handleDelete = async (student: Student) => {
    if (window.confirm(`Are you sure you want to delete ${student.firstName} ${student.lastName}?`)) {
      await deleteStudent(student.id);
    }
  };

  /**
   * Open dialog for creating new student
   */
  const handleCreateClick = () => {
    setDialogState({ isOpen: true, mode: 'create' });
  };

  /**
   * Open dialog for editing existing student
   */
  const handleEditClick = (student: Student) => {
    setDialogState({ isOpen: true, mode: 'edit', student });
  };

  /**
   * Close dialog
   */
  const handleDialogClose = () => {
    setDialogState({ isOpen: false, mode: null, student: undefined });
    fetchStudents(); // Refresh list
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Students</h1>
          <p className="text-gray-600 mt-1">Manage student registrations and profiles</p>
        </div>

        {/* Filters and Actions */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 mb-6">
          <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between">
            {/* Search */}
            <div className="flex-1 w-full sm:w-auto">
              <SearchBar
                placeholder="Search by name or guardian..."
                value={searchQuery}
                onChange={setSearchQuery}
              />
            </div>

            {/* Status Filter */}
            <Select
              value={statusFilter}
              onValueChange={(value) => setStatusFilter(value as StudentStatus | 'ALL')}
              className="w-full sm:w-48"
            >
              <option value="ALL">All Students</option>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
            </Select>

            {/* Create Button */}
            <Button onClick={handleCreateClick} className="w-full sm:w-auto">
              <UserPlus className="mr-2 h-4 w-4" />
              Register New Student
            </Button>
          </div>
        </div>

        {/* Error State */}
        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
            <p className="text-red-800">{error}</p>
          </div>
        )}

        {/* Loading State */}
        {loading && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {[...Array(6)].map((_, i) => (
              <Skeleton key={i} className="h-64 w-full" />
            ))}
          </div>
        )}

        {/* Empty State */}
        {!loading && students.length === 0 && (
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
            <p className="text-gray-500 text-lg">No students found</p>
            <p className="text-gray-400 mt-2">Try adjusting your search or filters</p>
          </div>
        )}

        {/* Student Grid */}
        {!loading && students.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {students.map((student) => (
              <StudentCard
                key={student.id}
                student={student}
                onEdit={() => handleEditClick(student)}
                onDelete={() => handleDelete(student)}
              />
            ))}
          </div>
        )}

        {/* Student Dialog */}
        <StudentDialog
          isOpen={dialogState.isOpen}
          mode={dialogState.mode!}
          student={dialogState.student}
          onClose={handleDialogClose}
        />
      </div>
    </div>
  );
}
```

---

## API Integration

### TypeScript Types (MUST match backend)

**File**: `src/types/student.ts`

```typescript
/**
 * Student interface
 * MUST match backend StudentResponse DTO exactly
 */
export interface Student {
  // Identification
  id: string; // Backend studentId (e.g., "STD-20260115-0001")

  // Personal Information
  firstName: string;
  lastName: string;
  dateOfBirth: string; // ISO 8601 format (YYYY-MM-DD)
  age: number; // Calculated from DOB
  adhaarNumber: string;
  identificationMarks: string;
  address: string;

  // Guardian Information
  guardianName: string; // Maps to fathersName on backend
  motherName: string;

  // Contact Information
  phone: string; // Maps to mobile on backend
  email: string;

  // Status
  status: StudentStatus;

  // Metadata
  version: number; // Optimistic locking
  createdAt: string; // ISO 8601 timestamp
  updatedAt: string; // ISO 8601 timestamp
}

export type StudentStatus = 'ACTIVE' | 'INACTIVE';

/**
 * DTO for creating new student
 */
export interface StudentCreateDto {
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
}

/**
 * DTO for updating student (restricted fields only)
 */
export interface StudentUpdateDto {
  firstName?: string;
  lastName?: string;
  phone?: string;
  status?: StudentStatus;
  version: number; // Required for optimistic locking
}

/**
 * Filters for student search
 */
export interface StudentFilters {
  status?: StudentStatus;
  lastName?: string;
  search?: string;
}
```

---

## Form Management

### Zod Validation Schema

**File**: `src/utils/validation.ts`

```typescript
import { z } from 'zod';

/**
 * Calculate age from date of birth
 */
export function calculateAge(dob: string): number {
  const today = new Date();
  const birthDate = new Date(dob);
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }

  return age;
}

/**
 * Student Form Validation Schema
 * MUST match backend validation rules
 */
export const studentSchema = z.object({
  firstName: z
    .string()
    .min(2, 'First name must be at least 2 characters')
    .max(100, 'First name cannot exceed 100 characters')
    .regex(/^[a-zA-Z\s]+$/, 'First name can only contain letters and spaces'),

  lastName: z
    .string()
    .min(2, 'Last name must be at least 2 characters')
    .max(100, 'Last name cannot exceed 100 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Last name can only contain letters and spaces'),

  dateOfBirth: z
    .string()
    .refine((dob) => {
      const age = calculateAge(dob);
      return age >= 3 && age <= 18;
    }, 'Student age must be between 3 and 18 years at registration')
    .refine((dob) => {
      return new Date(dob) <= new Date();
    }, 'Date of birth cannot be in the future'),

  phone: z
    .string()
    .regex(/^\d{10}$/, 'Phone number must be exactly 10 digits'),

  email: z
    .string()
    .email('Please enter a valid email address'),

  adhaarNumber: z
    .string()
    .regex(/^\d{12}$/, 'Adhaar number must be exactly 12 digits')
    .optional()
    .or(z.literal('')),

  address: z
    .string()
    .min(10, 'Address must be at least 10 characters')
    .max(500, 'Address cannot exceed 500 characters'),

  identificationMarks: z
    .string()
    .max(200, 'Identification marks cannot exceed 200 characters')
    .optional()
    .or(z.literal('')),

  guardianName: z
    .string()
    .min(1, 'Guardian name is required')
    .max(100, 'Guardian name cannot exceed 100 characters'),

  motherName: z
    .string()
    .min(1, 'Mother name is required')
    .max(100, 'Mother name cannot exceed 100 characters'),

  status: z.enum(['ACTIVE', 'INACTIVE']).optional(),
});

export type StudentFormData = z.infer<typeof studentSchema>;
```

### React Hook Form Implementation

**File**: `src/components/students/StudentForm.tsx`

```typescript
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { studentSchema, StudentFormData } from '../../utils/validation';
import { Input } from '../ui/input';
import { Textarea } from '../ui/textarea';
import { Button } from '../ui/button';
import { Label } from '../ui/label';
import { Select } from '../ui/select';

interface StudentFormProps {
  initialData?: Partial<StudentFormData>;
  onSubmit: (data: StudentFormData) => Promise<void>;
  isEditMode?: boolean;
}

export function StudentForm({ initialData, onSubmit, isEditMode = false }: StudentFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<StudentFormData>({
    resolver: zodResolver(studentSchema),
    defaultValues: initialData,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Personal Information Section */}
      <div className="space-y-4">
        <h3 className="text-lg font-semibold text-gray-900">Personal Information</h3>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* First Name */}
          <div>
            <Label htmlFor="firstName">First Name *</Label>
            <Input
              id="firstName"
              {...register('firstName')}
              className={errors.firstName ? 'border-red-500' : ''}
              disabled={isSubmitting}
            />
            {errors.firstName && (
              <p className="text-red-500 text-sm mt-1">{errors.firstName.message}</p>
            )}
          </div>

          {/* Last Name */}
          <div>
            <Label htmlFor="lastName">Last Name *</Label>
            <Input
              id="lastName"
              {...register('lastName')}
              className={errors.lastName ? 'border-red-500' : ''}
              disabled={isSubmitting}
            />
            {errors.lastName && (
              <p className="text-red-500 text-sm mt-1">{errors.lastName.message}</p>
            )}
          </div>
        </div>

        {/* Date of Birth */}
        <div>
          <Label htmlFor="dateOfBirth">Date of Birth *</Label>
          <Input
            id="dateOfBirth"
            type="date"
            {...register('dateOfBirth')}
            className={errors.dateOfBirth ? 'border-red-500' : ''}
            disabled={isEditMode || isSubmitting} // Immutable in edit mode
          />
          {errors.dateOfBirth && (
            <p className="text-red-500 text-sm mt-1">{errors.dateOfBirth.message}</p>
          )}
        </div>

        {/* Adhaar Number */}
        <div>
          <Label htmlFor="adhaarNumber">Adhaar Number</Label>
          <Input
            id="adhaarNumber"
            {...register('adhaarNumber')}
            placeholder="123456789012"
            className={errors.adhaarNumber ? 'border-red-500' : ''}
            disabled={isEditMode || isSubmitting} // Immutable in edit mode
          />
          {errors.adhaarNumber && (
            <p className="text-red-500 text-sm mt-1">{errors.adhaarNumber.message}</p>
          )}
        </div>

        {/* Address */}
        <div>
          <Label htmlFor="address">Address *</Label>
          <Textarea
            id="address"
            {...register('address')}
            rows={3}
            className={errors.address ? 'border-red-500' : ''}
            disabled={isSubmitting}
          />
          {errors.address && (
            <p className="text-red-500 text-sm mt-1">{errors.address.message}</p>
          )}
        </div>

        {/* Identification Marks */}
        <div>
          <Label htmlFor="identificationMarks">Identification Marks</Label>
          <Textarea
            id="identificationMarks"
            {...register('identificationMarks')}
            rows={2}
            className={errors.identificationMarks ? 'border-red-500' : ''}
            disabled={isSubmitting}
          />
          {errors.identificationMarks && (
            <p className="text-red-500 text-sm mt-1">{errors.identificationMarks.message}</p>
          )}
        </div>
      </div>

      {/* Guardian Information Section */}
      <div className="space-y-4">
        <h3 className="text-lg font-semibold text-gray-900">Guardian Information</h3>

        <div>
          <Label htmlFor="guardianName">Father's Name / Guardian *</Label>
          <Input
            id="guardianName"
            {...register('guardianName')}
            className={errors.guardianName ? 'border-red-500' : ''}
            disabled={isSubmitting}
          />
          {errors.guardianName && (
            <p className="text-red-500 text-sm mt-1">{errors.guardianName.message}</p>
          )}
        </div>

        <div>
          <Label htmlFor="motherName">Mother's Name *</Label>
          <Input
            id="motherName"
            {...register('motherName')}
            className={errors.motherName ? 'border-red-500' : ''}
            disabled={isSubmitting}
          />
          {errors.motherName && (
            <p className="text-red-500 text-sm mt-1">{errors.motherName.message}</p>
          )}
        </div>
      </div>

      {/* Contact Information Section */}
      <div className="space-y-4">
        <h3 className="text-lg font-semibold text-gray-900">Contact Information</h3>

        <div>
          <Label htmlFor="phone">Mobile Number *</Label>
          <Input
            id="phone"
            {...register('phone')}
            placeholder="9876543210"
            className={errors.phone ? 'border-red-500' : ''}
            disabled={isSubmitting}
          />
          {errors.phone && (
            <p className="text-red-500 text-sm mt-1">{errors.phone.message}</p>
          )}
        </div>

        <div>
          <Label htmlFor="email">Email *</Label>
          <Input
            id="email"
            type="email"
            {...register('email')}
            placeholder="student@example.com"
            className={errors.email ? 'border-red-500' : ''}
            disabled={isEditMode || isSubmitting} // Immutable in edit mode
          />
          {errors.email && (
            <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>
          )}
        </div>
      </div>

      {/* Status (Edit mode only) */}
      {isEditMode && (
        <div>
          <Label htmlFor="status">Status</Label>
          <Select
            id="status"
            {...register('status')}
            disabled={isSubmitting}
          >
            <option value="ACTIVE">Active</option>
            <option value="INACTIVE">Inactive</option>
          </Select>
        </div>
      )}

      {/* Submit Button */}
      <div className="flex justify-end space-x-4">
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Saving...' : isEditMode ? 'Update Student' : 'Register Student'}
        </Button>
      </div>
    </form>
  );
}
```

---

## State Management

### React Context for Global State

**File**: `src/contexts/AppContext.tsx`

```typescript
import { createContext, useContext, useState, ReactNode } from 'react';

interface AppContextValue {
  theme: 'light' | 'dark';
  setTheme: (theme: 'light' | 'dark') => void;
}

const AppContext = createContext<AppContextValue | undefined>(undefined);

export function AppProvider({ children }: { children: ReactNode }) {
  const [theme, setTheme] = useState<'light' | 'dark'>('light');

  return (
    <AppContext.Provider value={{ theme, setTheme }}>
      {children}
    </AppContext.Provider>
  );
}

export function useAppContext() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useAppContext must be used within AppProvider');
  }
  return context;
}
```

---

## Styling Guidelines

### CRITICAL: Use Reference Code Styles ONLY

**From Reference Code**: Copy Tailwind configuration EXACTLY

**File**: `tailwind.config.ts`

```typescript
// Copy from frontend/reference-code/tailwind.config.ts
import type { Config } from 'tailwindcss';

const config: Config = {
  darkMode: ['class'],
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      borderRadius: {
        lg: 'var(--radius)',
        md: 'calc(var(--radius) - 2px)',
        sm: 'calc(var(--radius) - 4px)',
      },
      colors: {
        background: 'hsl(var(--background))',
        foreground: 'hsl(var(--foreground))',
        card: {
          DEFAULT: 'hsl(var(--card))',
          foreground: 'hsl(var(--card-foreground))',
        },
        primary: {
          DEFAULT: 'hsl(var(--primary))',
          foreground: 'hsl(var(--primary-foreground))',
        },
        // ... copy rest from Reference Code
      },
    },
  },
  plugins: [require('tailwindcss-animate')],
};

export default config;
```

---

## Testing Requirements

### Component Test Example (Vitest)

```typescript
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { StudentCard } from '../StudentCard';
import { Student } from '../../types/student';

describe('StudentCard', () => {
  const mockStudent: Student = {
    id: 'STD-20260115-0001',
    firstName: 'John',
    lastName: 'Doe',
    dateOfBirth: '2010-01-15',
    age: 15,
    phone: '9876543210',
    email: 'john@example.com',
    address: '123 Main Street',
    guardianName: 'Father Doe',
    motherName: 'Mother Doe',
    adhaarNumber: '123456789012',
    identificationMarks: 'Mole on left arm',
    status: 'ACTIVE',
    version: 1,
    createdAt: '2026-01-15T10:00:00Z',
    updatedAt: '2026-01-15T10:00:00Z',
  };

  it('should render student information', () => {
    render(<StudentCard student={mockStudent} onEdit={vi.fn()} onDelete={vi.fn()} />);

    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('STD-20260115-0001')).toBeInTheDocument();
    expect(screen.getByText('9876543210')).toBeInTheDocument();
  });

  it('should call onEdit when edit button clicked', async () => {
    const onEdit = vi.fn();
    render(<StudentCard student={mockStudent} onEdit={onEdit} onDelete={vi.fn()} />);

    const editButton = screen.getByRole('button', { name: /edit/i });
    fireEvent.click(editButton);

    await waitFor(() => {
      expect(onEdit).toHaveBeenCalledTimes(1);
    });
  });
});
```

---

## Environment Configuration

### .env.development

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_STUDENT_SERVICE_URL=http://localhost:8081
VITE_CONFIG_SERVICE_URL=http://localhost:8082
```

### .env.production

```env
VITE_API_BASE_URL=https://api.schoolms.com
VITE_STUDENT_SERVICE_URL=https://students.schoolms.com
VITE_CONFIG_SERVICE_URL=https://config.schoolms.com
```

---

## Dockerfile

```dockerfile
# Build stage
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

---

## Deliverables Checklist

### Mandatory Deliverables

- [ ] UI mirrors Reference Code 1:1 (NO custom styles)
- [ ] Forms validate via Zod schemas matching backend rules
- [ ] Service layer handles all HTTP methods (GET, POST, PUT, DELETE)
- [ ] Custom hooks encapsulate data fetching logic
- [ ] Error handling with toast notifications
- [ ] Loading states (skeletons, spinners)
- [ ] TypeScript interfaces match backend DTOs exactly
- [ ] `docker build` passes successfully
- [ ] All API endpoints from OpenAPI spec implemented
- [ ] Responsive design works on mobile/tablet/desktop

---

**End of Frontend Implementation Guidelines**
