import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { X } from 'lucide-react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { configurationSchema } from '@/utils/validation';
import type { Configuration, ConfigurationCreateRequest, ConfigurationUpdateRequest } from '@/types/configuration';
import type { ConfigurationFormData } from '@/utils/validation';

interface ConfigurationDialogProps {
  mode: 'create' | 'edit';
  configuration?: Configuration;
  open: boolean;
  onClose: () => void;
  onSubmit: (data: ConfigurationCreateRequest | ConfigurationUpdateRequest) => Promise<void>;
}

export function ConfigurationDialog({
  mode,
  configuration,
  open,
  onClose,
  onSubmit,
}: ConfigurationDialogProps) {
  const isEditMode = mode === 'edit';

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
    watch,
    setValue,
  } = useForm<ConfigurationFormData>({
    resolver: zodResolver(configurationSchema),
    defaultValues: isEditMode && configuration
      ? {
          category: configuration.category,
          key: configuration.key,
          value: configuration.value,
          description: configuration.description || '',
        }
      : {
          category: 'GENERAL',
          key: '',
          value: '',
          description: '',
        },
  });

  const handleFormSubmit = async (data: ConfigurationFormData) => {
    await onSubmit(data);
    reset();
    onClose();
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  useEffect(() => {
    if (open && configuration && isEditMode) {
      reset({
        category: configuration.category,
        key: configuration.key,
        value: configuration.value,
        description: configuration.description || '',
      });
    } else if (open && !isEditMode) {
      reset({
        category: 'GENERAL',
        key: '',
        value: '',
        description: '',
      });
    }
  }, [open, configuration, isEditMode, reset]);

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <div className="flex items-center justify-between">
            <DialogTitle>{isEditMode ? 'Edit Configuration' : 'Add New Configuration'}</DialogTitle>
            <Button variant="ghost" size="icon" onClick={handleClose}>
              <X className="h-4 w-4" />
            </Button>
          </div>
        </DialogHeader>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
          {/* Category */}
          <div>
            <Label htmlFor="category">
              Category <span className="text-red-500">*</span>
            </Label>
            <Select
              value={watch('category')}
              onValueChange={(value: any) => setValue('category', value)}
            >
              <SelectTrigger>
                <SelectValue placeholder="Select category" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="GENERAL">General</SelectItem>
                <SelectItem value="ACADEMIC">Academic</SelectItem>
                <SelectItem value="FINANCE">Finance</SelectItem>
                <SelectItem value="SYSTEM">System</SelectItem>
              </SelectContent>
            </Select>
            {errors.category && (
              <p className="text-xs text-red-500 mt-1">{errors.category.message}</p>
            )}
          </div>

          {/* Key */}
          <div>
            <Label htmlFor="key">
              Key <span className="text-red-500">*</span>
            </Label>
            <Input
              id="key"
              {...register('key')}
              placeholder="e.g., school_name"
              disabled={isEditMode}
              className={isEditMode ? 'bg-gray-100 cursor-not-allowed' : ''}
            />
            {errors.key && <p className="text-xs text-red-500 mt-1">{errors.key.message}</p>}
          </div>

          {/* Value */}
          <div>
            <Label htmlFor="value">
              Value <span className="text-red-500">*</span>
            </Label>
            <Input id="value" {...register('value')} placeholder="Enter configuration value" />
            {errors.value && <p className="text-xs text-red-500 mt-1">{errors.value.message}</p>}
          </div>

          {/* Description */}
          <div>
            <Label htmlFor="description">Description</Label>
            <Textarea
              id="description"
              {...register('description')}
              placeholder="Enter description"
              rows={3}
            />
            {errors.description && (
              <p className="text-xs text-red-500 mt-1">{errors.description.message}</p>
            )}
          </div>

          {/* Form Actions */}
          <div className="flex justify-end gap-3 pt-4 border-t">
            <Button type="button" variant="outline" onClick={handleClose}>
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Saving...' : isEditMode ? 'Update Configuration' : 'Add Configuration'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
