import React, { useState } from 'react';
import toast from 'react-hot-toast';
import Card from '../components/Card';
import Input from '../components/Input';
import Button from '../components/Button';
import Loading from '../components/Loading';
import { getStudent } from '../services/studentService';
import { getPendingFees, getFeeJournal } from '../services/feeService';

const ParentPortal = () => {
  const [studentId, setStudentId] = useState('');
  const [mobile, setMobile] = useState('');
  const [student, setStudent] = useState(null);
  const [pendingFees, setPendingFees] = useState(null);
  const [feeJournal, setFeeJournal] = useState([]);
  const [loading, setLoading] = useState(false);
  const [authenticated, setAuthenticated] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!studentId || !mobile) {
      toast.error('Please enter student ID and mobile number');
      return;
    }

    setLoading(true);
    try {
      // Fetch student details
      const studentResponse = await getStudent(studentId);

      // Verify mobile number
      if (studentResponse.data.mobile !== mobile) {
        toast.error('Invalid mobile number');
        setLoading(false);
        return;
      }

      setStudent(studentResponse.data);
      setAuthenticated(true);

      // Fetch pending fees
      const pendingResponse = await getPendingFees(studentId);
      setPendingFees(pendingResponse.data);

      // Fetch fee journal
      const journalResponse = await getFeeJournal(studentId);
      setFeeJournal(journalResponse.data);

      toast.success('Login successful');
    } catch (_error) {
      const message = _error.response?.data?.message || 'Login failed';
      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    setStudentId('');
    setMobile('');
    setStudent(null);
    setPendingFees(null);
    setFeeJournal([]);
    setAuthenticated(false);
  };

  if (!authenticated) {
    return (
      <div className="max-w-md mx-auto mt-20">
        <Card title="Parent Portal Login">
          <form onSubmit={handleLogin}>
            <Input
              label="Student ID"
              name="studentId"
              value={studentId}
              onChange={(e) => setStudentId(e.target.value)}
              placeholder="Enter student ID"
              required
            />

            <Input
              label="Mobile Number"
              name="mobile"
              type="tel"
              value={mobile}
              onChange={(e) => setMobile(e.target.value)}
              placeholder="Enter registered mobile number"
              required
            />

            <div className="mt-6">
              <Button type="submit" fullWidth disabled={loading}>
                {loading ? 'Logging in...' : 'Login'}
              </Button>
            </div>
          </form>
        </Card>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Parent Portal</h1>
        <Button variant="secondary" onClick={handleLogout}>
          Logout
        </Button>
      </div>

      {student && (
        <Card title="Student Information" className="mb-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <span className="font-medium">Name:</span> {student.firstName} {student.lastName}
            </div>
            <div>
              <span className="font-medium">Class:</span> {student.className}
            </div>
            <div>
              <span className="font-medium">DOB:</span> {student.dob}
            </div>
            <div>
              <span className="font-medium">Mobile:</span> {student.mobile}
            </div>
            <div>
              <span className="font-medium">Father's Name:</span> {student.fatherName}
            </div>
            <div>
              <span className="font-medium">Mother's Name:</span> {student.motherName}
            </div>
          </div>
        </Card>
      )}

      {pendingFees && (
        <Card title="Pending Fees" className="mb-6">
          <div className="bg-yellow-50 p-6 rounded-lg">
            <div className="text-center">
              <p className="text-sm text-gray-600 mb-2">Total Pending Amount</p>
              <p className="text-4xl font-bold text-yellow-700">₹{pendingFees.totalPending}</p>

              {pendingFees.breakdown && pendingFees.breakdown.length > 0 && (
                <div className="mt-4 text-left">
                  <p className="font-semibold mb-2">Breakdown:</p>
                  <div className="space-y-1">
                    {pendingFees.breakdown.map((item, index) => (
                      <div key={index} className="flex justify-between">
                        <span>{item.month}/{item.year}</span>
                        <span className="font-medium">₹{item.amount}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>

            <div className="mt-6 text-center">
              <Button variant="success">
                Pay Online
              </Button>
            </div>
          </div>
        </Card>
      )}

      <Card title="Payment History">
        {loading ? (
          <Loading text="Loading payment history..." />
        ) : feeJournal.length === 0 ? (
          <p className="text-gray-500 text-center py-8">No payment history available</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Date
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Month/Year
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Amount Paid
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Receipt
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {feeJournal.map((entry) => (
                  <tr key={entry.id}>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {new Date(entry.paymentDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {entry.month}/{entry.year}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      ₹{entry.amount}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <Button
                        variant="outline"
                        className="text-sm"
                        onClick={() => toast.info('Receipt download feature coming soon')}
                      >
                        Download
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

export default ParentPortal;
