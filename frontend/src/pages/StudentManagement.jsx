import { useState, useEffect } from 'react';
import { studentService } from '../services/studentService';
import { classService } from '../services/classService';
import { useNotification } from '../contexts/NotificationContext';
import { useDebounce } from '../hooks/useDebounce';
import Card from '../components/common/Card';
import Table from '../components/common/Table';
import Modal from '../components/common/Modal';
import Loading from '../components/common/Loading';
import ConfirmDialog from '../components/common/ConfirmDialog';
import Badge from '../components/common/Badge';
import Input from '../components/forms/Input';
import Select from '../components/forms/Select';
import Textarea from '../components/forms/Textarea';
import { STUDENT_STATUS_OPTIONS, getCurrentAcademicYear } from '../utils/constants';
import { validateRequired, validateMobile, validateAge, validateNotFutureDate, validateEmail, validateMaxLength } from '../utils/validation';
import { formatDate, formatPhoneNumber, formatFullName } from '../utils/formatters';

const StudentManagement = () => {
  const [students, setStudents] = useState([]);
  const [classes, setClasses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [filterClassId, setFilterClassId] = useState('');
  const debouncedSearchQuery = useDebounce(searchQuery, 500);
  const { showSuccess, showError } = useNotification();

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    dateOfBirth: '',
    mobile: '',
    email: '',
    address: '',
    fatherName: '',
    motherName: '',
    classId: '',
    enrollmentDate: new Date().toISOString().split('T')[0],
    status: 'ACTIVE',
  });

  const [formErrors, setFormErrors] = useState({});

  useEffect(() => {
    fetchClasses();
  }, []);

  useEffect(() => {
    if (debouncedSearchQuery.length >= 3) {
      searchStudents();
    } else if (debouncedSearchQuery.length === 0) {
      fetchStudents();
    }
  }, [debouncedSearchQuery, filterClassId]);

  const fetchClasses = async () => {
    try {
      const response = await classService.getAll(getCurrentAcademicYear());
      setClasses(response.data || []);
    } catch (error) {
      showError('Failed to fetch classes');
    }
  };

  const fetchStudents = async () => {
    setLoading(true);
    try {
      const response = await studentService.getAll(filterClassId || null);
      setStudents(response.data || []);
    } catch (error) {
      showError(error.message || 'Failed to fetch students');
    } finally {
      setLoading(false);
    }
  };

  const searchStudents = async () => {
    setLoading(true);
    try {
      const response = await studentService.search(debouncedSearchQuery);
      let results = response.data || [];

      // Apply class filter if selected
      if (filterClassId) {
        results = results.filter(s => s.classId === Number(filterClassId));
      }

      setStudents(results);
    } catch (error) {
      showError(error.message || 'Failed to search students');
    } finally {
      setLoading(false);
    }
  };

  const validateForm = () => {
    const errors = {};

    const firstNameError = validateRequired(formData.firstName, 'First name') ||
                           validateMaxLength(formData.firstName, 100, 'First name');
    if (firstNameError) errors.firstName = firstNameError;

    const lastNameError = validateRequired(formData.lastName, 'Last name') ||
                          validateMaxLength(formData.lastName, 100, 'Last name');
    if (lastNameError) errors.lastName = lastNameError;

    const dobError = validateRequired(formData.dateOfBirth, 'Date of birth') ||
                     validateAge(formData.dateOfBirth);
    if (dobError) errors.dateOfBirth = dobError;

    const mobileError = validateMobile(formData.mobile);
    if (mobileError) errors.mobile = mobileError;

    if (formData.email) {
      const emailError = validateEmail(formData.email);
      if (emailError) errors.email = emailError;
    }

    const classError = validateRequired(formData.classId, 'Class');
    if (classError) errors.classId = classError;

    const addressError = validateRequired(formData.address, 'Address');
    if (addressError) errors.address = addressError;
    else if (formData.address.length < 10) {
      errors.address = 'Address must be at least 10 characters';
    }

    const fatherNameError = validateRequired(formData.fatherName, "Father's name") ||
                            validateMaxLength(formData.fatherName, 100, "Father's name");
    if (fatherNameError) errors.fatherName = fatherNameError;

    const motherNameError = validateRequired(formData.motherName, "Mother's name") ||
                            validateMaxLength(formData.motherName, 100, "Mother's name");
    if (motherNameError) errors.motherName = motherNameError;

    const enrollmentError = validateRequired(formData.enrollmentDate, 'Enrollment date') ||
                            validateNotFutureDate(formData.enrollmentDate, 'Enrollment date');
    if (enrollmentError) errors.enrollmentDate = enrollmentError;

    const statusError = validateRequired(formData.status, 'Status');
    if (statusError) errors.status = statusError;

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
        classId: Number(formData.classId),
      };

      if (selectedStudent) {
        await studentService.update(selectedStudent.id, payload);
        showSuccess('Student updated successfully');
      } else {
        await studentService.create(payload);
        showSuccess('Student created successfully');
      }

      handleCloseModal();
      fetchStudents();
    } catch (error) {
      showError(error.message || 'Failed to save student');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (student) => {
    setSelectedStudent(student);
    setFormData({
      firstName: student.firstName,
      lastName: student.lastName,
      dateOfBirth: student.dateOfBirth,
      mobile: student.mobile,
      email: student.email || '',
      address: student.address || '',
      fatherName: student.fatherName || '',
      motherName: student.motherName || '',
      classId: student.classId.toString(),
      enrollmentDate: student.enrollmentDate,
      status: student.status,
    });
    setIsModalOpen(true);
  };

  const handleDelete = async () => {
    if (!selectedStudent) return;

    setLoading(true);
    try {
      await studentService.delete(selectedStudent.id);
      showSuccess('Student deleted successfully');
      fetchStudents();
    } catch (error) {
      showError(error.message || 'Failed to delete student');
    } finally {
      setLoading(false);
      setSelectedStudent(null);
    }
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedStudent(null);
    setFormData({
      firstName: '',
      lastName: '',
      dateOfBirth: '',
      mobile: '',
      email: '',
      address: '',
      fatherName: '',
      motherName: '',
      classId: '',
      enrollmentDate: new Date().toISOString().split('T')[0],
      status: 'ACTIVE',
    });
    setFormErrors({});
  };

  const handleOpenDeleteDialog = (student) => {
    setSelectedStudent(student);
    setIsDeleteDialogOpen(true);
  };

  const getClassLabel = (classId) => {
    const classData = classes.find(c => c.id === classId);
    return classData ? `Class ${classData.classNumber} ${classData.section}` : '-';
  };

  const columns = [
    {
      header: 'Student ID',
      accessor: 'id',
    },
    {
      header: 'Name',
      render: (row) => formatFullName(row.firstName, row.lastName),
    },
    {
      header: 'Class',
      render: (row) => getClassLabel(row.classId),
    },
    {
      header: 'Mobile',
      render: (row) => formatPhoneNumber(row.mobile),
    },
    {
      header: 'Enrollment Date',
      render: (row) => formatDate(row.enrollmentDate),
    },
    {
      header: 'Status',
      render: (row) => <Badge text={row.status} status={row.status} />,
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
        <h1 className="text-3xl font-bold text-gray-900">Student Management</h1>
        <button
          onClick={() => setIsModalOpen(true)}
          className="btn-primary"
        >
          Add New Student
        </button>
      </div>

      <Card>
        <div className="mb-4 grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Search Students"
            name="search"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search by name (min 3 characters)..."
          />

          <Select
            label="Filter by Class"
            name="filterClass"
            value={filterClassId}
            onChange={(e) => setFilterClassId(e.target.value)}
            options={classes.map(c => ({
              value: c.id.toString(),
              label: `Class ${c.classNumber} ${c.section}`
            }))}
            placeholder="All Classes"
          />
        </div>

        {loading ? (
          <Loading />
        ) : (
          <Table
            columns={columns}
            data={students}
            emptyMessage="No students found. Add your first student to get started."
          />
        )}
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={selectedStudent ? 'Edit Student' : 'Add New Student'}
        size="large"
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="First Name"
              name="firstName"
              value={formData.firstName}
              onChange={handleInputChange}
              placeholder="Enter first name"
              error={formErrors.firstName}
              required
            />

            <Input
              label="Last Name"
              name="lastName"
              value={formData.lastName}
              onChange={handleInputChange}
              placeholder="Enter last name"
              error={formErrors.lastName}
              required
            />

            <Input
              label="Date of Birth"
              name="dateOfBirth"
              type="date"
              value={formData.dateOfBirth}
              onChange={handleInputChange}
              error={formErrors.dateOfBirth}
              required
            />

            <Input
              label="Mobile Number"
              name="mobile"
              value={formData.mobile}
              onChange={handleInputChange}
              placeholder="10-digit mobile number"
              error={formErrors.mobile}
              required
            />

            <Input
              label="Email"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleInputChange}
              placeholder="Optional"
              error={formErrors.email}
            />

            <Input
              label="Father's Name"
              name="fatherName"
              value={formData.fatherName}
              onChange={handleInputChange}
              placeholder="Enter father's name"
              error={formErrors.fatherName}
              required
            />

            <Input
              label="Mother's Name"
              name="motherName"
              value={formData.motherName}
              onChange={handleInputChange}
              placeholder="Enter mother's name"
              error={formErrors.motherName}
              required
            />

            <Select
              label="Class"
              name="classId"
              value={formData.classId}
              onChange={handleInputChange}
              options={classes.map(c => ({
                value: c.id.toString(),
                label: `Class ${c.classNumber} ${c.section}`
              }))}
              error={formErrors.classId}
              required
            />

            <Input
              label="Enrollment Date"
              name="enrollmentDate"
              type="date"
              value={formData.enrollmentDate}
              onChange={handleInputChange}
              error={formErrors.enrollmentDate}
              required
            />

            <Select
              label="Status"
              name="status"
              value={formData.status}
              onChange={handleInputChange}
              options={STUDENT_STATUS_OPTIONS}
              error={formErrors.status}
              required
            />

            <Textarea
              label="Address"
              name="address"
              value={formData.address}
              onChange={handleInputChange}
              placeholder="Enter full address (minimum 10 characters)"
              error={formErrors.address}
              className="col-span-2"
              rows={3}
              required
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
              {selectedStudent ? 'Update' : 'Create'} Student
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        isOpen={isDeleteDialogOpen}
        onClose={() => {
          setIsDeleteDialogOpen(false);
          setSelectedStudent(null);
        }}
        onConfirm={handleDelete}
        title="Delete Student"
        message={`Are you sure you want to delete ${selectedStudent?.firstName} ${selectedStudent?.lastName}? This action cannot be undone.`}
        confirmText="Delete"
        type="danger"
      />
    </div>
  );
};

export default StudentManagement;
