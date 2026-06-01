package ba.unsa.si.docflow.service.notification;

import ba.unsa.si.docflow.dto.notification.NotificationResponse;
import ba.unsa.si.docflow.dto.notification.UnreadCountResponse;
import ba.unsa.si.docflow.entity.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getMyNotifications();
    UnreadCountResponse getMyUnreadCount();
    NotificationResponse markOneRead(Long id);
    void markAllRead();
    void createNotification(
            Long targetUserId,
            Long documentId,
            Long commentId,
            NotificationType type,
            String title,
            String text,
            String actionUrl);
    void notifyDocumentAssigned(Long assigneeUserId, Long documentId, String documentName);
    void notifyReadyForApproval(Long approverUserId, Long documentId, String documentName);
    void notifyReturnedForCorrection(Long operatorUserId, Long documentId, String documentName, Long commentId);
    void notifyRejected(Long operatorUserId, Long documentId, String documentName, Long commentId);
    void notifyApproved(Long operatorUserId, Long documentId, String documentName);
}
