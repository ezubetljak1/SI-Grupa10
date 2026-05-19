export type RoleName = 'ADMIN' | 'OPERATOR' | 'APPROVER' | 'MANAGER';

export type AccountStatus = 'ACTIVE' | 'INACTIVE' | 'PENDING_PASSWORD_CHANGE';

export interface UserProfile {
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
