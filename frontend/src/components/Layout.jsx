import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Layout = ({ children }) => {
  const location = useLocation();

  const navLinks = [
    { path: '/', label: 'Home' },
    { path: '/school-config', label: 'School Config' },
    { path: '/students', label: 'Students' },
    { path: '/classes', label: 'Classes' },
    { path: '/fee-master', label: 'Fee Master' },
    { path: '/fee-receipt', label: 'Fee Receipt' },
    { path: '/parent-portal', label: 'Parent Portal' },
  ];

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Navigation Bar */}
      <nav className="bg-blue-600 text-white shadow-lg">
        <div className="container mx-auto px-4">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-bold">School Management System</h1>
            </div>
            <div className="hidden md:flex space-x-4">
              {navLinks.map((link) => (
                <Link
                  key={link.path}
                  to={link.path}
                  className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                    location.pathname === link.path
                      ? 'bg-blue-700'
                      : 'hover:bg-blue-500'
                  }`}
                >
                  {link.label}
                </Link>
              ))}
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-8">
        {children}
      </main>

      {/* Footer */}
      <footer className="bg-gray-800 text-white mt-auto">
        <div className="container mx-auto px-4 py-4 text-center">
          <p>&copy; 2025 School Management System. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
};

export default Layout;
