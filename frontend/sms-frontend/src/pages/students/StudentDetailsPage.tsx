import { useParams, useNavigate, Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { studentApi } from '@/api/studentApi';
import { useToast } from '@/components/ui/Toast';
import { Button } from '@/components/ui/Button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import Loading from '@/components/common/Loading';
import ErrorDisplay from '@/components/common/ErrorDisplay';
import Badge from '@/components/common/Badge';
import { formatDateTime } from '@/lib/utils';

export default function StudentDetailsPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  const { data: student, isLoading, error } = useQuery({
    queryKey: ['student', id],
    queryFn: () => studentApi.getStudentById(id!),
    enabled: !!id,
  });

  const deleteMutation = useMutation({
    mutationFn: studentApi.deleteStudent,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['students'] });
      showToast({
        type: 'success',
        title: 'Success',
        description: 'Student deleted successfully',
      });
      navigate('/students');
    },
    onError: (error: any) => {
      showToast({
        type: 'error',
        title: 'Error',
        description: error.problemDetail?.detail || 'Failed to delete student',
      });
    },
  });

  const statusMutation = useMutation({
    mutationFn: ({ studentId, status }: { studentId: string; status: 'ACTIVE' | 'INACTIVE' }) =>
      studentApi.updateStatus(studentId, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['student', id] });
      queryClient.invalidateQueries({ queryKey: ['students'] });
      showToast({
        type: 'success',
        title: 'Success',
        description: 'Student status updated',
      });
    },
    onError: (error: any) => {
      showToast({
        type: 'error',
        title: 'Error',
        description: error.problemDetail?.detail || 'Failed to update status',
      });
    },
  });

  const handleDelete = () => {
    if (window.confirm('Are you sure you want to delete this student?')) {
      deleteMutation.mutate(id!);
    }
  };

  const handleToggleStatus = () => {
    if (student) {
      const newStatus = student.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
      statusMutation.mutate({ studentId: id!, status: newStatus });
    }
  };

  if (isLoading) return <Loading />;
  if (error) return <ErrorDisplay error={error as any} />;
  if (!student) return <div>Student not found</div>;

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Student Details</h1>
          <p className="mt-2 text-sm text-gray-600">ID: {student.studentId}</p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" onClick={handleToggleStatus} isLoading={statusMutation.isPending}>
            {student.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
          </Button>
          <Link to={`/students/${id}/edit`}>
            <Button variant="outline">Edit</Button>
          </Link>
          <Button variant="destructive" onClick={handleDelete} isLoading={deleteMutation.isPending}>
            Delete
          </Button>
        </div>
      </div>

      <Card>
        <CardHeader>
          <div className="flex justify-between items-center">
            <CardTitle>Personal Information</CardTitle>
            <Badge variant={student.status === 'ACTIVE' ? 'success' : 'danger'}>
              {student.status}
            </Badge>
          </div>
        </CardHeader>
        <CardContent className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-sm font-medium text-gray-500">First Name</p>
            <p className="mt-1 text-sm text-gray-900">{student.firstName}</p>
          </div>
          <div>
            <p className="text-sm font-medium text-gray-500">Last Name</p>
            <p className="mt-1 text-sm text-gray-900">{student.lastName}</p>
          </div>
          <div>
            <p className="text-sm font-medium text-gray-500">Date of Birth</p>
            <p className="mt-1 text-sm text-gray-900">{student.dateOfBirth}</p>
          </div>
          <div>
            <p className="text-sm font-medium text-gray-500">Age</p>
            <p className="mt-1 text-sm text-gray-900">{student.age} years</p>
          </div>
          {student.caste && (
            <div>
              <p className="text-sm font-medium text-gray-500">Caste</p>
              <p className="mt-1 text-sm text-gray-900">{student.caste}</p>
            </div>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Address</CardTitle>
        </CardHeader>
        <CardContent className="space-y-2">
          <p className="text-sm text-gray-900">{student.address.street}</p>
          <p className="text-sm text-gray-900">
            {student.address.city}, {student.address.state} - {student.address.pinCode}
          </p>
          <p className="text-sm text-gray-900">{student.address.country}</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Contact Information</CardTitle>
        </CardHeader>
        <CardContent className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-sm font-medium text-gray-500">Mobile</p>
            <p className="mt-1 text-sm text-gray-900">{student.mobile}</p>
          </div>
          {student.email && (
            <div>
              <p className="text-sm font-medium text-gray-500">Email</p>
              <p className="mt-1 text-sm text-gray-900">{student.email}</p>
            </div>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Family Information</CardTitle>
        </CardHeader>
        <CardContent className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-sm font-medium text-gray-500">Father Name / Guardian</p>
            <p className="mt-1 text-sm text-gray-900">{student.fatherNameOrGuardian}</p>
          </div>
          {student.motherName && (
            <div>
              <p className="text-sm font-medium text-gray-500">Mother Name</p>
              <p className="mt-1 text-sm text-gray-900">{student.motherName}</p>
            </div>
          )}
        </CardContent>
      </Card>

      {(student.aadhaarNumber || student.moles) && (
        <Card>
          <CardHeader>
            <CardTitle>Additional Information</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-2 gap-4">
            {student.aadhaarNumber && (
              <div>
                <p className="text-sm font-medium text-gray-500">Aadhaar Number</p>
                <p className="mt-1 text-sm text-gray-900">{student.aadhaarNumber}</p>
              </div>
            )}
            {student.moles && (
              <div className="col-span-2">
                <p className="text-sm font-medium text-gray-500">Moles (Identifying Marks)</p>
                <p className="mt-1 text-sm text-gray-900">{student.moles}</p>
              </div>
            )}
          </CardContent>
        </Card>
      )}

      <Card>
        <CardHeader>
          <CardTitle>Audit Information</CardTitle>
        </CardHeader>
        <CardContent className="grid grid-cols-2 gap-4">
          <div>
            <p className="text-sm font-medium text-gray-500">Created At</p>
            <p className="mt-1 text-sm text-gray-900">{formatDateTime(student.createdAt)}</p>
          </div>
          <div>
            <p className="text-sm font-medium text-gray-500">Last Updated</p>
            <p className="mt-1 text-sm text-gray-900">{formatDateTime(student.updatedAt)}</p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
