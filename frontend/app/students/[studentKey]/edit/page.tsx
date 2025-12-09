'use client';

import React, { useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { ArrowLeft } from 'lucide-react';
import Link from 'next/link';
import { studentService } from '@/services/studentService';
import { updateStudentSchema, UpdateStudentFormData } from '@/lib/validations';
import { LoadingSpinner, PageLoadingSpinner } from '@/components/LoadingSpinner';
import { ErrorMessage } from '@/components/ErrorMessage';
import { getErrorMessage, getFieldErrors } from '@/lib/api-client';
import toast from 'react-hot-toast';

export default function EditStudentPage() {
  const params = useParams();
  const router = useRouter();
  const studentKey = params.studentKey as string;
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { data: student, isLoading, error } = useQuery({
    queryKey: ['student', studentKey],
    queryFn: () => studentService.getStudent(studentKey),
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm<UpdateStudentFormData>({
    resolver: zodResolver(updateStudentSchema),
    values: student
      ? {
          firstName: student.firstName,
          lastName: student.lastName,
          mobile: student.mobile,
          status: student.status,
        }
      : undefined,
  });

  const onSubmit = async (data: UpdateStudentFormData) => {
    setIsSubmitting(true);
    try {
      await studentService.updateStudent(studentKey, data);
      toast.success('Student updated successfully');
      router.push(`/students/${studentKey}`);
    } catch (err) {
      const fieldErrors = getFieldErrors(err);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => {
          setError(field as keyof UpdateStudentFormData, { message });
        });
      } else {
        toast.error(getErrorMessage(err));
      }
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return <PageLoadingSpinner />;
  }

  if (error) {
    return (
      <div className="mx-auto max-w-3xl">
        <ErrorMessage message={getErrorMessage(error)} />
      </div>
    );
  }

  if (!student) {
    return null;
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div>
        <Link
          href={`/students/${studentKey}`}
          className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900"
        >
          <ArrowLeft className="h-4 w-4" />
          Back to Student Details
        </Link>
        <h1 className="mt-2 text-3xl font-bold text-gray-900">Edit Student</h1>
        <p className="mt-1 text-sm text-gray-600">
          Update student information (Name, Mobile, Status only)
        </p>
      </div>

      <div className="card bg-blue-50 border-blue-200">
        <p className="text-sm text-blue-800">
          <strong>Note:</strong> Only name, mobile number, and status can be edited. Other fields
          like date of birth and identification details cannot be changed after registration.
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="card space-y-6">
        <div className="grid gap-6 md:grid-cols-2">
          <div>
            <label htmlFor="firstName" className="label">
              First Name <span className="text-red-600">*</span>
            </label>
            <input
              type="text"
              id="firstName"
              {...register('firstName')}
              className="input-field"
              disabled={isSubmitting}
            />
            {errors.firstName && (
              <p className="error-text">{errors.firstName.message}</p>
            )}
          </div>

          <div>
            <label htmlFor="lastName" className="label">
              Last Name <span className="text-red-600">*</span>
            </label>
            <input
              type="text"
              id="lastName"
              {...register('lastName')}
              className="input-field"
              disabled={isSubmitting}
            />
            {errors.lastName && (
              <p className="error-text">{errors.lastName.message}</p>
            )}
          </div>
        </div>

        <div>
          <label htmlFor="mobile" className="label">
            Mobile Number <span className="text-red-600">*</span>
          </label>
          <input
            type="tel"
            id="mobile"
            {...register('mobile')}
            className="input-field"
            disabled={isSubmitting}
          />
          {errors.mobile && (
            <p className="error-text">{errors.mobile.message}</p>
          )}
        </div>

        <div>
          <label htmlFor="status" className="label">
            Status <span className="text-red-600">*</span>
          </label>
          <select
            id="status"
            {...register('status')}
            className="input-field"
            disabled={isSubmitting}
          >
            <option value="Active">Active</option>
            <option value="Inactive">Inactive</option>
          </select>
          {errors.status && (
            <p className="error-text">{errors.status.message}</p>
          )}
        </div>

        <div className="flex justify-end gap-3 border-t pt-6">
          <Link href={`/students/${studentKey}`} className="btn-secondary">
            Cancel
          </Link>
          <button
            type="submit"
            disabled={isSubmitting}
            className="btn-primary flex items-center gap-2"
          >
            {isSubmitting && <LoadingSpinner size="sm" />}
            {isSubmitting ? 'Updating...' : 'Update Student'}
          </button>
        </div>
      </form>
    </div>
  );
}
