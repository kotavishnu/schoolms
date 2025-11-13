import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { ArrowLeft, Save } from 'lucide-react';
import { useCreateClass, useAcademicYears } from '../hooks/useClasses';
import { classFormSchema } from '../schemas/class.schema';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Loading } from '@/shared/components/feedback/Loading';
import { ROUTES } from '@/shared/constants/routes';
import type { ClassFormData, Section } from '../types/class.types';
import { CLASS_NAME_DISPLAY } from '../types/class.types';

/**
 * ClassCreate Page Component
 *
 * Provides a form for creating a new class with the following features:
 * - Class name (grade level) selection
 * - Section selection
 * - Academic year selection
 * - Maximum capacity input (1-100 students)
 * - Optional room number
 * - Optional class teacher selection
 * - Form validation with Zod
 * - Success/error notifications
 */
export function ClassCreate() {
  const navigate = useNavigate();
  const createMutation = useCreateClass();
  const { data: academicYears, isLoading: isLoadingYears } = useAcademicYears();

  // Find current academic year for default selection
  const currentYear = academicYears?.find((year) => year.isCurrent);

  // Form setup with React Hook Form and Zod validation
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting, isDirty },
  } = useForm<ClassFormData>({
    resolver: zodResolver(classFormSchema) as any,
    defaultValues: {
      className: 'CLASS_1',
      section: 'A',
      academicYearId: currentYear?.academicYearId,
      maxCapacity: 40,
      isActive: true,
    },
  });

  /**
   * Handle form submission
   */
  const onSubmit = async (data: ClassFormData) => {
    try {
      const response = await createMutation.mutateAsync(data);
      // Navigate to class details page after successful creation
      navigate(ROUTES.CLASS_DETAILS.replace(':classId', response.class.classId.toString()));
    } catch (error) {
      // Error handling is done in the mutation hook with toast
      console.error('Failed to create class:', error);
    }
  };

  /**
   * Handle cancel with unsaved changes confirmation
   */
  const handleCancel = () => {
    if (isDirty) {
      const confirmed = window.confirm(
        'You have unsaved changes. Are you sure you want to leave?'
      );
      if (!confirmed) return;
    }
    navigate(ROUTES.CLASSES);
  };

  // Show loading state while fetching academic years
  if (isLoadingYears) {
    return <Loading message="Loading form data..." />;
  }

  // Available sections
  const sections: Section[] = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Button
          variant="secondary"
          size="sm"
          onClick={handleCancel}
          aria-label="Go back to classes list"
        >
          <ArrowLeft className="h-5 w-5" aria-hidden="true" />
        </Button>
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Create New Class</h1>
          <p className="mt-1 text-sm text-gray-500">
            Define a new class section for the academic year
          </p>
        </div>
      </div>

      {/* Form */}
      <form onSubmit={handleSubmit(onSubmit as any)}>
        <Card className="p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Class Information</h2>

          <div className="space-y-6">
            {/* Row 1: Class Name and Section */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Class Name (Grade Level) */}
              <div>
                <label
                  htmlFor="className"
                  className="block text-sm font-medium text-gray-700 mb-2"
                >
                  Grade Level <span className="text-red-500">*</span>
                </label>
                <select
                  id="className"
                  {...register('className')}
                  className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent ${
                    errors.className ? 'border-red-500' : 'border-gray-300'
                  }`}
                  aria-required="true"
                  aria-invalid={!!errors.className}
                  aria-describedby={errors.className ? 'className-error' : undefined}
                >
                  {Object.entries(CLASS_NAME_DISPLAY).map(([key, value]) => (
                    <option key={key} value={key}>
                      {value}
                    </option>
                  ))}
                </select>
                {errors.className && (
                  <p id="className-error" className="mt-1 text-sm text-red-600" role="alert">
                    {errors.className.message}
                  </p>
                )}
              </div>

              {/* Section */}
              <div>
                <label
                  htmlFor="section"
                  className="block text-sm font-medium text-gray-700 mb-2"
                >
                  Section <span className="text-red-500">*</span>
                </label>
                <select
                  id="section"
                  {...register('section')}
                  className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent ${
                    errors.section ? 'border-red-500' : 'border-gray-300'
                  }`}
                  aria-required="true"
                  aria-invalid={!!errors.section}
                  aria-describedby={errors.section ? 'section-error' : undefined}
                >
                  {sections.map((section) => (
                    <option key={section} value={section}>
                      Section {section}
                    </option>
                  ))}
                </select>
                {errors.section && (
                  <p id="section-error" className="mt-1 text-sm text-red-600" role="alert">
                    {errors.section.message}
                  </p>
                )}
              </div>
            </div>

            {/* Row 2: Academic Year and Max Capacity */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Academic Year */}
              <div>
                <label
                  htmlFor="academicYearId"
                  className="block text-sm font-medium text-gray-700 mb-2"
                >
                  Academic Year <span className="text-red-500">*</span>
                </label>
                <select
                  id="academicYearId"
                  {...register('academicYearId', { valueAsNumber: true })}
                  className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent ${
                    errors.academicYearId ? 'border-red-500' : 'border-gray-300'
                  }`}
                  aria-required="true"
                  aria-invalid={!!errors.academicYearId}
                  aria-describedby={errors.academicYearId ? 'academicYearId-error' : undefined}
                >
                  <option value="">Select Academic Year</option>
                  {academicYears?.map((year) => (
                    <option key={year.academicYearId} value={year.academicYearId}>
                      {year.yearCode} {year.isCurrent && '(Current)'}
                    </option>
                  ))}
                </select>
                {errors.academicYearId && (
                  <p id="academicYearId-error" className="mt-1 text-sm text-red-600" role="alert">
                    {errors.academicYearId.message}
                  </p>
                )}
              </div>

              {/* Maximum Capacity */}
              <div>
                <label
                  htmlFor="maxCapacity"
                  className="block text-sm font-medium text-gray-700 mb-2"
                >
                  Maximum Capacity <span className="text-red-500">*</span>
                </label>
                <input
                  id="maxCapacity"
                  type="number"
                  min="1"
                  max="100"
                  {...register('maxCapacity', { valueAsNumber: true })}
                  className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent ${
                    errors.maxCapacity ? 'border-red-500' : 'border-gray-300'
                  }`}
                  placeholder="40"
                  aria-required="true"
                  aria-invalid={!!errors.maxCapacity}
                  aria-describedby={
                    errors.maxCapacity ? 'maxCapacity-error' : 'maxCapacity-help'
                  }
                />
                {errors.maxCapacity ? (
                  <p id="maxCapacity-error" className="mt-1 text-sm text-red-600" role="alert">
                    {errors.maxCapacity.message}
                  </p>
                ) : (
                  <p id="maxCapacity-help" className="mt-1 text-sm text-gray-500">
                    Maximum number of students allowed (1-100)
                  </p>
                )}
              </div>
            </div>

            {/* Row 3: Room Number (Optional) */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Room Number */}
              <div>
                <label
                  htmlFor="roomNumber"
                  className="block text-sm font-medium text-gray-700 mb-2"
                >
                  Room Number
                </label>
                <input
                  id="roomNumber"
                  type="text"
                  {...register('roomNumber')}
                  className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent ${
                    errors.roomNumber ? 'border-red-500' : 'border-gray-300'
                  }`}
                  placeholder="e.g., 101, A-Block-201"
                  maxLength={20}
                  aria-invalid={!!errors.roomNumber}
                  aria-describedby={errors.roomNumber ? 'roomNumber-error' : 'roomNumber-help'}
                />
                {errors.roomNumber ? (
                  <p id="roomNumber-error" className="mt-1 text-sm text-red-600" role="alert">
                    {errors.roomNumber.message}
                  </p>
                ) : (
                  <p id="roomNumber-help" className="mt-1 text-sm text-gray-500">
                    Optional classroom or room identifier
                  </p>
                )}
              </div>

              {/* Active Status */}
              <div>
                <label
                  htmlFor="isActive"
                  className="block text-sm font-medium text-gray-700 mb-2"
                >
                  Status
                </label>
                <select
                  id="isActive"
                  {...register('isActive', {
                    setValueAs: (value) => value === 'true',
                  })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                  aria-describedby="isActive-help"
                >
                  <option value="true">Active</option>
                  <option value="false">Inactive</option>
                </select>
                <p id="isActive-help" className="mt-1 text-sm text-gray-500">
                  Active classes are visible for enrollment
                </p>
              </div>
            </div>

            {/* Note about Class Teacher */}
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <p className="text-sm text-blue-800">
                <strong>Note:</strong> Class teacher assignment will be available after creating the
                class. You can assign a teacher from the class details page.
              </p>
            </div>
          </div>
        </Card>

        {/* Form Actions */}
        <div className="flex items-center justify-end gap-4 mt-6">
          <Button
            type="button"
            variant="secondary"
            onClick={handleCancel}
            disabled={isSubmitting}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            disabled={isSubmitting}
            isLoading={isSubmitting}
            className="flex items-center gap-2"
          >
            <Save className="h-5 w-5" aria-hidden="true" />
            {isSubmitting ? 'Creating...' : 'Create Class'}
          </Button>
        </div>
      </form>
    </div>
  );
}

export default ClassCreate;
