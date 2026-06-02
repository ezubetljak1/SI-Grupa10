import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import {
  EmptyStateComponent,
  PageHeaderComponent,
  StatusBadgeComponent,
  UiCardComponent,
} from '../../../shared/components';
import { AuthService } from '../../../auth/services/auth.service';
import { ApprovalApiService } from '../../services/approval-api.service';
import { DocflowDocument } from '../../../documents/models/document.models';

@Component({
  selector: 'app-approval-pending-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    EmptyStateComponent,
    PageHeaderComponent,
    StatusBadgeComponent,
    UiCardComponent,
  ],
  templateUrl: './approval-pending-page.html',
  styleUrl: './approval-pending-page.scss',
})
export class ApprovalPendingPageComponent implements OnInit {
  private readonly approvalApi = inject(ApprovalApiService);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  documents: DocflowDocument[] = [];
  loading = false;
  error: string | null = null;

  ngOnInit(): void {
    if (!this.authService.hasRole(['ADMIN', 'MANAGER'])) {
      void this.router.navigate(['/documents']);
      return;
    }

    this.loadPending();
  }

  loadPending(): void {
    this.loading = true;
    this.error = null;

    this.approvalApi.getCompletedForReview().subscribe({
      next: (response) => {
        this.loading = false;
        this.documents = response.payload ?? [];
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.error = this.extractErrorMessage(err.error) ?? 'Failed to load completed documents.';
      },
    });
  }

  formatDate(value: string | null | undefined): string {
    if (!value) return '-';

    return new Date(value).toLocaleString('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  formatFileSize(bytes: number | null | undefined): string {
    if (!bytes) return '-';
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  formatDocumentType(type: string | null | undefined): string {
    if (!type) return '-';

    return type
      .toLowerCase()
      .split('_')
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }

  private extractErrorMessage(errorBody: unknown): string | null {
    if (!errorBody) return null;
    if (typeof errorBody === 'string') return errorBody;
    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as { message?: string; payload?: string; code?: string };
      return body.message ?? body.payload ?? body.code ?? null;
    }
    return null;
  }
}
