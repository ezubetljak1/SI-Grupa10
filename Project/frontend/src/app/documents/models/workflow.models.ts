import { DocumentStatus } from './document.models';

export type CommentType =
  | 'GENERAL'
  | 'APPROVAL'
  | 'REJECTION'
  | 'CORRECTION_REQUEST'
  | 'SYSTEM';

export type StatusHistoryAction =
  | 'DOCUMENT_UPLOADED'
  | 'DOCUMENT_CREATED'
  | 'DOCUMENT_TYPE_CONFIRMED'
  | 'EXTRACTION_STARTED'
  | 'EXTRACTION_COMPLETED'
  | 'EXTRACTION_FAILED'
  | 'EXTRACTION_RETRIED'
  | 'EXTRACTION_CONFIRMED'
  | 'EXTRACTION_RECONFIRMED'
  | 'DOCUMENT_APPROVED'
  | 'DOCUMENT_REJECTED'
  | 'DOCUMENT_RETURNED_FOR_CORRECTION'
  | 'DOCUMENT_REASSIGNED'
  | 'SYSTEM_STATUS_CHANGE';

export interface StatusHistoryEntry {
  id: number;
  documentId: number;
  oldStatus: DocumentStatus | null;
  newStatus: DocumentStatus;
  action: StatusHistoryAction;
  changedAt: string;
  changedByUserId: number;
  changedByUserName?: string | null;
  commentId?: number | null;
  commentType?: CommentType | null;
  commentContent?: string | null;
  details?: string | null;
}

export interface DocumentComment {
  id: number;
  documentId: number;
  userId: number;
  userName?: string | null;
  type: CommentType;
  content: string;
  createdAt: string;
}

export interface CreateCommentRequest {
  content: string;
}
