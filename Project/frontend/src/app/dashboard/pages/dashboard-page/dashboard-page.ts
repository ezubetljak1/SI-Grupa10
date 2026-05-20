import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';

import {
  DashboardResponse,
  DocumentsByResponsibleUser,
} from '../../models/dashboard.models';
import { DashboardApiService } from '../../services/dashboard-api.service';

type StatusTone = 'success' | 'warning' | 'error' | 'info' | 'processing' | 'neutral';

type StatusEntry = {
  key: string;
  label: string;
  value: number;
  percent: number;
  tone: StatusTone;
};

type KpiCard = {
  title: string;
  value: string | number;
  subtitle: string;
  tone: 'primary' | 'success' | 'warning' | 'error' | 'info';
};

type LifecycleSegment = {
  label: string;
  value: number;
  percent: number;
  tone: StatusTone;
};

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-page.html',
  styleUrl: './dashboard-page.scss',
})
export class DashboardPage implements OnInit {
  private readonly dashboardApiService = inject(DashboardApiService);

  dashboard: DashboardResponse | null = null;
  loading = false;

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;

    this.dashboardApiService.getCompanyDashboard().subscribe({
      next: (response) => {
        this.loading = false;
        this.dashboard = response.payload;
      },
      error: () => {
        this.loading = false;
        this.dashboard = null;
      },
    });
  }

  get totalDocuments(): number {
    return this.dashboard?.totalDocuments ?? 0;
  }

  get kpiCards(): KpiCard[] {
    return [
      {
        title: 'Total documents',
        value: this.totalDocuments,
        subtitle: 'All documents registered in this company workspace',
        tone: 'primary',
      },
      {
        title: 'Need action',
        value: this.needsActionCount,
        subtitle: 'Classification review, approval or manual review required',
        tone: this.needsActionCount > 0 ? 'warning' : 'success',
      },
      {
        title: 'Completion rate',
        value: `${this.completionRate}%`,
        subtitle: `${this.completedCount} approved or completed document(s)`,
        tone: this.completionRate >= 70 ? 'success' : 'info',
      },
      {
        title: 'Failed processing',
        value: this.failedCount,
        subtitle: 'OCR or extraction failures that need attention',
        tone: this.failedCount > 0 ? 'error' : 'success',
      },
    ];
  }

  get statusEntries(): StatusEntry[] {
    const statusMap = this.dashboard?.documentsByStatus ?? {};
    const preferredOrder = [
      'UPLOADED',
      'EXTRACTED',
      'NEEDS_CLASSIFICATION_REVIEW',
      'READY_FOR_APPROVAL',
      'UNDER_REVIEW',
      'APPROVED',
      'COMPLETED',
      'REJECTED',
      'PROCESSING_FAILED',
    ];

    return Object.entries(statusMap)
      .sort(([first], [second]) => {
        const firstIndex = preferredOrder.indexOf(first);
        const secondIndex = preferredOrder.indexOf(second);

        if (firstIndex === -1 && secondIndex === -1) {
          return first.localeCompare(second);
        }

        if (firstIndex === -1) {
          return 1;
        }

        if (secondIndex === -1) {
          return -1;
        }

        return firstIndex - secondIndex;
      })
      .map(([key, value]) => ({
        key,
        label: this.formatStatusLabel(key),
        value,
        percent: this.percentOfTotal(value),
        tone: this.statusTone(key),
      }));
  }

  get lifecycleSegments(): LifecycleSegment[] {
    return [
      {
        label: 'Intake',
        value: this.intakeCount,
        percent: this.percentOfTotal(this.intakeCount),
        tone: 'info',
      },
      {
        label: 'Review',
        value: this.needsActionCount,
        percent: this.percentOfTotal(this.needsActionCount),
        tone: 'warning',
      },
      {
        label: 'Completed',
        value: this.completedCount,
        percent: this.percentOfTotal(this.completedCount),
        tone: 'success',
      },
      {
        label: 'Issues',
        value: this.issueCount,
        percent: this.percentOfTotal(this.issueCount),
        tone: 'error',
      },
    ];
  }

  get workloadUsers(): DocumentsByResponsibleUser[] {
    return [...(this.dashboard?.documentsByResponsibleUser ?? [])].sort(
      (first, second) => second.documentCount - first.documentCount
    );
  }

  get topWorkloadUsers(): DocumentsByResponsibleUser[] {
    return this.workloadUsers.slice(0, 6);
  }

  get maxUserDocumentCount(): number {
    return this.workloadUsers[0]?.documentCount ?? 0;
  }

  get busiestUser(): DocumentsByResponsibleUser | null {
    return this.workloadUsers[0] ?? null;
  }

  get assignedDocuments(): number {
    return this.workloadUsers.reduce((sum, user) => sum + user.documentCount, 0);
  }

  get unassignedDocuments(): number {
    return Math.max(this.totalDocuments - this.assignedDocuments, 0);
  }

  get intakeCount(): number {
    return this.statusCount('UPLOADED') + this.statusCount('EXTRACTED');
  }

  get needsActionCount(): number {
    return (
      this.statusCount('NEEDS_CLASSIFICATION_REVIEW') +
      this.statusCount('READY_FOR_APPROVAL') +
      this.statusCount('UNDER_REVIEW')
    );
  }

  get completedCount(): number {
    return this.statusCount('APPROVED') + this.statusCount('COMPLETED');
  }

  get failedCount(): number {
    return this.statusCount('PROCESSING_FAILED');
  }

  get rejectedCount(): number {
    return this.statusCount('REJECTED');
  }

  get issueCount(): number {
    return this.failedCount + this.rejectedCount;
  }

  get completionRate(): number {
    return this.percentOfTotal(this.completedCount);
  }

  get actionRate(): number {
    return this.percentOfTotal(this.needsActionCount);
  }

  get issueRate(): number {
    return this.percentOfTotal(this.issueCount);
  }

  get healthScore(): number {
    if (this.totalDocuments === 0) {
      return 100;
    }

    const penalty = this.issueRate * 1.5 + this.actionRate * 0.7;
    return Math.max(0, Math.round(100 - penalty));
  }

  get healthLabel(): string {
    if (this.totalDocuments === 0) {
      return 'No activity yet';
    }

    if (this.failedCount > 0) {
      return 'Needs attention';
    }

    if (this.needsActionCount > 0) {
      return 'Review queue active';
    }

    return 'Stable';
  }

  get healthDescription(): string {
    if (this.totalDocuments === 0) {
      return 'Upload documents to start tracking workflow health.';
    }

    if (this.failedCount > 0) {
      return 'Resolve failed processing items first because they block document flow.';
    }

    if (this.needsActionCount > 0) {
      return 'The main operational pressure is currently in manual review or approval.';
    }

    return 'No urgent issues detected from current dashboard statistics.';
  }

  get hasDashboardData(): boolean {
    return this.totalDocuments > 0 || this.workloadUsers.length > 0;
  }

  get lifecycleGradient(): string {
    if (this.totalDocuments === 0) {
      return 'conic-gradient(#e5e7eb 0deg 360deg)';
    }

    const intake = this.degrees(this.intakeCount);
    const review = this.degrees(this.needsActionCount);
    const completed = this.degrees(this.completedCount);
    const issues = Math.max(0, 360 - intake - review - completed);

    const first = intake;
    const second = intake + review;
    const third = intake + review + completed;
    const fourth = third + issues;

    return `conic-gradient(
      #2563eb 0deg ${first}deg,
      #d97706 ${first}deg ${second}deg,
      #059669 ${second}deg ${third}deg,
      #dc2626 ${third}deg ${fourth}deg
    )`;
  }

  percentOfTotal(value: number): number {
    if (this.totalDocuments === 0) {
      return 0;
    }

    return Math.min(100, Math.round((value / this.totalDocuments) * 100));
  }

  workloadPercent(value: number): number {
    if (this.maxUserDocumentCount === 0) {
      return 0;
    }

    return Math.min(100, Math.round((value / this.maxUserDocumentCount) * 100));
  }

  formatStatusLabel(status: string): string {
    return status
      .toLowerCase()
      .replaceAll('_', ' ')
      .replace(/\b\w/g, (letter) => letter.toUpperCase());
  }

  private statusCount(status: string): number {
    return this.dashboard?.documentsByStatus?.[status] ?? 0;
  }

  private degrees(value: number): number {
    if (this.totalDocuments === 0) {
      return 0;
    }

    return Math.round((value / this.totalDocuments) * 360);
  }

  private statusTone(status: string): StatusTone {
    switch (status) {
      case 'APPROVED':
      case 'COMPLETED':
        return 'success';
      case 'READY_FOR_APPROVAL':
      case 'NEEDS_CLASSIFICATION_REVIEW':
      case 'UNDER_REVIEW':
        return 'warning';
      case 'PROCESSING_FAILED':
      case 'REJECTED':
        return 'error';
      case 'EXTRACTED':
        return 'processing';
      case 'UPLOADED':
        return 'info';
      default:
        return 'neutral';
    }
  }
}
