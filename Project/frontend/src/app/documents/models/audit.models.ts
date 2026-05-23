export interface AuditLog {
  id: number;
  action: string;
  details: string;
  timestamp: string;
  userId: number;
  userFullName: string;
}
