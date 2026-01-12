# Frontend Implementation Guide
**School Management System - Phase 1**

**Version**: 1.0
**Date**: January 8, 2026
**Status**: Active

---

## Table of Contents

1. [Overview](#overview)
2. [Project Setup](#project-setup)
3. [Architecture Patterns](#architecture-patterns)
4. [Service Layer Implementation](#service-layer-implementation)
5. [Component Development](#component-development)
6. [State Management](#state-management)
7. [Form Handling and Validation](#form-handling-and-validation)
8. [Error Handling](#error-handling)
9. [Performance Optimization](#performance-optimization)
10. [Styling Guidelines](#styling-guidelines)
11. [Testing Strategy](#testing-strategy)

---

## Overview

### Purpose

This guide enforces **production-ready frontend development patterns** as specified in `FRONTEND_DESIGN_SPECIFICATION.md`, with emphasis on the Service Layer pattern and strict architectural compliance.

### Key Architectural Constraints

**MANDATORY PATTERNS** (Override generic React defaults):

1. **Service Layer Pattern**: Centralized API communication in `src/services/`
2. **State Management**: Context API for global, Hooks for local (NOT Redux/Zustand)
3. **Form Management**: React Hook Form + Zod validation
4. **Component Structure**: Container/Presentation separation
5. **Directory Structure**: As defined in spec (MUST follow exactly)

---

## Project Setup

### 1. Initialize Vite Project

```bash
npm create vite@latest frontend -- --template react-ts
cd frontend
npm install
```

---

### 2. Install Dependencies

```bash
# Core dependencies
npm install react-router-dom axios

# UI Framework
npm install tailwindcss@latest postcss autoprefixer
npm install -D @types/node

# Shadcn/ui (component library)
npx shadcn-ui@latest init

# Form Management
npm install react-hook-form zod @hookform/resolvers

# Icons
npm install lucide-react

# Notifications
npm install sonner

# Date handling
npm install date-fns

# Development tools
npm install -D @typescript-eslint/eslint-plugin @typescript-eslint/parser
npm install -D eslint-config-prettier eslint-plugin-react
npm install -D prettier
```

---

### 3. Project Structure (MUST FOLLOW)

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
│   │   │   ├── ViewStudentDialog.tsx
│   │   │   └── StudentFilters.tsx
│   │   ├── configurations/
│   │   │   ├── ConfigurationTable.tsx
│   │   │   └── ConfigurationDialog.tsx
│   │   ├── common/
│   │   │   ├── ErrorBoundary.tsx
│   │   │   ├── LoadingSpinner.tsx
│   │   │   └── ConfirmDialog.tsx
│   │   └── ui/             # Shadcn/ui components
│   │       ├── button.tsx
│   │       ├── dialog.tsx
│   │       ├── input.tsx
│   │       └── ...
│   ├── pages/
│   │   ├── HomePage.tsx
│   │   ├── StudentsPage.tsx
│   │   └── ConfigurationsPage.tsx
│   ├── services/           # API LAYER (MANDATORY)
│   │   ├── api.ts          # HTTP client setup
│   │   ├── studentService.ts
│   │   └── configurationService.ts
│   ├── contexts/
│   │   └── AppContext.tsx
│   ├── hooks/
│   │   ├── useStudents.ts
│   │   ├── useConfigurations.ts
│   │   ├── useDebounce.ts
│   │   └── useToast.ts
│   ├── types/
│   │   ├── student.ts
│   │   ├── configuration.ts
│   │   └── api.ts
│   ├── utils/
│   │   ├── validation.ts
│   │   ├── formatting.ts
│   │   └── constants.ts
│   ├── styles/
│   │   ├── index.css
│   │   └── tailwind.css
│   ├── App.tsx
│   └── main.tsx
├── .env.development
├── .env.production
├── vite.config.ts
├── tailwind.config.ts
├── tsconfig.json
└── package.json
```

---

## Architecture Patterns

### 1. Service Layer Pattern (MANDATORY)

**Purpose**: Abstraction from HTTP implementation, centralized API calls

**api.ts** (HTTP Client Setup):

```typescript
// src/services/api.ts
import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig } from 'axios';

const apiClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Add correlation ID for tracing
    const requestId = crypto.randomUUID();
    config.headers['X-Request-ID'] = requestId;

    // Future: Add auth token
    // const token = localStorage.getItem('authToken');
    // if (token) config.headers.Authorization = `Bearer ${token}`;

    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    // Global error handling
    if (error.response?.status === 401) {
      // Handle unauthorized (future auth)
      console.warn('Unauthorized access - redirect to login');
    }

    if (error.response?.status === 500) {
      console.error('Server error:', error);
    }

    // Re-throw to allow component-level handling
    return Promise.reject(error);
  }
);

export default apiClient;
```

---

**studentService.ts** (API Service):

```typescript
// src/services/studentService.ts
import apiClient from './api';
import type { Student, StudentCreateDto, StudentUpdateDto, StudentListResponse } from '@/types/student';
import type { ApiResponse } from '@/types/api';

export const studentService = {
  /**
   * Get all students with optional filters
   */
  async getAll(params?: {
    search?: string;
    status?: 'ACTIVE' | 'INACTIVE' | 'ALL';
    page?: number;
    size?: number;
  }): Promise<StudentListResponse> {
    const { data } = await apiClient.get<StudentListResponse>('/api/v1/students', { params });
    return data;
  },

  /**
   * Get student by ID
   */
  async getById(id: string): Promise<Student> {
    const { data } = await apiClient.get<Student>(`/api/v1/students/${id}`);
    return data;
  },

  /**
   * Create new student
   */
  async create(student: StudentCreateDto): Promise<Student> {
    const { data } = await apiClient.post<Student>('/api/v1/students', student);
    return data;
  },

  /**
   * Update student (only allowed fields)
   */
  async update(id: string, updates: StudentUpdateDto): Promise<Student> {
    const { data } = await apiClient.patch<Student>(`/api/v1/students/${id}`, updates);
    return data;
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
    const { data } = await apiClient.post<{ isUnique: boolean }>(
      '/api/v1/students/validate-phone',
      { phone, excludeStudentId: excludeId }
    );
    return data.isUnique;
  },

  /**
   * Get student statistics
   */
  async getStatistics(): Promise<{
    totalStudents: number;
    activeStudents: number;
    inactiveStudents: number;
  }> {
    const { data } = await apiClient.get('/api/v1/students/statistics');
    return data;
  },

  /**
   * Search students
   */
  async search(query: string): Promise<Student[]> {
    const { data } = await apiClient.get<StudentListResponse>('/api/v1/students', {
      params: { search: query },
    });
    return data.students;
  },
};
```

---

### 2. Type Definitions

**student.ts**:

```typescript
// src/types/student.ts

export interface Student {
  // Identification
  id: string; // e.g., "STU-2026-00001"

  // Personal Information
  firstName: string;
  lastName: string;
  dateOfBirth: string; // ISO 8601 date (YYYY-MM-DD)
  age: number; // Calculated from DOB
  adhaarNumber: string; // 12 digits
  identificationMarks: string | null;
  address: string;

  // Guardian Information
  guardianName: string;
  motherName: string;

  // Contact Information
  phone: string; // 10 digits
  email: string;

  // Status
  status: 'ACTIVE' | 'INACTIVE';

  // Metadata
  createdAt: string; // ISO 8601 timestamp
  updatedAt: string; // ISO 8601 timestamp
}

export type StudentCreateDto = Omit<Student, 'id' | 'age' | 'status' | 'createdAt' | 'updatedAt'>;

export interface StudentUpdateDto {
  firstName?: string;
  lastName?: string;
  phone?: string;
  status?: 'ACTIVE' | 'INACTIVE';
}

export interface StudentListResponse {
  students: Student[];
  totalCount: number;
  activeCount: number;
  inactiveCount: number;
  page?: number;
  size?: number;
  totalPages?: number;
}

export interface StudentFilters {
  search: string;
  status: 'ALL' | 'ACTIVE' | 'INACTIVE';
}
```

---

**api.ts** (Common types):

```typescript
// src/types/api.ts

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: ApiError;
  message?: string;
}

export interface ApiError {
  code: string;
  message: string;
  details?: Record<string, string[]>;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
```

---

## Component Development

### 1. Container Components (Stateful)

**StudentsPage.tsx** (Container):

```typescript
// src/pages/StudentsPage.tsx
import { useState, useEffect } from 'react';
import { toast } from 'sonner';
import { studentService } from '@/services/studentService';
import type { Student, StudentFilters } from '@/types/student';
import StudentCard from '@/components/students/StudentCard';
import StudentDialog from '@/components/students/StudentDialog';
import StudentFiltersComponent from '@/components/students/StudentFilters';
import { Button } from '@/components/ui/button';
import { Plus } from 'lucide-react';

export default function StudentsPage() {
  // State
  const [students, setStudents] = useState<Student[]>([]);
  const [filteredStudents, setFilteredStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filters, setFilters] = useState<StudentFilters>({
    search: '',
    status: 'ALL',
  });
  const [dialogState, setDialogState] = useState<{
    isOpen: boolean;
    mode: 'create' | 'edit' | null;
    student?: Student;
  }>({
    isOpen: false,
    mode: null,
  });

  // Fetch students on mount
  useEffect(() => {
    fetchStudents();
  }, []);

  // Filter students when filters change
  useEffect(() => {
    filterStudents();
  }, [students, filters]);

  const fetchStudents = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await studentService.getAll();
      setStudents(response.students);
    } catch (err) {
      setError('Failed to load students. Please try again.');
      toast.error('Failed to load students');
      console.error('Error fetching students:', err);
    } finally {
      setLoading(false);
    }
  };

  const filterStudents = () => {
    let filtered = students;

    // Status filter
    if (filters.status !== 'ALL') {
      filtered = filtered.filter((s) => s.status === filters.status);
    }

    // Search filter (client-side)
    if (filters.search) {
      const query = filters.search.toLowerCase();
      filtered = filtered.filter(
        (s) =>
          s.id.toLowerCase().includes(query) ||
          s.firstName.toLowerCase().includes(query) ||
          s.lastName.toLowerCase().includes(query) ||
          s.guardianName.toLowerCase().includes(query)
      );
    }

    setFilteredStudents(filtered);
  };

  const handleCreate = async (data: StudentCreateDto) => {
    try {
      await studentService.create(data);
      toast.success('Student registered successfully');
      setDialogState({ isOpen: false, mode: null });
      await fetchStudents(); // Refresh list
    } catch (err) {
      toast.error('Failed to create student');
      throw err; // Re-throw to show error in dialog
    }
  };

  const handleUpdate = async (id: string, data: StudentUpdateDto) => {
    try {
      await studentService.update(id, data);
      toast.success('Student updated successfully');
      setDialogState({ isOpen: false, mode: null });
      await fetchStudents();
    } catch (err) {
      toast.error('Failed to update student');
      throw err;
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Are you sure you want to delete this student?')) return;

    try {
      await studentService.delete(id);
      toast.success('Student deleted successfully');
      await fetchStudents();
    } catch (err) {
      toast.error('Failed to delete student');
      console.error('Error deleting student:', err);
    }
  };

  if (loading) {
    return <div className="flex items-center justify-center h-screen">Loading...</div>;
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center h-screen">
        <p className="text-red-600 mb-4">{error}</p>
        <Button onClick={fetchStudents}>Retry</Button>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Students</h1>
        <Button onClick={() => setDialogState({ isOpen: true, mode: 'create' })}>
          <Plus className="mr-2 h-4 w-4" />
          Register New Student
        </Button>
      </div>

      {/* Filters */}
      <StudentFiltersComponent filters={filters} onFilterChange={setFilters} />

      {/* Student Grid */}
      {filteredStudents.length === 0 ? (
        <div className="text-center py-12 text-gray-500">No students found</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredStudents.map((student) => (
            <StudentCard
              key={student.id}
              student={student}
              onEdit={(s) => setDialogState({ isOpen: true, mode: 'edit', student: s })}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}

      {/* Create/Edit Dialog */}
      <StudentDialog
        isOpen={dialogState.isOpen}
        mode={dialogState.mode || 'create'}
        student={dialogState.student}
        onClose={() => setDialogState({ isOpen: false, mode: null })}
        onSubmit={dialogState.mode === 'create' ? handleCreate : (data) => handleUpdate(dialogState.student!.id, data)}
      />
    </div>
  );
}
```

---

### 2. Presentation Components (Pure UI)

**StudentCard.tsx**:

```typescript
// src/components/students/StudentCard.tsx
import type { Student } from '@/types/student';
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Edit, Trash2, Eye } from 'lucide-react';

interface StudentCardProps {
  student: Student;
  onEdit: (student: Student) => void;
  onDelete: (id: string) => void;
}

export default function StudentCard({ student, onEdit, onDelete }: StudentCardProps) {
  return (
    <Card className="hover:shadow-lg transition-shadow">
      <CardHeader>
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="text-lg">{student.id}</CardTitle>
            <p className="text-sm text-gray-500">
              {student.firstName} {student.lastName}
            </p>
          </div>
          <Badge variant={student.status === 'ACTIVE' ? 'default' : 'secondary'}>
            {student.status}
          </Badge>
        </div>
      </CardHeader>

      <CardContent className="space-y-2">
        <div>
          <p className="text-sm text-gray-500">Guardian</p>
          <p className="font-medium">{student.guardianName}</p>
        </div>
        <div>
          <p className="text-sm text-gray-500">Phone</p>
          <p className="font-medium">{student.phone}</p>
        </div>
        <div>
          <p className="text-sm text-gray-500">Email</p>
          <p className="font-medium truncate">{student.email}</p>
        </div>
      </CardContent>

      <CardFooter className="flex justify-end gap-2">
        <Button variant="outline" size="sm" onClick={() => onEdit(student)}>
          <Edit className="h-4 w-4 mr-1" />
          Edit
        </Button>
        <Button variant="destructive" size="sm" onClick={() => onDelete(student.id)}>
          <Trash2 className="h-4 w-4 mr-1" />
          Delete
        </Button>
      </CardFooter>
    </Card>
  );
}
```

---

## State Management

### 1. Global State (Context API)

**AppContext.tsx**:

```typescript
// src/contexts/AppContext.tsx
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

export function useApp() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within AppProvider');
  }
  return context;
}
```

---

### 2. Custom Hooks

**useStudents.ts**:

```typescript
// src/hooks/useStudents.ts
import { useState, useCallback } from 'react';
import { studentService } from '@/services/studentService';
import type { Student, StudentCreateDto, StudentUpdateDto } from '@/types/student';

export function useStudents() {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchStudents = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await studentService.getAll();
      setStudents(response.students);
    } catch (err) {
      setError('Failed to fetch students');
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const createStudent = useCallback(async (data: StudentCreateDto) => {
    try {
      const newStudent = await studentService.create(data);
      setStudents((prev) => [...prev, newStudent]);
      return newStudent;
    } catch (err) {
      setError('Failed to create student');
      throw err;
    }
  }, []);

  const updateStudent = useCallback(async (id: string, data: StudentUpdateDto) => {
    try {
      const updated = await studentService.update(id, data);
      setStudents((prev) => prev.map((s) => (s.id === id ? updated : s)));
      return updated;
    } catch (err) {
      setError('Failed to update student');
      throw err;
    }
  }, []);

  const deleteStudent = useCallback(async (id: string) => {
    try {
      await studentService.delete(id);
      setStudents((prev) => prev.filter((s) => s.id !== id));
    } catch (err) {
      setError('Failed to delete student');
      throw err;
    }
  }, []);

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

## Form Handling and Validation

### 1. Zod Validation Schema

**validation.ts**:

```typescript
// src/utils/validation.ts
import { z } from 'zod';

// Calculate age from date of birth
function calculateAge(dob: Date): number {
  const today = new Date();
  let age = today.getFullYear() - dob.getFullYear();
  const monthDiff = today.getMonth() - dob.getMonth();
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dob.getDate())) {
    age--;
  }
  return age;
}

export const studentSchema = z.object({
  firstName: z
    .string()
    .min(1, 'First name is required')
    .max(50, 'First name cannot exceed 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'First name can only contain letters and spaces'),

  lastName: z
    .string()
    .min(1, 'Last name is required')
    .max(50, 'Last name cannot exceed 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Last name can only contain letters and spaces'),

  dateOfBirth: z
    .string()
    .refine((val) => !isNaN(Date.parse(val)), 'Invalid date format')
    .refine((val) => new Date(val) <= new Date(), 'Date of birth cannot be in the future')
    .refine((val) => {
      const age = calculateAge(new Date(val));
      return age >= 3 && age <= 18;
    }, 'Student must be between 3 and 18 years old'),

  adhaarNumber: z
    .string()
    .regex(/^\d{12}$/, 'Adhaar number must be exactly 12 digits'),

  phone: z
    .string()
    .regex(/^\d{10}$/, 'Phone number must be exactly 10 digits'),

  email: z
    .string()
    .email('Invalid email format'),

  address: z
    .string()
    .min(10, 'Address must be at least 10 characters')
    .max(500, 'Address cannot exceed 500 characters'),

  identificationMarks: z
    .string()
    .max(200, 'Identification marks cannot exceed 200 characters')
    .optional()
    .nullable(),

  guardianName: z
    .string()
    .min(1, 'Guardian name is required')
    .max(100, 'Guardian name cannot exceed 100 characters'),

  motherName: z
    .string()
    .min(1, 'Mother name is required')
    .max(100, 'Mother name cannot exceed 100 characters'),
});

export type StudentFormData = z.infer<typeof studentSchema>;
```

---

### 2. Form Component with React Hook Form

**StudentDialog.tsx**:

```typescript
// src/components/students/StudentDialog.tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { studentSchema, type StudentFormData } from '@/utils/validation';
import type { Student, StudentCreateDto, StudentUpdateDto } from '@/types/student';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { useState } from 'react';

interface StudentDialogProps {
  isOpen: boolean;
  mode: 'create' | 'edit';
  student?: Student;
  onClose: () => void;
  onSubmit: (data: StudentCreateDto | StudentUpdateDto) => Promise<void>;
}

export default function StudentDialog({ isOpen, mode, student, onClose, onSubmit }: StudentDialogProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<StudentFormData>({
    resolver: zodResolver(studentSchema),
    defaultValues: mode === 'edit' && student ? {
      firstName: student.firstName,
      lastName: student.lastName,
      dateOfBirth: student.dateOfBirth,
      adhaarNumber: student.adhaarNumber,
      phone: student.phone,
      email: student.email,
      address: student.address,
      identificationMarks: student.identificationMarks,
      guardianName: student.guardianName,
      motherName: student.motherName,
    } : undefined,
  });

  const handleFormSubmit = async (data: StudentFormData) => {
    try {
      setIsSubmitting(true);
      setError(null);
      await onSubmit(data);
      reset();
    } catch (err: any) {
      setError(err.response?.data?.detail || 'An error occurred');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            {mode === 'create' ? 'Register New Student' : 'Edit Student'}
          </DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
          {/* Error Alert */}
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              {error}
            </div>
          )}

          {/* Personal Information */}
          <div className="space-y-4">
            <h3 className="font-semibold">Personal Information</h3>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="firstName">First Name *</Label>
                <Input
                  id="firstName"
                  {...register('firstName')}
                  disabled={mode === 'edit'} // Editable in edit mode per spec
                  className={errors.firstName ? 'border-red-500' : ''}
                />
                {errors.firstName && (
                  <p className="text-sm text-red-500 mt-1">{errors.firstName.message}</p>
                )}
              </div>

              <div>
                <Label htmlFor="lastName">Last Name *</Label>
                <Input
                  id="lastName"
                  {...register('lastName')}
                  disabled={mode === 'edit'}
                  className={errors.lastName ? 'border-red-500' : ''}
                />
                {errors.lastName && (
                  <p className="text-sm text-red-500 mt-1">{errors.lastName.message}</p>
                )}
              </div>
            </div>

            <div>
              <Label htmlFor="dateOfBirth">Date of Birth *</Label>
              <Input
                id="dateOfBirth"
                type="date"
                {...register('dateOfBirth')}
                disabled={mode === 'edit'} // Immutable
                className={errors.dateOfBirth ? 'border-red-500' : ''}
              />
              {errors.dateOfBirth && (
                <p className="text-sm text-red-500 mt-1">{errors.dateOfBirth.message}</p>
              )}
            </div>

            <div>
              <Label htmlFor="adhaarNumber">Adhaar Number *</Label>
              <Input
                id="adhaarNumber"
                {...register('adhaarNumber')}
                disabled={mode === 'edit'} // Immutable
                maxLength={12}
                placeholder="123456789012"
                className={errors.adhaarNumber ? 'border-red-500' : ''}
              />
              {errors.adhaarNumber && (
                <p className="text-sm text-red-500 mt-1">{errors.adhaarNumber.message}</p>
              )}
            </div>

            <div>
              <Label htmlFor="address">Address *</Label>
              <Textarea
                id="address"
                {...register('address')}
                disabled={mode === 'edit'} // Immutable
                rows={3}
                className={errors.address ? 'border-red-500' : ''}
              />
              {errors.address && (
                <p className="text-sm text-red-500 mt-1">{errors.address.message}</p>
              )}
            </div>

            <div>
              <Label htmlFor="identificationMarks">Identification Marks</Label>
              <Textarea
                id="identificationMarks"
                {...register('identificationMarks')}
                disabled={mode === 'edit'} // Immutable
                rows={2}
              />
            </div>
          </div>

          {/* Guardian Information */}
          <div className="space-y-4">
            <h3 className="font-semibold">Guardian Information</h3>

            <div>
              <Label htmlFor="guardianName">Guardian Name *</Label>
              <Input
                id="guardianName"
                {...register('guardianName')}
                disabled={mode === 'edit'} // Immutable
                className={errors.guardianName ? 'border-red-500' : ''}
              />
              {errors.guardianName && (
                <p className="text-sm text-red-500 mt-1">{errors.guardianName.message}</p>
              )}
            </div>

            <div>
              <Label htmlFor="motherName">Mother Name *</Label>
              <Input
                id="motherName"
                {...register('motherName')}
                disabled={mode === 'edit'} // Immutable
                className={errors.motherName ? 'border-red-500' : ''}
              />
              {errors.motherName && (
                <p className="text-sm text-red-500 mt-1">{errors.motherName.message}</p>
              )}
            </div>
          </div>

          {/* Contact Information */}
          <div className="space-y-4">
            <h3 className="font-semibold">Contact Information</h3>

            <div>
              <Label htmlFor="phone">Phone *</Label>
              <Input
                id="phone"
                {...register('phone')}
                maxLength={10}
                placeholder="9876543210"
                className={errors.phone ? 'border-red-500' : ''}
              />
              {errors.phone && (
                <p className="text-sm text-red-500 mt-1">{errors.phone.message}</p>
              )}
            </div>

            <div>
              <Label htmlFor="email">Email *</Label>
              <Input
                id="email"
                type="email"
                {...register('email')}
                disabled={mode === 'edit'} // Immutable
                className={errors.email ? 'border-red-500' : ''}
              />
              {errors.email && (
                <p className="text-sm text-red-500 mt-1">{errors.email.message}</p>
              )}
            </div>
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={onClose} disabled={isSubmitting}>
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Saving...' : mode === 'create' ? 'Register Student' : 'Update Student'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
```

---

## Error Handling

**ErrorBoundary.tsx**:

```typescript
// src/components/common/ErrorBoundary.tsx
import { Component, ErrorInfo, ReactNode } from 'react';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
}

export default class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
    // TODO: Log to error tracking service (e.g., Sentry)
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex items-center justify-center min-h-screen p-4">
          <Alert variant="destructive" className="max-w-md">
            <AlertTitle>Something went wrong</AlertTitle>
            <AlertDescription>
              {this.state.error?.message || 'An unexpected error occurred'}
            </AlertDescription>
            <Button
              onClick={() => window.location.reload()}
              className="mt-4"
            >
              Refresh Page
            </Button>
          </Alert>
        </div>
      );
    }

    return this.props.children;
  }
}
```

---

## Performance Optimization

### 1. Memoization

```typescript
import { memo, useMemo, useCallback } from 'react';

// Memoize expensive components
const StudentCard = memo(({ student, onEdit, onDelete }: StudentCardProps) => {
  // Component implementation
});

// Memoize expensive calculations
const sortedStudents = useMemo(
  () => students.sort((a, b) => a.lastName.localeCompare(b.lastName)),
  [students]
);

// Memoize callbacks
const handleDelete = useCallback(
  (id: string) => deleteStudent(id),
  [deleteStudent]
);
```

---

### 2. Debouncing

**useDebounce.ts**:

```typescript
// src/hooks/useDebounce.ts
import { useState, useEffect } from 'react';

export function useDebounce<T>(value: T, delay: number = 300): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}
```

**Usage**:

```typescript
const [searchQuery, setSearchQuery] = useState('');
const debouncedQuery = useDebounce(searchQuery, 300);

useEffect(() => {
  if (debouncedQuery) {
    fetchStudents({ search: debouncedQuery });
  }
}, [debouncedQuery]);
```

---

## Styling Guidelines

### Tailwind Configuration

**tailwind.config.ts**:

```typescript
import type { Config } from 'tailwindcss';

const config: Config = {
  darkMode: ['class'],
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: 'hsl(var(--primary))',
          foreground: 'hsl(var(--primary-foreground))',
        },
        // ... other colors
      },
    },
  },
  plugins: [require('tailwindcss-animate')],
};

export default config;
```

---

## Testing Strategy

### Unit Tests (Vitest)

**studentService.test.ts**:

```typescript
// src/services/__tests__/studentService.test.ts
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { studentService } from '../studentService';
import apiClient from '../api';

vi.mock('../api');

describe('studentService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should fetch all students', async () => {
    const mockResponse = {
      data: {
        students: [{ id: 'STU-2026-00001', firstName: 'John', lastName: 'Doe' }],
        totalCount: 1,
      },
    };

    vi.mocked(apiClient.get).mockResolvedValue(mockResponse);

    const result = await studentService.getAll();

    expect(apiClient.get).toHaveBeenCalledWith('/api/v1/students', { params: undefined });
    expect(result.students).toHaveLength(1);
  });
});
```

---

## Summary

This frontend implementation guide provides:

1. **Service Layer Pattern**: Mandatory centralized API communication
2. **Type Safety**: Comprehensive TypeScript types
3. **Form Handling**: React Hook Form + Zod validation
4. **State Management**: Context API for global, custom hooks for local
5. **Component Structure**: Container/Presentation separation
6. **Error Handling**: Error boundaries and graceful failures
7. **Performance**: Memoization, debouncing, lazy loading
8. **Styling**: Tailwind CSS with Shadcn/ui components
9. **Testing**: Unit and integration test patterns

**Development Workflow**:
1. Define types in `src/types/`
2. Create service methods in `src/services/`
3. Build custom hooks in `src/hooks/`
4. Develop presentation components
5. Assemble container components (pages)
6. Add tests for each layer

---

**Next Steps**: Proceed to `07-testing-strategy.md` for comprehensive QA approach.
