import React from 'react';
import { Link } from 'react-router-dom';
import { type Student } from '@/types/student.types';
import StatusBadge from './StatusBadge';
import Button from '@/components/common/Button';

interface StudentCardProps {
  student: Student;
  onEdit?: (id: number) => void;
  onDelete?: (id: number) => void;
}

const StudentCard: React.FC<StudentCardProps> = ({ student, onEdit, onDelete }) => {
  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg transition-shadow p-6">
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
            {student.firstName} {student.lastName}
          </h3>
          <p className="text-sm text-gray-600 dark:text-gray-400">ID: {student.studentId}</p>
        </div>
        <StatusBadge status={student.status} />
      </div>

      <div className="space-y-2 mb-4">
        <div className="flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300">
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
            />
          </svg>
          <span>{student.mobile}</span>
        </div>

        <div className="flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300">
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
            />
          </svg>
          <span>Age: {student.currentAge} years</span>
        </div>

        {student.email && (
          <div className="flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300">
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
              />
            </svg>
            <span>{student.email}</span>
          </div>
        )}
      </div>

      <div className="flex gap-2 pt-4 border-t border-gray-200 dark:border-gray-700">
        <Link to={`/students/${student.id}`} className="flex-1">
          <Button variant="secondary" className="w-full">
            View Details
          </Button>
        </Link>
        {onEdit && (
          <Button variant="primary" onClick={() => onEdit(student.id)} className="flex-1">
            Edit
          </Button>
        )}
        {onDelete && (
          <Button variant="danger" onClick={() => onDelete(student.id)}>
            Delete
          </Button>
        )}
      </div>
    </div>
  );
};

export default StudentCard;
