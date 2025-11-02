import { useState, useEffect } from 'react';
import { feeJournalService } from '../services/feeJournalService';
import { studentService } from '../services/studentService';
import { useNotification } from '../contexts/NotificationContext';
import Card from '../components/common/Card';
import Table from '../components/common/Table';
import Modal from '../components/common/Modal';
import Loading from '../components/common/Loading';
import ConfirmDialog from '../components/common/ConfirmDialog';
import Badge from '../components/common/Badge';
import Input from '../components/forms/Input';
import Select from '../components/forms/Select';
import { MONTH_OPTIONS, PAYMENT_STATUS_OPTIONS } from '../utils/constants';
import { validateRequired, validateNonNegativeNumber, validateFutureDate, validateYear } from '../utils/validation';
import { formatDate, formatCurrency, formatEnumValue } from '../utils/formatters';

const FeeJournalManagement = () => {
  const [feeJournals, setFeeJournals] = useState([]);
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [isPaymentModalOpen, setIsPaymentModalOpen] = useState(false);
  const [selectedJournal, setSelectedJournal] = useState(null);
  const [filterStatus, setFilterStatus] = useState('');
  const [filterMonth, setFilterMonth] = useState('');
  const [filterYear, setFilterYear] = useState(new Date().getFullYear().toString());
  const [paymentAmount, setPaymentAmount] = useState('');
  const { showSuccess, showError } = useNotification();

  const [formData, setFormData] = useState({
    studentId: '',
    month: '',
    year: new Date().getFullYear().toString(),
    amountDue: '',
    dueDate: '',
    remarks: '',
  });

  const [formErrors, setFormErrors] = useState({});

  useEffect(() => {
    fetchStudents();
  }, []);

  useEffect(() => {
    fetchFeeJournals();
  }, [filterStatus, filterMonth, filterYear]);

  const fetchStudents = async () => {
    try {
      const response = await studentService.getAll();
      setStudents(response.data || []);
    } catch (error) {
      showError('Failed to fetch students');
    }
  };

  const fetchFeeJournals = async () => {
    setLoading(true);
    try {
      let response;

      if (filterStatus) {
        response = await feeJournalService.getByStatus(filterStatus);
      } else if (filterMonth && filterYear) {
        response = await feeJournalService.getByMonth(filterMonth, filterYear);
      } else {
        response = await feeJournalService.getAll();
      }

      setFeeJournals(response.data || []);
    } catch (error) {
      showError(error.message || 'Failed to fetch fee journals');
    } finally {
      setLoading(false);
    }
  };

  const validateForm = () => {
    const errors = {};

    const studentError = validateRequired(formData.studentId, 'Student');
    if (studentError) errors.studentId = studentError;

    const monthError = validateRequired(formData.month, 'Month');
    if (monthError) errors.month = monthError;

    const yearError = validateRequired(formData.year, 'Year') ||
                      validateYear(formData.year);
    if (yearError) errors.year = yearError;

    const amountError = validateRequired(formData.amountDue, 'Amount due') ||
                        validateNonNegativeNumber(formData.amountDue, 'Amount due');
    if (amountError) errors.amountDue = amountError;

    const dueDateError = validateRequired(formData.dueDate, 'Due date') ||
                         validateFutureDate(formData.dueDate, 'Due date');
    if (dueDateError) errors.dueDate = dueDateError;

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

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      showError('Please fix the validation errors');
      return;
    }

    setLoading(true);
    try {
      const payload = {
        ...formData,
        studentId: Number(formData.studentId),
        year: Number(formData.year),
        amountDue: Number(formData.amountDue),
      };

      if (selectedJournal) {
        await feeJournalService.update(selectedJournal.id, payload);
        showSuccess('Fee journal updated successfully');
      } else {
        await feeJournalService.create(payload);
        showSuccess('Fee journal created successfully');
      }

      handleCloseModal();
      fetchFeeJournals();
    } catch (error) {
      showError(error.message || 'Failed to save fee journal');
    } finally {
      setLoading(false);
    }
  };

  const handleRecordPayment = async () => {
    if (!paymentAmount || Number(paymentAmount) <= 0) {
      showError('Please enter a valid payment amount');
      return;
    }

    setLoading(true);
    try {
      await feeJournalService.recordPayment(selectedJournal.id, Number(paymentAmount));
      showSuccess('Payment recorded successfully');
      setIsPaymentModalOpen(false);
      setPaymentAmount('');
      setSelectedJournal(null);
      fetchFeeJournals();
    } catch (error) {
      showError(error.message || 'Failed to record payment');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (journal) => {
    setSelectedJournal(journal);
    setFormData({
      studentId: journal.studentId.toString(),
      month: journal.month,
      year: journal.year.toString(),
      amountDue: journal.amountDue.toString(),
      dueDate: journal.dueDate,
      remarks: journal.remarks || '',
    });
    setIsModalOpen(true);
  };

  const handleDelete = async () => {
    if (!selectedJournal) return;

    setLoading(true);
    try {
      await feeJournalService.delete(selectedJournal.id);
      showSuccess('Fee journal deleted successfully');
      fetchFeeJournals();
    } catch (error) {
      showError(error.message || 'Failed to delete fee journal');
    } finally {
      setLoading(false);
      setSelectedJournal(null);
    }
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedJournal(null);
    setFormData({
      studentId: '',
      month: '',
      year: new Date().getFullYear().toString(),
      amountDue: '',
      dueDate: '',
      remarks: '',
    });
    setFormErrors({});
  };

  const handleOpenDeleteDialog = (journal) => {
    setSelectedJournal(journal);
    setIsDeleteDialogOpen(true);
  };

  const handleOpenPaymentModal = (journal) => {
    setSelectedJournal(journal);
    setPaymentAmount(journal.amountDue - journal.amountPaid);
    setIsPaymentModalOpen(true);
  };

  const getStudentName = (studentId) => {
    const student = students.find(s => s.id === studentId);
    return student ? `${student.firstName} ${student.lastName}` : 'Unknown';
  };

  const columns = [
    {
      header: 'Student',
      render: (row) => getStudentName(row.studentId),
    },
    {
      header: 'Month',
      accessor: 'month',
    },
    {
      header: 'Year',
      accessor: 'year',
    },
    {
      header: 'Amount Due',
      render: (row) => formatCurrency(row.amountDue),
    },
    {
      header: 'Amount Paid',
      render: (row) => formatCurrency(row.amountPaid),
    },
    {
      header: 'Balance',
      render: (row) => formatCurrency(row.amountDue - row.amountPaid),
    },
    {
      header: 'Due Date',
      render: (row) => formatDate(row.dueDate),
    },
    {
      header: 'Status',
      render: (row) => <Badge text={row.paymentStatus} status={row.paymentStatus} />,
    },
    {
      header: 'Actions',
      render: (row) => (
        <div className="flex space-x-2">
          {row.paymentStatus !== 'PAID' && (
            <button
              onClick={() => handleOpenPaymentModal(row)}
              className="text-green-600 hover:text-green-800"
            >
              Pay
            </button>
          )}
          <button
            onClick={() => handleEdit(row)}
            className="text-primary-600 hover:text-primary-800"
          >
            Edit
          </button>
          <button
            onClick={() => handleOpenDeleteDialog(row)}
            className="text-red-600 hover:text-red-800"
          >
            Delete
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold text-gray-900">Fee Journal Management</h1>
        <button
          onClick={() => setIsModalOpen(true)}
          className="btn-primary"
        >
          Add Fee Journal
        </button>
      </div>

      <Card>
        <div className="mb-4 grid grid-cols-3 gap-4">
          <Select
            label="Filter by Month"
            name="filterMonth"
            value={filterMonth}
            onChange={(e) => setFilterMonth(e.target.value)}
            options={MONTH_OPTIONS}
            placeholder="All Months"
          />

          <Input
            label="Filter by Year"
            name="filterYear"
            type="number"
            value={filterYear}
            onChange={(e) => setFilterYear(e.target.value)}
            min="2000"
            max="2100"
          />

          <Select
            label="Filter by Status"
            name="filterStatus"
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
            options={PAYMENT_STATUS_OPTIONS}
            placeholder="All Statuses"
          />
        </div>

        {loading ? (
          <Loading />
        ) : (
          <Table
            columns={columns}
            data={feeJournals}
            emptyMessage="No fee journals found. Create your first fee journal entry to get started."
          />
        )}
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={selectedJournal ? 'Edit Fee Journal' : 'Add Fee Journal'}
        size="medium"
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

            <Select
              label="Month"
              name="month"
              value={formData.month}
              onChange={handleInputChange}
              options={MONTH_OPTIONS}
              error={formErrors.month}
              required
            />

            <Input
              label="Year"
              name="year"
              type="number"
              value={formData.year}
              onChange={handleInputChange}
              error={formErrors.year}
              required
              min="2000"
              max="2100"
            />

            <Input
              label="Amount Due"
              name="amountDue"
              type="number"
              value={formData.amountDue}
              onChange={handleInputChange}
              placeholder="Enter amount"
              error={formErrors.amountDue}
              required
              min="0"
              step="0.01"
            />

            <Input
              label="Due Date"
              name="dueDate"
              type="date"
              value={formData.dueDate}
              onChange={handleInputChange}
              error={formErrors.dueDate}
              required
            />

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
              {selectedJournal ? 'Update' : 'Create'} Fee Journal
            </button>
          </div>
        </form>
      </Modal>

      <Modal
        isOpen={isPaymentModalOpen}
        onClose={() => {
          setIsPaymentModalOpen(false);
          setPaymentAmount('');
          setSelectedJournal(null);
        }}
        title="Record Payment"
        size="small"
      >
        <div className="space-y-4">
          <div>
            <p className="text-sm text-gray-600">Student: {getStudentName(selectedJournal?.studentId)}</p>
            <p className="text-sm text-gray-600">Month/Year: {selectedJournal?.month} {selectedJournal?.year}</p>
            <p className="text-sm text-gray-600">Amount Due: {formatCurrency(selectedJournal?.amountDue)}</p>
            <p className="text-sm text-gray-600">Amount Paid: {formatCurrency(selectedJournal?.amountPaid)}</p>
            <p className="text-sm font-semibold text-gray-900">
              Balance: {formatCurrency(selectedJournal?.amountDue - selectedJournal?.amountPaid)}
            </p>
          </div>

          <Input
            label="Payment Amount"
            name="paymentAmount"
            type="number"
            value={paymentAmount}
            onChange={(e) => setPaymentAmount(e.target.value)}
            placeholder="Enter payment amount"
            required
            min="0"
            step="0.01"
          />

          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={() => {
                setIsPaymentModalOpen(false);
                setPaymentAmount('');
                setSelectedJournal(null);
              }}
              className="btn-secondary"
            >
              Cancel
            </button>
            <button
              onClick={handleRecordPayment}
              className="btn-primary"
              disabled={loading}
            >
              Record Payment
            </button>
          </div>
        </div>
      </Modal>

      <ConfirmDialog
        isOpen={isDeleteDialogOpen}
        onClose={() => {
          setIsDeleteDialogOpen(false);
          setSelectedJournal(null);
        }}
        onConfirm={handleDelete}
        title="Delete Fee Journal"
        message="Are you sure you want to delete this fee journal entry? This action cannot be undone."
        confirmText="Delete"
        type="danger"
      />
    </div>
  );
};

export default FeeJournalManagement;
