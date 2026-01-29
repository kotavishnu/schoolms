import { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { toast } from 'sonner';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from './ui/dialog';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Textarea } from './ui/textarea';
import { Checkbox } from './ui/checkbox';
import { configApi, type Configuration } from '../services/api/configApi';
import { configurationSchema, configurationUpdateSchema } from '../services/validation/configSchema';

interface ConfigurationDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess: () => void;
  configuration?: Configuration | null;
}

export function ConfigurationDialog({
  open,
  onOpenChange,
  onSuccess,
  configuration,
}: ConfigurationDialogProps) {
  const [loading, setLoading] = useState(false);
  const isEditMode = !!configuration;

  const {
    register,
    handleSubmit,
    reset,
    control,
    formState: { errors },
  } = useForm<any>({
    resolver: zodResolver(isEditMode ? configurationUpdateSchema : configurationSchema),
    defaultValues: {
      category: 'GENERAL',
      key: '',
      value: '',
      description: '',
      dataType: 'STRING',
      isEncrypted: false,
    },
  });

  useEffect(() => {
    if (configuration) {
      reset({
        category: configuration.category,
        key: configuration.key,
        value: configuration.value,
        description: configuration.description || '',
        dataType: configuration.dataType,
        isEncrypted: configuration.isEncrypted,
      });
    } else {
      reset({
        category: 'GENERAL',
        key: '',
        value: '',
        description: '',
        dataType: 'STRING',
        isEncrypted: false,
      });
    }
  }, [configuration, reset]);

  const handleFormSubmit = async (data: any) => {
    setLoading(true);
    try {
      // Use configuration's category/key in edit mode, form data in create mode
      const category = isEditMode ? configuration!.category : data.category;
      const key = isEditMode ? configuration!.key : data.key;

      await configApi.upsertConfiguration(
        category,
        key,
        {
          value: data.value,
          description: data.description || undefined,
          dataType: data.dataType,
          isEncrypted: data.isEncrypted,
        },
        isEditMode ? configuration!.version : undefined
      );

      toast.success(isEditMode ? 'Configuration updated successfully' : 'Configuration created successfully');
      onSuccess();
      onOpenChange(false);
      reset();
    } catch (error: any) {
      if (error.response?.status === 400) {
        const errors = error.response.data.errors || [];
        if (errors.length > 0) {
          errors.forEach((err: any) => toast.error(err.message || err));
        } else if (error.response.data.detail) {
          toast.error(error.response.data.detail);
        } else {
          toast.error('Invalid input. Please check your data.');
        }
      } else {
        toast.error(isEditMode ? 'Failed to update configuration' : 'Failed to create configuration');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>
            {isEditMode ? 'Edit Configuration' : 'Add New Configuration'}
          </DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
          <div>
            <Label htmlFor="category">Category *</Label>
            <Controller
              name="category"
              control={control}
              render={({ field }) => (
                <Select
                  value={field.value}
                  onValueChange={field.onChange}
                  disabled={isEditMode}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="GENERAL">General</SelectItem>
                    <SelectItem value="ACADEMIC">Academic</SelectItem>
                    <SelectItem value="FINANCIAL">Financial</SelectItem>
                  </SelectContent>
                </Select>
              )}
            />
            {errors.category && (
              <span className="text-xs text-red-600">{errors.category.message as string}</span>
            )}
            {isEditMode && (
              <span className="text-xs text-gray-500">Category cannot be changed</span>
            )}
          </div>

          <div>
            <Label htmlFor="key">Key *</Label>
            <Input
              id="key"
              {...register('key')}
              placeholder="e.g., SCHOOL_NAME, MAX_STUDENTS_PER_CLASS"
              disabled={isEditMode}
              className={isEditMode ? 'bg-gray-100 dark:bg-gray-800 cursor-not-allowed' : ''}
            />
            {errors.key && (
              <span className="text-xs text-red-600">{errors.key.message as string}</span>
            )}
            {isEditMode && (
              <span className="text-xs text-gray-500">Key cannot be changed</span>
            )}
            {!isEditMode && (
              <span className="text-xs text-gray-500">Use UPPERCASE with underscores (e.g., SCHOOL_NAME)</span>
            )}
          </div>

          <div>
            <Label htmlFor="value">Value *</Label>
            <Textarea
              id="value"
              {...register('value')}
              placeholder="Enter configuration value"
              rows={3}
            />
            {errors.value && (
              <span className="text-xs text-red-600">{errors.value.message as string}</span>
            )}
          </div>

          <div>
            <Label htmlFor="description">Description</Label>
            <Textarea
              id="description"
              {...register('description')}
              placeholder="Brief description of this configuration"
              rows={2}
            />
            {errors.description && (
              <span className="text-xs text-red-600">{errors.description.message as string}</span>
            )}
          </div>

          <div>
            <Label htmlFor="dataType">Data Type *</Label>
            <Controller
              name="dataType"
              control={control}
              render={({ field }) => (
                <Select value={field.value} onValueChange={field.onChange}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="STRING">String</SelectItem>
                    <SelectItem value="NUMBER">Number</SelectItem>
                    <SelectItem value="BOOLEAN">Boolean</SelectItem>
                    <SelectItem value="JSON">JSON</SelectItem>
                  </SelectContent>
                </Select>
              )}
            />
            {errors.dataType && (
              <span className="text-xs text-red-600">{errors.dataType.message as string}</span>
            )}
          </div>

          <div className="flex items-center space-x-2">
            <Controller
              name="isEncrypted"
              control={control}
              render={({ field }) => (
                <Checkbox
                  id="isEncrypted"
                  checked={field.value}
                  onCheckedChange={field.onChange}
                />
              )}
            />
            <Label htmlFor="isEncrypted" className="text-sm font-normal cursor-pointer">
              Encrypt this value (for sensitive data like passwords, API keys)
            </Label>
          </div>

          <div className="flex justify-end gap-2 pt-4 border-t">
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={loading}
            >
              Cancel
            </Button>
            <Button type="submit" className="bg-blue-600 hover:bg-blue-700" disabled={loading}>
              {loading ? 'Saving...' : (isEditMode ? 'Update Configuration' : 'Add Configuration')}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
