export type DocumentType =
  | 'INVOICE'
  | 'RECEIPT'
  | 'BANK_STATEMENT'
  | 'FORM'
  | 'OTHER';

export type ManualClassificationDocumentType =
  | 'INVOICE'
  | 'RECEIPT'
  | 'BANK_STATEMENT'
  | 'FORM';

export type DocumentStatus =
  | 'UPLOADED'
  | 'PROCESSING_FAILED'
  | 'EXTRACTED'
  | 'UNDER_REVIEW'
  | 'NEEDS_CLASSIFICATION_REVIEW'
  | 'READY_FOR_APPROVAL'
  | 'APPROVED'
  | 'REJECTED'
  | 'COMPLETED';

export interface DocumentTypeOption {
  value: DocumentType;
  label: string;
  description: string;
}

export const DOCUMENT_TYPE_OPTIONS: DocumentTypeOption[] = [
  {
    value: 'INVOICE',
    label: 'Invoice',
    description:
      'Supplier invoice with invoice number, date, supplier, amount and currency.',
  },
  {
    value: 'RECEIPT',
    label: 'Receipt / Expense',
    description:
      'Receipt or expense document proving a completed payment.',
  },
  {
    value: 'BANK_STATEMENT',
    label: 'Bank statement',
    description:
      'Bank account statement with account number, balances and transactions.',
  },
  {
    value: 'FORM',
    label: 'Form',
    description:
      'Structured form with labeled fields, tables or checkboxes.',
  },
  {
    value: 'OTHER',
    label: 'Other / Auto classify',
    description:
      'Use AI classifier first. If confidence is low, manual review will be required.',
  },
];

export const MANUAL_CLASSIFICATION_DOCUMENT_TYPES: DocumentTypeOption[] =
  DOCUMENT_TYPE_OPTIONS.filter((type) => type.value !== 'OTHER');

export interface DocflowDocument {
  id: number;
  companyId: number;
  createdBy: number;
  name: string;
  fileType: string;
  documentType: DocumentType;
  detectedDocumentType: DocumentType | null;
  classificationConfidence: number | null;
  processorIdUsed: string | null;
  storagePath: string;
  uploadDate: string;
  fileSize: number;
  documentStatus: DocumentStatus;
}

export interface DocumentCreateRequest {
  companyId: number;
  createdByUserId: number;
  name: string;
  fileType: string;
  documentType: DocumentType;
  storagePath: string;
  fileSize: number;
}

export interface DocumentUploadRequest {
  file: File;
  companyId: number;
  createdByUserId: number;
  documentType: DocumentType;
  name?: string;
}

export interface DocumentUpdateRequest {
  name?: string;
  documentType?: DocumentType;
  documentStatus?: DocumentStatus;
}

export interface ConfirmDocumentTypeRequest {
  documentType: ManualClassificationDocumentType;
}

export interface DocumentFilterRequest {
  name?: string;
  documentType?: DocumentType;
  documentStatus?: DocumentStatus;
  companyId?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
}