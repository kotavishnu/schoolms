# Frontend Implementation Guide - School Management System

**Version**: 1.0
**Date**: November 10, 2025
**Status**: Approved
**Author**: Frontend Architecture Team

---

## Table of Contents
1. [Overview](#overview)
2. [Project Structure](#project-structure)
3. [State Management](#state-management)
4. [API Integration](#api-integration)
5. [Form Handling & Validation](#form-handling--validation)
6. [Routing & Navigation](#routing--navigation)
7. [Component Guidelines](#component-guidelines)
8. [Authentication & Authorization](#authentication--authorization)
9. [Performance Optimization](#performance-optimization)
10. [Code Examples](#code-examples)
11. [Styling Guidelines](#styling-guidelines)
12. [Testing Strategy](#testing-strategy)

---

## 1. Overview

### 1.1 Technology Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| Framework | React | 18.x | UI library |
| Language | TypeScript | 5.x | Type safety |
| Build Tool | Vite | 5.x | Fast build tool |
| Styling | Tailwind CSS | 3.x | Utility-first CSS |
| UI Components | Shadcn/ui | Latest | Accessible components |
| State Management (Server) | React Query | 5.x | Server state caching |
| State Management (Client) | Zustand | 4.x | Lightweight client state |
| Routing | React Router | 6.x | Client-side routing |
| Forms | React Hook Form | 7.x | Performant forms |
| Validation | Zod | 3.x | Schema validation |
| HTTP Client | Axios | 1.x | API requests |
| Date Handling | date-fns | 3.x | Date manipulation |
| Icons | Lucide React | Latest | Icon library |
| Charts | Recharts | 2.x | Data visualization |

### 1.2 Project Setup

```json
{
  "name": "school-management-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:coverage": "vitest --coverage"
  },
  "dependencies": {
    "react": "^18.3.0",
    "react-dom": "^18.3.0",
    "react-router-dom": "^6.23.0",
    "@tanstack/react-query": "^5.35.0",
    "zustand": "^4.5.0",
    "react-hook-form": "^7.51.0",
    "zod": "^3.23.0",
    "@hookform/resolvers": "^3.3.0",
    "axios": "^1.7.0",
    "date-fns": "^3.6.0",
    "lucide-react": "^0.378.0",
    "recharts": "^2.12.0"
  },
  "devDependencies": {
    "@types/react": "^18.3.0",
    "@types/react-dom": "^18.3.0",
    "@typescript-eslint/eslint-plugin": "^7.8.0",
    "@typescript-eslint/parser": "^7.8.0",
    "@vitejs/plugin-react": "^4.2.0",
    "typescript": "^5.4.0",
    "vite": "^5.2.0",
    "vitest": "^1.5.0",
    "@testing-library/react": "^15.0.0",
    "@testing-library/jest-dom": "^6.4.0",
    "@testing-library/user-event": "^14.5.0",
    "eslint": "^8.57.0",
    "prettier": "^3.2.0",
    "tailwindcss": "^3.4.0",
    "autoprefixer": "^10.4.0",
    "postcss": "^8.4.0"
  }
}
```

---

## 2. Project Structure

### 2.1 Complete Directory Structure

```
src/
├── main.tsx                        (Entry point)
├── App.tsx                         (Root component)
├── vite-env.d.ts
│
├── features/                       (Feature-based organization)
│   ├── auth/
│   │   ├── components/
│   │   │   ├── LoginForm.tsx
│   │   │   └── ProtectedRoute.tsx
│   │   ├── hooks/
│   │   │   ├── useAuth.ts
│   │   │   └── useLogin.ts
│   │   ├── api/
│   │   │   └── authApi.ts
│   │   ├── store/
│   │   │   └── authStore.ts
│   │   └── types/
│   │       └── auth.types.ts
│   │
│   ├── students/
│   │   ├── components/
│   │   │   ├── StudentList.tsx
│   │   │   ├── StudentForm.tsx
│   │   │   ├── StudentDetails.tsx
│   │   │   ├── StudentCard.tsx
│   │   │   └── GuardianForm.tsx
│   │   ├── hooks/
│   │   │   ├── useStudents.ts
│   │   │   ├── useStudent.ts
│   │   │   └── useCreateStudent.ts
│   │   ├── api/
│   │   │   └── studentsApi.ts
│   │   ├── schemas/
│   │   │   └── studentSchema.ts
│   │   └── types/
│   │       └── student.types.ts
│   │
│   ├── classes/
│   │   ├── components/
│   │   │   ├── ClassList.tsx
│   │   │   ├── ClassForm.tsx
│   │   │   ├── ClassDetails.tsx
│   │   │   └── EnrollmentList.tsx
│   │   ├── hooks/
│   │   │   ├── useClasses.ts
│   │   │   └── useEnrollStudent.ts
│   │   ├── api/
│   │   │   └── classesApi.ts
│   │   └── types/
│   │       └── class.types.ts
│   │
│   ├── fees/
│   │   ├── components/
│   │   │   ├── FeeStructureList.tsx
│   │   │   ├── FeeStructureForm.tsx
│   │   │   ├── FeeJournalList.tsx
│   │   │   └── FeeCalculator.tsx
│   │   ├── hooks/
│   │   │   ├── useFeeStructures.ts
│   │   │   └── useCalculateFee.ts
│   │   ├── api/
│   │   │   └── feesApi.ts
│   │   └── types/
│   │       └── fee.types.ts
│   │
│   ├── payments/
│   │   ├── components/
│   │   │   ├── PaymentForm.tsx
│   │   │   ├── PaymentHistory.tsx
│   │   │   └── ReceiptViewer.tsx
│   │   ├── hooks/
│   │   │   ├── useRecordPayment.ts
│   │   │   └── useReceipt.ts
│   │   ├── api/
│   │   │   └── paymentsApi.ts
│   │   └── types/
│   │       └── payment.types.ts
│   │
│   ├── reports/
│   │   ├── components/
│   │   │   ├── EnrollmentReport.tsx
│   │   │   ├── FeeCollectionReport.tsx
│   │   │   ├── OverdueReport.tsx
│   │   │   └── DailyCollectionReport.tsx
│   │   ├── hooks/
│   │   │   └── useReports.ts
│   │   ├── api/
│   │   │   └── reportsApi.ts
│   │   └── types/
│   │       └── report.types.ts
│   │
│   └── dashboard/
│       ├── components/
│       │   ├── Dashboard.tsx
│       │   ├── StatsCard.tsx
│       │   └── RecentActivity.tsx
│       ├── hooks/
│       │   └── useDashboardStats.ts
│       └── api/
│           └── dashboardApi.ts
│
├── shared/                         (Shared/Common code)
│   ├── components/
│   │   ├── ui/                     (Shadcn components)
│   │   │   ├── Button.tsx
│   │   │   ├── Input.tsx
│   │   │   ├── Select.tsx
│   │   │   ├── Table.tsx
│   │   │   ├── Dialog.tsx
│   │   │   ├── Card.tsx
│   │   │   └── Badge.tsx
│   │   ├── layout/
│   │   │   ├── Layout.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   ├── Header.tsx
│   │   │   └── Footer.tsx
│   │   ├── form/
│   │   │   ├── FormField.tsx
│   │   │   ├── FormError.tsx
│   │   │   └── DatePicker.tsx
│   │   └── feedback/
│   │       ├── Loading.tsx
│   │       ├── ErrorMessage.tsx
│   │       ├── EmptyState.tsx
│   │       └── Toast.tsx
│   │
│   ├── hooks/
│   │   ├── useDebounce.ts
│   │   ├── useLocalStorage.ts
│   │   ├── usePagination.ts
│   │   └── usePermissions.ts
│   │
│   ├── utils/
│   │   ├── formatters.ts
│   │   ├── validators.ts
│   │   ├── dateUtils.ts
│   │   └── errorHandler.ts
│   │
│   ├── constants/
│   │   ├── routes.ts
│   │   ├── apiEndpoints.ts
│   │   └── permissions.ts
│   │
│   └── types/
│       ├── common.types.ts
│       └── api.types.ts
│
├── api/                            (API configuration)
│   ├── client.ts                   (Axios instance)
│   ├── queryClient.ts              (React Query config)
│   └── interceptors.ts
│
├── config/
│   └── env.ts                      (Environment variables)
│
├── routes/
│   └── index.tsx                   (Route configuration)
│
└── styles/
    ├── globals.css
    └── tailwind.css
```

---

## 3. State Management

### 3.1 Server State with React Query

**Configuration**:

```typescript
// api/queryClient.ts
import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      cacheTime: 10 * 60 * 1000, // 10 minutes
      refetchOnWindowFocus: false,
      retry: 1,
    },
    mutations: {
      retry: 0,
    },
  },
});

// main.tsx
import { QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';

root.render(
  <QueryClientProvider client={queryClient}>
    <App />
    <ReactQueryDevtools initialIsOpen={false} />
  </QueryClientProvider>
);
```

**Query Hook Example**:

```typescript
// features/students/hooks/useStudents.ts
import { useQuery } from '@tanstack/react-query';
import { studentsApi } from '../api/studentsApi';
import { StudentFilters } from '../types/student.types';

export const useStudents = (filters: StudentFilters) => {
  return useQuery({
    queryKey: ['students', filters],
    queryFn: () => studentsApi.getStudents(filters),
    select: (data) => data.content,
  });
};

// Usage in component
const StudentList = () => {
  const [filters, setFilters] = useState<StudentFilters>({ status: 'ACTIVE' });
  const { data: students, isLoading, error } = useStudents(filters);

  if (isLoading) return <Loading />;
  if (error) return <ErrorMessage error={error} />;

  return (
    <div>
      {students.map(student => (
        <StudentCard key={student.studentId} student={student} />
      ))}
    </div>
  );
};
```

**Mutation Hook Example**:

```typescript
// features/students/hooks/useCreateStudent.ts
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { studentsApi } from '../api/studentsApi';
import { CreateStudentRequest } from '../types/student.types';
import { toast } from '@/shared/components/ui/toast';

export const useCreateStudent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateStudentRequest) => studentsApi.createStudent(data),
    onSuccess: (newStudent) => {
      // Invalidate and refetch students list
      queryClient.invalidateQueries({ queryKey: ['students'] });

      toast.success('Student registered successfully');
    },
    onError: (error) => {
      toast.error('Failed to register student');
    },
  });
};

// Usage
const StudentForm = () => {
  const { mutate, isPending } = useCreateStudent();

  const onSubmit = (data: CreateStudentRequest) => {
    mutate(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      {/* Form fields */}
      <Button type="submit" disabled={isPending}>
        {isPending ? 'Saving...' : 'Save Student'}
      </Button>
    </form>
  );
};
```

### 3.2 Client State with Zustand

```typescript
// features/auth/store/authStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { User } from '../types/auth.types';

interface AuthState {
  user: User | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  login: (user: User, accessToken: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      accessToken: null,
      isAuthenticated: false,
      login: (user, accessToken) =>
        set({ user, accessToken, isAuthenticated: true }),
      logout: () =>
        set({ user: null, accessToken: null, isAuthenticated: false }),
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ user: state.user }), // Only persist user
    }
  )
);

// Usage
const Header = () => {
  const { user, logout } = useAuthStore();

  return (
    <header>
      <span>Welcome, {user?.fullName}</span>
      <Button onClick={logout}>Logout</Button>
    </header>
  );
};
```

---

## 4. API Integration

### 4.1 Axios Configuration

```typescript
// api/client.ts
import axios, { AxiosError, AxiosResponse } from 'axios';
import { useAuthStore } from '@/features/auth/store/authStore';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000,
});

// Request interceptor - Add JWT token
apiClient.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().accessToken;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - Handle errors
apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Unauthorized - logout user
      useAuthStore.getState().logout();
      window.location.href = '/login';
    }

    if (error.response?.status === 403) {
      // Forbidden - show permission error
      toast.error('You do not have permission to perform this action');
    }

    return Promise.reject(error);
  }
);
```

### 4.2 API Layer Example

```typescript
// features/students/api/studentsApi.ts
import { apiClient } from '@/api/client';
import {
  Student,
  StudentResponse,
  CreateStudentRequest,
  UpdateStudentRequest,
  StudentFilters,
  PageResponse,
} from '../types/student.types';

export const studentsApi = {
  // Get all students with pagination and filters
  getStudents: async (filters: StudentFilters): Promise<PageResponse<Student>> => {
    const { data } = await apiClient.get('/students', { params: filters });
    return data;
  },

  // Get single student by ID
  getStudent: async (studentId: number): Promise<StudentResponse> => {
    const { data } = await apiClient.get(`/students/${studentId}`);
    return data;
  },

  // Create new student
  createStudent: async (request: CreateStudentRequest): Promise<StudentResponse> => {
    const { data } = await apiClient.post('/students', request);
    return data;
  },

  // Update student
  updateStudent: async (
    studentId: number,
    request: UpdateStudentRequest
  ): Promise<StudentResponse> => {
    const { data } = await apiClient.put(`/students/${studentId}`, request);
    return data;
  },

  // Change student status
  changeStatus: async (
    studentId: number,
    status: string,
    reason?: string
  ): Promise<void> => {
    await apiClient.patch(`/students/${studentId}/status`, { status, reason });
  },

  // Get student enrollments
  getEnrollments: async (studentId: number) => {
    const { data } = await apiClient.get(`/students/${studentId}/enrollments`);
    return data;
  },
};
```

---

## 5. Form Handling & Validation

### 5.1 Zod Schema

```typescript
// features/students/schemas/studentSchema.ts
import { z } from 'zod';

export const guardianSchema = z.object({
  relationship: z.enum(['FATHER', 'MOTHER', 'GUARDIAN', 'OTHER']),
  firstName: z.string().min(2, 'First name must be at least 2 characters'),
  lastName: z.string().min(2, 'Last name must be at least 2 characters'),
  mobile: z.string().regex(/^\d{10}$/, 'Mobile must be exactly 10 digits'),
  email: z.string().email('Invalid email format').optional().or(z.literal('')),
  occupation: z.string().optional(),
  isPrimary: z.boolean().default(false),
});

export const createStudentSchema = z.object({
  firstName: z.string().min(2).max(50),
  lastName: z.string().min(2).max(50),
  dateOfBirth: z.date().refine(
    (date) => {
      const age = new Date().getFullYear() - date.getFullYear();
      return age >= 3 && age <= 18;
    },
    { message: 'Student age must be between 3 and 18' }
  ),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER']),
  mobile: z.string().regex(/^\d{10}$/, 'Mobile must be exactly 10 digits'),
  email: z.string().email().optional().or(z.literal('')),
  address: z.string().min(10, 'Address must be at least 10 characters'),
  bloodGroup: z.enum(['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-']).optional(),
  admissionDate: z.date(),
  guardians: z.array(guardianSchema).min(1, 'At least one guardian is required'),
});

export type CreateStudentFormData = z.infer<typeof createStudentSchema>;
```

### 5.2 React Hook Form with Zod

```typescript
// features/students/components/StudentForm.tsx
import { useForm, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { createStudentSchema, CreateStudentFormData } from '../schemas/studentSchema';
import { useCreateStudent } from '../hooks/useCreateStudent';

const StudentForm = () => {
  const { mutate, isPending } = useCreateStudent();

  const {
    register,
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<CreateStudentFormData>({
    resolver: zodResolver(createStudentSchema),
    defaultValues: {
      guardians: [{ isPrimary: true }],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: 'guardians',
  });

  const onSubmit = (data: CreateStudentFormData) => {
    mutate(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Student Information */}
      <Card>
        <CardHeader>
          <CardTitle>Student Information</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <FormField
              label="First Name"
              error={errors.firstName?.message}
              required
            >
              <Input {...register('firstName')} placeholder="John" />
            </FormField>

            <FormField
              label="Last Name"
              error={errors.lastName?.message}
              required
            >
              <Input {...register('lastName')} placeholder="Doe" />
            </FormField>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <FormField
              label="Date of Birth"
              error={errors.dateOfBirth?.message}
              required
            >
              <DatePicker
                control={control}
                name="dateOfBirth"
                placeholder="Select date"
              />
            </FormField>

            <FormField
              label="Gender"
              error={errors.gender?.message}
              required
            >
              <Select {...register('gender')}>
                <option value="">Select gender</option>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </Select>
            </FormField>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <FormField
              label="Mobile"
              error={errors.mobile?.message}
              required
            >
              <Input
                {...register('mobile')}
                placeholder="9876543210"
                maxLength={10}
              />
            </FormField>

            <FormField label="Email" error={errors.email?.message}>
              <Input
                {...register('email')}
                type="email"
                placeholder="john.doe@example.com"
              />
            </FormField>
          </div>

          <FormField
            label="Address"
            error={errors.address?.message}
            required
          >
            <Textarea
              {...register('address')}
              placeholder="Enter full address"
              rows={3}
            />
          </FormField>
        </CardContent>
      </Card>

      {/* Guardian Information */}
      <Card>
        <CardHeader>
          <CardTitle>Guardian Information</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {fields.map((field, index) => (
            <div key={field.id} className="p-4 border rounded-lg space-y-4">
              <div className="flex justify-between items-center">
                <h4 className="font-medium">Guardian {index + 1}</h4>
                {index > 0 && (
                  <Button
                    type="button"
                    variant="destructive"
                    size="sm"
                    onClick={() => remove(index)}
                  >
                    Remove
                  </Button>
                )}
              </div>

              <div className="grid grid-cols-2 gap-4">
                <FormField
                  label="Relationship"
                  error={errors.guardians?.[index]?.relationship?.message}
                  required
                >
                  <Select {...register(`guardians.${index}.relationship`)}>
                    <option value="">Select relationship</option>
                    <option value="FATHER">Father</option>
                    <option value="MOTHER">Mother</option>
                    <option value="GUARDIAN">Guardian</option>
                    <option value="OTHER">Other</option>
                  </Select>
                </FormField>

                <FormField
                  label="First Name"
                  error={errors.guardians?.[index]?.firstName?.message}
                  required
                >
                  <Input {...register(`guardians.${index}.firstName`)} />
                </FormField>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <FormField
                  label="Mobile"
                  error={errors.guardians?.[index]?.mobile?.message}
                  required
                >
                  <Input
                    {...register(`guardians.${index}.mobile`)}
                    maxLength={10}
                  />
                </FormField>

                <FormField
                  label="Email"
                  error={errors.guardians?.[index]?.email?.message}
                >
                  <Input
                    {...register(`guardians.${index}.email`)}
                    type="email"
                  />
                </FormField>
              </div>

              <FormField label="Primary Contact">
                <Checkbox {...register(`guardians.${index}.isPrimary`)} />
              </FormField>
            </div>
          ))}

          <Button
            type="button"
            variant="outline"
            onClick={() =>
              append({ isPrimary: false } as any)
            }
          >
            Add Guardian
          </Button>
        </CardContent>
      </Card>

      {/* Submit Button */}
      <div className="flex justify-end gap-4">
        <Button type="button" variant="outline">
          Cancel
        </Button>
        <Button type="submit" disabled={isPending}>
          {isPending ? 'Saving...' : 'Register Student'}
        </Button>
      </div>
    </form>
  );
};
```

---

## 6. Routing & Navigation

### 6.1 Route Configuration

```typescript
// routes/index.tsx
import { createBrowserRouter } from 'react-router-dom';
import { ProtectedRoute } from '@/features/auth/components/ProtectedRoute';
import { Layout } from '@/shared/components/layout/Layout';

// Lazy load routes
const Dashboard = lazy(() => import('@/features/dashboard/components/Dashboard'));
const StudentList = lazy(() => import('@/features/students/components/StudentList'));
const StudentForm = lazy(() => import('@/features/students/components/StudentForm'));
const StudentDetails = lazy(() => import('@/features/students/components/StudentDetails'));
const ClassList = lazy(() => import('@/features/classes/components/ClassList'));
const FeeStructureList = lazy(() => import('@/features/fees/components/FeeStructureList'));
const PaymentForm = lazy(() => import('@/features/payments/components/PaymentForm'));
const Reports = lazy(() => import('@/features/reports/components/Reports'));
const Login = lazy(() => import('@/features/auth/components/LoginForm'));

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <Layout />
      </ProtectedRoute>
    ),
    children: [
      {
        index: true,
        element: <Dashboard />,
      },
      {
        path: 'students',
        children: [
          { index: true, element: <StudentList /> },
          { path: 'new', element: <StudentForm /> },
          { path: ':studentId', element: <StudentDetails /> },
          { path: ':studentId/edit', element: <StudentForm /> },
        ],
      },
      {
        path: 'classes',
        children: [
          { index: true, element: <ClassList /> },
        ],
      },
      {
        path: 'fees',
        element: <FeeStructureList />,
      },
      {
        path: 'payments',
        element: <PaymentForm />,
      },
      {
        path: 'reports',
        element: <Reports />,
      },
    ],
  },
]);

// main.tsx
import { RouterProvider } from 'react-router-dom';

root.render(
  <QueryClientProvider client={queryClient}>
    <RouterProvider router={router} />
  </QueryClientProvider>
);
```

### 6.2 Protected Route

```typescript
// features/auth/components/ProtectedRoute.tsx
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredPermission?: string;
}

export const ProtectedRoute = ({ children, requiredPermission }: ProtectedRouteProps) => {
  const { isAuthenticated, user } = useAuthStore();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requiredPermission && !user?.permissions.includes(requiredPermission)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <>{children}</>;
};

// Usage
<Route
  path="/students/new"
  element={
    <ProtectedRoute requiredPermission="student:write">
      <StudentForm />
    </ProtectedRoute>
  }
/>
```

---

## 7. Component Guidelines

### 7.1 Component Structure

```typescript
// features/students/components/StudentCard.tsx
import { Student } from '../types/student.types';
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { useNavigate } from 'react-router-dom';

interface StudentCardProps {
  student: Student;
  onEdit?: (student: Student) => void;
  onDelete?: (studentId: number) => void;
}

export const StudentCard = ({ student, onEdit, onDelete }: StudentCardProps) => {
  const navigate = useNavigate();

  const handleViewDetails = () => {
    navigate(`/students/${student.studentId}`);
  };

  const getStatusColor = (status: string) => {
    const colors = {
      ACTIVE: 'bg-green-500',
      INACTIVE: 'bg-gray-500',
      GRADUATED: 'bg-blue-500',
      WITHDRAWN: 'bg-red-500',
    };
    return colors[status as keyof typeof colors] || 'bg-gray-500';
  };

  return (
    <Card className="hover:shadow-lg transition-shadow">
      <CardHeader>
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="text-lg">
              {student.firstName} {student.lastName}
            </CardTitle>
            <p className="text-sm text-gray-500">{student.studentCode}</p>
          </div>
          <Badge className={getStatusColor(student.status)}>
            {student.status}
          </Badge>
        </div>
      </CardHeader>

      <CardContent>
        <div className="space-y-2">
          <div className="flex justify-between text-sm">
            <span className="text-gray-600">Class:</span>
            <span className="font-medium">{student.currentClass}</span>
          </div>
          <div className="flex justify-between text-sm">
            <span className="text-gray-600">Mobile:</span>
            <span className="font-medium">{student.mobile}</span>
          </div>
          <div className="flex justify-between text-sm">
            <span className="text-gray-600">Admission Date:</span>
            <span className="font-medium">
              {formatDate(student.admissionDate)}
            </span>
          </div>
        </div>

        <div className="flex gap-2 mt-4">
          <Button size="sm" onClick={handleViewDetails}>
            View Details
          </Button>
          {onEdit && (
            <Button size="sm" variant="outline" onClick={() => onEdit(student)}>
              Edit
            </Button>
          )}
          {onDelete && (
            <Button
              size="sm"
              variant="destructive"
              onClick={() => onDelete(student.studentId)}
            >
              Delete
            </Button>
          )}
        </div>
      </CardContent>
    </Card>
  );
};
```

### 7.2 Reusable Table Component

```typescript
// shared/components/ui/DataTable.tsx
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/shared/components/ui/Table';
import { Button } from '@/shared/components/ui/Button';

interface Column<T> {
  key: string;
  header: string;
  render?: (item: T) => React.ReactNode;
}

interface DataTableProps<T> {
  data: T[];
  columns: Column<T>[];
  onRowClick?: (item: T) => void;
  isLoading?: boolean;
  emptyMessage?: string;
}

export function DataTable<T extends { id: string | number }>({
  data,
  columns,
  onRowClick,
  isLoading,
  emptyMessage = 'No data available',
}: DataTableProps<T>) {
  if (isLoading) {
    return <Loading />;
  }

  if (data.length === 0) {
    return <EmptyState message={emptyMessage} />;
  }

  return (
    <Table>
      <TableHeader>
        <TableRow>
          {columns.map((column) => (
            <TableHead key={column.key}>{column.header}</TableHead>
          ))}
        </TableRow>
      </TableHeader>
      <TableBody>
        {data.map((item) => (
          <TableRow
            key={item.id}
            onClick={() => onRowClick?.(item)}
            className={onRowClick ? 'cursor-pointer hover:bg-gray-50' : ''}
          >
            {columns.map((column) => (
              <TableCell key={column.key}>
                {column.render
                  ? column.render(item)
                  : (item as any)[column.key]}
              </TableCell>
            ))}
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}

// Usage
const StudentList = () => {
  const { data: students } = useStudents({});

  const columns: Column<Student>[] = [
    { key: 'studentCode', header: 'Student Code' },
    {
      key: 'name',
      header: 'Name',
      render: (student) => `${student.firstName} ${student.lastName}`,
    },
    { key: 'mobile', header: 'Mobile' },
    { key: 'currentClass', header: 'Class' },
    {
      key: 'status',
      header: 'Status',
      render: (student) => <Badge>{student.status}</Badge>,
    },
  ];

  return (
    <DataTable
      data={students}
      columns={columns}
      onRowClick={(student) => navigate(`/students/${student.studentId}`)}
    />
  );
};
```

---

## 8. Authentication & Authorization

### 8.1 Login Flow

```typescript
// features/auth/hooks/useLogin.ts
import { useMutation } from '@tanstack/react-query';
import { authApi } from '../api/authApi';
import { useAuthStore } from '../store/authStore';
import { useNavigate } from 'react-router-dom';
import { toast } from '@/shared/components/ui/toast';

export const useLogin = () => {
  const login = useAuthStore((state) => state.login);
  const navigate = useNavigate();

  return useMutation({
    mutationFn: authApi.login,
    onSuccess: (data) => {
      login(data.user, data.accessToken);
      navigate('/');
      toast.success('Login successful');
    },
    onError: (error: any) => {
      if (error.response?.status === 401) {
        toast.error('Invalid credentials');
      } else {
        toast.error('Login failed. Please try again.');
      }
    },
  });
};

// features/auth/components/LoginForm.tsx
const LoginForm = () => {
  const { mutate, isPending } = useLogin();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = (data: LoginFormData) => {
    mutate(data);
  };

  return (
    <div className="flex min-h-screen items-center justify-center">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>Login to School Management System</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <FormField label="Username" error={errors.username?.message} required>
              <Input {...register('username')} placeholder="Enter username" />
            </FormField>

            <FormField label="Password" error={errors.password?.message} required>
              <Input
                {...register('password')}
                type="password"
                placeholder="Enter password"
              />
            </FormField>

            <Button type="submit" className="w-full" disabled={isPending}>
              {isPending ? 'Logging in...' : 'Login'}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};
```

### 8.2 Permission-Based UI

```typescript
// shared/hooks/usePermissions.ts
import { useAuthStore } from '@/features/auth/store/authStore';

export const usePermissions = () => {
  const user = useAuthStore((state) => state.user);

  const hasPermission = (permission: string) => {
    return user?.permissions.includes(permission) || false;
  };

  const hasAnyPermission = (permissions: string[]) => {
    return permissions.some((permission) => hasPermission(permission));
  };

  const hasAllPermissions = (permissions: string[]) => {
    return permissions.every((permission) => hasPermission(permission));
  };

  return { hasPermission, hasAnyPermission, hasAllPermissions };
};

// Usage in component
const StudentActions = ({ student }: { student: Student }) => {
  const { hasPermission } = usePermissions();

  return (
    <div className="flex gap-2">
      {hasPermission('student:read') && (
        <Button onClick={() => navigate(`/students/${student.studentId}`)}>
          View
        </Button>
      )}
      {hasPermission('student:write') && (
        <Button onClick={() => navigate(`/students/${student.studentId}/edit`)}>
          Edit
        </Button>
      )}
      {hasPermission('student:delete') && (
        <Button variant="destructive" onClick={() => handleDelete(student.studentId)}>
          Delete
        </Button>
      )}
    </div>
  );
};
```

---

## 9. Performance Optimization

### 9.1 Code Splitting & Lazy Loading

```typescript
// Already shown in routing section
import { lazy, Suspense } from 'react';

const StudentList = lazy(() => import('@/features/students/components/StudentList'));

// Usage
<Suspense fallback={<Loading />}>
  <StudentList />
</Suspense>
```

### 9.2 Memoization

```typescript
import { memo, useMemo, useCallback } from 'react';

// Memo component
export const StudentCard = memo(({ student }: { student: Student }) => {
  // Component logic
});

// useMemo for expensive calculations
const StudentList = () => {
  const { data: students } = useStudents({});

  const filteredStudents = useMemo(() => {
    return students.filter((s) => s.status === 'ACTIVE');
  }, [students]);

  return <div>{/* Render filtered students */}</div>;
};

// useCallback for functions passed as props
const StudentList = () => {
  const handleEdit = useCallback((student: Student) => {
    navigate(`/students/${student.studentId}/edit`);
  }, [navigate]);

  return (
    <div>
      {students.map((student) => (
        <StudentCard key={student.studentId} student={student} onEdit={handleEdit} />
      ))}
    </div>
  );
};
```

### 9.3 Virtual Scrolling for Large Lists

```typescript
import { useVirtualizer } from '@tanstack/react-virtual';

const VirtualStudentList = ({ students }: { students: Student[] }) => {
  const parentRef = useRef<HTMLDivElement>(null);

  const rowVirtualizer = useVirtualizer({
    count: students.length,
    getScrollElement: () => parentRef.current,
    estimateSize: () => 100,
    overscan: 5,
  });

  return (
    <div ref={parentRef} className="h-[600px] overflow-auto">
      <div
        style={{
          height: `${rowVirtualizer.getTotalSize()}px`,
          width: '100%',
          position: 'relative',
        }}
      >
        {rowVirtualizer.getVirtualItems().map((virtualRow) => (
          <div
            key={virtualRow.key}
            style={{
              position: 'absolute',
              top: 0,
              left: 0,
              width: '100%',
              height: `${virtualRow.size}px`,
              transform: `translateY(${virtualRow.start}px)`,
            }}
          >
            <StudentCard student={students[virtualRow.index]} />
          </div>
        ))}
      </div>
    </div>
  );
};
```

---

## 10. Code Examples

### 10.1 Complete Feature Example: Payment Recording

```typescript
// types/payment.types.ts
export interface RecordPaymentRequest {
  studentId: number;
  paymentAmount: number;
  paymentMethod: 'CASH' | 'CARD' | 'UPI' | 'NET_BANKING';
  paymentDate: string;
  transactionReference?: string;
  remarks?: string;
  journalAllocations: JournalAllocation[];
}

export interface JournalAllocation {
  journalId: number;
  allocationAmount: number;
}

// schemas/paymentSchema.ts
export const paymentSchema = z.object({
  studentId: z.number().positive(),
  paymentAmount: z.number().positive(),
  paymentMethod: z.enum(['CASH', 'CARD', 'UPI', 'NET_BANKING']),
  paymentDate: z.date(),
  transactionReference: z.string().optional(),
  remarks: z.string().optional(),
  journalAllocations: z.array(
    z.object({
      journalId: z.number(),
      allocationAmount: z.number().positive(),
    })
  ).min(1),
}).refine(
  (data) => {
    const total = data.journalAllocations.reduce(
      (sum, allocation) => sum + allocation.allocationAmount,
      0
    );
    return total === data.paymentAmount;
  },
  {
    message: 'Total allocation must equal payment amount',
    path: ['journalAllocations'],
  }
);

// components/PaymentForm.tsx
const PaymentForm = () => {
  const [selectedStudent, setSelectedStudent] = useState<Student | null>(null);
  const { data: journals } = useFeeJournals(selectedStudent?.studentId);
  const { mutate, isPending } = useRecordPayment();

  const { register, handleSubmit, control, watch, formState: { errors } } =
    useForm<RecordPaymentFormData>({
      resolver: zodResolver(paymentSchema),
    });

  const { fields, append } = useFieldArray({
    control,
    name: 'journalAllocations',
  });

  const onSubmit = (data: RecordPaymentFormData) => {
    mutate(data, {
      onSuccess: () => {
        toast.success('Payment recorded successfully');
        // Reset form or navigate
      },
    });
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>Record Payment</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {/* Student Selection */}
          <FormField label="Select Student" required>
            <StudentSearchCombobox
              value={selectedStudent}
              onChange={setSelectedStudent}
            />
          </FormField>

          {selectedStudent && (
            <>
              {/* Payment Details */}
              <div className="grid grid-cols-2 gap-4">
                <FormField label="Payment Amount" error={errors.paymentAmount?.message} required>
                  <Input
                    {...register('paymentAmount', { valueAsNumber: true })}
                    type="number"
                    step="0.01"
                  />
                </FormField>

                <FormField label="Payment Method" error={errors.paymentMethod?.message} required>
                  <Select {...register('paymentMethod')}>
                    <option value="">Select method</option>
                    <option value="CASH">Cash</option>
                    <option value="CARD">Card</option>
                    <option value="UPI">UPI</option>
                    <option value="NET_BANKING">Net Banking</option>
                  </Select>
                </FormField>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <FormField label="Payment Date" required>
                  <DatePicker control={control} name="paymentDate" />
                </FormField>

                <FormField label="Transaction Reference">
                  <Input {...register('transactionReference')} />
                </FormField>
              </div>

              {/* Fee Allocation */}
              <div>
                <h4 className="font-medium mb-2">Allocate to Fee Journals</h4>
                <div className="space-y-2">
                  {journals?.map((journal, index) => (
                    <div key={journal.journalId} className="flex items-center gap-4 p-3 border rounded">
                      <Checkbox
                        {...register(`journalAllocations.${index}.selected`)}
                        onChange={(e) => {
                          if (e.target.checked) {
                            append({
                              journalId: journal.journalId,
                              allocationAmount: journal.balanceAmount,
                            });
                          }
                        }}
                      />
                      <div className="flex-1">
                        <p className="font-medium">{journal.feeName}</p>
                        <p className="text-sm text-gray-600">
                          {journal.feeMonth} - Balance: ₹{journal.balanceAmount}
                        </p>
                      </div>
                      <Input
                        {...register(`journalAllocations.${index}.allocationAmount`)}
                        type="number"
                        step="0.01"
                        placeholder="Amount"
                        className="w-32"
                      />
                    </div>
                  ))}
                </div>
                {errors.journalAllocations && (
                  <p className="text-sm text-red-500 mt-2">
                    {errors.journalAllocations.message}
                  </p>
                )}
              </div>

              <FormField label="Remarks">
                <Textarea {...register('remarks')} rows={3} />
              </FormField>
            </>
          )}
        </CardContent>
      </Card>

      <div className="flex justify-end gap-4">
        <Button type="button" variant="outline">
          Cancel
        </Button>
        <Button type="submit" disabled={isPending || !selectedStudent}>
          {isPending ? 'Recording...' : 'Record Payment'}
        </Button>
      </div>
    </form>
  );
};
```

---

## 11. Styling Guidelines

### 11.1 Tailwind Configuration

```javascript
// tailwind.config.js
/** @type {import('tailwindcss').Config} */
export default {
  darkMode: ['class'],
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#3b82f6',
          foreground: '#ffffff',
        },
        secondary: {
          DEFAULT: '#6b7280',
          foreground: '#ffffff',
        },
        success: '#10b981',
        error: '#ef4444',
        warning: '#f59e0b',
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      },
    },
  },
  plugins: [require('@tailwindcss/forms')],
};
```

### 11.2 Utility Classes

```typescript
// shared/utils/classNames.ts
export function cn(...inputs: (string | undefined | null | false)[]) {
  return inputs.filter(Boolean).join(' ');
}

// Usage
<div className={cn(
  'px-4 py-2 rounded',
  isActive && 'bg-blue-500 text-white',
  isDisabled && 'opacity-50 cursor-not-allowed'
)}>
  Content
</div>
```

---

## 12. Testing Strategy

### 12.1 Component Testing

```typescript
// features/students/components/__tests__/StudentCard.test.tsx
import { render, screen, fireEvent } from '@testing-library/react';
import { StudentCard } from '../StudentCard';

describe('StudentCard', () => {
  const mockStudent = {
    studentId: 1,
    studentCode: 'STU-2025-00001',
    firstName: 'John',
    lastName: 'Doe',
    mobile: '9876543210',
    status: 'ACTIVE',
    currentClass: '5-A',
    admissionDate: '2025-04-01',
  };

  it('renders student information correctly', () => {
    render(<StudentCard student={mockStudent} />);

    expect(screen.getByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('STU-2025-00001')).toBeInTheDocument();
    expect(screen.getByText('ACTIVE')).toBeInTheDocument();
  });

  it('calls onEdit when edit button is clicked', () => {
    const mockOnEdit = vi.fn();
    render(<StudentCard student={mockStudent} onEdit={mockOnEdit} />);

    fireEvent.click(screen.getByText('Edit'));
    expect(mockOnEdit).toHaveBeenCalledWith(mockStudent);
  });
});
```

### 12.2 Hook Testing

```typescript
// features/students/hooks/__tests__/useStudents.test.ts
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClientProvider } from '@tanstack/react-query';
import { useStudents } from '../useStudents';
import { queryClient } from '@/api/queryClient';

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
);

describe('useStudents', () => {
  it('fetches students successfully', async () => {
    const { result } = renderHook(() => useStudents({ status: 'ACTIVE' }), {
      wrapper,
    });

    await waitFor(() => expect(result.current.isSuccess).toBe(true));

    expect(result.current.data).toBeDefined();
    expect(Array.isArray(result.current.data)).toBe(true);
  });
});
```

---

## Document Control

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-11-10 | Frontend Team | Initial version |

