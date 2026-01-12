import { Input } from '../ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Label } from '../ui/label';
import { Search } from 'lucide-react';
import { useDebounce } from '../../hooks/useDebounce';
import { useEffect, useState } from 'react';

interface StudentFiltersProps {
  onSearchChange: (search: string) => void;
  onStatusChange: (status: string) => void;
  currentSearch: string;
  currentStatus: string;
}

export function StudentFilters({
  onSearchChange,
  onStatusChange,
  currentSearch,
  currentStatus,
}: StudentFiltersProps) {
  const [searchInput, setSearchInput] = useState(currentSearch);
  const debouncedSearch = useDebounce(searchInput, 300);

  useEffect(() => {
    onSearchChange(debouncedSearch);
  }, [debouncedSearch, onSearchChange]);

  return (
    <div className="flex flex-col sm:flex-row gap-4 mb-6">
      <div className="flex-1">
        <Label htmlFor="search" className="sr-only">
          Search students
        </Label>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
          <Input
            id="search"
            type="text"
            placeholder="Search by name or guardian..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      <div className="sm:w-48">
        <Label htmlFor="status" className="sr-only">
          Filter by status
        </Label>
        <Select value={currentStatus} onValueChange={onStatusChange}>
          <SelectTrigger id="status">
            <SelectValue placeholder="All Students" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="ALL">All Students</SelectItem>
            <SelectItem value="ACTIVE">Active</SelectItem>
            <SelectItem value="INACTIVE">Inactive</SelectItem>
          </SelectContent>
        </Select>
      </div>
    </div>
  );
}
