import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from './ui/dialog';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Textarea } from './ui/textarea';
import type { Student } from '../types';

interface StudentDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (student: Omit<Student, 'id'>) => void;
  student?: Student | null;
}

export function StudentDialog({ open, onOpenChange, onSubmit, student }: StudentDialogProps) {
  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<Omit<Student, 'id'>>({
    defaultValues: {
      firstName: '',
      lastName: '',
      guardianName: '',
      motherName: '',
      phone: '',
      age: 0,
      email: '',
      address: '',
      identificationMarks: '',
      status: 'ACTIVE',
    },
  });

  const status = watch('status');

  useEffect(() => {
    if (student) {
      reset(student);
    } else {
      reset({
        firstName: '',
        lastName: '',
        guardianName: '',
        motherName: '',
        phone: '',
        age: 0,
        email: '',
        address: '',
        identificationMarks: '',
        status: 'ACTIVE',
      });
    }
  }, [student, reset]);

  const handleFormSubmit = (data: Omit<Student, 'id'>) => {
    onSubmit(data);
    onOpenChange(false);
    reset();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>{student ? 'Edit Student' : 'Register New Student'}</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
          {/* Personal Information Section */}
          <div>
            <h3 className="text-base mb-4">Personal Information</h3>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="firstName">First Name*</Label>
                  <Input
                    id="firstName"
                    {...register('firstName', { required: 'First name is required' })}
                    placeholder="Enter first name"
                  />
                  {errors.firstName && (
                    <span className="text-xs text-red-600">{errors.firstName.message}</span>
                  )}
                </div>

                <div>
                  <Label htmlFor="lastName">Last Name*</Label>
                  <Input
                    id="lastName"
                    {...register('lastName', { required: 'Last name is required' })}
                    placeholder="Enter last name"
                  />
                  {errors.lastName && (
                    <span className="text-xs text-red-600">{errors.lastName.message}</span>
                  )}
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="age">Age*</Label>
                  <Input
                    id="age"
                    type="number"
                    {...register('age', { required: 'Age is required', valueAsNumber: true })}
                    placeholder="Enter age"
                  />
                  {errors.age && <span className="text-xs text-red-600">{errors.age.message}</span>}
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

              <div>
                <Label htmlFor="address">Address*</Label>
                <Textarea
                  id="address"
                  {...register('address', { required: 'Address is required' })}
                  placeholder="Enter complete address"
                  rows={3}
                />
                {errors.address && (
                  <span className="text-xs text-red-600">{errors.address.message}</span>
                )}
              </div>

              <div>
                <Label htmlFor="identificationMarks">Identification Marks</Label>
                <Input
                  id="identificationMarks"
                  {...register('identificationMarks')}
                  placeholder="Enter identification marks (e.g., mole, birthmark)"
                />
              </div>
            </div>
          </div>

          {/* Guardian Information Section */}
          <div className="border-t pt-6">
            <h3 className="text-base mb-4">Guardian Information</h3>
            <div className="space-y-4">
              <div>
                <Label htmlFor="guardianName">Name of Father/Guardian*</Label>
                <Input
                  id="guardianName"
                  {...register('guardianName', { required: 'Father/Guardian name is required' })}
                  placeholder="Enter father or guardian name"
                />
                {errors.guardianName && (
                  <span className="text-xs text-red-600">{errors.guardianName.message}</span>
                )}
              </div>

              <div>
                <Label htmlFor="motherName">Mother Name*</Label>
                <Input
                  id="motherName"
                  {...register('motherName', { required: 'Mother name is required' })}
                  placeholder="Enter mother name"
                />
                {errors.motherName && (
                  <span className="text-xs text-red-600">{errors.motherName.message}</span>
                )}
              </div>
            </div>
          </div>

          {/* Contact Information Section */}
          <div className="border-t pt-6">
            <h3 className="text-base mb-4">Contact Information</h3>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="phone">Phone Number*</Label>
                  <Input
                    id="phone"
                    {...register('phone', { required: 'Phone is required' })}
                    placeholder="Enter phone number"
                  />
                  {errors.phone && <span className="text-xs text-red-600">{errors.phone.message}</span>}
                </div>

                <div>
                  <Label htmlFor="email">Email*</Label>
                  <Input
                    id="email"
                    type="email"
                    {...register('email', { required: 'Email is required' })}
                    placeholder="Enter email address"
                  />
                  {errors.email && <span className="text-xs text-red-600">{errors.email.message}</span>}
                </div>
              </div>
            </div>
          </div>

          <div className="flex justify-end gap-2 pt-4 border-t">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" className="bg-blue-600 hover:bg-blue-700">
              {student ? 'Update Student' : 'Register Student'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}