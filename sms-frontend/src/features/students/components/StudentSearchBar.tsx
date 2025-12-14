import React, { useState, useEffect } from 'react';
import Input from '@/components/common/Input';
import Select from '@/components/common/Select';
import Button from '@/components/common/Button';
import { STATUS_OPTIONS } from '@/utils/constants';
import { type StudentSearchParams } from '@/types/student.types';
import { useDebounce } from '@/hooks/useDebounce';

interface StudentSearchBarProps {
  onSearch: (params: StudentSearchParams) => void;
}

const StudentSearchBar: React.FC<StudentSearchBarProps> = ({ onSearch }) => {
  const [lastName, setLastName] = useState('');
  const [guardianName, setGuardianName] = useState('');
  const [status, setStatus] = useState('');

  // Debounce search values
  const debouncedLastName = useDebounce(lastName, 500);
  const debouncedGuardianName = useDebounce(guardianName, 500);

  useEffect(() => {
    const params: StudentSearchParams = {};

    if (debouncedLastName) params.lastName = debouncedLastName;
    if (debouncedGuardianName) params.guardianName = debouncedGuardianName;
    if (status) params.status = status;

    onSearch(params);
  }, [debouncedLastName, debouncedGuardianName, status, onSearch]);

  const handleClear = () => {
    setLastName('');
    setGuardianName('');
    setStatus('');
  };

  const hasFilters = lastName || guardianName || status;

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4 mb-6">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Input
          label="Last Name"
          placeholder="Search by last name..."
          value={lastName}
          onChange={(e) => setLastName(e.target.value)}
        />

        <Input
          label="Guardian Name"
          placeholder="Search by father/mother name..."
          value={guardianName}
          onChange={(e) => setGuardianName(e.target.value)}
        />

        <Select
          label="Status"
          placeholder="All Statuses"
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          options={[{ value: '', label: 'All Statuses' }, ...STATUS_OPTIONS]}
        />
      </div>

      {hasFilters && (
        <div className="mt-4 flex justify-end">
          <Button variant="secondary" onClick={handleClear}>
            Clear Filters
          </Button>
        </div>
      )}
    </div>
  );
};

export default StudentSearchBar;
