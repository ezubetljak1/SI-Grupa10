package ba.unsa.si.docflow.service.workflow;

import ba.unsa.si.docflow.entity.CommentEntity;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.StatusHistoryEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.StatusHistoryAction;

public interface DocumentStatusTransitionService {

    StatusHistoryEntity recordInitialStatus(
            DocumentEntity document,
            Long changedByUserId,
            StatusHistoryAction action,
            String details);

    StatusHistoryEntity changeStatus(
            DocumentEntity document,
            DocumentStatus newStatus,
            StatusHistoryAction action,
            Long changedByUserId,
            CommentEntity comment,
            String details);
}
