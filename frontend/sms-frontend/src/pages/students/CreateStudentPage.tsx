import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { studentApi } from '@/api/studentApi';
import { useToast } from '@/components/ui/Toast';
import StudentForm from '@/components/features/students/StudentForm';
import { CreateStudentRequest } from '@/types/student';

export default function CreateStudentPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  const createMutation = useMutation({
    mutationFn: studentApi.createStudent,
    onSuccess: (student) => {
      queryClient.invalidateQueries({ queryKey: ['students'] });
      showToast({
        type: 'success',
        title: 'Success',
        description: `Student ${student.studentId} created successfully`,
      });
      navigate('/students');
    },
    onError: (error: any) => {
      showToast({
        type: 'error',
        title: error.problemDetail?.title || 'Error',
        description: error.problemDetail?.detail || 'Failed to create student',
      });
    },
  });

  const handleSubmit = (data: CreateStudentRequest) => {
    createMutation.mutate(data);
  };

  const handleCancel = () => {
    navigate('/students');
  };

  return (
    <div className="max-w-4xl mx-auto">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Register New Student</h1>
        <p className="mt-2 text-sm text-gray-600">Fill in the details to register a new student</p>
      </div>

      <StudentForm
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        isLoading={createMutation.isPending}
      />
    </div>
  );
}
