import { useState } from 'react';
import { UserPlus } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { StudentCard } from '@/components/students/StudentCard';
import { StudentDialog } from '@/components/students/StudentDialog';
import { useStudents } from '@/hooks/useStudents';
import { useDebounce } from '@/hooks/useDebounce';
import type { Student, StudentCreateRequest, StudentUpdateRequest } from '@/types/student';
import { useEffect } from 'react';

export function StudentsPage() {
  const { students, loading, createStudent, updateStudent, deleteStudent, searchStudents } =
    useStudents();

  const [lastNameSearch, setLastNameSearch] = useState('');
  const [guardianNameSearch, setGuardianNameSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<'ALL' | 'ACTIVE' | 'INACTIVE'>('ALL');
  const [dialogState, setDialogState] = useState<{
    mode: 'create' | 'edit' | 'view' | null;
    student?: Student;
  }>({ mode: null });
  const [deleteConfirmation, setDeleteConfirmation] = useState<{
    isOpen: boolean;
    student?: Student;
  }>({ isOpen: false });

  const debouncedLastName = useDebounce(lastNameSearch, 300);
  const debouncedGuardianName = useDebounce(guardianNameSearch, 300);

  // Trigger search when debounced search terms or filter changes
  useEffect(() => {
    // Combine search terms for API call
    const searchTerms = [debouncedLastName.trim(), debouncedGuardianName.trim()]
      .filter(Boolean)
      .join(' ');
    const status = statusFilter === 'ALL' ? undefined : statusFilter;
    searchStudents(searchTerms || undefined, status);
  }, [debouncedLastName, debouncedGuardianName, statusFilter]);

  const handleCreateStudent = async (data: StudentCreateRequest) => {
    await createStudent(data);
  };

  const handleUpdateStudent = async (data: StudentUpdateRequest) => {
    if (dialogState.student) {
      await updateStudent(dialogState.student.id, data);
    }
  };

  const handleDeleteConfirm = async () => {
    if (deleteConfirmation.student) {
      await deleteStudent(deleteConfirmation.student.id);
      setDeleteConfirmation({ isOpen: false });
    }
  };

  const handleDialogSubmit = async (data: StudentCreateRequest | StudentUpdateRequest) => {
    if (dialogState.mode === 'create') {
      await handleCreateStudent(data as StudentCreateRequest);
    } else if (dialogState.mode === 'edit') {
      await handleUpdateStudent(data as StudentUpdateRequest);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Students</h1>
          <p className="text-gray-600 mt-1">{students.length} total students</p>
        </div>
        <Button onClick={() => setDialogState({ mode: 'create' })}>
          <UserPlus className="h-4 w-4 mr-2" />
          Register New Student
        </Button>
      </div>

      {/* Filters */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <Label className="text-sm font-medium mb-2 block">Last Name</Label>
          <Input
            placeholder="Search by last name..."
            value={lastNameSearch}
            onChange={(e) => setLastNameSearch(e.target.value)}
          />
        </div>
        <div>
          <Label className="text-sm font-medium mb-2 block">Guardian Name</Label>
          <Input
            placeholder="Search by father/mother name..."
            value={guardianNameSearch}
            onChange={(e) => setGuardianNameSearch(e.target.value)}
          />
        </div>
        <div>
          <Label className="text-sm font-medium mb-2 block">Status</Label>
          <Select value={statusFilter} onValueChange={(value: any) => setStatusFilter(value)}>
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">All Statuses</SelectItem>
              <SelectItem value="ACTIVE">Active</SelectItem>
              <SelectItem value="INACTIVE">Inactive</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      {/* Student Cards Grid */}
      {loading ? (
        <div className="text-center py-12">
          <p className="text-gray-600">Loading students...</p>
        </div>
      ) : students.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-600">
            {lastNameSearch || guardianNameSearch || statusFilter !== 'ALL'
              ? 'No students found matching your filters'
              : 'No students registered yet'}
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {students.map((student) => (
            <StudentCard
              key={student.id}
              student={student}
              onView={(s) => setDialogState({ mode: 'view', student: s })}
              onEdit={(s) => setDialogState({ mode: 'edit', student: s })}
              onDelete={(s) => setDeleteConfirmation({ isOpen: true, student: s })}
            />
          ))}
        </div>
      )}

      {/* Student Dialog */}
      <StudentDialog
        mode={dialogState.mode || 'create'}
        student={dialogState.student}
        open={dialogState.mode !== null}
        onClose={() => setDialogState({ mode: null })}
        onSubmit={handleDialogSubmit}
      />

      {/* Delete Confirmation Dialog */}
      <AlertDialog
        open={deleteConfirmation.isOpen}
        onOpenChange={(open) => !open && setDeleteConfirmation({ isOpen: false })}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Student</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete {deleteConfirmation.student?.firstName}{' '}
              {deleteConfirmation.student?.lastName}? This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={handleDeleteConfirm} className="bg-red-600 hover:bg-red-700">
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
