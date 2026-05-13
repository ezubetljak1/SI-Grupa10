import {CommonModule} from '@angular/common';
import {HttpErrorResponse} from '@angular/common/http';
import {Component, inject, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute, RouterLink} from '@angular/router';

import {DocumentApiService} from '../../services/document-api.service';
import {
  FileTypeIconComponent,
  PageHeaderComponent,
  StatusBadgeComponent,
  UiCardComponent,
} from '../../shared/components';
import {DocflowDocument} from '../models/document.models';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {ToastrService} from 'ngx-toastr';
import {Extraction, ExtractionField} from '../models/extraction.models';

interface EditState {
  fieldId: number;
  editValue: string;
  saving: boolean;
  validationError: string | null;
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
        const rawUrl = `/api/documents/${this.document.id}/preview`;
        this.fileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(rawUrl);
        this.isPdf = this.document.fileType === 'application/pdf';
        this.isImage = this.document.fileType?.startsWith('image/');
        this.extractionError = null;
        this.extractionFields = [];
        if (this.document.documentStatus === 'EXTRACTED') {
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
    if (value.trim() === '') {
      return 'Field value cannot be empty.';
    }

    const name = field.fieldName.toLowerCase();

    if (name.includes('date') || name.includes('datum')) {
      const datePatterns = [
        /^\d{4}-\d{2}-\d{2}$/,
        /^\d{2}\.\d{2}\.\d{4}$/,
        /^\d{2}\/\d{2}\/\d{4}$/,
      ];
      const valid = datePatterns.some((p) => p.test(value.trim()));
      if (!valid) {
        return 'Invalid date format. Accepted formats: YYYY-MM-DD, DD.MM.YYYY, MM/DD/YYYY';
      }
    }

    if (
      name.includes('amount') ||
      name.includes('total') ||
      name.includes('price') ||
      name.includes('iznos') ||
      name.includes('cijena')
    ) {
      const numericPattern = /^-?\d+([.,]\d+)?$/;
      if (!numericPattern.test(value.trim())) {
        return 'Field must be a number (e.g. 1234.56 or 1234,56).';
      }
    }

    return null;
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
  }
}