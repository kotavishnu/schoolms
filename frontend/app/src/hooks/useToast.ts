/**
 * useToast Hook
 * Wrapper for Sonner toast notifications
 */

import { toast as sonnerToast } from 'sonner';
import { TOAST_DURATION } from '../utils/constants';

export function useToast() {
  return {
    success: (message: string, description?: string) => {
      sonnerToast.success(message, {
        description,
        duration: TOAST_DURATION.SUCCESS,
      });
    },

    error: (message: string, description?: string) => {
      sonnerToast.error(message, {
        description,
        duration: TOAST_DURATION.ERROR,
      });
    },

    info: (message: string, description?: string) => {
      sonnerToast.info(message, {
        description,
        duration: TOAST_DURATION.INFO,
      });
    },

    warning: (message: string, description?: string) => {
      sonnerToast.warning(message, {
        description,
        duration: TOAST_DURATION.INFO,
      });
    },

    loading: (message: string) => {
      return sonnerToast.loading(message);
    },

    dismiss: (toastId?: string | number) => {
      sonnerToast.dismiss(toastId);
    },
  };
}

export default useToast;
