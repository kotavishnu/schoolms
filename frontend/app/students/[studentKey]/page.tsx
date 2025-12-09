'use client';

import React from 'react';
import { useParams, useRouter } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft, Edit, Trash2, User, Phone, Mail, MapPin, Calendar, CreditCard } from 'lucide-react';
import Link from 'next/link';
import { studentService } from '@/services/studentService';
import { PageLoadingSpinner } from '@/components/LoadingSpinner';
import { ErrorMessage } from '@/components/ErrorMessage';
import { ConfirmDialog } from '@/components/ConfirmDialog';
import { formatDate, getStatusColor, calculateAge } from '@/utils/format';
import { getErrorMessage } from '@/lib/api-client';
import toast from 'react-hot-toast';

export default function StudentDetailPage() {
  const params = useParams();
  const router = useRouter();
  const studentKey = params.studentKey as string;
  const [showDeleteDialog, setShowDeleteDialog] = React.useState(false);
  const [isDeleting, setIsDeleting] = React.useState(false);

  const { data: student, isLoading, error, refetch } = useQuery({
    queryKey: ['student', studentKey],
    queryFn: () => studentService.getStudent(studentKey),
  });

  const handleDelete = async () => {
    setIsDeleting(true);
    try {
      await studentService.deleteStudent(studentKey);
      toast.success('Student deleted successfully');
      router.push('/students');
    } catch (err) {
      toast.error(getErrorMessage(err));
      setIsDeleting(false);
    }
  };

  if (isLoading) {
    return <PageLoadingSpinner />;
  }

  if (error) {
    return (
      <div className="mx-auto max-w-3xl">
        <ErrorMessage message={getErrorMessage(error)} onRetry={() => refetch()} />
      </div>
    );
  }

  if (!student) {
    return null;
  }

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <Link
            href="/students"
            className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900"
          >
            <ArrowLeft className="h-4 w-4" />
            Back to Students
          </Link>
          <h1 className="mt-2 text-3xl font-bold text-gray-900">
            {student.firstName} {student.lastName}
          </h1>
          <p className="mt-1 text-sm text-gray-600">Student ID: {student.studentKey}</p>
        </div>
        <div className="flex gap-2">
          <Link href={`/students/${studentKey}/edit`} className="btn-secondary flex items-center gap-2">
            <Edit className="h-4 w-4" />
            Edit
          </Link>
          <button
            onClick={() => setShowDeleteDialog(true)}
            className="btn-danger flex items-center gap-2"
          >
            <Trash2 className="h-4 w-4" />
            Delete
          </button>
        </div>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        <div className="card">
          <div className="mb-4 flex items-center gap-2">
            <User className="h-5 w-5 text-gray-400" />
            <h2 className="text-lg font-semibold text-gray-900">Personal Information</h2>
          </div>
          <dl className="space-y-3">
            <div>
              <dt className="text-sm font-medium text-gray-500">Full Name</dt>
              <dd className="mt-1 text-sm text-gray-900">
                {student.firstName} {student.lastName}
              </dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Date of Birth</dt>
              <dd className="mt-1 text-sm text-gray-900">
                {formatDate(student.dateOfBirth)} ({calculateAge(student.dateOfBirth)} years old)
              </dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Status</dt>
              <dd className="mt-1">
                <span
                  className={`inline-flex rounded-full px-2 py-1 text-xs font-semibold ${getStatusColor(
                    student.status
                  )}`}
                >
                  {student.status}
                </span>
              </dd>
            </div>
            {student.identificationMark && (
              <div>
                <dt className="text-sm font-medium text-gray-500">Identification Mark</dt>
                <dd className="mt-1 text-sm text-gray-900">{student.identificationMark}</dd>
              </div>
            )}
          </dl>
        </div>

        <div className="card">
          <div className="mb-4 flex items-center gap-2">
            <Phone className="h-5 w-5 text-gray-400" />
            <h2 className="text-lg font-semibold text-gray-900">Contact Information</h2>
          </div>
          <dl className="space-y-3">
            <div>
              <dt className="text-sm font-medium text-gray-500">Mobile Number</dt>
              <dd className="mt-1 text-sm text-gray-900">{student.mobile}</dd>
            </div>
            {student.email && (
              <div>
                <dt className="text-sm font-medium text-gray-500">Email</dt>
                <dd className="mt-1 text-sm text-gray-900">{student.email}</dd>
              </div>
            )}
            {student.address && (
              <div>
                <dt className="text-sm font-medium text-gray-500">Address</dt>
                <dd className="mt-1 text-sm text-gray-900">{student.address}</dd>
              </div>
            )}
          </dl>
        </div>

        <div className="card">
          <div className="mb-4 flex items-center gap-2">
            <User className="h-5 w-5 text-gray-400" />
            <h2 className="text-lg font-semibold text-gray-900">Family Information</h2>
          </div>
          <dl className="space-y-3">
            {student.fatherNameOrGuardian && (
              <div>
                <dt className="text-sm font-medium text-gray-500">Father/Guardian Name</dt>
                <dd className="mt-1 text-sm text-gray-900">{student.fatherNameOrGuardian}</dd>
              </div>
            )}
            {student.motherName && (
              <div>
                <dt className="text-sm font-medium text-gray-500">Mother Name</dt>
                <dd className="mt-1 text-sm text-gray-900">{student.motherName}</dd>
              </div>
            )}
          </dl>
        </div>

        <div className="card">
          <div className="mb-4 flex items-center gap-2">
            <CreditCard className="h-5 w-5 text-gray-400" />
            <h2 className="text-lg font-semibold text-gray-900">Additional Information</h2>
          </div>
          <dl className="space-y-3">
            {student.adhaarNumber && (
              <div>
                <dt className="text-sm font-medium text-gray-500">Adhaar Number</dt>
                <dd className="mt-1 text-sm text-gray-900">{student.adhaarNumber}</dd>
              </div>
            )}
            <div>
              <dt className="text-sm font-medium text-gray-500">Created At</dt>
              <dd className="mt-1 text-sm text-gray-900">{formatDate(student.createdAt, 'PPpp')}</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Last Updated</dt>
              <dd className="mt-1 text-sm text-gray-900">{formatDate(student.updatedAt, 'PPpp')}</dd>
            </div>
          </dl>
        </div>
      </div>

      <ConfirmDialog
        isOpen={showDeleteDialog}
        onClose={() => setShowDeleteDialog(false)}
        onConfirm={handleDelete}
        title="Delete Student"
        message={`Are you sure you want to delete ${student.firstName} ${student.lastName}? This action cannot be undone.`}
        confirmText="Delete"
        variant="danger"
        isLoading={isDeleting}
      />
    </div>
  );
}
