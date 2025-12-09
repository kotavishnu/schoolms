'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { Plus, Search, Eye, Edit, Trash2 } from 'lucide-react';
import { studentService } from '@/services/studentService';
import { LoadingSpinner, PageLoadingSpinner } from '@/components/LoadingSpinner';
import { ErrorMessage } from '@/components/ErrorMessage';
import { Pagination } from '@/components/Pagination';
import { ConfirmDialog } from '@/components/ConfirmDialog';
import { useDebounce } from '@/hooks/useDebounce';
import { formatDate, getStatusColor } from '@/utils/format';
import { getErrorMessage } from '@/lib/api-client';
import toast from 'react-hot-toast';
import { StudentSearchParams } from '@/types';

export default function StudentsPage() {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<'Active' | 'Inactive' | ''>('');
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(20);
  const [deleteStudentKey, setDeleteStudentKey] = useState<string | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const debouncedSearchTerm = useDebounce(searchTerm, 500);

  const searchParams: StudentSearchParams = {
    page: currentPage,
    size: pageSize,
    lastName: debouncedSearchTerm || undefined,
    status: statusFilter || undefined,
    sort: 'lastName,asc',
  };

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['students', searchParams],
    queryFn: () => studentService.searchStudents(searchParams),
  });

  const handleDelete = async () => {
    if (!deleteStudentKey) return;

    setIsDeleting(true);
    try {
      await studentService.deleteStudent(deleteStudentKey);
      toast.success('Student deleted successfully');
      setDeleteStudentKey(null);
      refetch();
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Students</h1>
          <p className="mt-1 text-sm text-gray-600">Manage student records and enrollments</p>
        </div>
        <Link href="/students/new" className="btn-primary flex items-center gap-2">
          <Plus className="h-4 w-4" />
          Add Student
        </Link>
      </div>

      <div className="card">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Search by last name..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setCurrentPage(0);
              }}
              className="input-field pl-10"
            />
          </div>
          <select
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value as any);
              setCurrentPage(0);
            }}
            className="input-field w-full sm:w-48"
          >
            <option value="">All Status</option>
            <option value="Active">Active</option>
            <option value="Inactive">Inactive</option>
          </select>
        </div>
      </div>

      {isLoading ? (
        <PageLoadingSpinner />
      ) : error ? (
        <ErrorMessage
          message={getErrorMessage(error)}
          onRetry={() => refetch()}
        />
      ) : !data || data.content.length === 0 ? (
        <div className="card text-center py-12">
          <p className="text-gray-500">No students found</p>
          <Link href="/students/new" className="btn-primary mt-4 inline-flex items-center gap-2">
            <Plus className="h-4 w-4" />
            Add First Student
          </Link>
        </div>
      ) : (
        <>
          <div className="card overflow-hidden p-0">
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                      Student ID
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                      Name
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                      Mobile
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                      Guardian
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                      Status
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider text-gray-500">
                      Created
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-medium uppercase tracking-wider text-gray-500">
                      Actions
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200 bg-white">
                  {data.content.map((student) => (
                    <tr key={student.studentKey} className="hover:bg-gray-50">
                      <td className="whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-900">
                        {student.studentKey}
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-900">
                        {student.firstName} {student.lastName}
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                        {student.mobile}
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                        {student.fatherNameOrGuardian || '-'}
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-sm">
                        <span
                          className={`inline-flex rounded-full px-2 py-1 text-xs font-semibold ${getStatusColor(
                            student.status
                          )}`}
                        >
                          {student.status}
                        </span>
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-sm text-gray-600">
                        {formatDate(student.createdAt)}
                      </td>
                      <td className="whitespace-nowrap px-6 py-4 text-right text-sm font-medium">
                        <div className="flex items-center justify-end gap-2">
                          <Link
                            href={`/students/${student.studentKey}`}
                            className="text-primary-600 hover:text-primary-900"
                            title="View details"
                          >
                            <Eye className="h-4 w-4" />
                          </Link>
                          <Link
                            href={`/students/${student.studentKey}/edit`}
                            className="text-blue-600 hover:text-blue-900"
                            title="Edit"
                          >
                            <Edit className="h-4 w-4" />
                          </Link>
                          <button
                            onClick={() => setDeleteStudentKey(student.studentKey)}
                            className="text-red-600 hover:text-red-900"
                            title="Delete"
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <Pagination
            currentPage={data.number}
            totalPages={data.totalPages}
            onPageChange={setCurrentPage}
            pageSize={data.size}
            totalElements={data.totalElements}
          />
        </>
      )}

      <ConfirmDialog
        isOpen={deleteStudentKey !== null}
        onClose={() => setDeleteStudentKey(null)}
        onConfirm={handleDelete}
        title="Delete Student"
        message="Are you sure you want to delete this student? This action cannot be undone."
        confirmText="Delete"
        variant="danger"
        isLoading={isDeleting}
      />
    </div>
  );
}
