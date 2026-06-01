import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { take } from 'rxjs/operators';

import { RoleName } from '../../../auth/models/auth.models';
import { AuthService } from '../../../auth/services/auth.service';
import { NotificationApiService } from '../../../notifications/services/notification-api.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [AsyncPipe, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app-shell.html',
  styleUrl: './app-shell.scss'
})
export class AppShellComponent implements OnInit, OnDestroy {
  private readonly authService = inject(AuthService);
  private readonly notificationApi = inject(NotificationApiService);

  readonly initialized$ = this.authService.initialized$;
  readonly profile$ = this.authService.profile$;
  readonly authenticated$ = this.authService.authenticated$;

  readonly unreadCount = signal<number>(0);
  private pollInterval?: number;

  ngOnInit(): void {
    this.fetchUnreadCount();
    this.pollInterval = window.setInterval(() => this.fetchUnreadCount(), 60000);
  }

  ngOnDestroy(): void {
    if (this.pollInterval) {
      window.clearInterval(this.pollInterval);
    }
  }

  private fetchUnreadCount(): void {
    this.authService.authenticated$.pipe(take(1)).subscribe((authenticated) => {
      if (authenticated) {
        this.notificationApi.getUnreadCount().pipe(take(1)).subscribe({
          next: (count) => this.unreadCount.set(count),
          error: () => {},
        });
      }
    });
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
