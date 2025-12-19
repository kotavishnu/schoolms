import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import Layout from './components/Layout';
import Home from './pages/Home';
import StudentRegistration from './pages/StudentRegistration';
import ClassManagement from './pages/ClassManagement';
import FeeMaster from './pages/FeeMaster';
import FeeReceipt from './pages/FeeReceipt';
import ParentPortal from './pages/ParentPortal';
import SchoolConfig from './pages/SchoolConfig';

function App() {
  return (
    <Router>
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 3000,
          style: {
            background: '#363636',
            color: '#fff',
          },
          success: {
            duration: 3000,
            iconTheme: {
              primary: '#4ade80',
              secondary: '#fff',
            },
          },
          error: {
            duration: 4000,
            iconTheme: {
              primary: '#ef4444',
              secondary: '#fff',
            },
          },
        }}
      />
      <Layout>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/students" element={<StudentRegistration />} />
          <Route path="/classes" element={<ClassManagement />} />
          <Route path="/fee-master" element={<FeeMaster />} />
          <Route path="/fee-receipt" element={<FeeReceipt />} />
          <Route path="/parent-portal" element={<ParentPortal />} />
          <Route path="/school-config" element={<SchoolConfig />} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
