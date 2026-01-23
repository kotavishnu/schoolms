import { useState } from 'react';
import { Plus, Edit, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { ConfigurationDialog } from '@/components/configurations/ConfigurationDialog';
import { useConfigurations } from '@/hooks/useConfigurations';
import type { Configuration, ConfigCategory, ConfigurationCreateRequest, ConfigurationUpdateRequest } from '@/types/configuration';
import { format } from 'date-fns';

const categoryColors: Record<ConfigCategory, string> = {
  GENERAL: 'bg-blue-100 text-blue-800',
  ACADEMIC: 'bg-purple-100 text-purple-800',
  FINANCE: 'bg-green-100 text-green-800',
  SYSTEM: 'bg-gray-100 text-gray-800',
};

export function ConfigurationsPage() {
  const {
    configurations,
    loading,
    createConfiguration,
    updateConfiguration,
    deleteConfiguration,
    filterByCategory,
  } = useConfigurations();

  const [categoryFilter, setCategoryFilter] = useState<ConfigCategory | 'ALL'>('ALL');
  const [dialogState, setDialogState] = useState<{
    mode: 'create' | 'edit' | null;
    configuration?: Configuration;
  }>({ mode: null });
  const [deleteConfirmation, setDeleteConfirmation] = useState<{
    isOpen: boolean;
    configuration?: Configuration;
  }>({ isOpen: false });

  const handleCategoryChange = (value: string) => {
    setCategoryFilter(value as ConfigCategory | 'ALL');
    if (value === 'ALL') {
      filterByCategory(undefined);
    } else {
      filterByCategory(value as ConfigCategory);
    }
  };

  const handleCreateConfiguration = async (data: ConfigurationCreateRequest) => {
    await createConfiguration(data);
  };

  const handleUpdateConfiguration = async (data: ConfigurationUpdateRequest) => {
    if (dialogState.configuration) {
      await updateConfiguration(
        dialogState.configuration.category,
        dialogState.configuration.key,
        data
      );
    }
  };

  const handleDeleteConfirm = async () => {
    if (deleteConfirmation.configuration) {
      await deleteConfiguration(
        deleteConfirmation.configuration.category,
        deleteConfirmation.configuration.key
      );
      setDeleteConfirmation({ isOpen: false });
    }
  };

  const handleDialogSubmit = async (data: ConfigurationCreateRequest | ConfigurationUpdateRequest) => {
    if (dialogState.mode === 'create') {
      await handleCreateConfiguration(data as ConfigurationCreateRequest);
    } else if (dialogState.mode === 'edit') {
      await handleUpdateConfiguration(data as ConfigurationUpdateRequest);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Configurations</h1>
          <p className="text-gray-600 mt-1">{configurations.length} total configurations</p>
        </div>
        <Button onClick={() => setDialogState({ mode: 'create' })}>
          <Plus className="h-4 w-4 mr-2" />
          Add New Configuration
        </Button>
      </div>

      {/* Category Filter */}
      <div>
        <Label className="text-sm font-medium mb-2 block">CATEGORY</Label>
        <Select value={categoryFilter} onValueChange={handleCategoryChange}>
          <SelectTrigger className="w-[200px]">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="ALL">All Categories</SelectItem>
            <SelectItem value="GENERAL">General</SelectItem>
            <SelectItem value="ACADEMIC">Academic</SelectItem>
            <SelectItem value="FINANCE">Finance</SelectItem>
            <SelectItem value="SYSTEM">System</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Configurations Table */}
      {loading ? (
        <div className="text-center py-12">
          <p className="text-gray-600">Loading configurations...</p>
        </div>
      ) : configurations.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-600">
            {categoryFilter !== 'ALL'
              ? 'No configurations found for this category'
              : 'No configurations found'}
          </p>
        </div>
      ) : (
        <div className="bg-white rounded-lg border border-gray-200 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    CATEGORY
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    KEY
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    VALUE
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    DESCRIPTION
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    LAST UPDATED
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    ACTIONS
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {configurations.map((config) => (
                  <tr key={`${config.category}-${config.key}`} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <Badge className={categoryColors[config.category]}>{config.category}</Badge>
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm font-medium text-gray-900">{config.key}</span>
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-gray-900">{config.value}</span>
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-gray-600">{config.description || '-'}</span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="text-sm text-gray-600">
                        {config.lastUpdated && !isNaN(new Date(config.lastUpdated).getTime())
                          ? format(new Date(config.lastUpdated), 'MMM d, yyyy')
                          : '-'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right">
                      <div className="flex justify-end gap-2">
                        <Button
                          size="sm"
                          onClick={() => setDialogState({ mode: 'edit', configuration: config })}
                          className="bg-blue-600 hover:bg-blue-700 text-white"
                        >
                          Edit
                        </Button>
                        <Button
                          size="sm"
                          onClick={() => setDeleteConfirmation({ isOpen: true, configuration: config })}
                          className="bg-red-600 hover:bg-red-700 text-white"
                        >
                          Delete
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Configuration Dialog */}
      <ConfigurationDialog
        mode={dialogState.mode || 'create'}
        configuration={dialogState.configuration}
        open={dialogState.mode !== null}
        onClose={() => setDialogState({ mode: null })}
        onSubmit={handleDialogSubmit}
      />

      {/* Delete Confirmation Dialog */}
      <AlertDialog
        open={deleteConfirmation.isOpen}
        onOpenChange={(open) => !open && setDeleteConfirmation({ isOpen: false })}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Configuration</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete the configuration "{deleteConfirmation.configuration?.key}"?
              This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={handleDeleteConfirm} className="bg-red-600 hover:bg-red-700">
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
