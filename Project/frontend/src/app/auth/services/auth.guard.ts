import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = async (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  await authService.init();

  if (authService.isAuthenticated()) {
    return true;
  }

  // If requested route is public registration, do not trigger Keycloak login here.
  const requested = state?.url ?? '';
  if (requested.startsWith('/register-company')) {
    // allow navigation to registration page
    return true;
  }

  // For protected pages initiate Keycloak login flow
  await authService.login(requested);
  return false;
};
