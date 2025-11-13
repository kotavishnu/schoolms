import { DollarSign, TrendingUp, AlertCircle, Calendar } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';

/**
 * Fee Dashboard Page
 *
 * Displays statistics and insights about fee collection including:
 * - Total fees collected (current month, year)
 * - Pending fees amount
 * - Overdue fees
 * - Collection statistics and charts
 * - Recent transactions
 */
export const FeeDashboard = () => {
  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Fee Dashboard</h1>
        <p className="text-gray-600 mt-1">Overview of fee collection and statistics</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">This Month Collection</p>
                <p className="text-2xl font-bold text-gray-900 mt-1">₹0.00</p>
                <p className="text-xs text-green-600 mt-1">↑ 0% from last month</p>
              </div>
              <div className="bg-blue-100 p-3 rounded-full">
                <DollarSign className="w-6 h-6 text-blue-600" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Total Pending Fees</p>
                <p className="text-2xl font-bold text-gray-900 mt-1">₹0.00</p>
                <p className="text-xs text-gray-600 mt-1">0 students</p>
              </div>
              <div className="bg-yellow-100 p-3 rounded-full">
                <Calendar className="w-6 h-6 text-yellow-600" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Overdue Fees</p>
                <p className="text-2xl font-bold text-gray-900 mt-1">₹0.00</p>
                <p className="text-xs text-red-600 mt-1">0 students</p>
              </div>
              <div className="bg-red-100 p-3 rounded-full">
                <AlertCircle className="w-6 h-6 text-red-600" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Collection Rate</p>
                <p className="text-2xl font-bold text-gray-900 mt-1">0%</p>
                <p className="text-xs text-gray-600 mt-1">Current academic year</p>
              </div>
              <div className="bg-green-100 p-3 rounded-full">
                <TrendingUp className="w-6 h-6 text-green-600" />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Collection Trends */}
        <Card>
          <CardHeader>
            <CardTitle>Collection Trends</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-center py-12 text-gray-500">
              <TrendingUp className="w-12 h-12 mx-auto mb-2 text-gray-400" />
              <p className="text-sm">Collection trend chart will appear here</p>
              <p className="text-xs mt-1">Monthly collection comparison</p>
            </div>
          </CardContent>
        </Card>

        {/* Fee Structure Distribution */}
        <Card>
          <CardHeader>
            <CardTitle>Fee Structure Distribution</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-center py-12 text-gray-500">
              <DollarSign className="w-12 h-12 mx-auto mb-2 text-gray-400" />
              <p className="text-sm">Fee structure distribution chart</p>
              <p className="text-xs mt-1">Breakdown by fee type</p>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Recent Transactions */}
      <Card className="mt-6">
        <CardHeader>
          <CardTitle>Recent Fee Transactions</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center py-12 text-gray-500">
            <Calendar className="w-12 h-12 mx-auto mb-2 text-gray-400" />
            <p className="text-sm">No recent transactions</p>
            <p className="text-xs mt-1">Fee transactions will appear here</p>
          </div>
        </CardContent>
      </Card>

      {/* Overdue Students */}
      <Card className="mt-6">
        <CardHeader>
          <CardTitle className="flex items-center justify-between">
            <span>Students with Overdue Fees</span>
            <Badge variant="error">0</Badge>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center py-12 text-gray-500">
            <AlertCircle className="w-12 h-12 mx-auto mb-2 text-gray-400" />
            <p className="text-sm">No overdue fees</p>
            <p className="text-xs mt-1">All students are up to date</p>
          </div>
        </CardContent>
      </Card>

      {/* Info Message */}
      <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
        <p className="text-sm text-blue-800">
          <strong>Coming Soon:</strong> Interactive charts, detailed analytics, fee collection reports,
          automated reminders, and advanced filtering options.
        </p>
      </div>
    </div>
  );
};

export default FeeDashboard;
