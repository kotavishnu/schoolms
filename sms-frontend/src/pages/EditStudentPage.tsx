import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useStudent } from '@/features/students/hooks/useStudent';
import { useUpdateStudent } from '@/features/students/hooks/useUpdateStudent';
import { type StudentFormData } from '@/schemas/studentSchema';
import { type UpdateStudentRequest } from '@/types/student.types';
import StudentForm from '@/features/students/components/StudentForm';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import Button from '@/components/common/Button';

const EditStudentPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const studentId = parseInt(id || '0', 10);

  const { data: student, isLoading, isError, error } = useStudent(studentId);
  const updateStudent = useUpdateStudent(studentId);

  const handleSubmit = (data: StudentFormData) => {
    if (!student) return;

    const request: UpdateStudentRequest = {
      firstName: data.firstName,
      lastName: data.lastName,
      mobile: data.mobile,
      status: student.status, // Keep existing status
      version: student.version,
    };

    updateStudent.mutate(request, {
      onSuccess: () => {
        navigate(`/students/${studentId}`);
      },
    });
  };

  const handleCancel = () => {
    navigate(`/students/${studentId}`);
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-96">
        <LoadingSpinner message="Loading student details..." />
      </div>
    );
  }

  if (isError || !student) {
    return (
      <div className="text-center py-12">
        <div className="text-red-600 dark:text-red-400 mb-4">
          <p className="text-lg font-semibold">Student not found</p>
          <p className="text-sm mt-2">
            {(error as any)?.message || 'The requested student could not be found'}
          </p>
        </div>
        <Button variant="primary" onClick={() => navigate('/students')}>
          Back to Students
        </Button>
      </div>
    );
  }

  const initialData: Partial<StudentFormData> = {
    firstName: student.firstName,
    lastName: student.lastName,
    address: student.address,
    mobile: student.mobile,
    dateOfBirth: student.dateOfBirth,
    fatherName: student.fatherName,
    motherName: student.motherName || '',
    identificationMark: student.identificationMark || '',
    email: student.email || '',
    aadhaarNumber: student.aadhaarNumber || '',
  };

  return (
    <div className="max-w-4xl mx-auto">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Edit Student</h1>
        <p className="text-gray-600 dark:text-gray-400 mt-1">
          Update information for {student.firstName} {student.lastName}
        </p>
      </div>

      <StudentForm
        initialData={initialData}
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        isLoading={updateStudent.isPending}
      />
    </div>
  );
};

export default EditStudentPage;
