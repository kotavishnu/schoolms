import React from 'react';
import type { PendingFee } from '../types/payment.types';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';
import { Input } from '@/shared/components/ui/Input';
import { Badge } from '@/shared/components/ui/Badge';
import { cn } from '@/shared/utils/cn';
import { AlertCircle } from 'lucide-react';

interface SelectedFeeItem {
  feeId: number;
  amountPaid: number;
}

interface FeeItemSelectorProps {
  /** List of pending fees for the student */
  pendingFees: PendingFee[];
  /** Currently selected fee items */
  selectedFees: SelectedFeeItem[];
  /** Callback when fee selection changes */
  onChange: (selectedFees: SelectedFeeItem[]) => void;
  /** Error message */
  error?: string;
  /** Whether the selector is disabled */
  disabled?: boolean;
}

/**
 * Fee Item Selector Component
 * Allows selecting pending fees and specifying payment amounts
 */
export const FeeItemSelector: React.FC<FeeItemSelectorProps> = ({
  pendingFees,
  selectedFees,
  onChange,
  error,
  disabled = false,
}) => {
  const isSelected = (feeId: number): boolean => {
    return selectedFees.some((item) => item.feeId === feeId);
  };

  const getSelectedAmount = (feeId: number): number => {
    const item = selectedFees.find((item) => item.feeId === feeId);
    return item?.amountPaid || 0;
  };

  const handleToggleFee = (fee: PendingFee): void => {
    if (isSelected(fee.id)) {
      // Remove from selection
      onChange(selectedFees.filter((item) => item.feeId !== fee.id));
    } else {
      // Add to selection with full pending amount
      onChange([...selectedFees, { feeId: fee.id, amountPaid: fee.amountPending }]);
    }
  };

  const handleAmountChange = (feeId: number, amount: string): void => {
    const numAmount = parseFloat(amount) || 0;
    const updatedFees = selectedFees.map((item) =>
      item.feeId === feeId ? { ...item, amountPaid: numAmount } : item
    );
    onChange(updatedFees);
  };

  if (pendingFees.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        <AlertCircle className="w-12 h-12 mx-auto mb-2 text-gray-400" />
        <p className="text-lg font-medium">No pending fees</p>
        <p className="text-sm">This student has no outstanding fee payments.</p>
      </div>
    );
  }

  return (
    <div className="w-full">
      <label className="block text-sm font-medium text-gray-700 mb-2">
        Select Fees to Pay
        <span className="text-red-500 ml-1">*</span>
      </label>

      <div className="space-y-3">
        {pendingFees.map((fee) => {
          const selected = isSelected(fee.id);
          const amount = getSelectedAmount(fee.id);

          return (
            <div
              key={fee.id}
              className={cn(
                'border-2 rounded-lg p-4 transition-all',
                selected ? 'border-blue-600 bg-blue-50' : 'border-gray-300 bg-white'
              )}
            >
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-start gap-3 flex-1">
                  <input
                    type="checkbox"
                    id={`fee-${fee.id}`}
                    checked={selected}
                    onChange={() => handleToggleFee(fee)}
                    disabled={disabled}
                    className={cn(
                      'w-5 h-5 text-blue-600 border-gray-300 rounded',
                      'focus:ring-2 focus:ring-blue-500 mt-0.5',
                      'disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer'
                    )}
                    aria-label={`Select ${fee.feeTypeName}`}
                  />

                  <div className="flex-1">
                    <label
                      htmlFor={`fee-${fee.id}`}
                      className="font-medium text-gray-900 cursor-pointer"
                    >
                      {fee.feeTypeName}
                    </label>
                    <div className="flex items-center gap-2 mt-1 text-sm text-gray-600">
                      <span>Total: {formatCurrency(fee.totalAmount)}</span>
                      <span>•</span>
                      <span>Paid: {formatCurrency(fee.amountPaid)}</span>
                      <span>•</span>
                      <span className="font-semibold text-gray-900">
                        Pending: {formatCurrency(fee.amountPending)}
                      </span>
                    </div>
                    <div className="flex items-center gap-2 mt-1">
                      <span className="text-sm text-gray-600">
                        Due: {formatDate(fee.dueDate)}
                      </span>
                      {fee.isOverdue && (
                        <Badge variant="error">
                          Overdue
                        </Badge>
                      )}
                    </div>
                  </div>
                </div>
              </div>

              {selected && (
                <div className="ml-8">
                  <Input
                    type="number"
                    label="Amount to Pay"
                    value={amount}
                    onChange={(e) => handleAmountChange(fee.id, e.target.value)}
                    min={0.01}
                    max={fee.amountPending}
                    step={0.01}
                    disabled={disabled}
                    required
                    helperText={`Maximum: ${formatCurrency(fee.amountPending)}`}
                    className="max-w-xs"
                  />
                </div>
              )}
            </div>
          );
        })}
      </div>

      {error && (
        <p className="mt-2 text-sm text-red-500" role="alert">
          {error}
        </p>
      )}
    </div>
  );
};
