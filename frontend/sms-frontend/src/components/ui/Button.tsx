import React from 'react';
import { cn } from '@/lib/utils';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'default' | 'destructive' | 'outline' | 'secondary' | 'ghost' | 'link';
  size?: 'default' | 'sm' | 'lg' | 'icon';
  isLoading?: boolean;
}

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant = 'default', size = 'default', isLoading, children, disabled, ...props }, ref) => {
    const variantStyles = {
      default: 'bg-gradient-to-r from-primary-600 to-primary-700 text-white hover:from-primary-700 hover:to-primary-800 shadow-md hover:shadow-lg active:scale-95',
      destructive: 'bg-gradient-to-r from-danger-600 to-danger-700 text-white hover:from-danger-700 hover:to-danger-800 shadow-md hover:shadow-lg active:scale-95',
      outline: 'border-2 border-slate-300 bg-white hover:bg-slate-50 hover:border-primary-500 text-slate-700 hover:text-primary-700 active:scale-95',
      secondary: 'bg-gradient-to-r from-secondary-600 to-secondary-700 text-white hover:from-secondary-700 hover:to-secondary-800 shadow-md hover:shadow-lg active:scale-95',
      ghost: 'hover:bg-slate-100 hover:text-slate-900 active:scale-95',
      link: 'text-primary-600 underline-offset-4 hover:underline hover:text-primary-700',
    };

    const sizeStyles = {
      default: 'h-11 px-6 py-2 text-sm',
      sm: 'h-9 rounded-lg px-4 text-xs',
      lg: 'h-12 rounded-lg px-8 text-base',
      icon: 'h-10 w-10',
    };

    return (
      <button
        className={cn(
          'inline-flex items-center justify-center rounded-lg font-semibold transition-all duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50',
          variantStyles[variant],
          sizeStyles[size],
          className
        )}
        ref={ref}
        disabled={disabled || isLoading}
        {...props}
      >
        {isLoading ? (
          <>
            <svg className="mr-2 h-4 w-4 animate-spin" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Loading...
          </>
        ) : (
          children
        )}
      </button>
    );
  }
);

Button.displayName = 'Button';

export { Button };
