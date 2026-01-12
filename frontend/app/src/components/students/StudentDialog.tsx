import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '../ui/dialog';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Textarea } from '../ui/textarea';
import { Alert, AlertDescription } from '../ui/alert';
import { AlertCircle, Loader2 } from 'lucide-react';
import type { Student, StudentCreateDto, StudentUpdateDto } from '../../types/student';
import { studentService } from '../../services/studentService';
import { calculateAge, isAgeInRange, isNotFutureDate } from '../../utils/validation';

// Zod validation schema for create mode
const createStudentSchema = z.object({
  firstName: z.string()
    .min(1, 'First name is required')
    .max(50, 'First name cannot exceed 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'First name can only contain letters and spaces'),
  lastName: z.string()
    .min(1, 'Last name is required')
    .max(50, 'Last name cannot exceed 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Last name can only contain letters and spaces'),
  dateOfBirth: z.string()
    .refine((date) => isNotFutureDate(new Date(date)), 'Date of birth cannot be in the future')
    .refine((date) => isAgeInRange(new Date(date)), 'Student must be between 3 and 18 years old'),
  adhaarNumber: z.string()
    .regex(/^\d{12}$/, 'Adhaar number must be exactly 12 digits'),
  phone: z.string()
    .regex(/^\d{10}$/, 'Phone number must be exactly 10 digits'),
  email: z.string()
    .email('Please enter a valid email address'),
  address: z.string()
    .min(10, 'Address must be at least 10 characters')
    .max(500, 'Address cannot exceed 500 characters'),
  identificationMarks: z.string()
    .max(200, 'Identification marks cannot exceed 200 characters')
    .optional(),
  guardianName: z.string()
    .min(1, 'Guardian name is required')
    .max(100, 'Guardian name cannot exceed 100 characters'),
  motherName: z.string()
    .min(1, 'Mother name is required')
    .max(100, 'Mother name cannot exceed 100 characters'),
  status: z.enum(['ACTIVE', 'INACTIVE']),
});

// Zod validation schema for edit mode (only firstName, lastName, phone, status allowed)
const editStudentSchema = z.object({
  firstName: z.string()
    .min(1, 'First name is required')
    .max(50, 'First name cannot exceed 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'First name can only contain letters and spaces'),
  lastName: z.string()
    .min(1, 'Last name is required')
    .max(50, 'Last name cannot exceed 50 characters')
    .regex(/^[a-zA-Z\s]+$/, 'Last name can only contain letters and spaces'),
  phone: z.string()
    .regex(/^\d{10}$/, 'Phone number must be exactly 10 digits'),
  status: z.enum(['ACTIVE', 'INACTIVE']),
});

interface StudentDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (student: StudentCreateDto | StudentUpdateDto) => Promise<void>;
  student?: Student | null;
  mode: 'create' | 'edit';
}

export function StudentDialog({ open, onOpenChange, onSubmit, student, mode }: StudentDialogProps) {
  const [isValidatingPhone, setIsValidatingPhone] = useState(false);
  const [generalError, setGeneralError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const isEditMode = mode === 'edit' && student;
  const schema = isEditMode ? editStudentSchema : createStudentSchema;

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    setError,
    clearErrors,
    formState: { errors },
  } = useForm<any>({
    resolver: zodResolver(schema),
    defaultValues: isEditMode
      ? {
          firstName: student?.firstName || '',
          lastName: student?.lastName || '',
          phone: student?.phone || '',
          status: student?.status || 'ACTIVE',
        }
      : {
          firstName: '',
          lastName: '',
          dateOfBirth: '',
          adhaarNumber: '',
          phone: '',
          email: '',
          address: '',
          identificationMarks: '',
          guardianName: '',
          motherName: '',
          status: 'ACTIVE',
        },
  });

  const status = watch('status');
  const phone = watch('phone');
  const dateOfBirth = watch('dateOfBirth');

  useEffect(() => {
    if (open) {
      setGeneralError(null);
      if (isEditMode) {
        reset({
          firstName: student?.firstName || '',
          lastName: student?.lastName || '',
          phone: student?.phone || '',
          status: student?.status || 'ACTIVE',
        });
      } else {
        reset({
          firstName: '',
          lastName: '',
          dateOfBirth: '',
          adhaarNumber: '',
          phone: '',
          email: '',
          address: '',
          identificationMarks: '',
          guardianName: '',
          motherName: '',
          status: 'ACTIVE',
        });
      }
    }
  }, [student, reset, open, isEditMode]);

  // Async phone validation
  useEffect(() => {
    const validatePhone = async () => {
      if (phone && phone.length === 10) {
        // Skip validation if editing and phone hasn't changed
        if (isEditMode && phone === student?.phone) {
          clearErrors('phone');
          return;
        }

        setIsValidatingPhone(true);
        try {
          const isUnique = await studentService.validatePhone(phone, student?.id);
          if (!isUnique) {
            setError('phone', {
              type: 'manual',
              message: 'This phone number is already registered',
            });
          } else {
            clearErrors('phone');
          }
        } catch (error) {
          console.error('Phone validation error:', error);
        } finally {
          setIsValidatingPhone(false);
        }
      }
    };

    const timeoutId = setTimeout(validatePhone, 500);
    return () => clearTimeout(timeoutId);
  }, [phone, student?.phone, student?.id, setError, clearErrors, isEditMode]);

  // Calculate age when DOB changes
  const calculatedAge = dateOfBirth ? calculateAge(new Date(dateOfBirth)) : null;

  const handleFormSubmit = async (data: any) => {
    setGeneralError(null);
    setIsSubmitting(true);

    try {
      if (isEditMode) {
        // Only send editable fields
        const updateData: StudentUpdateDto = {
          firstName: data.firstName,
          lastName: data.lastName,
          phone: data.phone,
          status: data.status,
        };
        await onSubmit(updateData);
      } else {
        // Send all fields for create
        const createData: StudentCreateDto = {
          ...data,
          identificationMarks: data.identificationMarks || '',
        };
        await onSubmit(createData);
      }
      onOpenChange(false);
      reset();
    } catch (error: any) {
      setGeneralError(error.message || 'Failed to save student. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>{isEditMode ? 'Edit Student' : 'Register New Student'}</DialogTitle>
        </DialogHeader>

        {generalError && (
          <Alert variant="destructive">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{generalError}</AlertDescription>
          </Alert>
        )}

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
          {/* Personal Information Section */}
          <div>
            <h3 className="text-base font-semibold mb-4">Personal Information</h3>
            <div className="space-y-4">
              {/* Student ID - Show in edit mode only, disabled */}
              {isEditMode && (
                <div>
                  <Label htmlFor="studentId">Student ID</Label>
                  <Input
                    id="studentId"
                    value={student?.id || ''}
                    disabled
                    className="bg-gray-100 dark:bg-gray-800 cursor-not-allowed"
                  />
                  <span className="text-xs text-gray-500">Student ID cannot be changed</span>
                </div>
              )}

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="firstName">First Name*</Label>
                  <Input
                    id="firstName"
                    {...register('firstName')}
                    placeholder="Enter first name"
                  />
                  {errors.firstName && (
                    <span className="text-xs text-red-600">{String(errors.firstName.message)}</span>
                  )}
                </div>

                <div>
                  <Label htmlFor="lastName">Last Name*</Label>
                  <Input
                    id="lastName"
                    {...register('lastName')}
                    placeholder="Enter last name"
                  />
                  {errors.lastName && (
                    <span className="text-xs text-red-600">{String(errors.lastName.message)}</span>
                  )}
                </div>
              </div>

              {!isEditMode && (
                <>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <Label htmlFor="dateOfBirth">Date of Birth*</Label>
                      <Input
                        id="dateOfBirth"
                        type="date"
                        {...register('dateOfBirth')}
                      />
                      {calculatedAge !== null && (
                        <span className="text-xs text-gray-600">Age: {calculatedAge} years</span>
                      )}
                      {errors.dateOfBirth && (
                        <span className="text-xs text-red-600">{String(errors.dateOfBirth.message)}</span>
                      )}
                    </div>

                    <div>
                      <Label htmlFor="adhaarNumber">Adhaar Number*</Label>
                      <Input
                        id="adhaarNumber"
                        {...register('adhaarNumber')}
                        placeholder="12 digit Adhaar number"
                        maxLength={12}
                      />
                      {errors.adhaarNumber && (
                        <span className="text-xs text-red-600">{String(errors.adhaarNumber.message)}</span>
                      )}
                    </div>
                  </div>

                  <div>
                    <Label htmlFor="address">Address*</Label>
                    <Textarea
                      id="address"
                      {...register('address')}
                      placeholder="Enter complete address"
                      rows={3}
                    />
                    {errors.address && (
                      <span className="text-xs text-red-600">{String(errors.address.message)}</span>
                    )}
                  </div>

                  <div>
                    <Label htmlFor="identificationMarks">Identification Marks</Label>
                    <Input
                      id="identificationMarks"
                      {...register('identificationMarks')}
                      placeholder="Enter identification marks (e.g., mole, birthmark)"
                    />
                    {errors.identificationMarks && (
                      <span className="text-xs text-red-600">{String(errors.identificationMarks.message)}</span>
                    )}
                  </div>
                </>
              )}

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="phone">Phone Number*</Label>
                  <div className="relative">
                    <Input
                      id="phone"
                      {...register('phone')}
                      placeholder="10 digit phone number"
                      maxLength={10}
                    />
                    {isValidatingPhone && (
                      <Loader2 className="absolute right-3 top-1/2 transform -translate-y-1/2 h-4 w-4 animate-spin text-gray-400" />
                    )}
                  </div>
                  {errors.phone && (
                    <span className="text-xs text-red-600">{String(errors.phone.message)}</span>
                  )}
                </div>

                <div>
                  <Label htmlFor="status">Status*</Label>
                  <Select value={status} onValueChange={(value) => setValue('status', value as 'ACTIVE' | 'INACTIVE')}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="ACTIVE">Active</SelectItem>
                      <SelectItem value="INACTIVE">Inactive</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </div>
          </div>

          {!isEditMode && (
            <>
              {/* Guardian Information Section */}
              <div className="border-t pt-6">
                <h3 className="text-base font-semibold mb-4">Guardian Information</h3>
                <div className="space-y-4">
                  <div>
                    <Label htmlFor="guardianName">Name of Father/Guardian*</Label>
                    <Input
                      id="guardianName"
                      {...register('guardianName')}
                      placeholder="Enter father or guardian name"
                    />
                    {errors.guardianName && (
                      <span className="text-xs text-red-600">{String(errors.guardianName.message)}</span>
                    )}
                  </div>

                  <div>
                    <Label htmlFor="motherName">Mother Name*</Label>
                    <Input
                      id="motherName"
                      {...register('motherName')}
                      placeholder="Enter mother name"
                    />
                    {errors.motherName && (
                      <span className="text-xs text-red-600">{String(errors.motherName.message)}</span>
                    )}
                  </div>
                </div>
              </div>

              {/* Contact Information Section */}
              <div className="border-t pt-6">
                <h3 className="text-base font-semibold mb-4">Contact Information</h3>
                <div className="space-y-4">
                  <div>
                    <Label htmlFor="email">Email*</Label>
                    <Input
                      id="email"
                      type="email"
                      {...register('email')}
                      placeholder="Enter email address"
                    />
                    {errors.email && (
                      <span className="text-xs text-red-600">{String(errors.email.message)}</span>
                    )}
                  </div>
                </div>
              </div>
            </>
          )}

          <div className="flex justify-end gap-2 pt-4 border-t">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)} disabled={isSubmitting}>
              Cancel
            </Button>
            <Button type="submit" className="bg-blue-600 hover:bg-blue-700" disabled={isSubmitting || isValidatingPhone}>
              {isSubmitting ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  {isEditMode ? 'Updating...' : 'Registering...'}
                </>
              ) : (
                <>{isEditMode ? 'Update Student' : 'Register Student'}</>
              )}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
