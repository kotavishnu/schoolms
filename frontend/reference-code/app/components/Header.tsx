import { Link, useLocation } from 'react-router-dom';
import { GraduationCap, Moon } from 'lucide-react';

export function Header() {
  const location = useLocation();

  const isActive = (path: string) => location.pathname === path;

  return (
    <header className="bg-white border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-blue-600 rounded flex items-center justify-center">
              <GraduationCap className="w-5 h-5 text-white" />
            </div>
            <span className="text-lg text-gray-900">School Management</span>
          </div>

          <nav className="flex items-center gap-8">
            <Link
              to="/"
              className={`text-sm ${
                isActive('/') ? 'text-blue-600' : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              Home
            </Link>
            <Link
              to="/students"
              className={`text-sm ${
                isActive('/students') ? 'text-blue-600' : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              Students
            </Link>
            <Link
              to="/configurations"
              className={`text-sm ${
                isActive('/configurations') ? 'text-blue-600' : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              Configurations
            </Link>
            <button className="p-2 hover:bg-gray-100 rounded">
              <Moon className="w-5 h-5 text-gray-600" />
            </button>
          </nav>
        </div>
      </div>
    </header>
  );
}
