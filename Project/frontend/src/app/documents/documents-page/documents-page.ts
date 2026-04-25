import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { DocumentApiService } from '../../services/document-api.service';
import { DocflowDocument } from '../models/document.models';
import { ValidationError } from '../../models/api.models';

@Component({
  selector: 'app-documents-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './documents-page.html',
  styleUrl: './documents-page.scss',
})
export class DocumentsPageComponent implements OnInit {
  private readonly documentApiService = inject(DocumentApiService);

  readonly maxFileSizeBytes = 10 * 1024 * 1024;

  selectedFile: File | null = null;

  companyId = 1;
  createdByUserId = 1;
  documentType = 'INVOICE';
  name = '';

  documents: DocflowDocument[] = [];
  uploadedDocument: DocflowDocument | null = null;

  result: unknown = null;
  errors: string[] = [];

  loading = false;
  listLoading = false;

  ngOnInit(): void {
    this.loadDocuments();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
    this.errors = [];
  }

  uploadDocument(): void {
    this.clearMessages();

    if (!this.selectedFile) {
      this.errors = ['You must select a file before uploading.'];
      return;
    }

    if (this.selectedFile.size > this.maxFileSizeBytes){
      this.errors = [
        `DOCUMENT_FILE_SIZE_EXCEEDED: File size exceeds the allowed limit. Maximum file size is ${this.formatFileSize(this.maxFileSizeBytes)}. Selected file size is ${this.formatFileSize(this.selectedFile.size)}.`,
      ];
      return;
    }

    this.loading = true;

    this.documentApiService
      .upload({
        file: this.selectedFile,
        companyId: this.companyId,
        createdByUserId: this.createdByUserId,
        documentType: this.documentType,
        name: this.name,
      })
      .subscribe({
        next: (response) => {
          this.loading = false;
          this.result = response;
          this.uploadedDocument = response.payload;
          this.loadDocuments();
        },
        error: (error: HttpErrorResponse) => {
          this.loading = false;
          this.handleError(error);
        },
      });
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
          this.result = response;
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
        this.result = response;
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

  deleteDocument(document: DocflowDocument): void {
    const confirmed = window.confirm(`Delete document "${document.name}"?`);

    if (!confirmed) {
      return;
    }

    this.clearMessages();

    this.documentApiService.delete(document.id).subscribe({
      next: (response) => {
        this.result = response;
        this.loadDocuments();
      },
      error: (error: HttpErrorResponse) => {
        this.handleError(error);
      },
    });
  }

  resetForm(): void {
    this.selectedFile = null;
    this.companyId = 1;
    this.createdByUserId = 1;
    this.documentType = 'INVOICE';
    this.name = '';
    this.clearMessages();
  }

  private clearMessages(): void {
    this.errors = [];
    this.result = null;
  }

  private handleError(error: HttpErrorResponse): void {
    this.result = error;
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
}