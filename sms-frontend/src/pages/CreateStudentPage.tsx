import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useCreateStudent } from '@/features/students/hooks/useCreateStudent';
import { type StudentFormData } from '@/schemas/studentSchema';
import { type CreateStudentRequest } from '@/types/student.types';
import StudentForm from '@/features/students/components/StudentForm';

const CreateStudentPage: React.FC = () => {
  const navigate = useNavigate();
  const createStudent = useCreateStudent();

  const handleSubmit = (data: StudentFormData) => {
    const request: CreateStudentRequest = {
      firstName: data.firstName,
      lastName: data.lastName,
      address: data.address,
      mobile: data.mobile,
      dateOfBirth: data.dateOfBirth,
      fatherName: data.fatherName,
      motherName: data.motherName || undefined,
      identificationMark: data.identificationMark || undefined,
      email: data.email || undefined,
      aadhaarNumber: data.aadhaarNumber || undefined,
    };

    createStudent.mutate(request);
  };

  const handleCancel = () => {
    navigate('/students');
  };

  return (
    <div className="max-w-4xl mx-auto">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Register New Student</h1>
        <p className="text-gray-600 dark:text-gray-400 mt-1">
          Fill in the student information to complete registration
        </p>
      </div>

      <StudentForm
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        isLoading={createStudent.isPending}
      />
    </div>
  );
};

export default CreateStudentPage;
