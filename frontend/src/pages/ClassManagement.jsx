import React, { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import Card from '../components/Card';
import Button from '../components/Button';
import Loading from '../components/Loading';
import { getAllClasses, getClassStudents } from '../services/classService';

const ClassManagement = () => {
  const [classes, setClasses] = useState([]);
  const [selectedClass, setSelectedClass] = useState(null);
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [studentsLoading, setStudentsLoading] = useState(false);

  useEffect(() => {
    fetchClasses();
  }, []);

  const fetchClasses = async () => {
    setLoading(true);
    try {
      const response = await getAllClasses();
      setClasses(response.data);
    } catch (_error) {
      toast.error('Failed to load classes');
    } finally {
      setLoading(false);
    }
  };

  const handleViewStudents = async (classItem) => {
    setSelectedClass(classItem);
    setStudentsLoading(true);
    try {
      const response = await getClassStudents(classItem.id);
      setStudents(response.data);
    } catch (_error) {
      toast.error('Failed to load students');
      setStudents([]);
    } finally {
      setStudentsLoading(false);
    }
  };

  if (loading) {
    return <Loading text="Loading classes..." />;
  }

  return (
    <div className="max-w-6xl mx-auto">
      <Card title="Class Management">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {classes.map((classItem) => (
            <div
              key={classItem.id}
              className="border border-gray-300 rounded-lg p-4 hover:shadow-md transition"
            >
              <h3 className="text-xl font-semibold mb-2">
                {classItem.name || `Class ${classItem.classNumber}`}
              </h3>
              <p className="text-gray-600 mb-2">
                Students: {classItem.studentCount || 0}
              </p>
              <p className="text-gray-600 mb-4">
                Class Number: {classItem.classNumber}
              </p>
              <Button
                onClick={() => handleViewStudents(classItem)}
                variant="outline"
                fullWidth
              >
                View Students
              </Button>
            </div>
          ))}
        </div>
      </Card>

      {selectedClass && (
        <Card title={`Students in ${selectedClass.name || `Class ${selectedClass.classNumber}`}`} className="mt-6">
          {studentsLoading ? (
            <Loading text="Loading students..." />
          ) : students.length === 0 ? (
            <p className="text-gray-500 text-center py-8">No students in this class</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Name
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      DOB
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Mobile
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Father's Name
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {students.map((student) => (
                    <tr key={student.id}>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {student.firstName} {student.lastName}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {student.dob}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {student.mobile}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        {student.fatherName}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      )}
    </div>
  );
};

export default ClassManagement;
