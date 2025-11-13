import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Upload, X } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Input } from '@/shared/components/ui/Input';
import { Select } from '@/shared/components/ui/Select';
import { useCreateStudent, useAvailableClasses } from '../hooks/useStudents';
import { studentRegistrationSchema, type StudentRegistrationFormData } from '../schemas/student.schema';
import { ROUTES } from '@/shared/constants/routes';

/**
 * Student Registration Form Page
 *
 * Features:
 * - Multi-section form: Personal Info, Contact, Guardian, Academic
 * - Photo upload with preview
 * - Form validation with Zod
 * - Success/error notifications
 * - Redirect to student list after success
 */
export const StudentRegistration = () => {
  const navigate = useNavigate();
  const [photoPreview, setPhotoPreview] = useState<string | null>(null);

  // React Hook Form setup
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
  } = useForm<StudentRegistrationFormData>({
    resolver: zodResolver(studentRegistrationSchema),
    defaultValues: {
      bloodGroup: undefined,
      email: undefined,
      phone: undefined,
      photo: null,
      address: {
        country: 'India',
      },
      guardian: {
        email: undefined,
        occupation: undefined,
      },
      academic: {
        rollNumber: undefined,
        previousSchool: undefined,
      },
    },
  });

  // Fetch available classes
  const { data: classes, isLoading: classesLoading } = useAvailableClasses();

  // Create student mutation
  const { mutate: createStudent, isPending } = useCreateStudent();

  /**
   * Handle photo upload
   */
  const handlePhotoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];

    if (file) {
      setValue('photo', file);

      // Create preview URL
      const reader = new FileReader();
      reader.onloadend = () => {
        setPhotoPreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  /**
   * Remove photo
   */
  const handleRemovePhoto = () => {
    setValue('photo', null);
    setPhotoPreview(null);
  };

  /**
   * Handle form submission
   */
  const onSubmit = (data: StudentRegistrationFormData) => {
    createStudent(data, {
      onSuccess: () => {
        navigate(ROUTES.STUDENTS);
      },
    });
  };

  return (
    <div className="container mx-auto px-4 py-8 max-w-4xl">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Student Registration</h1>
        <p className="text-gray-600 mt-1">Register a new student in the system</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)}>
        {/* Personal Information */}
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Personal Information</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Input
                id="firstName"
                label="First Name"
                placeholder="Enter first name"
                {...register('firstName')}
                error={errors.firstName?.message}
                required
              />

              <Input
                id="lastName"
                label="Last Name"
                placeholder="Enter last name"
                {...register('lastName')}
                error={errors.lastName?.message}
                required
              />

              <Input
                id="dateOfBirth"
                label="Date of Birth"
                type="date"
                {...register('dateOfBirth')}
                error={errors.dateOfBirth?.message}
                required
                helperText="Student must be between 3-18 years old"
              />

              <Select
                id="gender"
                label="Gender"
                {...register('gender')}
                error={errors.gender?.message}
                required
              >
                <option value="">Select gender</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </Select>

              <Select
                id="bloodGroup"
                label="Blood Group"
                {...register('bloodGroup')}
                error={errors.bloodGroup?.message}
              >
                <option value="">Select blood group (optional)</option>
                <option value="A+">A+</option>
                <option value="A-">A-</option>
                <option value="B+">B+</option>
                <option value="B-">B-</option>
                <option value="AB+">AB+</option>
                <option value="AB-">AB-</option>
                <option value="O+">O+</option>
                <option value="O-">O-</option>
              </Select>

              {/* Photo Upload */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Student Photo
                </label>
                <div className="flex items-start gap-4">
                  {photoPreview ? (
                    <div className="relative">
                      <img
                        src={photoPreview}
                        alt="Student preview"
                        className="w-24 h-24 rounded-lg object-cover border-2 border-gray-200"
                      />
                      <button
                        type="button"
                        onClick={handleRemovePhoto}
                        className="absolute -top-2 -right-2 bg-red-500 text-white rounded-full p-1 hover:bg-red-600"
                        aria-label="Remove photo"
                      >
                        <X className="w-4 h-4" />
                      </button>
                    </div>
                  ) : (
                    <label className="w-24 h-24 flex flex-col items-center justify-center border-2 border-dashed border-gray-300 rounded-lg cursor-pointer hover:border-blue-500 transition-colors">
                      <Upload className="w-6 h-6 text-gray-400" />
                      <span className="text-xs text-gray-500 mt-1">Upload</span>
                      <input
                        type="file"
                        accept="image/jpeg,image/jpg,image/png,image/webp"
                        onChange={handlePhotoChange}
                        className="hidden"
                        aria-label="Upload student photo"
                      />
                    </label>
                  )}
                  <p className="text-sm text-gray-500">
                    Max 5MB. Formats: JPEG, PNG, WebP
                  </p>
                </div>
                {errors.photo?.message && (
                  <p className="mt-1 text-sm text-red-500">{errors.photo.message}</p>
                )}
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Contact Information */}
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Contact Information</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Input
                id="email"
                label="Email"
                type="email"
                placeholder="student@example.com"
                {...register('email')}
                error={errors.email?.message}
              />

              <Input
                id="phone"
                label="Mobile Number"
                type="tel"
                placeholder="1234567890"
                {...register('phone')}
                error={errors.phone?.message}
                helperText="10-digit mobile number"
              />

              <Input
                id="street"
                label="Street Address"
                placeholder="House/Flat No, Street Name"
                {...register('address.street')}
                error={errors.address?.street?.message}
                required
                className="md:col-span-2"
              />

              <Input
                id="city"
                label="City"
                placeholder="Enter city"
                {...register('address.city')}
                error={errors.address?.city?.message}
                required
              />

              <Input
                id="state"
                label="State"
                placeholder="Enter state"
                {...register('address.state')}
                error={errors.address?.state?.message}
                required
              />

              <Input
                id="postalCode"
                label="Postal Code"
                placeholder="123456"
                {...register('address.postalCode')}
                error={errors.address?.postalCode?.message}
                required
                helperText="6-digit PIN code"
              />

              <Input
                id="country"
                label="Country"
                placeholder="Enter country"
                {...register('address.country')}
                error={errors.address?.country?.message}
                required
              />
            </div>
          </CardContent>
        </Card>

        {/* Guardian Information */}
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Guardian Information</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Input
                id="guardianName"
                label="Guardian Name"
                placeholder="Enter guardian name"
                {...register('guardian.name')}
                error={errors.guardian?.name?.message}
                required
              />

              <Select
                id="relationship"
                label="Relationship"
                {...register('guardian.relationship')}
                error={errors.guardian?.relationship?.message}
                required
              >
                <option value="">Select relationship</option>
                <option value="Father">Father</option>
                <option value="Mother">Mother</option>
                <option value="Guardian">Guardian</option>
                <option value="Other">Other</option>
              </Select>

              <Input
                id="guardianPhone"
                label="Guardian Mobile Number"
                type="tel"
                placeholder="1234567890"
                {...register('guardian.phone')}
                error={errors.guardian?.phone?.message}
                required
                helperText="10-digit mobile number"
              />

              <Input
                id="guardianEmail"
                label="Guardian Email"
                type="email"
                placeholder="guardian@example.com"
                {...register('guardian.email')}
                error={errors.guardian?.email?.message}
              />

              <Input
                id="occupation"
                label="Occupation"
                placeholder="Enter occupation"
                {...register('guardian.occupation')}
                error={errors.guardian?.occupation?.message}
              />
            </div>
          </CardContent>
        </Card>

        {/* Academic Information */}
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Academic Information</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Select
                id="classId"
                label="Class"
                {...register('academic.classId', { valueAsNumber: true })}
                error={errors.academic?.classId?.message}
                required
                disabled={classesLoading}
              >
                <option value="">Select class</option>
                {classes?.map((cls) => (
                  <option key={cls.id} value={cls.id}>
                    {cls.name} {cls.section} ({cls.currentEnrollment}/{cls.capacity})
                  </option>
                ))}
              </Select>

              <Input
                id="rollNumber"
                label="Roll Number"
                placeholder="Enter roll number"
                {...register('academic.rollNumber')}
                error={errors.academic?.rollNumber?.message}
              />

              <Input
                id="admissionDate"
                label="Admission Date"
                type="date"
                {...register('academic.admissionDate')}
                error={errors.academic?.admissionDate?.message}
                required
              />

              <Input
                id="previousSchool"
                label="Previous School"
                placeholder="Enter previous school name"
                {...register('academic.previousSchool')}
                error={errors.academic?.previousSchool?.message}
              />
            </div>
          </CardContent>
        </Card>

        {/* Form Actions */}
        <div className="flex justify-end gap-4">
          <Button
            type="button"
            variant="outline"
            onClick={() => navigate(ROUTES.STUDENTS)}
            disabled={isPending}
          >
            Cancel
          </Button>
          <Button type="submit" isLoading={isPending}>
            Register Student
          </Button>
        </div>
      </form>
    </div>
  );
};
