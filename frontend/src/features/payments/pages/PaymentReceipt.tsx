import React, { useRef, useEffect } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import { useReactToPrint } from 'react-to-print';
import { ArrowLeft, Printer, Download, Mail, MessageSquare } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { ReceiptTemplate } from '../components/ReceiptTemplate';
import { usePaymentReceipt } from '../api/paymentApi';
import { ROUTES } from '@/shared/constants/routes';
import toast from 'react-hot-toast';

/**
 * Payment Receipt Page
 * Displays a payment receipt with print and download functionality
 */
export const PaymentReceipt: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const receiptRef = useRef<HTMLDivElement>(null);

  const paymentId = id ? parseInt(id, 10) : 0;
  const autoPrint = searchParams.get('print') === 'true';

  // Fetch receipt data
  const { data: receipt, isLoading, error } = usePaymentReceipt(paymentId);

  // Print handler
  const handlePrint = useReactToPrint({
    contentRef: receiptRef,
    documentTitle: `Receipt_${receipt?.payment.receiptNumber}`,
    pageStyle: `
      @page {
        size: A4;
        margin: 10mm;
      }
      @media print {
        body {
          -webkit-print-color-adjust: exact;
          print-color-adjust: exact;
        }
        .no-print {
          display: none !important;
        }
      }
    `,
  });

  // Auto-print if requested via URL parameter
  useEffect(() => {
    if (autoPrint && receipt && receiptRef.current) {
      // Delay to ensure content is rendered
      setTimeout(() => {
        handlePrint();
      }, 500);
    }
  }, [autoPrint, receipt, handlePrint]);

  // Download as PDF (using browser's print to PDF)
  const handleDownload = () => {
    toast('Use your browser\'s "Save as PDF" option when printing');
    handlePrint();
  };

  // Send via email (placeholder)
  const handleSendEmail = () => {
    toast('Email functionality will be implemented soon');
    // TODO: Implement email sending API
  };

  // Send via SMS (placeholder)
  const handleSendSMS = () => {
    toast('SMS functionality will be implemented soon');
    // TODO: Implement SMS sending API
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 py-6 px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
            <p className="text-gray-500">Loading receipt...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error || !receipt) {
    return (
      <div className="min-h-screen bg-gray-50 py-6 px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          <Card>
            <div className="p-12 text-center">
              <div className="text-red-500 mb-4">
                <svg
                  className="w-16 h-16 mx-auto"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                  />
                </svg>
              </div>
              <h2 className="text-2xl font-bold text-gray-900 mb-2">Receipt Not Found</h2>
              <p className="text-gray-600 mb-6">
                {(error as any)?.message || 'The receipt you are looking for does not exist.'}
              </p>
              <Button onClick={() => navigate(ROUTES.PAYMENTS)}>
                <ArrowLeft className="w-4 h-4 mr-2" />
                Back to Payments
              </Button>
            </div>
          </Card>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-6 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto">
        {/* Action Bar - Hidden in print */}
        <div className="mb-6 no-print">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => navigate('/payments/history')}
            >
              <ArrowLeft className="w-4 h-4 mr-2" />
              Back to Payment History
            </Button>

            <div className="flex flex-wrap gap-2">
              <Button variant="outline" onClick={handlePrint}>
                <Printer className="w-4 h-4 mr-2" />
                Print
              </Button>
              <Button variant="outline" onClick={handleDownload}>
                <Download className="w-4 h-4 mr-2" />
                Download PDF
              </Button>
              <Button variant="outline" onClick={handleSendEmail}>
                <Mail className="w-4 h-4 mr-2" />
                Email
              </Button>
              <Button variant="outline" onClick={handleSendSMS}>
                <MessageSquare className="w-4 h-4 mr-2" />
                Send SMS
              </Button>
            </div>
          </div>
        </div>

        {/* Receipt Template */}
        <div className="bg-white shadow-lg">
          <ReceiptTemplate ref={receiptRef} receipt={receipt} />
        </div>

        {/* Additional Actions - Hidden in print */}
        <div className="mt-6 text-center text-sm text-gray-500 no-print">
          <p>
            This receipt is valid and digitally recorded in the School Management System.
          </p>
          <p className="mt-1">
            Receipt Number: <span className="font-semibold">{receipt.payment.receiptNumber}</span>
          </p>
        </div>
      </div>

      {/* Print Styles */}
      <style>
        {`
          @media print {
            body {
              margin: 0;
              padding: 0;
            }
            .no-print {
              display: none !important;
            }
            /* Remove page breaks within the receipt */
            .receipt-template {
              page-break-inside: avoid;
            }
            /* Ensure proper margins for printing */
            @page {
              margin: 10mm;
            }
          }
        `}
      </style>
    </div>
  );
};

export default PaymentReceipt;
