import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Users, Settings, Activity } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { studentService } from '@/services/studentService';
import { toast } from 'sonner';

export function HomePage() {
  const [statistics, setStatistics] = useState({
    totalCount: 0,
    activeCount: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStatistics = async () => {
      try {
        const stats = await studentService.getStatistics();
        setStatistics(stats);
      } catch (error) {
        toast.error('Failed to load statistics');
      } finally {
        setLoading(false);
      }
    };

    fetchStatistics();
  }, []);

  return (
    <div className="space-y-8">
      {/* Welcome Banner */}
      <div className="bg-blue-600 text-white rounded-lg p-8">
        <h1 className="text-3xl font-bold">Welcome to School Management System</h1>
        <p className="mt-2">Manage students, configurations, and school operations efficiently</p>
      </div>

      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Students</CardTitle>
            <Users className="h-4 w-4 text-blue-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{loading ? '...' : statistics.totalCount}</div>
            <p className="text-xs text-gray-600 mt-1">All registered students</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Active Students</CardTitle>
            <Activity className="h-4 w-4 text-green-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{loading ? '...' : statistics.activeCount}</div>
            <p className="text-xs text-gray-600 mt-1">Currently enrolled</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">System Status</CardTitle>
            <Settings className="h-4 w-4 text-purple-600" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">Active</div>
            <p className="text-xs text-gray-600 mt-1">All systems running</p>
          </CardContent>
        </Card>
      </div>

      {/* Quick Actions */}
      <div>
        <h2 className="text-xl font-semibold text-gray-900 mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Users className="h-5 w-5 text-blue-600" />
                Manage Students
              </CardTitle>
              <CardDescription>View, edit, and manage all student records</CardDescription>
            </CardHeader>
            <CardContent>
              <Link to="/students">
                <Button className="w-full">Go to Students</Button>
              </Link>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Users className="h-5 w-5 text-green-600" />
                Register Student
              </CardTitle>
              <CardDescription>Add a new student to the system</CardDescription>
            </CardHeader>
            <CardContent>
              <Link to="/students">
                <Button variant="outline" className="w-full">Register New Student</Button>
              </Link>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
