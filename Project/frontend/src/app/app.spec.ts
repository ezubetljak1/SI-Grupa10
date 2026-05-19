import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { App } from './app';
import { AuthService } from './auth/services/auth.service';

describe('App', () => {
  const authServiceMock = {
    profile$: of(null),
    authenticated$: of(false),
    init: vi.fn().mockResolvedValue(false),
    hasRole: vi.fn().mockReturnValue(false),
    login: vi.fn(),
    logout: vi.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should initialize authentication on startup', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    expect(authServiceMock.init).toHaveBeenCalled();
  });
});
