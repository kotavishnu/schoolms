import React from 'react';
import { cn } from '@/lib/utils';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  error?: string;
  label?: string;
}

const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, type, error, label, ...props }, ref) => {
    return (
      <div className="w-full">
        {label && (
          <label className="block text-sm font-semibold text-slate-700 mb-2">
            {label}
            {props.required && <span className="text-danger-500 ml-1">*</span>}
          </label>
        )}
        <input
          type={type}
          className={cn(
            'flex h-11 w-full rounded-lg border-2 border-slate-200 bg-white px-4 py-2 text-sm text-slate-900 transition-colors placeholder:text-slate-400 hover:border-slate-300 focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/20 disabled:cursor-not-allowed disabled:opacity-50 disabled:bg-slate-50',
            error && 'border-danger-500 focus:border-danger-500 focus:ring-danger-500/20',
            className
          )}
          ref={ref}
          {...props}
        />
        {error && <p className="mt-2 text-sm text-danger-600 font-medium">{error}</p>}
      </div>
    );
  }
);

Input.displayName = 'Input';

export { Input };
