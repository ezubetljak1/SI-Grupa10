import {CommonModule} from '@angular/common';
import {HttpErrorResponse} from '@angular/common/http';
import {Component, inject, OnInit} from '@angular/core';
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
import { ToastrService } from 'ngx-toastr';
import { ExtractionField } from '../models/extraction.models';

@Component({
  selector: 'app-document-detail-page',
  standalone: true,
  imports: [
    CommonModule,
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

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    
    if (!Number.isFinite(id) || id <= 0){
      this.toastr.error('Invalid document id.', 'Error');
      return;
    }

    this.loadDocument(id);
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
          this.loadExtractionFields();
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

    this.extractionRunning = true;
    this.extractionError = null;

    this.documentApiService.processExtraction(this.document.id).subscribe({
      next: (response) => {
        this.extractionRunning = false;
        this.extractionFields = response.payload?.fields ?? [];
        this.toastr.success('Extraction completed.', 'Success');

        this.loadDocument(this.document!.id);
      },
      error: (err: HttpErrorResponse) => {
        this.extractionRunning = false;
        const message = this.extractErrorMessage(err.error) ?? 'Extraction failed.';
        this.extractionError = message;
        this.toastr.error(message, 'Error');
      },
    });
  }

  retryExtraction(): void {
    if (!this.document) return;

    this.extractionRunning = true;
    this.extractionError = null;

    this.documentApiService.retryExtraction(this.document.id).subscribe({
      next: (response) => {
        this.extractionRunning = false;
        this.extractionFields = response.payload?.fields ?? [];
        this.toastr.success('Extraction retried.', 'Success');

        this.loadDocument(this.document!.id);
      },
      error: (err: HttpErrorResponse) => {
        this.extractionRunning = false;
        const message = this.extractErrorMessage(err.error) ?? 'Retry extraction failed.';
        this.extractionError = message;
        this.toastr.error(message, 'Error');
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

      if (firstError?.message) {
        return firstError.message;
      }

      if (firstError?.payload) {
        return firstError.payload;
      }

      if (firstError?.code) {
        return firstError.code;
      }

      return null;
    }

    if (typeof errorBody === 'string') {
      return errorBody;
    }

    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as { message?: string; payload?: string; code?: string };

      return body.message ?? body.payload ?? body.code ?? null;
    }

    return null;
  }
}
