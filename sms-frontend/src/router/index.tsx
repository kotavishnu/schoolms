import { createBrowserRouter } from 'react-router-dom';
import MainLayout from '@/components/layout/MainLayout';
import HomePage from '@/pages/HomePage';
import StudentsPage from '@/pages/StudentsPage';
import StudentDetailPage from '@/pages/StudentDetailPage';
import CreateStudentPage from '@/pages/CreateStudentPage';
import EditStudentPage from '@/pages/EditStudentPage';
import ConfigurationsPage from '@/pages/ConfigurationsPage';
import NotFoundPage from '@/pages/NotFoundPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      {
        index: true,
        element: <HomePage />,
      },
      {
        path: 'students',
        children: [
          {
            index: true,
            element: <StudentsPage />,
          },
          {
            path: 'create',
            element: <CreateStudentPage />,
          },
          {
            path: ':id',
            element: <StudentDetailPage />,
          },
          {
            path: ':id/edit',
            element: <EditStudentPage />,
          },
        ],
      },
      {
        path: 'configurations',
        element: <ConfigurationsPage />,
      },
      {
        path: '*',
        element: <NotFoundPage />,
      },
    ],
  },
]);

export default router;
