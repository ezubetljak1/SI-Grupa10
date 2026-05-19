import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { DocumentApiService} from '../../services/document-api.service';
import {
  FileTypeIconComponent,
  PageHeaderComponent,
  StatusBadgeComponent,
  UiCardComponent,
} from '../../shared/components';
import {
  DOCUMENT_TYPE_OPTIONS,
  DocflowDocument,
  MANUAL_CLASSIFICATION_DOCUMENT_TYPES,
  ManualClassificationDocumentType,
} from '../models/document.models';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ToastrService } from 'ngx-toastr';
import { Extraction, ExtractionField } from '../models/extraction.models';
import { AuthService } from '../../auth/services/auth.service';

interface EditState {
  fieldId: number;
  editValue: string;
  saving: boolean;
  validationError: string | null;
}

interface BackendValidationError {
  code?: string;
  message?: string;
  payload?: string;
}

@Component({
  selector: 'app-document-detail-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    PageHeaderComponent,
    UiCardComponent,
    FileTypeIconComponent,
    StatusBadgeComponent,
  ],
  templateUrl: './document-detail-page.html',
  styleUrl: './document-detail-page.scss',
})
export class DocumentDetailPageComponent implements OnInit {
  private readonly documentApiService = inject(DocumentApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly toastr = inject(ToastrService);
  private readonly authService = inject(AuthService);

  document: DocflowDocument | null = null;
  loading = false;
  downloading = false;

  private readonly sanitizer = inject(DomSanitizer);

  fileUrl: SafeResourceUrl | null = null;

  isPdf = false;
  isImage = false;

  extractionFields: ExtractionField[] = [];
  extractionLoading = false;
  extractionRunning = false;
  extractionError: string | null = null;

  extractionId: number | null = null;
  editState: EditState | null = null;
  confirmingExtraction = false;

  readonly documentTypeOptions = DOCUMENT_TYPE_OPTIONS;
  readonly manualClassificationTypeOptions = MANUAL_CLASSIFICATION_DOCUMENT_TYPES;

  selectedManualDocumentType: ManualClassificationDocumentType = 'FORM';
  confirmingDocumentType = false;

  get canManageExtraction(): boolean {
    return this.authService.hasRole(['ADMIN', 'OPERATOR']);
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      if (!Number.isFinite(id) || id <= 0) {
        this.toastr.error('Invalid document id.', 'Error');
        return;
      }
      this.resetDocumentState();
      this.loadDocument(id);
    });
  }

  loadDocument(id: number): void {
    this.loading = true;
    this.documentApiService.getById(id).subscribe({
      next: (response) => {
        this.loading = false;
        this.document = response.payload;
        this.selectedManualDocumentType = this.resolveDefaultManualDocumentType(this.document);
        const rawUrl = `/api/documents/${this.document.id}/preview`;
        this.fileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(rawUrl);
        this.isPdf = this.document.fileType === 'application/pdf';
        this.isImage = this.document.fileType?.startsWith('image/');
        this.extractionError = null;
        this.extractionFields = [];
        if (this.shouldLoadExtractionForStatus(this.document.documentStatus)) {
          this.loadExtraction();
        }
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const message = this.extractErrorMessage(err.error) ?? 'Failed to load document.';
        this.document = null;
        this.fileUrl = null;
        this.isPdf = false;
        this.isImage = false;
        this.toastr.error(message, 'Error');
      },
    });
  }

  confirmDocumentType(): void {
    if (!this.document) {
      return;
    }

    const documentId = this.document.id;
    this.confirmingDocumentType = true;

    this.documentApiService
      .confirmDocumentType(documentId, this.selectedManualDocumentType)
      .subscribe({
        next: (response) => {
          this.confirmingDocumentType = false;
          this.document = response.payload;
          this.extractionError = null;
          this.toastr.success(
            'Document type confirmed. You can run extraction again.',
            'Classification confirmed'
          );
          this.loadDocument(documentId);
        },
        error: (err: HttpErrorResponse) => {
          this.confirmingDocumentType = false;
          const message =
            this.extractErrorMessage(err.error) ??
            'Document type confirmation failed.';
          this.toastr.error(message, 'Error');
        },
      });
  }

  loadExtraction(): void {
    if (!this.document) return;
    this.extractionLoading = true;
    this.extractionError = null;
    this.documentApiService.getExtraction(this.document.id).subscribe({
      next: (response) => {
        const extraction: Extraction = response.payload;
        this.extractionId = extraction?.id ?? null;
        this.loadExtractionFields();
      },
      error: () => {
        this.loadExtractionFields();
      },
    });
  }

  loadExtractionFields(): void {
    if (!this.document) return;
    this.extractionLoading = true;
    this.extractionError = null;
    this.documentApiService.getExtractionFields(this.document.id).subscribe({
      next: (response) => {
        this.extractionLoading = false;
        this.extractionFields = response.payload ?? [];
      },
      error: (err: HttpErrorResponse) => {
        this.extractionLoading = false;
        this.extractionFields = [];
        this.extractionError =
          this.extractErrorMessage(err.error) ?? 'Failed to load extracted fields.';
      },
    });
  }

  runExtraction(): void {
    if (!this.document) return;
    const documentId = this.document.id;
    this.extractionRunning = true;
    this.extractionError = null;
    this.documentApiService.processExtraction(this.document.id).subscribe({
      next: (response) => {
        this.extractionRunning = false;
        this.extractionId = response.payload?.id ?? null;
        this.extractionFields = response.payload?.fields ?? [];
        this.toastr.success('Extraction completed.', 'Success');
        this.loadDocument(documentId);
      },
      error: (err: HttpErrorResponse) => {
        this.extractionRunning = false;
        const message = this.extractExtractionErrorMessage(err.error, 'Extraction failed.');
        this.extractionError = message;
        this.extractionFields = [];
        this.toastr.error(message, 'Error');
        this.loadDocument(documentId);
      },
    });
  }

  retryExtraction(): void {
    if (!this.document) return;
    const documentId = this.document.id;
    this.extractionRunning = true;
    this.extractionError = null;
    this.documentApiService.retryExtraction(this.document.id).subscribe({
      next: (response) => {
        this.extractionRunning = false;
        this.extractionId = response.payload?.id ?? null;
        this.extractionFields = response.payload?.fields ?? [];
        this.toastr.success('Extraction retried.', 'Success');
        this.loadDocument(documentId);
      },
      error: (err: HttpErrorResponse) => {
        this.extractionRunning = false;
        const message = this.extractExtractionErrorMessage(err.error, 'Retry extraction failed.');
        this.extractionError = message;
        this.extractionFields = [];
        this.toastr.error(message, 'Error');
        this.loadDocument(documentId);
      },
    });
  }

  startEdit(field: ExtractionField): void {
    if (this.editState && this.editState.fieldId !== field.id) {
      this.cancelEdit();
    }
    this.editState = {
      fieldId: field.id,
      editValue: field.value ?? '',
      saving: false,
      validationError: null,
    };
  }

  cancelEdit(): void {
    this.editState = null;
  }

  isEditing(field: ExtractionField): boolean {
    return this.editState?.fieldId === field.id;
  }

  validateEditValue(field: ExtractionField, value: string): string | null {
    const trimmed = value.trim();

    if (trimmed === '') {
      return 'Field value cannot be empty.';
    }

    const name = field.fieldName.toLowerCase();

    if (name.includes('date') || name.includes('datum')) {
      if (!this.isValidDateValue(trimmed)) {
        return 'Invalid date format. Supported formats are ISO YYYY-MM-DD or European DD.MM.YYYY / DD/MM/YYYY. US MM/DD/YYYY format is not supported.';
      }
    }

    if (
      name.includes('amount') ||
      name.includes('balance') ||
      name.includes('deposit') ||
      name.includes('withdrawal') ||
      name.includes('iznos') ||
      name.includes('cijena') ||
      name.endsWith('_price') ||
      name.endsWith('_quantity') ||
      [
        'net_amount',
        'vat_amount',
        'total_amount',
        'total_tax_amount',
        'tax_amount',
        'subtotal_amount',
        'amount',
        'price',
        'unit_price',
        'quantity',
        'qty',
        'starting_balance',
        'ending_balance',
        'opening_balance',
        'closing_balance',
        'current_balance',
        'transaction_deposit',
        'transaction_withdrawal',
        'transaction_amount',
        'table_item/transaction_deposit',
        'table_item/transaction_withdrawal',
        'table_item/transaction_amount',
      ].includes(name)
    ) {
      if (!this.isValidNumericValue(trimmed)) {
        return 'Field must be a numeric value only, without currency symbols or additional text. Use for example 1500, 1500.50 or 1500,50.';
      }
    }
    return null;
  }

  isClassificationReviewRequired(): boolean {
    return this.document?.documentStatus === 'NEEDS_CLASSIFICATION_REVIEW';
  }

  formatDocumentTypeLabel(documentType: string | null | undefined): string {
    if (!documentType) {
      return '—';
    }

    return (
      this.documentTypeOptions.find((type) => type.value === documentType)?.label ??
      documentType.replaceAll('_', ' ')
    );
  }

  formatClassificationConfidence(confidence: number | null | undefined): string {
    if (confidence === null || confidence === undefined) {
      return '—';
    }

    const normalized = confidence <= 1 ? confidence * 100 : confidence;
    return `${Math.round(normalized)}%`;
  }

  formatProcessorLabel(document: DocflowDocument | null | undefined): string {
    if (!document?.processorIdUsed) {
      return '—';
    }

    if (document.documentStatus === 'NEEDS_CLASSIFICATION_REVIEW') {
      return 'AI classifier';
    }

    const typeForProcessor = document.documentType ?? document.detectedDocumentType;

    switch (typeForProcessor) {
      case 'INVOICE':
        return 'Invoice parser';
      case 'RECEIPT':
        return 'Expense parser';
      case 'BANK_STATEMENT':
        return 'Bank statement parser';
      case 'FORM':
        return 'Form parser';
      case 'OTHER':
        return 'AI classifier';
      default:
        return 'Document AI processor';
    }
  }

  private resolveDefaultManualDocumentType(
    document: DocflowDocument
  ): ManualClassificationDocumentType {
    const detectedType = document.detectedDocumentType;

    if (
      detectedType === 'INVOICE' ||
      detectedType === 'RECEIPT' ||
      detectedType === 'BANK_STATEMENT' ||
      detectedType === 'FORM'
    ) {
      return detectedType;
    }

    return 'FORM';
  }

  private isValidDateValue(value: string): boolean {
    const matchers = [
      {
        pattern: /^(\d{4})-(\d{2})-(\d{2})$/,
        parts: (match: RegExpMatchArray) => [Number(match[1]), Number(match[2]), Number(match[3])],
      },
      {
        pattern: /^(\d{2})\.(\d{2})\.(\d{4})$/,
        parts: (match: RegExpMatchArray) => [Number(match[3]), Number(match[2]), Number(match[1])],
      },
      {
        pattern: /^(\d{2})\/(\d{2})\/(\d{4})$/,
        parts: (match: RegExpMatchArray) => [Number(match[3]), Number(match[2]), Number(match[1])],
      },
    ];

    for (const matcher of matchers) {
      const match = value.match(matcher.pattern);
      if (!match) {
        continue;
      }

      const [year, month, day] = matcher.parts(match);
      const parsed = new Date(Date.UTC(year, month - 1, day));
      return (
        parsed.getUTCFullYear() === year &&
        parsed.getUTCMonth() === month - 1 &&
        parsed.getUTCDate() === day
      );
    }

    return false;
  }

  private isValidNumericValue(value: string): boolean {
    if (value.includes(' ') || value.includes('\t') || (value.includes(',') && value.includes('.'))) {
      return false;
    }

    return /^\d+([.,]\d{1,2})?$/.test(value);
  }

  confirmEdit(field: ExtractionField): void {
    if (!this.editState || this.editState.fieldId !== field.id) return;

    const value = this.editState.editValue;
    const validationError = this.validateEditValue(field, value);

    if (validationError) {
      this.editState.validationError = validationError;
      return;
    }

    if (!this.extractionId) {
      this.toastr.error('Extraction ID not available. Please refresh the page.', 'Error');
      return;
    }

    this.editState.saving = true;
    this.editState.validationError = null;

    this.documentApiService.updateExtractionField(this.extractionId, field.id, value).subscribe({
      next: (response) => {
        const updated = response.payload;
        const idx = this.extractionFields.findIndex((f) => f.id === field.id);
        if (idx !== -1) {
          this.extractionFields[idx] = {
            ...this.extractionFields[idx],
            value: updated.value,
            corrected: updated.corrected,
            placeholder: updated.placeholder,
          };
        }
        this.editState = null;
        this.toastr.success('Changes saved successfully.', 'Success');
      },
      error: (err: HttpErrorResponse) => {
        if (this.editState) {
          this.editState.saving = false;
          this.editState.validationError =
            this.extractErrorMessage(err.error) ?? 'Failed to save. Please try again.';
        }
      },
    });
  }

  formatConfidence(confidence: number | null | undefined): string {
    if (confidence === null || confidence === undefined) return '—';
    if (!Number.isFinite(confidence)) return '—';
    const normalized = confidence <= 1 ? confidence * 100 : confidence;
    return `${Math.round(normalized)}%`;
  }

  downloadDocument(): void {
    if (!this.document) return;
    this.downloading = true;
    this.documentApiService.downloadFile(this.document.id).subscribe({
      next: (blob) => {
        this.downloading = false;
        const objectUrl = window.URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = objectUrl;
        anchor.download = this.resolveDownloadFileName(this.document!);
        anchor.click();
        window.URL.revokeObjectURL(objectUrl);
      },
      error: (err: HttpErrorResponse) => {
        this.downloading = false;
        const message = this.extractErrorMessage(err.error) ?? 'Failed to download file.';
        this.toastr.error(message, 'Error');
      },
    });
  }

  confirmExtraction(): void {
    if (!this.document) return;

    if (this.editState) {
      this.toastr.warning(
        'Save or cancel the current field edit before confirming extraction.',
        'Review required'
      );
      return;
    }

    const documentId = this.document.id;
    this.confirmingExtraction = true;
    this.extractionError = null;

    this.documentApiService.confirmExtraction(documentId).subscribe({
      next: (response) => {
        this.confirmingExtraction = false;
        this.extractionId = response.payload?.id ?? this.extractionId;
        this.extractionFields = response.payload?.fields ?? this.extractionFields;

        this.toastr.success(
          'Extraction confirmed. Document is ready for approval.',
          'Success'
        );

        this.loadDocument(documentId);
      },
      error: (err: HttpErrorResponse) => {
        this.confirmingExtraction = false;

        if (this.hasValidationErrors(err.error)) {
          const message = this.buildExtractionValidationMessage(
            err.error,
            'Review highlighted fields before confirming.'
          );

          this.extractionError = null;
          this.toastr.warning(message, 'Review required');
          return;
        }

        const message = this.extractExtractionErrorMessage(
          err.error,
          'Extraction confirmation failed.'
        );

        this.extractionError = message;
        this.toastr.error(message, 'Error');
      },
    });
  }

  formatFileSize(sizeInBytes: number): string {
    if (sizeInBytes < 1024) return `${sizeInBytes} B`;
    const kb = sizeInBytes / 1024;
    if (kb < 1024) return `${kb.toFixed(2)} KB`;
    return `${(kb / 1024).toFixed(2)} MB`;
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleString('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  isPlaceholderField(field: ExtractionField): boolean {
    return field.placeholder === true;
  }

  isLowConfidenceField(field: ExtractionField): boolean {
    if (field.placeholder) return false;
    if (field.confidence === null || field.confidence === undefined) return false;

    const confidencePercent = field.confidence <= 1 ? field.confidence * 100 : field.confidence;
    return confidencePercent < 70;
  }

  needsManualReview(field: ExtractionField): boolean {
    return (
      this.isPlaceholderField(field) ||
      (this.isLowConfidenceField(field) &&
        !field.corrected &&
        this.shouldLowConfidenceBlockConfirmation(field))
    );
  }

  get missingRequiredFields(): ExtractionField[] {
    return this.extractionFields.filter((field) => this.isPlaceholderField(field));
  }

  get lowConfidenceUnreviewedFields(): ExtractionField[] {
    return this.extractionFields.filter(
      (field) =>
        this.isLowConfidenceField(field) &&
        !field.corrected &&
        this.shouldLowConfidenceBlockConfirmation(field)
    );
  }

  private shouldLowConfidenceBlockConfirmation(field: ExtractionField): boolean {
    const documentType = this.document?.documentType;
    const fieldName = field.fieldName?.trim().toLowerCase() ?? '';

    if (documentType === 'INVOICE') {
      return true;
    }

    if (documentType === 'RECEIPT') {
      return (
        ['supplier_name', 'total_amount', 'currency'].includes(fieldName) ||
        ['receipt_date', 'expense_date', 'transaction_date', 'purchase_date'].includes(fieldName)
      );
    }

    if (documentType === 'BANK_STATEMENT') {
      return (
        ['account_number'].includes(fieldName) ||
        [
          'bank_name',
          'client_name',
          'account_holder_name',
          'customer_name',
          'statement_date',
          'statement_start_date',
          'statement_end_date',
          'starting_balance',
          'ending_balance',
          'opening_balance',
          'closing_balance',
          'current_balance',
          'table_item',
          'table_item/transaction_date',
          'table_item/transaction_deposit',
          'table_item/transaction_withdrawal',
          'table_item/transaction_amount',
          'transaction_date',
          'transaction_deposit',
          'transaction_withdrawal',
          'transaction_amount',
        ].includes(fieldName)
      );
    }

    return false;
  }

  get missingRequiredFieldNames(): string {
    return this.missingRequiredFields
      .map((field) => field.fieldName)
      .join(', ');
  }

  get lowConfidenceUnreviewedFieldNames(): string {
    return this.lowConfidenceUnreviewedFields
      .map((field) => field.fieldName)
      .join(', ');
  }

  hasPendingFieldIssues(): boolean {
    return this.missingRequiredFields.length > 0
      || this.lowConfidenceUnreviewedFields.length > 0;
  }

  getFieldReviewLabel(field: ExtractionField): string {
    if (this.isPlaceholderField(field)) {
      return 'Missing required';
    }

    if (this.isLowConfidenceField(field) && !field.corrected) {
      return 'Review needed';
    }

    if (field.corrected) {
      return 'Reviewed';
    }

    return 'OK';
  }

  getFieldReviewClass(field: ExtractionField): string {
    if (this.isPlaceholderField(field)) {
      return 'review-badge--missing';
    }

    if (this.isLowConfidenceField(field) && !field.corrected) {
      return 'review-badge--warning';
    }

    if (field.corrected) {
      return 'review-badge--reviewed';
    }

    return 'review-badge--ok';
  }

  displayFieldValue(field: ExtractionField): string {
    if (this.isPlaceholderField(field)) {
      return 'Missing value';
    }

    return field.value?.trim() || '—';
  }

  hasAiClassificationMetadata(): boolean {
    if (!this.document) {
      return false;
    }

    return (
      this.document.documentStatus === 'NEEDS_CLASSIFICATION_REVIEW' ||
      !!this.document.detectedDocumentType ||
      this.document.classificationConfidence !== null &&
        this.document.classificationConfidence !== undefined
    );
  }

  private resolveDownloadFileName(doc: DocflowDocument): string {
    if (doc.name.includes('.')) return doc.name;
    const ext = this.resolveExtension(doc);
    return ext ? `${doc.name}.${ext}` : doc.name;
  }

  private resolveExtension(doc: DocflowDocument): string {
    if (doc.storagePath?.includes('.')) return doc.storagePath.split('.').pop() ?? '';
    if (doc.fileType === 'application/pdf') return 'pdf';
    if (doc.fileType === 'image/png') return 'png';
    if (doc.fileType === 'image/jpeg') return 'jpg';
    return '';
  }

  private extractErrorMessage(errorBody: unknown): string | null {
    if (Array.isArray(errorBody)) {
      const firstError = errorBody[0];
      if (firstError?.message) return firstError.message;
      if (firstError?.payload) return firstError.payload;
      if (firstError?.code) return firstError.code;
      return null;
    }
    if (typeof errorBody === 'string') return errorBody;
    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as {message?: string; payload?: string; code?: string};
      return body.message ?? body.payload ?? body.code ?? null;
    }
    return null;
  }

  private extractErrorCode(errorBody: unknown): string | null {
    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as {code?: string};
      if (typeof body.code === 'string') return body.code;
    }
    return null;
  }

  private extractExtractionErrorMessage(errorBody: unknown, fallback: string): string {
    const code = this.extractErrorCode(errorBody);
    if (code === 'DOCUMENT_CLASSIFICATION_REVIEW_REQUIRED') {
      return 'AI classification needs manual review. Confirm the correct document type before running extraction again.';
    }

    if (code === 'EXTRACTION_FAILED') {
      return 'Document extraction could not be completed. Please check OCR/AI setup or try again later.';
    }
    return this.extractErrorMessage(errorBody) ?? fallback;
  }

  private resetDocumentState(): void {
    this.document = null;
    this.fileUrl = null;
    this.isPdf = false;
    this.isImage = false;
    this.extractionFields = [];
    this.extractionId = null;
    this.extractionError = null;
    this.extractionLoading = false;
    this.extractionRunning = false;
    this.editState = null;
    this.confirmingExtraction = false;
  }

  private shouldLoadExtractionForStatus(status: string | null | undefined): boolean {
    return status === 'EXTRACTED' || status === 'READY_FOR_APPROVAL';
  }

  private hasValidationErrors(errorBody: unknown): boolean {
    return this.extractValidationErrors(errorBody).length > 0;
  }

  private extractValidationErrors(errorBody: unknown): BackendValidationError[] {
    if (Array.isArray(errorBody)) {
      return errorBody
        .filter((item) => item && typeof item === 'object')
        .map((item) => item as BackendValidationError);
    }

    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as BackendValidationError;

      if (body.code || body.message || body.payload) {
        return [body];
      }
    }

    return [];
  }

  private buildExtractionValidationMessage(errorBody: unknown, fallback: string): string {
    const errors = this.extractValidationErrors(errorBody);

    if (errors.length === 0) {
      return this.extractExtractionErrorMessage(errorBody, fallback);
    }

    const codes = new Set(errors.map((error) => error.code).filter(Boolean));
    const parts: string[] = [];

    if (codes.has('EXTRACTION_REQUIRED_FIELD_MISSING')) {
      const fields = this.extractFieldNamesFromErrors(errors, 'EXTRACTION_REQUIRED_FIELD_MISSING');

      parts.push(
        fields.length > 0
          ? `Missing: ${fields.join(', ')}.`
          : 'Some required fields are missing.'
      );
    }

    if (codes.has('EXTRACTION_FIELD_EMPTY')) {
      const fields = this.extractFieldNamesFromErrors(errors, 'EXTRACTION_FIELD_EMPTY');

      parts.push(
        fields.length > 0
          ? `Empty: ${fields.join(', ')}.`
          : 'Some required fields are empty.'
      );
    }

    if (codes.has('EXTRACTION_FIELD_LOW_CONFIDENCE')) {
      const count = errors.filter(
        (error) => error.code === 'EXTRACTION_FIELD_LOW_CONFIDENCE'
      ).length;

      parts.push(
        count > 1
          ? `${count} low-confidence fields need review.`
          : 'One low-confidence field needs review.'
      );
    }

    if (
      codes.has('EXTRACTION_FIELD_DATE_FORMAT_INVALID')
      || codes.has('EXTRACTION_FIELD_NUMERIC_FORMAT_INVALID')
      || codes.has('EXTRACTION_FIELD_AMOUNT_INVALID')
    ) {
      parts.push('Fix invalid date or amount formats.');
    }

    if (codes.has('EXTRACTION_FIELD_AMOUNT_INCONSISTENT')) {
      parts.push('Check amount consistency.');
    }

    if (codes.has('EXTRACTION_FIELDS_MISSING')) {
      parts.push('Run extraction again before confirming.');
    }

    if (parts.length === 0) {
      return 'Review highlighted fields before confirming.';
    }

    return parts.join(' ');
  }

  private extractFieldNamesFromErrors(
    errors: BackendValidationError[],
    code: string
  ): string[] {
    const names = errors
      .filter((error) => error.code === code)
      .map((error) => error.message ?? error.payload ?? '')
      .map((message) => {
        const match = message.match(/'([^']+)'/);
        return match?.[1] ?? null;
      })
      .filter((value): value is string => Boolean(value));

    return Array.from(new Set(names));
  }
}
