import { useState, useCallback } from 'react';
import { Plus } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Alert, AlertDescription } from '../components/ui/alert';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../components/ui/select';
import { Label } from '../components/ui/label';
import { ConfigurationDialog } from '../components/configurations/ConfigurationDialog';
import { ConfigurationsTable } from '../components/configurations/ConfigurationsTable';
import { ConfirmDialog } from '../components/common/ConfirmDialog';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { useConfigurations } from '../hooks/useConfigurations';
import { useToast } from '../hooks/useToast';
import type { Configuration, ConfigurationCreateDto, ConfigurationUpdateDto, ConfigCategory } from '../types/configuration';

export function ConfigurationsPage() {
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');
  const [dialogMode, setDialogMode] = useState<'create' | 'edit'>('create');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingConfig, setEditingConfig] = useState<Configuration | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [configToDelete, setConfigToDelete] = useState<Configuration | null>(null);

  const toast = useToast();

  const category = categoryFilter === 'ALL' ? undefined : (categoryFilter as ConfigCategory);
  const { configurations, loading, error, createConfiguration, updateConfiguration, deleteConfiguration, refetch } = useConfigurations(category);

  const filteredConfigurations = configurations.filter((config) => {
    return categoryFilter === 'ALL' || config.category === categoryFilter;
  });

  const handleSubmit = useCallback(async (data: ConfigurationCreateDto | ConfigurationUpdateDto) => {
    try {
      if (dialogMode === 'create') {
        await createConfiguration(data as ConfigurationCreateDto);
        toast.success('Configuration added successfully');
      } else if (editingConfig) {
        await updateConfiguration(editingConfig.id, data as ConfigurationUpdateDto);
        toast.success('Configuration updated successfully');
      }
      setDialogOpen(false);
      setEditingConfig(null);
      await refetch();
    } catch (error: any) {
      toast.error(error.message || 'Operation failed');
      throw error;
    }
  }, [dialogMode, editingConfig, createConfiguration, updateConfiguration, toast, refetch]);

  const handleDelete = useCallback(async () => {
    if (!configToDelete) return;

    try {
      await deleteConfiguration(configToDelete.id);
      toast.success('Configuration deleted successfully');
      setDeleteDialogOpen(false);
      setConfigToDelete(null);
      await refetch();
    } catch (error: any) {
      toast.error(error.message || 'Failed to delete configuration');
    }
  }, [configToDelete, deleteConfiguration, toast, refetch]);

  const openCreateDialog = () => {
    setDialogMode('create');
    setEditingConfig(null);
    setDialogOpen(true);
  };

  const openEditDialog = (config: Configuration) => {
    setDialogMode('edit');
    setEditingConfig(config);
    setDialogOpen(true);
  };

  const openDeleteDialog = (config: Configuration) => {
    setConfigToDelete(config);
    setDeleteDialogOpen(true);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">System Configurations</h1>
          <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
            {loading ? 'Loading...' : `${filteredConfigurations.length} configurations`}
          </p>
        </div>
        <Button onClick={openCreateDialog} className="bg-blue-600 hover:bg-blue-700">
          <Plus className="h-4 w-4 mr-2" />
          Add New Configuration
        </Button>
      </div>

      {/* Category Filter */}
      <div className="flex items-end gap-4">
        <div className="flex-1 sm:w-64">
          <Label htmlFor="category-filter">Filter by Category</Label>
          <Select value={categoryFilter} onValueChange={setCategoryFilter}>
            <SelectTrigger id="category-filter">
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
      </div>

      {/* Error Alert */}
      {error && (
        <Alert variant="destructive">
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {/* Loading State */}
      {loading && (
        <div className="flex justify-center py-12">
          <LoadingSpinner size="lg" text="Loading configurations..." />
        </div>
      )}

      {/* Empty State */}
      {!loading && filteredConfigurations.length === 0 && (
        <div className="text-center py-12">
          <p className="text-gray-500 dark:text-gray-400 mb-4">
            {categoryFilter !== 'ALL'
              ? 'No configurations found for this category'
              : 'No configurations found'}
          </p>
          {categoryFilter === 'ALL' && (
            <Button onClick={openCreateDialog} variant="outline">
              Add First Configuration
            </Button>
          )}
        </div>
      )}

      {/* Configurations Table */}
      {!loading && filteredConfigurations.length > 0 && (
        <ConfigurationsTable
          configurations={filteredConfigurations}
          onEdit={openEditDialog}
          onDelete={openDeleteDialog}
        />
      )}

      {/* Dialogs */}
      <ConfigurationDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        onSubmit={handleSubmit}
        configuration={editingConfig}
        mode={dialogMode}
      />

      <ConfirmDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        onConfirm={handleDelete}
        title="Delete Configuration"
        description={`Are you sure you want to delete the configuration "${configToDelete?.key}"? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        variant="destructive"
      />
    </div>
  );
}
