package ba.unsa.si.docflow.service.workflow;

import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.dao.StatusHistoryDAO;
import ba.unsa.si.docflow.entity.CommentEntity;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.StatusHistoryEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.StatusHistoryAction;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentStatusTransitionServiceImpl implements DocumentStatusTransitionService {

    private final DocumentDAO documentDAO;
    private final StatusHistoryDAO statusHistoryDAO;

    @Override
    public StatusHistoryEntity recordInitialStatus(
            DocumentEntity document,
            Long changedByUserId,
            StatusHistoryAction action,
            String details) {
        validateRequired(document, document.getDocumentStatus(), action, changedByUserId);

        StatusHistoryEntity history = new StatusHistoryEntity();
        history.setDocument(document);
        history.setOldStatus(null);
        history.setNewStatus(document.getDocumentStatus());
        history.setAction(action);
        history.setChangedByUserId(changedByUserId);
        history.setDetails(details);

        return statusHistoryDAO.persist(history);
    }

    @Override
    public StatusHistoryEntity changeStatus(
            DocumentEntity document,
            DocumentStatus newStatus,
            StatusHistoryAction action,
            Long changedByUserId,
            CommentEntity comment,
            String details) {
        validateRequired(document, newStatus, action, changedByUserId);

        DocumentStatus oldStatus = document.getDocumentStatus();

        document.setDocumentStatus(newStatus);
        DocumentEntity savedDocument = documentDAO.merge(document);

        StatusHistoryEntity history = new StatusHistoryEntity();
        history.setDocument(savedDocument);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setAction(action);
        history.setChangedByUserId(changedByUserId);
        history.setComment(comment);
        history.setDetails(details);

        return statusHistoryDAO.persist(history);
    }

    private void validateRequired(
            DocumentEntity document,
            DocumentStatus newStatus,
            StatusHistoryAction action,
            Long changedByUserId) {
        if (document == null || document.getId() == null) {
            throw new IllegalArgumentException(
                    "Document must be persisted before status history can be recorded.");
        }

        if (newStatus == null) {
            throw new IllegalArgumentException("New document status must not be null.");
        }

        if (action == null) {
            throw new IllegalArgumentException("Status history action must not be null.");
        }

        if (changedByUserId == null) {
            throw new IllegalArgumentException("Changed by user id must not be null.");
        }
    }
}
