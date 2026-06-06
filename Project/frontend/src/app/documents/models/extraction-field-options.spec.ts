import { describe, expect, it } from 'vitest';

import {
  buildCustomFieldName,
  formatFieldLabel,
  getManualFieldOptions,
  slugifyCustomFieldKey,
} from './extraction-field-options';

describe('extraction-field-options', () => {
  it('returns invoice fields for invoice documents', () => {
    const fields = getManualFieldOptions('INVOICE');

    expect(fields).toContainEqual({
      fieldName: 'invoice_id',
      label: 'Invoice number',
    });

    expect(fields).toContainEqual({
      fieldName: 'total_amount',
      label: 'Total amount',
    });
  });

  it('returns receipt date options for receipt documents', () => {
    const fields = getManualFieldOptions('RECEIPT');

    expect(fields.map((field) => field.fieldName)).toContain('receipt_date');
    expect(fields.map((field) => field.fieldName)).toContain('expense_date');
  });

  it('slugifies a custom display label', () => {
    expect(slugifyCustomFieldKey('  Project Code 2026  ')).toBe(
      'project_code_2026'
    );
  });

  it('builds a prefixed custom field name', () => {
    expect(buildCustomFieldName('Project Code')).toBe('custom.project_code');
  });

  it('prefers the explicit display name', () => {
    expect(formatFieldLabel('custom.project_code', 'Internal Project Code')).toBe(
      'Internal Project Code'
    );
  });

  it('formats known canonical field names', () => {
    expect(formatFieldLabel('invoice_id')).toBe('Invoice number');
    expect(formatFieldLabel('total_amount')).toBe('Total amount');
  });

  it('formats custom field names when display name is missing', () => {
    expect(formatFieldLabel('custom.project_code')).toBe('Project code');
  });
});