package ba.unsa.si.docflow.dto.workflow;

import ba.unsa.si.docflow.entity.enums.CommentType;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.StatusHistoryAction;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StatusHistoryResponse {

    private Long id;
    private Long documentId;
    private DocumentStatus oldStatus;
    private DocumentStatus newStatus;
    private StatusHistoryAction action;
    private LocalDateTime changedAt;
    private Long changedByUserId;
    private String changedByUserName;
    private Long commentId;
    private CommentType commentType;
    private String commentContent;
    private String details;
}
