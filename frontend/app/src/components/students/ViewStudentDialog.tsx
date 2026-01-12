import { Dialog, DialogContent, DialogHeader, DialogTitle } from '../ui/dialog';
import { Badge } from '../ui/badge';
import { Button } from '../ui/button';
import { Separator } from '../ui/separator';
import type { Student } from '../../types/student';
import { formatDate } from '../../utils/formatting';
import { getStatusBadgeStyle } from '../../utils/formatting';
import { calculateAge } from '../../utils/validation';

interface ViewStudentDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  student: Student | null;
}

export function ViewStudentDialog({ open, onOpenChange, student }: ViewStudentDialogProps) {
  if (!student) return null;

  const badgeStyle = getStatusBadgeStyle(student.status);
  const age = calculateAge(new Date(student.dateOfBirth));

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <div className="flex items-center justify-between">
            <DialogTitle>Student Details</DialogTitle>
            <Badge variant={badgeStyle.variant} className={badgeStyle.className}>{badgeStyle.text}</Badge>
          </div>
        </DialogHeader>

        <div className="space-y-6">
          {/* Student ID */}
          <div>
            <p className="text-sm font-medium text-gray-500">Student ID</p>
            <p className="text-lg font-semibold text-gray-900 dark:text-white">{student.id}</p>
          </div>

          <Separator />

          {/* Personal Information */}
          <div>
            <h3 className="text-base font-semibold mb-4">Personal Information</h3>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm font-medium text-gray-500">First Name</p>
                <p className="text-base text-gray-900 dark:text-white">{student.firstName}</p>
              </div>
              <div>
                <p className="text-sm font-medium text-gray-500">Last Name</p>
                <p className="text-base text-gray-900 dark:text-white">{student.lastName}</p>
              </div>
              <div>
                <p className="text-sm font-medium text-gray-500">Date of Birth</p>
                <p className="text-base text-gray-900 dark:text-white">{formatDate(student.dateOfBirth)}</p>
              </div>
              <div>
                <p className="text-sm font-medium text-gray-500">Age</p>
                <p className="text-base text-gray-900 dark:text-white">{age} years</p>
              </div>
              <div>
                <p className="text-sm font-medium text-gray-500">Adhaar Number</p>
                <p className="text-base text-gray-900 dark:text-white">{student.adhaarNumber}</p>
              </div>
              {student.identificationMarks && (
                <div className="col-span-2">
                  <p className="text-sm font-medium text-gray-500">Identification Marks</p>
                  <p className="text-base text-gray-900 dark:text-white">{student.identificationMarks}</p>
                </div>
              )}
              <div className="col-span-2">
                <p className="text-sm font-medium text-gray-500">Address</p>
                <p className="text-base text-gray-900 dark:text-white">{student.address}</p>
              </div>
            </div>
          </div>

          <Separator />

          {/* Guardian Information */}
          <div>
            <h3 className="text-base font-semibold mb-4">Guardian Information</h3>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm font-medium text-gray-500">Father/Guardian Name</p>
                <p className="text-base text-gray-900 dark:text-white">{student.guardianName}</p>
              </div>
              <div>
                <p className="text-sm font-medium text-gray-500">Mother Name</p>
                <p className="text-base text-gray-900 dark:text-white">{student.motherName}</p>
              </div>
            </div>
          </div>

          <Separator />

          {/* Contact Information */}
          <div>
            <h3 className="text-base font-semibold mb-4">Contact Information</h3>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm font-medium text-gray-500">Phone</p>
                <p className="text-base text-gray-900 dark:text-white">{student.phone}</p>
              </div>
              <div>
                <p className="text-sm font-medium text-gray-500">Email</p>
                <p className="text-base text-gray-900 dark:text-white">{student.email}</p>
              </div>
            </div>
          </div>

          <Separator />

          {/* Metadata */}
          <div>
            <h3 className="text-base font-semibold mb-4">Registration Information</h3>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm font-medium text-gray-500">Created At</p>
                <p className="text-base text-gray-900 dark:text-white">
                  {formatDate(student.createdAt, 'PPpp')}
                </p>
              </div>
              <div>
                <p className="text-sm font-medium text-gray-500">Last Updated</p>
                <p className="text-base text-gray-900 dark:text-white">
                  {formatDate(student.updatedAt, 'PPpp')}
                </p>
              </div>
            </div>
          </div>
        </div>

        <div className="flex justify-end pt-4 border-t">
          <Button onClick={() => onOpenChange(false)} variant="outline">
            Close
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
