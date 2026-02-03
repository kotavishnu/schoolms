import { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { toast } from 'sonner';
import { format } from 'date-fns';
import { CalendarIcon } from 'lucide-react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from './ui/dialog';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Textarea } from './ui/textarea';
import { Calendar } from './ui/calendar';
import { Popover, PopoverContent, PopoverTrigger } from './ui/popover';
import { cn } from './ui/utils';
import { studentApi, type StudentResponse } from '../services/api/studentApi';
import {
  studentCreateSchema,
  studentUpdateSchema,
} from '../services/validation/studentSchema';
import type { Student } from '../types';

interface StudentDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess: () => void;
  student?: Student | null;
}

export function StudentDialog({ open, onOpenChange, onSuccess, student }: StudentDialogProps) {
  const [loading, setLoading] = useState(false);
  const [studentData, setStudentData] = useState<StudentResponse | null>(null);
  const isEditMode = !!student;

  const {
    register,
    handleSubmit,
    reset,
    control,
    formState: { errors },
  } = useForm<any>({
    resolver: isEditMode ? zodResolver(studentUpdateSchema) : zodResolver(studentCreateSchema),
    defaultValues: isEditMode ? {
      firstName: '',
      lastName: '',
      mobile: '',
      email: '',
      address: '',
      fathersName: '',
      mothersName: '',
      identificationMark: '',
      status: 'ACTIVE',
    } : {
      firstName: '',
      lastName: '',
      dateOfBirth: undefined,
      mobile: '',
      email: '',
      address: '',
      fathersName: '',
      mothersName: '',
      identificationMark: '',
      aadhaarNumber: '',
    },
  });

  // Fetch full student data when editing
  useEffect(() => {
    if (student && open) {
      fetchStudentData(student.id);
    }
  }, [student, open]);

  const fetchStudentData = async (studentId: string) => {
    try {
      const data = await studentApi.getStudent(studentId);
      setStudentData(data);

      if (isEditMode) {
        // Pre-populate form for editing (only editable fields)
        reset({
          firstName: data.firstName,
          lastName: data.lastName,
          mobile: data.mobile,
          email: data.email || '',
          address: data.address || '',
          fathersName: data.fathersName || '',
          mothersName: data.mothersName || '',
          identificationMark: data.identificationMark || '',
          status: data.status,
        });
      }
    } catch (error) {
      toast.error('Failed to load student details');
    }
  };

  const handleFormSubmit = async (data: any) => {
    setLoading(true);
    try {
      if (isEditMode && studentData) {
        // Update existing student (only editable fields: firstName, lastName, mobile, status)
        await studentApi.updateStudent(
          studentData.studentId,
          {
            firstName: data.firstName,
            lastName: data.lastName,
            mobile: data.mobile,
            status: data.status,
            version: studentData.version,
          }
        );
        toast.success('Student updated successfully');
      } else {
        // Create new student
        await studentApi.createStudent({
          firstName: data.firstName,
          lastName: data.lastName,
          dateOfBirth: format(data.dateOfBirth, 'yyyy-MM-dd'),
          mobile: data.mobile,
          email: data.email || undefined,
          address: data.address || undefined,
          fathersName: data.fathersName || undefined,
          mothersName: data.mothersName || undefined,
          identificationMark: data.identificationMark || undefined,
          aadhaarNumber: data.aadhaarNumber || undefined,
        });
        toast.success('Student registered successfully');
      }

      onSuccess();
      onOpenChange(false);
      reset();
      setStudentData(null);
    } catch (error: any) {
      if (error.response?.status === 409) {
        toast.error('Mobile number already registered');
      } else if (error.response?.status === 400) {
        const errors = error.response.data.errors || [];
        if (errors.length > 0) {
          errors.forEach((err: any) => toast.error(err.message || err));
        } else if (error.response.data.detail) {
          toast.error(error.response.data.detail);
        } else {
          toast.error('Invalid input. Please check your data.');
        }
      } else {
        toast.error(isEditMode ? 'Failed to update student' : 'Failed to register student');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>{isEditMode ? 'Edit Student' : 'Register New Student'}</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
          {/* Show Student ID in edit mode (read-only) */}
          {isEditMode && studentData && (
            <div>
              <Label htmlFor="studentId">Student ID</Label>
              <Input
                id="studentId"
                value={studentData.studentId}
                disabled
                className="bg-gray-100 dark:bg-gray-800 cursor-not-allowed"
              />
              <span className="text-xs text-gray-500">Student ID cannot be changed</span>
            </div>
          )}

          {/* Personal Information Section */}
          <div>
            <h3 className="text-base font-medium mb-4">Personal Information</h3>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="firstName">First Name *</Label>
                  <Input
                    id="firstName"
                    {...register('firstName')}
                    placeholder="Enter first name"
                  />
                  {errors.firstName && (
                    <span className="text-xs text-red-600">{errors.firstName.message as string}</span>
                  )}
                </div>

                <div>
                  <Label htmlFor="lastName">Last Name *</Label>
                  <Input
                    id="lastName"
                    {...register('lastName')}
                    placeholder="Enter last name"
                  />
                  {errors.lastName && (
                    <span className="text-xs text-red-600">{errors.lastName.message as string}</span>
                  )}
                </div>
              </div>

              {/* Date of Birth - Only in create mode */}
              {!isEditMode && (
                <div>
                  <Label>Date of Birth *</Label>
                  <Controller
                    name="dateOfBirth"
                    control={control}
                    render={({ field }) => (
                      <Popover>
                        <PopoverTrigger asChild>
                          <Button
                            variant="outline"
                            className={cn(
                              'w-full justify-start text-left font-normal',
                              !field.value && 'text-muted-foreground'
                            )}
                          >
                            <CalendarIcon className="mr-2 h-4 w-4" />
                            {field.value ? format(field.value, 'PPP') : <span>Pick a date</span>}
                          </Button>
                        </PopoverTrigger>
                        <PopoverContent className="w-auto p-0">
                          <Calendar
                            mode="single"
                            selected={field.value}
                            onSelect={field.onChange}
                            disabled={(date) =>
                              date > new Date() || date < new Date('1900-01-01')
                            }
                            initialFocus
                          />
                        </PopoverContent>
                      </Popover>
                    )}
                  />
                  {errors.dateOfBirth && (
                    <span className="text-xs text-red-600">{errors.dateOfBirth.message as string}</span>
                  )}
                  <span className="text-xs text-gray-500">Student must be between 3-18 years old</span>
                </div>
              )}

              {/* Identification Mark - Only in create mode or read-only in edit mode */}
              {!isEditMode ? (
                <div>
                  <Label htmlFor="identificationMark">Identification Marks</Label>
                  <Input
                    id="identificationMark"
                    {...register('identificationMark')}
                    placeholder="e.g., Mole on left cheek"
                  />
                  {errors.identificationMark && (
                    <span className="text-xs text-red-600">{errors.identificationMark.message as string}</span>
                  )}
                </div>
              ) : studentData && (
                <div>
                  <Label>Identification Marks (Read-only)</Label>
                  <Input value={studentData.identificationMark || 'Not provided'} disabled className="bg-gray-100 dark:bg-gray-800" />
                  <span className="text-xs text-gray-500">Identification marks cannot be changed after registration</span>
                </div>
              )}

              {/* Aadhaar - Only in create mode */}
              {!isEditMode && (
                <div>
                  <Label htmlFor="aadhaarNumber">Aadhaar Number</Label>
                  <Input
                    id="aadhaarNumber"
                    {...register('aadhaarNumber')}
                    placeholder="12-digit Aadhaar number"
                    maxLength={12}
                  />
                  {errors.aadhaarNumber && (
                    <span className="text-xs text-red-600">{errors.aadhaarNumber.message as string}</span>
                  )}
                </div>
              )}

              {/* Status - Only in edit mode */}
              {isEditMode && (
                <div>
                  <Label htmlFor="status">Status *</Label>
                  <Controller
                    name="status"
                    control={control}
                    render={({ field }) => (
                      <Select value={field.value || 'ACTIVE'} onValueChange={field.onChange}>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="ACTIVE">Active</SelectItem>
                          <SelectItem value="INACTIVE">Inactive</SelectItem>
                        </SelectContent>
                      </Select>
                    )}
                  />
                </div>
              )}
            </div>
          </div>

          {/* Guardian Information - Hidden in edit mode */}
          {!isEditMode && (
            <div className="border-t pt-6">
              <h3 className="text-base font-medium mb-4">Guardian Information</h3>
              <div className="space-y-4">
                <div>
                  <Label htmlFor="fathersName">Father's Name</Label>
                  <Input
                    id="fathersName"
                    {...register('fathersName')}
                    placeholder="Enter father's name"
                  />
                  {errors.fathersName && (
                    <span className="text-xs text-red-600">{errors.fathersName.message as string}</span>
                  )}
                </div>

                <div>
                  <Label htmlFor="mothersName">Mother's Name</Label>
                  <Input
                    id="mothersName"
                    {...register('mothersName')}
                    placeholder="Enter mother's name"
                  />
                  {errors.mothersName && (
                    <span className="text-xs text-red-600">{errors.mothersName.message as string}</span>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Contact Information - Hidden in edit mode */}
          {!isEditMode && (
            <div className="border-t pt-6">
              <h3 className="text-base font-medium mb-4">Contact Information</h3>
              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="mobile">Mobile Number *</Label>
                    <Input
                      id="mobile"
                      {...register('mobile')}
                      placeholder="10-digit mobile number"
                      maxLength={10}
                    />
                    {errors.mobile && (
                      <span className="text-xs text-red-600">{errors.mobile.message as string}</span>
                    )}
                  </div>

                  <div>
                    <Label htmlFor="email">Email</Label>
                    <Input
                      id="email"
                      type="email"
                      {...register('email')}
                      placeholder="student@example.com"
                    />
                    {errors.email && (
                      <span className="text-xs text-red-600">{errors.email.message as string}</span>
                    )}
                  </div>
                </div>

                <div>
                  <Label htmlFor="address">Address</Label>
                  <Textarea
                    id="address"
                    {...register('address')}
                    placeholder="Enter complete address"
                    rows={3}
                  />
                  {errors.address && (
                    <span className="text-xs text-red-600">{errors.address.message as string}</span>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Editable contact fields in edit mode (only mobile) */}
          {isEditMode && (
            <div className="border-t pt-6">
              <h3 className="text-base font-medium mb-4">Contact Information</h3>
              <div className="space-y-4">
                <div>
                  <Label htmlFor="mobile">Mobile Number *</Label>
                  <Input
                    id="mobile"
                    {...register('mobile')}
                    placeholder="10-digit mobile number"
                    maxLength={10}
                  />
                  {errors.mobile && (
                    <span className="text-xs text-red-600">{errors.mobile.message as string}</span>
                  )}
                </div>

                {/* Display read-only fields that cannot be edited */}
                {studentData && (
                  <>
                    <div>
                      <Label>Email (Read-only)</Label>
                      <Input value={studentData.email || 'Not provided'} disabled className="bg-gray-100 dark:bg-gray-800" />
                      <span className="text-xs text-gray-500">Email cannot be changed after registration</span>
                    </div>

                    <div>
                      <Label>Address (Read-only)</Label>
                      <Textarea value={studentData.address || 'Not provided'} disabled rows={2} className="bg-gray-100 dark:bg-gray-800" />
                      <span className="text-xs text-gray-500">Address cannot be changed after registration</span>
                    </div>

                    <div>
                      <Label>Father's Name (Read-only)</Label>
                      <Input value={studentData.fathersName || 'Not provided'} disabled className="bg-gray-100 dark:bg-gray-800" />
                      <span className="text-xs text-gray-500">Father's name cannot be changed after registration</span>
                    </div>

                    <div>
                      <Label>Mother's Name (Read-only)</Label>
                      <Input value={studentData.mothersName || 'Not provided'} disabled className="bg-gray-100 dark:bg-gray-800" />
                      <span className="text-xs text-gray-500">Mother's name cannot be changed after registration</span>
                    </div>
                  </>
                )}
              </div>
            </div>
          )}

          <div className="flex justify-end gap-2 pt-4 border-t">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)} disabled={loading}>
              Cancel
            </Button>
            <Button type="submit" className="bg-blue-600 hover:bg-blue-700" disabled={loading}>
              {loading ? 'Saving...' : (isEditMode ? 'Update Student' : 'Register Student')}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
