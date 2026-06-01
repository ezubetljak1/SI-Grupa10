import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { EmptyStateComponent, PageHeaderComponent } from '../../../shared/components';
import { NotificationApiService } from '../../services/notification-api.service';
import {
  Notification,
  NOTIFICATION_TYPE_ICONS,
  NOTIFICATION_TYPE_LABELS,
  NotificationType,
} from '../../models/notification.models';
import { formatApiDateTime } from '../../../shared/utils/datetime.utils';

@Component({
  selector: 'app-notification-center-page',
  standalone: true,
  imports: [CommonModule, PageHeaderComponent, EmptyStateComponent],
  templateUrl: './notification-center-page.html',
  styleUrl: './notification-center-page.scss',
})
export class NotificationCenterPageComponent implements OnInit {
  private readonly notificationApi = inject(NotificationApiService);
  private readonly toastr = inject(ToastrService);
  private readonly router = inject(Router);

  readonly notifications = signal<Notification[]>([]);
  readonly loading = signal<boolean>(false);
  readonly error = signal<string | null>(null);
  readonly filterTab = signal<'SVE' | 'NEPROCITANE'>('SVE');

  readonly unreadCount = computed(() =>
    this.notifications().filter((n) => !n.read).length
  );

  readonly filteredNotifications = computed(() => {
    const list = this.notifications();
    if (this.filterTab() === 'NEPROCITANE') {
      return list.filter((n) => !n.read);
    }
    return list;
  });

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.loading.set(true);
    this.error.set(null);

    this.notificationApi.getMyNotifications().subscribe({
      next: (data) => {
        this.notifications.set(data || []);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.error.set(err.error?.message || 'Neuspjelo učitavanje obavijesti.');
      },
    });
  }

  markAsRead(id: number, event?: Event): void {
    if (event) {
      event.stopPropagation();
    }

    this.notificationApi.markOneRead(id).subscribe({
      next: (updatedNotif) => {
        this.notifications.update((list) =>
          list.map((n) => (n.id === id ? { ...n, read: true, readAt: updatedNotif.readAt } : n))
        );
      },
      error: (err: HttpErrorResponse) => {
        this.toastr.error(
          err.error?.message || 'Greška pri označavanju obavijesti pročitanom.',
          'Greška'
        );
      },
    });
  }

  markAllAsRead(): void {
    this.notificationApi.markAllRead().subscribe({
      next: () => {
        const nowStr = new Date().toISOString();
        this.notifications.update((list) =>
          list.map((n) => ({ ...n, read: true, readAt: nowStr }))
        );
        this.toastr.success('Sve obavijesti su označene kao pročitane.', 'Uspjeh');
      },
      error: (err: HttpErrorResponse) => {
        this.toastr.error(
          err.error?.message || 'Greška pri označavanju svih obavijesti pročitanim.',
          'Greška'
        );
      },
    });
  }

  handleNotificationClick(notification: Notification): void {
    if (!notification.read) {
      this.markAsRead(notification.id);
    }

    if (notification.actionUrl) {
      void this.router.navigateByUrl(notification.actionUrl);
    }
  }

  getLabel(type: NotificationType): string {
    return NOTIFICATION_TYPE_LABELS[type] || 'Obavijest';
  }

  getIcon(type: NotificationType): string {
    return NOTIFICATION_TYPE_ICONS[type] || '🔔';
  }

  formatDate(dateStr: string): string {
    return formatApiDateTime(dateStr);
  }
}
