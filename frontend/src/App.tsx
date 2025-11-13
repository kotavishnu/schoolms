import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'react-hot-toast';
import { queryClient } from './api/queryClient';
import { LoginForm } from './features/auth/components/LoginForm';
import { ProtectedRoute } from './features/auth/components/ProtectedRoute';
import { Layout } from './shared/components/layout/Layout';
import { Dashboard } from './features/dashboard/components/Dashboard';
import { StudentList } from './features/students/pages/StudentList';
import { StudentRegistration } from './features/students/pages/StudentRegistration';
import { StudentDetails } from './features/students/pages/StudentDetails';
import { StudentEdit } from './features/students/pages/StudentEdit';
import { ClassList } from './features/classes/pages/ClassList';
import { ClassCreate } from './features/classes/pages/ClassCreate';
import { ClassDetails } from './features/classes/pages/ClassDetails';
import { ClassEdit } from './features/classes/pages/ClassEdit';
import { FeeStructureList } from './features/fees/pages/FeeStructureList';
import { FeeStructureCreate } from './features/fees/pages/FeeStructureCreate';
import { FeeStructureDetails } from './features/fees/pages/FeeStructureDetails';
import { FeeStructureEdit } from './features/fees/pages/FeeStructureEdit';
import { StudentFeeAssignment } from './features/fees/pages/StudentFeeAssignment';
import { FeeDashboard } from './features/fees/pages/FeeDashboard';
import { PaymentRecording } from './features/payments/pages/PaymentRecording';
import { PaymentHistory } from './features/payments/pages/PaymentHistory';
import { PaymentReceipt } from './features/payments/pages/PaymentReceipt';
import { StudentPaymentHistory } from './features/payments/pages/StudentPaymentHistory';
import { RefundManagement } from './features/payments/pages/RefundManagement';
import { PaymentDashboard } from './features/payments/pages/PaymentDashboard';
import { ROUTES } from './shared/constants/routes';

/**
 * Main application component with routing
 */
function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          {/* Public routes */}
          <Route path={ROUTES.LOGIN} element={<LoginForm />} />

          {/* Protected routes */}
          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path={ROUTES.DASHBOARD} element={<Dashboard />} />

              {/* Student routes */}
              <Route path={ROUTES.STUDENTS} element={<StudentList />} />
              <Route path={ROUTES.STUDENT_NEW} element={<StudentRegistration />} />
              <Route path={ROUTES.STUDENT_DETAILS} element={<StudentDetails />} />
              <Route path={ROUTES.STUDENT_EDIT} element={<StudentEdit />} />

              {/* Class routes */}
              <Route path={ROUTES.CLASSES} element={<ClassList />} />
              <Route path={ROUTES.CLASS_NEW} element={<ClassCreate />} />
              <Route path={ROUTES.CLASS_DETAILS} element={<ClassDetails />} />
              <Route path="/classes/:classId/edit" element={<ClassEdit />} />

              {/* Fee routes */}
              <Route path={ROUTES.FEE_STRUCTURES} element={<FeeStructureList />} />
              <Route path="/fees/structures/new" element={<FeeStructureCreate />} />
              <Route path="/fees/structures/:id" element={<FeeStructureDetails />} />
              <Route path="/fees/structures/:id/edit" element={<FeeStructureEdit />} />
              <Route path="/fees/assign" element={<StudentFeeAssignment />} />
              <Route path="/fees/dashboard" element={<FeeDashboard />} />

              {/* Payment routes */}
              <Route path="/payments/record" element={<PaymentRecording />} />
              <Route path="/payments/history" element={<PaymentHistory />} />
              <Route path="/payments/:id/receipt" element={<PaymentReceipt />} />
              <Route path="/payments/student/:studentId" element={<StudentPaymentHistory />} />
              <Route path="/payments/refunds" element={<RefundManagement />} />
              <Route path="/payments/dashboard" element={<PaymentDashboard />} />

              {/* Receipt routes - redirect to payment history */}
              <Route
                path={ROUTES.RECEIPTS}
                element={<Navigate to="/payments/history" replace />}
              />

              {/* Report routes - placeholders */}
              <Route
                path={ROUTES.REPORTS}
                element={<div className="text-center py-12">Reports Module Coming Soon</div>}
              />

              {/* Config routes - placeholders */}
              <Route
                path={ROUTES.CONFIG}
                element={<div className="text-center py-12">Configuration Module Coming Soon</div>}
              />

              {/* Unauthorized page */}
              <Route
                path={ROUTES.UNAUTHORIZED}
                element={
                  <div className="text-center py-12">
                    <h1 className="text-2xl font-bold text-red-600">Unauthorized</h1>
                    <p className="text-gray-600 mt-2">
                      You do not have permission to access this resource.
                    </p>
                  </div>
                }
              />
            </Route>
          </Route>

          {/* Catch all - redirect to dashboard */}
          <Route path="*" element={<Navigate to={ROUTES.DASHBOARD} replace />} />
        </Routes>
      </BrowserRouter>

      {/* Toast notifications */}
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: '#363636',
            color: '#fff',
          },
          success: {
            duration: 3000,
            iconTheme: {
              primary: '#10b981',
              secondary: '#fff',
            },
          },
          error: {
            duration: 5000,
            iconTheme: {
              primary: '#ef4444',
              secondary: '#fff',
            },
          },
        }}
      />
    </QueryClientProvider>
  );
}

export default App;
