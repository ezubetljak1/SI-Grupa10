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

  await authService.login(state.url);
  return router.parseUrl('/register-company');
};
