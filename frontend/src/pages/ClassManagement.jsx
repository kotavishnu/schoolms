import { useState, useEffect } from 'react';
import { classService } from '../services/classService';
import { useNotification } from '../contexts/NotificationContext';
import Card from '../components/common/Card';
import Table from '../components/common/Table';
import Modal from '../components/common/Modal';
import Loading from '../components/common/Loading';
import ConfirmDialog from '../components/common/ConfirmDialog';
import Input from '../components/forms/Input';
import Select from '../components/forms/Select';
import { CLASS_NUMBERS, getAcademicYearOptions, getCurrentAcademicYear } from '../utils/constants';
import { validateRequired, validateClassNumber, validateAcademicYear, validatePositiveNumber } from '../utils/validation';

const ClassManagement = () => {
  const [classes, setClasses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [selectedClass, setSelectedClass] = useState(null);
  const [filterAcademicYear, setFilterAcademicYear] = useState(getCurrentAcademicYear());
  const { showSuccess, showError } = useNotification();

  const [formData, setFormData] = useState({
    classNumber: '',
    section: '',
    academicYear: getCurrentAcademicYear(),
    capacity: '',
    roomNumber: '',
  });

  const [formErrors, setFormErrors] = useState({});

  useEffect(() => {
    fetchClasses();
  }, [filterAcademicYear]);

  const fetchClasses = async () => {
    setLoading(true);
    try {
      const response = await classService.getAll(filterAcademicYear);
      setClasses(response.data || []);
    } catch (error) {
      showError(error.message || 'Failed to fetch classes');
    } finally {
      setLoading(false);
    }
  };

  const validateForm = () => {
    const errors = {};

    const classNumberError = validateRequired(formData.classNumber, 'Class number') ||
                             validateClassNumber(formData.classNumber);
    if (classNumberError) errors.classNumber = classNumberError;

    const sectionError = validateRequired(formData.section, 'Section');
    if (sectionError) errors.section = sectionError;

    const academicYearError = validateRequired(formData.academicYear, 'Academic year') ||
                               validateAcademicYear(formData.academicYear);
    if (academicYearError) errors.academicYear = academicYearError;

    const capacityError = validateRequired(formData.capacity, 'Capacity') ||
                          validatePositiveNumber(formData.capacity, 'Capacity');
    if (capacityError) errors.capacity = capacityError;

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));

    // Clear error for this field
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
        classNumber: Number(formData.classNumber),
        capacity: Number(formData.capacity),
      };

      if (selectedClass) {
        await classService.update(selectedClass.id, payload);
        showSuccess('Class updated successfully');
      } else {
        await classService.create(payload);
        showSuccess('Class created successfully');
      }

      handleCloseModal();
      fetchClasses();
    } catch (error) {
      showError(error.message || 'Failed to save class');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (classData) => {
    setSelectedClass(classData);
    setFormData({
      classNumber: classData.classNumber.toString(),
      section: classData.section,
      academicYear: classData.academicYear,
      capacity: classData.capacity.toString(),
      roomNumber: classData.roomNumber || '',
    });
    setIsModalOpen(true);
  };

  const handleDelete = async () => {
    if (!selectedClass) return;

    setLoading(true);
    try {
      await classService.delete(selectedClass.id);
      showSuccess('Class deleted successfully');
      fetchClasses();
    } catch (error) {
      showError(error.message || 'Failed to delete class');
    } finally {
      setLoading(false);
      setSelectedClass(null);
    }
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedClass(null);
    setFormData({
      classNumber: '',
      section: '',
      academicYear: getCurrentAcademicYear(),
      capacity: '',
      roomNumber: '',
    });
    setFormErrors({});
  };

  const handleOpenDeleteDialog = (classData) => {
    setSelectedClass(classData);
    setIsDeleteDialogOpen(true);
  };

  const columns = [
    {
      header: 'Class',
      accessor: 'classNumber',
      render: (row) => `Class ${row.classNumber}`,
    },
    {
      header: 'Section',
      accessor: 'section',
    },
    {
      header: 'Academic Year',
      accessor: 'academicYear',
    },
    {
      header: 'Capacity',
      accessor: 'capacity',
    },
    {
      header: 'Current Students',
      accessor: 'currentStrength',
      render: (row) => row.currentStrength || 0,
    },
    {
      header: 'Available Seats',
      accessor: 'availableSeats',
      render: (row) => {
        const available = row.capacity - (row.currentStrength || 0);
        return (
          <span className={available > 0 ? 'text-green-600' : 'text-red-600'}>
            {available}
          </span>
        );
      },
    },
    {
      header: 'Room Number',
      accessor: 'roomNumber',
      render: (row) => row.roomNumber || '-',
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
        <h1 className="text-3xl font-bold text-gray-900">Class Management</h1>
        <button
          onClick={() => setIsModalOpen(true)}
          className="btn-primary"
        >
          Add New Class
        </button>
      </div>

      <Card>
        <div className="mb-4 flex items-center space-x-4">
          <Select
            label="Filter by Academic Year"
            name="filterAcademicYear"
            value={filterAcademicYear}
            onChange={(e) => setFilterAcademicYear(e.target.value)}
            options={getAcademicYearOptions()}
            className="w-64"
          />
        </div>

        {loading ? (
          <Loading />
        ) : (
          <Table
            columns={columns}
            data={classes}
            emptyMessage="No classes found. Create your first class to get started."
          />
        )}
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={selectedClass ? 'Edit Class' : 'Add New Class'}
        size="medium"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Class Number"
              name="classNumber"
              value={formData.classNumber}
              onChange={handleInputChange}
              options={CLASS_NUMBERS.map(num => ({ value: num.toString(), label: `Class ${num}` }))}
              error={formErrors.classNumber}
              required
            />

            <Input
              label="Section"
              name="section"
              value={formData.section}
              onChange={handleInputChange}
              placeholder="e.g., A, B, C"
              error={formErrors.section}
              required
            />

            <Input
              label="Academic Year"
              name="academicYear"
              value={formData.academicYear}
              onChange={handleInputChange}
              placeholder="2024-2025"
              error={formErrors.academicYear}
              required
            />

            <Input
              label="Capacity"
              name="capacity"
              type="number"
              value={formData.capacity}
              onChange={handleInputChange}
              placeholder="Enter capacity"
              error={formErrors.capacity}
              required
              min="1"
            />

            <Input
              label="Room Number"
              name="roomNumber"
              value={formData.roomNumber}
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
              {selectedClass ? 'Update' : 'Create'} Class
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        isOpen={isDeleteDialogOpen}
        onClose={() => {
          setIsDeleteDialogOpen(false);
          setSelectedClass(null);
        }}
        onConfirm={handleDelete}
        title="Delete Class"
        message={`Are you sure you want to delete Class ${selectedClass?.classNumber} ${selectedClass?.section}? This action cannot be undone.`}
        confirmText="Delete"
        type="danger"
      />
    </div>
  );
};

export default ClassManagement;
