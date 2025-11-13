import { useState } from 'react';
import { Search, Plus } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';
import { Select } from '@/shared/components/ui/Select';

/**
 * Student Fee Assignment Page
 *
 * This page allows assigning fee structures to students with options for:
 * - Searching and selecting students
 * - Selecting fee structures
 * - Customizing fee amounts (discounts, scholarships)
 * - Viewing assigned fees
 */
export const StudentFeeAssignment = () => {
  const [searchTerm, setSearchTerm] = useState('');

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Student Fee Assignment</h1>
        <p className="text-gray-600 mt-1">Assign fee structures to students and manage fee customizations</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Student Search */}
        <Card>
          <CardHeader>
            <CardTitle>Search Student</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <Input
                type="text"
                placeholder="Search by student name or ID..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>

            <div className="text-center py-12 text-gray-500">
              <Search className="w-12 h-12 mx-auto mb-2 text-gray-400" />
              <p className="text-sm">Search for a student to assign fees</p>
            </div>
          </CardContent>
        </Card>

        {/* Fee Structure Selection */}
        <Card>
          <CardHeader>
            <CardTitle>Fee Structure</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <Select label="Select Fee Structure">
              <option value="">Choose a fee structure...</option>
              <option value="1">Grade 1 Annual Fee - 2025-2026</option>
              <option value="2">Grade 2 Annual Fee - 2025-2026</option>
            </Select>

            <div className="space-y-2">
              <label className="block text-sm font-medium text-gray-700">
                Discount Type
              </label>
              <Select>
                <option value="">No Discount</option>
                <option value="PERCENTAGE">Percentage Discount</option>
                <option value="FIXED">Fixed Amount Discount</option>
                <option value="SCHOLARSHIP">Scholarship</option>
              </Select>
            </div>

            <Input
              type="number"
              label="Discount Amount"
              placeholder="Enter discount amount"
              disabled
            />

            <Input
              type="text"
              label="Reason"
              placeholder="Enter reason for discount"
              disabled
            />

            <Button className="w-full" disabled>
              <Plus className="w-4 h-4 mr-2" />
              Assign Fee Structure
            </Button>
          </CardContent>
        </Card>
      </div>

      {/* Assigned Fees */}
      <Card className="mt-6">
        <CardHeader>
          <CardTitle>Assigned Fees</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center py-12 text-gray-500">
            <p className="text-sm">Select a student to view assigned fees</p>
          </div>
        </CardContent>
      </Card>

      {/* Info Message */}
      <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
        <p className="text-sm text-blue-800">
          <strong>Coming Soon:</strong> Full fee assignment functionality with student search,
          fee structure selection, discount management, and bulk assignment features.
        </p>
      </div>
    </div>
  );
};

export default StudentFeeAssignment;
