import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';

import { RoleName } from '../models/auth.models';
import { AuthService } from './auth.service';

export const roleGuard: CanActivateFn = async (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const roles = (route.data['roles'] ?? []) as RoleName[];

  await authService.init();

  // If navigation target is public registration, allow through
  const requested = state?.url ?? router.url;
  if (requested.startsWith('/register-company')) {
    return true;
  }

  if (!authService.isAuthenticated()) {
    await authService.login(requested);
    return router.parseUrl('/register-company');
  }

  if (!authService.profile) {
    await firstValueFrom(authService.fetchCurrentUser());
  }

  if (roles.length === 0 || authService.hasRole(roles)) {
    return true;
  }

  return router.parseUrl('/documents');
};
