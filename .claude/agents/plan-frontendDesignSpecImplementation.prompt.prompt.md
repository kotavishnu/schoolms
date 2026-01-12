# Plan: Create Frontend Design Spec and Implement Application

Analyze the Figma-generated reference code in `frontend/reference-code`, extract design patterns, component structure, and UI specifications, then create a comprehensive FRONTEND_DESIGN_SPECIFICATION.md that guides architects, project managers, and frontend developers. Subsequently implement the complete school management system frontend with backend integration.

## Steps

### 1. Create FRONTEND_DESIGN_SPECIFICATION.md

Create comprehensive specification document in `specs/FRONTEND_DESIGN_SPECIFICATION.md` covering:

- **UI Component Architecture**: Shadcn/ui + Tailwind CSS framework, component library structure, reusable patterns
- **Data Models**: Update Student interface with missing fields (DOB, Adhaar Number, createdAt, updatedAt timestamps), Configuration interface alignment with backend
- **API Integration Layer Design**: HTTP client setup, service layer architecture, error handling patterns, loading state management
- **Form Validation Rules**: Age range validation (3-18), mobile uniqueness check, email format validation, required field enforcement, edit restrictions (only Name, Mobile, Status editable)
- **Routing Structure**: Document SPA routing with React Router v6, page hierarchy, navigation patterns
- **State Management Strategy**: Global state approach (Context API vs library), data flow patterns, cache strategy
- **Styling Guidelines**: Tailwind configuration, theme customization, responsive design breakpoints, dark mode support
- **Gap Analysis**: Reference code vs production requirements, implementation priorities, missing features roadmap

### 2. Set up Production Frontend Project Structure

Create organized production project outside `frontend/reference-code`:

**Directory Structure:**
```
frontend/
  src/
    services/        # API integration
    contexts/        # Global state management
    hooks/           # Custom React hooks
    components/      # Migrated and enhanced components
    pages/           # Page-level components
    types/           # Extended TypeScript interfaces
    utils/           # Helper functions
    styles/          # Global styles
```

**Initialize Project:**
- Create `package.json` with dependencies:
  - React 18+, TypeScript
  - React Router v6
  - Tailwind CSS
  - Shadcn/ui components
  - react-hook-form
  - axios for HTTP client
  - sonner for toast notifications
  - lucide-react for icons
- Set up build tool (Vite recommended)
- Configure TypeScript (tsconfig.json)
- Configure Tailwind (tailwind.config.js)

### 3. Implement API Integration Layer

Create robust API service layer in `frontend/src/services`:

**HTTP Client** (`services/api.ts`):
- Axios instance with base URL configuration
- Request/response interceptors
- Global error handling
- Loading state management
- Token handling (future auth support)

**Student Service** (`services/studentService.ts`):
- `GET /api/students` - List all students with filters (search, status)
- `GET /api/students?search=` - Search by ID, name, guardian
- `GET /api/students/:id` - Get student details
- `POST /api/students` - Create student (backend generates ID)
- `PATCH /api/students/:id` - Update student (only Name, Mobile, Status)
- `DELETE /api/students/:id` - Delete student
- Pagination support
- Filter/search query building

**Configuration Service** (`services/configurationService.ts`):
- `GET /api/configurations` - List all configurations
- `GET /api/configurations?category=` - Filter by category
- `POST /api/configurations` - Create configuration
- `PATCH /api/configurations/:id` - Update configuration
- `DELETE /api/configurations/:id` - Delete configuration

**Environment Configuration** (`.env`):
- `VITE_API_BASE_URL` for different environments
- Development, staging, production URLs

**Type Definitions** (`types/api.ts`):
- Request/response interfaces
- Error response types
- Pagination types
- API metadata types

### 4. Enhance Components with Backend Integration

Migrate and enhance components from `frontend/reference-code/app/components`:

**StudentsPage.tsx Enhancements:**
- Replace mock data with API calls using StudentService
- Implement loading states (skeleton loaders)
- Add error handling with user-friendly messages
- Implement pagination controls (if backend supports)
- Add student ID search field
- Enhance filters with backend search
- Toast notifications for CRUD operations
- Optimistic UI updates with rollback on failure
- Confirm dialogs using Alert Dialog component (replace window.confirm)

**StudentDialog.tsx Enhancements:**
- Add missing fields: DOB (date picker), Adhaar Number (input)
- Enhanced validation rules:
  - Age 3-18 validation (calculate from DOB)
  - Mobile uniqueness check (API call)
  - Email format validation
  - Required field validation
- Implement edit restrictions:
  - Only Name, Mobile, Status editable in edit mode
  - Disable other fields when editing
- Backend integration:
  - POST for create (receive backend-generated ID)
  - PATCH for update (restricted fields)
- Loading states during submission
- Error handling with field-level error messages
- Success/error toast notifications

**ViewStudentDialog.tsx Updates:**
- Display additional fields: DOB, Adhaar, createdAt, updatedAt
- Format dates properly
- Show student ID prominently

**ConfigurationsPage.tsx Enhancements:**
- Replace mock data with API calls
- Add category filter tabs/dropdown
- Backend integration for category-based retrieval
- Loading states and error handling
- Toast notifications for CRUD operations
- Pagination if needed

**ConfigurationDialog.tsx Updates:**
- Backend integration (POST/PATCH)
- Validation for required fields
- Auto-update lastUpdated on backend
- Error handling and loading states

**HomePage.tsx Enhancements:**
- Replace hardcoded statistics with real API data
- Create dashboard API endpoint or calculate from students list
- Display dynamic counts:
  - Total students
  - Active students
  - Total configurations
- Loading states for statistics cards
- Error handling with fallback UI

**Toast Notification System:**
- Implement Sonner toast provider
- Success messages: "Student created successfully", "Configuration updated"
- Error messages: "Failed to delete student", "Mobile number already exists"
- Consistent toast patterns across all CRUD operations

**Header.tsx Updates:**
- Keep existing navigation
- Dark mode toggle (optional implementation)
- Active route highlighting (already implemented)

**New Components:**
- Error Boundary component for error catching
- Loading component/skeleton loaders
- Pagination component
- Confirmation Dialog component (reusable Alert Dialog wrapper)

### 5. Build and Test the Application

Complete setup and validation:

**Build Configuration:**
- Configure Vite for development and production builds
- Set up path aliases (@/ for src)
- Configure environment variable loading
- Optimize bundle size

**Environment Setup:**
- Create `.env.development` with local API URL
- Create `.env.production` with production API URL
- Document environment variables in README

**Error Handling:**
- Implement global error boundary
- Add component-level error boundaries for critical sections
- Network error handling (offline detection)
- API timeout handling
- Validation error display

**Loading States:**
- Skeleton loaders for data fetching
- Button loading states during submission
- Page-level loading indicators
- Disable forms during submission

**Testing:**
- Manual testing of CRUD operations against backend services
- Verify validation rules:
  - Age range 3-18
  - Mobile uniqueness
  - Email format
  - Required fields
  - Edit restrictions
- Test error scenarios:
  - Network failures
  - Backend errors
  - Validation failures
- Test responsive design on different screen sizes
- Test navigation and routing
- Verify toast notifications appear correctly

**Integration Verification:**
- Confirm Student Service endpoints are functional
- Confirm Configuration Service endpoints are functional
- Test microservice failure handling
- Verify CORS configuration (per lessons learned)
- Test data persistence across page refreshes

## Further Considerations

### 1. Missing Backend Services
**Question**: Does the backend (Student Service, Configuration Service) already exist with endpoints ready, or should this plan include backend implementation first?

**Impact**: Frontend implementation assumes backend APIs are available. If not ready, need to:
- Implement backend microservices first (Student Service, Configuration Service)
- Or use mock API server (JSON Server, MSW) for development
- Or implement backend and frontend in parallel with API contract

### 2. State Management Choice
**Question**: Should we use React Context API (simpler, built-in) or a library like Zustand/Redux (more scalable) for global state management?

**Recommendation**: 
- **Context API**: Sufficient for current scope, no external dependencies, simpler
- **Zustand/Redux**: Better for complex state, time-travel debugging, middleware support

**Suggested Approach**: Start with Context API, migrate to library if complexity grows

### 3. Testing Requirements
**Question**: Should the plan include setting up unit tests (Jest + React Testing Library) and E2E tests (Playwright/Cypress) immediately, or defer to a later phase?

**Options**:
- **Immediate**: Better code quality, TDD approach, prevents regressions
- **Deferred**: Faster initial implementation, add tests in Phase 2

**Suggested Approach**: Implement basic unit tests for critical components (forms, validation), defer comprehensive E2E tests to Phase 2

### 4. Build Tool Selection
**Question**: Vite vs Create React App vs other?

**Recommendation**: **Vite** - Faster builds, better DX, modern, already configured in reference code styles

### 5. Authentication Integration
**Note**: Requirements state authentication "handled externally" but may need:
- Token storage mechanism
- Protected route wrapper
- Auth context for future integration
- Login redirect handling

**Suggested Approach**: Create auth context placeholder for future integration

### 6. Monitoring and Analytics
**Question**: Should we include frontend error tracking (Sentry) and analytics (Google Analytics) setup?

**Suggested Approach**: Add error tracking for production, defer analytics to Phase 2

## Success Criteria

- ✅ FRONTEND_DESIGN_SPECIFICATION.md created with comprehensive guidelines
- ✅ Production frontend project initialized with proper structure
- ✅ All UI components migrated and enhanced from reference code
- ✅ API integration layer implemented and tested
- ✅ Student CRUD operations fully functional with backend
- ✅ Configuration CRUD operations fully functional with backend
- ✅ All validation rules implemented (age, mobile uniqueness, email)
- ✅ Edit restrictions enforced (Name, Mobile, Status only)
- ✅ Missing fields added (DOB, Adhaar, timestamps)
- ✅ Loading states and error handling in place
- ✅ Toast notifications working for all operations
- ✅ Responsive design maintained from reference code
- ✅ Application builds and runs without errors
- ✅ Manual testing completed successfully

## Timeline Estimate

- **Step 1** (FRONTEND_DESIGN_SPECIFICATION.md): 2-3 hours
- **Step 2** (Project Setup): 1-2 hours
- **Step 3** (API Integration Layer): 3-4 hours
- **Step 4** (Component Enhancement): 8-10 hours
- **Step 5** (Build & Test): 3-4 hours

**Total**: ~17-23 hours of development work
