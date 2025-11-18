import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { CreateStudentRequest, Student } from '@/types/student';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import { calculateAge } from '@/lib/utils';

const studentSchema = z.object({
  firstName: z.string().min(2, 'First name must be at least 2 characters').max(50, 'First name must be less than 50 characters'),
  lastName: z.string().min(2, 'Last name must be at least 2 characters').max(50, 'Last name must be less than 50 characters'),
  dateOfBirth: z.string().refine((date) => {
    const age = calculateAge(date);
    return age >= 3 && age <= 18;
  }, { message: 'Student age must be between 3 and 18 years' }),
  street: z.string().min(1, 'Street address is required'),
  city: z.string().min(1, 'City is required'),
  state: z.string().min(1, 'State is required'),
  pinCode: z.string().regex(/^[0-9]{6}$/, 'Pin code must be 6 digits'),
  country: z.string().min(1, 'Country is required').default('India'),
  mobile: z.string().regex(/^[0-9]{10}$/, 'Mobile must be 10 digits'),
  email: z.string().email('Invalid email').optional().or(z.literal('')),
  fatherNameOrGuardian: z.string().min(1, 'Father/Guardian name is required'),
  motherName: z.string().optional(),
  caste: z.string().optional(),
  moles: z.string().optional(),
  aadhaarNumber: z.string().regex(/^[0-9]{12}$/, 'Aadhaar must be 12 digits').optional().or(z.literal('')),
});

type StudentFormData = z.infer<typeof studentSchema>;

interface StudentFormProps {
  student?: Student;
  onSubmit: (data: CreateStudentRequest) => void;
  onCancel: () => void;
  isLoading?: boolean;
}

export default function StudentForm({ student, onSubmit, onCancel, isLoading }: StudentFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<StudentFormData>({
    resolver: zodResolver(studentSchema),
    defaultValues: student
      ? {
          firstName: student.firstName,
          lastName: student.lastName,
          dateOfBirth: student.dateOfBirth,
          street: student.address.street,
          city: student.address.city,
          state: student.address.state,
          pinCode: student.address.pinCode,
          country: student.address.country || 'India',
          mobile: student.mobile,
          email: student.email || '',
          fatherNameOrGuardian: student.fatherNameOrGuardian,
          motherName: student.motherName || '',
          caste: student.caste || '',
          moles: student.moles || '',
          aadhaarNumber: student.aadhaarNumber || '',
        }
      : undefined,
  });

  const dateOfBirth = watch('dateOfBirth');
  const calculatedAge = dateOfBirth ? calculateAge(dateOfBirth) : null;

  const handleFormSubmit = (data: StudentFormData) => {
    onSubmit(data as CreateStudentRequest);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>Personal Information</CardTitle>
        </CardHeader>
        <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            {...register('firstName')}
            label="First Name"
            error={errors.firstName?.message}
            required
          />
          <Input
            {...register('lastName')}
            label="Last Name"
            error={errors.lastName?.message}
            required
          />
          <div>
            <Input
              {...register('dateOfBirth')}
              type="date"
              label="Date of Birth"
              error={errors.dateOfBirth?.message}
              required
            />
            {calculatedAge !== null && (
              <p className="mt-1 text-sm text-gray-600">Age: {calculatedAge} years</p>
            )}
          </div>
          <Input
            {...register('caste')}
            label="Caste"
            error={errors.caste?.message}
          />
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Address Information</CardTitle>
        </CardHeader>
        <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="md:col-span-2">
            <Input
              {...register('street')}
              label="Street Address"
              error={errors.street?.message}
              required
            />
          </div>
          <Input
            {...register('city')}
            label="City"
            error={errors.city?.message}
            required
          />
          <Input
            {...register('state')}
            label="State"
            error={errors.state?.message}
            required
          />
          <Input
            {...register('pinCode')}
            label="Pin Code"
            placeholder="6 digits"
            error={errors.pinCode?.message}
            required
          />
          <Input
            {...register('country')}
            label="Country"
            error={errors.country?.message}
            required
          />
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Contact Information</CardTitle>
        </CardHeader>
        <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            {...register('mobile')}
            label="Mobile Number"
            placeholder="10 digits"
            error={errors.mobile?.message}
            required
          />
          <Input
            {...register('email')}
            type="email"
            label="Email"
            error={errors.email?.message}
          />
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Family Information</CardTitle>
        </CardHeader>
        <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            {...register('fatherNameOrGuardian')}
            label="Father Name / Guardian"
            error={errors.fatherNameOrGuardian?.message}
            required
          />
          <Input
            {...register('motherName')}
            label="Mother Name"
            error={errors.motherName?.message}
          />
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Additional Information</CardTitle>
        </CardHeader>
        <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            {...register('aadhaarNumber')}
            label="Aadhaar Number"
            placeholder="12 digits"
            error={errors.aadhaarNumber?.message}
          />
          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Moles (Identifying Marks)
            </label>
            <textarea
              {...register('moles')}
              className="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
              rows={3}
            />
            {errors.moles && (
              <p className="mt-1 text-sm text-red-500">{errors.moles.message}</p>
            )}
          </div>
        </CardContent>
      </Card>

      <div className="flex justify-end gap-4">
        <Button type="button" variant="outline" onClick={onCancel} disabled={isLoading}>
          Cancel
        </Button>
        <Button type="submit" isLoading={isLoading}>
          {student ? 'Update Student' : 'Create Student'}
        </Button>
      </div>
    </form>
  );
}
