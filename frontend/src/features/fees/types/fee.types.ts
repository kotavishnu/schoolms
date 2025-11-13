/**
 * TypeScript type definitions for Fee Management Module
 */

/**
 * Fee type enumeration
 */
export type FeeType = 'TUITION' | 'LIBRARY' | 'COMPUTER' | 'SPORTS' | 'TRANSPORT' | 'LAB' | 'OTHER';

/**
 * Fee frequency enumeration
 */
export type FeeFrequency = 'MONTHLY' | 'QUARTERLY' | 'ANNUAL' | 'ONE_TIME';

/**
 * Fee status enumeration
 */
export type FeeStatus = 'ACTIVE' | 'INACTIVE';

/**
 * Payment status enumeration
 */
export type PaymentStatus = 'PENDING' | 'PARTIAL' | 'PAID' | 'OVERDUE' | 'WAIVED';

/**
 * Payment method enumeration
 */
export type PaymentMethod = 'CASH' | 'UPI' | 'CARD' | 'BANK_TRANSFER' | 'CHEQUE' | 'ONLINE';

/**
 * Fee component within a fee structure
 */
export interface FeeComponent {
  feeType: FeeType;
  feeName: string;
  amount: number;
  description?: string;
}

/**
 * Due date configuration
 */
export interface DueDateConfig {
  dueDay: number; // Day of month (1-31)
  gracePeriodDays: number;
  lateFeeAmount: number;
  lateFeePercentage?: number;
}

/**
 * Complete fee structure information
 */
export interface FeeStructure {
  feeStructureId: number;
  structureName: string;
  academicYear: {
    academicYearId: number;
    yearCode: string;
  };
  frequency: FeeFrequency;
  components: FeeComponent[];
  applicableClasses: string[]; // Array of class IDs or names
  totalAmount: number;
  dueDateConfig: DueDateConfig;
  effectiveFrom: string;
  effectiveTo?: string;
  isActive: boolean;
  version: number;
  description?: string;
  createdAt: string;
  updatedAt: string;
  createdBy?: {
    userId: number;
    username: string;
  };
}

/**
 * Fee structure form data for creation/editing
 */
export interface FeeStructureFormData {
  structureName: string;
  academicYearCode: string;
  frequency: FeeFrequency;
  applicableClasses: string[];
  effectiveFrom: string;
  effectiveTo?: string;
  description?: string;

  // Fee components
  components: {
    feeType: FeeType;
    feeName: string;
    amount: number;
    description?: string;
  }[];

  // Due date configuration
  dueDateConfig: {
    dueDay: number;
    gracePeriodDays: number;
    lateFeeAmount: number;
    lateFeePercentage?: number;
  };
}

/**
 * Student fee assignment
 */
export interface StudentFee {
  studentFeeId: number;
  student: {
    studentId: number;
    studentCode: string;
    fullName: string;
    class: string;
  };
  feeStructure: FeeStructure;
  customAmount?: number; // Override amount (for discounts/scholarships)
  discount?: {
    discountType: 'PERCENTAGE' | 'FIXED';
    discountValue: number;
    reason: string;
  };
  effectiveFrom: string;
  effectiveTo?: string;
  isActive: boolean;
  createdAt: string;
}

/**
 * Fee journal entry (monthly fee record per student)
 */
export interface FeeJournal {
  journalId: number;
  student: {
    studentId: number;
    studentCode: string;
    fullName: string;
  };
  feeStructure: {
    feeStructureId: number;
    feeType: FeeType;
    feeName: string;
  };
  feeMonth: string; // YYYY-MM format
  dueAmount: number;
  paidAmount: number;
  balanceAmount: number;
  status: PaymentStatus;
  dueDate: string;
  paidDate?: string;
  lateFeeApplied?: number;
  waiverReason?: string;
}

/**
 * Fee filters for listing
 */
export interface FeeFilters {
  search?: string;
  academicYear?: string;
  feeType?: FeeType;
  frequency?: FeeFrequency;
  isActive?: boolean;
  applicableClass?: string;
  page?: number;
  limit?: number;
}

/**
 * Paginated fee structure list response
 */
export interface FeeStructureListResponse {
  content: FeeStructure[];
  page: {
    number: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
}

/**
 * Fee structure creation response
 */
export interface FeeStructureCreateResponse {
  feeStructure: FeeStructure;
  message: string;
}

/**
 * Fee structure update response
 */
export interface FeeStructureUpdateResponse {
  feeStructure: FeeStructure;
  message: string;
}

/**
 * Fee structure deletion response
 */
export interface FeeStructureDeleteResponse {
  message: string;
}

/**
 * Student fee assignment request
 */
export interface StudentFeeAssignmentRequest {
  studentId: number;
  feeStructureId: number;
  customAmount?: number;
  discount?: {
    discountType: 'PERCENTAGE' | 'FIXED';
    discountValue: number;
    reason: string;
  };
  effectiveFrom: string;
  effectiveTo?: string;
}

/**
 * Student fee assignment response
 */
export interface StudentFeeAssignmentResponse {
  studentFee: StudentFee;
  message: string;
}

/**
 * Dashboard statistics
 */
export interface FeeDashboardStats {
  currentMonth: {
    totalDue: number;
    totalCollected: number;
    totalPending: number;
    collectionPercentage: number;
  };
  currentYear: {
    totalDue: number;
    totalCollected: number;
    totalPending: number;
    collectionPercentage: number;
  };
  overdueStats: {
    totalOverdueAmount: number;
    totalOverdueStudents: number;
    overdueByClass: {
      className: string;
      overdueAmount: number;
      studentCount: number;
    }[];
  };
  collectionTrend: {
    month: string;
    collected: number;
    pending: number;
  }[];
  byFeeType: {
    feeType: FeeType;
    feeName: string;
    totalDue: number;
    totalCollected: number;
    percentage: number;
  }[];
  recentTransactions: {
    receiptNumber: string;
    studentName: string;
    amount: number;
    paymentMethod: PaymentMethod;
    paymentDate: string;
  }[];
}

/**
 * Academic year option for dropdowns
 */
export interface AcademicYearOption {
  academicYearId: number;
  yearCode: string;
  startDate: string;
  endDate: string;
  isCurrent: boolean;
}

/**
 * Class option for dropdowns
 */
export interface ClassOption {
  classId: number;
  className: string;
  section: string;
  currentEnrollment: number;
  maxCapacity: number;
}

/**
 * Fee calculation request
 */
export interface FeeCalculationRequest {
  studentId: number;
  academicYearCode: string;
  feeMonth: string; // YYYY-MM format
}

/**
 * Fee calculation response
 */
export interface FeeCalculationResponse {
  studentId: number;
  studentName: string;
  class: string;
  feeMonth: string;
  feeBreakdown: {
    feeStructureId: number;
    feeType: FeeType;
    feeName: string;
    amount: number;
    frequency: FeeFrequency;
  }[];
  totalAmount: number;
  discountApplied?: number;
  netAmount: number;
}
