import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { configApi } from '@/api/configApi';
import { ConfigCategory } from '@/types/config';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import Loading from '@/components/common/Loading';
import ErrorDisplay from '@/components/common/ErrorDisplay';
import { SettingsTable } from '@/components/features/config/SettingsTable';
import { SettingFormDialog } from '@/components/features/config/SettingFormDialog';

const CATEGORIES: { value: ConfigCategory; label: string }[] = [
  { value: 'GENERAL', label: 'General' },
  { value: 'ACADEMIC', label: 'Academic' },
  { value: 'FINANCIAL', label: 'Financial' },
];

export default function ConfigurationPage() {
  const [activeTab, setActiveTab] = useState<ConfigCategory>('GENERAL');
  const [showAddDialog, setShowAddDialog] = useState(false);

  const { data, isLoading, error } = useQuery({
    queryKey: ['config-settings'],
    queryFn: () => configApi.getAllSettings(),
  });

  const currentSettings = data?.[activeTab] || [];

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Configuration</h1>
          <p className="mt-2 text-sm text-gray-600">Manage school settings and configuration</p>
        </div>
        <div className="flex gap-3">
          <Link to="/configuration/school-profile">
            <Button variant="outline">School Profile</Button>
          </Link>
          <Button onClick={() => setShowAddDialog(true)}>Add Setting</Button>
        </div>
      </div>

      {isLoading ? (
        <Loading />
      ) : error ? (
        <ErrorDisplay error={error as any} />
      ) : (
        <>
          <div className="border-b border-gray-200">
            <nav className="-mb-px flex space-x-8">
              {CATEGORIES.map((category) => (
                <button
                  key={category.value}
                  onClick={() => setActiveTab(category.value)}
                  className={`
                    whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm
                    ${
                      activeTab === category.value
                        ? 'border-blue-500 text-blue-600'
                        : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                    }
                  `}
                >
                  {category.label}
                  <span className="ml-2 bg-gray-100 text-gray-900 py-0.5 px-2.5 rounded-full text-xs font-medium">
                    {data?.[category.value]?.length || 0}
                  </span>
                </button>
              ))}
            </nav>
          </div>

          <Card>
            <CardHeader>
              <CardTitle>{activeTab} Settings</CardTitle>
            </CardHeader>
            <CardContent className="p-0">
              <SettingsTable settings={currentSettings} />
            </CardContent>
          </Card>
        </>
      )}

      <SettingFormDialog
        open={showAddDialog}
        onOpenChange={setShowAddDialog}
        category={activeTab}
      />
    </div>
  );
}
