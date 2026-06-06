import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  provideRouter,
} from '@angular/router';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { AuthService } from './auth.service';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  const authServiceMock = {
    init: vi.fn(),
    isAuthenticated: vi.fn(),
    login: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();

    authServiceMock.init.mockResolvedValue(false);
    authServiceMock.isAuthenticated.mockReturnValue(false);
    authServiceMock.login.mockResolvedValue(undefined);

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
      ],
    });
  });

  function runGuard(url: string): Promise<boolean> {
    return TestBed.runInInjectionContext(
      () =>
        authGuard(
          {} as ActivatedRouteSnapshot,
          { url } as RouterStateSnapshot
        ) as Promise<boolean>
    );
  }

  it('allows authenticated users to open protected routes', async () => {
    authServiceMock.isAuthenticated.mockReturnValue(true);

    await expect(runGuard('/documents')).resolves.toBe(true);
    expect(authServiceMock.login).not.toHaveBeenCalled();
  });

  it('starts login flow for unauthenticated protected routes', async () => {
    await expect(runGuard('/documents')).resolves.toBe(false);

    expect(authServiceMock.login).toHaveBeenCalledWith('/documents');
  });

  it('allows public company registration without login', async () => {
    await expect(runGuard('/register-company')).resolves.toBe(true);

    expect(authServiceMock.login).not.toHaveBeenCalled();
  });
});