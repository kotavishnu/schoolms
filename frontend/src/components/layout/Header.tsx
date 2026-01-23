import { Link, useLocation } from 'react-router-dom';
import { GraduationCap } from 'lucide-react';

export function Header() {
  const location = useLocation();

  const isActive = (path: string) => location.pathname === path;

  return (
    <header className="bg-white border-b border-gray-200 sticky top-0 z-50">
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 text-xl font-bold text-blue-600">
            <GraduationCap className="h-8 w-8" />
            <span>School Management</span>
          </Link>

          {/* Navigation Links */}
          <nav className="flex items-center gap-6">
            <Link
              to="/"
              className={`text-sm font-medium transition-colors hover:text-blue-600 ${
                isActive('/') ? 'text-blue-600' : 'text-gray-600'
              }`}
            >
              Home
            </Link>
            <Link
              to="/students"
              className={`text-sm font-medium transition-colors hover:text-blue-600 ${
                isActive('/students') ? 'text-blue-600' : 'text-gray-600'
              }`}
            >
              Students
            </Link>
            <Link
              to="/configurations"
              className={`text-sm font-medium transition-colors hover:text-blue-600 ${
                isActive('/configurations') ? 'text-blue-600' : 'text-gray-600'
              }`}
            >
              Configurations
            </Link>
          </nav>
        </div>
      </div>
    </header>
  );
}
