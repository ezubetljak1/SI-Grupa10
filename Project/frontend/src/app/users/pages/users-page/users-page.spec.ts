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
    getCurrentUser: ReturnType<typeof vi.fn>;
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
  };

  beforeEach(async () => {
    userApiMock = {
      list: vi.fn().mockReturnValue(of(listResponse)),
      create: vi.fn().mockReturnValue(of({ code: 'OK', payload: createdUser })),
      changeRole: vi.fn(),
      changeStatus: vi.fn(),
      resetPassword: vi.fn(),
      getCurrentUser: vi.fn().mockReturnValue(of({ code: 'OK', payload: createdUser })),
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

  it('creates a user and reloads the list', () => {
    const component = fixture.componentInstance;

    component.newUser.firstName = 'Demo';
    component.newUser.lastName = 'User';
    component.newUser.email = 'demo@example.com';
    component.newUser.role = 'OPERATOR';
    component.createUser();
    fixture.detectChanges();

    expect(userApiMock.create).toHaveBeenCalledWith({
      firstName: 'Demo',
      lastName: 'User',
      email: 'demo@example.com',
      role: 'OPERATOR',
    });
    expect(userApiMock.list).toHaveBeenCalledTimes(2);
  });
});
