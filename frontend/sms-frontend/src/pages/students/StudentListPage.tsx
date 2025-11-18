import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { studentApi } from '@/api/studentApi';
import { StudentSearchParams, StudentStatus } from '@/types/student';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
import Loading from '@/components/common/Loading';
import ErrorDisplay from '@/components/common/ErrorDisplay';
import Badge from '@/components/common/Badge';
import { formatDate } from '@/lib/utils';

export default function StudentListPage() {
  const [searchParams, setSearchParams] = useState<StudentSearchParams>({
    page: 0,
    size: 20,
    sort: 'createdAt,desc',
  });

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['students', searchParams],
    queryFn: () => studentApi.searchStudents(searchParams),
  });

  const handleSearch = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    setSearchParams((prev) => ({
      ...prev,
      firstName: formData.get('firstName') as string || undefined,
      lastName: formData.get('lastName') as string || undefined,
      mobile: formData.get('mobile') as string || undefined,
      status: formData.get('status') as StudentStatus || undefined,
      page: 0, // Reset to first page on search
    }));
  };

  const handlePageChange = (newPage: number) => {
    setSearchParams((prev) => ({ ...prev, page: newPage }));
  };

  const handleClearFilters = () => {
    setSearchParams({
      page: 0,
      size: 20,
      sort: 'createdAt,desc',
    });
    // Reset form
    const form = document.getElementById('search-form') as HTMLFormElement;
    if (form) form.reset();
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Students</h1>
          <p className="mt-2 text-sm text-gray-600">Manage student registrations and profiles</p>
        </div>
        <Link to="/students/new">
          <Button>Add New Student</Button>
        </Link>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Search Filters</CardTitle>
        </CardHeader>
        <CardContent>
          <form id="search-form" onSubmit={handleSearch} className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <Input name="firstName" placeholder="First Name" label="First Name" />
            <Input name="lastName" placeholder="Last Name" label="Last Name" />
            <Input name="mobile" placeholder="Mobile Number" label="Mobile" />
            <div className="w-full">
              <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
              <select
                name="status"
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
              >
                <option value="">All</option>
                <option value="ACTIVE">Active</option>
                <option value="INACTIVE">Inactive</option>
              </select>
            </div>
            <div className="flex gap-2 md:col-span-4">
              <Button type="submit">Search</Button>
              <Button type="button" variant="outline" onClick={handleClearFilters}>
                Clear Filters
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      {isLoading ? (
        <Loading />
      ) : error ? (
        <ErrorDisplay error={error as any} />
      ) : data ? (
        <>
          <Card>
            <CardContent className="p-0">
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Student ID
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Name
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Age
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Mobile
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Status
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Created
                      </th>
                      <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {data.content.length === 0 ? (
                      <tr>
                        <td colSpan={7} className="px-6 py-12 text-center text-gray-500">
                          No students found. Try adjusting your search criteria.
                        </td>
                      </tr>
                    ) : (
                      data.content.map((student) => (
                        <tr key={student.studentId} className="hover:bg-gray-50">
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-blue-600">
                            <Link to={`/students/${student.studentId}`}>
                              {student.studentId}
                            </Link>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                            {student.firstName} {student.lastName}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {student.age}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {student.mobile}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <Badge variant={student.status === 'ACTIVE' ? 'success' : 'danger'}>
                              {student.status}
                            </Badge>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {formatDate(student.createdAt)}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium space-x-2">
                            <Link to={`/students/${student.studentId}`}>
                              <Button size="sm" variant="outline">View</Button>
                            </Link>
                            <Link to={`/students/${student.studentId}/edit`}>
                              <Button size="sm" variant="outline">Edit</Button>
                            </Link>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </CardContent>
          </Card>

          {data.totalPages > 1 && (
            <div className="flex items-center justify-between bg-white px-6 py-4 rounded-lg border">
              <div className="text-sm text-gray-700">
                Showing page {data.page + 1} of {data.totalPages} ({data.totalElements} total students)
              </div>
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handlePageChange(data.page - 1)}
                  disabled={data.first}
                >
                  Previous
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handlePageChange(data.page + 1)}
                  disabled={data.last}
                >
                  Next
                </Button>
              </div>
            </div>
          )}
        </>
      ) : null}
    </div>
  );
}
