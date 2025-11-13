/**
 * Fee Structure List Page
 *
 * Features:
 * - Display fee structures in a table with pagination
 * - Search by structure name
 * - Filter by academic year, fee type, and status
 * - Action buttons: View, Edit, Delete, Activate/Deactivate
 * - Add new fee structure button
 */
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Plus, Eye, Edit, Trash2, Power } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Input } from '@/shared/components/ui/Input';
import { Select } from '@/shared/components/ui/Select';
import { Table, type Column } from '@/shared/components/ui/Table';
import { Badge } from '@/shared/components/ui/Badge';
import {
  useFeeStructures,
  useDeleteFeeStructure,
  useToggleFeeStructureStatus,
  useAcademicYears,
} from '../hooks/useFees';
import type { FeeStructure, FeeType, FeeFrequency } from '../types/fee.types';

export const FeeStructureList = () => {
  const navigate = useNavigate();

  // Filter state
  const [search, setSearch] = useState('');
  const [academicYear, setAcademicYear] = useState('');
  const [feeType, setFeeType] = useState<FeeType | ''>('');
  const [frequency, setFrequency] = useState<FeeFrequency | ''>('');
  const [isActive, setIsActive] = useState<boolean | undefined>(undefined);
  const [page, setPage] = useState(0);
  const limit = 20;

  // Build filters object
  const filters = {
    search: search || undefined,
    academicYear: academicYear || undefined,
    feeType: feeType || undefined,
    frequency: frequency || undefined,
    isActive,
    page,
    limit,
  };

  // Fetch fee structures with filters
  const { data, isLoading, error } = useFeeStructures(filters);

  // Fetch academic years for filter dropdown
  const { data: academicYears } = useAcademicYears();

  // Delete mutation
  const { mutate: deleteFeeStructure, isPending: isDeleting } = useDeleteFeeStructure();

  // Toggle status mutation
  const { mutate: toggleStatus, isPending: isToggling } = useToggleFeeStructureStatus();

  /**
   * Get badge variant based on fee structure status
   */
  const getStatusBadge = (status: boolean) => {
    return status ? (
      <Badge variant="success">Active</Badge>
    ) : (
      <Badge variant="error">Inactive</Badge>
    );
  };

  /**
   * Get badge variant for fee frequency
   */
  const getFrequencyBadge = (frequency: FeeFrequency) => {
    const variants: Record<FeeFrequency, 'info' | 'success' | 'warning'> = {
      MONTHLY: 'info',
      QUARTERLY: 'success',
      ANNUAL: 'warning',
      ONE_TIME: 'info',
    };

    return <Badge variant={variants[frequency]}>{frequency}</Badge>;
  };

  /**
   * Handle delete fee structure with confirmation
   */
  const handleDelete = (feeStructure: FeeStructure) => {
    if (
      window.confirm(
        `Are you sure you want to delete "${feeStructure.structureName}"? This action cannot be undone.`
      )
    ) {
      deleteFeeStructure(feeStructure.feeStructureId);
    }
  };

  /**
   * Handle toggle fee structure status
   */
  const handleToggleStatus = (feeStructure: FeeStructure) => {
    const newStatus = !feeStructure.isActive;
    const action = newStatus ? 'activate' : 'deactivate';

    if (window.confirm(`Are you sure you want to ${action} "${feeStructure.structureName}"?`)) {
      toggleStatus({ id: feeStructure.feeStructureId, isActive: newStatus });
    }
  };

  /**
   * Handle search input change
   */
  const handleSearchChange = (value: string) => {
    setSearch(value);
    setPage(0); // Reset to first page on search
  };

  /**
   * Handle filter changes
   */
  const handleFilterChange = (filterType: string, value: any) => {
    setPage(0); // Reset to first page on filter change

    switch (filterType) {
      case 'academicYear':
        setAcademicYear(value);
        break;
      case 'feeType':
        setFeeType(value);
        break;
      case 'frequency':
        setFrequency(value);
        break;
      case 'status':
        setIsActive(value === '' ? undefined : value === 'true');
        break;
    }
  };

  /**
   * Format currency amount
   */
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 2,
    }).format(amount);
  };

  /**
   * Table column definitions
   */
  const columns: Column<FeeStructure>[] = [
    {
      key: 'structureName',
      header: 'Structure Name',
      width: '250px',
    },
    {
      key: 'academicYear',
      header: 'Academic Year',
      width: '120px',
      render: (feeStructure) => feeStructure.academicYear.yearCode,
    },
    {
      key: 'frequency',
      header: 'Frequency',
      width: '120px',
      render: (feeStructure) => getFrequencyBadge(feeStructure.frequency),
    },
    {
      key: 'totalAmount',
      header: 'Total Amount',
      width: '140px',
      render: (feeStructure) => (
        <span className="font-semibold text-gray-900">
          {formatCurrency(feeStructure.totalAmount)}
        </span>
      ),
    },
    {
      key: 'applicableClasses',
      header: 'Classes',
      width: '150px',
      render: (feeStructure) => (
        <span className="text-sm text-gray-600">
          {feeStructure.applicableClasses.slice(0, 3).join(', ')}
          {feeStructure.applicableClasses.length > 3 && '...'}
        </span>
      ),
    },
    {
      key: 'isActive',
      header: 'Status',
      width: '100px',
      render: (feeStructure) => getStatusBadge(feeStructure.isActive),
    },
    {
      key: 'actions',
      header: 'Actions',
      width: '200px',
      render: (feeStructure) => (
        <div className="flex items-center gap-2">
          <button
            onClick={(e) => {
              e.stopPropagation();
              navigate(`/fees/structures/${feeStructure.feeStructureId}`);
            }}
            className="p-1 text-blue-600 hover:bg-blue-50 rounded"
            aria-label="View fee structure details"
            title="View"
          >
            <Eye className="w-4 h-4" />
          </button>
          <button
            onClick={(e) => {
              e.stopPropagation();
              navigate(`/fees/structures/${feeStructure.feeStructureId}/edit`);
            }}
            className="p-1 text-gray-600 hover:bg-gray-50 rounded"
            aria-label="Edit fee structure"
            title="Edit"
          >
            <Edit className="w-4 h-4" />
          </button>
          <button
            onClick={(e) => {
              e.stopPropagation();
              handleToggleStatus(feeStructure);
            }}
            className={`p-1 rounded ${
              feeStructure.isActive
                ? 'text-orange-600 hover:bg-orange-50'
                : 'text-green-600 hover:bg-green-50'
            }`}
            aria-label={feeStructure.isActive ? 'Deactivate' : 'Activate'}
            title={feeStructure.isActive ? 'Deactivate' : 'Activate'}
            disabled={isToggling}
          >
            <Power className="w-4 h-4" />
          </button>
          <button
            onClick={(e) => {
              e.stopPropagation();
              handleDelete(feeStructure);
            }}
            className="p-1 text-red-600 hover:bg-red-50 rounded"
            aria-label="Delete fee structure"
            title="Delete"
            disabled={isDeleting}
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      ),
    },
  ];

  /**
   * Handle pagination
   */
  const handlePreviousPage = () => {
    setPage((prev) => Math.max(0, prev - 1));
  };

  const handleNextPage = () => {
    if (data && page < data.page.totalPages - 1) {
      setPage((prev) => prev + 1);
    }
  };

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Fee Structures</h1>
          <p className="text-gray-600 mt-1">Manage fee structures and assignments</p>
        </div>
        <Button onClick={() => navigate('/fees/structures/new')} className="flex items-center gap-2">
          <Plus className="w-4 h-4" />
          Create Fee Structure
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Fee Structure List</CardTitle>
        </CardHeader>
        <CardContent>
          {/* Filters Section */}
          <div className="mb-6 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
            {/* Search Input */}
            <div className="relative lg:col-span-2">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <Input
                type="text"
                placeholder="Search by structure name..."
                value={search}
                onChange={(e) => handleSearchChange(e.target.value)}
                className="pl-10"
                aria-label="Search fee structures"
              />
            </div>

            {/* Academic Year Filter */}
            <Select
              value={academicYear}
              onChange={(e) => handleFilterChange('academicYear', e.target.value)}
              aria-label="Filter by academic year"
            >
              <option value="">All Years</option>
              {academicYears?.map((year) => (
                <option key={year.academicYearId} value={year.yearCode}>
                  {year.yearCode} {year.isCurrent ? '(Current)' : ''}
                </option>
              ))}
            </Select>

            {/* Fee Type Filter */}
            <Select
              value={feeType}
              onChange={(e) => handleFilterChange('feeType', e.target.value)}
              aria-label="Filter by fee type"
            >
              <option value="">All Types</option>
              <option value="TUITION">Tuition</option>
              <option value="LIBRARY">Library</option>
              <option value="COMPUTER">Computer</option>
              <option value="SPORTS">Sports</option>
              <option value="TRANSPORT">Transport</option>
              <option value="LAB">Lab</option>
              <option value="OTHER">Other</option>
            </Select>

            {/* Status Filter */}
            <Select
              value={isActive === undefined ? '' : String(isActive)}
              onChange={(e) => handleFilterChange('status', e.target.value)}
              aria-label="Filter by status"
            >
              <option value="">All Status</option>
              <option value="true">Active</option>
              <option value="false">Inactive</option>
            </Select>
          </div>

          {/* Error State */}
          {error && (
            <div className="text-center py-8 text-red-600" role="alert">
              <p>Failed to load fee structures. Please try again.</p>
            </div>
          )}

          {/* Table */}
          <Table
            data={data?.content || []}
            columns={columns}
            isLoading={isLoading}
            emptyMessage="No fee structures found. Create your first fee structure to get started."
          />

          {/* Pagination */}
          {data && data.page.totalPages > 1 && (
            <div className="mt-6 flex items-center justify-between border-t pt-4">
              <p className="text-sm text-gray-600">
                Showing {page * limit + 1} to {Math.min((page + 1) * limit, data.page.totalElements)}{' '}
                of {data.page.totalElements} fee structures
              </p>
              <div className="flex items-center gap-2">
                <Button variant="outline" size="sm" onClick={handlePreviousPage} disabled={page === 0}>
                  Previous
                </Button>
                <span className="text-sm text-gray-600">
                  Page {page + 1} of {data.page.totalPages}
                </span>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={handleNextPage}
                  disabled={page === data.page.totalPages - 1}
                >
                  Next
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};
