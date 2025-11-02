import { useState, useEffect } from 'react';
import { feeMasterService } from '../services/feeMasterService';
import { useNotification } from '../contexts/NotificationContext';
import Card from '../components/common/Card';
import Table from '../components/common/Table';
import Modal from '../components/common/Modal';
import Loading from '../components/common/Loading';
import ConfirmDialog from '../components/common/ConfirmDialog';
import Badge from '../components/common/Badge';
import Input from '../components/forms/Input';
import Select from '../components/forms/Select';
import Textarea from '../components/forms/Textarea';
import { FEE_TYPE_OPTIONS, FEE_FREQUENCY_OPTIONS } from '../utils/constants';
import { validateRequired, validatePositiveNumber, validatePastOrPresentDate } from '../utils/validation';
import { formatDate, formatCurrency, formatEnumValue } from '../utils/formatters';

const FeeMasterManagement = () => {
  const [feeMasters, setFeeMasters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [selectedFeeMaster, setSelectedFeeMaster] = useState(null);
  const [filterType, setFilterType] = useState('');
  const [showActiveOnly, setShowActiveOnly] = useState(false);
  const { showSuccess, showError } = useNotification();

  const [formData, setFormData] = useState({
    feeType: '',
    amount: '',
    frequency: '',
    applicableFrom: new Date().toISOString().split('T')[0],
    applicableTo: '',
    description: '',
    active: true,
  });

  const [formErrors, setFormErrors] = useState({});

  useEffect(() => {
    fetchFeeMasters();
  }, [filterType, showActiveOnly]);

  const fetchFeeMasters = async () => {
    setLoading(true);
    try {
      let response;

      if (showActiveOnly) {
        response = await feeMasterService.getActive();
      } else if (filterType) {
        response = await feeMasterService.getByType(filterType);
      } else {
        response = await feeMasterService.getAll();
      }

      setFeeMasters(response.data || []);
    } catch (error) {
      showError(error.message || 'Failed to fetch fee masters');
    } finally {
      setLoading(false);
    }
  };

  const validateForm = () => {
    const errors = {};

    const typeError = validateRequired(formData.feeType, 'Fee type');
    if (typeError) errors.feeType = typeError;

    const amountError = validateRequired(formData.amount, 'Amount') ||
                        validatePositiveNumber(formData.amount, 'Amount');
    if (amountError) errors.amount = amountError;

    const frequencyError = validateRequired(formData.frequency, 'Frequency');
    if (frequencyError) errors.frequency = frequencyError;

    const fromError = validateRequired(formData.applicableFrom, 'Applicable from') ||
                      validatePastOrPresentDate(formData.applicableFrom, 'Applicable from');
    if (fromError) errors.applicableFrom = fromError;

    // Validate applicableTo is after applicableFrom if provided
    if (formData.applicableTo && formData.applicableFrom) {
      const fromDate = new Date(formData.applicableFrom);
      const toDate = new Date(formData.applicableTo);
      if (toDate <= fromDate) {
        errors.applicableTo = 'Applicable to must be after applicable from';
      }
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));

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
        amount: Number(formData.amount),
        applicableTo: formData.applicableTo || null,
      };

      if (selectedFeeMaster) {
        await feeMasterService.update(selectedFeeMaster.id, payload);
        showSuccess('Fee master updated successfully');
      } else {
        await feeMasterService.create(payload);
        showSuccess('Fee master created successfully');
      }

      handleCloseModal();
      fetchFeeMasters();
    } catch (error) {
      showError(error.message || 'Failed to save fee master');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (feeMaster) => {
    setSelectedFeeMaster(feeMaster);
    setFormData({
      feeType: feeMaster.feeType,
      amount: feeMaster.amount.toString(),
      frequency: feeMaster.frequency,
      applicableFrom: feeMaster.applicableFrom,
      applicableTo: feeMaster.applicableTo || '',
      description: feeMaster.description || '',
      active: feeMaster.active,
    });
    setIsModalOpen(true);
  };

  const handleDelete = async () => {
    if (!selectedFeeMaster) return;

    setLoading(true);
    try {
      await feeMasterService.delete(selectedFeeMaster.id);
      showSuccess('Fee master deleted successfully');
      fetchFeeMasters();
    } catch (error) {
      showError(error.message || 'Failed to delete fee master');
    } finally {
      setLoading(false);
      setSelectedFeeMaster(null);
    }
  };

  const handleToggleActive = async (feeMaster) => {
    setLoading(true);
    try {
      if (feeMaster.active) {
        await feeMasterService.deactivate(feeMaster.id);
        showSuccess('Fee master deactivated successfully');
      } else {
        await feeMasterService.activate(feeMaster.id);
        showSuccess('Fee master activated successfully');
      }
      fetchFeeMasters();
    } catch (error) {
      showError(error.message || 'Failed to update fee master status');
    } finally {
      setLoading(false);
    }
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedFeeMaster(null);
    setFormData({
      feeType: '',
      amount: '',
      frequency: '',
      applicableFrom: new Date().toISOString().split('T')[0],
      applicableTo: '',
      description: '',
      active: true,
    });
    setFormErrors({});
  };

  const handleOpenDeleteDialog = (feeMaster) => {
    setSelectedFeeMaster(feeMaster);
    setIsDeleteDialogOpen(true);
  };

  const columns = [
    {
      header: 'Fee Type',
      render: (row) => formatEnumValue(row.feeType),
    },
    {
      header: 'Amount',
      render: (row) => formatCurrency(row.amount),
    },
    {
      header: 'Frequency',
      render: (row) => formatEnumValue(row.frequency),
    },
    {
      header: 'Applicable From',
      render: (row) => formatDate(row.applicableFrom),
    },
    {
      header: 'Applicable To',
      render: (row) => row.applicableTo ? formatDate(row.applicableTo) : 'Ongoing',
    },
    {
      header: 'Status',
      render: (row) => (
        <Badge
          text={row.active ? 'Active' : 'Inactive'}
          status={row.active ? 'ACTIVE' : 'INACTIVE'}
        />
      ),
    },
    {
      header: 'Actions',
      render: (row) => (
        <div className="flex space-x-2">
          <button
            onClick={() => handleEdit(row)}
            className="text-primary-600 hover:text-primary-800"
          >
            Edit
          </button>
          <button
            onClick={() => handleToggleActive(row)}
            className="text-yellow-600 hover:text-yellow-800"
          >
            {row.active ? 'Deactivate' : 'Activate'}
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
        <h1 className="text-3xl font-bold text-gray-900">Fee Master Management</h1>
        <button
          onClick={() => setIsModalOpen(true)}
          className="btn-primary"
        >
          Add Fee Master
        </button>
      </div>

      <Card>
        <div className="mb-4 flex items-center space-x-4">
          <Select
            label="Filter by Fee Type"
            name="filterType"
            value={filterType}
            onChange={(e) => setFilterType(e.target.value)}
            options={FEE_TYPE_OPTIONS}
            placeholder="All Types"
            className="w-64"
          />

          <div className="flex items-center pt-6">
            <input
              type="checkbox"
              id="showActiveOnly"
              checked={showActiveOnly}
              onChange={(e) => setShowActiveOnly(e.target.checked)}
              className="w-4 h-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
            />
            <label htmlFor="showActiveOnly" className="ml-2 text-sm text-gray-700">
              Show active only
            </label>
          </div>
        </div>

        {loading ? (
          <Loading />
        ) : (
          <Table
            columns={columns}
            data={feeMasters}
            emptyMessage="No fee masters found. Create your first fee master to get started."
          />
        )}
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={selectedFeeMaster ? 'Edit Fee Master' : 'Add Fee Master'}
        size="medium"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Fee Type"
              name="feeType"
              value={formData.feeType}
              onChange={handleInputChange}
              options={FEE_TYPE_OPTIONS}
              error={formErrors.feeType}
              required
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

            <Select
              label="Frequency"
              name="frequency"
              value={formData.frequency}
              onChange={handleInputChange}
              options={FEE_FREQUENCY_OPTIONS}
              error={formErrors.frequency}
              required
            />

            <Input
              label="Applicable From"
              name="applicableFrom"
              type="date"
              value={formData.applicableFrom}
              onChange={handleInputChange}
              error={formErrors.applicableFrom}
              required
            />

            <Input
              label="Applicable To"
              name="applicableTo"
              type="date"
              value={formData.applicableTo}
              onChange={handleInputChange}
              placeholder="Optional (leave empty for ongoing)"
              error={formErrors.applicableTo}
            />

            <div className="flex items-center pt-6">
              <input
                type="checkbox"
                id="active"
                name="active"
                checked={formData.active}
                onChange={handleInputChange}
                className="w-4 h-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
              />
              <label htmlFor="active" className="ml-2 text-sm text-gray-700">
                Active
              </label>
            </div>

            <Textarea
              label="Description"
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="Optional description"
              className="col-span-2"
              rows={3}
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
              {selectedFeeMaster ? 'Update' : 'Create'} Fee Master
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        isOpen={isDeleteDialogOpen}
        onClose={() => {
          setIsDeleteDialogOpen(false);
          setSelectedFeeMaster(null);
        }}
        onConfirm={handleDelete}
        title="Delete Fee Master"
        message={`Are you sure you want to delete this ${selectedFeeMaster?.feeType} fee master? This action cannot be undone.`}
        confirmText="Delete"
        type="danger"
      />
    </div>
  );
};

export default FeeMasterManagement;
