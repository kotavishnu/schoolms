import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Pencil, Trash2, Users, Plus, Search, Filter } from 'lucide-react';
import { useClasses, useAcademicYears, useDeleteClass } from '../hooks/useClasses';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';
import { Loading } from '@/shared/components/feedback/Loading';
import { ErrorMessage } from '@/shared/components/feedback/ErrorMessage';
import { ROUTES } from '@/shared/constants/routes';
import type { ClassFilters, ClassName } from '../types/class.types';
import { CLASS_NAME_DISPLAY, getFormattedClassName } from '../types/class.types';

/**
 * ClassList Page Component
 *
 * Displays a paginated, filterable list of classes with the following features:
 * - Filter by academic year
 * - Filter by grade level
 * - Search by class name/section
 * - Pagination
 * - View, Edit, Delete actions
 * - Create new class button
 */
export function ClassList() {
  const navigate = useNavigate();

  // State for filters and pagination
  const [filters, setFilters] = useState<ClassFilters>({
    search: '',
    academicYearId: undefined,
    className: undefined,
    section: undefined,
    isActive: true,
    page: 0,
    limit: 20,
  });

  // Fetch data using React Query hooks
  const { data: classData, isLoading, isError, error, refetch } = useClasses(filters);
  const { data: academicYears, isLoading: isLoadingYears } = useAcademicYears();
  const deleteMutation = useDeleteClass();

  // Extract classes and pagination info
  const classes = classData?.content || [];
  const pageInfo = classData?.page;

  /**
   * Handle search input change with debouncing
   */
  const handleSearchChange = (value: string) => {
    setFilters((prev) => ({ ...prev, search: value, page: 0 }));
  };

  /**
   * Handle academic year filter change
   */
  const handleAcademicYearChange = (yearId: string) => {
    setFilters((prev) => ({
      ...prev,
      academicYearId: yearId ? parseInt(yearId, 10) : undefined,
      page: 0,
    }));
  };

  /**
   * Handle grade level filter change
   */
  const handleGradeChange = (grade: string) => {
    setFilters((prev) => ({
      ...prev,
      className: (grade as ClassName) || undefined,
      page: 0,
    }));
  };

  /**
   * Handle active status filter change
   */
  const handleActiveFilterChange = (active: string) => {
    setFilters((prev) => ({
      ...prev,
      isActive: active === 'all' ? undefined : active === 'active',
      page: 0,
    }));
  };

  /**
   * Handle pagination change
   */
  const handlePageChange = (newPage: number) => {
    setFilters((prev) => ({ ...prev, page: newPage }));
  };

  /**
   * Navigate to class details page
   */
  const handleViewClass = (classId: number) => {
    navigate(ROUTES.CLASS_DETAILS.replace(':classId', classId.toString()));
  };

  /**
   * Navigate to class edit page
   */
  const handleEditClass = (classId: number, event: React.MouseEvent) => {
    event.stopPropagation();
    navigate(`/classes/${classId}/edit`);
  };

  /**
   * Handle class deletion with confirmation
   */
  const handleDeleteClass = async (classId: number, className: string, event: React.MouseEvent) => {
    event.stopPropagation();

    const confirmed = window.confirm(
      `Are you sure you want to delete ${className}? This action cannot be undone and will fail if students are enrolled.`
    );

    if (confirmed) {
      await deleteMutation.mutateAsync(classId);
      refetch();
    }
  };

  /**
   * Navigate to create new class page
   */
  const handleCreateClass = () => {
    navigate(ROUTES.CLASS_NEW);
  };

  /**
   * Get capacity utilization color
   */
  const getCapacityColor = (utilization: number): string => {
    if (utilization >= 90) return 'text-red-600';
    if (utilization >= 75) return 'text-yellow-600';
    return 'text-green-600';
  };

  /**
   * Calculate capacity utilization percentage
   */
  const getUtilization = (current: number, max: number): number => {
    return max > 0 ? Math.round((current / max) * 100) : 0;
  };

  if (isLoading || isLoadingYears) {
    return <Loading message="Loading classes..." />;
  }

  if (isError) {
    return (
      <ErrorMessage
        message={error instanceof Error ? error.message : 'Failed to load classes'}
        onRetry={refetch}
      />
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Classes</h1>
          <p className="mt-1 text-sm text-gray-500">
            Manage class sections and student enrollments
          </p>
        </div>
        <Button
          onClick={handleCreateClass}
          className="flex items-center gap-2"
          aria-label="Create new class"
        >
          <Plus className="h-5 w-5" aria-hidden="true" />
          Create New Class
        </Button>
      </div>

      {/* Filters */}
      <Card className="p-6">
        <div className="flex items-center gap-2 mb-4">
          <Filter className="h-5 w-5 text-gray-500" aria-hidden="true" />
          <h2 className="text-lg font-semibold text-gray-900">Filters</h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Search */}
          <div>
            <label htmlFor="search" className="sr-only">
              Search classes
            </label>
            <div className="relative">
              <Search
                className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400"
                aria-hidden="true"
              />
              <input
                id="search"
                type="text"
                placeholder="Search by class or section..."
                value={filters.search || ''}
                onChange={(e) => handleSearchChange(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                aria-label="Search classes by name or section"
              />
            </div>
          </div>

          {/* Academic Year Filter */}
          <div>
            <label htmlFor="academic-year" className="sr-only">
              Filter by academic year
            </label>
            <select
              id="academic-year"
              value={filters.academicYearId || ''}
              onChange={(e) => handleAcademicYearChange(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              aria-label="Filter by academic year"
            >
              <option value="">All Academic Years</option>
              {academicYears?.map((year) => (
                <option key={year.academicYearId} value={year.academicYearId}>
                  {year.yearCode} {year.isCurrent && '(Current)'}
                </option>
              ))}
            </select>
          </div>

          {/* Grade Level Filter */}
          <div>
            <label htmlFor="grade" className="sr-only">
              Filter by grade level
            </label>
            <select
              id="grade"
              value={filters.className || ''}
              onChange={(e) => handleGradeChange(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              aria-label="Filter by grade level"
            >
              <option value="">All Grades</option>
              {Object.entries(CLASS_NAME_DISPLAY).map(([key, value]) => (
                <option key={key} value={key}>
                  {value}
                </option>
              ))}
            </select>
          </div>

          {/* Active Status Filter */}
          <div>
            <label htmlFor="active-status" className="sr-only">
              Filter by active status
            </label>
            <select
              id="active-status"
              value={filters.isActive === undefined ? 'all' : filters.isActive ? 'active' : 'inactive'}
              onChange={(e) => handleActiveFilterChange(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
              aria-label="Filter by active status"
            >
              <option value="all">All Status</option>
              <option value="active">Active Only</option>
              <option value="inactive">Inactive Only</option>
            </select>
          </div>
        </div>
      </Card>

      {/* Class Grid */}
      {classes.length === 0 ? (
        <Card className="p-12">
          <div className="text-center">
            <Users className="mx-auto h-12 w-12 text-gray-400" aria-hidden="true" />
            <h3 className="mt-2 text-sm font-semibold text-gray-900">No classes found</h3>
            <p className="mt-1 text-sm text-gray-500">
              {filters.search || filters.academicYearId || filters.className
                ? 'Try adjusting your filters'
                : 'Get started by creating a new class'}
            </p>
            {!filters.search && !filters.academicYearId && (
              <Button onClick={handleCreateClass} className="mt-4">
                Create New Class
              </Button>
            )}
          </div>
        </Card>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {classes.map((classItem) => {
              const utilization = getUtilization(
                classItem.currentEnrollment,
                classItem.maxCapacity
              );
              const formattedName = getFormattedClassName(classItem.className, classItem.section);

              return (
                <Card
                  key={classItem.classId}
                  className="p-6 hover:shadow-lg transition-shadow cursor-pointer"
                  onClick={() => handleViewClass(classItem.classId)}
                  role="article"
                  aria-label={`Class ${formattedName}`}
                >
                  {/* Header */}
                  <div className="flex items-start justify-between mb-4">
                    <div>
                      <h3 className="text-xl font-bold text-gray-900">{formattedName}</h3>
                      <p className="text-sm text-gray-500 mt-1">
                        {classItem.academicYear.yearCode}
                      </p>
                    </div>
                    <Badge variant={classItem.isActive ? 'success' : 'default'}>
                      {classItem.isActive ? 'Active' : 'Inactive'}
                    </Badge>
                  </div>

                  {/* Class Teacher */}
                  {classItem.classTeacher && (
                    <div className="mb-4">
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Class Teacher:</span>{' '}
                        {classItem.classTeacher.firstName} {classItem.classTeacher.lastName}
                      </p>
                    </div>
                  )}

                  {/* Room Number */}
                  {classItem.roomNumber && (
                    <div className="mb-4">
                      <p className="text-sm text-gray-600">
                        <span className="font-medium">Room:</span> {classItem.roomNumber}
                      </p>
                    </div>
                  )}

                  {/* Enrollment Stats */}
                  <div className="mb-4 p-3 bg-gray-50 rounded-lg">
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-sm font-medium text-gray-700">Enrollment</span>
                      <span className={`text-sm font-bold ${getCapacityColor(utilization)}`}>
                        {utilization}%
                      </span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-gray-600">
                      <Users className="h-4 w-4" aria-hidden="true" />
                      <span>
                        {classItem.currentEnrollment} / {classItem.maxCapacity} students
                      </span>
                    </div>
                    <div className="mt-2 w-full bg-gray-200 rounded-full h-2">
                      <div
                        className={`h-2 rounded-full transition-all ${
                          utilization >= 90
                            ? 'bg-red-600'
                            : utilization >= 75
                            ? 'bg-yellow-500'
                            : 'bg-green-600'
                        }`}
                        style={{ width: `${Math.min(utilization, 100)}%` }}
                        role="progressbar"
                        aria-valuenow={utilization}
                        aria-valuemin={0}
                        aria-valuemax={100}
                        aria-label={`Capacity utilization ${utilization}%`}
                      />
                    </div>
                  </div>

                  {/* Actions */}
                  <div className="flex gap-2 pt-4 border-t border-gray-200">
                    <Button
                      variant="secondary"
                      size="sm"
                      onClick={(e) => handleEditClass(classItem.classId, e)}
                      className="flex-1 flex items-center justify-center gap-2"
                      aria-label={`Edit ${formattedName}`}
                    >
                      <Pencil className="h-4 w-4" aria-hidden="true" />
                      Edit
                    </Button>
                    <Button
                      variant="danger"
                      size="sm"
                      onClick={(e) => handleDeleteClass(classItem.classId, formattedName, e)}
                      className="flex items-center justify-center gap-2"
                      aria-label={`Delete ${formattedName}`}
                    >
                      <Trash2 className="h-4 w-4" aria-hidden="true" />
                      Delete
                    </Button>
                  </div>
                </Card>
              );
            })}
          </div>

          {/* Pagination */}
          {pageInfo && pageInfo.totalPages > 1 && (
            <div className="flex items-center justify-between px-4 py-3 bg-white border border-gray-200 rounded-lg">
              <div className="flex-1 flex justify-between sm:hidden">
                <Button
                  variant="secondary"
                  onClick={() => handlePageChange(filters.page! - 1)}
                  disabled={filters.page === 0}
                >
                  Previous
                </Button>
                <Button
                  variant="secondary"
                  onClick={() => handlePageChange(filters.page! + 1)}
                  disabled={filters.page! >= pageInfo.totalPages - 1}
                >
                  Next
                </Button>
              </div>
              <div className="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
                <div>
                  <p className="text-sm text-gray-700">
                    Showing{' '}
                    <span className="font-medium">{filters.page! * filters.limit! + 1}</span> to{' '}
                    <span className="font-medium">
                      {Math.min((filters.page! + 1) * filters.limit!, pageInfo.totalElements)}
                    </span>{' '}
                    of <span className="font-medium">{pageInfo.totalElements}</span> classes
                  </p>
                </div>
                <div className="flex gap-2">
                  <Button
                    variant="secondary"
                    onClick={() => handlePageChange(filters.page! - 1)}
                    disabled={filters.page === 0}
                    aria-label="Previous page"
                  >
                    Previous
                  </Button>
                  <Button
                    variant="secondary"
                    onClick={() => handlePageChange(filters.page! + 1)}
                    disabled={filters.page! >= pageInfo.totalPages - 1}
                    aria-label="Next page"
                  >
                    Next
                  </Button>
                </div>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default ClassList;
