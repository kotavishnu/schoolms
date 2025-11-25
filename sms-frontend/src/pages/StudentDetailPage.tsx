import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useStudent } from '@/features/students/hooks/useStudent';
import { useDeleteStudent } from '@/features/students/hooks/useDeleteStudent';
import StudentDetail from '@/features/students/components/StudentDetail';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import ConfirmDialog from '@/components/common/ConfirmDialog';
import Button from '@/components/common/Button';

const StudentDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const studentId = parseInt(id || '0', 10);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);

  const { data: student, isLoading, isError, error } = useStudent(studentId);
  const deleteStudent = useDeleteStudent();

  const handleDelete = () => {
    deleteStudent.mutate(studentId);
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
          <svg
            className="w-12 h-12 mx-auto mb-4"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <p className="text-lg font-semibold">Student not found</p>
          <p className="text-sm mt-2">
            {(error as any)?.message || 'The requested student could not be found'}
          </p>
        </div>
        <Button variant="primary" onClick={() => window.history.back()}>
          Go Back
        </Button>
      </div>
    );
  }

  return (
    <>
      <StudentDetail student={student} onDelete={() => setShowDeleteDialog(true)} />

      <ConfirmDialog
        isOpen={showDeleteDialog}
        title="Delete Student"
        message={`Are you sure you want to delete ${student.firstName} ${student.lastName}? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        type="danger"
        onConfirm={handleDelete}
        onCancel={() => setShowDeleteDialog(false)}
        loading={deleteStudent.isPending}
      />
    </>
  );
};

export default StudentDetailPage;
