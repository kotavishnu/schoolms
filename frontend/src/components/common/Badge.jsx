import { STATUS_COLORS } from '../../utils/constants';

const Badge = ({ text, status, className = '' }) => {
  const colorClass = STATUS_COLORS[status] || 'bg-gray-100 text-gray-800';

  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${colorClass} ${className}`}
    >
      {text}
    </span>
  );
};

export default Badge;
