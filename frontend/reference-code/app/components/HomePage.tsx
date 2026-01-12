import { useNavigate } from 'react-router-dom';
import { Users, CheckCircle, Settings, UserPlus } from 'lucide-react';
import { Button } from './ui/button';

export function HomePage() {
  const navigate = useNavigate();

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Welcome Banner */}
      <div className="bg-blue-600 rounded-lg p-8 mb-8">
        <h1 className="text-3xl text-white mb-2">Welcome to School Management System</h1>
        <p className="text-blue-100">Manage students, configurations, and school operations efficiently</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <Users className="w-6 h-6 text-blue-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Total Students</p>
              <p className="text-3xl">1</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
              <CheckCircle className="w-6 h-6 text-green-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Active Students</p>
              <p className="text-3xl">1</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg p-6 border border-gray-200">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
              <Settings className="w-6 h-6 text-purple-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">System</p>
              <p className="text-3xl">Active</p>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div>
        <h2 className="text-xl mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <button
            onClick={() => navigate('/students')}
            className="bg-white rounded-lg p-6 border border-gray-200 hover:border-blue-500 hover:shadow-md transition-all text-left focus:outline-none focus-visible:outline-2 focus-visible:outline-blue-600 focus-visible:outline-offset-2 focus-visible:ring-4 focus-visible:ring-blue-600/20"
          >
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                <Users className="w-6 h-6 text-blue-600" />
              </div>
              <div>
                <h3 className="text-base mb-1">Manage Students</h3>
                <p className="text-sm text-gray-600">View, search, and manage student records</p>
              </div>
            </div>
          </button>

          <button
            onClick={() => navigate('/students')}
            className="bg-white rounded-lg p-6 border border-gray-200 hover:border-green-500 hover:shadow-md transition-all text-left focus:outline-none focus-visible:outline-2 focus-visible:outline-green-600 focus-visible:outline-offset-2 focus-visible:ring-4 focus-visible:ring-green-600/20"
          >
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                <UserPlus className="w-6 h-6 text-green-600" />
              </div>
              <div>
                <h3 className="text-base mb-1">Register Student</h3>
                <p className="text-sm text-gray-600">Add a new student to the system</p>
              </div>
            </div>
          </button>
        </div>
      </div>
    </div>
  );
}
