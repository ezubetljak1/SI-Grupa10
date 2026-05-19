import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { RoleName } from '../../../auth/models/auth.models';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [AsyncPipe, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app-shell.html',
  styleUrl: './app-shell.scss'
})
export class AppShellComponent {
  private readonly authService = inject(AuthService);

  readonly profile$ = this.authService.profile$;
  readonly authenticated$ = this.authService.authenticated$;

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
