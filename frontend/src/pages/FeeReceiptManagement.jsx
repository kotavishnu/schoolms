import { useState, useEffect } from 'react';
import { feeReceiptService } from '../services/feeReceiptService';
import { studentService } from '../services/studentService';
import { useNotification } from '../contexts/NotificationContext';
import Card from '../components/common/Card';
import Table from '../components/common/Table';
import Modal from '../components/common/Modal';
import Loading from '../components/common/Loading';
import Input from '../components/forms/Input';
import Select from '../components/forms/Select';
import { PAYMENT_METHOD_OPTIONS, MONTH_OPTIONS } from '../utils/constants';
import { validateRequired, validatePositiveNumber, validateNotFutureDate } from '../utils/validation';
import { formatDate, formatCurrency } from '../utils/formatters';

const FeeReceiptManagement = () => {
  const [receipts, setReceipts] = useState([]);
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [filterMethod, setFilterMethod] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const { showSuccess, showError } = useNotification();

  const [formData, setFormData] = useState({
    studentId: '',
    amount: '',
    paymentDate: new Date().toISOString().split('T')[0],
    paymentMethod: '',
    transactionId: '',
    chequeNumber: '',
    bankName: '',
    monthsPaid: [],
    feeBreakdown: {},
    remarks: '',
  });

  const [formErrors, setFormErrors] = useState({});
  const [selectedMonths, setSelectedMonths] = useState([]);

  useEffect(() => {
    fetchStudents();
    fetchReceipts();
  }, []);

  useEffect(() => {
    if (startDate && endDate) {
      fetchReceiptsByDateRange();
    } else if (filterMethod) {
      fetchReceiptsByMethod();
    } else {
      fetchReceipts();
    }
  }, [filterMethod, startDate, endDate]);

  const fetchStudents = async () => {
    try {
      const response = await studentService.getAll();
      setStudents(response.data || []);
    } catch (error) {
      showError('Failed to fetch students');
    }
  };

  const fetchReceipts = async () => {
    setLoading(true);
    try {
      const response = await feeReceiptService.getAll();
      setReceipts(response.data || []);
    } catch (error) {
      showError(error.message || 'Failed to fetch receipts');
    } finally {
      setLoading(false);
    }
  };

  const fetchReceiptsByDateRange = async () => {
    setLoading(true);
    try {
      const response = await feeReceiptService.getByDateRange(startDate, endDate);
      setReceipts(response.data || []);
    } catch (error) {
      showError(error.message || 'Failed to fetch receipts');
    } finally {
      setLoading(false);
    }
  };

  const fetchReceiptsByMethod = async () => {
    setLoading(true);
    try {
      const response = await feeReceiptService.getByPaymentMethod(filterMethod);
      setReceipts(response.data || []);
    } catch (error) {
      showError(error.message || 'Failed to fetch receipts');
    } finally {
      setLoading(false);
    }
  };

  const validateForm = () => {
    const errors = {};

    const studentError = validateRequired(formData.studentId, 'Student');
    if (studentError) errors.studentId = studentError;

    const amountError = validateRequired(formData.amount, 'Amount') ||
                        validatePositiveNumber(formData.amount, 'Amount');
    if (amountError) errors.amount = amountError;

    const dateError = validateRequired(formData.paymentDate, 'Payment date') ||
                      validateNotFutureDate(formData.paymentDate, 'Payment date');
    if (dateError) errors.paymentDate = dateError;

    const methodError = validateRequired(formData.paymentMethod, 'Payment method');
    if (methodError) errors.paymentMethod = methodError;

    if (formData.paymentMethod === 'ONLINE' && !formData.transactionId) {
      errors.transactionId = 'Transaction ID is required for online payments';
    }

    if (formData.paymentMethod === 'CHEQUE') {
      if (!formData.chequeNumber) {
        errors.chequeNumber = 'Cheque number is required';
      }
      if (!formData.bankName) {
        errors.bankName = 'Bank name is required';
      }
    }

    if (selectedMonths.length === 0) {
      errors.monthsPaid = 'Please select at least one month';
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));

    if (formErrors[name]) {
      setFormErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handleMonthToggle = (month) => {
    setSelectedMonths(prev => {
      if (prev.includes(month)) {
        return prev.filter(m => m !== month);
      } else {
        return [...prev, month];
      }
    });
    if (formErrors.monthsPaid) {
      setFormErrors(prev => ({ ...prev, monthsPaid: '' }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      showError('Please fix the validation errors');
      return;
    }

    setLoading(true);
    try {
      const payload = {
        studentId: Number(formData.studentId),
        amount: Number(formData.amount),
        paymentDate: formData.paymentDate,
        paymentMethod: formData.paymentMethod,
        transactionId: formData.transactionId || null,
        chequeNumber: formData.chequeNumber || null,
        bankName: formData.bankName || null,
        monthsPaid: selectedMonths,
        feeBreakdown: {},
        remarks: formData.remarks || null,
      };

      await feeReceiptService.create(payload);
      showSuccess('Fee receipt generated successfully');
      handleCloseModal();
      fetchReceipts();
    } catch (error) {
      showError(error.message || 'Failed to generate receipt');
    } finally {
      setLoading(false);
    }
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setFormData({
      studentId: '',
      amount: '',
      paymentDate: new Date().toISOString().split('T')[0],
      paymentMethod: '',
      transactionId: '',
      chequeNumber: '',
      bankName: '',
      monthsPaid: [],
      feeBreakdown: {},
      remarks: '',
    });
    setSelectedMonths([]);
    setFormErrors({});
  };

  const getStudentName = (studentId) => {
    const student = students.find(s => s.id === studentId);
    return student ? `${student.firstName} ${student.lastName}` : 'Unknown';
  };

  const columns = [
    {
      header: 'Receipt #',
      accessor: 'receiptNumber',
    },
    {
      header: 'Student',
      render: (row) => getStudentName(row.studentId),
    },
    {
      header: 'Amount',
      render: (row) => formatCurrency(row.amount),
    },
    {
      header: 'Payment Date',
      render: (row) => formatDate(row.paymentDate),
    },
    {
      header: 'Payment Method',
      accessor: 'paymentMethod',
    },
    {
      header: 'Months Paid',
      render: (row) => {
        if (!row.monthsPaid) return '-';
        // Handle both array and string formats
        if (Array.isArray(row.monthsPaid)) {
          return row.monthsPaid.join(', ');
        }
        return row.monthsPaid;
      },
    },
    {
      header: 'Transaction ID',
      render: (row) => row.transactionId || row.chequeNumber || '-',
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold text-gray-900">Fee Receipt Management</h1>
        <button
          onClick={() => setIsModalOpen(true)}
          className="btn-primary"
        >
          Generate Receipt
        </button>
      </div>

      <Card>
        <div className="mb-4 grid grid-cols-3 gap-4">
          <Select
            label="Filter by Payment Method"
            name="filterMethod"
            value={filterMethod}
            onChange={(e) => setFilterMethod(e.target.value)}
            options={PAYMENT_METHOD_OPTIONS}
            placeholder="All Methods"
          />

          <Input
            label="Start Date"
            name="startDate"
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />

          <Input
            label="End Date"
            name="endDate"
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
          />
        </div>

        {loading ? (
          <Loading />
        ) : (
          <Table
            columns={columns}
            data={receipts}
            emptyMessage="No receipts found."
          />
        )}
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title="Generate Fee Receipt"
        size="large"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Student"
              name="studentId"
              value={formData.studentId}
              onChange={handleInputChange}
              options={students.map(s => ({
                value: s.id.toString(),
                label: `${s.firstName} ${s.lastName} (ID: ${s.id})`
              }))}
              error={formErrors.studentId}
              required
              className="col-span-2"
            />

            <Input
              label="Amount"
              name="amount"
              type="number"
              value={formData.amount}
              onChange={handleInputChange}
              placeholder="Enter amount"
              error={formErrors.amount}
              required
              min="0"
              step="0.01"
            />

            <Input
              label="Payment Date"
              name="paymentDate"
              type="date"
              value={formData.paymentDate}
              onChange={handleInputChange}
              error={formErrors.paymentDate}
              required
            />

            <Select
              label="Payment Method"
              name="paymentMethod"
              value={formData.paymentMethod}
              onChange={handleInputChange}
              options={PAYMENT_METHOD_OPTIONS}
              error={formErrors.paymentMethod}
              required
              className="col-span-2"
            />

            {formData.paymentMethod === 'ONLINE' && (
              <Input
                label="Transaction ID"
                name="transactionId"
                value={formData.transactionId}
                onChange={handleInputChange}
                placeholder="Enter transaction ID"
                error={formErrors.transactionId}
                required
                className="col-span-2"
              />
            )}

            {formData.paymentMethod === 'CHEQUE' && (
              <>
                <Input
                  label="Cheque Number"
                  name="chequeNumber"
                  value={formData.chequeNumber}
                  onChange={handleInputChange}
                  placeholder="Enter cheque number"
                  error={formErrors.chequeNumber}
                  required
                />

                <Input
                  label="Bank Name"
                  name="bankName"
                  value={formData.bankName}
                  onChange={handleInputChange}
                  placeholder="Enter bank name"
                  error={formErrors.bankName}
                  required
                />
              </>
            )}

            <div className="col-span-2">
              <label className="label">
                Months Paid <span className="text-red-500 ml-1">*</span>
              </label>
              <div className="grid grid-cols-4 gap-2 mt-2">
                {MONTH_OPTIONS.map(month => (
                  <label key={month.value} className="flex items-center space-x-2">
                    <input
                      type="checkbox"
                      checked={selectedMonths.includes(month.value)}
                      onChange={() => handleMonthToggle(month.value)}
                      className="w-4 h-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
                    />
                    <span className="text-sm text-gray-700">{month.label}</span>
                  </label>
                ))}
              </div>
              {formErrors.monthsPaid && (
                <p className="mt-1 text-sm text-red-600">{formErrors.monthsPaid}</p>
              )}
            </div>

            <Input
              label="Remarks"
              name="remarks"
              value={formData.remarks}
              onChange={handleInputChange}
              placeholder="Optional"
              className="col-span-2"
            />
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={handleCloseModal}
              className="btn-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn-primary"
              disabled={loading}
            >
              Generate Receipt
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default FeeReceiptManagement;
