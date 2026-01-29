import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from './ui/dialog';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Textarea } from './ui/textarea';
import type { Configuration } from '../types';

interface ConfigurationDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (config: Omit<Configuration, 'id'>) => void;
  configuration?: Configuration | null;
}

export function ConfigurationDialog({
  open,
  onOpenChange,
  onSubmit,
  configuration,
}: ConfigurationDialogProps) {
  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<Omit<Configuration, 'id'>>({
    defaultValues: {
      category: 'GENERAL',
      key: '',
      value: '',
      description: '',
      lastUpdated: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
    },
  });

  const category = watch('category');

  useEffect(() => {
    if (configuration) {
      reset(configuration);
    } else {
      reset({
        category: 'GENERAL',
        key: '',
        value: '',
        description: '',
        lastUpdated: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
      });
    }
  }, [configuration, reset]);

  const handleFormSubmit = (data: Omit<Configuration, 'id'>) => {
    onSubmit({
      ...data,
      lastUpdated: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
    });
    onOpenChange(false);
    reset();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>
            {configuration ? 'Edit Configuration' : 'Add New Configuration'}
          </DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
          <div>
            <Label htmlFor="category">Category*</Label>
            <Select value={category} onValueChange={(value) => setValue('category', value)}>
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
          </div>

          <div>
            <Label htmlFor="key">Key*</Label>
            <Input
              id="key"
              {...register('key', { required: 'Key is required' })}
              placeholder="e.g., school_name"
            />
            {errors.key && <span className="text-xs text-red-600">{errors.key.message}</span>}
          </div>

          <div>
            <Label htmlFor="value">Value*</Label>
            <Input
              id="value"
              {...register('value', { required: 'Value is required' })}
              placeholder="Enter configuration value"
            />
            {errors.value && <span className="text-xs text-red-600">{errors.value.message}</span>}
          </div>

          <div>
            <Label htmlFor="description">Description*</Label>
            <Textarea
              id="description"
              {...register('description', { required: 'Description is required' })}
              placeholder="Enter description"
              rows={3}
            />
            {errors.description && (
              <span className="text-xs text-red-600">{errors.description.message}</span>
            )}
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" className="bg-blue-600 hover:bg-blue-700">
              {configuration ? 'Update Configuration' : 'Add Configuration'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
