import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  provideRouter,
} from '@angular/router';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { RoleName } from '../models/auth.models';
import { AuthService } from './auth.service';
import { roleGuard } from './role.guard';

describe('roleGuard', () => {
  const authServiceMock = {
    init: vi.fn(),
    isAuthenticated: vi.fn(),
    login: vi.fn(),
    fetchCurrentUser: vi.fn(),
    hasRole: vi.fn(),
    profile: {
      id: 1,
      role: 'ADMIN',
    },
  };

  beforeEach(() => {
    vi.clearAllMocks();

    authServiceMock.init.mockResolvedValue(true);
    authServiceMock.isAuthenticated.mockReturnValue(true);
    authServiceMock.login.mockResolvedValue(undefined);
    authServiceMock.fetchCurrentUser.mockReturnValue(of(authServiceMock.profile));
    authServiceMock.hasRole.mockReturnValue(true);
    authServiceMock.profile = {
      id: 1,
      role: 'ADMIN',
    };

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

  function runGuard(
    roles: RoleName[],
    url = '/dashboard'
  ): Promise<boolean | UrlTree> {
    return TestBed.runInInjectionContext(
      () =>
        roleGuard(
          {
            data: { roles },
          } as unknown as ActivatedRouteSnapshot,
          { url } as RouterStateSnapshot
        ) as Promise<boolean | UrlTree>
    );
  }

  it('allows users with an accepted role', async () => {
    authServiceMock.hasRole.mockReturnValue(true);

    await expect(runGuard(['ADMIN', 'MANAGER'])).resolves.toBe(true);
  });

  it('redirects users without an accepted role to documents', async () => {
    authServiceMock.hasRole.mockReturnValue(false);

    const result = await runGuard(['ADMIN']);

    expect(result).toBeInstanceOf(UrlTree);
    expect((result as UrlTree).toString()).toBe('/documents');
  });

  it('loads the current profile when it is missing', async () => {
    authServiceMock.profile = null as never;
    authServiceMock.hasRole.mockReturnValue(true);

    await expect(runGuard(['ADMIN'])).resolves.toBe(true);

    expect(authServiceMock.fetchCurrentUser).toHaveBeenCalled();
  });

  it('allows public registration route', async () => {
    authServiceMock.isAuthenticated.mockReturnValue(false);

    await expect(
      runGuard(['ADMIN'], '/register-company')
    ).resolves.toBe(true);

    expect(authServiceMock.login).not.toHaveBeenCalled();
  });
});