'use client';

import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Plus, Edit, Trash2, Settings as SettingsIcon } from 'lucide-react';
import { configService } from '@/services/configService';
import { PageLoadingSpinner } from '@/components/LoadingSpinner';
import { ErrorMessage } from '@/components/ErrorMessage';
import { ConfirmDialog } from '@/components/ConfirmDialog';
import { ConfigurationSetting, SettingCategory } from '@/types';
import { getErrorMessage } from '@/lib/api-client';
import toast from 'react-hot-toast';
import { ConfigForm } from '@/components/ConfigForm';

export default function SettingsPage() {
  const [selectedCategory, setSelectedCategory] = useState<SettingCategory | ''>('');
  const [editingSetting, setEditingSetting] = useState<ConfigurationSetting | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const [showCreateForm, setShowCreateForm] = useState(false);

  const { data: settings, isLoading, error, refetch } = useQuery({
    queryKey: ['settings', selectedCategory],
    queryFn: () => configService.getSettings(selectedCategory || undefined),
  });

  const handleDelete = async () => {
    if (!deletingId) return;

    setIsDeleting(true);
    try {
      await configService.deleteSetting(deletingId);
      toast.success('Setting deleted successfully');
      setDeletingId(null);
      refetch();
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setIsDeleting(false);
    }
  };

  const groupedSettings = React.useMemo(() => {
    if (!settings) return {};

    return settings.reduce((acc, setting) => {
      if (!acc[setting.category]) {
        acc[setting.category] = [];
      }
      acc[setting.category].push(setting);
      return acc;
    }, {} as Record<SettingCategory, ConfigurationSetting[]>);
  }, [settings]);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Configuration Settings</h1>
          <p className="mt-1 text-sm text-gray-600">
            Manage school configuration and system settings
          </p>
        </div>
        <button
          onClick={() => {
            setEditingSetting(null);
            setShowCreateForm(true);
          }}
          className="btn-primary flex items-center gap-2"
        >
          <Plus className="h-4 w-4" />
          Add Setting
        </button>
      </div>

      <div className="card">
        <label htmlFor="category" className="label">
          Filter by Category
        </label>
        <select
          id="category"
          value={selectedCategory}
          onChange={(e) => setSelectedCategory(e.target.value as any)}
          className="input-field"
        >
          <option value="">All Categories</option>
          <option value="General">General</option>
          <option value="Academic">Academic</option>
          <option value="Financial">Financial</option>
        </select>
      </div>

      {isLoading ? (
        <PageLoadingSpinner />
      ) : error ? (
        <ErrorMessage message={getErrorMessage(error)} onRetry={() => refetch()} />
      ) : !settings || settings.length === 0 ? (
        <div className="card text-center py-12">
          <SettingsIcon className="mx-auto h-12 w-12 text-gray-400" />
          <p className="mt-2 text-gray-500">No configuration settings found</p>
          <button
            onClick={() => setShowCreateForm(true)}
            className="btn-primary mt-4 inline-flex items-center gap-2"
          >
            <Plus className="h-4 w-4" />
            Add First Setting
          </button>
        </div>
      ) : (
        <div className="space-y-6">
          {(Object.entries(groupedSettings) as [SettingCategory, ConfigurationSetting[]][]).map(([category, categorySettings]) => (
            <div key={category} className="card">
              <h2 className="mb-4 text-lg font-semibold text-gray-900">{category}</h2>
              <div className="space-y-4">
                {categorySettings.map((setting) => (
                  <div
                    key={setting.settingId}
                    className="flex items-start justify-between border-b border-gray-100 pb-4 last:border-0 last:pb-0"
                  >
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <code className="rounded bg-gray-100 px-2 py-1 text-sm font-mono text-gray-800">
                          {setting.settingKey}
                        </code>
                      </div>
                      <p className="mt-1 text-sm text-gray-900 font-medium">
                        {setting.settingValue}
                      </p>
                      {setting.description && (
                        <p className="mt-1 text-sm text-gray-600">{setting.description}</p>
                      )}
                      <p className="mt-1 text-xs text-gray-500">
                        Last updated: {new Date(setting.updatedAt).toLocaleDateString()}
                      </p>
                    </div>
                    <div className="flex items-center gap-2 ml-4">
                      <button
                        onClick={() => {
                          setEditingSetting(setting);
                          setShowCreateForm(true);
                        }}
                        className="text-blue-600 hover:text-blue-900"
                        title="Edit"
                      >
                        <Edit className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => setDeletingId(setting.settingId)}
                        className="text-red-600 hover:text-red-900"
                        title="Delete"
                      >
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      {showCreateForm && (
        <ConfigForm
          setting={editingSetting}
          onClose={() => {
            setShowCreateForm(false);
            setEditingSetting(null);
          }}
          onSuccess={() => {
            setShowCreateForm(false);
            setEditingSetting(null);
            refetch();
          }}
        />
      )}

      <ConfirmDialog
        isOpen={deletingId !== null}
        onClose={() => setDeletingId(null)}
        onConfirm={handleDelete}
        title="Delete Setting"
        message="Are you sure you want to delete this configuration setting? This action cannot be undone."
        confirmText="Delete"
        variant="danger"
        isLoading={isDeleting}
      />
    </div>
  );
}
