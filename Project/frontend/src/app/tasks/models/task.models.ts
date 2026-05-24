import { DocumentStatus } from '../../documents/models/document.models';

export type TaskType = 'EXTRACTION' | 'CORRECTION' | 'APPROVAL';
export type TaskStatus = 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export interface TaskResponse {
  id: number;
  documentId: number;
  documentName: string;
  documentStatus: DocumentStatus;
  assignedUserId: number;
  assignedUserName?: string | null;
  assignedByUserId: number;
  assignedByUserName?: string | null;
  taskType: TaskType;
  status: TaskStatus;
  dueDate?: string | null;
  createdAt: string;
  completedAt?: string | null;
  completedByUserId?: number | null;
  completedByUserName?: string | null;
}

export interface AssignTaskRequest {
  assignedUserId: number;
  taskType: TaskType;
  dueDate?: string | null;
}
