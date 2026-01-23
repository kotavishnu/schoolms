import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'sonner';
import { Layout } from '@/components/layout/Layout';
import { HomePage } from '@/pages/HomePage';
import { StudentsPage } from '@/pages/StudentsPage';
import { ConfigurationsPage } from '@/pages/ConfigurationsPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/students" element={<StudentsPage />} />
          <Route path="/configurations" element={<ConfigurationsPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
      <Toaster position="top-right" richColors />
    </BrowserRouter>
  );
}

export default App;
