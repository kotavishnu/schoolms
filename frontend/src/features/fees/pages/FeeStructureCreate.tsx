/**
 * Fee Structure Creation Page
 *
 * Features:
 * - Create new fee structure with multiple components
 * - Select academic year and applicable classes
 * - Configure due dates and late fees
 * - Form validation with Zod
 * - Calculate total amount automatically
 */
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm, useFieldArray, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Plus, Trash2, ArrowLeft } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Input } from '@/shared/components/ui/Input';
import { Select } from '@/shared/components/ui/Select';
import { FormField } from '@/shared/components/form/FormField';
import {
  useCreateFeeStructure,
  useAcademicYears,
  useAvailableClasses,
} from '../hooks/useFees';
import {
  feeStructureFormSchema,
  getDefaultFeeComponent,
  getDefaultDueDateConfig,
  type FeeStructureFormInput,
} from '../schemas/fee.schema';

export const FeeStructureCreate = () => {
  const navigate = useNavigate();

  // Fetch academic years and classes
  const { data: academicYears } = useAcademicYears();
  const { data: classes } = useAvailableClasses();

  // Create mutation
  const { mutate: createFeeStructure, isPending } = useCreateFeeStructure();

  // Form setup
  const {
    control,
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<FeeStructureFormInput>({
    resolver: zodResolver(feeStructureFormSchema),
    defaultValues: {
      structureName: '',
      academicYearCode: '',
      frequency: 'MONTHLY',
      applicableClasses: [],
      effectiveFrom: new Date().toISOString().split('T')[0],
      effectiveTo: undefined,
      description: '',
      components: [getDefaultFeeComponent()],
      dueDateConfig: getDefaultDueDateConfig(),
    },
  });

  // Fee components field array
  const { fields, append, remove } = useFieldArray({
    control,
    name: 'components',
  });

  // Watch components for total calculation
  const components = watch('components');

  // Calculate total amount
  const totalAmount = components.reduce((sum, component) => sum + (Number(component.amount) || 0), 0);

  /**
   * Handle form submission
   */
  const onSubmit = (data: FeeStructureFormInput) => {
    createFeeStructure(data, {
      onSuccess: () => {
        navigate('/fees/structures');
      },
    });
  };

  /**
   * Handle cancel with confirmation if form is dirty
   */
  const handleCancel = () => {
    navigate('/fees/structures');
  };

  /**
   * Format currency
   */
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 2,
    }).format(amount);
  };

  return (
    <div className="container mx-auto px-4 py-8 max-w-5xl">
      <div className="mb-6">
        <Button variant="ghost" onClick={handleCancel} className="mb-4">
          <ArrowLeft className="w-4 h-4 mr-2" />
          Back to Fee Structures
        </Button>
        <h1 className="text-3xl font-bold text-gray-900">Create Fee Structure</h1>
        <p className="text-gray-600 mt-1">Set up a new fee structure for students</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        {/* Basic Information */}
        <Card>
          <CardHeader>
            <CardTitle>Basic Information</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <FormField
              label="Structure Name"
              error={errors.structureName?.message}
              required
            >
              <Input
                {...register('structureName')}
                placeholder="e.g., Grade 1-3 Annual Fee 2025-2026"
              />
            </FormField>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <FormField
                label="Academic Year"
                error={errors.academicYearCode?.message}
                required
              >
                <Select {...register('academicYearCode')}>
                  <option value="">Select Academic Year</option>
                  {academicYears?.map((year) => (
                    <option key={year.academicYearId} value={year.yearCode}>
                      {year.yearCode} {year.isCurrent ? '(Current)' : ''}
                    </option>
                  ))}
                </Select>
              </FormField>

              <FormField
                label="Frequency"
                error={errors.frequency?.message}
                required
              >
                <Select {...register('frequency')}>
                  <option value="MONTHLY">Monthly</option>
                  <option value="QUARTERLY">Quarterly</option>
                  <option value="ANNUAL">Annual</option>
                  <option value="ONE_TIME">One Time</option>
                </Select>
              </FormField>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <FormField
                label="Effective From"
                error={errors.effectiveFrom?.message}
                required
              >
                <Input type="date" {...register('effectiveFrom')} />
              </FormField>

              <FormField
                label="Effective To (Optional)"
                error={errors.effectiveTo?.message}
              >
                <Input type="date" {...register('effectiveTo')} />
              </FormField>
            </div>

            <FormField
              label="Description (Optional)"
              error={errors.description?.message}
            >
              <textarea
                {...register('description')}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                rows={3}
                placeholder="Add any additional notes about this fee structure"
              />
            </FormField>
          </CardContent>
        </Card>

        {/* Fee Components */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>Fee Components</CardTitle>
            <Button
              type="button"
              variant="secondary"
              size="sm"
              onClick={() => append(getDefaultFeeComponent())}
            >
              <Plus className="w-4 h-4 mr-2" />
              Add Component
            </Button>
          </CardHeader>
          <CardContent className="space-y-4">
            {fields.map((field, index) => (
              <div key={field.id} className="border rounded-lg p-4 bg-gray-50">
                <div className="flex justify-between items-center mb-4">
                  <h4 className="font-medium text-gray-900">Component {index + 1}</h4>
                  {fields.length > 1 && (
                    <Button
                      type="button"
                      variant="danger"
                      size="sm"
                      onClick={() => remove(index)}
                    >
                      <Trash2 className="w-4 h-4 mr-2" />
                      Remove
                    </Button>
                  )}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <FormField
                    label="Fee Type"
                    error={errors.components?.[index]?.feeType?.message}
                    required
                  >
                    <Select {...register(`components.${index}.feeType`)}>
                      <option value="TUITION">Tuition</option>
                      <option value="LIBRARY">Library</option>
                      <option value="COMPUTER">Computer</option>
                      <option value="SPORTS">Sports</option>
                      <option value="TRANSPORT">Transport</option>
                      <option value="LAB">Lab</option>
                      <option value="OTHER">Other</option>
                    </Select>
                  </FormField>

                  <FormField
                    label="Fee Name"
                    error={errors.components?.[index]?.feeName?.message}
                    required
                  >
                    <Input
                      {...register(`components.${index}.feeName`)}
                      placeholder="e.g., Monthly Tuition Fee"
                    />
                  </FormField>

                  <FormField
                    label="Amount (INR)"
                    error={errors.components?.[index]?.amount?.message}
                    required
                  >
                    <Input
                      type="number"
                      step="0.01"
                      {...register(`components.${index}.amount`, { valueAsNumber: true })}
                      placeholder="0.00"
                    />
                  </FormField>

                  <FormField
                    label="Description (Optional)"
                    error={errors.components?.[index]?.description?.message}
                  >
                    <Input
                      {...register(`components.${index}.description`)}
                      placeholder="Additional details"
                    />
                  </FormField>
                </div>
              </div>
            ))}

            {/* Total Amount Display */}
            <div className="flex justify-end items-center border-t pt-4 mt-4">
              <div className="text-right">
                <p className="text-sm text-gray-600">Total Amount:</p>
                <p className="text-2xl font-bold text-gray-900">{formatCurrency(totalAmount)}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Applicable Classes */}
        <Card>
          <CardHeader>
            <CardTitle>Applicable Classes</CardTitle>
          </CardHeader>
          <CardContent>
            <FormField
              label="Select Classes"
              error={errors.applicableClasses?.message}
              required
              helperText="Select one or more classes for this fee structure"
            >
              <Controller
                name="applicableClasses"
                control={control}
                render={({ field }) => (
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-2 max-h-64 overflow-y-auto border rounded-lg p-4">
                    {classes?.map((classOption) => (
                      <label
                        key={classOption.classId}
                        className="flex items-center space-x-2 cursor-pointer hover:bg-gray-50 p-2 rounded"
                      >
                        <input
                          type="checkbox"
                          value={String(classOption.classId)}
                          checked={field.value.includes(String(classOption.classId))}
                          onChange={(e) => {
                            const value = e.target.value;
                            const newValue = e.target.checked
                              ? [...field.value, value]
                              : field.value.filter((v) => v !== value);
                            field.onChange(newValue);
                          }}
                          className="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
                        />
                        <span className="text-sm text-gray-700">
                          {classOption.className} {classOption.section}
                        </span>
                      </label>
                    ))}
                  </div>
                )}
              />
            </FormField>
          </CardContent>
        </Card>

        {/* Due Date Configuration */}
        <Card>
          <CardHeader>
            <CardTitle>Due Date & Late Fee Configuration</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <FormField
                label="Due Day of Month"
                error={errors.dueDateConfig?.dueDay?.message}
                required
                helperText="Day of the month (1-31)"
              >
                <Input
                  type="number"
                  min="1"
                  max="31"
                  {...register('dueDateConfig.dueDay', { valueAsNumber: true })}
                />
              </FormField>

              <FormField
                label="Grace Period (Days)"
                error={errors.dueDateConfig?.gracePeriodDays?.message}
                required
              >
                <Input
                  type="number"
                  min="0"
                  max="30"
                  {...register('dueDateConfig.gracePeriodDays', { valueAsNumber: true })}
                />
              </FormField>

              <FormField
                label="Late Fee Amount (INR)"
                error={errors.dueDateConfig?.lateFeeAmount?.message}
                required
              >
                <Input
                  type="number"
                  step="0.01"
                  min="0"
                  {...register('dueDateConfig.lateFeeAmount', { valueAsNumber: true })}
                />
              </FormField>
            </div>

            <FormField
              label="Late Fee Percentage (Optional)"
              error={errors.dueDateConfig?.lateFeePercentage?.message}
              helperText="Percentage of total fee (0-100%)"
            >
              <Input
                type="number"
                step="0.01"
                min="0"
                max="100"
                {...register('dueDateConfig.lateFeePercentage', { valueAsNumber: true })}
                placeholder="0.00"
              />
            </FormField>
          </CardContent>
        </Card>

        {/* Form Actions */}
        <div className="flex justify-end gap-4">
          <Button type="button" variant="secondary" onClick={handleCancel}>
            Cancel
          </Button>
          <Button type="submit" isLoading={isPending} disabled={isPending}>
            Create Fee Structure
          </Button>
        </div>
      </form>
    </div>
  );
};
