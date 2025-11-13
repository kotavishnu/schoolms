import React from 'react';
import type { PaymentMethod } from '../types/payment.types';
import { cn } from '@/shared/utils/cn';
import { CreditCard, Banknote, Building2, Smartphone, FileText } from 'lucide-react';

interface PaymentMethodSelectorProps {
  /** Currently selected payment method */
  value: PaymentMethod | '';
  /** Callback when payment method is selected */
  onChange: (method: PaymentMethod) => void;
  /** Error message */
  error?: string;
  /** Whether the selector is disabled */
  disabled?: boolean;
}

/**
 * Payment Method Selector Component
 * Displays payment methods as selectable cards with icons
 */
export const PaymentMethodSelector: React.FC<PaymentMethodSelectorProps> = ({
  value,
  onChange,
  error,
  disabled = false,
}) => {
  const paymentMethods: { method: PaymentMethod; icon: React.ElementType; label: string }[] = [
    { method: 'Cash', icon: Banknote, label: 'Cash' },
    { method: 'Card', icon: CreditCard, label: 'Card' },
    { method: 'Bank Transfer', icon: Building2, label: 'Bank Transfer' },
    { method: 'UPI', icon: Smartphone, label: 'UPI' },
    { method: 'Cheque', icon: FileText, label: 'Cheque' },
  ];

  return (
    <div className="w-full">
      <label className="block text-sm font-medium text-gray-700 mb-2">
        Payment Method
        <span className="text-red-500 ml-1">*</span>
      </label>

      <div className="grid grid-cols-2 md:grid-cols-5 gap-3">
        {paymentMethods.map(({ method, icon: Icon, label }) => (
          <button
            key={method}
            type="button"
            onClick={() => !disabled && onChange(method)}
            disabled={disabled}
            className={cn(
              'flex flex-col items-center justify-center p-4 rounded-lg border-2 transition-all',
              'focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2',
              'disabled:opacity-50 disabled:cursor-not-allowed',
              value === method
                ? 'border-blue-600 bg-blue-50 text-blue-700'
                : 'border-gray-300 bg-white text-gray-700 hover:border-gray-400 hover:bg-gray-50'
            )}
            aria-pressed={value === method}
            aria-label={`Select ${label} payment method`}
          >
            <Icon className="w-6 h-6 mb-2" />
            <span className="text-sm font-medium">{label}</span>
          </button>
        ))}
      </div>

      {error && (
        <p className="mt-2 text-sm text-red-500" role="alert">
          {error}
        </p>
      )}
    </div>
  );
};
