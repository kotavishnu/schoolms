import { useState, useEffect } from 'react';
import { Edit, Trash2, Plus } from 'lucide-react';
import { toast } from 'sonner';
import { Button } from './ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Skeleton } from './ui/skeleton';
import { ConfigurationDialog } from './ConfigurationDialog';
import { configApi, type Configuration } from '../services/api/configApi';

export function ConfigurationsPage() {
  const [configurations, setConfigurations] = useState<Configuration[]>([]);
  const [loading, setLoading] = useState(false);
  const [categoryFilter, setCategoryFilter] = useState<string>('all');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingConfig, setEditingConfig] = useState<Configuration | null>(null);

  useEffect(() => {
    fetchConfigurations();
  }, [categoryFilter]);

  const fetchConfigurations = async () => {
    setLoading(true);
    try {
      const response = await configApi.getAllConfigurations(
        categoryFilter !== 'all' ? categoryFilter : undefined
      );
      setConfigurations(response);
    } catch (error) {
      toast.error('Failed to fetch configurations');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (category: string, key: string) => {
    if (confirm('Are you sure you want to delete this configuration?')) {
      try {
        await configApi.deleteConfiguration(category, key);
        toast.success('Configuration deleted successfully');
        await fetchConfigurations();
      } catch (error) {
        toast.error('Failed to delete configuration');
      }
    }
  };

  const openEditDialog = (config: Configuration) => {
    setEditingConfig(config);
    setDialogOpen(true);
  };

  const closeDialog = () => {
    setDialogOpen(false);
    setEditingConfig(null);
  };

  const groupedConfigs = configurations.reduce((acc, config) => {
    if (!acc[config.category]) {
      acc[config.category] = [];
    }
    acc[config.category].push(config);
    return acc;
  }, {} as Record<string, Configuration[]>);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold mb-1">Configurations</h1>
          <p className="text-sm text-gray-600">{configurations.length} total configurations</p>
        </div>
        <Button onClick={() => setDialogOpen(true)} className="bg-blue-600 hover:bg-blue-700">
          <Plus className="w-4 h-4 mr-2" />
          Add Configuration
        </Button>
      </div>

      {/* Filter Section */}
      <div className="bg-white rounded-lg p-6 mb-6 border border-gray-200">
        <div className="flex items-center gap-4">
          <label className="text-sm font-medium text-gray-700">Filter by Category:</label>
          <Select value={categoryFilter} onValueChange={setCategoryFilter}>
            <SelectTrigger className="w-[200px]">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Categories</SelectItem>
              <SelectItem value="GENERAL">General</SelectItem>
              <SelectItem value="ACADEMIC">Academic</SelectItem>
              <SelectItem value="FINANCIAL">Financial</SelectItem>
              <SelectItem value="SYSTEM">System</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      {/* Loading Skeleton */}
      {loading && (
        <div className="space-y-6">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="bg-white rounded-lg border border-gray-200">
              <div className="px-6 py-4 border-b border-gray-200 bg-gray-50">
                <Skeleton className="h-6 w-32 mb-2" />
                <Skeleton className="h-4 w-48" />
              </div>
              <div className="p-6 space-y-4">
                <Skeleton className="h-12 w-full" />
                <Skeleton className="h-12 w-full" />
                <Skeleton className="h-12 w-full" />
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Configurations by Category */}
      {!loading && configurations.length === 0 && (
        <div className="bg-white rounded-lg p-8 text-center border border-gray-200">
          <p className="text-gray-500">No configurations found</p>
        </div>
      )}

      {!loading && configurations.length > 0 && (
        <div className="space-y-6">
          {Object.entries(groupedConfigs).map(([category, configs]) => (
            <div key={category} className="bg-white rounded-lg border border-gray-200">
              <div className="px-6 py-4 border-b border-gray-200 bg-gray-50">
                <h2 className="text-lg font-medium">{category}</h2>
                <p className="text-sm text-gray-600">{configs.length} configuration(s)</p>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="border-b border-gray-200 bg-gray-50">
                      <th className="text-left py-3 px-6 text-sm font-medium text-gray-700">Key</th>
                      <th className="text-left py-3 px-6 text-sm font-medium text-gray-700">Value</th>
                      <th className="text-left py-3 px-6 text-sm font-medium text-gray-700">Description</th>
                      <th className="text-left py-3 px-6 text-sm font-medium text-gray-700">Data Type</th>
                      <th className="text-left py-3 px-6 text-sm font-medium text-gray-700">Last Updated</th>
                      <th className="text-right py-3 px-6 text-sm font-medium text-gray-700">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {configs.map((config) => (
                      <tr key={config.id} className="border-b border-gray-100 hover:bg-gray-50">
                        <td className="py-4 px-6">
                          <span className="font-mono text-sm">{config.key}</span>
                        </td>
                        <td className="py-4 px-6">
                          <span className="text-sm">
                            {config.isEncrypted ? '********' : config.value}
                          </span>
                        </td>
                        <td className="py-4 px-6 text-sm text-gray-600">
                          {config.description || 'N/A'}
                        </td>
                        <td className="py-4 px-6">
                          <span className="px-2 py-1 text-xs rounded bg-blue-100 text-blue-800">
                            {config.dataType}
                          </span>
                        </td>
                        <td className="py-4 px-6 text-sm text-gray-600">
                          {new Date(config.updatedAt).toLocaleDateString()}
                        </td>
                        <td className="py-4 px-6 text-right">
                          <div className="flex justify-end gap-2">
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => openEditDialog(config)}
                            >
                              <Edit className="w-4 h-4" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => handleDelete(config.category, config.key)}
                              className="text-red-600 hover:text-red-700 hover:bg-red-50"
                            >
                              <Trash2 className="w-4 h-4" />
                            </Button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          ))}
        </div>
      )}

      <ConfigurationDialog
        open={dialogOpen}
        onOpenChange={closeDialog}
        onSuccess={fetchConfigurations}
        configuration={editingConfig}
      />
    </div>
  );
}
