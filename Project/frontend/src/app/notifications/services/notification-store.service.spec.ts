import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { Notification } from '../models/notification.models';
import { NotificationApiService } from './notification-api.service';
import { NotificationStoreService } from './notification-store.service';

describe('NotificationStoreService', () => {
  let store: NotificationStoreService;

  const unreadNotification: Notification = {
    id: 1,
    userId: 10,
    documentId: 100,
    commentId: null,
    type: 'DOCUMENT_ASSIGNED',
    title: 'New task assigned',
    text: 'You have been assigned a task.',
    actionUrl: '/documents/100',
    read: false,
    createdAt: '2026-06-06T10:00:00',
    readAt: null,
    emailSentAt: null,
  };

  const readNotification: Notification = {
    id: 2,
    userId: 10,
    documentId: 101,
    commentId: null,
    type: 'DOCUMENT_APPROVED',
    title: 'Document approved',
    text: 'The document has been approved.',
    actionUrl: '/documents/101',
    read: true,
    createdAt: '2026-06-05T10:00:00',
    readAt: '2026-06-05T11:00:00',
    emailSentAt: null,
  };

  const apiMock = {
    getMyNotifications: vi.fn(),
    getUnreadCount: vi.fn(),
    markOneRead: vi.fn(),
    markAllRead: vi.fn(),
  };

  const toastrMock = {
    success: vi.fn(),
    error: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();

    apiMock.getMyNotifications.mockReturnValue(
      of([unreadNotification, readNotification])
    );

    apiMock.getUnreadCount.mockReturnValue(of(3));

    apiMock.markOneRead.mockReturnValue(
      of({
        ...unreadNotification,
        read: true,
        readAt: '2026-06-06T10:30:00',
      })
    );

    apiMock.markAllRead.mockReturnValue(of(undefined));

    TestBed.configureTestingModule({
      providers: [
        NotificationStoreService,
        {
          provide: NotificationApiService,
          useValue: apiMock,
        },
        {
          provide: ToastrService,
          useValue: toastrMock,
        },
      ],
    });

    store = TestBed.inject(NotificationStoreService);
  });

  it('loads notifications and calculates unread count', () => {
    store.loadNotifications();

    expect(store.notifications()).toHaveLength(2);
    expect(store.unreadCount()).toBe(1);
    expect(store.loading()).toBe(false);
  });

  it('refreshes unread count from API', () => {
    store.refreshUnreadCount();

    expect(store.unreadCount()).toBe(3);
  });

  it('marks one notification as read and decrements unread count', () => {
    store.loadNotifications();
    store.markAsRead(1);

    expect(store.notifications().find((item) => item.id === 1)?.read).toBe(true);
    expect(store.unreadCount()).toBe(0);
  });

  it('marks all notifications as read', () => {
    store.loadNotifications();
    store.markAllAsRead();

    expect(store.notifications().every((item) => item.read)).toBe(true);
    expect(store.unreadCount()).toBe(0);
    expect(toastrMock.success).toHaveBeenCalled();
  });
});