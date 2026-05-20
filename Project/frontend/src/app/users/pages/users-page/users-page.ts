import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

import { AccountStatus, RoleName } from '../../../auth/models/auth.models';
import {
  EmptyStateComponent,
  PageHeaderComponent,
  UiCardComponent,
} from '../../../shared/components';
import { UserCreateRequest, UserResponse } from '../../models/user.models';
import { UserApiService } from '../../services/user-api.service';

type PasswordResult = {
  email: string;
  temporaryPassword: string;
  action: 'created' | 'reset';
};

type RoleOption = {
  value: RoleName;
  label: string;
  description: string;
};

@Component({
  selector: 'app-users-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    PageHeaderComponent,
    UiCardComponent,
    EmptyStateComponent,
  ],
  templateUrl: './users-page.html',
  styleUrl: './users-page.scss',
})
export class UsersPageComponent implements OnInit {
  private readonly userApi = inject(UserApiService);
  private readonly toastr = inject(ToastrService);

  readonly roleOptions: RoleOption[] = [
    { value: 'ADMIN', label: 'Admin', description: 'Full company administration access' },
    { value: 'MANAGER', label: 'Manager', description: 'Can manage document workflow and users' },
    { value: 'OPERATOR', label: 'Operator', description: 'Can upload and process documents' },
    { value: 'APPROVER', label: 'Approver', description: 'Can review and approve documents' },
  ];

  users: UserResponse[] = [];
  loading = false;
  saving = false;
  actionUserId: number | null = null;
  currentUserId: number | null = null;
  search = '';

  passwordResult: PasswordResult | null = null;

  readonly newUser: UserCreateRequest = {
    firstName: '',
    lastName: '',
    email: '',
    role: 'OPERATOR',
  };

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;

    this.userApi
      .list({ search: this.search.trim(), size: 100 })
      .subscribe({
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

  clearSearch(): void {
    this.search = '';
    this.loadUsers();
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
    if (user.role === role || this.isCurrentUser(user)) {
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
    if (this.isCurrentUser(user)) {
      return;
    }

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
          ? { email: user.email, temporaryPassword: response.payload, action: 'reset' }
          : null;

        this.toastr.success('Temporary password generated.', 'Success');
      },
      error: (error: HttpErrorResponse) => {
        this.actionUserId = null;
        this.toastr.error(this.extractErrorMessage(error.error), 'Reset failed');
      },
    });
  }

  copyTemporaryPassword(): void {
    if (!this.passwordResult?.temporaryPassword || !navigator.clipboard) {
      return;
    }

    navigator.clipboard
      .writeText(this.passwordResult.temporaryPassword)
      .then(() => this.toastr.success('Temporary password copied.', 'Copied'))
      .catch(() => this.toastr.warning('Could not copy password automatically.', 'Copy failed'));
  }

  isBusy(user: UserResponse): boolean {
    return this.actionUserId === user.id;
  }

  isCurrentUser(user: UserResponse): boolean {
    return this.currentUserId === user.id;
  }

  statusLabel(status: AccountStatus): string {
    return status.replaceAll('_', ' ');
  }

  roleLabel(role: RoleName): string {
    return this.roleOptions.find((option) => option.value === role)?.label ?? role;
  }

  getInitials(user: UserResponse): string {
    const first = user.firstName?.trim().charAt(0) ?? '';
    const last = user.lastName?.trim().charAt(0) ?? '';
    return `${first}${last}`.toUpperCase() || '?';
  }

  fullName(user: UserResponse): string {
    return `${user.firstName} ${user.lastName}`.trim();
  }

  statusClass(status: AccountStatus): string {
    switch (status) {
      case 'ACTIVE':
        return 'status-pill--active';
      case 'PENDING_PASSWORD_CHANGE':
        return 'status-pill--pending';
      case 'INACTIVE':
        return 'status-pill--inactive';
      default:
        return 'status-pill--neutral';
    }
  }

  get totalUsers(): number {
    return this.users.length;
  }

  get activeUsers(): number {
    return this.users.filter((user) => user.accountStatus === 'ACTIVE').length;
  }

  get pendingUsers(): number {
    return this.users.filter((user) => user.accountStatus === 'PENDING_PASSWORD_CHANGE').length;
  }

  get adminUsers(): number {
    return this.users.filter((user) => user.role === 'ADMIN').length;
  }

  private loadCurrentUser(): void {
    this.userApi.getCurrentUser().subscribe({
      next: (response) => {
        this.currentUserId = response.payload.id;
      },
      error: () => {
        this.currentUserId = null;
      },
    });
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
