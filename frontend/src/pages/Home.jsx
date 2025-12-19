import React from 'react';
import { Link } from 'react-router-dom';
import Card from '../components/Card';

const Home = () => {
  const features = [
    {
      title: 'Student Registration',
      description: 'Register new students with complete details',
      link: '/students',
      icon: '👨‍🎓',
      color: 'bg-blue-500'
    },
    {
      title: 'Class Management',
      description: 'View and manage classes and students',
      link: '/classes',
      icon: '🏫',
      color: 'bg-green-500'
    },
    {
      title: 'Fee Master',
      description: 'Configure fee structures for different classes',
      link: '/fee-master',
      icon: '💰',
      color: 'bg-yellow-500'
    },
    {
      title: 'Fee Receipt',
      description: 'Generate fee receipts and record payments',
      link: '/fee-receipt',
      icon: '🧾',
      color: 'bg-purple-500'
    },
    {
      title: 'Parent Portal',
      description: 'Parent-facing fee payment interface',
      link: '/parent-portal',
      icon: '👪',
      color: 'bg-pink-500'
    },
    {
      title: 'School Configuration',
      description: 'Setup school details and fee frequency',
      link: '/school-config',
      icon: '⚙️',
      color: 'bg-red-500'
    }
  ];

  return (
    <div className="max-w-7xl mx-auto">
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold text-gray-800 mb-4">
          Welcome to School Management System
        </h1>
        <p className="text-xl text-gray-600">
          Manage your school operations efficiently with our comprehensive solution
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {features.map((feature, index) => (
          <Link
            key={index}
            to={feature.link}
            className="block transform transition hover:scale-105"
          >
            <Card className="h-full hover:shadow-xl transition-shadow">
              <div className="flex flex-col items-center text-center">
                <div className={`${feature.color} text-white text-5xl mb-4 p-4 rounded-full w-24 h-24 flex items-center justify-center`}>
                  {feature.icon}
                </div>
                <h3 className="text-xl font-semibold mb-2">{feature.title}</h3>
                <p className="text-gray-600">{feature.description}</p>
              </div>
            </Card>
          </Link>
        ))}
      </div>

      <div className="mt-12">
        <Card>
          <h2 className="text-2xl font-bold mb-4">System Features</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex items-start">
              <span className="text-green-500 text-2xl mr-3">✓</span>
              <div>
                <h4 className="font-semibold">Student Management</h4>
                <p className="text-gray-600 text-sm">Complete student registration and tracking</p>
              </div>
            </div>
            <div className="flex items-start">
              <span className="text-green-500 text-2xl mr-3">✓</span>
              <div>
                <h4 className="font-semibold">Fee Management</h4>
                <p className="text-gray-600 text-sm">Automated fee calculation and receipt generation</p>
              </div>
            </div>
            <div className="flex items-start">
              <span className="text-green-500 text-2xl mr-3">✓</span>
              <div>
                <h4 className="font-semibold">Class Organization</h4>
                <p className="text-gray-600 text-sm">Manage classes from 1 to 10</p>
              </div>
            </div>
            <div className="flex items-start">
              <span className="text-green-500 text-2xl mr-3">✓</span>
              <div>
                <h4 className="font-semibold">Parent Portal</h4>
                <p className="text-gray-600 text-sm">Parents can view and pay fees online</p>
              </div>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default Home;
