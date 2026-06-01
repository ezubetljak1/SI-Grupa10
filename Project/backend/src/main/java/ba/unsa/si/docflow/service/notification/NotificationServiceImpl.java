package ba.unsa.si.docflow.service.notification;

import ba.unsa.si.docflow.dao.NotificationDAO;
import ba.unsa.si.docflow.dao.TaskDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.notification.NotificationResponse;
import ba.unsa.si.docflow.dto.notification.UnreadCountResponse;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.NotificationEntity;
import ba.unsa.si.docflow.entity.TaskEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.NotificationType;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.entity.enums.TaskType;
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
    private final TaskDAO taskDAO;
    private final UserDAO userDAO;

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
            throw new ApiNotFoundException("Notification was not found.");
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

        if (targetUserId == null) {
            return;
        }

        NotificationEntity notification =
                NotificationEntity.builder()
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
                "New task assigned",
                "You have been assigned a task for document: " + documentName,
                "/documents/" + documentId);
    }

    @Override
    public void notifyReadyForApproval(Long approverUserId, Long documentId, String documentName) {

        createNotification(
                approverUserId,
                documentId,
                null,
                NotificationType.DOCUMENT_READY_FOR_APPROVAL,
                "Document ready for approval",
                "Document " + documentName + " is ready for your approval.",
                "/documents/" + documentId);
    }

    @Override
    public void notifyReturnedForCorrection(
            Long operatorUserId, Long documentId, String documentName, Long commentId) {

        createNotification(
                operatorUserId,
                documentId,
                commentId,
                NotificationType.DOCUMENT_RETURNED_FOR_CORRECTION,
                "Document returned for correction",
                "Document " + documentName + " has been returned for correction.",
                "/documents/" + documentId);
    }

    @Override
    public void notifyRejected(
            Long operatorUserId, Long documentId, String documentName, Long commentId) {

        createNotification(
                operatorUserId,
                documentId,
                commentId,
                NotificationType.DOCUMENT_REJECTED,
                "Document rejected",
                "Document " + documentName + " has been rejected.",
                "/documents/" + documentId);
    }

    @Override
    public void notifyApproved(Long operatorUserId, Long documentId, String documentName) {

        createNotification(
                operatorUserId,
                documentId,
                null,
                NotificationType.DOCUMENT_APPROVED,
                "Document approved",
                "Document " + documentName + " has been approved.",
                "/documents/" + documentId);
    }

    @Override
    public void notifyReadyForApprovalRecipients(DocumentEntity document) {
        TaskEntity activeApprovalTask =
                taskDAO.findActiveByDocumentIdAndTaskType(document.getId(), TaskType.APPROVAL);

        if (activeApprovalTask != null) {
            notifyReadyForApproval(
                    activeApprovalTask.getAssignedUserId(), document.getId(), document.getName());

            return;
        }

        List<UserEntity> approvers =
                userDAO.findActiveByCompanyIdAndRoleName(
                        document.getCompanyId(), RoleName.APPROVER);

        for (UserEntity approver : approvers) {
            notifyReadyForApproval(approver.getId(), document.getId(), document.getName());
        }
    }
}
