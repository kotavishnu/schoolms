# School Management System - Frontend

A comprehensive React-based frontend application for managing school operations including classes, students, fees, and receipts.

## Technology Stack

- **React 18.2+** - Modern React with functional components and hooks
- **Vite 5.0+** - Fast build tool and development server
- **React Router 6.20+** - Client-side routing
- **Axios 1.6+** - HTTP client for API requests
- **Tailwind CSS 3.4+** - Utility-first CSS framework
- **date-fns 4.1+** - Date manipulation and formatting

## Features

### 1. Dashboard
- Quick stats overview (total classes, students, active fee masters)
- Today's collection and receipts
- Quick action buttons for common tasks

### 2. Class Management
- Create, edit, and delete classes
- Filter by academic year
- View capacity and current strength
- Track available seats

### 3. Student Management
- Complete CRUD operations for students
- Advanced search with debouncing (min 3 characters)
- Filter by class
- Mobile validation (10 digits, starts with 6-9)
- Age validation (3-18 years)
- Student status tracking

### 4. Fee Master Management
- Manage different fee types (Tuition, Library, Computer, Sports, etc.)
- Set amount and frequency
- Activate/Deactivate fee masters
- Filter by type and active status

### 5. Fee Journal Management
- Create and track monthly fee dues
- Filter by month, year, and payment status
- Record payments
- Track pending, partial, and overdue payments

### 6. Fee Receipt Generation
- Generate receipts for fee payments
- Support multiple payment methods (Cash, Online, Cheque, Card)
- Select multiple months paid
- Filter receipts by date range and payment method

### 7. School Configuration
- Manage system configurations
- Support for different data types
- Categorize configurations
- Filter by category

## Installation

### Prerequisites
- Node.js 18+ and npm
- Backend API running on http://localhost:8080

### Setup Steps

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm run dev
   ```

4. **Access the application**
   - Open http://localhost:3000 in your browser
   - The Vite proxy will forward API requests to http://localhost:8080

## Available Scripts

- `npm run dev` - Start development server (port 3000)
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## Project Structure

```
frontend/
├── src/
│   ├── components/      # Reusable components
│   │   ├── common/      # Common components (Modal, Table, Loading, etc.)
│   │   ├── forms/       # Form input components
│   │   └── layout/      # Layout components
│   ├── contexts/        # React contexts (NotificationContext)
│   ├── hooks/           # Custom hooks (useFetch, useDebounce)
│   ├── pages/           # Page components
│   ├── services/        # API service layer
│   ├── utils/           # Utility functions
│   ├── App.jsx          # Main app component
│   └── main.jsx         # Entry point
├── vite.config.js       # Vite configuration with proxy
└── tailwind.config.js   # Tailwind configuration
```

## API Integration

The application communicates with the backend API at http://localhost:8080/api.

All API calls go through the service layer in `src/services/`:
- classService.js
- studentService.js
- feeMasterService.js
- feeJournalService.js
- feeReceiptService.js
- schoolConfigService.js

## Key Features

- **Responsive Design** - Works on all screen sizes
- **Form Validation** - Client-side validation matching backend rules
- **Error Handling** - User-friendly error messages
- **Loading States** - Visual feedback for all async operations
- **Notifications** - Toast notifications for success/error messages
- **Debounced Search** - Efficient search with automatic debouncing
- **Modal Dialogs** - Reusable modal components
- **Confirmation Dialogs** - Prevent accidental deletions

## Troubleshooting

### CORS Issues
- Ensure backend is running on port 8080
- Vite proxy should handle CORS automatically

### API Connection Issues
- Verify backend is running: http://localhost:8080/api/students
- Check network tab in browser dev tools

### Build Issues
```bash
rm -rf node_modules package-lock.json
npm install
```

## License

This project is part of the School Management System.
