import React from 'react';
import type { PendingFee } from '../types/payment.types';
import { formatCurrency } from '@/shared/utils/formatters';
import { cn } from '@/shared/utils/cn';

interface SelectedFeeItem {
  feeId: number;
  amountPaid: number;
}

interface PaymentSummaryProps {
  /** List of pending fees */
  pendingFees: PendingFee[];
  /** Selected fees with amounts */
  selectedFees: SelectedFeeItem[];
  /** Previous outstanding balance */
  previousBalance?: number;
  /** Additional CSS classes */
  className?: string;
}

/**
 * Payment Summary Component
 * Displays a summary of selected fees and total payment amount
 */
export const PaymentSummary: React.FC<PaymentSummaryProps> = ({
  pendingFees,
  selectedFees,
  previousBalance = 0,
  className,
}) => {
  // Calculate total payment amount
  const totalAmount = selectedFees.reduce((sum, item) => sum + item.amountPaid, 0);

  // Calculate remaining balance after payment
  const remainingBalance = previousBalance - totalAmount;

  // Get fee details for selected fees
  const selectedFeeDetails = selectedFees.map((selected) => {
    const fee = pendingFees.find((f) => f.id === selected.feeId);
    return {
      name: fee?.feeTypeName || 'Unknown Fee',
      amountPaid: selected.amountPaid,
      remaining: (fee?.amountPending || 0) - selected.amountPaid,
    };
  });

  if (selectedFees.length === 0) {
    return (
      <div className={cn('bg-gray-50 rounded-lg p-6 border border-gray-200', className)}>
        <h3 className="text-lg font-semibold text-gray-900 mb-2">Payment Summary</h3>
        <p className="text-gray-500">No fees selected for payment</p>
      </div>
    );
  }

  return (
    <div className={cn('bg-white rounded-lg border-2 border-gray-300 shadow-sm', className)}>
      <div className="bg-gray-50 px-6 py-4 border-b border-gray-300">
        <h3 className="text-lg font-semibold text-gray-900">Payment Summary</h3>
      </div>

      <div className="p-6">
        {/* Selected Fees Breakdown */}
        <div className="space-y-3 mb-4">
          <h4 className="text-sm font-medium text-gray-700 mb-2">Selected Fees</h4>
          {selectedFeeDetails.map((fee, index) => (
            <div key={index} className="flex justify-between items-center text-sm">
              <span className="text-gray-700">{fee.name}</span>
              <div className="text-right">
                <div className="font-medium text-gray-900">{formatCurrency(fee.amountPaid)}</div>
                {fee.remaining > 0 && (
                  <div className="text-xs text-gray-500">
                    Remaining: {formatCurrency(fee.remaining)}
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>

        {/* Divider */}
        <div className="border-t border-gray-300 my-4"></div>

        {/* Balance Information */}
        <div className="space-y-2">
          <div className="flex justify-between items-center text-sm">
            <span className="text-gray-600">Previous Outstanding</span>
            <span className="font-medium text-gray-900">
              {formatCurrency(previousBalance)}
            </span>
          </div>

          <div className="flex justify-between items-center text-sm">
            <span className="text-gray-600">Payment Amount</span>
            <span className="font-medium text-gray-900">- {formatCurrency(totalAmount)}</span>
          </div>
        </div>

        {/* Divider */}
        <div className="border-t-2 border-gray-400 my-4"></div>

        {/* Total Amount */}
        <div className="flex justify-between items-center">
          <span className="text-lg font-semibold text-gray-900">Total to Pay</span>
          <span className="text-2xl font-bold text-blue-600">{formatCurrency(totalAmount)}</span>
        </div>

        {/* Remaining Balance */}
        <div className="mt-4 p-3 bg-blue-50 rounded-lg">
          <div className="flex justify-between items-center">
            <span className="text-sm font-medium text-gray-700">
              Balance After Payment
            </span>
            <span
              className={cn(
                'text-lg font-bold',
                remainingBalance > 0 ? 'text-orange-600' : 'text-green-600'
              )}
            >
              {formatCurrency(Math.max(0, remainingBalance))}
            </span>
          </div>
          {remainingBalance <= 0 && (
            <p className="text-xs text-green-600 mt-1">All fees will be paid</p>
          )}
        </div>
      </div>
    </div>
  );
};
