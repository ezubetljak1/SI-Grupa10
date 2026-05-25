import { AccountStatus, RoleName } from '../../auth/models/auth.models';

export interface UserResponse {
  id: number;
  companyId: number;
  role: RoleName;
  firstName: string;
  lastName: string;
  email: string;
  accountStatus: AccountStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface UserCreateRequest {
  firstName: string;
  lastName: string;
  email: string;
  role: RoleName;
}

export interface UserUpdateRequest {
  firstName: string;
  lastName: string;
}

export interface UserRoleChangeRequest {
  role: RoleName;
}

export interface UserStatusChangeRequest {
  accountStatus: AccountStatus;
}
