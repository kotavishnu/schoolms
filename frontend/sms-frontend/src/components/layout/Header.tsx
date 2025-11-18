import { Link, useLocation } from 'react-router-dom';
import { cn } from '@/lib/utils';

export default function Header() {
  const location = useLocation();

  const navItems = [
    { label: 'Students', path: '/students' },
    { label: 'Configuration', path: '/configuration' },
  ];

  const isActive = (path: string) => location.pathname.startsWith(path);

  return (
    <header className="bg-white shadow-sm border-b">
      <div className="container mx-auto px-4 max-w-7xl">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center space-x-8">
            <Link to="/" className="flex items-center space-x-2">
              <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
                <span className="text-white font-bold text-lg">S</span>
              </div>
              <span className="text-xl font-bold text-gray-900">School Management</span>
            </Link>

            <nav className="hidden md:flex space-x-1">
              {navItems.map((item) => (
                <Link
                  key={item.path}
                  to={item.path}
                  className={cn(
                    'px-4 py-2 rounded-md text-sm font-medium transition-colors',
                    isActive(item.path)
                      ? 'bg-blue-100 text-blue-700'
                      : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'
                  )}
                >
                  {item.label}
                </Link>
              ))}
            </nav>
          </div>

          <div className="flex items-center space-x-4">
            <span className="text-sm text-gray-600">User: ADMIN</span>
          </div>
        </div>
      </div>
    </header>
  );
}
