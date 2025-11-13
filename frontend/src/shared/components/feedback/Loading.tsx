import React from 'react';

export interface LoadingProps {
  message?: string;
}

/**
 * Loading spinner component
 */
export const Loading: React.FC<LoadingProps> = ({ message = 'Loading...' }) => {
  return (
    <div className="flex flex-col items-center justify-center py-12">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mb-4" />
      <p className="text-gray-600">{message}</p>
    </div>
  );
};
