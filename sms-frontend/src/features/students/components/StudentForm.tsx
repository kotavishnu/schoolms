import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { studentSchema, type StudentFormData } from '@/schemas/studentSchema';
import Input from '@/components/common/Input';
import Button from '@/components/common/Button';

interface StudentFormProps {
  initialData?: Partial<StudentFormData>;
  onSubmit: (data: StudentFormData) => void;
  isLoading?: boolean;
  onCancel?: () => void;
}

const StudentForm: React.FC<StudentFormProps> = ({
  initialData,
  onSubmit,
  isLoading = false,
  onCancel,
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<StudentFormData>({
    resolver: zodResolver(studentSchema),
    defaultValues: initialData,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Personal Information Section */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
          Personal Information
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="First Name"
            {...register('firstName')}
            error={errors.firstName?.message}
            required
          />

          <Input
            label="Last Name"
            {...register('lastName')}
            error={errors.lastName?.message}
            required
          />

          <Input
            label="Date of Birth"
            type="date"
            {...register('dateOfBirth')}
            error={errors.dateOfBirth?.message}
            required
          />

          <Input
            label="Mobile Number"
            {...register('mobile')}
            placeholder="10-15 digits"
            error={errors.mobile?.message}
            required
          />

          <Input
            label="Email"
            type="email"
            {...register('email')}
            error={errors.email?.message}
            placeholder="Optional"
          />

          <Input
            label="Aadhaar Number"
            {...register('aadhaarNumber')}
            placeholder="12 digits (Optional)"
            error={errors.aadhaarNumber?.message}
            maxLength={12}
          />
        </div>

        <div className="mt-4">
          <Input
            label="Address"
            {...register('address')}
            error={errors.address?.message}
            multiline
            rows={3}
            required
          />
        </div>

        <div className="mt-4">
          <Input
            label="Identification Mark"
            {...register('identificationMark')}
            error={errors.identificationMark?.message}
            placeholder="Optional"
          />
        </div>
      </div>

      {/* Guardian Information Section */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
          Guardian Information
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Father's Name / Guardian"
            {...register('fatherName')}
            error={errors.fatherName?.message}
            required
          />

          <Input
            label="Mother's Name"
            {...register('motherName')}
            error={errors.motherName?.message}
            placeholder="Optional"
          />
        </div>
      </div>

      {/* Form Actions */}
      <div className="flex justify-end gap-4">
        {onCancel && (
          <Button type="button" variant="secondary" onClick={onCancel} disabled={isLoading}>
            Cancel
          </Button>
        )}
        <Button type="submit" variant="primary" isLoading={isLoading} disabled={isLoading}>
          {initialData ? 'Update Student' : 'Register Student'}
        </Button>
      </div>
    </form>
  );
};

export default StudentForm;
