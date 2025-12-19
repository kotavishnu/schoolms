import React from 'react';

const Loading = ({ text = 'Loading...' }) => {
  return (
    <div className="flex items-center justify-center p-8">
      <div className="text-center">
        <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        <p className="mt-4 text-gray-600">{text}</p>
      </div>
    </div>
  );
};

export default Loading;
