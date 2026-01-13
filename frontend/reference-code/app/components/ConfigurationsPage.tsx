import { useState } from 'react';
import { Edit, Trash2, Plus } from 'lucide-react';
import { Button } from './ui/button';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from './ui/alert-dialog';
import { ConfigurationDialog } from './ConfigurationDialog';
import type { Configuration } from '../types';

const initialConfigurations: Configuration[] = [
  {
    id: '1',
    category: 'GENERAL',
    key: 'school_name',
    value: 'ABC International School',
    description: 'Official school name',
    lastUpdated: 'Nov 23, 2025',
  },
  {
    id: '2',
    category: 'GENERAL',
    key: 'school_code',
    value: 'ABC-001',
    description: 'Unique school identification code',
    lastUpdated: 'Nov 23, 2025',
  },
  {
    id: '3',
    category: 'GENERAL',
    key: 'school_address',
    value: '123 Education Street, City, State',
    description: 'School physical address',
    lastUpdated: 'Nov 23, 2025',
  },
  {
    id: '4',
    category: 'ACADEMIC',
    key: 'academic_year_end',
    value: '2026-03-31',
    description: 'End date of academic year',
    lastUpdated: 'Nov 23, 2025',
  },
  {
    id: '5',
    category: 'ACADEMIC',
    key: 'grading_system',
    value: 'A-F',
    description: 'Grading system used (A-F, Percentage, etc.)',
    lastUpdated: 'Nov 23, 2025',
  },
];

export function ConfigurationsPage() {
  const [configurations, setConfigurations] = useState<Configuration[]>(initialConfigurations);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [editingConfig, setEditingConfig] = useState<Configuration | null>(null);
  const [deletingConfig, setDeletingConfig] = useState<Configuration | null>(null);

  const handleCreate = (config: Omit<Configuration, 'id'>) => {
    const newConfig: Configuration = {
      ...config,
      id: String(configurations.length + 1),
    };
    setConfigurations([...configurations, newConfig]);
  };

  const handleUpdate = (id: string, updatedConfig: Omit<Configuration, 'id'>) => {
    setConfigurations(
      configurations.map((c) => (c.id === id ? { ...updatedConfig, id } : c))
    );
  };

  const openDeleteDialog = (config: Configuration) => {
    setDeletingConfig(config);
    setDeleteDialogOpen(true);
  };

  const handleConfirmDelete = () => {
    if (deletingConfig) {
      setConfigurations(configurations.filter((c) => c.id !== deletingConfig.id));
      setDeleteDialogOpen(false);
      setDeletingConfig(null);
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

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl mb-1">Configurations</h1>
          <p className="text-sm text-gray-600">{configurations.length} total configurations</p>
        </div>
        <Button onClick={() => setDialogOpen(true)} className="bg-blue-600 hover:bg-blue-700">
          <Plus className="w-4 h-4 mr-2" />
          Add New Configuration
        </Button>
      </div>

      {/* Configurations Table */}
      <div className="bg-white rounded-lg border border-gray-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs uppercase text-gray-500">Category</th>
                <th className="px-6 py-3 text-left text-xs uppercase text-gray-500">Key</th>
                <th className="px-6 py-3 text-left text-xs uppercase text-gray-500">Value</th>
                <th className="px-6 py-3 text-left text-xs uppercase text-gray-500">Description</th>
                <th className="px-6 py-3 text-left text-xs uppercase text-gray-500">Last Updated</th>
                <th className="px-6 py-3 text-left text-xs uppercase text-gray-500">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {configurations.map((config) => (
                <tr key={config.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4">
                    <span
                      className={`px-2 py-1 text-xs rounded ${
                        config.category === 'GENERAL'
                          ? 'bg-blue-100 text-blue-800'
                          : config.category === 'ACADEMIC'
                          ? 'bg-purple-100 text-purple-800'
                          : 'bg-gray-100 text-gray-800'
                      }`}
                    >
                      {config.category}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm">{config.key}</td>
                  <td className="px-6 py-4 text-sm">{config.value}</td>
                  <td className="px-6 py-4 text-sm text-gray-600">{config.description}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{config.lastUpdated}</td>
                  <td className="px-6 py-4">
                    <div className="flex gap-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => openEditDialog(config)}
                      >
                        <Edit className="w-4 h-4" />
                      </Button>
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => openDeleteDialog(config)}
                        aria-label="Delete configuration"
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

      {configurations.length === 0 && (
        <div className="text-center py-12 text-gray-500 bg-white rounded-lg border border-gray-200">
          No configurations found
        </div>
      )}

      <ConfigurationDialog
        open={dialogOpen}
        onOpenChange={closeDialog}
        onSubmit={
          editingConfig ? (data) => handleUpdate(editingConfig.id, data) : handleCreate
        }
        configuration={editingConfig}
      />

      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Configuration</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete the configuration "{deletingConfig?.key}"? This action
              cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={handleConfirmDelete} className="bg-red-600 hover:bg-red-700">
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
