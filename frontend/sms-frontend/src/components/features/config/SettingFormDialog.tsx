import { useState, useEffect } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { configApi } from '@/api/configApi';
import {
  ConfigSetting,
  ConfigCategory,
  CreateSettingRequest,
  UpdateSettingRequest,
} from '@/types/config';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/Dialog';
import { Input } from '@/components/ui/Input';
import { Button } from '@/components/ui/Button';
import { useToast } from '@/components/ui/Toast';

interface SettingFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  setting?: ConfigSetting;
  category?: ConfigCategory;
}

export function SettingFormDialog({
  open,
  onOpenChange,
  setting,
  category,
}: SettingFormDialogProps) {
  const { showToast } = useToast();
  const queryClient = useQueryClient();
  const isEdit = !!setting;

  const [formData, setFormData] = useState({
    category: category || ('GENERAL' as ConfigCategory),
    key: '',
    value: '',
    description: '',
  });

  useEffect(() => {
    if (setting) {
      setFormData({
        category: setting.category,
        key: setting.key,
        value: setting.value,
        description: setting.description || '',
      });
    } else {
      setFormData({
        category: category || 'GENERAL',
        key: '',
        value: '',
        description: '',
      });
    }
  }, [setting, category, open]);

  const createMutation = useMutation({
    mutationFn: (data: CreateSettingRequest) => configApi.createSetting(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['config-settings'] });
      showToast({
        type: 'success',
        title: 'Success',
        description: 'Configuration setting created successfully',
      });
      onOpenChange(false);
    },
    onError: (error: any) => {
      showToast({
        type: 'error',
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to create setting',
      });
    },
  });

  const updateMutation = useMutation({
    mutationFn: (data: UpdateSettingRequest & { settingId: number }) =>
      configApi.updateSetting(data.settingId, {
        value: data.value,
        description: data.description,
        version: data.version,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['config-settings'] });
      showToast({
        type: 'success',
        title: 'Success',
        description: 'Configuration setting updated successfully',
      });
      onOpenChange(false);
    },
    onError: (error: any) => {
      showToast({
        type: 'error',
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to update setting',
      });
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (isEdit && setting) {
      updateMutation.mutate({
        settingId: setting.settingId,
        value: formData.value,
        description: formData.description,
        version: setting.version || 0,
      });
    } else {
      createMutation.mutate({
        category: formData.category,
        key: formData.key,
        value: formData.value,
        description: formData.description,
      });
    }
  };

  const handleChange = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const isLoading = createMutation.isPending || updateMutation.isPending;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{isEdit ? 'Edit Setting' : 'Add New Setting'}</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Category <span className="text-red-500">*</span>
            </label>
            <select
              value={formData.category}
              onChange={(e) => handleChange('category', e.target.value)}
              disabled={isEdit}
              required
              className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm disabled:opacity-50"
            >
              <option value="GENERAL">General</option>
              <option value="ACADEMIC">Academic</option>
              <option value="FINANCIAL">Financial</option>
            </select>
          </div>

          <Input
            label="Key"
            value={formData.key}
            onChange={(e) => handleChange('key', e.target.value.toUpperCase())}
            placeholder="SETTING_KEY"
            disabled={isEdit}
            required
            pattern="^[A-Z][A-Z0-9_]*$"
            title="Use UPPERCASE letters, numbers, and underscores only. Must start with a letter."
          />

          <Input
            label="Value"
            value={formData.value}
            onChange={(e) => handleChange('value', e.target.value)}
            placeholder="Setting value"
            required
          />

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Description
            </label>
            <textarea
              value={formData.description}
              onChange={(e) => handleChange('description', e.target.value)}
              placeholder="Optional description"
              rows={3}
              maxLength={500}
              className="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
            />
            <p className="mt-1 text-xs text-gray-500">
              {formData.description.length}/500 characters
            </p>
          </div>

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={isLoading}
            >
              Cancel
            </Button>
            <Button type="submit" isLoading={isLoading}>
              {isEdit ? 'Update' : 'Create'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
