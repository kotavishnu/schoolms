import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { configApi } from '@/api/configApi';
import { ConfigSetting } from '@/types/config';
import { Button } from '@/components/ui/Button';
import { ConfirmDialog } from '@/components/ui/Dialog';
import { useToast } from '@/components/ui/Toast';
import { formatDate } from '@/lib/utils';
import { SettingFormDialog } from './SettingFormDialog';

interface SettingsTableProps {
  settings: ConfigSetting[];
}

export function SettingsTable({ settings }: SettingsTableProps) {
  const { showToast } = useToast();
  const queryClient = useQueryClient();
  const [editingSetting, setEditingSetting] = useState<ConfigSetting | null>(null);
  const [deletingSetting, setDeletingSetting] = useState<ConfigSetting | null>(null);

  const deleteMutation = useMutation({
    mutationFn: (settingId: number) => configApi.deleteSetting(settingId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['config-settings'] });
      showToast({
        type: 'success',
        title: 'Success',
        description: 'Setting deleted successfully',
      });
      setDeletingSetting(null);
    },
    onError: (error: any) => {
      showToast({
        type: 'error',
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to delete setting',
      });
    },
  });

  const handleDelete = () => {
    if (deletingSetting) {
      deleteMutation.mutate(deletingSetting.settingId);
    }
  };

  if (settings.length === 0) {
    return (
      <div className="text-center py-12 text-gray-500">
        No settings found in this category. Click "Add Setting" to create one.
      </div>
    );
  }

  return (
    <>
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Key
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Value
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Description
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Last Updated
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Updated By
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {settings.map((setting) => (
              <tr key={setting.settingId} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap">
                  <code className="text-sm font-mono text-blue-600 bg-blue-50 px-2 py-1 rounded">
                    {setting.key}
                  </code>
                </td>
                <td className="px-6 py-4">
                  <div className="text-sm text-gray-900 max-w-xs truncate" title={setting.value}>
                    {setting.value}
                  </div>
                </td>
                <td className="px-6 py-4">
                  <div className="text-sm text-gray-500 max-w-xs truncate" title={setting.description}>
                    {setting.description || '-'}
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {formatDate(setting.updatedAt)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {setting.updatedBy}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => setEditingSetting(setting)}
                  >
                    Edit
                  </Button>
                  <Button
                    size="sm"
                    variant="destructive"
                    onClick={() => setDeletingSetting(setting)}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {editingSetting && (
        <SettingFormDialog
          open={!!editingSetting}
          onOpenChange={(open) => !open && setEditingSetting(null)}
          setting={editingSetting}
        />
      )}

      <ConfirmDialog
        open={!!deletingSetting}
        onOpenChange={(open) => !open && setDeletingSetting(null)}
        title="Delete Setting"
        description={`Are you sure you want to delete "${deletingSetting?.key}"? This action cannot be undone.`}
        onConfirm={handleDelete}
        isLoading={deleteMutation.isPending}
        confirmText="Delete"
        variant="destructive"
      />
    </>
  );
}
