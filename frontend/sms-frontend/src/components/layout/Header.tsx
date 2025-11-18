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
    <header className="bg-white shadow-md border-b border-slate-200 backdrop-blur-sm bg-white/95 sticky top-0 z-50">
      <div className="container mx-auto px-4 max-w-7xl">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center space-x-8">
            <Link to="/" className="flex items-center space-x-3 group">
              <div className="w-10 h-10 bg-gradient-to-br from-primary-600 to-primary-700 rounded-xl flex items-center justify-center shadow-lg shadow-primary-500/30 group-hover:shadow-primary-500/50 transition-all duration-300 group-hover:scale-105">
                <span className="text-white font-bold text-xl">S</span>
              </div>
              <div className="flex flex-col">
                <span className="text-xl font-bold text-slate-900 leading-tight">School Management</span>
                <span className="text-xs text-slate-500">Student Information System</span>
              </div>
            </Link>

            <nav className="hidden md:flex space-x-2">
              {navItems.map((item) => (
                <Link
                  key={item.path}
                  to={item.path}
                  className={cn(
                    'px-4 py-2 rounded-lg text-sm font-semibold transition-all duration-200',
                    isActive(item.path)
                      ? 'bg-gradient-to-r from-primary-600 to-primary-700 text-white shadow-md shadow-primary-500/30'
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  )}
                >
                  {item.label}
                </Link>
              ))}
            </nav>
          </div>

          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2 bg-slate-100 px-4 py-2 rounded-lg">
              <div className="w-2 h-2 bg-success-500 rounded-full animate-pulse"></div>
              <span className="text-sm font-medium text-slate-700">ADMIN</span>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
}
