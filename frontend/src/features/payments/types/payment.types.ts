/**
 * TypeScript type definitions for Payment & Receipts Module
 */

/**
 * Payment method enumeration
 */
export type PaymentMethod = 'Cash' | 'Card' | 'Bank Transfer' | 'UPI' | 'Cheque';

/**
 * Payment status enumeration
 */
export type PaymentStatus = 'Completed' | 'Pending' | 'Failed' | 'Refunded' | 'Partially Refunded';

/**
 * Refund status enumeration
 */
export type RefundStatus = 'Pending' | 'Approved' | 'Rejected' | 'Completed';

/**
 * Fee item paid in a payment transaction
 */
export interface FeeItem {
  /** Fee assignment ID */
  feeId: number;
  /** Fee type name (e.g., Tuition, Sports, Lab) */
  feeName: string;
  /** Amount due for this fee */
  amountDue: number;
  /** Amount paid in this transaction */
  amountPaid: number;
  /** Remaining balance after payment */
  remainingBalance: number;
}

/**
 * Complete payment information
 */
export interface Payment {
  /** Payment ID */
  id: number;
  /** Receipt number (auto-generated) */
  receiptNumber: string;
  /** Student ID */
  studentId: number;
  /** Student name */
  studentName: string;
  /** Student class */
  studentClass: string;
  /** Fee items paid */
  feeItems: FeeItem[];
  /** Total amount paid */
  totalAmount: number;
  /** Previous outstanding balance */
  previousBalance: number;
  /** Remaining balance after payment */
  remainingBalance: number;
  /** Payment date */
  paymentDate: string;
  /** Payment method */
  paymentMethod: PaymentMethod;
  /** Transaction reference (for online payments) */
  transactionReference?: string;
  /** Payment notes */
  notes?: string;
  /** Payment status */
  status: PaymentStatus;
  /** Created by user ID */
  createdBy?: number;
  /** Created by user name */
  createdByName?: string;
  /** Creation timestamp */
  createdAt: string;
  /** Last update timestamp */
  updatedAt: string;
}

/**
 * Receipt information for display and printing
 */
export interface Receipt {
  /** Payment details */
  payment: Payment;
  /** School information */
  school: {
    name: string;
    address: string;
    phone: string;
    email: string;
    website?: string;
    logoUrl?: string;
  };
  /** Student details */
  student: {
    id: number;
    studentId: string;
    name: string;
    class: string;
    section: string;
    rollNumber?: string;
    guardianName: string;
    guardianPhone: string;
  };
  /** Academic year */
  academicYear: string;
}

/**
 * Refund information
 */
export interface Refund {
  /** Refund ID */
  id: number;
  /** Original payment ID */
  paymentId: number;
  /** Receipt number of original payment */
  receiptNumber: string;
  /** Student ID */
  studentId: number;
  /** Student name */
  studentName: string;
  /** Refund amount */
  refundAmount: number;
  /** Refund reason */
  reason: string;
  /** Refund status */
  status: RefundStatus;
  /** Requested by user ID */
  requestedBy: number;
  /** Requested by user name */
  requestedByName: string;
  /** Approved by user ID */
  approvedBy?: number;
  /** Approved by user name */
  approvedByName?: string;
  /** Approval/rejection notes */
  approvalNotes?: string;
  /** Request date */
  requestDate: string;
  /** Approval date */
  approvalDate?: string;
  /** Refund date */
  refundDate?: string;
  /** Creation timestamp */
  createdAt: string;
  /** Last update timestamp */
  updatedAt: string;
}

/**
 * Payment form data for recording payments
 */
export interface PaymentFormData {
  /** Student ID */
  studentId: number;
  /** Fee items to pay */
  feeItems: {
    feeId: number;
    amountPaid: number;
  }[];
  /** Payment date */
  paymentDate: string;
  /** Payment method */
  paymentMethod: PaymentMethod;
  /** Transaction reference */
  transactionReference?: string;
  /** Payment notes */
  notes?: string;
}

/**
 * Refund form data
 */
export interface RefundFormData {
  /** Payment ID */
  paymentId: number;
  /** Refund amount */
  refundAmount: number;
  /** Refund reason */
  reason: string;
  /** Full or partial refund */
  isFullRefund: boolean;
}

/**
 * Filters for payment list
 */
export interface PaymentFilters {
  /** Search by student name or receipt number */
  search?: string;
  /** Filter by payment method */
  paymentMethod?: PaymentMethod;
  /** Filter by status */
  status?: PaymentStatus;
  /** Filter by date range start */
  startDate?: string;
  /** Filter by date range end */
  endDate?: string;
  /** Filter by student ID */
  studentId?: number;
  /** Pagination - page number */
  page?: number;
  /** Pagination - items per page */
  limit?: number;
}

/**
 * Paginated payment list response
 */
export interface PaymentListResponse {
  /** List of payments */
  payments: Payment[];
  /** Total count */
  total: number;
  /** Current page */
  page: number;
  /** Items per page */
  limit: number;
  /** Total pages */
  totalPages: number;
}

/**
 * Dashboard statistics
 */
export interface DashboardStats {
  /** Today's total collections */
  todayCollections: number;
  /** This month's total collections */
  monthCollections: number;
  /** This year's total collections */
  yearCollections: number;
  /** Collection by payment method */
  collectionsByMethod: {
    method: PaymentMethod;
    amount: number;
    count: number;
  }[];
  /** Recent payments (last 10) */
  recentPayments: Payment[];
  /** Pending payments summary */
  pendingPayments: {
    count: number;
    totalAmount: number;
  };
  /** Revenue trends (last 12 months) */
  revenueTrends: {
    month: string;
    amount: number;
  }[];
  /** Top paying classes */
  topClasses: {
    className: string;
    totalAmount: number;
    studentCount: number;
  }[];
}

/**
 * Student's pending fee information
 */
export interface PendingFee {
  /** Fee assignment ID */
  id: number;
  /** Fee type ID */
  feeTypeId: number;
  /** Fee type name */
  feeTypeName: string;
  /** Total amount */
  totalAmount: number;
  /** Amount paid */
  amountPaid: number;
  /** Amount pending */
  amountPending: number;
  /** Due date */
  dueDate: string;
  /** Is overdue */
  isOverdue: boolean;
}

/**
 * Student fee summary
 */
export interface StudentFeeSummary {
  /** Student ID */
  studentId: number;
  /** Student name */
  studentName: string;
  /** Class */
  studentClass: string;
  /** Total fees assigned */
  totalFees: number;
  /** Total amount paid */
  totalPaid: number;
  /** Total outstanding */
  totalOutstanding: number;
  /** Pending fees */
  pendingFees: PendingFee[];
}

/**
 * Payment creation response
 */
export interface PaymentCreateResponse {
  /** Created payment */
  payment: Payment;
  /** Success message */
  message: string;
}

/**
 * Payment export options
 */
export interface PaymentExportOptions {
  /** Export format */
  format: 'excel' | 'pdf';
  /** Filters to apply */
  filters?: PaymentFilters;
}
