import { useState } from 'react';
import { Phone, Calendar, Mail, Trash2, Edit, Eye } from 'lucide-react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { StudentDialog } from './StudentDialog';
import { ViewStudentDialog } from './ViewStudentDialog';
import type { Student } from '../types';

const initialStudents: Student[] = [
  {
    id: 'STU-2025-00001',
    firstName: 'John',
    lastName: 'Doe',
    guardianName: 'Jane Doe',
    motherName: 'Mary Doe',
    phone: '1234567890',
    age: 15,
    email: 'john.doe@example.com',
    address: '123 Main Street, City, State, ZIP',
    identificationMarks: 'Mole on left cheek',
    status: 'ACTIVE',
  },
];

export function StudentsPage() {
  const [students, setStudents] = useState<Student[]>(initialStudents);
  const [searchLastName, setSearchLastName] = useState('');
  const [searchGuardian, setSearchGuardian] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [viewDialogOpen, setViewDialogOpen] = useState(false);
  const [editingStudent, setEditingStudent] = useState<Student | null>(null);
  const [viewingStudent, setViewingStudent] = useState<Student | null>(null);

  const filteredStudents = students.filter((student) => {
    const matchesLastName = student.lastName.toLowerCase().includes(searchLastName.toLowerCase());
    const matchesGuardian = student.guardianName.toLowerCase().includes(searchGuardian.toLowerCase());
    const matchesStatus = statusFilter === 'all' || student.status === statusFilter;
    return matchesLastName && matchesGuardian && matchesStatus;
  });

  const handleCreate = (student: Omit<Student, 'id'>) => {
    const newStudent: Student = {
      ...student,
      id: `STU-2025-${String(students.length + 1).padStart(5, '0')}`,
    };
    setStudents([...students, newStudent]);
  };

  const handleUpdate = (id: string, updatedStudent: Omit<Student, 'id'>) => {
    setStudents(students.map((s) => (s.id === id ? { ...updatedStudent, id } : s)));
  };

  const handleDelete = (id: string) => {
    if (confirm('Are you sure you want to delete this student?')) {
      setStudents(students.filter((s) => s.id !== id));
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

      {/* Student Cards */}
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

      {filteredStudents.length === 0 && (
        <div className="text-center py-12 text-gray-500">No students found</div>
      )}

      <StudentDialog
        open={dialogOpen}
        onOpenChange={closeDialog}
        onSubmit={editingStudent ? (data) => handleUpdate(editingStudent.id, data) : handleCreate}
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