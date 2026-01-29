import { useEffect, useState } from 'react';
import { toast } from 'sonner';
import { format } from 'date-fns';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from './ui/dialog';
import { Button } from './ui/button';
import { studentApi, type StudentResponse, type Enrollment } from '../services/api/studentApi';
import type { Student } from '../types';

interface ViewStudentDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  student: Student | null;
}

export function ViewStudentDialog({ open, onOpenChange, student }: ViewStudentDialogProps) {
  const [loading, setLoading] = useState(false);
  const [studentDetails, setStudentDetails] = useState<StudentResponse | null>(null);
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);

  useEffect(() => {
    if (student && open) {
      fetchStudentDetails(student.id);
    }
  }, [student, open]);

  const fetchStudentDetails = async (studentId: string) => {
    setLoading(true);
    try {
      const [details, enrollmentHistory] = await Promise.all([
        studentApi.getStudent(studentId),
        studentApi.getEnrollmentHistory(studentId),
      ]);
      setStudentDetails(details);
      setEnrollments(enrollmentHistory.enrollments);
    } catch (error) {
      toast.error('Failed to load student details');
    } finally {
      setLoading(false);
    }
  };

  if (!student || !studentDetails) {
    return (
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent className="max-w-3xl">
          <DialogHeader>
            <DialogTitle>Student Details</DialogTitle>
          </DialogHeader>
          <div className="py-8 text-center text-gray-500">
            {loading ? 'Loading student details...' : 'No student selected'}
          </div>
        </DialogContent>
      </Dialog>
    );
  }

  const age = Math.floor(
    (Date.now() - new Date(studentDetails.dateOfBirth).getTime()) / (365.25 * 24 * 60 * 60 * 1000)
  );

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Student Details</DialogTitle>
        </DialogHeader>

        <div className="space-y-6">
          {/* Basic Information */}
          <div>
            <h3 className="text-base font-medium mb-4">Basic Information</h3>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm text-gray-600">Student ID</label>
                  <p className="mt-1 font-medium">{studentDetails.studentId}</p>
                </div>
                <div>
                  <label className="text-sm text-gray-600">Status</label>
                  <p className="mt-1">
                    <span
                      className={`px-2 py-1 text-xs rounded ${
                        studentDetails.status === 'ACTIVE'
                          ? 'bg-green-100 text-green-800'
                          : 'bg-gray-100 text-gray-800'
                      }`}
                    >
                      {studentDetails.status}
                    </span>
                  </p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm text-gray-600">First Name</label>
                  <p className="mt-1">{studentDetails.firstName}</p>
                </div>
                <div>
                  <label className="text-sm text-gray-600">Last Name</label>
                  <p className="mt-1">{studentDetails.lastName}</p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm text-gray-600">Date of Birth</label>
                  <p className="mt-1">
                    {format(new Date(studentDetails.dateOfBirth), 'MMMM d, yyyy')}
                  </p>
                </div>
                <div>
                  <label className="text-sm text-gray-600">Age</label>
                  <p className="mt-1">{age} years</p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm text-gray-600">Identification Marks</label>
                  <p className="mt-1">{studentDetails.identificationMark || 'N/A'}</p>
                </div>
                {studentDetails.aadhaarNumber && (
                  <div>
                    <label className="text-sm text-gray-600">Aadhaar Number</label>
                    <p className="mt-1">{studentDetails.aadhaarNumber}</p>
                  </div>
                )}
              </div>

              {studentDetails.address && (
                <div>
                  <label className="text-sm text-gray-600">Address</label>
                  <p className="mt-1 whitespace-pre-wrap">{studentDetails.address}</p>
                </div>
              )}
            </div>
          </div>

          {/* Guardian Information */}
          <div className="border-t pt-6">
            <h3 className="text-base font-medium mb-4">Guardian Information</h3>
            <div className="space-y-4">
              {studentDetails.fathersName && (
                <div>
                  <label className="text-sm text-gray-600">Father's Name</label>
                  <p className="mt-1">{studentDetails.fathersName}</p>
                </div>
              )}

              {studentDetails.mothersName && (
                <div>
                  <label className="text-sm text-gray-600">Mother's Name</label>
                  <p className="mt-1">{studentDetails.mothersName}</p>
                </div>
              )}

              {!studentDetails.fathersName && !studentDetails.mothersName && (
                <p className="text-sm text-gray-500">No guardian information available</p>
              )}
            </div>
          </div>

          {/* Contact Information */}
          <div className="border-t pt-6">
            <h3 className="text-base font-medium mb-4">Contact Information</h3>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm text-gray-600">Mobile Number</label>
                  <p className="mt-1">{studentDetails.mobile}</p>
                </div>
                {studentDetails.email && (
                  <div>
                    <label className="text-sm text-gray-600">Email</label>
                    <p className="mt-1">{studentDetails.email}</p>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Enrollment History */}
          <div className="border-t pt-6">
            <h3 className="text-base font-medium mb-4">Enrollment History</h3>
            {enrollments.length > 0 ? (
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b">
                      <th className="text-left py-2 px-2">Academic Year</th>
                      <th className="text-left py-2 px-2">Standard</th>
                      <th className="text-left py-2 px-2">Section</th>
                      <th className="text-left py-2 px-2">Roll Number</th>
                      <th className="text-left py-2 px-2">Enrollment Date</th>
                      <th className="text-left py-2 px-2">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {enrollments.map((enrollment) => (
                      <tr key={enrollment.id} className="border-b">
                        <td className="py-2 px-2">{enrollment.academicYear}</td>
                        <td className="py-2 px-2">{enrollment.standard}</td>
                        <td className="py-2 px-2">{enrollment.section}</td>
                        <td className="py-2 px-2">{enrollment.rollNumber || 'N/A'}</td>
                        <td className="py-2 px-2">
                          {format(new Date(enrollment.enrollmentDate), 'MMM d, yyyy')}
                        </td>
                        <td className="py-2 px-2">
                          <span
                            className={`px-2 py-1 text-xs rounded ${
                              enrollment.status === 'ACTIVE'
                                ? 'bg-green-100 text-green-800'
                                : 'bg-gray-100 text-gray-800'
                            }`}
                          >
                            {enrollment.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p className="text-sm text-gray-500">No enrollment history available</p>
            )}
          </div>

          {/* Metadata */}
          <div className="border-t pt-6">
            <h3 className="text-base font-medium mb-4">System Information</h3>
            <div className="grid grid-cols-2 gap-4 text-sm text-gray-600">
              <div>
                <label className="text-xs">Created At</label>
                <p className="mt-1">{format(new Date(studentDetails.createdAt), 'PPpp')}</p>
              </div>
              <div>
                <label className="text-xs">Last Updated</label>
                <p className="mt-1">{format(new Date(studentDetails.updatedAt), 'PPpp')}</p>
              </div>
            </div>
          </div>
        </div>

        <div className="flex justify-end pt-4 border-t">
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            Close
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
