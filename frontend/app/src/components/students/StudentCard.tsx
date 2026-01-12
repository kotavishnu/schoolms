import { Card, CardContent, CardFooter, CardHeader } from '../ui/card';
import { Badge } from '../ui/badge';
import { Button } from '../ui/button';
import { Eye, Pencil, Trash2, Phone, Mail, Calendar } from 'lucide-react';
import type { Student } from '../../types/student';
import { getStatusBadgeStyle } from '../../utils/formatting';
import { calculateAge } from '../../utils/validation';

interface StudentCardProps {
  student: Student;
  onView: (student: Student) => void;
  onEdit: (student: Student) => void;
  onDelete: (student: Student) => void;
}

export function StudentCard({ student, onView, onEdit, onDelete }: StudentCardProps) {
  const badgeStyle = getStatusBadgeStyle(student.status);
  const age = calculateAge(new Date(student.dateOfBirth));

  return (
    <Card className="bg-white hover:shadow-lg transition-shadow">
      <CardHeader className="pb-4">
        <div className="flex items-start justify-between gap-3">
          <div className="flex-1 min-w-0">
            <h3 className="text-xl font-semibold text-gray-900 mb-1">
              {student.firstName} {student.lastName}
            </h3>
            <p className="text-sm text-gray-500">{student.id}</p>
          </div>
          <Badge variant={badgeStyle.variant} className={badgeStyle.className}>{badgeStyle.text}</Badge>
        </div>
      </CardHeader>

      <CardContent className="space-y-3 pt-0">
        <div className="flex items-center gap-2 text-sm text-gray-700">
          <Phone className="h-4 w-4 text-gray-500" />
          <span>{student.phone}</span>
        </div>

        <div className="flex items-center gap-2 text-sm text-gray-700">
          <Calendar className="h-4 w-4 text-gray-500" />
          <span>Age: {age} years</span>
        </div>

        <div className="flex items-center gap-2 text-sm text-gray-700">
          <Mail className="h-4 w-4 text-gray-500" />
          <span className="truncate">{student.email}</span>
        </div>
      </CardContent>

      <CardFooter className="flex gap-2 border-t pt-4">
        <Button
          variant="outline"
          size="sm"
          onClick={() => onView(student)}
          className="flex-1 text-gray-700 hover:bg-gray-50"
        >
          <Eye className="h-4 w-4 mr-1" />
          View Details
        </Button>
        <Button
          size="sm"
          onClick={() => onEdit(student)}
          className="bg-blue-600 hover:bg-blue-700 text-white"
        >
          <Pencil className="h-4 w-4 mr-1" />
          Edit
        </Button>
        <Button
          size="sm"
          onClick={() => onDelete(student)}
          className="bg-red-600 hover:bg-red-700 text-white"
        >
          <Trash2 className="h-4 w-4" />
        </Button>
      </CardFooter>
    </Card>
  );
}
