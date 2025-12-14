import React from 'react';
import { useNavigate } from 'react-router-dom';
import { type Student } from '@/types/student.types';
import { formatDate } from '@/utils/dateUtils';
import { formatPhoneNumber } from '@/utils/formatters';
import StatusBadge from './StatusBadge';
import Button from '@/components/common/Button';

interface StudentDetailProps {
  student: Student;
  onDelete?: () => void;
}

const StudentDetail: React.FC<StudentDetailProps> = ({ student, onDelete }) => {
  const navigate = useNavigate();

  const InfoRow: React.FC<{ label: string; value: string | number | undefined }> = ({
    label,
    value,
  }) => (
    <div className="py-3 grid grid-cols-3 gap-4 border-b border-gray-200 dark:border-gray-700">
      <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">{label}</dt>
      <dd className="text-sm text-gray-900 dark:text-gray-100 col-span-2">
        {value || 'Not provided'}
      </dd>
    </div>
  );

  return (
    <div className="space-y-6">
      {/* Header Section */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">
              {student.firstName} {student.lastName}
            </h1>
            <p className="text-gray-600 dark:text-gray-400 mt-1">
              Student ID: {student.studentId}
            </p>
          </div>
          <div className="flex items-center gap-3">
            <StatusBadge status={student.status} />
          </div>
        </div>

        <div className="mt-6 flex flex-wrap gap-3">
          <Button variant="primary" onClick={() => navigate(`/students/${student.id}/edit`)}>
            Edit Student
          </Button>
          {onDelete && (
            <Button variant="danger" onClick={onDelete}>
              Delete Student
            </Button>
          )}
          <Button variant="secondary" onClick={() => navigate('/students')}>
            Back to List
          </Button>
        </div>
      </div>

      {/* Personal Information */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
          Personal Information
        </h2>
        <dl className="divide-y divide-gray-200 dark:divide-gray-700">
          <InfoRow label="Full Name" value={`${student.firstName} ${student.lastName}`} />
          <InfoRow label="Date of Birth" value={formatDate(student.dateOfBirth)} />
          <InfoRow label="Age" value={`${student.currentAge} years`} />
          <InfoRow label="Mobile Number" value={formatPhoneNumber(student.mobile)} />
          <InfoRow label="Email" value={student.email} />
          <InfoRow label="Address" value={student.address} />
          <InfoRow label="Identification Mark" value={student.identificationMark} />
          <InfoRow label="Aadhaar Number" value={student.aadhaarNumber} />
        </dl>
      </div>

      {/* Guardian Information */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
          Guardian Information
        </h2>
        <dl className="divide-y divide-gray-200 dark:divide-gray-700">
          <InfoRow label="Father's Name / Guardian" value={student.fatherName} />
          <InfoRow label="Mother's Name" value={student.motherName} />
        </dl>
      </div>

      {/* System Information */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
        <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
          System Information
        </h2>
        <dl className="divide-y divide-gray-200 dark:divide-gray-700">
          <InfoRow label="Status" value={student.status} />
          <InfoRow label="Created At" value={formatDate(student.createdAt)} />
          <InfoRow label="Last Updated" value={formatDate(student.updatedAt)} />
          <InfoRow label="Version" value={student.version} />
        </dl>
      </div>
    </div>
  );
};

export default StudentDetail;
