import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Calendar,
  DollarSign,
  TrendingUp,
  Receipt as ReceiptIcon,
  Eye,
  Download,
} from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';
import { Input } from '@/shared/components/ui/Input';
import { useStudentPayments, useStudentFeeSummary } from '../api/paymentApi';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';
import { ROUTES } from '@/shared/constants/routes';

/**
 * Student Payment History Page
 * Displays complete payment history for a specific student
 */
export const StudentPaymentHistory: React.FC = () => {
  const { studentId } = useParams<{ studentId: string }>();
  const navigate = useNavigate();
  const [dateFilter, setDateFilter] = useState({ startDate: '', endDate: '' });

  const studentIdNum = studentId ? parseInt(studentId, 10) : 0;

  // Fetch student payments and fee summary
  const { data: payments, isLoading: isLoadingPayments, error: paymentsError } = useStudentPayments(studentIdNum);
  const { data: feeSummary, isLoading: isLoadingFees } = useStudentFeeSummary(studentIdNum);

  const isLoading = isLoadingPayments || isLoadingFees;

  // Filter payments by date range
  const filteredPayments = payments?.filter((payment) => {
    if (!dateFilter.startDate && !dateFilter.endDate) return true;

    const paymentDate = new Date(payment.paymentDate);
    const start = dateFilter.startDate ? new Date(dateFilter.startDate) : null;
    const end = dateFilter.endDate ? new Date(dateFilter.endDate) : null;

    if (start && paymentDate < start) return false;
    if (end && paymentDate > end) return false;

    return true;
  });

  // Calculate statistics
  const totalPaid = filteredPayments?.reduce((sum, p) => sum + p.totalAmount, 0) || 0;
  const paymentCount = filteredPayments?.length || 0;
  const averagePayment = paymentCount > 0 ? totalPaid / paymentCount : 0;

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 py-6 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
            <p className="text-gray-500">Loading payment history...</p>
          </div>
        </div>
      </div>
    );
  }

  if (paymentsError || !payments || !feeSummary) {
    return (
      <div className="min-h-screen bg-gray-50 py-6 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <Card>
            <div className="p-12 text-center">
              <h2 className="text-2xl font-bold text-gray-900 mb-2">
                Error Loading Payment History
              </h2>
              <p className="text-gray-600 mb-6">
                {(paymentsError as any)?.message || 'Failed to load student payment history'}
              </p>
              <Button onClick={() => navigate(ROUTES.STUDENTS)}>
                <ArrowLeft className="w-4 h-4 mr-2" />
                Back to Students
              </Button>
            </div>
          </Card>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-6 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-6">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate(`/students/${studentId}`)}
            className="mb-4"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to Student Details
          </Button>
          <div className="flex justify-between items-start">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">{feeSummary.studentName}</h1>
              <p className="mt-2 text-gray-600">
                {feeSummary.studentClass} • Payment History & Fee Summary
              </p>
            </div>
            <Button onClick={() => navigate(`/payments/record?studentId=${studentId}`)}>
              <DollarSign className="w-4 h-4 mr-2" />
              Record New Payment
            </Button>
          </div>
        </div>

        {/* Fee Summary Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
          <Card>
            <div className="p-6">
              <div className="flex items-center justify-between mb-2">
                <h3 className="text-sm font-medium text-gray-600">Total Fees</h3>
                <DollarSign className="w-5 h-5 text-gray-400" />
              </div>
              <p className="text-2xl font-bold text-gray-900">
                {formatCurrency(feeSummary.totalFees)}
              </p>
            </div>
          </Card>

          <Card>
            <div className="p-6">
              <div className="flex items-center justify-between mb-2">
                <h3 className="text-sm font-medium text-gray-600">Total Paid</h3>
                <TrendingUp className="w-5 h-5 text-green-500" />
              </div>
              <p className="text-2xl font-bold text-green-600">
                {formatCurrency(feeSummary.totalPaid)}
              </p>
            </div>
          </Card>

          <Card>
            <div className="p-6">
              <div className="flex items-center justify-between mb-2">
                <h3 className="text-sm font-medium text-gray-600">Outstanding</h3>
                <DollarSign className="w-5 h-5 text-orange-500" />
              </div>
              <p className="text-2xl font-bold text-orange-600">
                {formatCurrency(feeSummary.totalOutstanding)}
              </p>
            </div>
          </Card>

          <Card>
            <div className="p-6">
              <div className="flex items-center justify-between mb-2">
                <h3 className="text-sm font-medium text-gray-600">Payments Made</h3>
                <ReceiptIcon className="w-5 h-5 text-blue-500" />
              </div>
              <p className="text-2xl font-bold text-gray-900">{paymentCount}</p>
              <p className="text-xs text-gray-500 mt-1">
                Avg: {formatCurrency(averagePayment)}
              </p>
            </div>
          </Card>
        </div>

        {/* Pending Fees */}
        {feeSummary.pendingFees.length > 0 && (
          <Card className="mb-6">
            <div className="p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Pending Fees</h2>
              <div className="space-y-3">
                {feeSummary.pendingFees.map((fee) => (
                  <div
                    key={fee.id}
                    className="flex items-center justify-between p-4 bg-orange-50 border border-orange-200 rounded-lg"
                  >
                    <div className="flex-1">
                      <h3 className="font-medium text-gray-900">{fee.feeTypeName}</h3>
                      <div className="flex items-center gap-4 mt-1 text-sm text-gray-600">
                        <span>Total: {formatCurrency(fee.totalAmount)}</span>
                        <span>•</span>
                        <span>Paid: {formatCurrency(fee.amountPaid)}</span>
                        <span>•</span>
                        <span className="font-semibold text-orange-700">
                          Pending: {formatCurrency(fee.amountPending)}
                        </span>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="text-sm text-gray-600">Due: {formatDate(fee.dueDate)}</p>
                      {fee.isOverdue && (
                        <Badge variant="error" className="mt-1">
                          Overdue
                        </Badge>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </Card>
        )}

        {/* Payment History */}
        <Card>
          <div className="p-6">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-semibold text-gray-900">Payment Timeline</h2>

              {/* Date Filter */}
              <div className="flex gap-2">
                <Input
                  type="date"
                  value={dateFilter.startDate}
                  onChange={(e) =>
                    setDateFilter((prev) => ({ ...prev, startDate: e.target.value }))
                  }
                  placeholder="From"
                  className="w-40"
                />
                <Input
                  type="date"
                  value={dateFilter.endDate}
                  onChange={(e) =>
                    setDateFilter((prev) => ({ ...prev, endDate: e.target.value }))
                  }
                  placeholder="To"
                  className="w-40"
                />
                {(dateFilter.startDate || dateFilter.endDate) && (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setDateFilter({ startDate: '', endDate: '' })}
                  >
                    Clear
                  </Button>
                )}
              </div>
            </div>

            {filteredPayments && filteredPayments.length > 0 ? (
              <div className="relative">
                {/* Timeline Line */}
                <div className="absolute left-8 top-0 bottom-0 w-0.5 bg-gray-300"></div>

                {/* Timeline Items */}
                <div className="space-y-6">
                  {filteredPayments.map((payment, index) => (
                    <div key={payment.id} className="relative flex items-start gap-6">
                      {/* Timeline Dot */}
                      <div className="flex-shrink-0 w-16 flex justify-center">
                        <div className="w-4 h-4 rounded-full bg-blue-600 border-4 border-white shadow-md z-10"></div>
                      </div>

                      {/* Payment Card */}
                      <Card className="flex-1">
                        <div className="p-4">
                          <div className="flex justify-between items-start">
                            <div className="flex-1">
                              <div className="flex items-center gap-3 mb-2">
                                <h3 className="font-semibold text-gray-900">
                                  {payment.receiptNumber}
                                </h3>
                                <Badge
                                  variant={
                                    payment.status === 'Completed'
                                      ? 'success'
                                      : payment.status === 'Refunded'
                                      ? 'info'
                                      : 'warning'
                                  }
                                >
                                  {payment.status}
                                </Badge>
                              </div>

                              <div className="flex items-center gap-4 text-sm text-gray-600">
                                <span className="flex items-center gap-1">
                                  <Calendar className="w-4 h-4" />
                                  {formatDate(payment.paymentDate)}
                                </span>
                                <span>•</span>
                                <span>{payment.paymentMethod}</span>
                                {payment.transactionReference && (
                                  <>
                                    <span>•</span>
                                    <span>Ref: {payment.transactionReference}</span>
                                  </>
                                )}
                              </div>

                              {/* Fee Items */}
                              <div className="mt-3">
                                <p className="text-sm font-medium text-gray-700 mb-2">
                                  Fees Paid:
                                </p>
                                <div className="flex flex-wrap gap-2">
                                  {payment.feeItems.map((item, idx) => (
                                    <span
                                      key={idx}
                                      className="inline-flex items-center px-2 py-1 rounded-md bg-gray-100 text-xs text-gray-700"
                                    >
                                      {item.feeName}: {formatCurrency(item.amountPaid)}
                                    </span>
                                  ))}
                                </div>
                              </div>

                              {payment.notes && (
                                <p className="mt-2 text-sm text-gray-600 italic">
                                  Note: {payment.notes}
                                </p>
                              )}
                            </div>

                            <div className="flex flex-col items-end gap-2 ml-4">
                              <p className="text-2xl font-bold text-blue-600">
                                {formatCurrency(payment.totalAmount)}
                              </p>
                              <div className="flex gap-2">
                                <Button
                                  variant="outline"
                                  size="sm"
                                  onClick={() => navigate(`/payments/${payment.id}/receipt`)}
                                >
                                  <Eye className="w-4 h-4 mr-1" />
                                  View
                                </Button>
                                <Button
                                  variant="outline"
                                  size="sm"
                                  onClick={() => window.open(`/payments/${payment.id}/receipt?print=true`, '_blank')}
                                >
                                  <Download className="w-4 h-4" />
                                </Button>
                              </div>
                            </div>
                          </div>
                        </div>
                      </Card>
                    </div>
                  ))}
                </div>
              </div>
            ) : (
              <div className="text-center py-12 text-gray-500">
                <ReceiptIcon className="w-12 h-12 mx-auto mb-2 text-gray-400" />
                <p className="text-lg font-medium">No payments found</p>
                <p className="text-sm mt-2">
                  {dateFilter.startDate || dateFilter.endDate
                    ? 'Try adjusting the date range'
                    : 'No payment history available for this student'}
                </p>
              </div>
            )}
          </div>
        </Card>
      </div>
    </div>
  );
};

export default StudentPaymentHistory;
