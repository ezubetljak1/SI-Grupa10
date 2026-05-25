import { Component, Input } from '@angular/core';

type BadgeVariant =
  | 'success'
  | 'warning'
  | 'error'
  | 'info'
  | 'processing'
  | 'approval'
  | 'neutral';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  templateUrl: './status-badge.html',
  styleUrl: './status-badge.scss',
})
export class StatusBadgeComponent {
  @Input() status = '';

  get normalizedStatus(): string {
    return this.status?.trim()?.toUpperCase() || 'UNKNOWN';
  }

  get label(): string {
    const labels: Record<string, string> = {
      UPLOADED: 'Uploaded',
      PROCESSING_FAILED: 'Processing Failed',
      EXTRACTED: 'Extracted',
      UNDER_REVIEW: 'Under Review',
      NEEDS_CLASSIFICATION_REVIEW: 'Needs Classification Review',
      READY_FOR_APPROVAL: 'Ready for Approval',
      NEEDS_CORRECTION: 'Needs Correction',
      APPROVED: 'Approved',
      REJECTED: 'Rejected',
      COMPLETED: 'Completed',

      STORED: 'Stored',
      VALIDATED: 'Validated',
      PROCESSING: 'Processing',
      PENDING: 'Pending',
      FAILED: 'Failed',
      ERROR: 'Error',
      UNKNOWN: 'Unknown',
    };

    return labels[this.normalizedStatus] ?? this.formatFallbackLabel(this.normalizedStatus);
  }

  get variant(): BadgeVariant {
    const variants: Record<string, BadgeVariant> = {
      UPLOADED: 'success',
      PROCESSING_FAILED: 'error',
      EXTRACTED: 'info',
      UNDER_REVIEW: 'warning',
      NEEDS_CLASSIFICATION_REVIEW: 'warning',
      READY_FOR_APPROVAL: 'approval',
      NEEDS_CORRECTION: 'warning',
      APPROVED: 'success',
      REJECTED: 'error',
      COMPLETED: 'success',

      STORED: 'success',
      VALIDATED: 'success',
      PROCESSING: 'processing',
      PENDING: 'warning',
      FAILED: 'error',
      ERROR: 'error',
      UNKNOWN: 'neutral',
    };

    return variants[this.normalizedStatus] ?? 'neutral';
  }

  private formatFallbackLabel(value: string): string {
    return value
      .toLowerCase()
      .split('_')
      .filter(Boolean)
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }
}