import { DocumentType } from './document.models';

export interface ManualFieldOption {
  fieldName: string;
  label: string;
}

const INVOICE_FIELDS: ManualFieldOption[] = [
  { fieldName: 'invoice_id', label: 'Invoice number' },
  { fieldName: 'invoice_date', label: 'Invoice date' },
  { fieldName: 'supplier_name', label: 'Supplier name' },
  { fieldName: 'total_amount', label: 'Total amount' },
  { fieldName: 'net_amount', label: 'Net amount' },
  { fieldName: 'vat_amount', label: 'VAT amount' },
  { fieldName: 'currency', label: 'Currency' },
  { fieldName: 'due_date', label: 'Due date' },
];

const RECEIPT_FIELDS: ManualFieldOption[] = [
  { fieldName: 'supplier_name', label: 'Supplier / merchant' },
  { fieldName: 'total_amount', label: 'Total amount' },
  { fieldName: 'currency', label: 'Currency' },
  { fieldName: 'receipt_date', label: 'Receipt date' },
  { fieldName: 'expense_date', label: 'Expense date' },
  { fieldName: 'transaction_date', label: 'Transaction date' },
  { fieldName: 'purchase_date', label: 'Purchase date' },
];

const BANK_STATEMENT_FIELDS: ManualFieldOption[] = [
  { fieldName: 'account_number', label: 'Account number' },
  { fieldName: 'bank_name', label: 'Bank name' },
  { fieldName: 'client_name', label: 'Client name' },
  { fieldName: 'statement_date', label: 'Statement date' },
  { fieldName: 'starting_balance', label: 'Starting balance' },
  { fieldName: 'ending_balance', label: 'Ending balance' },
];

const FORM_FIELDS: ManualFieldOption[] = [
  { fieldName: 'applicant', label: 'Applicant' },
  { fieldName: 'approved', label: 'Approved' },
  { fieldName: 'reference', label: 'Reference' },
];

const MANUAL_FIELDS_BY_TYPE: Partial<Record<DocumentType, ManualFieldOption[]>> = {
  INVOICE: INVOICE_FIELDS,
  RECEIPT: RECEIPT_FIELDS,
  BANK_STATEMENT: BANK_STATEMENT_FIELDS,
  FORM: FORM_FIELDS,
  OTHER: INVOICE_FIELDS,
};

export const CUSTOM_FIELD_OPTION_VALUE = '__custom__';

export function getManualFieldOptions(documentType: DocumentType | string | null | undefined): ManualFieldOption[] {
  if (!documentType) {
    return INVOICE_FIELDS;
  }

  return MANUAL_FIELDS_BY_TYPE[documentType as DocumentType] ?? INVOICE_FIELDS;
}

export function slugifyCustomFieldKey(label: string): string {
  const slug = label
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '_')
    .replace(/^_+|_+$/g, '')
    .replace(/_+/g, '_');

  return slug.length > 0 ? slug : 'field';
}

export function buildCustomFieldName(label: string): string {
  return `custom.${slugifyCustomFieldKey(label)}`;
}

const FIELD_LABELS: Record<string, string> = {
  invoice_id: 'Invoice number',
  invoice_date: 'Invoice date',
  supplier_name: 'Supplier name',
  total_amount: 'Total amount',
  net_amount: 'Net amount',
  vat_amount: 'VAT amount',
  currency: 'Currency',
  due_date: 'Due date',
  receipt_date: 'Receipt date',
  expense_date: 'Expense date',
  account_number: 'Account number',
  bank_name: 'Bank name',
};

export function formatFieldLabel(fieldName: string, displayName?: string | null): string {
  if (displayName?.trim()) {
    return displayName.trim();
  }

  const normalized = fieldName.trim().toLowerCase();
  if (FIELD_LABELS[normalized]) {
    return FIELD_LABELS[normalized];
  }

  if (normalized.startsWith('custom.')) {
    const suffix = normalized.slice('custom.'.length).replaceAll('_', ' ');
    return suffix.charAt(0).toUpperCase() + suffix.slice(1);
  }

  return normalized.replaceAll('_', ' ');
}
