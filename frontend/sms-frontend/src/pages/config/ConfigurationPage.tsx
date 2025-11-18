import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';

export default function ConfigurationPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Configuration</h1>
        <p className="mt-2 text-sm text-gray-600">Manage school settings and configuration</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>School Configuration</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-gray-600">
            Configuration management interface will be implemented here.
          </p>
          <p className="mt-2 text-sm text-gray-500">
            This feature requires the Configuration Service to be running on port 8082.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
