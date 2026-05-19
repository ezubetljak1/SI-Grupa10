import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

import { RoleName, AccountStatus } from '../../../auth/models/auth.models';
import { EmptyStateComponent, PageHeaderComponent, UiCardComponent } from '../../../shared/components';
import { UserApiService } from '../../services/user-api.service';
import { UserCreateRequest, UserResponse } from '../../models/user.models';

@Component({
  selector: 'app-users-page',
  standalone: true,
  imports: [CommonModule, FormsModule, PageHeaderComponent, UiCardComponent, EmptyStateComponent],
  templateUrl: './users-page.html',
  styleUrl: './users-page.scss',
})
export class UsersPageComponent implements OnInit {
  private readonly userApi = inject(UserApiService);
  private readonly toastr = inject(ToastrService);

  readonly roles: RoleName[] = ['ADMIN', 'MANAGER', 'OPERATOR', 'APPROVER'];

  users: UserResponse[] = [];
  loading = false;
  saving = false;
  actionUserId: number | null = null;
  search = '';
  passwordResult: { email: string; temporaryPassword: string; action: 'created' | 'reset' } | null = null;

  readonly newUser: UserCreateRequest = {
    firstName: '',
    lastName: '',
    email: '',
    role: 'OPERATOR',
  };

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;

    this.userApi.list({ search: this.search.trim(), size: 100 }).subscribe({
      next: (response) => {
        this.loading = false;
        this.users = response.payload ?? [];
      },
      error: (error: HttpErrorResponse) => {
        this.loading = false;
        this.toastr.error(this.extractErrorMessage(error.error), 'Users unavailable');
      },
    });
  }

  createUser(): void {
    if (!this.isCreateFormValid()) {
      this.toastr.warning('Enter first name, last name, email and role.', 'Validation');
      return;
    }

    this.saving = true;
    this.userApi.create(this.trimmedNewUser()).subscribe({
      next: (response) => {
        this.saving = false;
        this.passwordResult = response.payload.temporaryPassword
          ? {
              email: response.payload.email,
              temporaryPassword: response.payload.temporaryPassword,
              action: 'created',
            }
          : null;
        this.resetForm();
        this.toastr.success('User created successfully.', 'Success');
        this.loadUsers();
      },
      error: (error: HttpErrorResponse) => {
        this.saving = false;
        this.toastr.error(this.extractErrorMessage(error.error), 'Create user failed');
      },
    });
  }

  changeRole(user: UserResponse, role: RoleName): void {
    if (user.role === role) {
      return;
    }

    this.actionUserId = user.id;
    this.userApi.changeRole(user.id, { role }).subscribe({
      next: (response) => {
        this.actionUserId = null;
        this.replaceUser(response.payload);
        this.toastr.success('Role updated.', 'Success');
      },
      error: (error: HttpErrorResponse) => {
        this.actionUserId = null;
        this.toastr.error(this.extractErrorMessage(error.error), 'Role update failed');
      },
    });
  }

  changeStatus(user: UserResponse): void {
    const nextStatus: AccountStatus = user.accountStatus === 'INACTIVE' ? 'ACTIVE' : 'INACTIVE';
    this.actionUserId = user.id;

    this.userApi.changeStatus(user.id, { accountStatus: nextStatus }).subscribe({
      next: (response) => {
        this.actionUserId = null;
        this.replaceUser(response.payload);
        this.toastr.success(
          nextStatus === 'ACTIVE' ? 'User activated.' : 'User deactivated.',
          'Success'
        );
      },
      error: (error: HttpErrorResponse) => {
        this.actionUserId = null;
        this.toastr.error(this.extractErrorMessage(error.error), 'Status update failed');
      },
    });
  }

  resetPassword(user: UserResponse): void {
    this.actionUserId = user.id;

    this.userApi.resetPassword(user.id).subscribe({
      next: (response) => {
        this.actionUserId = null;
        this.passwordResult = response.payload
          ? {
              email: user.email,
              temporaryPassword: response.payload,
              action: 'reset',
            }
          : null;
        this.toastr.success('Temporary password generated.', 'Success');
      },
      error: (error: HttpErrorResponse) => {
        this.actionUserId = null;
        this.toastr.error(this.extractErrorMessage(error.error), 'Reset failed');
      },
    });
  }

  isBusy(user: UserResponse): boolean {
    return this.actionUserId === user.id;
  }

  statusLabel(status: AccountStatus): string {
    return status.replaceAll('_', ' ');
  }

  private replaceUser(updated: UserResponse): void {
    this.users = this.users.map((user) => (user.id === updated.id ? updated : user));
  }

  private isCreateFormValid(): boolean {
    return (
      this.newUser.firstName.trim().length > 0 &&
      this.newUser.lastName.trim().length > 0 &&
      this.newUser.email.trim().length > 0 &&
      this.newUser.role.trim().length > 0
    );
  }

  private trimmedNewUser(): UserCreateRequest {
    return {
      firstName: this.newUser.firstName.trim(),
      lastName: this.newUser.lastName.trim(),
      email: this.newUser.email.trim(),
      role: this.newUser.role,
    };
  }

  private resetForm(): void {
    this.newUser.firstName = '';
    this.newUser.lastName = '';
    this.newUser.email = '';
    this.newUser.role = 'OPERATOR';
  }

  private extractErrorMessage(errorBody: unknown): string {
    if (typeof errorBody === 'string') {
      return errorBody;
    }

    if (Array.isArray(errorBody) && errorBody.length > 0) {
      const firstError = errorBody[0] as { message?: string; payload?: string; code?: string };
      return firstError.message ?? firstError.payload ?? firstError.code ?? 'Request failed.';
    }

    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as { message?: string; payload?: string; code?: string };
      return body.message ?? body.payload ?? body.code ?? 'Request failed.';
    }

    return 'Request failed.';
  }
}
