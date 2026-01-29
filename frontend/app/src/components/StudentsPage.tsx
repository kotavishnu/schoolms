import { useState, useEffect } from 'react';
import { Phone, Calendar, Mail, Trash2, Edit, Eye } from 'lucide-react';
import { toast } from 'sonner';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Skeleton } from './ui/skeleton';
import { StudentDialog } from './StudentDialog';
import { ViewStudentDialog } from './ViewStudentDialog';
import { studentApi, type StudentResponse } from '../services/api/studentApi';
import type { Student } from '../types';

// Map API response to Student type
const mapStudentResponse = (response: StudentResponse): Student => ({
  id: response.studentId,
  firstName: response.firstName,
  lastName: response.lastName,
  guardianName: response.fathersName || '',
  motherName: response.mothersName || '',
  phone: response.mobile,
  age: Math.floor((Date.now() - new Date(response.dateOfBirth).getTime()) / (365.25 * 24 * 60 * 60 * 1000)),
  email: response.email || '',
  address: response.address || '',
  identificationMarks: response.identificationMark || '',
  status: response.status,
});

export function StudentsPage() {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchLastName, setSearchLastName] = useState('');
  const [searchGuardian, setSearchGuardian] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [viewDialogOpen, setViewDialogOpen] = useState(false);
  const [editingStudent, setEditingStudent] = useState<Student | null>(null);
  const [viewingStudent, setViewingStudent] = useState<Student | null>(null);

  // Fetch students on mount
  useEffect(() => {
    fetchStudents();
  }, []);

  const fetchStudents = async () => {
    setLoading(true);
    try {
      const response = await studentApi.searchStudents({});
      const mappedStudents = response.content.map(mapStudentResponse);
      setStudents(mappedStudents);
    } catch (error) {
      toast.error('Failed to fetch students');
    } finally {
      setLoading(false);
    }
  };

  const filteredStudents = students.filter((student) => {
    const matchesLastName = student.lastName.toLowerCase().includes(searchLastName.toLowerCase());
    const matchesGuardian = student.guardianName.toLowerCase().includes(searchGuardian.toLowerCase());
    const matchesStatus = statusFilter === 'all' || student.status === statusFilter;
    return matchesLastName && matchesGuardian && matchesStatus;
  });

  const handleDelete = async (id: string) => {
    if (confirm('Are you sure you want to delete this student?')) {
      try {
        await studentApi.deleteStudent(id);
        toast.success('Student deleted successfully');
        await fetchStudents();
      } catch (error) {
        toast.error('Failed to delete student');
      }
    }
  };

  const openEditDialog = (student: Student) => {
    setEditingStudent(student);
    setDialogOpen(true);
  };

  const openViewDialog = (student: Student) => {
    setViewingStudent(student);
    setViewDialogOpen(true);
  };

  const closeDialog = () => {
    setDialogOpen(false);
    setEditingStudent(null);
  };

  const closeViewDialog = () => {
    setViewDialogOpen(false);
    setViewingStudent(null);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl mb-1">Students</h1>
          <p className="text-sm text-gray-600">{filteredStudents.length} total students</p>
        </div>
        <Button onClick={() => setDialogOpen(true)} className="bg-blue-600 hover:bg-blue-700">
          Register New Student
        </Button>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-lg p-6 mb-6 border border-gray-200">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="text-sm text-gray-600 mb-2 block">Last Name</label>
            <Input
              placeholder="Search by last name..."
              value={searchLastName}
              onChange={(e) => setSearchLastName(e.target.value)}
            />
          </div>
          <div>
            <label className="text-sm text-gray-600 mb-2 block">Guardian Name</label>
            <Input
              placeholder="Search by father/mother name..."
              value={searchGuardian}
              onChange={(e) => setSearchGuardian(e.target.value)}
            />
          </div>
          <div>
            <label className="text-sm text-gray-600 mb-2 block">Status</label>
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger>
                <SelectValue placeholder="All Statuses" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Statuses</SelectItem>
                <SelectItem value="ACTIVE">Active</SelectItem>
                <SelectItem value="INACTIVE">Inactive</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>

      {/* Loading Skeleton */}
      {loading && (
        <div className="space-y-4">
          {[...Array(6)].map((_, i) => (
            <div key={i} className="bg-white rounded-lg p-6 border border-gray-200">
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <Skeleton className="h-6 w-48 mb-2" />
                  <Skeleton className="h-4 w-32" />
                </div>
                <Skeleton className="h-6 w-16" />
              </div>
              <div className="space-y-2 mb-4">
                <Skeleton className="h-4 w-full" />
                <Skeleton className="h-4 w-3/4" />
                <Skeleton className="h-4 w-5/6" />
              </div>
              <div className="flex gap-2">
                <Skeleton className="h-9 flex-1" />
                <Skeleton className="h-9 w-20" />
                <Skeleton className="h-9 w-10" />
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Student Cards */}
      {!loading && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredStudents.map((student) => (
          <div key={student.id} className="bg-white rounded-lg p-6 border border-gray-200">
            <div className="flex items-start justify-between mb-4">
              <div>
                <h3 className="text-lg mb-1">
                  {student.firstName} {student.lastName}
                </h3>
                <p className="text-xs text-gray-500">ID: {student.id}</p>
              </div>
              <span
                className={`px-2 py-1 text-xs rounded ${
                  student.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                }`}
              >
                {student.status}
              </span>
            </div>

            <div className="space-y-2 mb-4">
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <Phone className="w-4 h-4" />
                <span>{student.phone}</span>
              </div>
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <Calendar className="w-4 h-4" />
                <span>Age: {student.age} years</span>
              </div>
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <Mail className="w-4 h-4" />
                <span>{student.email}</span>
              </div>
            </div>

            <div className="flex gap-2">
              <Button
                variant="outline"
                size="sm"
                className="flex-1"
                onClick={() => openViewDialog(student)}
              >
                <Eye className="w-4 h-4 mr-1" />
                View Details
              </Button>
              <Button
                variant="default"
                size="sm"
                className="bg-blue-600 hover:bg-blue-700"
                onClick={() => openEditDialog(student)}
              >
                <Edit className="w-4 h-4 mr-1" />
                Edit
              </Button>
              <Button
                variant="destructive"
                size="sm"
                onClick={() => handleDelete(student.id)}
              >
                <Trash2 className="w-4 h-4" />
              </Button>
            </div>
          </div>
          ))}
        </div>
      )}

      {!loading && filteredStudents.length === 0 && (
        <div className="text-center py-12 text-gray-500">No students found</div>
      )}

      <StudentDialog
        open={dialogOpen}
        onOpenChange={closeDialog}
        onSuccess={fetchStudents}
        student={editingStudent}
      />

      <ViewStudentDialog
        open={viewDialogOpen}
        onOpenChange={closeViewDialog}
        student={viewingStudent}
      />
    </div>
  );
}