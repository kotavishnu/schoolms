import React, { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { ArrowLeft, RefreshCw, AlertCircle, CheckCircle, XCircle } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Input } from '@/shared/components/ui/Input';
import { Badge } from '@/shared/components/ui/Badge';
import { usePayment, useRefundPayment } from '../api/paymentApi';
import { refundFormSchema, type RefundFormValues } from '../schemas/payment.schema';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';
import toast from 'react-hot-toast';

/**
 * Refund Management Page
 * Allows users to initiate and manage refunds for payments
 */
export const RefundManagement: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const paymentIdParam = searchParams.get('paymentId');

  const [paymentId, setPaymentId] = useState<number>(
    paymentIdParam ? parseInt(paymentIdParam, 10) : 0
  );
  const [searchQuery, setSearchQuery] = useState('');

  // Fetch payment details
  const { data: payment, isLoading, error } = usePayment(paymentId);

  const refundMutation = useRefundPayment();

  // Form setup
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<RefundFormValues>({
    resolver: zodResolver(refundFormSchema),
    defaultValues: {
      paymentId: 0,
      refundAmount: 0,
      reason: '',
      isFullRefund: true,
    },
  });

  const isFullRefund = watch('isFullRefund');
  const refundAmount = watch('refundAmount');

  // Handle search for payment
  const handleSearchPayment = () => {
    // Mock search - replace with actual API call
    const mockPaymentId = parseInt(searchQuery, 10);
    if (mockPaymentId && !isNaN(mockPaymentId)) {
      setPaymentId(mockPaymentId);
    } else {
      toast.error('Please enter a valid receipt number');
    }
  };

  // Handle refund type toggle
  const handleRefundTypeChange = (full: boolean) => {
    setValue('isFullRefund', full);
    if (full && payment) {
      setValue('refundAmount', payment.totalAmount);
    } else {
      setValue('refundAmount', 0);
    }
  };

  // Submit refund request
  const onSubmit = async (data: RefundFormValues) => {
    try {
      await refundMutation.mutateAsync({
        paymentId: payment!.id,
        data: {
          ...data,
          paymentId: payment!.id,
        },
      });
      toast.success('Refund request submitted successfully');
      navigate('/payments/history');
    } catch (error) {
      // Error handled by mutation
      console.error('Refund error:', error);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-6 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="mb-6">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate('/payments/history')}
            className="mb-4"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to Payment History
          </Button>
          <h1 className="text-3xl font-bold text-gray-900">Refund Management</h1>
          <p className="mt-2 text-gray-600">
            Initiate and manage payment refunds
          </p>
        </div>

        {/* Payment Search */}
        {!paymentId && (
          <Card className="mb-6">
            <div className="p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">
                Find Payment to Refund
              </h2>
              <div className="flex gap-4">
                <Input
                  placeholder="Enter receipt number or payment ID"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleSearchPayment()}
                  className="flex-1"
                />
                <Button onClick={handleSearchPayment}>
                  Search
                </Button>
              </div>
            </div>
          </Card>
        )}

        {/* Payment Details */}
        {paymentId && (
          <>
            {isLoading ? (
              <Card>
                <div className="p-12 text-center">
                  <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                  <p className="text-gray-500">Loading payment details...</p>
                </div>
              </Card>
            ) : error || !payment ? (
              <Card>
                <div className="p-12 text-center">
                  <AlertCircle className="w-16 h-16 mx-auto mb-4 text-red-500" />
                  <h2 className="text-2xl font-bold text-gray-900 mb-2">Payment Not Found</h2>
                  <p className="text-gray-600 mb-6">
                    {(error as any)?.message || 'The payment you are looking for does not exist.'}
                  </p>
                  <Button onClick={() => setPaymentId(0)}>
                    Search Again
                  </Button>
                </div>
              </Card>
            ) : (
              <>
                {/* Payment Information */}
                <Card className="mb-6">
                  <div className="p-6">
                    <div className="flex justify-between items-start mb-6">
                      <div>
                        <h2 className="text-xl font-semibold text-gray-900 mb-2">
                          Payment Details
                        </h2>
                        <Badge variant="success">{payment.status}</Badge>
                      </div>
                      <Button variant="ghost" size="sm" onClick={() => setPaymentId(0)}>
                        Change Payment
                      </Button>
                    </div>

                    <div className="grid grid-cols-2 gap-6">
                      <div>
                        <p className="text-sm text-gray-600 mb-1">Receipt Number</p>
                        <p className="font-semibold text-gray-900">{payment.receiptNumber}</p>
                      </div>
                      <div>
                        <p className="text-sm text-gray-600 mb-1">Payment Date</p>
                        <p className="font-semibold text-gray-900">
                          {formatDate(payment.paymentDate)}
                        </p>
                      </div>
                      <div>
                        <p className="text-sm text-gray-600 mb-1">Student</p>
                        <p className="font-semibold text-gray-900">{payment.studentName}</p>
                        <p className="text-sm text-gray-600">{payment.studentClass}</p>
                      </div>
                      <div>
                        <p className="text-sm text-gray-600 mb-1">Amount Paid</p>
                        <p className="text-2xl font-bold text-blue-600">
                          {formatCurrency(payment.totalAmount)}
                        </p>
                      </div>
                      <div>
                        <p className="text-sm text-gray-600 mb-1">Payment Method</p>
                        <p className="font-semibold text-gray-900">{payment.paymentMethod}</p>
                      </div>
                      {payment.transactionReference && (
                        <div>
                          <p className="text-sm text-gray-600 mb-1">Transaction Reference</p>
                          <p className="font-semibold text-gray-900">
                            {payment.transactionReference}
                          </p>
                        </div>
                      )}
                    </div>

                    {/* Fee Items */}
                    <div className="mt-6 pt-6 border-t border-gray-200">
                      <h3 className="text-sm font-medium text-gray-700 mb-3">Fees Paid</h3>
                      <div className="space-y-2">
                        {payment.feeItems.map((item, index) => (
                          <div
                            key={index}
                            className="flex justify-between items-center text-sm"
                          >
                            <span className="text-gray-700">{item.feeName}</span>
                            <span className="font-medium text-gray-900">
                              {formatCurrency(item.amountPaid)}
                            </span>
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>
                </Card>

                {/* Refund Form */}
                {payment.status === 'Completed' ? (
                  <form onSubmit={handleSubmit(onSubmit)}>
                    <Card>
                      <div className="p-6 space-y-6">
                        <h2 className="text-xl font-semibold text-gray-900">
                          Refund Information
                        </h2>

                        {/* Refund Type Selection */}
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-3">
                            Refund Type
                            <span className="text-red-500 ml-1">*</span>
                          </label>
                          <div className="flex gap-4">
                            <button
                              type="button"
                              onClick={() => handleRefundTypeChange(true)}
                              className={`flex-1 p-4 border-2 rounded-lg transition-all ${
                                isFullRefund
                                  ? 'border-blue-600 bg-blue-50 text-blue-700'
                                  : 'border-gray-300 bg-white text-gray-700 hover:border-gray-400'
                              }`}
                            >
                              <CheckCircle className="w-6 h-6 mx-auto mb-2" />
                              <p className="font-medium">Full Refund</p>
                              <p className="text-sm mt-1">
                                Refund entire amount: {formatCurrency(payment.totalAmount)}
                              </p>
                            </button>
                            <button
                              type="button"
                              onClick={() => handleRefundTypeChange(false)}
                              className={`flex-1 p-4 border-2 rounded-lg transition-all ${
                                !isFullRefund
                                  ? 'border-blue-600 bg-blue-50 text-blue-700'
                                  : 'border-gray-300 bg-white text-gray-700 hover:border-gray-400'
                              }`}
                            >
                              <XCircle className="w-6 h-6 mx-auto mb-2" />
                              <p className="font-medium">Partial Refund</p>
                              <p className="text-sm mt-1">Refund a specific amount</p>
                            </button>
                          </div>
                        </div>

                        {/* Refund Amount (for partial refunds) */}
                        {!isFullRefund && (
                          <Input
                            type="number"
                            label="Refund Amount"
                            {...register('refundAmount', { valueAsNumber: true })}
                            error={errors.refundAmount?.message}
                            required
                            min={0.01}
                            max={payment.totalAmount}
                            step={0.01}
                            helperText={`Maximum refundable: ${formatCurrency(payment.totalAmount)}`}
                          />
                        )}

                        {/* Refund Reason */}
                        <div>
                          <label
                            htmlFor="reason"
                            className="block text-sm font-medium text-gray-700 mb-1"
                          >
                            Reason for Refund
                            <span className="text-red-500 ml-1">*</span>
                          </label>
                          <textarea
                            id="reason"
                            {...register('reason')}
                            rows={4}
                            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors"
                            placeholder="Please provide a detailed reason for this refund request (minimum 10 characters)"
                            required
                          />
                          {errors.reason && (
                            <p className="mt-1 text-sm text-red-500">{errors.reason.message}</p>
                          )}
                        </div>

                        {/* Refund Summary */}
                        <div className="bg-blue-50 border-2 border-blue-200 rounded-lg p-4">
                          <h3 className="font-semibold text-gray-900 mb-2">Refund Summary</h3>
                          <div className="space-y-2">
                            <div className="flex justify-between text-sm">
                              <span className="text-gray-600">Original Payment</span>
                              <span className="font-medium text-gray-900">
                                {formatCurrency(payment.totalAmount)}
                              </span>
                            </div>
                            <div className="flex justify-between text-sm">
                              <span className="text-gray-600">Refund Amount</span>
                              <span className="font-medium text-gray-900">
                                {formatCurrency(
                                  isFullRefund ? payment.totalAmount : refundAmount
                                )}
                              </span>
                            </div>
                            <div className="border-t border-blue-300 pt-2 flex justify-between">
                              <span className="font-semibold text-gray-900">
                                {isFullRefund ? 'Full Refund' : 'Partial Refund'}
                              </span>
                              <span className="text-lg font-bold text-blue-600">
                                - {formatCurrency(
                                  isFullRefund ? payment.totalAmount : refundAmount
                                )}
                              </span>
                            </div>
                          </div>
                        </div>

                        {/* Warning */}
                        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 flex items-start gap-3">
                          <AlertCircle className="w-5 h-5 text-yellow-600 mt-0.5 flex-shrink-0" />
                          <div className="text-sm text-yellow-800">
                            <p className="font-medium mb-1">Important Note</p>
                            <p>
                              This refund request will be submitted for approval. Once approved,
                              the refund will be processed and the payment status will be updated
                              accordingly.
                            </p>
                          </div>
                        </div>
                      </div>
                    </Card>

                    {/* Submit Button */}
                    <div className="mt-6 flex justify-end gap-4">
                      <Button
                        type="button"
                        variant="outline"
                        onClick={() => navigate('/payments/history')}
                        disabled={isSubmitting}
                      >
                        Cancel
                      </Button>
                      <Button
                        type="submit"
                        disabled={isSubmitting}
                        className="min-w-[200px]"
                      >
                        {isSubmitting ? (
                          <span className="flex items-center gap-2">
                            <span className="animate-spin">⏳</span>
                            Submitting...
                          </span>
                        ) : (
                          <span className="flex items-center gap-2">
                            <RefreshCw className="w-4 h-4" />
                            Submit Refund Request
                          </span>
                        )}
                      </Button>
                    </div>
                  </form>
                ) : (
                  <Card>
                    <div className="p-12 text-center">
                      <AlertCircle className="w-16 h-16 mx-auto mb-4 text-yellow-500" />
                      <h2 className="text-2xl font-bold text-gray-900 mb-2">
                        Refund Not Available
                      </h2>
                      <p className="text-gray-600 mb-6">
                        This payment cannot be refunded because its status is "{payment.status}".
                        Only completed payments can be refunded.
                      </p>
                      <Button onClick={() => navigate('/payments/history')}>
                        Back to Payment History
                      </Button>
                    </div>
                  </Card>
                )}
              </>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default RefundManagement;
