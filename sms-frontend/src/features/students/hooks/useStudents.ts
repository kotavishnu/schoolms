import { useQuery } from '@tanstack/react-query';
import { studentService } from '@/services/api/studentService';
import { type StudentSearchParams } from '@/types/student.types';

export const useStudents = (params: StudentSearchParams = {}) => {
  return useQuery({
    queryKey: ['students', params],
    queryFn: () => studentService.searchStudents(params),
    staleTime: 2 * 60 * 1000, // 2 minutes
    placeholderData: (previousData) => previousData, // Keep previous data while fetching new page (v5 syntax)
  });
};
