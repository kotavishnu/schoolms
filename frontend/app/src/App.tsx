import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'sonner';
import { ErrorBoundary } from './components/common/ErrorBoundary';
import { Layout } from './components/layout/Layout';
import { HomePage } from './pages/HomePage';
import { StudentsPage } from './pages/StudentsPage';
import { ConfigurationsPage } from './pages/ConfigurationsPage';

function App() {
  return (
    <ErrorBoundary>
      <BrowserRouter>
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
          <Routes>
            <Route path="/" element={<Layout />}>
              <Route index element={<HomePage />} />
              <Route path="students" element={<StudentsPage />} />
              <Route path="configurations" element={<ConfigurationsPage />} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Route>
          </Routes>
          <Toaster
            position="top-right"
            expand={false}
            richColors
            closeButton
            duration={4000}
          />
        </div>
      </BrowserRouter>
    </ErrorBoundary>
  );
}

export default App;
