import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { configSchema, type ConfigFormData } from '@/schemas/configSchema';
import { CATEGORY_OPTIONS } from '@/utils/constants';
import Input from '@/components/common/Input';
import Select from '@/components/common/Select';
import Button from '@/components/common/Button';

interface ConfigurationFormProps {
  initialData?: Partial<ConfigFormData>;
  onSubmit: (data: ConfigFormData) => void;
  isLoading?: boolean;
  onCancel: () => void;
}

const ConfigurationForm: React.FC<ConfigurationFormProps> = ({
  initialData,
  onSubmit,
  isLoading = false,
  onCancel,
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ConfigFormData>({
    resolver: zodResolver(configSchema),
    defaultValues: initialData,
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <Select
        label="Category"
        {...register('category')}
        options={[...CATEGORY_OPTIONS]}
        error={errors.category?.message}
        required
      />

      <Input
        label="Configuration Key"
        {...register('configKey')}
        placeholder="e.g., SCHOOL_NAME, MAX_CLASS_SIZE"
        error={errors.configKey?.message}
        required
      />

      <Input
        label="Configuration Value"
        {...register('configValue')}
        placeholder="e.g., ABC School, 30"
        error={errors.configValue?.message}
        required
      />

      <Input
        label="Description"
        {...register('description')}
        placeholder="Optional description"
        error={errors.description?.message}
        multiline
        rows={3}
      />

      <div className="flex justify-end gap-3 pt-4">
        <Button type="button" variant="secondary" onClick={onCancel} disabled={isLoading}>
          Cancel
        </Button>
        <Button type="submit" variant="primary" isLoading={isLoading} disabled={isLoading}>
          {initialData ? 'Update Configuration' : 'Create Configuration'}
        </Button>
      </div>
    </form>
  );
};

export default ConfigurationForm;
