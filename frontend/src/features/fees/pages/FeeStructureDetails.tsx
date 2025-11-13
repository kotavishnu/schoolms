import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Edit, DollarSign, Calendar, Users } from 'lucide-react';
import { useFeeStructure } from '../hooks/useFees';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';
import { Loading } from '@/shared/components/feedback/Loading';
import { ErrorMessage } from '@/shared/components/feedback/ErrorMessage';
import { formatCurrency } from '@/shared/utils/formatters';

/**
 * Fee Structure Details Page
 *
 * Displays detailed information about a fee structure including:
 * - Basic information (name, year, frequency, status)
 * - Fee components breakdown
 * - Applicable classes
 * - Due date configuration
 * - Late fee settings
 */
export const FeeStructureDetails = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const feeStructureId = id ? parseInt(id, 10) : 0;

  const { data, isLoading, error, refetch } = useFeeStructure(feeStructureId);

  if (isLoading) {
    return <Loading message="Loading fee structure details..." />;
  }

  if (error || !data) {
    return (
      <ErrorMessage
        message={error instanceof Error ? error.message : 'Failed to load fee structure'}
        onRetry={refetch}
      />
    );
  }

  const feeStructure = data as any;

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            onClick={() => navigate('/fees/structures')}
            className="flex items-center gap-2"
          >
            <ArrowLeft className="h-5 w-5" />
            Back
          </Button>
          <div>
            <h1 className="text-3xl font-bold text-gray-900">{feeStructure.structureName}</h1>
            <p className="text-gray-600 mt-1">
              {feeStructure.academicYear.yearCode}
              {feeStructure.academicYear.isCurrent && ' (Current)'}
            </p>
          </div>
          <Badge variant={feeStructure.isActive ? 'success' : 'default'}>
            {feeStructure.isActive ? 'Active' : 'Inactive'}
          </Badge>
        </div>
        <Button
          onClick={() => navigate(`/fees/structures/${feeStructureId}/edit`)}
          className="flex items-center gap-2"
        >
          <Edit className="h-4 w-4" />
          Edit Structure
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Content */}
        <div className="lg:col-span-2 space-y-6">
          {/* Fee Components */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <DollarSign className="h-5 w-5" />
                Fee Components
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {feeStructure.components.map((component: any, index: number) => (
                  <div
                    key={index}
                    className="flex items-center justify-between p-4 bg-gray-50 rounded-lg"
                  >
                    <div>
                      <p className="font-medium text-gray-900">{component.feeName}</p>
                      <p className="text-sm text-gray-600">{component.feeType}</p>
                      {component.description && (
                        <p className="text-sm text-gray-500 mt-1">{component.description}</p>
                      )}
                    </div>
                    <div className="text-right">
                      <p className="text-lg font-semibold text-gray-900">
                        {formatCurrency(component.amount)}
                      </p>
                    </div>
                  </div>
                ))}
                <div className="flex items-center justify-between p-4 bg-blue-50 rounded-lg border-2 border-blue-200">
                  <p className="text-lg font-semibold text-gray-900">Total Amount</p>
                  <p className="text-2xl font-bold text-blue-600">
                    {formatCurrency(feeStructure.totalAmount)}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Due Date Configuration */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Calendar className="h-5 w-5" />
                Due Date & Late Fee Configuration
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-500">Due Day of Month</p>
                  <p className="text-base font-medium text-gray-900">
                    {feeStructure.dueDateConfig.dueDay}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Grace Period</p>
                  <p className="text-base font-medium text-gray-900">
                    {feeStructure.dueDateConfig.gracePeriodDays} days
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Late Fee Amount</p>
                  <p className="text-base font-medium text-gray-900">
                    {formatCurrency(feeStructure.dueDateConfig.lateFeeAmount)}
                  </p>
                </div>
                {feeStructure.dueDateConfig.lateFeePercentage && (
                  <div>
                    <p className="text-sm text-gray-500">Late Fee Percentage</p>
                    <p className="text-base font-medium text-gray-900">
                      {feeStructure.dueDateConfig.lateFeePercentage}%
                    </p>
                  </div>
                )}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Basic Information */}
          <Card>
            <CardHeader>
              <CardTitle>Basic Information</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <p className="text-sm text-gray-500">Frequency</p>
                <Badge variant="info">{feeStructure.frequency}</Badge>
              </div>
              <div>
                <p className="text-sm text-gray-500">Effective From</p>
                <p className="text-base font-medium text-gray-900">
                  {new Date(feeStructure.effectiveFrom).toLocaleDateString()}
                </p>
              </div>
              {feeStructure.effectiveTo && (
                <div>
                  <p className="text-sm text-gray-500">Effective To</p>
                  <p className="text-base font-medium text-gray-900">
                    {new Date(feeStructure.effectiveTo).toLocaleDateString()}
                  </p>
                </div>
              )}
              {feeStructure.description && (
                <div>
                  <p className="text-sm text-gray-500">Description</p>
                  <p className="text-base text-gray-900">{feeStructure.description}</p>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Applicable Classes */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Users className="h-5 w-5" />
                Applicable Classes
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex flex-wrap gap-2">
                {feeStructure.applicableClasses.map((className: string, index: number) => (
                  <Badge key={index} variant="default">
                    {className}
                  </Badge>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* Statistics */}
          <Card>
            <CardHeader>
              <CardTitle>Statistics</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <p className="text-sm text-gray-500">Created Date</p>
                <p className="text-base font-medium text-gray-900">
                  {new Date(feeStructure.createdAt).toLocaleDateString()}
                </p>
              </div>
              {feeStructure.updatedAt && (
                <div>
                  <p className="text-sm text-gray-500">Last Updated</p>
                  <p className="text-base font-medium text-gray-900">
                    {new Date(feeStructure.updatedAt).toLocaleDateString()}
                  </p>
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default FeeStructureDetails;
