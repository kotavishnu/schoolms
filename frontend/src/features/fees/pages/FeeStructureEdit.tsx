import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Loading } from '@/shared/components/feedback/Loading';
import { ErrorMessage } from '@/shared/components/feedback/ErrorMessage';
import { useFeeStructure } from '../hooks/useFees';

/**
 * Fee Structure Edit Page
 *
 * This is a placeholder page that will contain the edit form for fee structures.
 * The form will be similar to FeeStructureCreate but pre-populated with existing data.
 */
export const FeeStructureEdit = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const feeStructureId = id ? parseInt(id, 10) : 0;

  const { data, isLoading, error } = useFeeStructure(feeStructureId);

  if (isLoading) {
    return <Loading message="Loading fee structure..." />;
  }

  if (error || !data) {
    return (
      <ErrorMessage
        message={error instanceof Error ? error.message : 'Failed to load fee structure'}
      />
    );
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl">
      <div className="flex items-center gap-4 mb-6">
        <Button
          variant="ghost"
          onClick={() => navigate(`/fees/structures/${feeStructureId}`)}
          className="flex items-center gap-2"
        >
          <ArrowLeft className="h-5 w-5" />
          Back
        </Button>
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Edit Fee Structure</h1>
          <p className="text-gray-600 mt-1">{data.structureName}</p>
        </div>
      </div>

      <Card className="p-12">
        <div className="text-center text-gray-500">
          <p className="text-lg font-medium mb-2">Fee Structure Edit Form</p>
          <p className="text-sm">
            This feature will allow editing of existing fee structures with pre-populated data.
          </p>
          <p className="text-sm mt-4">
            Coming soon - Full edit functionality with validation and update API integration.
          </p>
        </div>
      </Card>
    </div>
  );
};

export default FeeStructureEdit;
