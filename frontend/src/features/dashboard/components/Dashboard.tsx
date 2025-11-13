import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Users, BookOpen, DollarSign, TrendingUp } from 'lucide-react';

interface StatCardProps {
  title: string;
  value: string | number;
  icon: React.ElementType;
  trend?: string;
}

const StatCard: React.FC<StatCardProps> = ({ title, value, icon: Icon, trend }) => (
  <Card>
    <CardContent className="pt-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-2xl font-bold text-gray-900 mt-2">{value}</p>
          {trend && (
            <p className="text-sm text-green-600 mt-1 flex items-center gap-1">
              <TrendingUp className="h-3 w-3" />
              {trend}
            </p>
          )}
        </div>
        <div className="h-12 w-12 bg-blue-100 rounded-full flex items-center justify-center">
          <Icon className="h-6 w-6 text-blue-600" />
        </div>
      </div>
    </CardContent>
  </Card>
);

/**
 * Main Dashboard component
 */
export const Dashboard: React.FC = () => {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-gray-600 mt-1">Overview of your school management system</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Students"
          value="1,234"
          icon={Users}
          trend="+12% from last month"
        />
        <StatCard
          title="Active Classes"
          value="45"
          icon={BookOpen}
          trend="+2 new classes"
        />
        <StatCard
          title="Monthly Revenue"
          value="₹5,45,000"
          icon={DollarSign}
          trend="+8% from last month"
        />
        <StatCard
          title="Pending Fees"
          value="₹1,25,000"
          icon={DollarSign}
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Recent Activities</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex items-center justify-between py-3 border-b">
                <div>
                  <p className="font-medium text-gray-900">New student registered</p>
                  <p className="text-sm text-gray-500">John Doe - Class 5-A</p>
                </div>
                <span className="text-xs text-gray-500">2 hours ago</span>
              </div>
              <div className="flex items-center justify-between py-3 border-b">
                <div>
                  <p className="font-medium text-gray-900">Payment received</p>
                  <p className="text-sm text-gray-500">₹25,000 from Jane Smith</p>
                </div>
                <span className="text-xs text-gray-500">5 hours ago</span>
              </div>
              <div className="flex items-center justify-between py-3">
                <div>
                  <p className="font-medium text-gray-900">Class created</p>
                  <p className="text-sm text-gray-500">Class 6-C added</p>
                </div>
                <span className="text-xs text-gray-500">1 day ago</span>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Upcoming Events</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="flex items-center gap-3 py-3 border-b">
                <div className="h-12 w-12 bg-blue-100 rounded-lg flex items-center justify-center flex-shrink-0">
                  <span className="text-lg font-bold text-blue-600">15</span>
                </div>
                <div>
                  <p className="font-medium text-gray-900">Mid-term Examinations</p>
                  <p className="text-sm text-gray-500">Classes 1-10</p>
                </div>
              </div>
              <div className="flex items-center gap-3 py-3 border-b">
                <div className="h-12 w-12 bg-green-100 rounded-lg flex items-center justify-center flex-shrink-0">
                  <span className="text-lg font-bold text-green-600">20</span>
                </div>
                <div>
                  <p className="font-medium text-gray-900">Fee Collection Due</p>
                  <p className="text-sm text-gray-500">Q2 Fees</p>
                </div>
              </div>
              <div className="flex items-center gap-3 py-3">
                <div className="h-12 w-12 bg-yellow-100 rounded-lg flex items-center justify-center flex-shrink-0">
                  <span className="text-lg font-bold text-yellow-600">25</span>
                </div>
                <div>
                  <p className="font-medium text-gray-900">Parent-Teacher Meeting</p>
                  <p className="text-sm text-gray-500">All classes</p>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};
