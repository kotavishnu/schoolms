import { ApiError } from '@/types/error';

interface ErrorDisplayProps {
  error: ApiError | Error;
}

export default function ErrorDisplay({ error }: ErrorDisplayProps) {
  const isApiError = (err: ApiError | Error): err is ApiError => {
    return 'problemDetail' in err;
  };

  const problemDetail = isApiError(error) ? error.problemDetail : null;
  const message = isApiError(error)
    ? problemDetail?.detail
    : error instanceof Error
    ? error.message
    : 'An unknown error occurred';

  return (
    <div className="bg-red-50 border border-red-200 rounded-lg p-6">
      <div className="flex items-start">
        <div className="flex-shrink-0">
          <svg
            className="h-6 w-6 text-red-600"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
        </div>
        <div className="ml-3 flex-1">
          <h3 className="text-sm font-medium text-red-800">
            {problemDetail?.title || 'Error'}
          </h3>
          <div className="mt-2 text-sm text-red-700">
            <p>{message}</p>
          </div>
          {problemDetail?.errors && problemDetail.errors.length > 0 && (
            <ul className="mt-2 list-disc list-inside text-sm text-red-700">
              {problemDetail.errors.map((err, idx) => (
                <li key={idx}>{err}</li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}
