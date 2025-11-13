import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Plus, Edit, Eye, Trash2 } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Input } from '@/shared/components/ui/Input';
import { Select } from '@/shared/components/ui/Select';
import { Table, type Column } from '@/shared/components/ui/Table';
import { Badge } from '@/shared/components/ui/Badge';
import { useStudents, useDeleteStudent } from '../hooks/useStudents';
import type { Student, StudentStatus } from '../types/student.types';
import { ROUTES } from '@/shared/constants/routes';

/**
 * Student List Page Component
 *
 * Features:
 * - Display students in a table with pagination
 * - Search by name, ID, or phone
 * - Filter by status and class
 * - Action buttons: View, Edit, Delete
 * - Add new student button
 */
export const StudentList = () => {
  const navigate = useNavigate();

  // Filter state
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState<StudentStatus | ''>('');
  const [classId, setClassId] = useState<number | ''>('');
  const [page, setPage] = useState(1);
  const limit = 10;

  // Build filters object
  const filters = {
    search: search || undefined,
    status: status || undefined,
    classId: classId || undefined,
    page,
    limit,
  };

  // Fetch students with filters
  const { data, isLoading, error } = useStudents(filters);

  // Delete mutation
  const { mutate: deleteStudent, isPending: isDeleting } = useDeleteStudent();

  /**
   * Get badge variant based on student status
   */
  const getStatusBadge = (status: StudentStatus) => {
    const variants: Record<StudentStatus, 'success' | 'error' | 'warning' | 'info'> = {
      Active: 'success',
      Inactive: 'warning',
      Graduated: 'info',
      Dropped: 'error',
    };

    return variants[status] || 'info';
  };

  /**
   * Handle delete student with confirmation
   */
  const handleDelete = (student: Student) => {
    if (window.confirm(`Are you sure you want to delete ${student.firstName} ${student.lastName}?`)) {
      deleteStudent(student.id);
    }
  };

  /**
   * Handle search input change with debouncing
   */
  const handleSearchChange = (value: string) => {
    setSearch(value);
    setPage(1); // Reset to first page on search
  };

  /**
   * Handle filter changes
   */
  const handleStatusChange = (value: string) => {
    setStatus(value as StudentStatus | '');
    setPage(1);
  };

  /**
   * Handle pagination
   */
  const handlePreviousPage = () => {
    setPage((prev) => Math.max(1, prev - 1));
  };

  const handleNextPage = () => {
    if (data && page < data.totalPages) {
      setPage((prev) => prev + 1);
    }
  };

  /**
   * Table column definitions
   */
  const columns: Column<Student>[] = [
    {
      key: 'studentId',
      header: 'Student ID',
      width: '120px',
    },
    {
      key: 'name',
      header: 'Name',
      render: (student) => `${student.firstName} ${student.lastName}`,
    },
    {
      key: 'class',
      header: 'Class',
      render: (student) =>
        student.academic.className
          ? `${student.academic.className} ${student.academic.section || ''}`
          : 'N/A',
    },
    {
      key: 'guardian',
      header: 'Guardian',
      render: (student) => student.guardian.name,
    },
    {
      key: 'phone',
      header: 'Phone',
      render: (student) => student.guardian.phone,
    },
    {
      key: 'status',
      header: 'Status',
      width: '120px',
      render: (student) => (
        <Badge variant={getStatusBadge(student.status)}>{student.status}</Badge>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      width: '150px',
      render: (student) => (
        <div className="flex items-center gap-2">
          <button
            onClick={(e) => {
              e.stopPropagation();
              navigate(ROUTES.STUDENT_DETAILS.replace(':studentId', student.id.toString()));
            }}
            className="p-1 text-blue-600 hover:bg-blue-50 rounded"
            aria-label="View student details"
            title="View"
          >
            <Eye className="w-4 h-4" />
          </button>
          <button
            onClick={(e) => {
              e.stopPropagation();
              navigate(ROUTES.STUDENT_EDIT.replace(':studentId', student.id.toString()));
            }}
            className="p-1 text-gray-600 hover:bg-gray-50 rounded"
            aria-label="Edit student"
            title="Edit"
          >
            <Edit className="w-4 h-4" />
          </button>
          <button
            onClick={(e) => {
              e.stopPropagation();
              handleDelete(student);
            }}
            className="p-1 text-red-600 hover:bg-red-50 rounded"
            aria-label="Delete student"
            title="Delete"
            disabled={isDeleting}
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Students</h1>
          <p className="text-gray-600 mt-1">Manage student registrations and information</p>
        </div>
        <Button
          onClick={() => navigate(ROUTES.STUDENT_NEW)}
          className="flex items-center gap-2"
        >
          <Plus className="w-4 h-4" />
          Add New Student
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Student List</CardTitle>
        </CardHeader>
        <CardContent>
          {/* Filters Section */}
          <div className="mb-6 grid grid-cols-1 md:grid-cols-3 gap-4">
            {/* Search Input */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <Input
                type="text"
                placeholder="Search by name, ID, or phone..."
                value={search}
                onChange={(e) => handleSearchChange(e.target.value)}
                className="pl-10"
                aria-label="Search students"
              />
            </div>

            {/* Status Filter */}
            <Select
              value={status}
              onChange={(e) => handleStatusChange(e.target.value)}
              aria-label="Filter by status"
            >
              <option value="">All Status</option>
              <option value="Active">Active</option>
              <option value="Inactive">Inactive</option>
              <option value="Graduated">Graduated</option>
              <option value="Dropped">Dropped</option>
            </Select>

            {/* Class Filter - Placeholder for now */}
            <Select
              value={classId}
              onChange={(e) => setClassId(e.target.value ? Number(e.target.value) : '')}
              aria-label="Filter by class"
            >
              <option value="">All Classes</option>
              {/* TODO: Populate with actual classes from API */}
            </Select>
          </div>

          {/* Error State */}
          {error && (
            <div className="text-center py-8 text-red-600" role="alert">
              <p>Failed to load students. Please try again.</p>
            </div>
          )}

          {/* Table */}
          <Table
            data={data?.students || []}
            columns={columns}
            isLoading={isLoading}
            emptyMessage="No students found. Add your first student to get started."
          />

          {/* Pagination */}
          {data && data.totalPages > 1 && (
            <div className="mt-6 flex items-center justify-between border-t pt-4">
              <p className="text-sm text-gray-600">
                Showing {(page - 1) * limit + 1} to{' '}
                {Math.min(page * limit, data.total)} of {data.total} students
              </p>
              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={handlePreviousPage}
                  disabled={page === 1}
                >
                  Previous
                </Button>
                <span className="text-sm text-gray-600">
                  Page {page} of {data.totalPages}
                </span>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={handleNextPage}
                  disabled={page === data.totalPages}
                >
                  Next
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};
