import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Filter,
  Download,
  Eye,
  Printer,
  RefreshCw,
  ChevronLeft,
  ChevronRight,
  Plus,
} from 'lucide-react';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Select } from '@/shared/components/ui/Select';
import { Badge } from '@/shared/components/ui/Badge';
import { Table } from '@/shared/components/ui/Table';
import { usePayments, paymentApi } from '../api/paymentApi';
import type { PaymentFilters, PaymentStatus } from '../types/payment.types';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';
import toast from 'react-hot-toast';

/**
 * Payment History Page
 * Displays list of all payments with filters and actions
 */
export const PaymentHistory: React.FC = () => {
  const navigate = useNavigate();

  // Filter state
  const [filters, setFilters] = useState<PaymentFilters>({
    search: '',
    paymentMethod: undefined,
    status: undefined,
    startDate: '',
    endDate: '',
    page: 1,
    limit: 20,
  });

  const [showFilters, setShowFilters] = useState(false);

  // Fetch payments with filters
  const { data, isLoading, error, refetch } = usePayments(filters);

  // Handle filter changes
  const handleSearchChange = (value: string) => {
    setFilters((prev) => ({ ...prev, search: value, page: 1 }));
  };

  const handleFilterChange = (key: keyof PaymentFilters, value: any) => {
    setFilters((prev) => ({ ...prev, [key]: value, page: 1 }));
  };

  const handleClearFilters = () => {
    setFilters({
      search: '',
      paymentMethod: undefined,
      status: undefined,
      startDate: '',
      endDate: '',
      page: 1,
      limit: 20,
    });
  };

  const handlePageChange = (newPage: number) => {
    setFilters((prev) => ({ ...prev, page: newPage }));
  };

  // Export payments
  const handleExport = async (format: 'excel' | 'pdf') => {
    try {
      toast.loading(`Exporting to ${format.toUpperCase()}...`);
      const blob = await paymentApi.exportPayments({ format, filters });

      // Create download link
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `payments_${new Date().toISOString().split('T')[0]}.${format}`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);

      toast.dismiss();
      toast.success(`Exported successfully!`);
    } catch (error) {
      toast.dismiss();
      toast.error('Export failed. Please try again.');
    }
  };

  // Get badge variant based on status
  const getStatusBadge = (status: PaymentStatus) => {
    switch (status) {
      case 'Completed':
        return <Badge variant="success">{status}</Badge>;
      case 'Pending':
        return <Badge variant="warning">{status}</Badge>;
      case 'Failed':
        return <Badge variant="error">{status}</Badge>;
      case 'Refunded':
        return <Badge variant="info">{status}</Badge>;
      case 'Partially Refunded':
        return <Badge variant="warning">{status}</Badge>;
      default:
        return <Badge>{status}</Badge>;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-6 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-6">
          <div className="flex justify-between items-start">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Payment History</h1>
              <p className="mt-2 text-gray-600">View and manage all payment records</p>
            </div>
            <Button onClick={() => navigate('/payments/record')}>
              <Plus className="w-4 h-4 mr-2" />
              Record Payment
            </Button>
          </div>
        </div>

        {/* Search and Filters */}
        <Card className="mb-6">
          <div className="p-6">
            <div className="flex flex-col md:flex-row gap-4">
              {/* Search */}
              <div className="flex-1">
                <Input
                  placeholder="Search by student name or receipt number..."
                  value={filters.search}
                  onChange={(e) => handleSearchChange(e.target.value)}
                  
                />
              </div>

              {/* Filter Toggle */}
              <Button
                variant="outline"
                onClick={() => setShowFilters(!showFilters)}
                className="md:w-auto"
              >
                <Filter className="w-4 h-4 mr-2" />
                {showFilters ? 'Hide' : 'Show'} Filters
              </Button>

              {/* Export */}
              <div className="flex gap-2">
                <Button variant="outline" onClick={() => handleExport('excel')}>
                  <Download className="w-4 h-4 mr-2" />
                  Excel
                </Button>
                <Button variant="outline" onClick={() => handleExport('pdf')}>
                  <Download className="w-4 h-4 mr-2" />
                  PDF
                </Button>
              </div>

              {/* Refresh */}
              <Button variant="outline" onClick={() => refetch()}>
                <RefreshCw className="w-4 h-4" />
              </Button>
            </div>

            {/* Advanced Filters */}
            {showFilters && (
              <div className="mt-4 pt-4 border-t border-gray-200">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                  {/* Payment Method Filter */}
                  <Select
                    label="Payment Method"
                    value={filters.paymentMethod || ''}
                    onChange={(e) =>
                      handleFilterChange(
                        'paymentMethod',
                        e.target.value || undefined
                      )
                    }
                  >
                    <option value="">All Methods</option>
                    <option value="Cash">Cash</option>
                    <option value="Card">Card</option>
                    <option value="Bank Transfer">Bank Transfer</option>
                    <option value="UPI">UPI</option>
                    <option value="Cheque">Cheque</option>
                  </Select>

                  {/* Status Filter */}
                  <Select
                    label="Status"
                    value={filters.status || ''}
                    onChange={(e) =>
                      handleFilterChange('status', e.target.value || undefined)
                    }
                  >
                    <option value="">All Statuses</option>
                    <option value="Completed">Completed</option>
                    <option value="Pending">Pending</option>
                    <option value="Failed">Failed</option>
                    <option value="Refunded">Refunded</option>
                    <option value="Partially Refunded">Partially Refunded</option>
                  </Select>

                  {/* Start Date */}
                  <Input
                    type="date"
                    label="From Date"
                    value={filters.startDate}
                    onChange={(e) => handleFilterChange('startDate', e.target.value)}
                  />

                  {/* End Date */}
                  <Input
                    type="date"
                    label="To Date"
                    value={filters.endDate}
                    onChange={(e) => handleFilterChange('endDate', e.target.value)}
                  />
                </div>

                <div className="mt-4 flex justify-end">
                  <Button variant="ghost" onClick={handleClearFilters}>
                    Clear Filters
                  </Button>
                </div>
              </div>
            )}
          </div>
        </Card>

        {/* Payments Table */}
        <Card>
          <div className="overflow-x-auto">
            {isLoading ? (
              <div className="text-center py-12 text-gray-500">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                <p>Loading payments...</p>
              </div>
            ) : error ? (
              <div className="text-center py-12 text-red-500">
                <p className="font-medium">Error loading payments</p>
                <p className="text-sm mt-2">
                  {(error as any)?.message || 'Failed to load payments'}
                </p>
                <Button variant="outline" onClick={() => refetch()} className="mt-4">
                  Try Again
                </Button>
              </div>
            ) : !data || data.payments.length === 0 ? (
              <div className="text-center py-12 text-gray-500">
                <p className="text-lg font-medium">No payments found</p>
                <p className="text-sm mt-2">
                  {filters.search || filters.paymentMethod || filters.status
                    ? 'Try adjusting your filters'
                    : 'Record your first payment to get started'}
                </p>
              </div>
            ) : (
              <>
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50 border-b border-gray-200">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Receipt No
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Date
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Student
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Class
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Amount
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Method
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Status
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {data.payments.map((payment) => (
                      <tr key={payment.id} className="hover:bg-gray-50 transition-colors">
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-blue-600">
                          {payment.receiptNumber}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {formatDate(payment.paymentDate)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {payment.studentName}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                          {payment.studentClass}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-right font-semibold text-gray-900">
                          {formatCurrency(payment.totalAmount)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                          {payment.paymentMethod}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          {getStatusBadge(payment.status)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm">
                          <div className="flex items-center justify-end gap-2">
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => navigate(`/payments/${payment.id}/receipt`)}
                              title="View Receipt"
                            >
                              <Eye className="w-4 h-4" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => window.open(`/payments/${payment.id}/receipt?print=true`, '_blank')}
                              title="Print Receipt"
                            >
                              <Printer className="w-4 h-4" />
                            </Button>
                            {payment.status === 'Completed' && (
                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => navigate(`/payments/refunds?paymentId=${payment.id}`)}
                                title="Refund"
                              >
                                <RefreshCw className="w-4 h-4" />
                              </Button>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>

                {/* Pagination */}
                <div className="bg-white px-6 py-4 border-t border-gray-200">
                  <div className="flex items-center justify-between">
                    <div className="text-sm text-gray-700">
                      Showing{' '}
                      <span className="font-medium">
                        {(data.page - 1) * data.limit + 1}
                      </span>{' '}
                      to{' '}
                      <span className="font-medium">
                        {Math.min(data.page * data.limit, data.total)}
                      </span>{' '}
                      of <span className="font-medium">{data.total}</span> results
                    </div>

                    <div className="flex items-center gap-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handlePageChange(data.page - 1)}
                        disabled={data.page === 1}
                      >
                        <ChevronLeft className="w-4 h-4" />
                        Previous
                      </Button>

                      <div className="flex items-center gap-1">
                        {Array.from({ length: data.totalPages }, (_, i) => i + 1)
                          .filter((page) => {
                            // Show first page, last page, current page, and pages around current
                            return (
                              page === 1 ||
                              page === data.totalPages ||
                              Math.abs(page - data.page) <= 1
                            );
                          })
                          .map((page, index, array) => (
                            <React.Fragment key={page}>
                              {index > 0 && array[index - 1] !== page - 1 && (
                                <span className="px-2 text-gray-500">...</span>
                              )}
                              <Button
                                variant={page === data.page ? 'primary' : 'outline'}
                                size="sm"
                                onClick={() => handlePageChange(page)}
                                className="min-w-[40px]"
                              >
                                {page}
                              </Button>
                            </React.Fragment>
                          ))}
                      </div>

                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handlePageChange(data.page + 1)}
                        disabled={data.page === data.totalPages}
                      >
                        Next
                        <ChevronRight className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>
                </div>
              </>
            )}
          </div>
        </Card>
      </div>
    </div>
  );
};

export default PaymentHistory;
