import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Edit, Users, Calendar, BookOpen, UserCheck } from 'lucide-react';
import { useClass, useClassEnrollments } from '../hooks/useClasses';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';
import { Loading } from '@/shared/components/feedback/Loading';
import { ErrorMessage } from '@/shared/components/feedback/ErrorMessage';
import { Table } from '@/shared/components/ui/Table';
import { ROUTES } from '@/shared/constants/routes';
import { getFormattedClassName } from '../types/class.types';

/**
 * ClassDetails Page Component
 *
 * Displays detailed information about a specific class with tabs for:
 * - Overview: Class information, teacher, room, enrollment stats
 * - Students: List of enrolled students
 * - Timetable: Class schedule (placeholder)
 * - Attendance: Attendance tracking (placeholder)
 */
export function ClassDetails() {
  const { classId } = useParams<{ classId: string }>();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<'overview' | 'students' | 'timetable' | 'attendance'>('overview');

  const id = classId ? parseInt(classId, 10) : 0;
  const { data: classData, isLoading, isError, error, refetch } = useClass(id);
  const { data: enrollments, isLoading: isLoadingEnrollments } = useClassEnrollments(id);

  if (isLoading) {
    return <Loading message="Loading class details..." />;
  }

  if (isError || !classData) {
    return (
      <ErrorMessage
        message={error instanceof Error ? error.message : 'Failed to load class details'}
        onRetry={refetch}
      />
    );
  }

  const classItem = classData as any;
  const formattedName = getFormattedClassName(classItem.className, classItem.section);
  const utilization = classItem.maxCapacity > 0
    ? Math.round((classItem.currentEnrollment / classItem.maxCapacity) * 100)
    : 0;

  const tabs = [
    { id: 'overview', label: 'Overview', icon: BookOpen },
    { id: 'students', label: 'Students', icon: Users },
    { id: 'timetable', label: 'Timetable', icon: Calendar },
    { id: 'attendance', label: 'Attendance', icon: UserCheck },
  ] as const;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            onClick={() => navigate(ROUTES.CLASSES)}
            className="flex items-center gap-2"
            aria-label="Back to classes"
          >
            <ArrowLeft className="h-5 w-5" />
            Back
          </Button>
          <div>
            <h1 className="text-3xl font-bold text-gray-900">{formattedName}</h1>
            <p className="mt-1 text-sm text-gray-500">
              {classItem.academicYear.yearCode} {classItem.academicYear.isCurrent && '(Current)'}
            </p>
          </div>
          <Badge variant={classItem.isActive ? 'success' : 'default'}>
            {classItem.isActive ? 'Active' : 'Inactive'}
          </Badge>
        </div>
        <Button
          onClick={() => navigate(`/classes/${classItem.classId}/edit`)}
          className="flex items-center gap-2"
        >
          <Edit className="h-5 w-5" />
          Edit Class
        </Button>
      </div>

      {/* Tabs */}
      <Card className="p-0">
        <div className="border-b border-gray-200">
          <nav className="flex -mb-px">
            {tabs.map((tab) => {
              const Icon = tab.icon;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center gap-2 px-6 py-4 text-sm font-medium border-b-2 transition-colors ${
                    activeTab === tab.id
                      ? 'border-blue-600 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                  aria-current={activeTab === tab.id ? 'page' : undefined}
                >
                  <Icon className="h-5 w-5" />
                  {tab.label}
                </button>
              );
            })}
          </nav>
        </div>

        <div className="p-6">
          {/* Overview Tab */}
          {activeTab === 'overview' && (
            <div className="space-y-6">
              {/* Class Information */}
              <div>
                <h2 className="text-lg font-semibold text-gray-900 mb-4">Class Information</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-gray-500">Class Name</p>
                    <p className="text-base font-medium text-gray-900">{formattedName}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500">Academic Year</p>
                    <p className="text-base font-medium text-gray-900">{classItem.academicYear.yearCode}</p>
                  </div>
                  {classItem.roomNumber && (
                    <div>
                      <p className="text-sm text-gray-500">Room Number</p>
                      <p className="text-base font-medium text-gray-900">{classItem.roomNumber}</p>
                    </div>
                  )}
                  <div>
                    <p className="text-sm text-gray-500">Status</p>
                    <Badge variant={classItem.isActive ? 'success' : 'default'}>
                      {classItem.isActive ? 'Active' : 'Inactive'}
                    </Badge>
                  </div>
                </div>
              </div>

              {/* Class Teacher */}
              {classItem.classTeacher && (
                <div>
                  <h2 className="text-lg font-semibold text-gray-900 mb-4">Class Teacher</h2>
                  <Card className="p-4 bg-gray-50">
                    <p className="text-base font-medium text-gray-900">
                      {classItem.classTeacher.firstName} {classItem.classTeacher.lastName}
                    </p>
                    {classItem.classTeacher.email && (
                      <p className="text-sm text-gray-600 mt-1">{classItem.classTeacher.email}</p>
                    )}
                  </Card>
                </div>
              )}

              {/* Enrollment Statistics */}
              <div>
                <h2 className="text-lg font-semibold text-gray-900 mb-4">Enrollment Statistics</h2>
                <Card className="p-6 bg-gradient-to-br from-blue-50 to-indigo-50">
                  <div className="flex items-center justify-between mb-4">
                    <div>
                      <p className="text-sm text-gray-600">Current Enrollment</p>
                      <p className="text-3xl font-bold text-gray-900">
                        {classItem.currentEnrollment} / {classItem.maxCapacity}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="text-sm text-gray-600">Capacity</p>
                      <p className={`text-3xl font-bold ${
                        utilization >= 90 ? 'text-red-600' : utilization >= 75 ? 'text-yellow-600' : 'text-green-600'
                      }`}>
                        {utilization}%
                      </p>
                    </div>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-3">
                    <div
                      className={`h-3 rounded-full transition-all ${
                        utilization >= 90 ? 'bg-red-600' : utilization >= 75 ? 'bg-yellow-500' : 'bg-green-600'
                      }`}
                      style={{ width: `${Math.min(utilization, 100)}%` }}
                      role="progressbar"
                      aria-valuenow={utilization}
                      aria-valuemin={0}
                      aria-valuemax={100}
                    />
                  </div>
                  <p className="text-sm text-gray-600 mt-2">
                    {classItem.maxCapacity - classItem.currentEnrollment} seats available
                  </p>
                </Card>
              </div>
            </div>
          )}

          {/* Students Tab */}
          {activeTab === 'students' && (
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <h2 className="text-lg font-semibold text-gray-900">
                  Enrolled Students ({classItem.currentEnrollment})
                </h2>
                <Button variant="secondary" size="sm">
                  <Users className="h-4 w-4 mr-2" />
                  Manage Enrollment
                </Button>
              </div>

              {isLoadingEnrollments ? (
                <Loading message="Loading students..." />
              ) : enrollments && enrollments.length > 0 ? (
                <Table
                  data={enrollments}
                  columns={[
                    {
                      key: 'rollNumber',
                      header: 'Roll No',
                      width: '100px',
                    },
                    {
                      key: 'studentName',
                      header: 'Student Name',
                      render: (enrollment) => (
                        <button
                          onClick={() => navigate(`/students/${enrollment.studentId}`)}
                          className="text-blue-600 hover:underline"
                        >
                          {enrollment.studentName}
                        </button>
                      ),
                    },
                    {
                      key: 'enrollmentDate',
                      header: 'Enrollment Date',
                      render: (enrollment) => new Date(enrollment.enrollmentDate).toLocaleDateString(),
                    },
                    {
                      key: 'status',
                      header: 'Status',
                      render: (enrollment: any) => (
                        <Badge variant={(enrollment.isActive ?? true) ? 'success' : 'default'}>
                          {(enrollment.isActive ?? true) ? 'Active' : 'Inactive'}
                        </Badge>
                      ),
                    },
                  ]}
                />
              ) : (
                <div className="text-center py-12 text-gray-500">
                  <Users className="mx-auto h-12 w-12 text-gray-400 mb-2" />
                  <p className="text-lg font-medium">No students enrolled</p>
                  <p className="text-sm">Start enrolling students to this class</p>
                </div>
              )}
            </div>
          )}

          {/* Timetable Tab */}
          {activeTab === 'timetable' && (
            <div className="text-center py-12 text-gray-500">
              <Calendar className="mx-auto h-12 w-12 text-gray-400 mb-2" />
              <p className="text-lg font-medium">Timetable Coming Soon</p>
              <p className="text-sm">Class schedule and timetable management will be available here</p>
            </div>
          )}

          {/* Attendance Tab */}
          {activeTab === 'attendance' && (
            <div className="text-center py-12 text-gray-500">
              <UserCheck className="mx-auto h-12 w-12 text-gray-400 mb-2" />
              <p className="text-lg font-medium">Attendance Coming Soon</p>
              <p className="text-sm">Attendance tracking and reports will be available here</p>
            </div>
          )}
        </div>
      </Card>
    </div>
  );
}

export default ClassDetails;
