import { apiClient } from '@/api/client';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import type {
  Payment,
  PaymentListResponse,
  PaymentFilters,
  PaymentFormData,
  PaymentCreateResponse,
  Receipt,
  DashboardStats,
  StudentFeeSummary,
  Refund,
  RefundFormData,
  PaymentExportOptions,
} from '../types/payment.types';
import toast from 'react-hot-toast';

/**
 * Payment API service functions
 */

const PAYMENTS_ENDPOINT = '/api/v1/payments';

/**
 * Get paginated list of payments with optional filters
 *
 * @param filters - Search, date, method, status, and pagination filters
 * @returns Paginated payment list
 */
export const getPayments = async (filters?: PaymentFilters): Promise<PaymentListResponse> => {
  const params = new URLSearchParams();

  if (filters?.search) params.append('search', filters.search);
  if (filters?.paymentMethod) params.append('paymentMethod', filters.paymentMethod);
  if (filters?.status) params.append('status', filters.status);
  if (filters?.startDate) params.append('startDate', filters.startDate);
  if (filters?.endDate) params.append('endDate', filters.endDate);
  if (filters?.studentId) params.append('studentId', filters.studentId.toString());
  if (filters?.page) params.append('page', filters.page.toString());
  if (filters?.limit) params.append('limit', filters.limit.toString());

  const response = await apiClient.get<PaymentListResponse>(
    `${PAYMENTS_ENDPOINT}?${params.toString()}`
  );

  return response.data;
};

/**
 * Get a single payment by ID
 *
 * @param id - Payment ID
 * @returns Payment details
 */
export const getPaymentById = async (id: number): Promise<Payment> => {
  const response = await apiClient.get<Payment>(`${PAYMENTS_ENDPOINT}/${id}`);
  return response.data;
};

/**
 * Get receipt data for a payment
 *
 * @param id - Payment ID
 * @returns Receipt information
 */
export const getPaymentReceipt = async (id: number): Promise<Receipt> => {
  const response = await apiClient.get<Receipt>(`${PAYMENTS_ENDPOINT}/receipt/${id}`);
  return response.data;
};

/**
 * Get payment history for a specific student
 *
 * @param studentId - Student ID
 * @returns List of student's payments
 */
export const getStudentPayments = async (studentId: number): Promise<Payment[]> => {
  const response = await apiClient.get<Payment[]>(`${PAYMENTS_ENDPOINT}/student/${studentId}`);
  return response.data;
};

/**
 * Get student's pending fees and fee summary
 *
 * @param studentId - Student ID
 * @returns Student fee summary with pending fees
 */
export const getStudentFeeSummary = async (studentId: number): Promise<StudentFeeSummary> => {
  const response = await apiClient.get<StudentFeeSummary>(
    `${PAYMENTS_ENDPOINT}/student/${studentId}/fees`
  );
  return response.data;
};

/**
 * Get dashboard statistics
 *
 * @returns Dashboard stats
 */
export const getDashboardStats = async (): Promise<DashboardStats> => {
  const response = await apiClient.get<DashboardStats>(`${PAYMENTS_ENDPOINT}/dashboard`);
  return response.data;
};

/**
 * Record a new payment
 *
 * @param data - Payment form data
 * @returns Created payment
 */
export const createPayment = async (data: PaymentFormData): Promise<PaymentCreateResponse> => {
  const response = await apiClient.post<PaymentCreateResponse>(PAYMENTS_ENDPOINT, data);
  return response.data;
};

/**
 * Initiate a refund for a payment
 *
 * @param id - Payment ID
 * @param data - Refund form data
 * @returns Refund information
 */
export const refundPayment = async (id: number, data: RefundFormData): Promise<Refund> => {
  const response = await apiClient.post<Refund>(`${PAYMENTS_ENDPOINT}/${id}/refund`, data);
  return response.data;
};

/**
 * Export payments data
 *
 * @param options - Export options (format and filters)
 * @returns Blob data for download
 */
export const exportPayments = async (options: PaymentExportOptions): Promise<Blob> => {
  const params = new URLSearchParams();
  params.append('format', options.format);

  if (options.filters?.search) params.append('search', options.filters.search);
  if (options.filters?.paymentMethod) params.append('paymentMethod', options.filters.paymentMethod);
  if (options.filters?.status) params.append('status', options.filters.status);
  if (options.filters?.startDate) params.append('startDate', options.filters.startDate);
  if (options.filters?.endDate) params.append('endDate', options.filters.endDate);

  const response = await apiClient.get(`${PAYMENTS_ENDPOINT}/export?${params.toString()}`, {
    responseType: 'blob',
  });

  return response.data;
};

/**
 * React Query Hooks
 */

/**
 * Hook to fetch payments with filters
 */
export const usePayments = (filters?: PaymentFilters) => {
  return useQuery({
    queryKey: ['payments', filters],
    queryFn: () => getPayments(filters),
    staleTime: 2 * 60 * 1000, // 2 minutes
  });
};

/**
 * Hook to fetch a single payment by ID
 */
export const usePayment = (id: number) => {
  return useQuery({
    queryKey: ['payments', id],
    queryFn: () => getPaymentById(id),
    staleTime: 5 * 60 * 1000, // 5 minutes
    enabled: !!id,
  });
};

/**
 * Hook to fetch payment receipt
 */
export const usePaymentReceipt = (id: number) => {
  return useQuery({
    queryKey: ['payments', 'receipt', id],
    queryFn: () => getPaymentReceipt(id),
    staleTime: 10 * 60 * 1000, // 10 minutes
    enabled: !!id,
  });
};

/**
 * Hook to fetch student payments
 */
export const useStudentPayments = (studentId: number) => {
  return useQuery({
    queryKey: ['payments', 'student', studentId],
    queryFn: () => getStudentPayments(studentId),
    staleTime: 2 * 60 * 1000,
    enabled: !!studentId,
  });
};

/**
 * Hook to fetch student fee summary
 */
export const useStudentFeeSummary = (studentId: number) => {
  return useQuery({
    queryKey: ['payments', 'student', studentId, 'fees'],
    queryFn: () => getStudentFeeSummary(studentId),
    staleTime: 1 * 60 * 1000, // 1 minute - fees change frequently
    enabled: !!studentId,
  });
};

/**
 * Hook to fetch dashboard statistics
 */
export const useDashboardStats = () => {
  return useQuery({
    queryKey: ['payments', 'dashboard'],
    queryFn: getDashboardStats,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Hook to create a payment
 */
export const useCreatePayment = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createPayment,
    onSuccess: (data) => {
      toast.success(data.message || 'Payment recorded successfully');
      queryClient.invalidateQueries({ queryKey: ['payments'] });
      queryClient.invalidateQueries({ queryKey: ['students'] });
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to record payment';
      toast.error(message);
    },
  });
};

/**
 * Hook to refund a payment
 */
export const useRefundPayment = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ paymentId, data }: { paymentId: number; data: RefundFormData }) =>
      refundPayment(paymentId, data),
    onSuccess: () => {
      toast.success('Refund initiated successfully');
      queryClient.invalidateQueries({ queryKey: ['payments'] });
    },
    onError: (error: any) => {
      const message = error.response?.data?.detail || 'Failed to initiate refund';
      toast.error(message);
    },
  });
};

/**
 * Export all payment API functions
 */
export const paymentApi = {
  getPayments,
  getPaymentById,
  getPaymentReceipt,
  getStudentPayments,
  getStudentFeeSummary,
  getDashboardStats,
  createPayment,
  refundPayment,
  exportPayments,
};

export default paymentApi;
