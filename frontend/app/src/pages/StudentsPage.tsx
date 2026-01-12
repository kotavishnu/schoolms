import { useState, useCallback, useMemo } from 'react';
import { Plus } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Alert, AlertDescription } from '../components/ui/alert';
import { StudentDialog } from '../components/students/StudentDialog';
import { ViewStudentDialog } from '../components/students/ViewStudentDialog';
import { StudentCard } from '../components/students/StudentCard';
import { StudentFilters } from '../components/students/StudentFilters';
import { ConfirmDialog } from '../components/common/ConfirmDialog';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { useStudents } from '../hooks/useStudents';
import { useToast } from '../hooks/useToast';
import type { Student, StudentCreateDto, StudentUpdateDto } from '../types/student';

export function StudentsPage() {
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<'ALL' | 'ACTIVE' | 'INACTIVE'>('ALL');
  const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [viewDialogOpen, setViewDialogOpen] = useState(false);
  const [editingStudent, setEditingStudent] = useState<Student | null>(null);
  const [viewingStudent, setViewingStudent] = useState<Student | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [studentToDelete, setStudentToDelete] = useState<Student | null>(null);

  const toast = useToast();

  const filters = useMemo(() => ({
    search: searchQuery,
    status: statusFilter === 'ALL' ? undefined : (statusFilter as 'ACTIVE' | 'INACTIVE'),
  }), [searchQuery, statusFilter]);

  const { students, loading, error, createStudent, updateStudent, deleteStudent, refetch } = useStudents(filters);

  const filteredStudents = students.filter((student) => {
    const matchesStatus = statusFilter === 'ALL' || student.status === statusFilter;
    return matchesStatus;
  });

  const handleCreate = useCallback(async (data: StudentCreateDto | StudentUpdateDto) => {
    try {
      if (dialogMode === 'create') {
        await createStudent(data as StudentCreateDto);
        toast.success('Student registered successfully');
      } else if (editingStudent) {
        await updateStudent(editingStudent.id, data as StudentUpdateDto);
        toast.success('Student updated successfully');
      }
      setDialogOpen(false);
      setEditingStudent(null);
      await refetch();
    } catch (error: any) {
      toast.error(error.message || 'Operation failed');
      throw error;
    }
  }, [dialogMode, editingStudent, createStudent, updateStudent, toast, refetch]);

  const handleDelete = useCallback(async () => {
    if (!studentToDelete) return;

    try {
      await deleteStudent(studentToDelete.id);
      toast.success('Student deleted successfully');
      setDeleteDialogOpen(false);
      setStudentToDelete(null);
      await refetch();
    } catch (error: any) {
      toast.error(error.message || 'Failed to delete student');
    }
  }, [studentToDelete, deleteStudent, toast, refetch]);

  const openCreateDialog = () => {
    setDialogMode('create');
    setEditingStudent(null);
    setDialogOpen(true);
  };

  const openEditDialog = (student: Student) => {
    setDialogMode('edit');
    setEditingStudent(student);
    setDialogOpen(true);
  };

  const openViewDialog = (student: Student) => {
    setViewingStudent(student);
    setViewDialogOpen(true);
  };

  const openDeleteDialog = (student: Student) => {
    setStudentToDelete(student);
    setDeleteDialogOpen(true);
  };

  const handleStatusChange = (status: string) => {
    setStatusFilter(status as 'ALL' | 'ACTIVE' | 'INACTIVE');
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Students</h1>
          <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
            {loading ? 'Loading...' : `${filteredStudents.length} total students`}
          </p>
        </div>
        <Button onClick={openCreateDialog} className="bg-blue-600 hover:bg-blue-700">
          <Plus className="h-4 w-4 mr-2" />
          Register New Student
        </Button>
      </div>

      {/* Filters */}
      <StudentFilters
        onSearchChange={setSearchQuery}
        onStatusChange={handleStatusChange}
        currentSearch={searchQuery}
        currentStatus={statusFilter}
      />

      {/* Error Alert */}
      {error && (
        <Alert variant="destructive">
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {/* Loading State */}
      {loading && (
        <div className="flex justify-center py-12">
          <LoadingSpinner size="lg" text="Loading students..." />
        </div>
      )}

      {/* Empty State */}
      {!loading && filteredStudents.length === 0 && (
        <div className="text-center py-12">
          <p className="text-gray-500 dark:text-gray-400 mb-4">
            {searchQuery || statusFilter !== 'ALL'
              ? 'No students found matching your filters'
              : 'No students registered yet'}
          </p>
          {!searchQuery && statusFilter === 'ALL' && (
            <Button onClick={openCreateDialog} variant="outline">
              Register First Student
            </Button>
          )}
        </div>
      )}

      {/* Students Grid */}
      {!loading && filteredStudents.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredStudents.map((student) => (
            <StudentCard
              key={student.id}
              student={student}
              onView={openViewDialog}
              onEdit={openEditDialog}
              onDelete={openDeleteDialog}
            />
          ))}
        </div>
      )}

      {/* Dialogs */}
      <StudentDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        onSubmit={handleCreate}
        student={editingStudent}
        mode={dialogMode}
      />

      <ViewStudentDialog
        open={viewDialogOpen}
        onOpenChange={setViewDialogOpen}
        student={viewingStudent}
      />

      <ConfirmDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        onConfirm={handleDelete}
        title="Delete Student"
        description={`Are you sure you want to delete ${studentToDelete?.firstName} ${studentToDelete?.lastName}? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        variant="destructive"
      />
    </div>
  );
}
