import { Dialog, DialogContent, DialogHeader, DialogTitle } from './ui/dialog';
import { Button } from './ui/button';
import type { Student } from '../types';

interface ViewStudentDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  student: Student | null;
}

export function ViewStudentDialog({ open, onOpenChange, student }: ViewStudentDialogProps) {
  if (!student) return null;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Student Details</DialogTitle>
        </DialogHeader>

        <div className="space-y-6">
          {/* Basic Information */}
          <div>
            <h3 className="text-base mb-4">Basic Information</h3>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm text-gray-600">Student ID</label>
                  <p className="mt-1">{student.id}</p>
                </div>
                <div>
                  <label className="text-sm text-gray-600">Status</label>
                  <p className="mt-1">
                    <span
                      className={`px-2 py-1 text-xs rounded ${
                        student.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                      }`}
                    >
                      {student.status}
                    </span>
                  </p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm text-gray-600">First Name</label>
                  <p className="mt-1">{student.firstName}</p>
                </div>
                <div>
                  <label className="text-sm text-gray-600">Last Name</label>
                  <p className="mt-1">{student.lastName}</p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm text-gray-600">Age</label>
                  <p className="mt-1">{student.age} years</p>
                </div>
                <div>
                  <label className="text-sm text-gray-600">Identification Marks</label>
                  <p className="mt-1">{student.identificationMarks || 'N/A'}</p>
                </div>
              </div>

              <div>
                <label className="text-sm text-gray-600">Address</label>
                <p className="mt-1 whitespace-pre-wrap">{student.address}</p>
              </div>
            </div>
          </div>

          {/* Guardian Information */}
          <div className="border-t pt-6">
            <h3 className="text-base mb-4">Guardian Information</h3>
            <div className="space-y-4">
              <div>
                <label className="text-sm text-gray-600">Name of Father/Guardian</label>
                <p className="mt-1">{student.guardianName}</p>
              </div>

              <div>
                <label className="text-sm text-gray-600">Mother Name</label>
                <p className="mt-1">{student.motherName}</p>
              </div>
            </div>
          </div>

          {/* Contact Information */}
          <div className="border-t pt-6">
            <h3 className="text-base mb-4">Contact Information</h3>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-sm text-gray-600">Phone Number</label>
                  <p className="mt-1">{student.phone}</p>
                </div>
                <div>
                  <label className="text-sm text-gray-600">Email</label>
                  <p className="mt-1">{student.email}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="flex justify-end pt-6 border-t">
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            Close
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}