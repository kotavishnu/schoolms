import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { configApi } from '@/api/configApi';
import { UpdateSchoolProfileRequest } from '@/types/config';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import Loading from '@/components/common/Loading';
import ErrorDisplay from '@/components/common/ErrorDisplay';
import { useToast } from '@/components/ui/Toast';
import { formatDate } from '@/lib/utils';

export default function SchoolProfilePage() {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const queryClient = useQueryClient();
  const [isEditing, setIsEditing] = useState(false);

  const { data: profile, isLoading, error } = useQuery({
    queryKey: ['school-profile'],
    queryFn: () => configApi.getSchoolProfile(),
  });

  const [formData, setFormData] = useState<UpdateSchoolProfileRequest>({
    schoolName: '',
    schoolCode: '',
    logoPath: '',
    address: '',
    phone: '',
    email: '',
  });

  useState(() => {
    if (profile) {
      setFormData({
        schoolName: profile.schoolName,
        schoolCode: profile.schoolCode,
        logoPath: profile.logoPath || '',
        address: profile.address,
        phone: profile.phone,
        email: profile.email,
      });
    }
  });

  const updateMutation = useMutation({
    mutationFn: (data: UpdateSchoolProfileRequest) =>
      configApi.updateSchoolProfile(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['school-profile'] });
      showToast({
        type: 'success',
        title: 'Success',
        description: 'School profile updated successfully',
      });
      setIsEditing(false);
    },
    onError: (error: any) => {
      showToast({
        type: 'error',
        title: 'Error',
        description: error.response?.data?.detail || 'Failed to update profile',
      });
    },
  });

  const handleEdit = () => {
    if (profile) {
      setFormData({
        schoolName: profile.schoolName,
        schoolCode: profile.schoolCode,
        logoPath: profile.logoPath || '',
        address: profile.address,
        phone: profile.phone,
        email: profile.email,
      });
      setIsEditing(true);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    if (profile) {
      setFormData({
        schoolName: profile.schoolName,
        schoolCode: profile.schoolCode,
        logoPath: profile.logoPath || '',
        address: profile.address,
        phone: profile.phone,
        email: profile.email,
      });
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    updateMutation.mutate(formData);
  };

  const handleChange = (field: keyof UpdateSchoolProfileRequest, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  if (isLoading) return <Loading />;
  if (error) return <ErrorDisplay error={error as any} />;
  if (!profile) return <ErrorDisplay error={new Error('School profile not found')} />;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">School Profile</h1>
          <p className="mt-2 text-sm text-gray-600">
            Manage your school's basic information
          </p>
        </div>
        <Button variant="outline" onClick={() => navigate('/configuration')}>
          Back to Configuration
        </Button>
      </div>

      <Card>
        <CardHeader>
          <div className="flex justify-between items-center">
            <CardTitle>School Information</CardTitle>
            {!isEditing && (
              <Button onClick={handleEdit}>Edit Profile</Button>
            )}
          </div>
        </CardHeader>
        <CardContent>
          {isEditing ? (
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <Input
                  label="School Name"
                  value={formData.schoolName}
                  onChange={(e) => handleChange('schoolName', e.target.value)}
                  required
                  maxLength={200}
                />

                <Input
                  label="School Code"
                  value={formData.schoolCode}
                  onChange={(e) => handleChange('schoolCode', e.target.value.toUpperCase())}
                  required
                  pattern="^[A-Z0-9]{3,20}$"
                  title="3-20 characters, uppercase letters and numbers only"
                />

                <Input
                  label="Phone"
                  value={formData.phone}
                  onChange={(e) => handleChange('phone', e.target.value)}
                  required
                  pattern="^[0-9+()-]{10,15}$"
                  title="Valid phone number (10-15 characters)"
                />

                <Input
                  label="Email"
                  type="email"
                  value={formData.email}
                  onChange={(e) => handleChange('email', e.target.value)}
                  required
                />

                <div className="md:col-span-2">
                  <Input
                    label="Logo Path"
                    value={formData.logoPath}
                    onChange={(e) => handleChange('logoPath', e.target.value)}
                    placeholder="/uploads/logos/school-logo.png"
                  />
                </div>

                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Address <span className="text-red-500">*</span>
                  </label>
                  <textarea
                    value={formData.address}
                    onChange={(e) => handleChange('address', e.target.value)}
                    required
                    maxLength={500}
                    rows={3}
                    className="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                  />
                </div>
              </div>

              <div className="flex gap-2 pt-4">
                <Button type="submit" isLoading={updateMutation.isPending}>
                  Save Changes
                </Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={handleCancel}
                  disabled={updateMutation.isPending}
                >
                  Cancel
                </Button>
              </div>
            </form>
          ) : (
            <div className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-500 mb-1">
                    School Name
                  </label>
                  <p className="text-base text-gray-900">{profile.schoolName}</p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-500 mb-1">
                    School Code
                  </label>
                  <p className="text-base text-gray-900">
                    <code className="bg-gray-100 px-2 py-1 rounded">{profile.schoolCode}</code>
                  </p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-500 mb-1">
                    Phone
                  </label>
                  <p className="text-base text-gray-900">{profile.phone}</p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-500 mb-1">
                    Email
                  </label>
                  <p className="text-base text-gray-900">{profile.email}</p>
                </div>

                {profile.logoPath && (
                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-500 mb-1">
                      Logo Path
                    </label>
                    <p className="text-base text-gray-900 font-mono text-sm">
                      {profile.logoPath}
                    </p>
                  </div>
                )}

                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-gray-500 mb-1">
                    Address
                  </label>
                  <p className="text-base text-gray-900 whitespace-pre-line">
                    {profile.address}
                  </p>
                </div>
              </div>

              <div className="border-t pt-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                  <div>
                    <label className="block text-xs font-medium text-gray-500 mb-1">
                      Last Updated
                    </label>
                    <p className="text-gray-700">{formatDate(profile.updatedAt)}</p>
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-500 mb-1">
                      Updated By
                    </label>
                    <p className="text-gray-700">{profile.updatedBy}</p>
                  </div>
                </div>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
