'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { ArrowLeft } from 'lucide-react';
import Link from 'next/link';
import { studentService } from '@/services/studentService';
import { createStudentSchema, CreateStudentFormData } from '@/lib/validations';
import { LoadingSpinner } from '@/components/LoadingSpinner';
import { getErrorMessage, getFieldErrors } from '@/lib/api-client';
import toast from 'react-hot-toast';

export default function NewStudentPage() {
  const router = useRouter();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm<CreateStudentFormData>({
    resolver: zodResolver(createStudentSchema),
  });

  const onSubmit = async (data: CreateStudentFormData) => {
    setIsSubmitting(true);
    try {
      const student = await studentService.createStudent(data);
      toast.success('Student created successfully');
      router.push(`/students/${student.studentKey}`);
    } catch (err) {
      const fieldErrors = getFieldErrors(err);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => {
          setError(field as keyof CreateStudentFormData, { message });
        });
      } else {
        toast.error(getErrorMessage(err));
      }
      setIsSubmitting(false);
    }
  };

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div>
        <Link
          href="/students"
          className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900"
        >
          <ArrowLeft className="h-4 w-4" />
          Back to Students
        </Link>
        <h1 className="mt-2 text-3xl font-bold text-gray-900">Add New Student</h1>
        <p className="mt-1 text-sm text-gray-600">Register a new student in the system</p>
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

          <div>
            <label htmlFor="dateOfBirth" className="label">
              Date of Birth <span className="text-red-600">*</span>
            </label>
            <input
              type="date"
              id="dateOfBirth"
              {...register('dateOfBirth')}
              className="input-field"
              disabled={isSubmitting}
            />
            {errors.dateOfBirth && (
              <p className="error-text">{errors.dateOfBirth.message}</p>
            )}
            <p className="mt-1 text-xs text-gray-500">Student must be 3-18 years old</p>
          </div>

          <div>
            <label htmlFor="mobile" className="label">
              Mobile Number <span className="text-red-600">*</span>
            </label>
            <input
              type="tel"
              id="mobile"
              {...register('mobile')}
              placeholder="+919876543210"
              className="input-field"
              disabled={isSubmitting}
            />
            {errors.mobile && (
              <p className="error-text">{errors.mobile.message}</p>
            )}
            <p className="mt-1 text-xs text-gray-500">10-15 digits, optionally with +</p>
          </div>

          <div>
            <label htmlFor="email" className="label">
              Email
            </label>
            <input
              type="email"
              id="email"
              {...register('email')}
              placeholder="student@example.com"
              className="input-field"
              disabled={isSubmitting}
            />
            {errors.email && (
              <p className="error-text">{errors.email.message}</p>
            )}
          </div>

          <div>
            <label htmlFor="adhaarNumber" className="label">
              Adhaar Number
            </label>
            <input
              type="text"
              id="adhaarNumber"
              {...register('adhaarNumber')}
              placeholder="123456789012"
              className="input-field"
              disabled={isSubmitting}
            />
            {errors.adhaarNumber && (
              <p className="error-text">{errors.adhaarNumber.message}</p>
            )}
            <p className="mt-1 text-xs text-gray-500">Exactly 12 digits</p>
          </div>
        </div>

        <div>
          <label htmlFor="address" className="label">
            Address
          </label>
          <textarea
            id="address"
            {...register('address')}
            rows={3}
            className="input-field"
            disabled={isSubmitting}
          />
          {errors.address && (
            <p className="error-text">{errors.address.message}</p>
          )}
        </div>

        <div className="grid gap-6 md:grid-cols-2">
          <div>
            <label htmlFor="fatherNameOrGuardian" className="label">
              Father Name / Guardian
            </label>
            <input
              type="text"
              id="fatherNameOrGuardian"
              {...register('fatherNameOrGuardian')}
              className="input-field"
              disabled={isSubmitting}
            />
            {errors.fatherNameOrGuardian && (
              <p className="error-text">{errors.fatherNameOrGuardian.message}</p>
            )}
          </div>

          <div>
            <label htmlFor="motherName" className="label">
              Mother Name
            </label>
            <input
              type="text"
              id="motherName"
              {...register('motherName')}
              className="input-field"
              disabled={isSubmitting}
            />
            {errors.motherName && (
              <p className="error-text">{errors.motherName.message}</p>
            )}
          </div>
        </div>

        <div>
          <label htmlFor="identificationMark" className="label">
            Identification Mark
          </label>
          <input
            type="text"
            id="identificationMark"
            {...register('identificationMark')}
            className="input-field"
            disabled={isSubmitting}
          />
          {errors.identificationMark && (
            <p className="error-text">{errors.identificationMark.message}</p>
          )}
        </div>

        <div className="flex justify-end gap-3 border-t pt-6">
          <Link href="/students" className="btn-secondary">
            Cancel
          </Link>
          <button
            type="submit"
            disabled={isSubmitting}
            className="btn-primary flex items-center gap-2"
          >
            {isSubmitting && <LoadingSpinner size="sm" />}
            {isSubmitting ? 'Creating...' : 'Create Student'}
          </button>
        </div>
      </form>
    </div>
  );
}
