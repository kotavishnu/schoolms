import React, { forwardRef, type InputHTMLAttributes, type TextareaHTMLAttributes } from 'react';

interface BaseInputProps {
  label?: string;
  error?: string;
  helperText?: string;
  required?: boolean;
}

interface InputProps extends BaseInputProps, InputHTMLAttributes<HTMLInputElement> {
  multiline?: false;
}

interface TextareaProps extends BaseInputProps, TextareaHTMLAttributes<HTMLTextAreaElement> {
  multiline: true;
}

type CombinedInputProps = InputProps | TextareaProps;

export const Input = forwardRef<HTMLInputElement | HTMLTextAreaElement, CombinedInputProps>(
  ({ label, error, helperText, multiline = false, required, className = '', ...props }, ref) => {
    const baseClasses =
      'w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 transition-colors';
    const errorClasses = error
      ? 'border-danger-500 focus:border-danger-500 focus:ring-danger-500'
      : 'border-gray-300 focus:border-primary-500 focus:ring-primary-500';

    const inputElement = multiline ? (
      <textarea
        ref={ref as React.Ref<HTMLTextAreaElement>}
        className={`${baseClasses} ${errorClasses} ${className}`}
        {...(props as TextareaHTMLAttributes<HTMLTextAreaElement>)}
      />
    ) : (
      <input
        ref={ref as React.Ref<HTMLInputElement>}
        className={`${baseClasses} ${errorClasses} ${className}`}
        {...(props as InputHTMLAttributes<HTMLInputElement>)}
      />
    );

    return (
      <div className="flex flex-col">
        {label && (
          <label className="mb-1 text-sm font-medium text-gray-700">
            {label}
            {required && <span className="text-danger-500 ml-1">*</span>}
          </label>
        )}

        {inputElement}

        {error && <span className="mt-1 text-sm text-danger-600">{error}</span>}
        {helperText && !error && <span className="mt-1 text-sm text-gray-500">{helperText}</span>}
      </div>
    );
  }
);

Input.displayName = 'Input';

export default Input;
