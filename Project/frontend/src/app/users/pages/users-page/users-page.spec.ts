import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

import { PagedResponse } from '../../../models/api.models';
import { UserResponse } from '../../models/user.models';
import { UserApiService } from '../../services/user-api.service';
import { UsersPageComponent } from './users-page';

describe('UsersPageComponent', () => {
  let fixture: ComponentFixture<UsersPageComponent>;
  let userApiMock: {
    list: ReturnType<typeof vi.fn>;
    create: ReturnType<typeof vi.fn>;
    changeRole: ReturnType<typeof vi.fn>;
    changeStatus: ReturnType<typeof vi.fn>;
    resetPassword: ReturnType<typeof vi.fn>;
  };

  const listResponse: PagedResponse<UserResponse> = {
    code: 'OK',
    payload: [],
    page: 0,
    size: 100,
    totalElements: 0,
    totalPages: 0,
  };

  const createdUser: UserResponse = {
    id: 10,
    companyId: 1,
    role: 'OPERATOR',
    firstName: 'Demo',
    lastName: 'User',
    email: 'demo@example.com',
    accountStatus: 'PENDING_PASSWORD_CHANGE',
    temporaryPassword: 'TempPass123!',
  };

  beforeEach(async () => {
    userApiMock = {
      list: vi.fn().mockReturnValue(of(listResponse)),
      create: vi.fn().mockReturnValue(of({ code: 'OK', payload: createdUser })),
      changeRole: vi.fn(),
      changeStatus: vi.fn(),
      resetPassword: vi.fn().mockReturnValue(of({ code: 'OK', payload: 'ResetPass123!' })),
    };

    await TestBed.configureTestingModule({
      imports: [UsersPageComponent],
      providers: [
        { provide: UserApiService, useValue: userApiMock },
        {
          provide: ToastrService,
          useValue: {
            success: vi.fn(),
            warning: vi.fn(),
            error: vi.fn(),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UsersPageComponent);
    fixture.detectChanges();
  });

  it('shows temporary password after creating a user', () => {
    const component = fixture.componentInstance;

    component.newUser.firstName = 'Demo';
    component.newUser.lastName = 'User';
    component.newUser.email = 'demo@example.com';
    component.newUser.role = 'OPERATOR';
    component.createUser();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Temporary password');
    expect(compiled.textContent).toContain('demo@example.com');
    expect(compiled.textContent).toContain('TempPass123!');
  });

  it('shows temporary password after password reset', () => {
    fixture.componentInstance.resetPassword(createdUser);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('demo@example.com');
    expect(compiled.textContent).toContain('ResetPass123!');
  });
});
