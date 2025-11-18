import { cn } from '@/lib/utils';

interface BadgeProps {
  variant?: 'success' | 'danger' | 'warning' | 'info' | 'default';
  children: React.ReactNode;
  className?: string;
}

export default function Badge({ variant = 'default', children, className }: BadgeProps) {
  const variantStyles = {
    success: 'bg-success-100 text-success-800 border border-success-200',
    danger: 'bg-danger-100 text-danger-800 border border-danger-200',
    warning: 'bg-warning-100 text-warning-800 border border-warning-200',
    info: 'bg-primary-100 text-primary-800 border border-primary-200',
    default: 'bg-slate-100 text-slate-800 border border-slate-200',
  };

  return (
    <span
      className={cn(
        'inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold shadow-sm',
        variantStyles[variant],
        className
      )}
    >
      {children}
    </span>
  );
}
