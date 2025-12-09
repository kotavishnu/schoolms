'use client';

import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { X } from 'lucide-react';
import { configService } from '@/services/configService';
import { createConfigSchema, updateConfigSchema } from '@/lib/validations';
import type { CreateConfigFormData, UpdateConfigFormData } from '@/lib/validations';
import { ConfigurationSetting } from '@/types';
import { LoadingSpinner } from './LoadingSpinner';
import { getErrorMessage, getFieldErrors } from '@/lib/api-client';
import toast from 'react-hot-toast';

interface ConfigFormProps {
  setting: ConfigurationSetting | null;
  onClose: () => void;
  onSuccess: () => void;
}

export function ConfigForm({ setting, onClose, onSuccess }: ConfigFormProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const isEditing = !!setting;

  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm({
    resolver: zodResolver(isEditing ? updateConfigSchema : createConfigSchema),
    defaultValues: isEditing
      ? {
          value: setting.settingValue,
          description: setting.description || '',
        }
      : {
          category: 'General' as const,
          key: '',
          value: '',
          description: '',
        },
  });

  const onSubmit = async (data: CreateConfigFormData | UpdateConfigFormData) => {
    setIsSubmitting(true);
    try {
      if (isEditing && setting) {
        await configService.updateSetting(setting.settingId, data as UpdateConfigFormData);
        toast.success('Setting updated successfully');
      } else {
        await configService.createSetting(data as CreateConfigFormData);
        toast.success('Setting created successfully');
      }
      onSuccess();
    } catch (err) {
      const fieldErrors = getFieldErrors(err);
      if (Object.keys(fieldErrors).length > 0) {
        Object.entries(fieldErrors).forEach(([field, message]) => {
          setError(field as any, { message });
        });
      } else {
        toast.error(getErrorMessage(err));
      }
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="relative w-full max-w-2xl rounded-lg bg-white p-6 shadow-xl">
        <button
          onClick={onClose}
          disabled={isSubmitting}
          className="absolute right-4 top-4 text-gray-400 hover:text-gray-600"
        >
          <X className="h-5 w-5" />
        </button>

        <h2 className="text-2xl font-bold text-gray-900">
          {isEditing ? 'Edit Configuration Setting' : 'Add Configuration Setting'}
        </h2>
        <p className="mt-1 text-sm text-gray-600">
          {isEditing
            ? 'Update the configuration setting values'
            : 'Create a new configuration setting for the system'}
        </p>

        <form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-6">
          {!isEditing && (
            <>
              <div>
                <label htmlFor="category" className="label">
                  Category <span className="text-red-600">*</span>
                </label>
                <select
                  id="category"
                  {...register('category')}
                  className="input-field"
                  disabled={isSubmitting}
                >
                  <option value="General">General</option>
                  <option value="Academic">Academic</option>
                  <option value="Financial">Financial</option>
                </select>
                {errors.category && (
                  <p className="error-text">{errors.category.message}</p>
                )}
              </div>

              <div>
                <label htmlFor="key" className="label">
                  Setting Key <span className="text-red-600">*</span>
                </label>
                <input
                  type="text"
                  id="key"
                  {...register('key')}
                  placeholder="e.g., academic.year.current"
                  className="input-field"
                  disabled={isSubmitting}
                />
                {errors.key && (
                  <p className="error-text">{errors.key.message}</p>
                )}
                <p className="mt-1 text-xs text-gray-500">
                  Use lowercase with dots, underscores, or hyphens
                </p>
              </div>
            </>
          )}

          {isEditing && setting && (
            <div className="card bg-gray-50">
              <div className="space-y-2">
                <div>
                  <span className="text-sm font-medium text-gray-500">Category:</span>
                  <span className="ml-2 text-sm text-gray-900">{setting.category}</span>
                </div>
                <div>
                  <span className="text-sm font-medium text-gray-500">Setting Key:</span>
                  <code className="ml-2 rounded bg-gray-200 px-2 py-1 text-sm font-mono text-gray-800">
                    {setting.settingKey}
                  </code>
                </div>
              </div>
            </div>
          )}

          <div>
            <label htmlFor="value" className="label">
              Setting Value <span className="text-red-600">*</span>
            </label>
            <input
              type="text"
              id="value"
              {...register('value')}
              placeholder="e.g., 2025-2026"
              className="input-field"
              disabled={isSubmitting}
            />
            {errors.value && (
              <p className="error-text">{errors.value.message}</p>
            )}
          </div>

          <div>
            <label htmlFor="description" className="label">
              Description
            </label>
            <textarea
              id="description"
              {...register('description')}
              rows={3}
              placeholder="Optional description for this setting"
              className="input-field"
              disabled={isSubmitting}
            />
            {errors.description && (
              <p className="error-text">{errors.description.message}</p>
            )}
          </div>

          <div className="flex justify-end gap-3 border-t pt-6">
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="btn-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="btn-primary flex items-center gap-2"
            >
              {isSubmitting && <LoadingSpinner size="sm" />}
              {isSubmitting
                ? isEditing
                  ? 'Updating...'
                  : 'Creating...'
                : isEditing
                ? 'Update Setting'
                : 'Create Setting'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
