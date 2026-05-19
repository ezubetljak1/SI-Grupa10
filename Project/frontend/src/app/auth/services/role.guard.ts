import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';

import { RoleName } from '../models/auth.models';
import { AuthService } from './auth.service';

export const roleGuard: CanActivateFn = async (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const roles = (route.data['roles'] ?? []) as RoleName[];

  await authService.init();

  if (!authService.isAuthenticated()) {
    await authService.login(router.url);
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
