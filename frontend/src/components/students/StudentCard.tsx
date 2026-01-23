import { User, Phone, Mail, Eye, Edit, Trash2 } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import type { Student } from '@/types/student';

interface StudentCardProps {
  student: Student;
  onView: (student: Student) => void;
  onEdit: (student: Student) => void;
  onDelete: (student: Student) => void;
}

export function StudentCard({ student, onView, onEdit, onDelete }: StudentCardProps) {
  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardContent className="p-6">
        <div className="space-y-4">
          {/* Header with ID and Status */}
          <div className="flex items-start justify-between">
            <div>
              <p className="text-sm text-gray-600">Student ID</p>
              <p className="font-semibold text-gray-900">{student.id}</p>
            </div>
            <Badge variant={student.status === 'ACTIVE' ? 'default' : 'secondary'}>
              {student.status}
            </Badge>
          </div>

          {/* Student Name */}
          <div className="flex items-center gap-2">
            <User className="h-5 w-5 text-gray-400" />
            <div>
              <p className="font-medium text-gray-900">
                {student.firstName} {student.lastName}
              </p>
              <p className="text-sm text-gray-600">Age: {student.age} years</p>
            </div>
          </div>

          {/* Guardian */}
          <div>
            <p className="text-sm text-gray-600">Guardian</p>
            <p className="text-sm font-medium text-gray-900">{student.guardianName}</p>
          </div>

          {/* Contact Info */}
          <div className="space-y-1">
            <div className="flex items-center gap-2 text-sm">
              <Phone className="h-4 w-4 text-gray-400" />
              <span className="text-gray-900">{student.phone}</span>
            </div>
            <div className="flex items-center gap-2 text-sm">
              <Mail className="h-4 w-4 text-gray-400" />
              <span className="text-gray-900">{student.email}</span>
            </div>
          </div>

          {/* Actions */}
          <div className="flex gap-2 pt-2 border-t">
            <Button
              size="sm"
              onClick={() => onView(student)}
              className="flex-1 bg-gray-600 hover:bg-gray-700 text-white"
            >
              <Eye className="h-4 w-4 mr-1" />
              View Details
            </Button>
            <Button
              size="sm"
              onClick={() => onEdit(student)}
              className="flex-1 bg-blue-600 hover:bg-blue-700 text-white"
            >
              <Edit className="h-4 w-4 mr-1" />
              Edit
            </Button>
            <Button
              size="sm"
              onClick={() => onDelete(student)}
              className="bg-red-600 hover:bg-red-700 text-white"
            >
              <Trash2 className="h-4 w-4" />
              Delete
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
