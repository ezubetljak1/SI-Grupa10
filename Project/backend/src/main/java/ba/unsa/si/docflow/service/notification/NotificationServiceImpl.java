package ba.unsa.si.docflow.service.notification;

import ba.unsa.si.docflow.dao.NotificationDAO;
import ba.unsa.si.docflow.dto.notification.NotificationResponse;
import ba.unsa.si.docflow.dto.notification.UnreadCountResponse;
import ba.unsa.si.docflow.entity.NotificationEntity;
import ba.unsa.si.docflow.entity.enums.NotificationType;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.mapper.NotificationMapper;
import ba.unsa.si.docflow.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO;
    private final CurrentUserService currentUserService;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {
        Long userId = currentUserService.getCurrentUserId();
        List<NotificationEntity> entities = notificationDAO.findByUserId(userId);
        return notificationMapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadCountResponse getMyUnreadCount() {
        Long userId = currentUserService.getCurrentUserId();
        long count = notificationDAO.countUnreadByUserId(userId);
        return new UnreadCountResponse(count);
    }

    @Override
    public NotificationResponse markOneRead(Long id) {
        Long userId = currentUserService.getCurrentUserId();
        NotificationEntity notification = notificationDAO.findByIdAndUserId(id, userId);
        if (notification == null) {
            throw new ApiNotFoundException("Notifikacija nije pronađena.");
        }

        notification.setRead(true);
        notification.setReadAt(Instant.now());
        NotificationEntity updated = notificationDAO.merge(notification);
        return notificationMapper.toResponse(updated);
    }

    @Override
    public void markAllRead() {
        Long userId = currentUserService.getCurrentUserId();
        notificationDAO.markAllReadForUser(userId, Instant.now());
    }

    @Override
    public void createNotification(
            Long targetUserId,
            Long documentId,
            Long commentId,
            NotificationType type,
            String title,
            String text,
            String actionUrl) {

        NotificationEntity notification = NotificationEntity.builder()
                .userId(targetUserId)
                .documentId(documentId)
                .commentId(commentId)
                .type(type)
                .title(title)
                .text(text)
                .actionUrl(actionUrl)
                .read(false)
                .createdAt(Instant.now())
                .build();

        notificationDAO.persist(notification);
    }

    @Override
    public void notifyDocumentAssigned(Long assigneeUserId, Long documentId, String documentName) {
        createNotification(
                assigneeUserId,
                documentId,
                null,
                NotificationType.DOCUMENT_ASSIGNED,
                "Novi dokument dodijeljen",
                "Dodijeljen vam je dokument: " + documentName,
                "/documents/" + documentId);
    }

    @Override
    public void notifyReadyForApproval(Long approverUserId, Long documentId, String documentName) {
        createNotification(
                approverUserId,
                documentId,
                null,
                NotificationType.DOCUMENT_READY_FOR_APPROVAL,
                "Dokument spreman za odobrenje",
                "Dokument " + documentName + " je spreman za vaše odobrenje.",
                "/review");
    }

    @Override
    public void notifyReturnedForCorrection(
            Long operatorUserId, Long documentId, String documentName, Long commentId) {
        createNotification(
                operatorUserId,
                documentId,
                commentId,
                NotificationType.DOCUMENT_RETURNED_FOR_CORRECTION,
                "Dokument vraćen na ispravku",
                "Dokument " + documentName + " je vraćen na ispravku.",
                "/documents/" + documentId);
    }

    @Override
    public void notifyRejected(Long operatorUserId, Long documentId, String documentName, Long commentId) {
        createNotification(
                operatorUserId,
                documentId,
                commentId,
                NotificationType.DOCUMENT_REJECTED,
                "Dokument odbijen",
                "Dokument " + documentName + " je odbijen.",
                "/documents/" + documentId);
    }

    @Override
    public void notifyApproved(Long operatorUserId, Long documentId, String documentName) {
        createNotification(
                operatorUserId,
                documentId,
                null,
                NotificationType.DOCUMENT_APPROVED,
                "Dokument odobren",
                "Dokument " + documentName + " je odobren.",
                "/documents/" + documentId);
    }
}
