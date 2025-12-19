import React, { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import Card from '../components/Card';
import Input from '../components/Input';
import Button from '../components/Button';
import { searchStudents } from '../services/studentService';
import { calculateFee, recordPayment, generateReceipt } from '../services/feeService';

const FeeReceipt = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [feeDetails, setFeeDetails] = useState(null);
  const [paymentAmount, setPaymentAmount] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (searchQuery.length > 2) {
      const debounce = setTimeout(async () => {
        try {
          const results = await searchStudents(searchQuery);
          setSuggestions(results.data);
        } catch (_error) {
          console.error('Search failed:', _error);
        }
      }, 300);

      return () => clearTimeout(debounce);
    } else {
      setSuggestions([]);
    }
  }, [searchQuery]);

  const handleSelectStudent = async (student) => {
    setSelectedStudent(student);
    setSearchQuery(`${student.firstName} ${student.lastName}`);
    setSuggestions([]);

    // Calculate fee for the student
    try {
      const response = await calculateFee(student.id, {
        month: new Date().getMonth() + 1,
        year: new Date().getFullYear()
      });
      setFeeDetails(response.data);
      setPaymentAmount(response.data.totalAmount);
    } catch (_error) {
      toast.error('Failed to calculate fee');
    }
  };

  const handlePayment = async () => {
    if (!selectedStudent || !paymentAmount) {
      toast.error('Please select a student and enter payment amount');
      return;
    }

    setLoading(true);
    try {
      const paymentData = {
        studentId: selectedStudent.id,
        amount: parseFloat(paymentAmount),
        paymentDate: new Date().toISOString(),
        month: new Date().getMonth() + 1,
        year: new Date().getFullYear()
      };

      const response = await recordPayment(paymentData);
      toast.success('Payment recorded successfully');

      // Generate receipt
      if (response.data.id) {
        await handleGenerateReceipt(response.data.id);
      }

      // Reset form
      setSearchQuery('');
      setSelectedStudent(null);
      setFeeDetails(null);
      setPaymentAmount('');
    } catch (_error) {
      const message = _error.response?.data?.message || 'Payment failed';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateReceipt = async (paymentId) => {
    try {
      const response = await generateReceipt(paymentId);

      // Create blob and download
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `receipt_${paymentId}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);

      toast.success('Receipt generated successfully');
    } catch (_error) {
      toast.error('Failed to generate receipt');
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <Card title="Fee Receipt">
        <div className="mb-6">
          <div className="relative">
            <Input
              label="Search Student"
              name="search"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Type student name to search..."
            />

            {suggestions.length > 0 && (
              <div className="absolute z-10 w-full bg-white border border-gray-300 rounded-md shadow-lg mt-1 max-h-60 overflow-y-auto">
                {suggestions.map((student) => (
                  <div
                    key={student.id}
                    className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                    onClick={() => handleSelectStudent(student)}
                  >
                    <div className="font-medium">
                      {student.firstName} {student.lastName}
                    </div>
                    <div className="text-sm text-gray-500">
                      Class: {student.className} | Mobile: {student.mobile}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {selectedStudent && feeDetails && (
          <div className="space-y-6">
            <div className="bg-blue-50 p-4 rounded-lg">
              <h3 className="text-lg font-semibold mb-2">Student Information</h3>
              <div className="grid grid-cols-2 gap-2 text-sm">
                <div>
                  <span className="font-medium">Name:</span> {selectedStudent.firstName} {selectedStudent.lastName}
                </div>
                <div>
                  <span className="font-medium">Class:</span> {selectedStudent.className}
                </div>
                <div>
                  <span className="font-medium">Mobile:</span> {selectedStudent.mobile}
                </div>
                <div>
                  <span className="font-medium">Father's Name:</span> {selectedStudent.fatherName}
                </div>
              </div>
            </div>

            <div className="bg-green-50 p-4 rounded-lg">
              <h3 className="text-lg font-semibold mb-2">Fee Details</h3>
              <div className="space-y-2">
                {feeDetails.feeBreakdown && feeDetails.feeBreakdown.map((item, index) => (
                  <div key={index} className="flex justify-between">
                    <span>{item.type}</span>
                    <span className="font-medium">₹{item.amount}</span>
                  </div>
                ))}
                <div className="border-t pt-2 mt-2 flex justify-between text-lg font-bold">
                  <span>Total Amount:</span>
                  <span>₹{feeDetails.totalAmount}</span>
                </div>
              </div>
            </div>

            <div>
              <Input
                label="Payment Amount"
                name="paymentAmount"
                type="number"
                value={paymentAmount}
                onChange={(e) => setPaymentAmount(e.target.value)}
                placeholder="Enter payment amount"
                required
              />
            </div>

            <div className="flex gap-4">
              <Button onClick={handlePayment} disabled={loading}>
                {loading ? 'Processing...' : 'Record Payment & Generate Receipt'}
              </Button>
              <Button
                variant="secondary"
                onClick={() => {
                  setSearchQuery('');
                  setSelectedStudent(null);
                  setFeeDetails(null);
                  setPaymentAmount('');
                }}
              >
                Clear
              </Button>
            </div>
          </div>
        )}
      </Card>
    </div>
  );
};

export default FeeReceipt;
