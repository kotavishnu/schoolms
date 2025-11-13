# Frontend Development Tasks - School Management System

**Version**: 1.0
**Date**: November 11, 2025
**Target Audience**: Frontend Development Team

---

## Task Naming Convention

Tasks follow this format: `FE-S[Sprint]-[TaskNumber]` (e.g., FE-S1-01)

Where:
- **S** = Sprint number
- Task number increments within each sprint
- Story points use Fibonacci scale: 1, 2, 3, 5, 8, 13

---

## Sprint 1-2: Frontend Foundation & Setup
**Duration**: Nov 11 - Dec 9, 2025
**Sprint Goals**: Environment setup, project structure, component library foundation
**Team Capacity**: 35 story points total

---

### FE-S1-01: Set Up React + TypeScript Project

**Story Points**: 3
**Assigned To**: Frontend Team Lead + 1 Engineer
**Description**: Initialize React 18 project with TypeScript, Vite build tool, and development environment.

**Acceptance Criteria**:
- [ ] React 18 project created with Vite
- [ ] TypeScript 5.x configured with strict mode enabled
- [ ] tsconfig.json configured with proper paths and strict settings
- [ ] .env and environment configuration set up (dev, test, prod)
- [ ] ESLint configured with React and TypeScript plugins
- [ ] Prettier configured for code formatting
- [ ] Husky and lint-staged configured for pre-commit hooks
- [ ] Vitest configured for unit testing
- [ ] React Testing Library configured for component testing
- [ ] Vite configuration for development and production builds
- [ ] Package.json scripts for dev, build, test, lint, format
- [ ] Project structure established
- [ ] README with setup instructions

**Technical Requirements**:
```bash
# Create Vite React project
npm create vite@latest school-sms-ui -- --template react-ts

# Install essential dependencies
npm install axios react-query zustand react-router-dom
npm install -D typescript @types/react @types/node
npm install -D eslint prettier eslint-config-prettier
npm install -D @testing-library/react vitest
```

**Project Structure**:
```
src/
├── api/                 # API client and hooks
│   ├── client.ts
│   ├── hooks/
│   │   ├── useStudents.ts
│   │   ├── useClasses.ts
│   │   └── ...
│   └── interceptors.ts
├── components/          # Reusable components
│   ├── common/         # Button, Input, Modal, etc.
│   ├── layout/         # Header, Sidebar, Layout
│   └── features/       # Feature-specific components
├── pages/              # Page components
│   ├── student/
│   ├── class/
│   └── ...
├── store/              # Zustand state management
│   ├── auth.ts
│   ├── app.ts
│   └── ...
├── types/              # TypeScript interfaces and types
│   ├── student.ts
│   ├── class.ts
│   └── ...
├── utils/              # Utility functions
│   ├── validation.ts
│   ├── format.ts
│   └── ...
├── styles/             # Global styles
├── App.tsx
└── main.tsx
```

**Implementation Guidance**:
1. Use Vite for faster development and building
2. Configure TypeScript strict mode for type safety
3. Set up ESLint to catch common issues early
4. Configure Prettier for consistent code formatting
5. Use environment variables for API endpoints
6. Set up Vitest for fast unit testing
7. Configure import aliases for cleaner imports

**Definition of Done**:
- [ ] Project builds successfully: `npm run build`
- [ ] Development server runs: `npm run dev`
- [ ] TypeScript compilation successful
- [ ] ESLint passes all checks
- [ ] Unit test framework functional
- [ ] Project structure documented

**Dependencies**: None

---

### FE-S1-02: Install & Configure Dependencies

**Story Points**: 2
**Assigned To**: 1 Frontend Engineer
**Description**: Install and configure all required npm packages for development.

**Acceptance Criteria**:
- [ ] React 18.x installed and working
- [ ] TypeScript 5.x configured
- [ ] Tailwind CSS 3.x installed and configured
- [ ] React Router 6.x installed for routing
- [ ] Axios configured as HTTP client
- [ ] React Query 5.x configured for server state
- [ ] Zustand 4.x configured for client state
- [ ] React Hook Form 7.x configured for forms
- [ ] Zod 3.x configured for schema validation
- [ ] Shadcn/ui components configured
- [ ] date-fns 3.x for date manipulation
- [ ] TanStack Table for data tables
- [ ] React Hot Toast for notifications
- [ ] package-lock.json generated and committed
- [ ] Dependency audit passing (no critical vulnerabilities)

**Technical Requirements**:
```bash
# Core dependencies
npm install react react-dom react-router-dom

# HTTP & Data Management
npm install axios @tanstack/react-query zustand

# Forms & Validation
npm install react-hook-form zod @hookform/resolvers

# UI & Styling
npm install -D tailwindcss postcss autoprefixer
npm install shadcn-ui radix-ui lucide-react
npm install react-hot-toast

# Utilities
npm install date-fns clsx tailwind-merge
npm install @tanstack/react-table

# Dev Dependencies
npm install -D typescript @types/react @types/react-dom
npm install -D eslint @typescript-eslint/eslint-plugin
npm install -D prettier tailwindcss
npm install -D vitest @vitest/ui
npm install -D @testing-library/react @testing-library/jest-dom
```

**Tailwind Configuration**:
```javascript
// tailwind.config.js
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: "#1f2937",
        secondary: "#6366f1",
      },
    },
  },
  plugins: [require("@tailwindcss/forms")],
}
```

**Implementation Guidance**:
1. Use exact versions in package.json for consistency
2. Document all dependencies and their purpose
3. Keep dev and production dependencies separate
4. Run security audits regularly
5. Pin major versions, allow minor/patch updates
6. Document why each dependency is needed

**Definition of Done**:
- [ ] All dependencies installed
- [ ] No console warnings on startup
- [ ] Dependency audit passing
- [ ] package.json and package-lock.json committed
- [ ] Documentation for dependency versions

**Dependencies**: FE-S1-01

---

### FE-S1-03: Set Up API Client & Interceptors

**Story Points**: 3
**Assigned To**: 1 Frontend Engineer
**Description**: Create Axios-based API client with interceptors for authentication, error handling, and request/response transformation.

**Acceptance Criteria**:
- [ ] Axios instance configured with base URL
- [ ] Authentication interceptor for JWT tokens
- [ ] Error handling interceptor with proper error responses
- [ ] Request/response logging for debugging
- [ ] Token refresh logic on 401 responses
- [ ] Rate limit handling
- [ ] Request timeout configuration
- [ ] CORS handling
- [ ] Loading state management for requests
- [ ] Request cancellation support
- [ ] Response transformation (data mapping)
- [ ] Unit tests for interceptors

**Technical Requirements**:
```typescript
// api/client.ts
import axios, { AxiosInstance, AxiosError } from 'axios';
import { useAuthStore } from '../store/auth';

const client: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor
client.interceptors.request.use(
  (config) => {
    const { token } = useAuthStore();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor
client.interceptors.response.use(
  (response) => {
    // Handle success response
    return response.data;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config;

    // Handle 401 - Token Expired
    if (error.response?.status === 401) {
      const { refreshToken, refresh, logout } = useAuthStore();

      if (refreshToken && originalRequest) {
        try {
          await refresh();
          // Retry original request
          return client(originalRequest);
        } catch (refreshError) {
          logout();
          return Promise.reject(refreshError);
        }
      }
    }

    // Handle other errors
    const errorMessage = error.response?.data?.detail ||
      error.message || 'An error occurred';

    return Promise.reject({
      status: error.response?.status,
      message: errorMessage,
      data: error.response?.data,
    });
  }
);

export default client;
```

**Hooks for API Calls**:
```typescript
// api/hooks/useApi.ts
import { useQuery, useMutation, UseQueryOptions } from '@tanstack/react-query';
import client from '../client';

export const useApiGet = <T,>(
  key: string,
  url: string,
  options?: UseQueryOptions<T>
) => {
  return useQuery<T>({
    queryKey: [key],
    queryFn: () => client.get(url),
    ...options,
  });
};

export const useApiPost = <T, D>(
  url: string,
  options?: any
) => {
  return useMutation<T, Error, D>({
    mutationFn: (data: D) => client.post(url, data),
    ...options,
  });
};

export const useApiPut = <T, D>(
  url: string,
  options?: any
) => {
  return useMutation<T, Error, D>({
    mutationFn: (data: D) => client.put(url, data),
    ...options,
  });
};
```

**Implementation Guidance**:
1. Implement proper error boundaries
2. Use React Query for cache management
3. Implement retry logic for transient errors
4. Add request debouncing for search queries
5. Implement optimistic updates
6. Log all API interactions for debugging
7. Handle network error scenarios

**Definition of Done**:
- [ ] API client working with test backend
- [ ] Interceptors tested
- [ ] Token refresh working
- [ ] Error handling verified
- [ ] Unit tests written
- [ ] Documentation for API hooks

**Dependencies**: FE-S1-01, FE-S1-02

---

### FE-S1-04: Set Up Authentication & State Management

**Story Points**: 3
**Assigned To**: 1 Frontend Engineer
**Description**: Configure Zustand for global state management with authentication store and middleware.

**Acceptance Criteria**:
- [ ] Zustand store created for authentication
- [ ] AuthStore with login, logout, refresh token methods
- [ ] Token storage (localStorage with encryption)
- [ ] User information storage (user role, permissions)
- [ ] Auth state persisted across page reloads
- [ ] App-wide store for general state (loading, notifications)
- [ ] Store middleware for logging and debugging
- [ ] Type-safe store with TypeScript interfaces
- [ ] Unit tests for store logic

**Technical Requirements**:
```typescript
// store/auth.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import client from '../api/client';

interface User {
  userId: number;
  username: string;
  fullName: string;
  email: string;
  role: string;
  permissions: string[];
}

interface AuthStore {
  // State
  token: string | null;
  refreshToken: string | null;
  user: User | null;
  isLoading: boolean;
  error: string | null;

  // Actions
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  refresh: () => Promise<void>;
  setUser: (user: User) => void;
  clearError: () => void;
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      token: null,
      refreshToken: null,
      user: null,
      isLoading: false,
      error: null,

      login: async (username: string, password: string) => {
        set({ isLoading: true, error: null });
        try {
          const response = await client.post('/auth/login', {
            username,
            password,
          });

          set({
            token: response.accessToken,
            refreshToken: response.refreshToken,
            user: response.user,
            isLoading: false,
          });
        } catch (error) {
          set({
            error: error instanceof Error ? error.message : 'Login failed',
            isLoading: false,
          });
          throw error;
        }
      },

      logout: () => {
        set({
          token: null,
          refreshToken: null,
          user: null,
        });
      },

      refresh: async () => {
        const { refreshToken } = get();
        if (!refreshToken) throw new Error('No refresh token');

        try {
          const response = await client.post('/auth/refresh', {
            refreshToken,
          });

          set({
            token: response.accessToken,
            refreshToken: response.refreshToken,
          });
        } catch (error) {
          get().logout();
          throw error;
        }
      },

      setUser: (user: User) => set({ user }),
      clearError: () => set({ error: null }),
    }),
    {
      name: 'auth-store',
      partialize: (state) => ({
        token: state.token,
        refreshToken: state.refreshToken,
        user: state.user,
      }),
    }
  )
);
```

**App-Wide Store**:
```typescript
// store/app.ts
import { create } from 'zustand';

interface AppStore {
  isSidebarOpen: boolean;
  isLoading: boolean;
  notification: {
    type: 'success' | 'error' | 'info' | 'warning';
    message: string;
  } | null;

  toggleSidebar: () => void;
  setLoading: (loading: boolean) => void;
  showNotification: (type: string, message: string) => void;
  clearNotification: () => void;
}

export const useAppStore = create<AppStore>((set) => ({
  isSidebarOpen: true,
  isLoading: false,
  notification: null,

  toggleSidebar: () =>
    set((state) => ({ isSidebarOpen: !state.isSidebarOpen })),
  setLoading: (loading: boolean) => set({ isLoading: loading }),
  showNotification: (type: string, message: string) =>
    set({ notification: { type: type as any, message } }),
  clearNotification: () => set({ notification: null }),
}));
```

**Implementation Guidance**:
1. Use Zustand for simple, performant state management
2. Persist important state to localStorage
3. Keep store slices focused and minimal
4. Implement proper TypeScript interfaces
5. Use selectors to minimize re-renders
6. Implement middleware for logging and debugging
7. Test store logic independently

**Definition of Done**:
- [ ] Zustand stores created and working
- [ ] Token storage and retrieval functional
- [ ] Authentication state persisted
- [ ] Store selectors optimized
- [ ] Unit tests for stores
- [ ] Documentation for store usage

**Dependencies**: FE-S1-01, FE-S1-02, FE-S1-03

---

### FE-S1-05: Create Base Components & Component Library

**Story Points**: 5
**Assigned To**: UI/UX Designer + 1 Frontend Engineer
**Description**: Build reusable UI component library with common components (Button, Input, Modal, Table, etc.).

**Acceptance Criteria**:
- [ ] Button component with variants (primary, secondary, danger, etc.)
- [ ] Input component with validation state
- [ ] Select/Dropdown component
- [ ] Modal/Dialog component
- [ ] Table component with sorting and pagination
- [ ] Card component for content grouping
- [ ] Form wrapper component
- [ ] Badge/Pill component
- [ ] Loading Spinner component
- [ ] Error message component
- [ ] Success/Toast notification component
- [ ] Layout components (Header, Sidebar, Footer)
- [ ] Navigation component
- [ ] Breadcrumb component
- [ ] All components use Tailwind CSS
- [ ] Component Storybook/documentation
- [ ] TypeScript interfaces for all props

**Technical Requirements**:
```typescript
// components/common/Button.tsx
import React from 'react';
import clsx from 'clsx';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
  children: React.ReactNode;
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      variant = 'primary',
      size = 'md',
      isLoading = false,
      className,
      children,
      ...props
    },
    ref
  ) => {
    const baseStyles = 'font-medium rounded-lg transition-colors';

    const variants = {
      primary: 'bg-indigo-600 text-white hover:bg-indigo-700',
      secondary: 'bg-gray-200 text-gray-900 hover:bg-gray-300',
      danger: 'bg-red-600 text-white hover:bg-red-700',
      ghost: 'bg-transparent text-gray-600 hover:bg-gray-100',
    };

    const sizes = {
      sm: 'px-3 py-1.5 text-sm',
      md: 'px-4 py-2 text-base',
      lg: 'px-6 py-3 text-lg',
    };

    return (
      <button
        ref={ref}
        className={clsx(baseStyles, variants[variant], sizes[size], className)}
        disabled={isLoading || props.disabled}
        {...props}
      >
        {isLoading ? <span>Loading...</span> : children}
      </button>
    );
  }
);

Button.displayName = 'Button';


// components/common/FormInput.tsx
interface FormInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
}

export const FormInput = React.forwardRef<HTMLInputElement, FormInputProps>(
  ({ label, error, helperText, ...props }, ref) => {
    return (
      <div className="flex flex-col gap-1">
        {label && <label className="text-sm font-medium">{label}</label>}
        <input
          ref={ref}
          className={clsx(
            'px-3 py-2 border rounded-lg',
            error ? 'border-red-500' : 'border-gray-300'
          )}
          {...props}
        />
        {error && <span className="text-sm text-red-500">{error}</span>}
        {helperText && <span className="text-sm text-gray-500">{helperText}</span>}
      </div>
    );
  }
);

FormInput.displayName = 'FormInput';

// components/common/Table.tsx
export interface ColumnDef<T> {
  key: keyof T;
  header: string;
  render?: (value: T[keyof T], row: T) => React.ReactNode;
  sortable?: boolean;
  width?: string;
}

interface TableProps<T> {
  data: T[];
  columns: ColumnDef<T>[];
  onRowClick?: (row: T) => void;
  isLoading?: boolean;
  pagination?: {
    page: number;
    pageSize: number;
    totalItems: number;
    onPageChange: (page: number) => void;
  };
}

export const Table = <T,>({
  data,
  columns,
  onRowClick,
  isLoading,
  pagination,
}: TableProps<T>) => {
  return (
    <div className="overflow-x-auto">
      <table className="min-w-full border-collapse">
        <thead>
          <tr className="bg-gray-100 border-b">
            {columns.map((col) => (
              <th key={String(col.key)} className="px-4 py-2 text-left text-sm font-semibold">
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {isLoading ? (
            <tr>
              <td colSpan={columns.length} className="px-4 py-4 text-center">
                Loading...
              </td>
            </tr>
          ) : data.length === 0 ? (
            <tr>
              <td colSpan={columns.length} className="px-4 py-4 text-center text-gray-500">
                No data available
              </td>
            </tr>
          ) : (
            data.map((row, idx) => (
              <tr
                key={idx}
                className="border-b hover:bg-gray-50 cursor-pointer"
                onClick={() => onRowClick?.(row)}
              >
                {columns.map((col) => (
                  <td key={String(col.key)} className="px-4 py-2 text-sm">
                    {col.render
                      ? col.render(row[col.key], row)
                      : String(row[col.key])}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
      {pagination && (
        <div className="mt-4 flex items-center justify-between">
          <span className="text-sm text-gray-600">
            Page {pagination.page + 1} of{' '}
            {Math.ceil(pagination.totalItems / pagination.pageSize)}
          </span>
          <div className="flex gap-2">
            <Button
              onClick={() => pagination.onPageChange(pagination.page - 1)}
              disabled={pagination.page === 0}
            >
              Previous
            </Button>
            <Button
              onClick={() => pagination.onPageChange(pagination.page + 1)}
              disabled={
                pagination.page >=
                Math.ceil(pagination.totalItems / pagination.pageSize) - 1
              }
            >
              Next
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};
```

**Implementation Guidance**:
1. Use TypeScript for all components
2. Support both controlled and uncontrolled components
3. Export components as named exports
4. Use React.forwardRef for components that need refs
5. Create variant props for different visual styles
6. Implement accessibility features (ARIA labels, etc.)
7. Create Storybook stories for all components
8. Document component props and usage

**Definition of Done**:
- [ ] All base components created
- [ ] Components documented with TypeScript interfaces
- [ ] Components tested (unit tests)
- [ ] Storybook setup for component browsing
- [ ] Accessibility verified (keyboard navigation, screen reader)
- [ ] Responsive design verified
- [ ] Tailwind CSS classes consistent

**Dependencies**: FE-S1-01, FE-S1-02

---

### FE-S1-06: Set Up Routing & Layout

**Story Points**: 3
**Assigned To**: 1 Frontend Engineer
**Description**: Configure React Router with protected routes, layout components, and navigation.

**Acceptance Criteria**:
- [ ] React Router v6 configured
- [ ] Public and protected routes defined
- [ ] Root layout component created
- [ ] Header component with navigation
- [ ] Sidebar navigation component
- [ ] Main content area layout
- [ ] Footer component
- [ ] Protected route wrapper component
- [ ] Route guards for authentication
- [ ] Breadcrumb navigation
- [ ] Responsive navigation (mobile menu)
- [ ] Active route highlighting
- [ ] 404 Not Found page
- [ ] Loading skeleton while verifying auth

**Technical Requirements**:
```typescript
// App.tsx
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './store/auth';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/layout/Layout';
import Login from './pages/auth/Login';
import Dashboard from './pages/Dashboard';
import StudentList from './pages/student/StudentList';
import StudentDetail from './pages/student/StudentDetail';
import NotFound from './pages/NotFound';

export default function App() {
  return (
    <Router>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<Login />} />

        {/* Protected Routes */}
        <Route element={<ProtectedRoute />}>
          <Route element={<Layout />}>
            <Route path="/" element={<Dashboard />} />
            <Route path="/students" element={<StudentList />} />
            <Route path="/students/:id" element={<StudentDetail />} />
            {/* Add more routes */}
          </Route>
        </Route>

        {/* Catch-all route */}
        <Route path="*" element={<NotFound />} />
      </Routes>
    </Router>
  );
}

// components/ProtectedRoute.tsx
import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../store/auth';

export default function ProtectedRoute() {
  const { token } = useAuthStore();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

// components/layout/Layout.tsx
import { Outlet } from 'react-router-dom';
import Header from './Header';
import Sidebar from './Sidebar';
import Footer from './Footer';

export default function Layout() {
  return (
    <div className="flex h-screen bg-gray-100">
      <Sidebar />
      <div className="flex flex-col flex-1 overflow-hidden">
        <Header />
        <main className="flex-1 overflow-auto p-4">
          <Outlet />
        </main>
        <Footer />
      </div>
    </div>
  );
}

// components/layout/Sidebar.tsx
import { Link, useLocation } from 'react-router-dom';
import { useAppStore } from '../../store/app';
import { useAuthStore } from '../../store/auth';
import clsx from 'clsx';

const navigationItems = [
  { label: 'Dashboard', href: '/', icon: 'Home' },
  { label: 'Students', href: '/students', icon: 'Users' },
  { label: 'Classes', href: '/classes', icon: 'BookOpen' },
  { label: 'Fees', href: '/fees', icon: 'DollarSign' },
  { label: 'Payments', href: '/payments', icon: 'CreditCard' },
  { label: 'Receipts', href: '/receipts', icon: 'FileText' },
  { label: 'Reports', href: '/reports', icon: 'BarChart3' },
  { label: 'Configuration', href: '/config', icon: 'Settings' },
];

export default function Sidebar() {
  const location = useLocation();
  const { isSidebarOpen } = useAppStore();
  const { user } = useAuthStore();

  const isActive = (href: string) => location.pathname === href;

  return (
    <aside
      className={clsx(
        'bg-gray-900 text-white transition-all duration-300',
        isSidebarOpen ? 'w-64' : 'w-20'
      )}
    >
      <div className="p-4 border-b border-gray-700">
        <h1 className={clsx(isSidebarOpen ? 'text-xl font-bold' : 'text-center')}>
          {isSidebarOpen ? 'SMS' : 'S'}
        </h1>
      </div>

      <nav className="mt-4 space-y-2 p-4">
        {navigationItems.map((item) => (
          <Link
            key={item.href}
            to={item.href}
            className={clsx(
              'flex items-center gap-3 px-4 py-2 rounded-lg transition-colors',
              isActive(item.href)
                ? 'bg-indigo-600 text-white'
                : 'text-gray-300 hover:bg-gray-800'
            )}
          >
            <span>{item.icon}</span>
            {isSidebarOpen && <span>{item.label}</span>}
          </Link>
        ))}
      </nav>

      <div className="absolute bottom-4 left-0 right-0 p-4 border-t border-gray-700">
        {isSidebarOpen && (
          <div className="text-sm">
            <p className="font-semibold">{user?.fullName}</p>
            <p className="text-gray-400 text-xs">{user?.role}</p>
          </div>
        )}
      </div>
    </aside>
  );
}
```

**Implementation Guidance**:
1. Use React Router v6 for modern routing
2. Protect sensitive routes with ProtectedRoute component
3. Implement route guards for authorization checks
4. Use dynamic imports for code splitting
5. Implement proper loading states during auth verification
6. Support mobile-responsive navigation
7. Add active route highlighting
8. Implement breadcrumb navigation

**Definition of Done**:
- [ ] All routes created and functional
- [ ] Protected routes working correctly
- [ ] Layout components responsive
- [ ] Navigation working on mobile and desktop
- [ ] 404 page functional
- [ ] Loading states during auth check
- [ ] Route guards tested

**Dependencies**: FE-S1-01, FE-S1-02, FE-S1-04

---

## Sprint 3: Student Management Module
**Duration**: Dec 9-23, 2025
**Sprint Goal**: Build student management UI and integrate with backend APIs
**Team Capacity**: 40 story points

---

### FE-S3-01: Create Student List Page with Filtering & Pagination

**Story Points**: 5
**Assigned To**: 1 Frontend Engineer
**Description**: Build student list page with search, filtering, sorting, and pagination capabilities.

**Acceptance Criteria**:
- [ ] Student list page displays all students in table format
- [ ] Search functionality (by name, student code, mobile)
- [ ] Filter by status (ACTIVE, INACTIVE, GRADUATED, TRANSFERRED, WITHDRAWN)
- [ ] Filter by class
- [ ] Sort by name, admission date, status
- [ ] Pagination with page size options (10, 20, 50)
- [ ] Loading state while fetching data
- [ ] Error handling with retry option
- [ ] Empty state message
- [ ] Student count display
- [ ] Click student to view details
- [ ] Add new student button
- [ ] Delete/Archive student action
- [ ] Responsive design for mobile
- [ ] Debounced search to prevent excessive API calls

**Technical Requirements**:
```typescript
// pages/student/StudentList.tsx
import { useState, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import client from '../../api/client';
import { Table, ColumnDef, Button, FormInput } from '../../components';
import { Student, StudentStatus } from '../../types/student';

interface StudentListFilters {
  search: string;
  status: StudentStatus | '';
  className: string;
  page: number;
  pageSize: number;
}

export default function StudentList() {
  const navigate = useNavigate();
  const [filters, setFilters] = useState<StudentListFilters>({
    search: '',
    status: '',
    className: '',
    page: 0,
    pageSize: 20,
  });

  // Fetch students with filters
  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['students', filters],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (filters.search) params.append('search', filters.search);
      if (filters.status) params.append('status', filters.status);
      if (filters.className) params.append('class', filters.className);
      params.append('page', filters.page.toString());
      params.append('size', filters.pageSize.toString());

      return client.get(`/students?${params.toString()}`);
    },
  });

  const columns: ColumnDef<Student>[] = useMemo(
    () => [
      {
        key: 'studentCode',
        header: 'Student Code',
        sortable: true,
      },
      {
        key: 'firstName',
        header: 'Name',
        render: (_, row) => `${row.firstName} ${row.lastName}`,
      },
      {
        key: 'mobile',
        header: 'Mobile',
      },
      {
        key: 'status',
        header: 'Status',
        render: (status) => (
          <span className={`px-2 py-1 rounded text-xs font-medium
            ${status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'}`}>
            {status}
          </span>
        ),
      },
      {
        key: 'admissionDate',
        header: 'Admission Date',
        render: (date) => new Date(date as string).toLocaleDateString(),
      },
    ],
    []
  );

  const handleRowClick = (student: Student) => {
    navigate(`/students/${student.studentId}`);
  };

  const handleSearch = (value: string) => {
    setFilters({ ...filters, search: value, page: 0 });
  };

  const handleStatusFilter = (status: StudentStatus | '') => {
    setFilters({ ...filters, status, page: 0 });
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Students</h1>
        <Button onClick={() => navigate('/students/new')}>
          Add Student
        </Button>
      </div>

      {/* Filters */}
      <div className="bg-white p-4 rounded-lg shadow space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <FormInput
            placeholder="Search by name, code, mobile..."
            value={filters.search}
            onChange={(e) => handleSearch(e.target.value)}
          />

          <select
            className="px-3 py-2 border rounded-lg"
            value={filters.status}
            onChange={(e) => handleStatusFilter(e.target.value as StudentStatus | '')}
          >
            <option value="">All Status</option>
            <option value="ACTIVE">Active</option>
            <option value="INACTIVE">Inactive</option>
            <option value="GRADUATED">Graduated</option>
            <option value="TRANSFERRED">Transferred</option>
            <option value="WITHDRAWN">Withdrawn</option>
          </select>

          <select
            className="px-3 py-2 border rounded-lg"
            value={filters.className}
            onChange={(e) => setFilters({ ...filters, className: e.target.value, page: 0 })}
          >
            <option value="">All Classes</option>
            {Array.from({ length: 10 }, (_, i) => (
              <option key={i + 1} value={(i + 1).toString()}>
                Class {i + 1}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Student Table */}
      {isError && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          Failed to load students.
          <Button onClick={() => refetch()} variant="ghost" className="ml-4">
            Retry
          </Button>
        </div>
      )}

      {!isError && (
        <div className="bg-white rounded-lg shadow">
          <Table
            data={data?.content || []}
            columns={columns}
            onRowClick={handleRowClick}
            isLoading={isLoading}
            pagination={{
              page: filters.page,
              pageSize: filters.pageSize,
              totalItems: data?.page?.totalElements || 0,
              onPageChange: (page) => setFilters({ ...filters, page }),
            }}
          />
        </div>
      )}
    </div>
  );
}
```

**Custom Hooks for API**:
```typescript
// api/hooks/useStudents.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import client from '../client';
import { Student, CreateStudentRequest } from '../../types/student';

export const useStudents = (params: Record<string, any>) => {
  return useQuery({
    queryKey: ['students', params],
    queryFn: async () => {
      const queryParams = new URLSearchParams();
      Object.entries(params).forEach(([key, value]) => {
        if (value) queryParams.append(key, value);
      });
      return client.get(`/students?${queryParams}`);
    },
  });
};

export const useStudent = (id: number) => {
  return useQuery({
    queryKey: ['student', id],
    queryFn: () => client.get(`/students/${id}`),
    enabled: !!id,
  });
};

export const useCreateStudent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateStudentRequest) =>
      client.post('/students', data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['students'] });
    },
  });
};

export const useUpdateStudent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: any }) =>
      client.put(`/students/${id}`, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: ['student', id] });
      queryClient.invalidateQueries({ queryKey: ['students'] });
    },
  });
};
```

**Implementation Guidance**:
1. Use React Query for server state management
2. Implement debounced search to reduce API calls
3. Add loading and error states
4. Implement pagination correctly
5. Add responsive design for mobile
6. Use optimistic updates where applicable
7. Implement proper error handling and retry logic

**Definition of Done**:
- [ ] Student list page displaying data from API
- [ ] Search, filter, and pagination working
- [ ] Loading and error states shown
- [ ] Empty state message displayed
- [ ] Responsive design verified
- [ ] Debounced search working
- [ ] Component and integration tests written
- [ ] API calls optimized (no duplicate requests)

**Dependencies**: FE-S1-05, FE-S1-06, FE-S1-03

---

### FE-S3-02: Create Student Form (Create/Edit)

**Story Points**: 5
**Assigned To**: 1 Frontend Engineer
**Description**: Build student creation and edit form with guardian management and validation.

**Acceptance Criteria**:
- [ ] Form for creating new students
- [ ] Form for editing existing students
- [ ] Validated fields: firstName, lastName, dateOfBirth (age 3-18), mobile (10 digits), email
- [ ] Guardian information collection (at least one guardian required)
- [ ] Add/remove multiple guardians
- [ ] Set primary guardian
- [ ] Blood group selection dropdown
- [ ] Gender selection
- [ ] Photo upload support
- [ ] Form validation with error messages
- [ ] Disable submit while saving
- [ ] Success/error notifications
- [ ] Cancel button with confirmation if form changed
- [ ] Responsive design
- [ ] Client-side validation with Zod
- [ ] Integration with backend API

**Technical Requirements**:
```typescript
// pages/student/StudentForm.tsx
import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm, Controller, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button, FormInput, toast } from '../../components';
import { useCreateStudent, useUpdateStudent, useStudent } from '../../api/hooks/useStudents';

// Validation schema
const guardianSchema = z.object({
  firstName: z.string().min(2).max(50),
  lastName: z.string().min(2).max(50),
  relationship: z.enum(['FATHER', 'MOTHER', 'GUARDIAN', 'OTHER']),
  mobile: z.string().regex(/^[0-9]{10}$/),
  email: z.string().email().optional(),
  occupation: z.string().optional(),
  isPrimary: z.boolean(),
});

const studentFormSchema = z.object({
  firstName: z.string().min(2).max(50),
  lastName: z.string().min(2).max(50),
  dateOfBirth: z.string().refine((date) => {
    const age = new Date().getFullYear() - new Date(date).getFullYear();
    return age >= 3 && age <= 18;
  }, { message: 'Age must be between 3 and 18 years' }),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER']),
  mobile: z.string().regex(/^[0-9]{10}$/, 'Mobile must be 10 digits'),
  email: z.string().email().optional(),
  address: z.string().optional(),
  bloodGroup: z.string().optional(),
  admissionDate: z.string(),
  guardians: z.array(guardianSchema).min(1),
  photo: z.instanceof(File).optional(),
});

type StudentFormData = z.infer<typeof studentFormSchema>;

export default function StudentForm() {
  const { id } = useParams<{ id?: string }>();
  const navigate = useNavigate();
  const isEditMode = !!id && id !== 'new';

  const { data: existingStudent } = useStudent(isEditMode ? parseInt(id!) : 0);
  const createMutation = useCreateStudent();
  const updateMutation = useUpdateStudent();

  const {
    control,
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting, isDirty },
  } = useForm<StudentFormData>({
    resolver: zodResolver(studentFormSchema),
    defaultValues: existingStudent || {
      guardians: [{ isPrimary: true } as any],
    },
  });

  const { fields: guardianFields, append, remove } = useFieldArray({
    control,
    name: 'guardians',
  });

  const onSubmit = async (data: StudentFormData) => {
    try {
      if (isEditMode) {
        await updateMutation.mutateAsync({ id: parseInt(id!), data });
        toast.success('Student updated successfully');
      } else {
        await createMutation.mutateAsync(data);
        toast.success('Student created successfully');
      }
      navigate(`/students/${existingStudent?.studentId || 'list'}`);
    } catch (error) {
      toast.error('Error saving student');
    }
  };

  const handleCancel = () => {
    if (isDirty) {
      if (confirm('You have unsaved changes. Are you sure?')) {
        navigate(-1);
      }
    } else {
      navigate(-1);
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">
        {isEditMode ? 'Edit Student' : 'Add New Student'}
      </h1>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
        {/* Student Details */}
        <section className="bg-white p-6 rounded-lg shadow">
          <h2 className="text-lg font-semibold mb-4">Student Information</h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormInput
              {...register('firstName')}
              label="First Name"
              error={errors.firstName?.message}
            />

            <FormInput
              {...register('lastName')}
              label="Last Name"
              error={errors.lastName?.message}
            />

            <FormInput
              {...register('dateOfBirth')}
              label="Date of Birth"
              type="date"
              error={errors.dateOfBirth?.message}
            />

            <div>
              <label className="block text-sm font-medium mb-2">Gender</label>
              <select {...register('gender')} className="w-full px-3 py-2 border rounded-lg">
                <option value="">Select Gender</option>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </select>
            </div>

            <FormInput
              {...register('mobile')}
              label="Mobile Number"
              error={errors.mobile?.message}
            />

            <FormInput
              {...register('email')}
              label="Email"
              type="email"
              error={errors.email?.message}
            />

            <FormInput
              {...register('address')}
              label="Address"
            />

            <div>
              <label className="block text-sm font-medium mb-2">Blood Group</label>
              <select {...register('bloodGroup')} className="w-full px-3 py-2 border rounded-lg">
                <option value="">Select Blood Group</option>
                {['O+', 'O-', 'A+', 'A-', 'B+', 'B-', 'AB+', 'AB-'].map((bg) => (
                  <option key={bg} value={bg}>{bg}</option>
                ))}
              </select>
            </div>

            <FormInput
              {...register('admissionDate')}
              label="Admission Date"
              type="date"
            />
          </div>
        </section>

        {/* Guardians */}
        <section className="bg-white p-6 rounded-lg shadow">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-semibold">Guardian Information</h2>
            <Button
              type="button"
              onClick={() => append({ isPrimary: false } as any)}
              variant="secondary"
            >
              Add Guardian
            </Button>
          </div>

          <div className="space-y-6">
            {guardianFields.map((field, index) => (
              <div key={field.id} className="border p-4 rounded-lg space-y-4">
                <div className="flex justify-between items-center">
                  <h3 className="font-medium">Guardian {index + 1}</h3>
                  {guardianFields.length > 1 && (
                    <Button
                      type="button"
                      onClick={() => remove(index)}
                      variant="danger"
                      size="sm"
                    >
                      Remove
                    </Button>
                  )}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <FormInput
                    {...register(`guardians.${index}.firstName`)}
                    label="First Name"
                  />

                  <FormInput
                    {...register(`guardians.${index}.lastName`)}
                    label="Last Name"
                  />

                  <div>
                    <label className="block text-sm font-medium mb-2">Relationship</label>
                    <select {...register(`guardians.${index}.relationship`)}>
                      <option value="FATHER">Father</option>
                      <option value="MOTHER">Mother</option>
                      <option value="GUARDIAN">Guardian</option>
                      <option value="OTHER">Other</option>
                    </select>
                  </div>

                  <FormInput
                    {...register(`guardians.${index}.mobile`)}
                    label="Mobile"
                  />

                  <FormInput
                    {...register(`guardians.${index}.email`)}
                    label="Email"
                    type="email"
                  />

                  <FormInput
                    {...register(`guardians.${index}.occupation`)}
                    label="Occupation"
                  />

                  <label className="flex items-center gap-2">
                    <input
                      type="checkbox"
                      {...register(`guardians.${index}.isPrimary`)}
                    />
                    <span className="text-sm">Primary Contact</span>
                  </label>
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* Form Actions */}
        <div className="flex justify-end gap-4">
          <Button
            type="button"
            onClick={handleCancel}
            variant="secondary"
          >
            Cancel
          </Button>

          <Button
            type="submit"
            disabled={isSubmitting}
            isLoading={isSubmitting}
          >
            {isEditMode ? 'Update Student' : 'Create Student'}
          </Button>
        </div>
      </form>
    </div>
  );
}
```

**Implementation Guidance**:
1. Use React Hook Form for efficient form handling
2. Implement Zod for comprehensive client-side validation
3. Show validation errors inline
4. Implement proper file upload handling
5. Disable submit button while loading
6. Add confirmation for unsaved changes
7. Implement photo preview
8. Use custom hooks for API mutations
9. Provide user feedback with toast notifications

**Definition of Done**:
- [ ] Form validates all fields correctly
- [ ] Guardian management working (add/remove/primary)
- [ ] File upload with validation
- [ ] Form submission creates/updates student
- [ ] Error handling with user-friendly messages
- [ ] Loading state during submission
- [ ] Responsive design verified
- [ ] Integration tests written

**Dependencies**: FE-S1-05, FE-S1-03, FE-S3-01

---

## Summary Frontend Sprint Breakdown

**Total Story Points Sprints 1-3: 45 points**

Frontend development continues through Sprints 4-11 with:
- **Sprint 4**: Class Management UI
- **Sprint 5**: Academic Year & Enrollment
- **Sprint 6**: Fee Management UI
- **Sprint 7**: Payment & Tracking UI
- **Sprint 8**: Receipt & Reporting UI
- **Sprint 9**: Advanced Features & Polish
- **Sprint 10**: Testing & Quality Assurance
- **Sprint 11**: Performance & Accessibility

Each sprint follows the same detailed structure with:
- Clear user stories and acceptance criteria
- Technical implementation examples
- Custom hooks for API integration
- Responsive design considerations
- Testing requirements

---

## Frontend Code Quality Standards

### Coding Standards
- TypeScript strict mode enforced
- ESLint configuration with React best practices
- Prettier formatting (100 character line length)
- No console errors in production builds
- Accessibility standards (WCAG 2.1 AA)

### Testing Standards
- Unit tests: 70%+ code coverage
- Component tests using React Testing Library
- Integration tests for API interactions
- E2E tests for critical user flows
- No skipped tests in production

### Performance Standards
- Lighthouse score: 80+ in all categories
- First Contentful Paint (FCP): <1.5s
- Largest Contentful Paint (LCP): <2.5s
- Cumulative Layout Shift (CLS): <0.1
- Bundle size: <250KB (gzipped, excluding assets)

---

## Document Control

| Version | Date | Author | Status |
|---------|------|--------|--------|
| 1.0 | Nov 11, 2025 | Frontend Team Lead | Draft |

