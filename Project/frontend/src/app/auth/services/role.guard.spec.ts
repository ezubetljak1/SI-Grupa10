import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, UrlTree } from '@angular/router';
import { of } from 'rxjs';

import { AuthService } from './auth.service';
import { roleGuard } from './role.guard';

describe('roleGuard', () => {
  const authServiceMock = {
    init: vi.fn().mockResolvedValue(true),
    isAuthenticated: vi.fn(),
    profile: null as null | { role: string },
    fetchCurrentUser: vi.fn().mockReturnValue(of(null)),
    hasRole: vi.fn(),
    login: vi.fn(),
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
      ],
    }).compileComponents();
  });

  it('redirects approvers away from the review route', async () => {
    const router = TestBed.inject(Router);

    authServiceMock.isAuthenticated.mockReturnValue(true);
    authServiceMock.hasRole.mockReturnValue(false);

    const result = await TestBed.runInInjectionContext(() =>
      roleGuard(
        { data: { roles: ['ADMIN', 'MANAGER'] } } as never,
        { url: '/review' } as never
      )
    );

    expect(router.serializeUrl(result as UrlTree)).toBe('/documents');
  });
});
