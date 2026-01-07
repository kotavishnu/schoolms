import React, { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import Card from '../components/Card';
import Input from '../components/Input';
import Select from '../components/Select';
import Button from '../components/Button';
import { createStudent } from '../services/studentService';
import { getAllClasses } from '../services/classService';

const StudentRegistration = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    dob: '',
    address: '',
    caste: '',
    mobile: '',
    religion: '',
    molesOnBody: '',
    motherName: '',
    fatherName: '',
    classId: ''
  });

  const [classes, setClasses] = useState([]);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    fetchClasses();
  }, []);

  const fetchClasses = async () => {
    try {
      const response = await getAllClasses();
      setClasses(response.data.map(cls => ({
        value: cls.id,
        label: cls.name || `Class ${cls.classNumber}`
      })));
    } catch (_error) {
      toast.error('Failed to load classes');
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear error for this field when user types
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.firstName.trim()) {
      newErrors.firstName = 'First name is required';
    }
    if (!formData.lastName.trim()) {
      newErrors.lastName = 'Last name is required';
    }
    if (!formData.dob) {
      newErrors.dob = 'Date of birth is required';
    }
    if (!formData.mobile.trim()) {
      newErrors.mobile = 'Mobile number is required';
    } else if (!/^\d{10}$/.test(formData.mobile)) {
      newErrors.mobile = 'Mobile number must be 10 digits';
    }
    if (!formData.classId) {
      newErrors.classId = 'Please select a class';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      toast.error('Please fix the errors in the form');
      return;
    }

    setLoading(true);
    try {
      await createStudent(formData);
      toast.success('Student registered successfully!');
      // Reset form
      setFormData({
        firstName: '',
        lastName: '',
        dob: '',
        address: '',
        caste: '',
        mobile: '',
        religion: '',
        molesOnBody: '',
        motherName: '',
        fatherName: '',
        classId: ''
      });
    } catch (_error) {
      const message = _error.response?.data?.message || 'Failed to register student';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <Card title="Student Registration">
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input
              label="First Name"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              required
              error={errors.firstName}
              placeholder="Enter first name"
            />

            <Input
              label="Last Name"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              required
              error={errors.lastName}
              placeholder="Enter last name"
            />

            <Input
              label="Date of Birth"
              name="dob"
              type="date"
              value={formData.dob}
              onChange={handleChange}
              required
              error={errors.dob}
            />

            <Input
              label="Mobile Number"
              name="mobile"
              type="tel"
              value={formData.mobile}
              onChange={handleChange}
              required
              error={errors.mobile}
              placeholder="10-digit mobile number"
            />

            <Input
              label="Father's Name"
              name="fatherName"
              value={formData.fatherName}
              onChange={handleChange}
              placeholder="Enter father's name"
            />

            <Input
              label="Mother's Name"
              name="motherName"
              value={formData.motherName}
              onChange={handleChange}
              placeholder="Enter mother's name"
            />

            <Input
              label="Caste"
              name="caste"
              value={formData.caste}
              onChange={handleChange}
              placeholder="Enter caste"
            />

            <Input
              label="Religion"
              name="religion"
              value={formData.religion}
              onChange={handleChange}
              placeholder="Enter religion"
            />

            <Input
              label="Moles on Body"
              name="molesOnBody"
              value={formData.molesOnBody}
              onChange={handleChange}
              placeholder="Identification marks"
            />

            <Select
              label="Class"
              name="classId"
              value={formData.classId}
              onChange={handleChange}
              options={classes}
              required
              error={errors.classId}
              placeholder="Select class"
            />
          </div>

          <div className="mt-4">
            <Input
              label="Address"
              name="address"
              value={formData.address}
              onChange={handleChange}
              placeholder="Enter full address"
            />
          </div>

          <div className="mt-6 flex gap-4">
            <Button type="submit" disabled={loading}>
              {loading ? 'Registering...' : 'Register Student'}
            </Button>
            <Button
              type="button"
              variant="secondary"
              onClick={() => {
                setFormData({
                  firstName: '',
                  lastName: '',
                  dob: '',
                  address: '',
                  caste: '',
                  mobile: '',
                  religion: '',
                  molesOnBody: '',
                  motherName: '',
                  fatherName: '',
                  classId: ''
                });
                setErrors({});
              }}
            >
              Clear Form
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};

export default StudentRegistration;
