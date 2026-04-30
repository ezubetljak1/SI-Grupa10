import { Component, Input } from '@angular/core';

type BadgeVariant = 'success' | 'warning' | 'error' | 'info' | 'processing' | 'neutral';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  templateUrl: './status-badge.html',
  styleUrl: './status-badge.scss'
})
export class StatusBadgeComponent {
  @Input() status = '';

  get normalizedStatus(): string {
    return this.status?.trim()?.toUpperCase() || 'UNKNOWN';
  }

  get label(): string {
    const labels: Record<string, string> = {
      UPLOADED: 'Uploaded',
      STORED: 'Stored',
      VALIDATED: 'Validated',
      PROCESSING: 'Processing',
      PENDING: 'Pending',
      FAILED: 'Failed',
      ERROR: 'Error',
      REJECTED: 'Rejected',
      UNKNOWN: 'Unknown'
    };

    return labels[this.normalizedStatus] ?? this.formatFallbackLabel(this.normalizedStatus);
  }

  get variant(): BadgeVariant {
    const variants: Record<string, BadgeVariant> = {
      UPLOADED: 'success',
      STORED: 'success',
      VALIDATED: 'success',
      PROCESSING: 'processing',
      PENDING: 'warning',
      FAILED: 'error',
      ERROR: 'error',
      REJECTED: 'error',
      UNKNOWN: 'neutral'
    };

    return variants[this.normalizedStatus] ?? 'neutral';
  }

  private formatFallbackLabel(value: string): string {
    return value
      .toLowerCase()
      .split('_')
      .filter(Boolean)
      .map(part => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }
}