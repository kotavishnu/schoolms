import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { NotificationProvider } from './contexts/NotificationContext';
import Layout from './components/layout/Layout';
import Dashboard from './pages/Dashboard';
import ClassManagement from './pages/ClassManagement';
import StudentManagement from './pages/StudentManagement';
import FeeMasterManagement from './pages/FeeMasterManagement';
import FeeJournalManagement from './pages/FeeJournalManagement';
import FeeReceiptManagement from './pages/FeeReceiptManagement';
import SchoolConfig from './pages/SchoolConfig';

function App() {
  return (
    <NotificationProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Dashboard />} />
            <Route path="classes" element={<ClassManagement />} />
            <Route path="students" element={<StudentManagement />} />
            <Route path="fee-masters" element={<FeeMasterManagement />} />
            <Route path="fee-journals" element={<FeeJournalManagement />} />
            <Route path="fee-receipts" element={<FeeReceiptManagement />} />
            <Route path="config" element={<SchoolConfig />} />
          </Route>
        </Routes>
      </Router>
    </NotificationProvider>
  );
}

export default App;
