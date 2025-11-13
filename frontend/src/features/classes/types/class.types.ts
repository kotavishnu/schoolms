/**
 * TypeScript type definitions for Class Management Module
 */

/**
 * Class name enumeration (Grade levels)
 */
export type ClassName =
  | 'CLASS_1'
  | 'CLASS_2'
  | 'CLASS_3'
  | 'CLASS_4'
  | 'CLASS_5'
  | 'CLASS_6'
  | 'CLASS_7'
  | 'CLASS_8'
  | 'CLASS_9'
  | 'CLASS_10';

/**
 * Section enumeration
 */
export type Section = 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J';

/**
 * Enrollment status enumeration
 */
export type EnrollmentStatus = 'ENROLLED' | 'PROMOTED' | 'WITHDRAWN';

/**
 * Academic year information
 */
export interface AcademicYear {
  academicYearId: number;
  yearCode: string; // Format: YYYY-YYYY (e.g., "2024-2025")
  startDate: string;
  endDate: string;
  isCurrent: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * Teacher information (simplified for class teacher reference)
 */
export interface Teacher {
  teacherId: number;
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
}

/**
 * Complete school class information
 */
export interface SchoolClass {
  classId: number;
  className: ClassName;
  section: Section;
  academicYear: AcademicYear;
  maxCapacity: number;
  currentEnrollment: number;
  classTeacher?: Teacher;
  roomNumber?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * Class form data for creation/edit
 */
export interface ClassFormData {
  className: ClassName;
  section: Section;
  academicYearId: number;
  maxCapacity: number;
  classTeacherId?: number;
  roomNumber?: string;
  isActive?: boolean;
}

/**
 * Filters for class list
 */
export interface ClassFilters {
  search?: string;
  academicYearId?: number;
  className?: ClassName;
  section?: Section;
  isActive?: boolean;
  page?: number;
  limit?: number;
}

/**
 * Paginated class list response
 */
export interface ClassListResponse {
  content: SchoolClass[];
  page: {
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
  };
}

/**
 * Student enrollment information
 */
export interface Enrollment {
  enrollmentId: number;
  studentId: number;
  studentCode: string;
  studentName: string;
  classId: number;
  enrollmentDate: string;
  status: EnrollmentStatus;
  withdrawalDate?: string;
  withdrawalReason?: string;
  promotedToClassId?: number;
}

/**
 * Student for enrollment (simplified view)
 */
export interface EnrollableStudent {
  studentId: number;
  studentCode: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  currentClass?: string;
  status: string;
}

/**
 * Enroll student request
 */
export interface EnrollStudentRequest {
  studentId: number;
  classId: number;
  enrollmentDate: string;
}

/**
 * Remove student from class request
 */
export interface RemoveStudentRequest {
  enrollmentId: number;
  withdrawalDate: string;
  withdrawalReason: string;
}

/**
 * Academic year form data
 */
export interface AcademicYearFormData {
  yearCode: string;
  startDate: string;
  endDate: string;
  isCurrent: boolean;
}

/**
 * Class capacity summary
 */
export interface ClassCapacitySummary {
  classId: number;
  className: string; // e.g., "Class 5-A"
  maxCapacity: number;
  currentEnrollment: number;
  availableSeats: number;
  utilizationPercentage: number;
}

/**
 * Class creation response
 */
export interface ClassCreateResponse {
  class: SchoolClass;
  message: string;
}

/**
 * Class update response
 */
export interface ClassUpdateResponse {
  class: SchoolClass;
  message: string;
}

/**
 * Class deletion response
 */
export interface ClassDeleteResponse {
  message: string;
}

/**
 * Enrollment response
 */
export interface EnrollmentResponse {
  enrollment: Enrollment;
  message: string;
}

/**
 * Class statistics for details page
 */
export interface ClassStatistics {
  totalStudents: number;
  maleStudents: number;
  femaleStudents: number;
  averageAge: number;
  attendanceRate?: number;
  capacityUtilization: number;
}

/**
 * Timetable entry (for future enhancement)
 */
export interface TimetableEntry {
  day: string;
  period: number;
  subject: string;
  teacherId?: number;
  teacherName?: string;
  startTime: string;
  endTime: string;
}

/**
 * Class name display mapping
 */
export const CLASS_NAME_DISPLAY: Record<ClassName, string> = {
  CLASS_1: 'Class 1',
  CLASS_2: 'Class 2',
  CLASS_3: 'Class 3',
  CLASS_4: 'Class 4',
  CLASS_5: 'Class 5',
  CLASS_6: 'Class 6',
  CLASS_7: 'Class 7',
  CLASS_8: 'Class 8',
  CLASS_9: 'Class 9',
  CLASS_10: 'Class 10',
};

/**
 * Get formatted class name (e.g., "Class 5-A")
 */
export const getFormattedClassName = (className: ClassName, section: Section): string => {
  return `${CLASS_NAME_DISPLAY[className]}-${section}`;
};

/**
 * Get class grade number from ClassName enum
 */
export const getClassGradeNumber = (className: ClassName): number => {
  return parseInt(className.replace('CLASS_', ''), 10);
};
