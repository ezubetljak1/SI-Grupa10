import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { DocumentApiService } from '../../services/document-api.service';
import {
  FileTypeIconComponent,
  PageHeaderComponent,
  StatusBadgeComponent,
  UiCardComponent,
} from '../../shared/components';
import { DocflowDocument } from '../models/document.models';

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

  document: DocflowDocument | null = null;
  loading = false;
  error: string | null = null;
  downloading = false;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadDocument(id);
    }
  }

  loadDocument(id: number): void {
    this.loading = true;
    this.error = null;

    this.documentApiService.getById(id).subscribe({
      next: (response) => {
        this.loading = false;
        this.document = response.payload;
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.error = err.error?.message ?? err.error?.payload ?? 'Failed to load document.';
      },
    });
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
      error: () => {
        this.downloading = false;
        this.error = 'Failed to download file.';
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
      day: '2-digit', month: 'short', year: 'numeric',
      hour: '2-digit', minute: '2-digit',
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
}