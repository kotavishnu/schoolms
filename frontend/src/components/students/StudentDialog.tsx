import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { X } from 'lucide-react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { studentCreateSchema, studentUpdateSchema, calculateAge } from '@/utils/validation';
import { studentService } from '@/services/studentService';
import { useDebounce } from '@/hooks/useDebounce';
import type { Student, StudentCreateRequest, StudentUpdateRequest } from '@/types/student';
import type { StudentCreateFormData, StudentUpdateFormData } from '@/utils/validation';
import { toast } from 'sonner';

interface StudentDialogProps {
  mode: 'create' | 'edit' | 'view';
  student?: Student;
  open: boolean;
  onClose: () => void;
  onSubmit: (data: StudentCreateRequest | StudentUpdateRequest) => Promise<void>;
}

export function StudentDialog({ mode, student, open, onClose, onSubmit }: StudentDialogProps) {
  const isEditMode = mode === 'edit';
  const isViewMode = mode === 'view';
  const isCreateMode = mode === 'create';

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isValidatingPhone, setIsValidatingPhone] = useState(false);
  const [phoneError, setPhoneError] = useState<string | null>(null);

  const createForm = useForm<StudentCreateFormData>({
    resolver: zodResolver(studentCreateSchema),
  });

  const editForm = useForm<StudentUpdateFormData>({
    resolver: zodResolver(studentUpdateSchema),
  });

  const debouncedPhone = useDebounce(
    isEditMode ? editForm.watch('phone') : createForm.watch('phone'),
    500
  );

  const [calculatedAge, setCalculatedAge] = useState<number | null>(null);
  useEffect(() => {
    if (isCreateMode) {
      const dob = createForm.watch('dateOfBirth');
      if (dob) {
        try {
          setCalculatedAge(calculateAge(dob));
        } catch {
          setCalculatedAge(null);
        }
      }
    }
  }, [isCreateMode, createForm.watch('dateOfBirth')]);

  useEffect(() => {
    const validatePhoneUniqueness = async () => {
      if (!debouncedPhone || debouncedPhone.length !== 10) {
        setPhoneError(null);
        return;
      }

      setIsValidatingPhone(true);
      setPhoneError(null);

      try {
        const isUnique = await studentService.validatePhone(
          debouncedPhone,
          isEditMode ? student?.id : undefined
        );
        if (!isUnique) {
          setPhoneError('This phone number is already registered');
        }
      } catch (error) {
        console.error('Phone validation error:', error);
      } finally {
        setIsValidatingPhone(false);
      }
    };

    validatePhoneUniqueness();
  }, [debouncedPhone, isEditMode, student?.id]);

  const handleFormSubmit = async (data: any) => {
    if (phoneError) {
      toast.error('Please fix validation errors');
      return;
    }

    setIsSubmitting(true);
    try {
      await onSubmit(data);
      createForm.reset();
      editForm.reset();
      onClose();
    } catch (error) {
      console.error('Form submission error:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClose = () => {
    createForm.reset();
    editForm.reset();
    setPhoneError(null);
    onClose();
  };

  useEffect(() => {
    if (open && student && isEditMode) {
      editForm.reset({
        firstName: student.firstName,
        lastName: student.lastName,
        phone: student.phone,
        status: student.status,
      });
    } else if (open && isCreateMode) {
      createForm.reset({
        firstName: '',
        lastName: '',
        dateOfBirth: '',
        adhaarNumber: '',
        identificationMarks: '',
        address: '',
        guardianName: '',
        motherName: '',
        phone: '',
        email: '',
        status: 'ACTIVE',
      });
    }
  }, [open, student, isEditMode, isCreateMode]);

  if (isViewMode && student) {
    return (
      <Dialog open={open} onOpenChange={handleClose}>
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <div className="flex items-center justify-between">
              <DialogTitle>Student Details</DialogTitle>
              <Button variant="ghost" size="icon" onClick={handleClose}>
                <X className="h-4 w-4" />
              </Button>
            </div>
          </DialogHeader>
          <div className="space-y-6">
            {/* Basic Information */}
            <div>
              <h3 className="text-lg font-semibold mb-4">Basic Information</h3>
              <div className="space-y-3">
                <div className="grid grid-cols-2 gap-4">
                  <div><Label className="text-gray-600">Student ID</Label><p className="text-sm font-medium">{student.id}</p></div>
                  <div><Label className="text-gray-600">Status</Label><p className="text-sm font-medium">{student.status}</p></div>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div><Label className="text-gray-600">First Name</Label><p className="text-sm font-medium">{student.firstName}</p></div>
                  <div><Label className="text-gray-600">Last Name</Label><p className="text-sm font-medium">{student.lastName}</p></div>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div><Label className="text-gray-600">Age</Label><p className="text-sm font-medium">{student.age} years</p></div>
                  <div><Label className="text-gray-600">Identification Marks</Label><p className="text-sm font-medium">{student.identificationMarks || '-'}</p></div>
                </div>
                <div><Label className="text-gray-600">Address</Label><p className="text-sm font-medium">{student.address}</p></div>
              </div>
            </div>

            {/* Guardian Information */}
            <div>
              <h3 className="text-lg font-semibold mb-4">Guardian Information</h3>
              <div className="space-y-3">
                <div><Label className="text-gray-600">Name of Father/Guardian</Label><p className="text-sm font-medium">{student.guardianName}</p></div>
                <div><Label className="text-gray-600">Mother Name</Label><p className="text-sm font-medium">{student.motherName}</p></div>
              </div>
            </div>

            {/* Contact Information */}
            <div>
              <h3 className="text-lg font-semibold mb-4">Contact Information</h3>
              <div className="grid grid-cols-2 gap-4">
                <div><Label className="text-gray-600">Phone Number</Label><p className="text-sm font-medium">{student.phone}</p></div>
                <div><Label className="text-gray-600">Email</Label><p className="text-sm font-medium">{student.email}</p></div>
              </div>
            </div>

            <div className="flex justify-end pt-4 border-t"><Button onClick={handleClose}>Close</Button></div>
          </div>
        </DialogContent>
      </Dialog>
    );
  }

  if (isEditMode && student) {
    const { register, handleSubmit, formState: { errors }, watch, setValue } = editForm;
    return (
      <Dialog open={open} onOpenChange={handleClose}>
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <div className="flex items-center justify-between">
              <DialogTitle>Edit Student</DialogTitle>
              <Button variant="ghost" size="icon" onClick={handleClose}>
                <X className="h-4 w-4" />
              </Button>
            </div>
          </DialogHeader>
          <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
            <div>
              <h3 className="text-lg font-semibold mb-4">Personal Information</h3>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label>First Name <span className="text-red-500">*</span></Label>
                  <Input {...register('firstName')} placeholder="Enter first name" />
                  {errors.firstName && <p className="text-xs text-red-500 mt-1">{errors.firstName.message}</p>}
                </div>
                <div>
                  <Label>Last Name <span className="text-red-500">*</span></Label>
                  <Input {...register('lastName')} placeholder="Enter last name" />
                  {errors.lastName && <p className="text-xs text-red-500 mt-1">{errors.lastName.message}</p>}
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4 mt-4">
                <div>
                  <Label>Age <span className="text-red-500">*</span></Label>
                  <Input value={student.age} disabled className="bg-gray-50" placeholder="Enter age" />
                </div>
                <div>
                  <Label>Status <span className="text-red-500">*</span></Label>
                  <Select value={watch('status')} onValueChange={(value) => setValue('status', value as 'ACTIVE' | 'INACTIVE')}>
                    <SelectTrigger><SelectValue /></SelectTrigger>
                    <SelectContent>
                      <SelectItem value="ACTIVE">Active</SelectItem>
                      <SelectItem value="INACTIVE">Inactive</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
              <div className="mt-4">
                <Label>Address <span className="text-red-500">*</span></Label>
                <Textarea value={student.address} disabled className="bg-gray-50" rows={3} placeholder="Enter complete address" />
              </div>
              <div className="mt-4">
                <Label>Identification Marks</Label>
                <Textarea value={student.identificationMarks || ''} disabled className="bg-gray-50" rows={2} placeholder="Enter identification marks (e.g., mole, birthmark)" />
              </div>
            </div>
            <div>
              <h3 className="text-lg font-semibold mb-4">Guardian Information</h3>
              <div>
                <Label>Name of Father/Guardian <span className="text-red-500">*</span></Label>
                <Input value={student.guardianName} disabled className="bg-gray-50" placeholder="Enter father or guardian name" />
              </div>
              <div className="mt-4">
                <Label>Mother Name <span className="text-red-500">*</span></Label>
                <Input value={student.motherName} disabled className="bg-gray-50" placeholder="Enter mother name" />
              </div>
            </div>
            <div>
              <h3 className="text-lg font-semibold mb-4">Contact Information</h3>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label>Phone Number <span className="text-red-500">*</span></Label>
                  <Input {...register('phone')} maxLength={10} placeholder="Enter phone number" />
                  {isValidatingPhone && <p className="text-xs text-blue-500 mt-1">Checking availability...</p>}
                  {phoneError && <p className="text-xs text-red-500 mt-1">{phoneError}</p>}
                  {errors.phone && <p className="text-xs text-red-500 mt-1">{errors.phone.message}</p>}
                </div>
                <div>
                  <Label>Email <span className="text-red-500">*</span></Label>
                  <Input type="email" value={student.email} disabled className="bg-gray-50" placeholder="Enter email address" />
                </div>
              </div>
            </div>
            <div className="flex justify-end gap-3 pt-4 border-t">
              <Button type="button" variant="outline" onClick={handleClose}>Cancel</Button>
              <Button type="submit" disabled={isSubmitting || isValidatingPhone || !!phoneError}>
                {isSubmitting ? 'Saving...' : 'Update Student'}
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    );
  }

  const { register, handleSubmit, formState: { errors }, watch, setValue } = createForm;
  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <div className="flex items-center justify-between">
            <DialogTitle>Register New Student</DialogTitle>
            <Button variant="ghost" size="icon" onClick={handleClose}>
              <X className="h-4 w-4" />
            </Button>
          </div>
        </DialogHeader>
        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
          <div>
            <h3 className="text-lg font-semibold mb-4">Personal Information</h3>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label>First Name <span className="text-red-500">*</span></Label>
                <Input {...register('firstName')} />
                {errors.firstName && <p className="text-xs text-red-500 mt-1">{errors.firstName.message}</p>}
              </div>
              <div>
                <Label>Last Name <span className="text-red-500">*</span></Label>
                <Input {...register('lastName')} />
                {errors.lastName && <p className="text-xs text-red-500 mt-1">{errors.lastName.message}</p>}
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4 mt-4">
              <div>
                <Label>Age <span className="text-red-500">*</span></Label>
                <Input
                  placeholder="Enter age"
                  value={calculatedAge !== null ? calculatedAge : ''}
                  disabled
                  className="bg-gray-50"
                />
                {calculatedAge !== null && <p className="text-xs text-gray-500 mt-1">Calculated from date of birth</p>}
              </div>
              <div>
                <Label>Status <span className="text-red-500">*</span></Label>
                <Select value={watch('status')} onValueChange={(value) => setValue('status', value as 'ACTIVE' | 'INACTIVE')}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ACTIVE">Active</SelectItem>
                    <SelectItem value="INACTIVE">Inactive</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
            <div className="mt-4">
              <Label>Address <span className="text-red-500">*</span></Label>
              <Textarea {...register('address')} rows={3} placeholder="Enter complete address" />
              {errors.address && <p className="text-xs text-red-500 mt-1">{errors.address.message}</p>}
            </div>
            <div className="grid grid-cols-2 gap-4 mt-4">
              <div>
                <Label>Date of Birth <span className="text-red-500">*</span></Label>
                <Input type="date" {...register('dateOfBirth')} />
                {errors.dateOfBirth && <p className="text-xs text-red-500 mt-1">{errors.dateOfBirth.message}</p>}
              </div>
              <div>
                <Label>Aadhaar Number <span className="text-red-500">*</span></Label>
                <Input {...register('adhaarNumber')} maxLength={12} placeholder="Enter 12-digit Aadhaar" />
                {errors.adhaarNumber && <p className="text-xs text-red-500 mt-1">{errors.adhaarNumber.message}</p>}
              </div>
            </div>
            <div className="mt-4">
              <Label>Identification Marks</Label>
              <Textarea {...register('identificationMarks')} rows={2} placeholder="Enter identification marks (e.g., mole, birthmark)" />
              {errors.identificationMarks && <p className="text-xs text-red-500 mt-1">{errors.identificationMarks.message}</p>}
            </div>
          </div>
          <div>
            <h3 className="text-lg font-semibold mb-4">Guardian Information</h3>
            <div>
              <Label>Name of Father/Guardian <span className="text-red-500">*</span></Label>
              <Input {...register('guardianName')} placeholder="Enter father or guardian name" />
              {errors.guardianName && <p className="text-xs text-red-500 mt-1">{errors.guardianName.message}</p>}
            </div>
            <div className="mt-4">
              <Label>Mother Name <span className="text-red-500">*</span></Label>
              <Input {...register('motherName')} placeholder="Enter mother name" />
              {errors.motherName && <p className="text-xs text-red-500 mt-1">{errors.motherName.message}</p>}
            </div>
          </div>
          <div>
            <h3 className="text-lg font-semibold mb-4">Contact Information</h3>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label>Phone Number <span className="text-red-500">*</span></Label>
                <Input {...register('phone')} maxLength={10} placeholder="Enter phone number" />
                {isValidatingPhone && <p className="text-xs text-blue-500 mt-1">Checking availability...</p>}
                {phoneError && <p className="text-xs text-red-500 mt-1">{phoneError}</p>}
                {errors.phone && <p className="text-xs text-red-500 mt-1">{errors.phone.message}</p>}
              </div>
              <div>
                <Label>Email <span className="text-red-500">*</span></Label>
                <Input type="email" {...register('email')} placeholder="Enter email address" />
                {errors.email && <p className="text-xs text-red-500 mt-1">{errors.email.message}</p>}
              </div>
            </div>
          </div>
          <div className="flex justify-end gap-3 pt-4 border-t">
            <Button type="button" variant="outline" onClick={handleClose}>Cancel</Button>
            <Button type="submit" disabled={isSubmitting || isValidatingPhone || !!phoneError}>
              {isSubmitting ? 'Saving...' : 'Register Student'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
