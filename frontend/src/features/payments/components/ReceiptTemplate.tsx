import React from 'react';
import type { Receipt } from '../types/payment.types';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';
import { cn } from '@/shared/utils/cn';

interface ReceiptTemplateProps {
  /** Receipt data */
  receipt: Receipt;
  /** Additional CSS classes */
  className?: string;
}

/**
 * Receipt Template Component
 * Displays a formatted receipt ready for printing or PDF generation
 */
export const ReceiptTemplate = React.forwardRef<HTMLDivElement, ReceiptTemplateProps>(
  ({ receipt, className }, ref) => {
    const { payment, school, student, academicYear } = receipt;

    return (
      <div
        ref={ref}
        className={cn('bg-white p-8 max-w-4xl mx-auto print:p-0', className)}
        style={{ minHeight: '11in' }}
      >
        {/* Header */}
        <div className="border-b-4 border-blue-600 pb-6 mb-6">
          <div className="flex items-start justify-between">
            {/* School Info */}
            <div className="flex-1">
              {school.logoUrl && (
                <img
                  src={school.logoUrl}
                  alt={`${school.name} logo`}
                  className="h-16 w-auto mb-3"
                />
              )}
              <h1 className="text-3xl font-bold text-gray-900 mb-2">{school.name}</h1>
              <p className="text-sm text-gray-600 whitespace-pre-line">{school.address}</p>
              <div className="flex gap-4 mt-2 text-sm text-gray-600">
                <span>Phone: {school.phone}</span>
                <span>Email: {school.email}</span>
              </div>
              {school.website && (
                <p className="text-sm text-gray-600">Website: {school.website}</p>
              )}
            </div>

            {/* Receipt Info */}
            <div className="text-right">
              <div className="bg-blue-600 text-white px-4 py-2 rounded-lg mb-3">
                <h2 className="text-xl font-bold">FEE RECEIPT</h2>
              </div>
              <div className="space-y-1 text-sm">
                <p>
                  <span className="font-semibold">Receipt No:</span>{' '}
                  <span className="text-blue-600 font-bold">{payment.receiptNumber}</span>
                </p>
                <p>
                  <span className="font-semibold">Date:</span>{' '}
                  {formatDate(payment.paymentDate)}
                </p>
                <p>
                  <span className="font-semibold">Academic Year:</span> {academicYear}
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Student Information */}
        <div className="mb-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-3 border-b border-gray-300 pb-2">
            Student Information
          </h3>
          <div className="grid grid-cols-2 gap-x-8 gap-y-2 text-sm">
            <div>
              <span className="text-gray-600">Student Name:</span>{' '}
              <span className="font-medium text-gray-900">{student.name}</span>
            </div>
            <div>
              <span className="text-gray-600">Student ID:</span>{' '}
              <span className="font-medium text-gray-900">{student.studentId}</span>
            </div>
            <div>
              <span className="text-gray-600">Class:</span>{' '}
              <span className="font-medium text-gray-900">
                {student.class} - {student.section}
              </span>
            </div>
            {student.rollNumber && (
              <div>
                <span className="text-gray-600">Roll Number:</span>{' '}
                <span className="font-medium text-gray-900">{student.rollNumber}</span>
              </div>
            )}
            <div>
              <span className="text-gray-600">Guardian Name:</span>{' '}
              <span className="font-medium text-gray-900">{student.guardianName}</span>
            </div>
            <div>
              <span className="text-gray-600">Guardian Phone:</span>{' '}
              <span className="font-medium text-gray-900">{student.guardianPhone}</span>
            </div>
          </div>
        </div>

        {/* Payment Details */}
        <div className="mb-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-3 border-b border-gray-300 pb-2">
            Payment Details
          </h3>

          {/* Fee Items Table */}
          <table className="w-full border-collapse mb-4">
            <thead>
              <tr className="bg-gray-100 border-b-2 border-gray-300">
                <th className="text-left p-3 text-sm font-semibold text-gray-700">
                  Fee Description
                </th>
                <th className="text-right p-3 text-sm font-semibold text-gray-700">
                  Amount Due
                </th>
                <th className="text-right p-3 text-sm font-semibold text-gray-700">
                  Amount Paid
                </th>
                <th className="text-right p-3 text-sm font-semibold text-gray-700">
                  Balance
                </th>
              </tr>
            </thead>
            <tbody>
              {payment.feeItems.map((item, index) => (
                <tr key={index} className="border-b border-gray-200">
                  <td className="p-3 text-sm text-gray-900">{item.feeName}</td>
                  <td className="p-3 text-sm text-right text-gray-900">
                    {formatCurrency(item.amountDue)}
                  </td>
                  <td className="p-3 text-sm text-right font-medium text-gray-900">
                    {formatCurrency(item.amountPaid)}
                  </td>
                  <td className="p-3 text-sm text-right text-gray-900">
                    {formatCurrency(item.remainingBalance)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Payment Summary */}
          <div className="bg-gray-50 rounded-lg p-4 space-y-2">
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">Previous Outstanding Balance:</span>
              <span className="font-medium text-gray-900">
                {formatCurrency(payment.previousBalance)}
              </span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-gray-600">Amount Paid Today:</span>
              <span className="font-medium text-gray-900">
                {formatCurrency(payment.totalAmount)}
              </span>
            </div>
            <div className="border-t-2 border-gray-300 pt-2 flex justify-between">
              <span className="text-lg font-semibold text-gray-900">
                Remaining Balance:
              </span>
              <span className="text-xl font-bold text-blue-600">
                {formatCurrency(payment.remainingBalance)}
              </span>
            </div>
          </div>
        </div>

        {/* Payment Method */}
        <div className="mb-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-3 border-b border-gray-300 pb-2">
            Payment Information
          </h3>
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span className="text-gray-600">Payment Method:</span>{' '}
              <span className="font-medium text-gray-900">{payment.paymentMethod}</span>
            </div>
            {payment.transactionReference && (
              <div>
                <span className="text-gray-600">
                  {payment.paymentMethod === 'Cheque' ? 'Cheque Number:' : 'Transaction Reference:'}
                </span>{' '}
                <span className="font-medium text-gray-900">{payment.transactionReference}</span>
              </div>
            )}
            {payment.notes && (
              <div className="col-span-2">
                <span className="text-gray-600">Notes:</span>{' '}
                <span className="text-gray-900">{payment.notes}</span>
              </div>
            )}
          </div>
        </div>

        {/* Total Amount Paid (Highlighted) */}
        <div className="bg-blue-50 border-2 border-blue-600 rounded-lg p-4 mb-8">
          <div className="flex justify-between items-center">
            <span className="text-xl font-semibold text-gray-900">Total Amount Paid:</span>
            <span className="text-3xl font-bold text-blue-600">
              {formatCurrency(payment.totalAmount)}
            </span>
          </div>
        </div>

        {/* Footer */}
        <div className="border-t-2 border-gray-300 pt-6">
          <div className="flex justify-between items-end">
            {/* Received By */}
            <div>
              <p className="text-sm text-gray-600 mb-1">Received By:</p>
              <p className="text-sm font-medium text-gray-900">
                {payment.createdByName || 'Office Staff'}
              </p>
              <div className="border-t border-gray-400 w-48 mt-8">
                <p className="text-xs text-gray-500 mt-1">Signature</p>
              </div>
            </div>

            {/* School Seal */}
            <div className="text-center">
              <div className="border-2 border-gray-400 rounded-full w-24 h-24 flex items-center justify-center mb-2">
                <span className="text-xs text-gray-500">School Seal</span>
              </div>
              <p className="text-xs text-gray-500">Official Seal</p>
            </div>
          </div>

          {/* Thank You Message */}
          <div className="text-center mt-8 pt-6 border-t border-gray-200">
            <p className="text-sm text-gray-600">
              Thank you for your payment. This is a computer-generated receipt.
            </p>
            <p className="text-xs text-gray-500 mt-1">
              For any queries, please contact the accounts department.
            </p>
          </div>
        </div>
      </div>
    );
  }
);

ReceiptTemplate.displayName = 'ReceiptTemplate';
