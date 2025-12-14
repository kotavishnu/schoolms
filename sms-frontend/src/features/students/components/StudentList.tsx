import React, { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useStudents } from '../hooks/useStudents';
import { type StudentSearchParams } from '@/types/student.types';
import StudentCard from './StudentCard';
import StudentSearchBar from './StudentSearchBar';
import Pagination from '@/components/common/Pagination';
import LoadingSpinner from '@/components/common/LoadingSpinner';
import Button from '@/components/common/Button';
import ConfirmDialog from '@/components/common/ConfirmDialog';
import { useDeleteStudent } from '../hooks/useDeleteStudent';
import { PAGINATION } from '@/utils/constants';

const StudentList: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useState<StudentSearchParams>({
    page: PAGINATION.DEFAULT_PAGE,
    size: PAGINATION.DEFAULT_SIZE,
  });
  const [deleteId, setDeleteId] = useState<number | null>(null);

  const { data, isLoading, isError, error } = useStudents(searchParams);
  const deleteStudent = useDeleteStudent();

  const handleSearch = useCallback((params: StudentSearchParams) => {
    setSearchParams((prev) => ({
      ...params,
      page: PAGINATION.DEFAULT_PAGE, // Reset to first page on new search
      size: prev.size,
    }));
  }, []);

  const handlePageChange = (page: number) => {
    setSearchParams({
      ...searchParams,
      page: page - 1, // Backend uses 0-based indexing
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleEdit = (id: number) => {
    navigate(`/students/${id}/edit`);
  };

  const handleDeleteClick = (id: number) => {
    setDeleteId(id);
  };

  const handleDeleteConfirm = () => {
    if (deleteId) {
      deleteStudent.mutate(deleteId);
      setDeleteId(null);
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-96">
        <LoadingSpinner message="Loading students..." />
      </div>
    );
  }

  if (isError) {
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
          <p className="text-lg font-semibold">Failed to load students</p>
          <p className="text-sm mt-2">{(error as any)?.message || 'An error occurred'}</p>
        </div>
        <Button variant="primary" onClick={() => window.location.reload()}>
          Retry
        </Button>
      </div>
    );
  }

  const students = data?.content || [];
  const totalPages = data?.totalPages || 0;
  const currentPage = (data?.pageNumber || 0) + 1; // Convert to 1-based for display

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Students</h1>
          <p className="text-gray-600 dark:text-gray-400 mt-1">
            {data?.totalElements || 0} total students
          </p>
        </div>
        <Button variant="primary" onClick={() => navigate('/students/create')}>
          Register New Student
        </Button>
      </div>

      {/* Search Bar */}
      <StudentSearchBar onSearch={handleSearch} />

      {/* Student Grid */}
      {students.length === 0 ? (
        <div className="text-center py-12 bg-white dark:bg-gray-800 rounded-lg shadow-md">
          <svg
            className="w-16 h-16 mx-auto text-gray-400 mb-4"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"
            />
          </svg>
          <p className="text-lg font-medium text-gray-900 dark:text-white">No students found</p>
          <p className="text-gray-600 dark:text-gray-400 mt-1">
            Try adjusting your search criteria or register a new student
          </p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {students.map((student: any) => (
              <StudentCard
                key={student.id}
                student={student}
                onEdit={handleEdit}
                onDelete={handleDeleteClick}
              />
            ))}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex justify-center mt-8">
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            </div>
          )}
        </>
      )}

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={deleteId !== null}
        title="Delete Student"
        message="Are you sure you want to delete this student? This action cannot be undone."
        confirmText="Delete"
        cancelText="Cancel"
        type="danger"
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteId(null)}
        loading={deleteStudent.isPending}
      />
    </div>
  );
};

export default StudentList;
