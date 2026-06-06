import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';

import { ToastrService } from 'ngx-toastr';

import { AccountStatus, RoleName } from '../../../auth/models/auth.models';
import { AuthService } from '../../../auth/services/auth.service';
import { PageHeaderComponent, UiCardComponent } from '../../../shared/components';
import { formatApiDateTime } from '../../../shared/utils/datetime.utils';
import { UserResponse } from '../../models/user.models';
import { UserApiService } from '../../services/user-api.service';
import { Router } from '@angular/router';
import { Notification } from '../../../notifications/models/notification.models';
import { NotificationStoreService } from '../../../notifications/services/notification-store.service';
import { computed, signal } from '@angular/core';

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

  private readonly router = inject(Router);
  private readonly notificationStore = inject(NotificationStoreService);

  readonly notifications = this.notificationStore.notifications;
  readonly notificationLoading = this.notificationStore.loading;
  readonly unreadCount = this.notificationStore.unreadCount;

  readonly readNotificationsExpanded = signal(false);

  readonly unreadNotifications = computed(() =>
    this.notifications().filter(notification => !notification.read)
  );

  readonly readNotifications = computed(() =>
    this.notifications().filter(notification => notification.read)
  );

  toggleReadNotifications(): void {
    this.readNotificationsExpanded.update(expanded => !expanded);
  }

  ngOnInit(): void {
    this.loadProfile();
    this.notificationStore.loadNotifications();
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
    return formatApiDateTime(value);
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

  markNotificationAsRead(
    notificationId: number,
    event: MouseEvent
  ): void {
    event.stopPropagation();
    this.notificationStore.markAsRead(notificationId);
  }

  markAllNotificationsAsRead(): void {
    this.notificationStore.markAllAsRead();
  }

  openNotification(notification: Notification): void {
    this.notificationStore.markAsRead(notification.id);

    if (notification.actionUrl) {
      void this.router.navigateByUrl(notification.actionUrl);
    }
  }
}
