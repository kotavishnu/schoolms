import { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { ArrowLeft, Save } from 'lucide-react';
import { useClass, useUpdateClass, useAcademicYears } from '../hooks/useClasses';
import { classFormSchema } from '../schemas/class.schema';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Loading } from '@/shared/components/feedback/Loading';
import { ErrorMessage } from '@/shared/components/feedback/ErrorMessage';
import { ROUTES } from '@/shared/constants/routes';
import type { ClassFormData } from '../types/class.types';
import { CLASS_NAME_DISPLAY } from '../types/class.types';

/**
 * ClassEdit Page Component
 *
 * Provides a form for editing an existing class with:
 * - Pre-populated form fields
 * - Class name (grade level) selection
 * - Section selection
 * - Academic year selection
 * - Maximum capacity input
 * - Room number input
 * - Active status toggle
 * - Form validation with Zod
 */
export function ClassEdit() {
  const { classId } = useParams<{ classId: string }>();
  const navigate = useNavigate();
  const id = classId ? parseInt(classId, 10) : 0;

  const { data: classData, isLoading: isLoadingClass, isError, error } = useClass(id);
  const updateMutation = useUpdateClass();
  const { data: academicYears, isLoading: isLoadingYears } = useAcademicYears();

  // Form setup with React Hook Form and Zod validation
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting, isDirty },
    reset,
  } = useForm<ClassFormData>({
    resolver: zodResolver(classFormSchema) as any,
  });

  // Populate form when class data is loaded
  useEffect(() => {
    if (classData) {
      const classItem = classData as any;
      reset({
        className: classItem.className,
        section: classItem.section,
        academicYearId: classItem.academicYear.academicYearId,
        maxCapacity: classItem.maxCapacity,
        roomNumber: classItem.roomNumber || '',
        classTeacherId: classItem.classTeacher?.userId,
        isActive: classItem.isActive,
      });
    }
  }, [classData, reset]);

  /**
   * Handle form submission
   */
  const onSubmit = async (data: ClassFormData) => {
    try {
      await updateMutation.mutateAsync({ classId: id, data });
      navigate(ROUTES.CLASS_DETAILS.replace(':classId', id.toString()));
    } catch (error) {
      // Error is handled by the mutation
      console.error('Failed to update class:', error);
    }
  };

  if (isLoadingClass || isLoadingYears) {
    return <Loading message="Loading class details..." />;
  }

  if (isError || !classData) {
    return (
      <ErrorMessage
        message={error instanceof Error ? error.message : 'Failed to load class details'}
      />
    );
  }

  const sections = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Button
          variant="ghost"
          onClick={() => navigate(ROUTES.CLASS_DETAILS.replace(':classId', id.toString()))}
          className="flex items-center gap-2"
        >
          <ArrowLeft className="h-5 w-5" />
          Back
        </Button>
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Edit Class</h1>
          <p className="mt-1 text-sm text-gray-500">
            Update class information and settings
          </p>
        </div>
      </div>

      {/* Form */}
      <form onSubmit={handleSubmit(onSubmit as any)} className="space-y-6">
        <Card className="p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-6">Class Information</h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Class Name (Grade Level) */}
            <div>
              <label htmlFor="className" className="block text-sm font-medium text-gray-700 mb-1">
                Class Name (Grade) <span className="text-red-500">*</span>
              </label>
              <select
                id="className"
                {...register('className')}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                {Object.entries(CLASS_NAME_DISPLAY).map(([key, value]) => (
                  <option key={key} value={key}>
                    {value}
                  </option>
                ))}
              </select>
              {errors.className && (
                <p className="mt-1 text-sm text-red-500">{errors.className.message}</p>
              )}
            </div>

            {/* Section */}
            <div>
              <label htmlFor="section" className="block text-sm font-medium text-gray-700 mb-1">
                Section <span className="text-red-500">*</span>
              </label>
              <select
                id="section"
                {...register('section')}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                {sections.map((section) => (
                  <option key={section} value={section}>
                    Section {section}
                  </option>
                ))}
              </select>
              {errors.section && (
                <p className="mt-1 text-sm text-red-500">{errors.section.message}</p>
              )}
            </div>

            {/* Academic Year */}
            <div>
              <label htmlFor="academicYearId" className="block text-sm font-medium text-gray-700 mb-1">
                Academic Year <span className="text-red-500">*</span>
              </label>
              <select
                id="academicYearId"
                {...register('academicYearId', { valueAsNumber: true })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                {academicYears?.map((year) => (
                  <option key={year.academicYearId} value={year.academicYearId}>
                    {year.yearCode} {year.isCurrent && '(Current)'}
                  </option>
                ))}
              </select>
              {errors.academicYearId && (
                <p className="mt-1 text-sm text-red-500">{errors.academicYearId.message}</p>
              )}
            </div>

            {/* Maximum Capacity */}
            <div>
              <label htmlFor="maxCapacity" className="block text-sm font-medium text-gray-700 mb-1">
                Maximum Capacity <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                id="maxCapacity"
                {...register('maxCapacity', { valueAsNumber: true })}
                min={1}
                max={100}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="e.g., 40"
              />
              {errors.maxCapacity && (
                <p className="mt-1 text-sm text-red-500">{errors.maxCapacity.message}</p>
              )}
              <p className="mt-1 text-sm text-gray-500">Maximum number of students (1-100)</p>
            </div>

            {/* Room Number */}
            <div>
              <label htmlFor="roomNumber" className="block text-sm font-medium text-gray-700 mb-1">
                Room Number
              </label>
              <input
                type="text"
                id="roomNumber"
                {...register('roomNumber')}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="e.g., A-101"
              />
              {errors.roomNumber && (
                <p className="mt-1 text-sm text-red-500">{errors.roomNumber.message}</p>
              )}
            </div>

            {/* Active Status */}
            <div className="flex items-center">
              <input
                type="checkbox"
                id="isActive"
                {...register('isActive')}
                className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
              <label htmlFor="isActive" className="ml-2 text-sm font-medium text-gray-700">
                Active
              </label>
            </div>
          </div>
        </Card>

        {/* Form Actions */}
        <div className="flex items-center justify-end gap-4">
          <Button
            type="button"
            variant="ghost"
            onClick={() => navigate(ROUTES.CLASS_DETAILS.replace(':classId', id.toString()))}
            disabled={isSubmitting}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            disabled={isSubmitting || !isDirty}
            className="flex items-center gap-2"
          >
            <Save className="h-5 w-5" />
            {isSubmitting ? 'Saving...' : 'Save Changes'}
          </Button>
        </div>
      </form>
    </div>
  );
}

export default ClassEdit;
