import { Link, useLocation } from 'react-router-dom';
import { cn } from '@/shared/utils/cn';
import { ROUTES } from '@/shared/constants/routes';
import {
  Home,
  Users,
  BookOpen,
  DollarSign,
  CreditCard,
  FileText,
  BarChart3,
  Settings,
} from 'lucide-react';

interface NavItem {
  label: string;
  href: string;
  icon: React.ElementType;
}

const navigationItems: NavItem[] = [
  { label: 'Dashboard', href: ROUTES.DASHBOARD, icon: Home },
  { label: 'Students', href: ROUTES.STUDENTS, icon: Users },
  { label: 'Classes', href: ROUTES.CLASSES, icon: BookOpen },
  { label: 'Fees', href: ROUTES.FEES, icon: DollarSign },
  { label: 'Payments', href: ROUTES.PAYMENTS, icon: CreditCard },
  { label: 'Receipts', href: ROUTES.RECEIPTS, icon: FileText },
  { label: 'Reports', href: ROUTES.REPORTS, icon: BarChart3 },
  { label: 'Settings', href: ROUTES.CONFIG, icon: Settings },
];

export const Sidebar: React.FC = () => {
  const location = useLocation();

  const isActive = (href: string) => {
    if (href === '/') {
      return location.pathname === href;
    }
    return location.pathname.startsWith(href);
  };

  return (
    <aside className="w-64 bg-gray-900 text-white min-h-screen">
      <div className="p-6 border-b border-gray-700">
        <h1 className="text-xl font-bold">School SMS</h1>
        <p className="text-sm text-gray-400 mt-1">Management System</p>
      </div>

      <nav className="mt-6 space-y-1 px-3">
        {navigationItems.map((item) => {
          const Icon = item.icon;
          const active = isActive(item.href);

          return (
            <Link
              key={item.href}
              to={item.href}
              className={cn(
                'flex items-center gap-3 px-4 py-3 rounded-lg transition-colors',
                active
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-300 hover:bg-gray-800 hover:text-white'
              )}
            >
              <Icon className="h-5 w-5" />
              <span className="font-medium">{item.label}</span>
            </Link>
          );
        })}
      </nav>
    </aside>
  );
};
