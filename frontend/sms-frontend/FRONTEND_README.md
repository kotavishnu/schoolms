# School Management System - Frontend

A modern, responsive web application for managing student registrations and school configuration built with React, TypeScript, and Tailwind CSS.

## Features

- Student Registration with form validation
- Student List with search and pagination
- Student Details view
- Edit Student functionality
- Delete Student with soft delete
- Status management (Active/Inactive)
- Configuration management interface
- Error handling with RFC 7807 compliance
- Toast notifications
- Responsive design

## Tech Stack

- **Framework**: React 18.2.0
- **Language**: TypeScript
- **Build Tool**: Vite 7.x
- **Styling**: Tailwind CSS 3.4.x
- **Routing**: React Router 6.x
- **State Management**: TanStack Query (React Query)
- **HTTP Client**: Axios
- **Form Handling**: React Hook Form + Zod validation
- **Icons**: Lucide React
- **Date Utilities**: date-fns

## Prerequisites

- Node.js 20.x or higher
- npm or pnpm
- Backend services running (Student Service on port 8081)

## Installation

```bash
cd D:\wks-sms-specs\frontend\sms-frontend
npm install
```

## Development

```bash
npm run dev
```

Application available at: http://localhost:5173

## Building for Production

```bash
npm run build
npm run preview
```

## Project Structure

```
src/
├── api/              # API client and services
├── components/       # Reusable components
│   ├── ui/          # UI components
│   ├── layout/      # Layout components
│   ├── common/      # Common components
│   └── features/    # Feature components
├── pages/           # Page components
├── types/           # TypeScript types
├── lib/             # Utilities
├── App.tsx          # Main App
└── main.tsx         # Entry point
```

## Environment Variables

- `VITE_API_BASE_URL` - Backend API URL (default: http://localhost:8081)
- `VITE_ENV` - Environment (development/production)

## Testing the Application

1. Start backend service on port 8081
2. Run `npm run dev`
3. Open http://localhost:5173
4. Test all features

## Key Features Implemented

### Student Management
- Create, Read, Update, Delete operations
- Search and filter functionality
- Pagination support
- Status management
- Form validation with Zod

### Error Handling
- RFC 7807 compliant error messages
- Toast notifications
- Network error handling
- Validation error display

### UI/UX
- Responsive design
- Loading states
- Error states
- Success feedback
- Clean, modern interface

## API Integration

Connects to Student Service at http://localhost:8081

Endpoints:
- GET /students - List with pagination
- POST /students - Create student
- GET /students/:id - Get details
- PUT /students/:id - Update student
- DELETE /students/:id - Delete student
- PATCH /students/:id/status - Update status

## Build Output

- Bundle size: ~427KB (gzipped: ~135KB)
- Production-ready build in `dist/` directory

## Future Enhancements

- Unit and E2E tests
- Configuration Service integration
- Advanced search filters
- Student photo upload
- Data export functionality
- Authentication/Authorization

---

**Version**: 1.0.0
**Last Updated**: 2025-11-18
