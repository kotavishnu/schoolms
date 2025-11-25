import React, { useState } from 'react';
import { useConfigurations } from '../hooks/useConfigurations';
import { useCreateConfiguration } from '../hooks/useCreateConfiguration';
import { useUpdateConfiguration } from '../hooks/useUpdateConfiguration';
import { useDeleteConfiguration } from '../hooks/useDeleteConfiguration';
import { type Configuration, type CreateConfigRequest, type UpdateConfigRequest } from '@/types/config.types';
import { type ConfigFormData } from '@/schemas/configSchema';
import { formatDate } from '@/utils/dateUtils';
import Table, { type Column } from '@/components/common/Table';
import Button from '@/components/common/Button';
import Modal from '@/components/common/Modal';
import ConfirmDialog from '@/components/common/ConfirmDialog';
import ConfigurationForm from './ConfigurationForm';
import LoadingSpinner from '@/components/common/LoadingSpinner';

const ConfigurationList: React.FC = () => {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [editConfig, setEditConfig] = useState<Configuration | null>(null);
  const [deleteId, setDeleteId] = useState<number | null>(null);

  const { data, isLoading, isError, error } = useConfigurations();
  const createMutation = useCreateConfiguration();
  const updateMutation = useUpdateConfiguration(editConfig?.id || 0);
  const deleteMutation = useDeleteConfiguration();

  const handleCreate = (formData: ConfigFormData) => {
    const request: CreateConfigRequest = {
      category: formData.category,
      configKey: formData.configKey,
      configValue: formData.configValue,
      description: formData.description,
    };

    createMutation.mutate(request, {
      onSuccess: () => {
        setIsCreateModalOpen(false);
      },
    });
  };

  const handleUpdate = (formData: ConfigFormData) => {
    if (!editConfig) return;

    const request: UpdateConfigRequest = {
      category: formData.category,
      configKey: formData.configKey,
      configValue: formData.configValue,
      description: formData.description,
      version: editConfig.version,
    };

    updateMutation.mutate(request, {
      onSuccess: () => {
        setEditConfig(null);
      },
    });
  };

  const handleDeleteConfirm = () => {
    if (deleteId) {
      deleteMutation.mutate(deleteId, {
        onSuccess: () => {
          setDeleteId(null);
        },
      });
    }
  };

  const columns: Column<Configuration>[] = [
    {
      key: 'category',
      header: 'Category',
      render: (config) => (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400">
          {config.category}
        </span>
      ),
    },
    {
      key: 'configKey',
      header: 'Key',
      render: (config) => (
        <span className="font-mono text-sm font-medium">{config.configKey}</span>
      ),
    },
    {
      key: 'configValue',
      header: 'Value',
      render: (config) => (
        <span className="font-mono text-sm">{config.configValue}</span>
      ),
    },
    {
      key: 'description',
      header: 'Description',
      render: (config) => (
        <span className="text-sm text-gray-600 dark:text-gray-400">
          {config.description || '-'}
        </span>
      ),
    },
    {
      key: 'updatedAt',
      header: 'Last Updated',
      render: (config) => (
        <span className="text-sm text-gray-600 dark:text-gray-400">
          {formatDate(config.updatedAt)}
        </span>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (config) => (
        <div className="flex gap-2">
          <Button
            variant="secondary"
            onClick={() => setEditConfig(config)}
            className="text-xs px-2 py-1"
          >
            Edit
          </Button>
          <Button
            variant="danger"
            onClick={() => setDeleteId(config.id)}
            className="text-xs px-2 py-1"
          >
            Delete
          </Button>
        </div>
      ),
    },
  ];

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-96">
        <LoadingSpinner message="Loading configurations..." />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="text-center py-12">
        <div className="text-red-600 dark:text-red-400 mb-4">
          <p className="text-lg font-semibold">Failed to load configurations</p>
          <p className="text-sm mt-2">{(error as any)?.message || 'An error occurred'}</p>
        </div>
        <Button variant="primary" onClick={() => window.location.reload()}>
          Retry
        </Button>
      </div>
    );
  }

  const configurations = data?.content || [];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Configurations</h1>
          <p className="text-gray-600 dark:text-gray-400 mt-1">
            {data?.totalElements || 0} total configurations
          </p>
        </div>
        <Button variant="primary" onClick={() => setIsCreateModalOpen(true)}>
          Add New Configuration
        </Button>
      </div>

      {/* Configuration Table */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md overflow-hidden">
        <Table
          columns={columns}
          data={configurations}
          emptyMessage="No configurations found. Add your first configuration to get started."
        />
      </div>

      {/* Create Modal */}
      <Modal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        title="Create New Configuration"
        size="md"
      >
        <ConfigurationForm
          onSubmit={handleCreate}
          onCancel={() => setIsCreateModalOpen(false)}
          isLoading={createMutation.isPending}
        />
      </Modal>

      {/* Edit Modal */}
      {editConfig && (
        <Modal
          isOpen={!!editConfig}
          onClose={() => setEditConfig(null)}
          title="Edit Configuration"
          size="md"
        >
          <ConfigurationForm
            initialData={{
              category: editConfig.category,
              configKey: editConfig.configKey,
              configValue: editConfig.configValue,
              description: editConfig.description,
            }}
            onSubmit={handleUpdate}
            onCancel={() => setEditConfig(null)}
            isLoading={updateMutation.isPending}
          />
        </Modal>
      )}

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={deleteId !== null}
        title="Delete Configuration"
        message="Are you sure you want to delete this configuration? This action cannot be undone."
        confirmText="Delete"
        cancelText="Cancel"
        type="danger"
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteId(null)}
        loading={deleteMutation.isPending}
      />
    </div>
  );
};

export default ConfigurationList;
