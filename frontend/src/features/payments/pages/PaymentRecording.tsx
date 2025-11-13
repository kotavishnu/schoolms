import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Search, ArrowLeft, Receipt as ReceiptIcon, AlertCircle } from 'lucide-react';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { PaymentMethodSelector } from '../components/PaymentMethodSelector';
import { FeeItemSelector } from '../components/FeeItemSelector';
import { PaymentSummary } from '../components/PaymentSummary';
import { paymentFormSchema, type PaymentFormValues } from '../schemas/payment.schema';
import { useCreatePayment, useStudentFeeSummary } from '../api/paymentApi';
import { ROUTES } from '@/shared/constants/routes';
import { formatCurrency } from '@/shared/utils/formatters';
import toast from 'react-hot-toast';

/**
 * Payment Recording Page
 * Allows users to record fee payments for students
 */
export const PaymentRecording: React.FC = () => {
  const navigate = useNavigate();
  const [studentId, setStudentId] = useState<number | null>(null);
  const [studentSearchQuery, setStudentSearchQuery] = useState('');
  const [showStudentSearch, setShowStudentSearch] = useState(true);

  // Fetch student fee summary when student is selected
  const {
    data: feeSummary,
    isLoading: isLoadingFees,
    error: feesError,
  } = useStudentFeeSummary(studentId!);

  const createPaymentMutation = useCreatePayment();

  // Form setup
  const {
    control,
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<PaymentFormValues>({
    resolver: zodResolver(paymentFormSchema),
    defaultValues: {
      studentId: 0,
      feeItems: [],
      paymentDate: new Date().toISOString().split('T')[0],
      paymentMethod: '' as any,
      transactionReference: '',
      notes: '',
    },
  });

  const selectedFees = watch('feeItems');
  const paymentMethod = watch('paymentMethod') as string | undefined;

  // Update studentId in form when selected
  useEffect(() => {
    if (studentId) {
      setValue('studentId', studentId);
    }
  }, [studentId, setValue]);

  // Mock student search - replace with actual API call
  const mockStudents = [
    { id: 1, name: 'John Doe', class: 'Class 5 - A', rollNumber: '501' },
    { id: 2, name: 'Jane Smith', class: 'Class 6 - B', rollNumber: '602' },
    { id: 3, name: 'Mike Johnson', class: 'Class 7 - A', rollNumber: '703' },
  ];

  const filteredStudents = mockStudents.filter(
    (student) =>
      student.name.toLowerCase().includes(studentSearchQuery.toLowerCase()) ||
      student.rollNumber.includes(studentSearchQuery)
  );

  const handleStudentSelect = (id: number) => {
    setStudentId(id);
    setShowStudentSearch(false);
  };

  const handleChangeStudent = () => {
    setStudentId(null);
    setShowStudentSearch(true);
    setValue('feeItems', []);
  };

  const onSubmit = async (data: PaymentFormValues) => {
    try {
      const result = await createPaymentMutation.mutateAsync(data);
      toast.success('Payment recorded successfully!');
      // Navigate to receipt page
      navigate(`/payments/${result.payment.id}/receipt`);
    } catch (error) {
      // Error handled by mutation
      console.error('Payment error:', error);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-6 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-6">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate(ROUTES.PAYMENTS)}
            className="mb-4"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to Payments
          </Button>
          <h1 className="text-3xl font-bold text-gray-900">Record Payment</h1>
          <p className="mt-2 text-gray-600">
            Record fee payment for a student and generate receipt
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left Column - Student Selection & Payment Form */}
          <div className="lg:col-span-2 space-y-6">
            {/* Student Selection */}
            <Card>
              <div className="p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4">
                  Student Information
                </h2>

                {showStudentSearch ? (
                  <div>
                    <Input
                      label="Search Student"
                      placeholder="Enter student name or roll number"
                      value={studentSearchQuery}
                      onChange={(e) => setStudentSearchQuery(e.target.value)}
                      
                    />

                    {studentSearchQuery && (
                      <div className="mt-4 border border-gray-300 rounded-lg max-h-64 overflow-y-auto">
                        {filteredStudents.length > 0 ? (
                          filteredStudents.map((student) => (
                            <button
                              key={student.id}
                              type="button"
                              onClick={() => handleStudentSelect(student.id)}
                              className="w-full px-4 py-3 text-left hover:bg-blue-50 border-b border-gray-200 last:border-b-0 transition-colors"
                            >
                              <div className="font-medium text-gray-900">{student.name}</div>
                              <div className="text-sm text-gray-600">
                                {student.class} • Roll: {student.rollNumber}
                              </div>
                            </button>
                          ))
                        ) : (
                          <div className="px-4 py-6 text-center text-gray-500">
                            No students found
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                ) : (
                  <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                    {feeSummary && (
                      <div>
                        <div className="flex justify-between items-start mb-4">
                          <div>
                            <h3 className="text-lg font-semibold text-gray-900">
                              {feeSummary.studentName}
                            </h3>
                            <p className="text-sm text-gray-600">{feeSummary.studentClass}</p>
                          </div>
                          <Button variant="ghost" size="sm" onClick={handleChangeStudent}>
                            Change Student
                          </Button>
                        </div>

                        <div className="grid grid-cols-3 gap-4">
                          <div>
                            <p className="text-xs text-gray-600 mb-1">Total Fees</p>
                            <p className="text-lg font-semibold text-gray-900">
                              {formatCurrency(feeSummary.totalFees)}
                            </p>
                          </div>
                          <div>
                            <p className="text-xs text-gray-600 mb-1">Paid</p>
                            <p className="text-lg font-semibold text-green-600">
                              {formatCurrency(feeSummary.totalPaid)}
                            </p>
                          </div>
                          <div>
                            <p className="text-xs text-gray-600 mb-1">Outstanding</p>
                            <p className="text-lg font-semibold text-orange-600">
                              {formatCurrency(feeSummary.totalOutstanding)}
                            </p>
                          </div>
                        </div>
                      </div>
                    )}
                  </div>
                )}
              </div>
            </Card>

            {/* Payment Form */}
            {studentId && feeSummary && (
              <form onSubmit={handleSubmit(onSubmit)}>
                <Card>
                  <div className="p-6 space-y-6">
                    <h2 className="text-xl font-semibold text-gray-900">Payment Details</h2>

                    {/* Fee Items Selection */}
                    {isLoadingFees ? (
                      <div className="text-center py-8 text-gray-500">
                        Loading fees...
                      </div>
                    ) : feesError ? (
                      <div className="bg-red-50 border border-red-200 rounded-lg p-4 flex items-start gap-3">
                        <AlertCircle className="w-5 h-5 text-red-500 mt-0.5" />
                        <div>
                          <h3 className="text-sm font-medium text-red-800">
                            Error loading fees
                          </h3>
                          <p className="text-sm text-red-600 mt-1">
                            {(feesError as any)?.message || 'Failed to load student fees'}
                          </p>
                        </div>
                      </div>
                    ) : (
                      <Controller
                        name="feeItems"
                        control={control}
                        render={({ field }) => (
                          <FeeItemSelector
                            pendingFees={feeSummary.pendingFees}
                            selectedFees={field.value}
                            onChange={field.onChange}
                            error={errors.feeItems?.message}
                            disabled={isSubmitting}
                          />
                        )}
                      />
                    )}

                    {/* Payment Date */}
                    <Input
                      type="date"
                      label="Payment Date"
                      {...register('paymentDate')}
                      error={errors.paymentDate?.message}
                      required
                      max={new Date().toISOString().split('T')[0]}
                    />

                    {/* Payment Method */}
                    <Controller
                      name="paymentMethod"
                      control={control}
                      render={({ field }) => (
                        <PaymentMethodSelector
                          value={field.value}
                          onChange={field.onChange}
                          error={errors.paymentMethod?.message}
                          disabled={isSubmitting}
                        />
                      )}
                    />

                    {/* Transaction Reference (for non-cash payments) */}
                    {paymentMethod && paymentMethod !== 'Cash' && (
                      <Input
                        label={
                          paymentMethod === 'Cheque'
                            ? 'Cheque Number'
                            : 'Transaction Reference'
                        }
                        placeholder={
                          paymentMethod === 'Cheque'
                            ? 'Enter cheque number'
                            : 'Enter transaction reference/ID'
                        }
                        {...register('transactionReference')}
                        error={errors.transactionReference?.message}
                        required={paymentMethod !== 'Cash'}
                      />
                    )}

                    {/* Notes */}
                    <div>
                      <label
                        htmlFor="notes"
                        className="block text-sm font-medium text-gray-700 mb-1"
                      >
                        Notes (Optional)
                      </label>
                      <textarea
                        id="notes"
                        {...register('notes')}
                        rows={3}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors"
                        placeholder="Add any additional notes about this payment"
                      />
                      {errors.notes && (
                        <p className="mt-1 text-sm text-red-500">{errors.notes.message}</p>
                      )}
                    </div>
                  </div>
                </Card>

                {/* Submit Button */}
                <div className="mt-6 flex justify-end gap-4">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => navigate(ROUTES.PAYMENTS)}
                    disabled={isSubmitting}
                  >
                    Cancel
                  </Button>
                  <Button
                    type="submit"
                    disabled={
                      isSubmitting || selectedFees.length === 0 || !paymentMethod
                    }
                    className="min-w-[200px]"
                  >
                    {isSubmitting ? (
                      <span className="flex items-center gap-2">
                        <span className="animate-spin">⏳</span>
                        Processing...
                      </span>
                    ) : (
                      <span className="flex items-center gap-2">
                        <ReceiptIcon className="w-4 h-4" />
                        Record Payment & Generate Receipt
                      </span>
                    )}
                  </Button>
                </div>
              </form>
            )}
          </div>

          {/* Right Column - Payment Summary */}
          {studentId && feeSummary && selectedFees.length > 0 && (
            <div className="lg:col-span-1">
              <div className="sticky top-6">
                <PaymentSummary
                  pendingFees={feeSummary.pendingFees}
                  selectedFees={selectedFees}
                  previousBalance={feeSummary.totalOutstanding}
                />
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PaymentRecording;
