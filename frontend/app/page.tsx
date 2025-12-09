import Link from 'next/link';
import { GraduationCap, Settings, Users, TrendingUp } from 'lucide-react';

export default function HomePage() {
  return (
    <div className="space-y-8">
      <div className="text-center">
        <h1 className="text-4xl font-bold text-gray-900">School Management System</h1>
        <p className="mt-2 text-lg text-gray-600">
          Streamline your school administration and student management
        </p>
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <Link href="/students" className="group">
          <div className="card hover:shadow-lg transition-shadow">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-primary-100 p-3 group-hover:bg-primary-200 transition-colors">
                <GraduationCap className="h-8 w-8 text-primary-600" />
              </div>
              <div>
                <h2 className="text-lg font-semibold text-gray-900">Student Management</h2>
                <p className="text-sm text-gray-600">Manage student records and enrollments</p>
              </div>
            </div>
          </div>
        </Link>

        <Link href="/settings" className="group">
          <div className="card hover:shadow-lg transition-shadow">
            <div className="flex items-center gap-4">
              <div className="rounded-lg bg-secondary-100 p-3 group-hover:bg-secondary-200 transition-colors">
                <Settings className="h-8 w-8 text-secondary-600" />
              </div>
              <div>
                <h2 className="text-lg font-semibold text-gray-900">Configuration</h2>
                <p className="text-sm text-gray-600">Manage school settings and preferences</p>
              </div>
            </div>
          </div>
        </Link>

        <div className="card bg-gray-50">
          <div className="flex items-center gap-4">
            <div className="rounded-lg bg-gray-200 p-3">
              <TrendingUp className="h-8 w-8 text-gray-600" />
            </div>
            <div>
              <h2 className="text-lg font-semibold text-gray-900">Reports</h2>
              <p className="text-sm text-gray-600">Coming soon in Phase 2</p>
            </div>
          </div>
        </div>
      </div>

      <div className="card bg-primary-50 border-primary-200">
        <div className="flex items-start gap-4">
          <Users className="h-6 w-6 text-primary-600 mt-1" />
          <div>
            <h3 className="font-semibold text-primary-900">Quick Start Guide</h3>
            <ul className="mt-2 space-y-2 text-sm text-primary-800">
              <li>1. Navigate to Students to register new students</li>
              <li>2. Use the search feature to find existing students</li>
              <li>3. Configure school settings in the Settings page</li>
              <li>4. Manage student status (Active/Inactive) as needed</li>
            </ul>
          </div>
        </div>
      </div>

      <div className="grid gap-6 md:grid-cols-3">
        <div className="card text-center">
          <div className="text-3xl font-bold text-primary-600">0</div>
          <div className="mt-1 text-sm text-gray-600">Total Students</div>
        </div>
        <div className="card text-center">
          <div className="text-3xl font-bold text-secondary-600">0</div>
          <div className="mt-1 text-sm text-gray-600">Active Students</div>
        </div>
        <div className="card text-center">
          <div className="text-3xl font-bold text-gray-600">0</div>
          <div className="mt-1 text-sm text-gray-600">Configuration Settings</div>
        </div>
      </div>
    </div>
  );
}
