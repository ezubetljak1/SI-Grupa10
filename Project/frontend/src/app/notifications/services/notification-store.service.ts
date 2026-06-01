import { Injectable, inject, signal } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Notification } from '../models/notification.models';
import { NotificationApiService } from './notification-api.service';

@Injectable({
  providedIn: 'root',
})
export class NotificationStoreService {
  private readonly notificationApi = inject(NotificationApiService);
  private readonly toastr = inject(ToastrService);

  readonly notifications = signal<Notification[]>([]);
  readonly unreadCount = signal(0);
  readonly loading = signal(false);

  loadNotifications(): void {
    this.loading.set(true);

    this.notificationApi.getMyNotifications().subscribe({
      next: (notifications) => {
        const values = notifications ?? [];

        this.notifications.set(values);
        this.unreadCount.set(
          values.filter((notification) => !notification.read).length
        );

        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  refreshUnreadCount(): void {
    this.notificationApi.getUnreadCount().subscribe({
      next: (count) => this.unreadCount.set(count),
      error: () => {},
    });
  }

  markAsRead(notificationId: number): void {
    const notification =
      this.notifications().find((item) => item.id === notificationId);

    if (!notification || notification.read) {
      return;
    }

    this.notificationApi.markOneRead(notificationId).subscribe({
      next: (updated) => {
        this.notifications.update((items) =>
          items.map((item) =>
            item.id === notificationId
              ? {
                  ...item,
                  read: true,
                  readAt: updated.readAt,
                }
              : item
          )
        );

        this.unreadCount.update((count) => Math.max(0, count - 1));
      },
      error: () => {
        this.toastr.error(
          'The notification could not be marked as read.',
          'Action failed'
        );
      },
    });
  }

  markAllAsRead(): void {
    this.notificationApi.markAllRead().subscribe({
      next: () => {
        const readAt = new Date().toISOString();

        this.notifications.update((items) =>
          items.map((item) => ({
            ...item,
            read: true,
            readAt,
          }))
        );

        this.unreadCount.set(0);

        this.toastr.success(
          'All notifications have been marked as read.',
          'Success'
        );
      },
      error: () => {
        this.toastr.error(
          'Notifications could not be marked as read.',
          'Action failed'
        );
      },
    });
  }
}