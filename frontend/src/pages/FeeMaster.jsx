import React, { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import Card from '../components/Card';
import Input from '../components/Input';
import Select from '../components/Select';
import Button from '../components/Button';
import Loading from '../components/Loading';
import { getFeeMaster, createFeeMaster, updateFeeMaster, deleteFeeMaster } from '../services/feeService';
import { getAllClasses } from '../services/classService';

const FeeMaster = () => {
  const [formData, setFormData] = useState({
    classId: '',
    amount: '',
    feeType: 'TUITION',
    frequency: 'MONTHLY',
    description: ''
  });

  const [feeMasters, setFeeMasters] = useState([]);
  const [classes, setClasses] = useState([]);
  const [loading, setLoading] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [editId, setEditId] = useState(null);

  const feeTypes = [
    { value: 'TUITION', label: 'Tuition Fee' },
    { value: 'LIBRARY', label: 'Library Fee' },
    { value: 'COMPUTER', label: 'Computer Fee' },
    { value: 'SPORTS', label: 'Sports Fee' },
    { value: 'SPECIAL', label: 'Special Fee' },
    { value: 'OTHER', label: 'Other' }
  ];

  const frequencies = [
    { value: 'MONTHLY', label: 'Monthly' },
    { value: 'QUARTERLY', label: 'Quarterly' },
    { value: 'YEARLY', label: 'Yearly' }
  ];

  useEffect(() => {
    fetchFeeMasters();
    fetchClasses();
  }, []);

  const fetchFeeMasters = async () => {
    setLoading(true);
    try {
      const response = await getFeeMaster();
      setFeeMasters(response.data);
    } catch (_error) {
      toast.error('Failed to load fee masters');
    } finally {
      setLoading(false);
    }
  };

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
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      if (editMode) {
        await updateFeeMaster(editId, formData);
        toast.success('Fee master updated successfully');
      } else {
        await createFeeMaster(formData);
        toast.success('Fee master created successfully');
      }

      // Reset form
      setFormData({
        classId: '',
        amount: '',
        feeType: 'TUITION',
        frequency: 'MONTHLY',
        description: ''
      });
      setEditMode(false);
      setEditId(null);
      fetchFeeMasters();
    } catch (_error) {
      const message = _error.response?.data?.message || 'Operation failed';
      toast.error(message);
    }
  };

  const handleEdit = (feeMaster) => {
    setFormData({
      classId: feeMaster.classId,
      amount: feeMaster.amount,
      feeType: feeMaster.feeType,
      frequency: feeMaster.frequency,
      description: feeMaster.description || ''
    });
    setEditMode(true);
    setEditId(feeMaster.id);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this fee master?')) {
      try {
        await deleteFeeMaster(id);
        toast.success('Fee master deleted successfully');
        fetchFeeMasters();
      } catch (_error) {
        toast.error('Failed to delete fee master');
      }
    }
  };

  const cancelEdit = () => {
    setFormData({
      classId: '',
      amount: '',
      feeType: 'TUITION',
      frequency: 'MONTHLY',
      description: ''
    });
    setEditMode(false);
    setEditId(null);
  };

  return (
    <div className="max-w-6xl mx-auto">
      <Card title={editMode ? 'Edit Fee Master' : 'Create Fee Master'}>
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Select
              label="Class"
              name="classId"
              value={formData.classId}
              onChange={handleChange}
              options={classes}
              required
            />

            <Input
              label="Amount"
              name="amount"
              type="number"
              value={formData.amount}
              onChange={handleChange}
              required
              placeholder="Enter amount"
            />

            <Select
              label="Fee Type"
              name="feeType"
              value={formData.feeType}
              onChange={handleChange}
              options={feeTypes}
              required
            />

            <Select
              label="Frequency"
              name="frequency"
              value={formData.frequency}
              onChange={handleChange}
              options={frequencies}
              required
            />
          </div>

          <div className="mt-4">
            <Input
              label="Description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              placeholder="Optional description"
            />
          </div>

          <div className="mt-6 flex gap-4">
            <Button type="submit">
              {editMode ? 'Update Fee Master' : 'Create Fee Master'}
            </Button>
            {editMode && (
              <Button type="button" variant="secondary" onClick={cancelEdit}>
                Cancel
              </Button>
            )}
          </div>
        </form>
      </Card>

      <Card title="Fee Masters List" className="mt-6">
        {loading ? (
          <Loading text="Loading fee masters..." />
        ) : feeMasters.length === 0 ? (
          <p className="text-gray-500 text-center py-8">No fee masters configured</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Class
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Fee Type
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Amount
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Frequency
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {feeMasters.map((fee) => (
                  <tr key={fee.id}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {fee.className}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {fee.feeType}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      ₹{fee.amount}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {fee.frequency}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap space-x-2">
                      <Button
                        onClick={() => handleEdit(fee)}
                        variant="outline"
                        className="text-sm"
                      >
                        Edit
                      </Button>
                      <Button
                        onClick={() => handleDelete(fee.id)}
                        variant="danger"
                        className="text-sm"
                      >
                        Delete
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  );
};

export default FeeMaster;
