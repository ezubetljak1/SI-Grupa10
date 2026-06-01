import { Component, HostListener, OnInit, OnDestroy, computed, inject, signal } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet, Router } from '@angular/router';
import { take, filter } from 'rxjs/operators';

import { RoleName } from '../../../auth/models/auth.models';
import { AuthService } from '../../../auth/services/auth.service';
import { Notification } from '../../../notifications/models/notification.models';
import { NotificationStoreService } from '../../../notifications/services/notification-store.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [AsyncPipe, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app-shell.html',
  styleUrl: './app-shell.scss'
})
export class AppShellComponent implements OnInit, OnDestroy {
  private readonly authService = inject(AuthService);

  readonly initialized$ = this.authService.initialized$;
  readonly profile$ = this.authService.profile$;
  readonly authenticated$ = this.authService.authenticated$;

  private pollInterval?: number;

  private readonly router = inject(Router);
  private readonly notificationStore = inject(NotificationStoreService);

  readonly unreadCount = this.notificationStore.unreadCount;
  readonly notifications = this.notificationStore.notifications;
  readonly notificationDropdownOpen = signal(false);

  readonly recentNotifications = computed(() =>
    this.notifications().slice(0, 5)
  );

  ngOnInit(): void {
  this.authenticated$
    .pipe(
      filter((authenticated) => authenticated),
      take(1)
    )
    .subscribe(() => {
      this.notificationStore.refreshUnreadCount();
    });

  this.pollInterval = window.setInterval(
    () => this.refreshUnreadCountIfAuthenticated(),
    10000
  );
}

private refreshUnreadCountIfAuthenticated(): void {
  this.authenticated$
    .pipe(take(1))
    .subscribe((authenticated) => {
      if (authenticated) {
        this.notificationStore.refreshUnreadCount();
      }
    });
}

  ngOnDestroy(): void {
    if (this.pollInterval) {
      window.clearInterval(this.pollInterval);
    }
  }

  toggleNotificationDropdown(event: MouseEvent): void {
    event.stopPropagation();

    const nextValue = !this.notificationDropdownOpen();

    this.notificationDropdownOpen.set(nextValue);

    if (nextValue) {
      this.notificationStore.loadNotifications();
    }
  }

  closeNotificationDropdown(): void {
    this.notificationDropdownOpen.set(false);
  }

  markAllNotificationsAsRead(event: MouseEvent): void {
    event.stopPropagation();
    this.notificationStore.markAllAsRead();
  }

  openNotification(
    notification: Notification,
    event: MouseEvent
  ): void {
    event.stopPropagation();

    this.notificationStore.markAsRead(notification.id);
    this.closeNotificationDropdown();

    if (notification.actionUrl) {
      void this.router.navigateByUrl(notification.actionUrl);
    }
  }

  @HostListener('document:click')
  onDocumentClick(): void {
    this.closeNotificationDropdown();
  }

  hasRole(roles: RoleName[]): boolean {
    return this.authService.hasRole(roles);
  }

  login(): void {
    void this.authService.login('/documents');
  }

  logout(): void {
    void this.authService.logout();
  }

  initials(firstName?: string, lastName?: string, email?: string): string {
    const firstInitial = firstName?.trim().charAt(0) ?? '';
    const lastInitial = lastName?.trim().charAt(0) ?? '';
    const initials = `${firstInitial}${lastInitial}`.trim();

    if (initials) {
      return initials.toUpperCase();
    }

    return email?.slice(0, 2).toUpperCase() ?? 'DF';
  }
}
