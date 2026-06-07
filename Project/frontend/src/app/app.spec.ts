import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { App } from './app';
import { AuthService } from './auth/services/auth.service';
import { Notification } from './notifications/models/notification.models';
import { NotificationStoreService } from './notifications/services/notification-store.service';

describe('App', () => {
  const authServiceMock = {
    initialized$: of(true),
    profile$: of(null),
    authenticated$: of(false),
    init: vi.fn().mockResolvedValue(false),
    hasRole: vi.fn().mockReturnValue(false),
    login: vi.fn(),
    logout: vi.fn(),
  };

  const notificationStoreMock = {
    unreadCount: signal(0),
    notifications: signal<Notification[]>([]),
    refreshUnreadCount: vi.fn(),
    loadNotifications: vi.fn(),
    markAllAsRead: vi.fn(),
    markAsRead: vi.fn(),
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    notificationStoreMock.unreadCount.set(0);
    notificationStoreMock.notifications.set([]);

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
        {
          provide: NotificationStoreService,
          useValue: notificationStoreMock,
        },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);

    expect(fixture.componentInstance).toBeTruthy();

    fixture.destroy();
  });

  it('should initialize authentication on startup', () => {
    const fixture = TestBed.createComponent(App);

    fixture.detectChanges();

    expect(authServiceMock.init).toHaveBeenCalled();

    fixture.destroy();
  });
});