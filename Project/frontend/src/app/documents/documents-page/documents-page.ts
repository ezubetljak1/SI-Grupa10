import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ValidationError } from '../../models/api.models';
import { DocumentApiService } from '../../services/document-api.service';
import {
  EmptyStateComponent,
  FileTypeIconComponent,
  PageHeaderComponent,
  StatusBadgeComponent,
  UiCardComponent
} from '../../shared/components';
import { DocflowDocument, DOCUMENT_TYPE_OPTIONS } from '../models/document.models';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-documents-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    EmptyStateComponent,
    FileTypeIconComponent,
    PageHeaderComponent,
    StatusBadgeComponent,
    UiCardComponent,
  ],
  templateUrl: './documents-page.html',
  styleUrl: './documents-page.scss',
})
export class DocumentsPageComponent implements OnInit {
  private readonly documentApiService = inject(DocumentApiService);
  private readonly toastr = inject(ToastrService);

  documents: DocflowDocument[] = [];
  errors: string[] = [];
  listLoading = false;

  confirmingDeleteId: number | null = null;

  readonly documentTypeOptions = DOCUMENT_TYPE_OPTIONS;

  formatDocumentTypeLabel(documentType: string | null | undefined): string {
    if (!documentType) {
      return '—';
    }

    return (
      this.documentTypeOptions.find((type) => type.value === documentType)?.label ??
      documentType.replaceAll('_', ' ')
    );
  }

  needsClassificationReview(document: DocflowDocument): boolean {
    return document.documentStatus === 'NEEDS_CLASSIFICATION_REVIEW';
  }

  ngOnInit(): void {
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.listLoading = true;

    this.documentApiService
      .getAll({
        page: 0,
        size: 20,
        sortBy: 'id',
        sortDirection: 'desc',
      })
      .subscribe({
        next: (response) => {
          this.listLoading = false;
          this.documents = response.payload ?? [];
        },
        error: (error: HttpErrorResponse) => {
          this.listLoading = false;
          this.handleError(error);
        },
      });
  }

  loadDocumentById(id: number): void {
    this.clearMessages();

    this.documentApiService.getById(id).subscribe({
      next: (response) => {
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
      },
    });
  }

  downloadDocument(document: DocflowDocument): void {
    this.clearMessages();

    this.documentApiService.downloadFile(document.id).subscribe({
      next: (blob) => {
        this.downloadBlob(blob, this.resolveDownloadFileName(document));
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
      },
    });
  }

  deleteDocumentConfirmed(document: DocflowDocument): void {
    this.confirmingDeleteId = null;

    this.clearMessages();

    this.documentApiService.delete(document.id).subscribe({
      next: (response) => {
        this.toastr.success("Document deleted successfully!", "Success");
        this.loadDocuments();
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
      },
    });
  }

  formatFileSize(sizeInBytes: number): string {
    if (sizeInBytes < 1024) {
      return `${sizeInBytes} B`;
    }

    const sizeInKb = sizeInBytes / 1024;

    if (sizeInKb < 1024) {
      return `${sizeInKb.toFixed(2)} KB`;
    }

    const sizeInMb = sizeInKb / 1024;

    return `${sizeInMb.toFixed(2)} MB`;
  }

  private clearMessages(): void {
    this.errors = [];
  }

  private handleError(error: HttpErrorResponse): void {
    this.errors = this.extractErrorMessages(error.error);
  }

  private extractErrorMessages(errorBody: unknown): string[] {
    if (Array.isArray(errorBody)) {
      return errorBody.map((error: ValidationError) => {
        if (error.message && error.code) {
          return `${error.code}: ${error.message}`;
        }

        return error.message || error.code || JSON.stringify(error);
      });
    }

    if (typeof errorBody === 'string') {
      return [errorBody];
    }

    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as { message?: string; payload?: string; code?: string };

      if (body.message) {
        return [body.message];
      }

      if (body.payload) {
        return [body.payload];
      }

      if (body.code) {
        return [body.code];
      }
    }

    return ['Request failed. Check the backend logs or browser Network tab.'];
  }

  private downloadBlob(blob: Blob, fileName: string): void {
    const objectUrl = window.URL.createObjectURL(blob);
    const anchor = document.createElement('a');

    anchor.href = objectUrl;
    anchor.download = fileName;
    anchor.click();

    window.URL.revokeObjectURL(objectUrl);
  }

  private resolveDownloadFileName(document: DocflowDocument): string {
    if (document.name.includes('.')) {
      return document.name;
    }

    const extension = this.resolveExtension(document);

    return extension ? `${document.name}.${extension}` : document.name;
  }

  private resolveExtension(document: DocflowDocument): string {
    if (document.storagePath?.includes('.')) {
      return document.storagePath.split('.').pop() ?? '';
    }

    if (document.fileType === 'application/pdf') {
      return 'pdf';
    }

    if (document.fileType === 'image/png') {
      return 'png';
    }

    if (document.fileType === 'image/jpeg') {
      return 'jpg';
    }

    return '';
  }
}
