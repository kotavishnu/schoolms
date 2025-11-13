import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Edit, Mail, Phone, MapPin, User, Calendar, ArrowLeft } from 'lucide-react';
import { format } from 'date-fns';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { useStudent } from '../hooks/useStudents';
import type { StudentStatus } from '../types/student.types';
import { ROUTES } from '@/shared/constants/routes';

type TabType = 'overview' | 'academic' | 'fees' | 'documents';

/**
 * Student Details Page Component
 *
 * Features:
 * - Display all student information in organized sections
 * - Tabs: Overview, Academic Records, Fee History, Documents
 * - Edit button to navigate to edit form
 * - Status badge
 * - Profile photo display
 * - Guardian contact information
 */
export const StudentDetails = () => {
  const navigate = useNavigate();
  const { studentId } = useParams<{ studentId: string }>();
  const [activeTab, setActiveTab] = useState<TabType>('overview');

  // Fetch student data
  const { data: student, isLoading, error } = useStudent(Number(studentId));

  /**
   * Get badge variant based on student status
   */
  const getStatusBadge = (status: StudentStatus) => {
    const variants: Record<StudentStatus, 'success' | 'error' | 'warning' | 'info'> = {
      Active: 'success',
      Inactive: 'warning',
      Graduated: 'info',
      Dropped: 'error',
    };

    return variants[status] || 'default';
  };

  /**
   * Tab navigation component
   */
  const TabButton = ({ tab, label }: { tab: TabType; label: string }) => (
    <button
      onClick={() => setActiveTab(tab)}
      className={`px-4 py-2 font-medium text-sm rounded-lg transition-colors ${
        activeTab === tab
          ? 'bg-blue-600 text-white'
          : 'text-gray-600 hover:bg-gray-100'
      }`}
      aria-current={activeTab === tab ? 'page' : undefined}
    >
      {label}
    </button>
  );

  // Loading state
  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-96">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600" />
      </div>
    );
  }

  // Error state
  if (error || !student) {
    return (
      <div className="text-center py-12">
        <p className="text-red-600">Failed to load student details. Please try again.</p>
        <Button onClick={() => navigate(ROUTES.STUDENTS)} className="mt-4">
          Back to Students
        </Button>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl">
      {/* Header */}
      <div className="mb-6">
        <Button
          variant="ghost"
          onClick={() => navigate(ROUTES.STUDENTS)}
          className="mb-4 flex items-center gap-2"
        >
          <ArrowLeft className="w-4 h-4" />
          Back to Students
        </Button>

        <div className="flex justify-between items-start">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">
              {student.firstName} {student.lastName}
            </h1>
            <p className="text-gray-600 mt-1">Student ID: {student.studentId}</p>
          </div>
          <div className="flex items-center gap-4">
            <Badge variant={getStatusBadge(student.status)}>{student.status}</Badge>
            <Button
              onClick={() =>
                navigate(ROUTES.STUDENT_EDIT.replace(':studentId', student.id.toString()))
              }
              className="flex items-center gap-2"
            >
              <Edit className="w-4 h-4" />
              Edit Student
            </Button>
          </div>
        </div>
      </div>

      {/* Profile Overview Card */}
      <Card className="mb-6">
        <CardContent className="pt-6">
          <div className="flex flex-col md:flex-row gap-6">
            {/* Profile Photo */}
            <div className="flex-shrink-0">
              {student.photoUrl ? (
                <img
                  src={student.photoUrl}
                  alt={`${student.firstName} ${student.lastName}`}
                  className="w-32 h-32 rounded-lg object-cover border-2 border-gray-200"
                />
              ) : (
                <div className="w-32 h-32 rounded-lg bg-gray-200 flex items-center justify-center">
                  <User className="w-16 h-16 text-gray-400" />
                </div>
              )}
            </div>

            {/* Quick Info */}
            <div className="flex-grow grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex items-start gap-3">
                <Calendar className="w-5 h-5 text-gray-400 mt-0.5" />
                <div>
                  <p className="text-sm text-gray-500">Date of Birth</p>
                  <p className="font-medium">
                    {format(new Date(student.dateOfBirth), 'MMMM dd, yyyy')}
                  </p>
                </div>
              </div>

              <div className="flex items-start gap-3">
                <User className="w-5 h-5 text-gray-400 mt-0.5" />
                <div>
                  <p className="text-sm text-gray-500">Gender</p>
                  <p className="font-medium">{student.gender}</p>
                </div>
              </div>

              {student.email && (
                <div className="flex items-start gap-3">
                  <Mail className="w-5 h-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-sm text-gray-500">Email</p>
                    <p className="font-medium">{student.email}</p>
                  </div>
                </div>
              )}

              {student.phone && (
                <div className="flex items-start gap-3">
                  <Phone className="w-5 h-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-sm text-gray-500">Phone</p>
                    <p className="font-medium">{student.phone}</p>
                  </div>
                </div>
              )}

              <div className="flex items-start gap-3">
                <MapPin className="w-5 h-5 text-gray-400 mt-0.5" />
                <div>
                  <p className="text-sm text-gray-500">Address</p>
                  <p className="font-medium">
                    {student.address.street}, {student.address.city}
                  </p>
                  <p className="text-sm text-gray-600">
                    {student.address.state} {student.address.postalCode}
                  </p>
                </div>
              </div>

              {student.bloodGroup && (
                <div className="flex items-start gap-3">
                  <div className="w-5 h-5 flex items-center justify-center text-gray-400 mt-0.5 font-bold">
                    B
                  </div>
                  <div>
                    <p className="text-sm text-gray-500">Blood Group</p>
                    <p className="font-medium">{student.bloodGroup}</p>
                  </div>
                </div>
              )}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Tabs */}
      <div className="mb-6 flex gap-2 border-b border-gray-200 pb-2">
        <TabButton tab="overview" label="Overview" />
        <TabButton tab="academic" label="Academic Records" />
        <TabButton tab="fees" label="Fee History" />
        <TabButton tab="documents" label="Documents" />
      </div>

      {/* Tab Content */}
      {activeTab === 'overview' && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Academic Information */}
          <Card>
            <CardHeader>
              <CardTitle>Academic Information</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div>
                  <p className="text-sm text-gray-500">Class & Section</p>
                  <p className="font-medium">
                    {student.academic.className} {student.academic.section || ''}
                  </p>
                </div>

                {student.academic.rollNumber && (
                  <div>
                    <p className="text-sm text-gray-500">Roll Number</p>
                    <p className="font-medium">{student.academic.rollNumber}</p>
                  </div>
                )}

                <div>
                  <p className="text-sm text-gray-500">Admission Date</p>
                  <p className="font-medium">
                    {format(new Date(student.academic.admissionDate), 'MMMM dd, yyyy')}
                  </p>
                </div>

                {student.academic.previousSchool && (
                  <div>
                    <p className="text-sm text-gray-500">Previous School</p>
                    <p className="font-medium">{student.academic.previousSchool}</p>
                  </div>
                )}
              </div>
            </CardContent>
          </Card>

          {/* Guardian Information */}
          <Card>
            <CardHeader>
              <CardTitle>Guardian Information</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div>
                  <p className="text-sm text-gray-500">Name</p>
                  <p className="font-medium">{student.guardian.name}</p>
                </div>

                <div>
                  <p className="text-sm text-gray-500">Relationship</p>
                  <p className="font-medium">{student.guardian.relationship}</p>
                </div>

                <div>
                  <p className="text-sm text-gray-500">Mobile Number</p>
                  <p className="font-medium">{student.guardian.phone}</p>
                </div>

                {student.guardian.email && (
                  <div>
                    <p className="text-sm text-gray-500">Email</p>
                    <p className="font-medium">{student.guardian.email}</p>
                  </div>
                )}

                {student.guardian.occupation && (
                  <div>
                    <p className="text-sm text-gray-500">Occupation</p>
                    <p className="font-medium">{student.guardian.occupation}</p>
                  </div>
                )}
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {activeTab === 'academic' && (
        <Card>
          <CardContent className="py-12 text-center text-gray-500">
            Academic records feature coming soon
          </CardContent>
        </Card>
      )}

      {activeTab === 'fees' && (
        <Card>
          <CardContent className="py-12 text-center text-gray-500">
            Fee history feature coming soon
          </CardContent>
        </Card>
      )}

      {activeTab === 'documents' && (
        <Card>
          <CardContent className="py-12 text-center text-gray-500">
            Documents feature coming soon
          </CardContent>
        </Card>
      )}
    </div>
  );
};
