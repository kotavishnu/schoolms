import { useState, useEffect } from 'react';
import { schoolConfigService } from '../services/schoolConfigService';
import { useNotification } from '../contexts/NotificationContext';
import Card from '../components/common/Card';
import Table from '../components/common/Table';
import Modal from '../components/common/Modal';
import Loading from '../components/common/Loading';
import ConfirmDialog from '../components/common/ConfirmDialog';
import Input from '../components/forms/Input';
import Select from '../components/forms/Select';
import Textarea from '../components/forms/Textarea';
import { CONFIG_DATA_TYPE_OPTIONS, CONFIG_CATEGORY_OPTIONS } from '../utils/constants';
import { validateRequired } from '../utils/validation';

const SchoolConfig = () => {
  const [configs, setConfigs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [selectedConfig, setSelectedConfig] = useState(null);
  const [filterCategory, setFilterCategory] = useState('');
  const [showEditableOnly, setShowEditableOnly] = useState(false);
  const { showSuccess, showError } = useNotification();

  const [formData, setFormData] = useState({
    key: '',
    value: '',
    dataType: 'STRING',
    category: 'GENERAL',
    description: '',
    editable: true,
  });

  const [formErrors, setFormErrors] = useState({});

  useEffect(() => {
    fetchConfigs();
  }, [filterCategory, showEditableOnly]);

  const fetchConfigs = async () => {
    setLoading(true);
    try {
      let response;

      if (showEditableOnly) {
        response = await schoolConfigService.getEditable();
      } else if (filterCategory) {
        response = await schoolConfigService.getAll(filterCategory);
      } else {
        response = await schoolConfigService.getAll();
      }

      // Map backend field names to frontend field names
      const mappedConfigs = (response.data || []).map(config => ({
        ...config,
        key: config.configKey,
        value: config.configValue,
        editable: config.isEditable,
      }));

      setConfigs(mappedConfigs);
    } catch (error) {
      showError(error.message || 'Failed to fetch configurations');
    } finally {
      setLoading(false);
    }
  };

  const validateForm = () => {
    const errors = {};

    const keyError = validateRequired(formData.key, 'Key');
    if (keyError) errors.key = keyError;

    const valueError = validateRequired(formData.value, 'Value');
    if (valueError) errors.value = valueError;

    const dataTypeError = validateRequired(formData.dataType, 'Data type');
    if (dataTypeError) errors.dataType = dataTypeError;

    const categoryError = validateRequired(formData.category, 'Category');
    if (categoryError) errors.category = categoryError;

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
      // Map frontend field names to backend DTO field names
      const requestData = {
        configKey: formData.key,
        configValue: formData.value,
        dataType: formData.dataType,
        category: formData.category,
        description: formData.description,
        isEditable: formData.editable,
      };

      if (selectedConfig) {
        await schoolConfigService.update(selectedConfig.id, requestData);
        showSuccess('Configuration updated successfully');
      } else {
        await schoolConfigService.create(requestData);
        showSuccess('Configuration created successfully');
      }

      handleCloseModal();
      fetchConfigs();
    } catch (error) {
      showError(error.message || 'Failed to save configuration');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (config) => {
    setSelectedConfig(config);
    setFormData({
      key: config.key,
      value: config.value,
      dataType: config.dataType,
      category: config.category,
      description: config.description || '',
      editable: config.editable,
    });
    setIsModalOpen(true);
  };

  const handleDelete = async () => {
    if (!selectedConfig) return;

    setLoading(true);
    try {
      await schoolConfigService.delete(selectedConfig.id);
      showSuccess('Configuration deleted successfully');
      fetchConfigs();
    } catch (error) {
      showError(error.message || 'Failed to delete configuration');
    } finally {
      setLoading(false);
      setSelectedConfig(null);
    }
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedConfig(null);
    setFormData({
      key: '',
      value: '',
      dataType: 'STRING',
      category: 'GENERAL',
      description: '',
      editable: true,
    });
    setFormErrors({});
  };

  const handleOpenDeleteDialog = (config) => {
    setSelectedConfig(config);
    setIsDeleteDialogOpen(true);
  };

  const columns = [
    {
      header: 'Key',
      accessor: 'key',
    },
    {
      header: 'Value',
      accessor: 'value',
      render: (row) => (
        <span className="font-mono text-sm">
          {row.value.length > 50 ? `${row.value.substring(0, 50)}...` : row.value}
        </span>
      ),
    },
    {
      header: 'Data Type',
      accessor: 'dataType',
    },
    {
      header: 'Category',
      accessor: 'category',
    },
    {
      header: 'Editable',
      render: (row) => (
        <span className={row.editable ? 'text-green-600' : 'text-gray-400'}>
          {row.editable ? 'Yes' : 'No'}
        </span>
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
          {row.editable && (
            <button
              onClick={() => handleOpenDeleteDialog(row)}
              className="text-red-600 hover:text-red-800"
            >
              Delete
            </button>
          )}
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold text-gray-900">School Configuration</h1>
        <button
          onClick={() => setIsModalOpen(true)}
          className="btn-primary"
        >
          Add Configuration
        </button>
      </div>

      <Card>
        <div className="mb-4 flex items-center space-x-4">
          <Select
            label="Filter by Category"
            name="filterCategory"
            value={filterCategory}
            onChange={(e) => setFilterCategory(e.target.value)}
            options={CONFIG_CATEGORY_OPTIONS}
            placeholder="All Categories"
            className="w-64"
          />

          <div className="flex items-center pt-6">
            <input
              type="checkbox"
              id="showEditableOnly"
              checked={showEditableOnly}
              onChange={(e) => setShowEditableOnly(e.target.checked)}
              className="w-4 h-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
            />
            <label htmlFor="showEditableOnly" className="ml-2 text-sm text-gray-700">
              Show editable only
            </label>
          </div>
        </div>

        {loading ? (
          <Loading />
        ) : (
          <Table
            columns={columns}
            data={configs}
            emptyMessage="No configurations found."
          />
        )}
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={selectedConfig ? 'Edit Configuration' : 'Add Configuration'}
        size="medium"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Key"
              name="key"
              value={formData.key}
              onChange={handleInputChange}
              placeholder="e.g., SCHOOL_NAME"
              error={formErrors.key}
              required
              disabled={!!selectedConfig}
            />

            <Select
              label="Data Type"
              name="dataType"
              value={formData.dataType}
              onChange={handleInputChange}
              options={CONFIG_DATA_TYPE_OPTIONS}
              error={formErrors.dataType}
              required
            />

            <Select
              label="Category"
              name="category"
              value={formData.category}
              onChange={handleInputChange}
              options={CONFIG_CATEGORY_OPTIONS}
              error={formErrors.category}
              required
            />

            <div className="flex items-center pt-6">
              <input
                type="checkbox"
                id="editable"
                name="editable"
                checked={formData.editable}
                onChange={handleInputChange}
                className="w-4 h-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
              />
              <label htmlFor="editable" className="ml-2 text-sm text-gray-700">
                Editable
              </label>
            </div>

            <Textarea
              label="Value"
              name="value"
              value={formData.value}
              onChange={handleInputChange}
              placeholder="Enter configuration value"
              error={formErrors.value}
              required
              className="col-span-2"
              rows={3}
            />

            <Textarea
              label="Description"
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="Optional description"
              className="col-span-2"
              rows={2}
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
              {selectedConfig ? 'Update' : 'Create'} Configuration
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        isOpen={isDeleteDialogOpen}
        onClose={() => {
          setIsDeleteDialogOpen(false);
          setSelectedConfig(null);
        }}
        onConfirm={handleDelete}
        title="Delete Configuration"
        message={`Are you sure you want to delete the configuration "${selectedConfig?.key}"? This action cannot be undone.`}
        confirmText="Delete"
        type="danger"
      />
    </div>
  );
};

export default SchoolConfig;
