import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  DollarSign,
  TrendingUp,
  Clock,
  PieChart as PieChartIcon,
  Plus,
  Eye,
  ArrowRight,
} from 'lucide-react';
import {
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  BarChart,
  Bar,
} from 'recharts';
import { Card } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { useDashboardStats } from '../api/paymentApi';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';
import type { PaymentMethod } from '../types/payment.types';

// Color palette for charts
const CHART_COLORS = {
  Cash: '#10B981',
  Card: '#3B82F6',
  'Bank Transfer': '#8B5CF6',
  UPI: '#F59E0B',
  Cheque: '#EF4444',
};

/**
 * Payment Dashboard Page
 * Displays payment statistics, trends, and recent activity
 */
export const PaymentDashboard: React.FC = () => {
  const navigate = useNavigate();

  // Fetch dashboard statistics
  const { data: stats, isLoading, error } = useDashboardStats();

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 py-6 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
            <p className="text-gray-500">Loading dashboard...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error || !stats) {
    return (
      <div className="min-h-screen bg-gray-50 py-6 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <Card>
            <div className="p-12 text-center">
              <h2 className="text-2xl font-bold text-gray-900 mb-2">
                Error Loading Dashboard
              </h2>
              <p className="text-gray-600">
                {(error as any)?.message || 'Failed to load dashboard statistics'}
              </p>
            </div>
          </Card>
        </div>
      </div>
    );
  }

  // Prepare data for pie chart
  const pieChartData = stats.collectionsByMethod.map((item) => ({
    name: item.method,
    value: item.amount,
    count: item.count,
  }));

  return (
    <div className="min-h-screen bg-gray-50 py-6 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-6">
          <div className="flex justify-between items-start">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Payment Dashboard</h1>
              <p className="mt-2 text-gray-600">
                Overview of collections, trends, and payment statistics
              </p>
            </div>
            <Button onClick={() => navigate('/payments/record')}>
              <Plus className="w-4 h-4 mr-2" />
              Record Payment
            </Button>
          </div>
        </div>

        {/* Summary Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
          {/* Today's Collections */}
          <Card>
            <div className="p-6">
              <div className="flex items-center justify-between mb-2">
                <h3 className="text-sm font-medium text-gray-600">Today's Collections</h3>
                <DollarSign className="w-5 h-5 text-green-500" />
              </div>
              <p className="text-2xl font-bold text-gray-900">
                {formatCurrency(stats.todayCollections)}
              </p>
              <p className="text-xs text-gray-500 mt-1">As of {formatDate(new Date().toISOString())}</p>
            </div>
          </Card>

          {/* This Month's Collections */}
          <Card>
            <div className="p-6">
              <div className="flex items-center justify-between mb-2">
                <h3 className="text-sm font-medium text-gray-600">This Month</h3>
                <TrendingUp className="w-5 h-5 text-blue-500" />
              </div>
              <p className="text-2xl font-bold text-gray-900">
                {formatCurrency(stats.monthCollections)}
              </p>
              <p className="text-xs text-gray-500 mt-1">
                {new Date().toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}
              </p>
            </div>
          </Card>

          {/* This Year's Collections */}
          <Card>
            <div className="p-6">
              <div className="flex items-center justify-between mb-2">
                <h3 className="text-sm font-medium text-gray-600">This Year</h3>
                <TrendingUp className="w-5 h-5 text-purple-500" />
              </div>
              <p className="text-2xl font-bold text-gray-900">
                {formatCurrency(stats.yearCollections)}
              </p>
              <p className="text-xs text-gray-500 mt-1">
                {new Date().getFullYear()}
              </p>
            </div>
          </Card>

          {/* Pending Payments */}
          <Card>
            <div className="p-6">
              <div className="flex items-center justify-between mb-2">
                <h3 className="text-sm font-medium text-gray-600">Pending Payments</h3>
                <Clock className="w-5 h-5 text-orange-500" />
              </div>
              <p className="text-2xl font-bold text-orange-600">
                {stats.pendingPayments.count}
              </p>
              <p className="text-xs text-gray-500 mt-1">
                Amount: {formatCurrency(stats.pendingPayments.totalAmount)}
              </p>
            </div>
          </Card>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
          {/* Collection by Payment Method - Pie Chart */}
          <Card>
            <div className="p-6">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl font-semibold text-gray-900">
                  Collections by Payment Method
                </h2>
                <PieChartIcon className="w-5 h-5 text-gray-400" />
              </div>

              {pieChartData.length > 0 ? (
                <>
                  <ResponsiveContainer width="100%" height={300}>
                    <PieChart>
                      <Pie
                        data={pieChartData}
                        cx="50%"
                        cy="50%"
                        labelLine={false}
                        label={(entry) => `${entry.name}: ${formatCurrency(entry.value)}`}
                        outerRadius={100}
                        fill="#8884d8"
                        dataKey="value"
                      >
                        {pieChartData.map((entry, index) => (
                          <Cell
                            key={`cell-${index}`}
                            fill={CHART_COLORS[entry.name as PaymentMethod] || '#94A3B8'}
                          />
                        ))}
                      </Pie>
                      <Tooltip
                        formatter={(value: number) => formatCurrency(value)}
                      />
                    </PieChart>
                  </ResponsiveContainer>

                  {/* Legend */}
                  <div className="mt-4 grid grid-cols-2 gap-2">
                    {stats.collectionsByMethod.map((item) => (
                      <div key={item.method} className="flex items-center gap-2">
                        <div
                          className="w-4 h-4 rounded"
                          style={{ backgroundColor: CHART_COLORS[item.method] }}
                        ></div>
                        <div className="text-sm">
                          <span className="text-gray-700">{item.method}</span>
                          <span className="text-gray-500 ml-2">({item.count})</span>
                        </div>
                      </div>
                    ))}
                  </div>
                </>
              ) : (
                <div className="text-center py-12 text-gray-500">
                  <p>No payment data available</p>
                </div>
              )}
            </div>
          </Card>

          {/* Top Paying Classes */}
          <Card>
            <div className="p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">
                Top Paying Classes
              </h2>

              {stats.topClasses.length > 0 ? (
                <div className="space-y-4">
                  {stats.topClasses.slice(0, 5).map((classData, index) => (
                    <div key={index} className="flex items-center justify-between">
                      <div className="flex-1">
                        <div className="flex items-center gap-2">
                          <Badge variant="default">{index + 1}</Badge>
                          <span className="font-medium text-gray-900">
                            {classData.className}
                          </span>
                          <span className="text-sm text-gray-500">
                            ({classData.studentCount} students)
                          </span>
                        </div>
                        <div className="mt-1 bg-gray-200 rounded-full h-2">
                          <div
                            className="bg-blue-600 h-2 rounded-full"
                            style={{
                              width: `${
                                (classData.totalAmount / stats.topClasses[0].totalAmount) * 100
                              }%`,
                            }}
                          ></div>
                        </div>
                      </div>
                      <div className="ml-4 text-right">
                        <p className="font-semibold text-gray-900">
                          {formatCurrency(classData.totalAmount)}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-12 text-gray-500">
                  <p>No class data available</p>
                </div>
              )}
            </div>
          </Card>
        </div>

        {/* Revenue Trends - Line Chart */}
        <Card className="mb-6">
          <div className="p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">
              Revenue Trends (Last 12 Months)
            </h2>

            {stats.revenueTrends.length > 0 ? (
              <ResponsiveContainer width="100%" height={350}>
                <LineChart data={stats.revenueTrends}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis
                    dataKey="month"
                    tick={{ fontSize: 12 }}
                  />
                  <YAxis
                    tick={{ fontSize: 12 }}
                    tickFormatter={(value) => `₹${value / 1000}k`}
                  />
                  <Tooltip
                    formatter={(value: number) => formatCurrency(value)}
                    labelFormatter={(label) => `Month: ${label}`}
                  />
                  <Legend />
                  <Line
                    type="monotone"
                    dataKey="amount"
                    name="Revenue"
                    stroke="#3B82F6"
                    strokeWidth={2}
                    dot={{ r: 4 }}
                    activeDot={{ r: 6 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            ) : (
              <div className="text-center py-12 text-gray-500">
                <p>No revenue trend data available</p>
              </div>
            )}
          </div>
        </Card>

        {/* Recent Payments */}
        <Card>
          <div className="p-6">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-semibold text-gray-900">Recent Payments</h2>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => navigate('/payments/history')}
              >
                View All
                <ArrowRight className="w-4 h-4 ml-2" />
              </Button>
            </div>

            {stats.recentPayments.length > 0 ? (
              <div className="space-y-3">
                {stats.recentPayments.slice(0, 5).map((payment) => (
                  <div
                    key={payment.id}
                    className="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
                  >
                    <div className="flex-1">
                      <div className="flex items-center gap-3">
                        <span className="font-medium text-blue-600">
                          {payment.receiptNumber}
                        </span>
                        <Badge
                          variant={payment.status === 'Completed' ? 'success' : 'warning'}
                         
                        >
                          {payment.status}
                        </Badge>
                      </div>
                      <div className="mt-1 flex items-center gap-4 text-sm text-gray-600">
                        <span>{payment.studentName}</span>
                        <span>•</span>
                        <span>{payment.studentClass}</span>
                        <span>•</span>
                        <span>{formatDate(payment.paymentDate)}</span>
                        <span>•</span>
                        <span>{payment.paymentMethod}</span>
                      </div>
                    </div>
                    <div className="flex items-center gap-4">
                      <p className="text-lg font-semibold text-gray-900">
                        {formatCurrency(payment.totalAmount)}
                      </p>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => navigate(`/payments/${payment.id}/receipt`)}
                      >
                        <Eye className="w-4 h-4" />
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-12 text-gray-500">
                <p>No recent payments</p>
              </div>
            )}
          </div>
        </Card>
      </div>
    </div>
  );
};

export default PaymentDashboard;
