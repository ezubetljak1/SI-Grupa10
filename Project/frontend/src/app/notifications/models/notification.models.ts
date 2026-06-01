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
  DOCUMENT_ASSIGNED: 'Novi dokument dodijeljen',
  DOCUMENT_READY_FOR_APPROVAL: 'Spremno za odobrenje',
  DOCUMENT_RETURNED_FOR_CORRECTION: 'Vraćeno na ispravku',
  DOCUMENT_REJECTED: 'Dokument odbijen',
  DOCUMENT_APPROVED: 'Dokument odobren',
  EMAIL_REMINDER: 'Podsjetnik obavijesti',
};

export const NOTIFICATION_TYPE_ICONS: Record<NotificationType, string> = {
  DOCUMENT_ASSIGNED: '📁',
  DOCUMENT_READY_FOR_APPROVAL: '📝',
  DOCUMENT_RETURNED_FOR_CORRECTION: '⚠️',
  DOCUMENT_REJECTED: '❌',
  DOCUMENT_APPROVED: '✅',
  EMAIL_REMINDER: '✉️',
};
