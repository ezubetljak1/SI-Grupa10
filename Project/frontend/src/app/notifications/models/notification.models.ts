export type NotificationType =
  | 'DOCUMENT_ASSIGNED'
  | 'DOCUMENT_READY_FOR_APPROVAL'
  | 'DOCUMENT_RETURNED_FOR_CORRECTION'
  | 'DOCUMENT_REJECTED'
  | 'DOCUMENT_APPROVED'
  | 'EMAIL_REMINDER';

export interface Notification {
  id: number;
  userId: number;
  documentId: number | null;
  commentId: number | null;
  type: NotificationType;
  title: string;
  text: string;
  actionUrl: string | null;
  read: boolean;
  createdAt: string;
  readAt: string | null;
  emailSentAt: string | null;
}

export interface UnreadCountResponse {
  unreadCount: number;
}

export const NOTIFICATION_TYPE_LABELS: Record<NotificationType, string> = {
  DOCUMENT_ASSIGNED: 'Task assigned',
  DOCUMENT_READY_FOR_APPROVAL: 'Ready for approval',
  DOCUMENT_RETURNED_FOR_CORRECTION: 'Returned for correction',
  DOCUMENT_REJECTED: 'Document rejected',
  DOCUMENT_APPROVED: 'Document approved',
  EMAIL_REMINDER: 'Notification reminder',
};

export const NOTIFICATION_TYPE_ICONS: Record<NotificationType, string> = {
  DOCUMENT_ASSIGNED: '📁',
  DOCUMENT_READY_FOR_APPROVAL: '📝',
  DOCUMENT_RETURNED_FOR_CORRECTION: '⚠️',
  DOCUMENT_REJECTED: '❌',
  DOCUMENT_APPROVED: '✅',
  EMAIL_REMINDER: '✉️',
};
