import React, { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import Card from '../components/Card';
import Input from '../components/Input';
import Select from '../components/Select';
import Button from '../components/Button';
import Loading from '../components/Loading';
import { getSchoolConfig, saveSchoolConfig } from '../services/schoolService';

const SchoolConfig = () => {
  const [formData, setFormData] = useState({
    schoolName: '',
    address: '',
    city: '',
    state: '',
    pincode: '',
    phone: '',
    email: '',
    feeFrequency: 'MONTHLY',
    principalName: '',
    affiliationNumber: ''
  });

  const [loading, setLoading] = useState(false);
  const [configExists, setConfigExists] = useState(false);

  const frequencies = [
    { value: 'MONTHLY', label: 'Monthly' },
    { value: 'QUARTERLY', label: 'Quarterly' },
    { value: 'YEARLY', label: 'Yearly' }
  ];

  useEffect(() => {
    fetchConfig();
  }, []);

  const fetchConfig = async () => {
    setLoading(true);
    try {
      const response = await getSchoolConfig();
      if (response.data) {
        setFormData(response.data);
        setConfigExists(true);
      }
    } catch (_error) {
      // Config doesn't exist yet
      console.log('No config found, creating new');
    } finally {
      setLoading(false);
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

    setLoading(true);
    try {
      await saveSchoolConfig(formData);
      toast.success('School configuration saved successfully');
      setConfigExists(true);
    } catch (_error) {
      const message = _error.response?.data?.message || 'Failed to save configuration';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  if (loading && !formData.schoolName) {
    return <Loading text="Loading configuration..." />;
  }

  return (
    <div className="max-w-4xl mx-auto">
      <Card title="School Configuration">
        <form onSubmit={handleSubmit}>
          <div className="space-y-6">
            {/* Basic Information */}
            <div>
              <h3 className="text-lg font-semibold mb-4 text-blue-600">Basic Information</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <Input
                  label="School Name"
                  name="schoolName"
                  value={formData.schoolName}
                  onChange={handleChange}
                  required
                  placeholder="Enter school name"
                  className="md:col-span-2"
                />

                <Input
                  label="Principal Name"
                  name="principalName"
                  value={formData.principalName}
                  onChange={handleChange}
                  placeholder="Enter principal name"
                />

                <Input
                  label="Affiliation Number"
                  name="affiliationNumber"
                  value={formData.affiliationNumber}
                  onChange={handleChange}
                  placeholder="Enter affiliation number"
                />
              </div>
            </div>

            {/* Contact Information */}
            <div>
              <h3 className="text-lg font-semibold mb-4 text-blue-600">Contact Information</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <Input
                  label="Phone Number"
                  name="phone"
                  type="tel"
                  value={formData.phone}
                  onChange={handleChange}
                  required
                  placeholder="Enter phone number"
                />

                <Input
                  label="Email Address"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                  required
                  placeholder="Enter email address"
                />
              </div>
            </div>

            {/* Address Information */}
            <div>
              <h3 className="text-lg font-semibold mb-4 text-blue-600">Address</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <Input
                  label="Street Address"
                  name="address"
                  value={formData.address}
                  onChange={handleChange}
                  required
                  placeholder="Enter street address"
                  className="md:col-span-2"
                />

                <Input
                  label="City"
                  name="city"
                  value={formData.city}
                  onChange={handleChange}
                  required
                  placeholder="Enter city"
                />

                <Input
                  label="State"
                  name="state"
                  value={formData.state}
                  onChange={handleChange}
                  required
                  placeholder="Enter state"
                />

                <Input
                  label="PIN Code"
                  name="pincode"
                  value={formData.pincode}
                  onChange={handleChange}
                  required
                  placeholder="Enter PIN code"
                />
              </div>
            </div>

            {/* Fee Configuration */}
            <div>
              <h3 className="text-lg font-semibold mb-4 text-blue-600">Fee Configuration</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <Select
                  label="Fee Frequency"
                  name="feeFrequency"
                  value={formData.feeFrequency}
                  onChange={handleChange}
                  options={frequencies}
                  required
                />
              </div>
            </div>
          </div>

          <div className="mt-8 flex gap-4">
            <Button type="submit" disabled={loading}>
              {loading ? 'Saving...' : configExists ? 'Update Configuration' : 'Save Configuration'}
            </Button>
            <Button
              type="button"
              variant="secondary"
              onClick={() => fetchConfig()}
            >
              Reset
            </Button>
          </div>
        </form>

        {configExists && (
          <div className="mt-6 p-4 bg-green-50 border border-green-200 rounded-lg">
            <p className="text-green-800">
              <strong>Configuration Status:</strong> School configuration is active
            </p>
          </div>
        )}
      </Card>
    </div>
  );
};

export default SchoolConfig;
