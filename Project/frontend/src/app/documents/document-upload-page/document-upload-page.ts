import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, inject, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { ValidationError } from '../../models/api.models';
import { DocumentApiService } from '../../services/document-api.service';
import { FileTypeIconComponent, PageHeaderComponent, UiCardComponent } from '../../shared/components';
import { DocflowDocument } from '../models/document.models';

@Component({
  selector: 'app-document-upload-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    PageHeaderComponent,
    UiCardComponent,
    FileTypeIconComponent,
  ],
  templateUrl: './document-upload-page.html',
  styleUrl: './document-upload-page.scss',
})
export class DocumentUploadPageComponent {
  private readonly documentApiService = inject(DocumentApiService);
  private readonly toastr = inject(ToastrService);

  @ViewChild('fileInput') private fileInput?: ElementRef<HTMLInputElement>;

  readonly maxFileSizeBytes = 10 * 1024 * 1024;
  readonly acceptedFileTypes = '.pdf,.jpg,.jpeg,.png,application/pdf,image/jpeg,image/png';
  readonly allowedExtensions = ['pdf', 'jpg', 'jpeg', 'png'];
  readonly documentTypes = ['INVOICE', 'OTHER'];

  selectedFile: File | null = null;
  uploadedDocument: DocflowDocument | null = null;
  message: {
    type: 'success' | 'warning' | 'error' | null;
    text: string;
  } = {
    type: null,
    text: '',
  };

  companyId = 1;
  createdByUserId = 1;
  documentType = 'INVOICE';
  name = '';
  loading = false;

  onFileSelected(event: Event): void {
    this.message = { type: null, text: '' };
    const input = event.target as HTMLInputElement;
    const nextFile = input.files?.[0] ?? null;

    if (!nextFile) {
      this.selectedFile = null;
      return;
    }

    const validationError = this.validateFile(nextFile);

    if (validationError) {
      this.selectedFile = null;
      input.value = '';

      this.message = {
        type: 'error',
        text: validationError,
      };

      return;
    }

    this.selectedFile = nextFile;
  }

  uploadDocument(): void {
    this.message = { type: null, text: '' };

    if (!this.selectedFile) {
      this.message = {
        type: 'warning',
        text: 'Please select a file before uploading.',
      };
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
          this.uploadedDocument = response.payload;

          this.toastr.success('Document uploaded successfully.', 'Success');

          this.resetForm(false);
        },
        error: (error: HttpErrorResponse) => {
          this.loading = false;
          this.handleError(error);
        },
      });
  }

  clearSelectedFile(): void {
    this.selectedFile = null;

    if (this.fileInput) {
      this.fileInput.nativeElement.value = '';
    }
  }

  resetForm(clearUploadedDocument = true): void {
    this.clearSelectedFile();
    this.companyId = 1;
    this.createdByUserId = 1;
    this.documentType = 'INVOICE';
    this.name = '';
    this.message = { type: null, text: '' };

    if (clearUploadedDocument) {
      this.uploadedDocument = null;
    }
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

  private validateFile(file: File): string | null {
    if (file.size > this.maxFileSizeBytes) {
      return `File size exceeds the allowed limit of ${this.formatFileSize(this.maxFileSizeBytes)}.`;
    }

    const extension = file.name.split('.').pop()?.toLowerCase() ?? '';
    const hasAllowedExtension = this.allowedExtensions.includes(extension);
    const hasAllowedMimeType = ['application/pdf', 'image/jpeg', 'image/png'].includes(file.type);

    if (!hasAllowedExtension && !hasAllowedMimeType) {
      return 'Only PDF, JPG, JPEG and PNG files are supported.';
    }

    return null;
  }

  private handleError(error: HttpErrorResponse): void {
    const errors = this.extractErrorMessages(error.error);

    this.message = {
      type: 'error',
      text: errors[0] ?? 'Upload failed.',
    };
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
}
