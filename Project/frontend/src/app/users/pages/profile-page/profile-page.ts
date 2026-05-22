import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';

import { ToastrService } from 'ngx-toastr';

import { AccountStatus, RoleName } from '../../../auth/models/auth.models';
import { AuthService } from '../../../auth/services/auth.service';
import { PageHeaderComponent, UiCardComponent } from '../../../shared/components';
import { UserResponse } from '../../models/user.models';
import { UserApiService } from '../../services/user-api.service';

type NotificationPreview = {
  title: string;
  audience: string;
  status: 'Planned' | 'Upcoming';
  description: string;
  signal: string;
};

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, PageHeaderComponent, UiCardComponent],
  templateUrl: './profile-page.html',
  styleUrl: './profile-page.scss',
})
export class ProfilePageComponent implements OnInit {
  private readonly userApi = inject(UserApiService);
  private readonly authService = inject(AuthService);
  private readonly toastr = inject(ToastrService);

  profile: UserResponse | null = null;
  loading = false;
  passwordActionInProgress = false;

  readonly notificationPreview: NotificationPreview[] = [
    {
      title: 'Document waiting for approval',
      audience: 'Approvers',
      status: 'Planned',
      description:
        'Will notify approvers when a document enters the READY_FOR_APPROVAL stage.',
      signal: 'Approval queue',
    },
    {
      title: 'Rejected or returned document',
      audience: 'Operators',
      status: 'Planned',
      description:
        'Will notify the responsible operator when a document is rejected or returned for correction.',
      signal: 'Correction needed',
    },
    {
      title: 'Status history activity',
      audience: 'Managers and approvers',
      status: 'Upcoming',
      description:
        'Will highlight important approval workflow events once status history is fully connected.',
      signal: 'Workflow timeline',
    },
  ];

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading = true;

    this.userApi.getCurrentUser().subscribe({
      next: (response) => {
        this.loading = false;
        this.profile = response.payload;
      },
      error: (error: HttpErrorResponse) => {
        this.loading = false;
        this.toastr.error(this.extractErrorMessage(error.error), 'Profile unavailable');
      },
    });
  }

  changePassword(): void {
    this.passwordActionInProgress = true;

    this.authService.changePassword('/profile').catch(() => {
      this.passwordActionInProgress = false;
      this.toastr.error('Password change could not be started.', 'Action failed');
    });
  }

  initials(profile: UserResponse): string {
    const first = profile.firstName?.trim().charAt(0) ?? '';
    const last = profile.lastName?.trim().charAt(0) ?? '';
    const initials = `${first}${last}`.trim();

    return initials ? initials.toUpperCase() : profile.email.slice(0, 2).toUpperCase();
  }

  fullName(profile: UserResponse): string {
    return `${profile.firstName} ${profile.lastName}`.trim();
  }

  statusLabel(status: AccountStatus): string {
    return status.replaceAll('_', ' ');
  }

  statusClass(status: AccountStatus): string {
    switch (status) {
      case 'ACTIVE':
        return 'status-pill--active';
      case 'PENDING_PASSWORD_CHANGE':
        return 'status-pill--pending';
      case 'INACTIVE':
        return 'status-pill--inactive';
      default:
        return 'status-pill--neutral';
    }
  }

  roleDescription(role: RoleName): string {
    switch (role) {
      case 'ADMIN':
        return 'Full company administration access, including user and role management.';
      case 'MANAGER':
        return 'Can manage document workflow, review workspace activity and coordinate users.';
      case 'OPERATOR':
        return 'Can upload documents and run document intake/extraction actions.';
      case 'APPROVER':
        return 'Can review extracted data and approve documents.';
      default:
        return 'Company workspace access.';
    }
  }

  formatDate(value?: string): string {
    if (!value) {
      return '—';
    }

    return new Date(value).toLocaleString();
  }

  private extractErrorMessage(errorBody: unknown): string {
    if (typeof errorBody === 'string') {
      return errorBody;
    }

    if (Array.isArray(errorBody) && errorBody.length > 0) {
      const firstError = errorBody[0] as { message?: string; payload?: string; code?: string };

      return firstError.message ?? firstError.payload ?? firstError.code ?? 'Request failed.';
    }

    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as { message?: string; payload?: string; code?: string };

      return body.message ?? body.payload ?? body.code ?? 'Request failed.';
    }

    return 'Request failed.';
  }
}
