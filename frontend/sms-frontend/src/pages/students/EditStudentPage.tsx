import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { studentApi } from '@/api/studentApi';
import { useToast } from '@/components/ui/Toast';
import StudentForm from '@/components/features/students/StudentForm';
import Loading from '@/components/common/Loading';
import ErrorDisplay from '@/components/common/ErrorDisplay';
import { UpdateStudentRequest } from '@/types/student';

export default function EditStudentPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  const { data: student, isLoading, error } = useQuery({
    queryKey: ['student', id],
    queryFn: () => studentApi.getStudentById(id!),
    enabled: !!id,
  });

  const updateMutation = useMutation({
    mutationFn: (data: UpdateStudentRequest) => studentApi.updateStudent(id!, data),
    onSuccess: (updatedStudent) => {
      queryClient.invalidateQueries({ queryKey: ['student', id] });
      queryClient.invalidateQueries({ queryKey: ['students'] });
      showToast({
        type: 'success',
        title: 'Success',
        description: `Student ${updatedStudent.studentId} updated successfully`,
      });
      navigate(`/students/${id}`);
    },
    onError: (error: any) => {
      if (error.statusCode === 409) {
        showToast({
          type: 'error',
          title: 'Concurrent Update',
          description: 'This student was modified by another user. Please refresh and try again.',
        });
      } else {
        showToast({
          type: 'error',
          title: error.problemDetail?.title || 'Error',
          description: error.problemDetail?.detail || 'Failed to update student',
        });
      }
    },
  });

  const handleSubmit = (data: any) => {
    if (student) {
      const updateData: UpdateStudentRequest = {
        ...data,
        version: student.version || 0,
      };
      updateMutation.mutate(updateData);
    }
  };

  const handleCancel = () => {
    navigate(`/students/${id}`);
  };

  if (isLoading) return <Loading />;
  if (error) return <ErrorDisplay error={error as any} />;
  if (!student) return <div>Student not found</div>;

  return (
    <div className="max-w-4xl mx-auto">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Edit Student</h1>
        <p className="mt-2 text-sm text-gray-600">Update student information - ID: {student.studentId}</p>
      </div>

      <StudentForm
        student={student}
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        isLoading={updateMutation.isPending}
      />
    </div>
  );
}
