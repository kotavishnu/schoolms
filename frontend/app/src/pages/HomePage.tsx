import { useNavigate } from 'react-router-dom';
import { Users, CheckCircle, Settings, UserPlus } from 'lucide-react';
import { Card, CardContent } from '../components/ui/card';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { Alert, AlertDescription } from '../components/ui/alert';
import { useStudents } from '../hooks/useStudents';

export function HomePage() {
  const navigate = useNavigate();
  const { statistics, loading, error } = useStudents();

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <LoadingSpinner size="lg" text="Loading dashboard..." />
      </div>
    );
  }

  if (error) {
    return (
      <Alert variant="destructive">
        <AlertDescription>{error}</AlertDescription>
      </Alert>
    );
  }

  const stats = statistics || { totalStudents: 0, activeStudents: 0, inactiveStudents: 0 };

  return (
    <div className="space-y-8">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-700 rounded-lg p-8 shadow-lg">
        <h1 className="text-3xl font-bold text-white mb-2">
          Welcome to School Management System
        </h1>
        <p className="text-blue-100">
          Manage students, configurations, and school operations efficiently
        </p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="bg-white dark:bg-gray-800 hover:shadow-lg transition-shadow">
          <CardContent className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900 rounded-lg flex items-center justify-center">
                <Users className="w-6 h-6 text-blue-600 dark:text-blue-400" />
              </div>
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">Total Students</p>
                <p className="text-3xl font-bold text-gray-900 dark:text-white">
                  {stats.totalStudents}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="bg-white dark:bg-gray-800 hover:shadow-lg transition-shadow">
          <CardContent className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-green-100 dark:bg-green-900 rounded-lg flex items-center justify-center">
                <CheckCircle className="w-6 h-6 text-green-600 dark:text-green-400" />
              </div>
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">Active Students</p>
                <p className="text-3xl font-bold text-gray-900 dark:text-white">
                  {stats.activeStudents}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="bg-white dark:bg-gray-800 hover:shadow-lg transition-shadow">
          <CardContent className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900 rounded-lg flex items-center justify-center">
                <Settings className="w-6 h-6 text-purple-600 dark:text-purple-400" />
              </div>
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">System</p>
                <p className="text-3xl font-bold text-gray-900 dark:text-white">
                  Active
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Quick Actions */}
      <div>
        <h2 className="text-xl font-semibold text-gray-900 dark:text-white mb-4">
          Quick Actions
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Card
            className="bg-white dark:bg-gray-800 cursor-pointer hover:border-blue-500 hover:shadow-lg transition-all"
            onClick={() => navigate('/students')}
          >
            <CardContent className="p-6">
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900 rounded-lg flex items-center justify-center">
                  <Users className="w-6 h-6 text-blue-600 dark:text-blue-400" />
                </div>
                <div>
                  <h3 className="text-base font-semibold text-gray-900 dark:text-white mb-1">
                    Manage Students
                  </h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">
                    View, search, and manage student records
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card
            className="bg-white dark:bg-gray-800 cursor-pointer hover:border-green-500 hover:shadow-lg transition-all"
            onClick={() => navigate('/students')}
          >
            <CardContent className="p-6">
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 bg-green-100 dark:bg-green-900 rounded-lg flex items-center justify-center">
                  <UserPlus className="w-6 h-6 text-green-600 dark:text-green-400" />
                </div>
                <div>
                  <h3 className="text-base font-semibold text-gray-900 dark:text-white mb-1">
                    Register Student
                  </h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">
                    Add a new student to the system
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
