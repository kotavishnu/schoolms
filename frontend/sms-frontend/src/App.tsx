import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastProvider } from '@/components/ui/Toast';
import MainLayout from '@/components/layout/MainLayout';
import StudentListPage from '@/pages/students/StudentListPage';
import CreateStudentPage from '@/pages/students/CreateStudentPage';
import StudentDetailsPage from '@/pages/students/StudentDetailsPage';
import EditStudentPage from '@/pages/students/EditStudentPage';
import ConfigurationPage from '@/pages/config/ConfigurationPage';
import NotFoundPage from '@/pages/NotFoundPage';

// Create a client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5000,
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ToastProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<MainLayout />}>
              <Route index element={<Navigate to="/students" replace />} />
              <Route path="students" element={<StudentListPage />} />
              <Route path="students/new" element={<CreateStudentPage />} />
              <Route path="students/:id" element={<StudentDetailsPage />} />
              <Route path="students/:id/edit" element={<EditStudentPage />} />
              <Route path="configuration" element={<ConfigurationPage />} />
              <Route path="*" element={<NotFoundPage />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </ToastProvider>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  );
}

export default App;
