import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '../ui/dialog';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Textarea } from '../ui/textarea';
import { Alert, AlertDescription } from '../ui/alert';
import { AlertCircle, Loader2 } from 'lucide-react';
import type { Configuration, ConfigurationCreateDto, ConfigurationUpdateDto, ConfigCategory } from '../../types/configuration';

const configurationSchema = z.object({
  category: z.enum(['GENERAL', 'ACADEMIC', 'FINANCE', 'SYSTEM']),
  key: z.string()
    .min(1, 'Key is required')
    .max(100, 'Key cannot exceed 100 characters')
    .regex(/^[A-Z0-9_]+$/, 'Key must contain only uppercase letters, numbers, and underscores'),
  value: z.string()
    .min(1, 'Value is required')
    .max(1000, 'Value cannot exceed 1000 characters'),
  description: z.string()
    .max(500, 'Description cannot exceed 500 characters')
    .optional(),
});

interface ConfigurationDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (config: ConfigurationCreateDto | ConfigurationUpdateDto) => Promise<void>;
  configuration?: Configuration | null;
  mode: 'create' | 'edit';
}

export function ConfigurationDialog({
  open,
  onOpenChange,
  onSubmit,
  configuration,
  mode,
}: ConfigurationDialogProps) {
  const [generalError, setGeneralError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const isEditMode = mode === 'edit' && configuration;

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<any>({
    resolver: zodResolver(configurationSchema),
    defaultValues: {
      category: 'GENERAL',
      key: '',
      value: '',
      description: '',
    },
  });

  const category = watch('category');

  useEffect(() => {
    if (open) {
      setGeneralError(null);
      if (isEditMode) {
        reset({
          category: configuration.category,
          key: configuration.key,
          value: configuration.value,
          description: configuration.description || '',
        });
      } else {
        reset({
          category: 'GENERAL',
          key: '',
          value: '',
          description: '',
        });
      }
    }
  }, [configuration, reset, open, isEditMode]);

  const handleFormSubmit = async (data: any) => {
    setGeneralError(null);
    setIsSubmitting(true);

    try {
      if (isEditMode) {
        // In edit mode, only send value and description
        const updateData: ConfigurationUpdateDto = {
          value: data.value,
          description: data.description || '',
        };
        await onSubmit(updateData);
      } else {
        // In create mode, send all fields
        const createData: ConfigurationCreateDto = {
          category: data.category,
          key: data.key,
          value: data.value,
          description: data.description || '',
        };
        await onSubmit(createData);
      }
      onOpenChange(false);
      reset();
    } catch (error: any) {
      setGeneralError(error.message || 'Failed to save configuration. Please try again.');
    } finally {
      setIsSubmitting(false);
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

        {generalError && (
          <Alert variant="destructive">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{generalError}</AlertDescription>
          </Alert>
        )}

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
          <div>
            <Label htmlFor="category">Category*</Label>
            <Select
              value={category}
              onValueChange={(value) => setValue('category', value as ConfigCategory)}
              disabled={!!isEditMode}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="GENERAL">General</SelectItem>
                <SelectItem value="ACADEMIC">Academic</SelectItem>
                <SelectItem value="FINANCE">Finance</SelectItem>
                <SelectItem value="SYSTEM">System</SelectItem>
              </SelectContent>
            </Select>
            {isEditMode && (
              <p className="text-xs text-gray-500 mt-1">Category cannot be changed</p>
            )}
          </div>

          <div>
            <Label htmlFor="key">Key*</Label>
            <Input
              id="key"
              {...register('key')}
              placeholder="e.g., SCHOOL_NAME"
              disabled={!!isEditMode}
            />
            {errors.key && (
              <span className="text-xs text-red-600">{String(errors.key.message)}</span>
            )}
            {isEditMode && (
              <p className="text-xs text-gray-500 mt-1">Key cannot be changed</p>
            )}
          </div>

          <div>
            <Label htmlFor="value">Value*</Label>
            <Input
              id="value"
              {...register('value')}
              placeholder="Enter configuration value"
            />
            {errors.value && (
              <span className="text-xs text-red-600">{String(errors.value.message)}</span>
            )}
          </div>

          <div>
            <Label htmlFor="description">Description</Label>
            <Textarea
              id="description"
              {...register('description')}
              placeholder="Enter description"
              rows={3}
            />
            {errors.description && (
              <span className="text-xs text-red-600">{String(errors.description.message)}</span>
            )}
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={isSubmitting}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              className="bg-blue-600 hover:bg-blue-700"
              disabled={isSubmitting}
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  {isEditMode ? 'Updating...' : 'Adding...'}
                </>
              ) : (
                <>{isEditMode ? 'Update Configuration' : 'Add Configuration'}</>
              )}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
