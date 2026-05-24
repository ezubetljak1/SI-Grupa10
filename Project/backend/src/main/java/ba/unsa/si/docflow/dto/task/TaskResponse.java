package ba.unsa.si.docflow.dto.task;

import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.TaskStatus;
import ba.unsa.si.docflow.entity.enums.TaskType;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponse {

    private Long id;
    private Long documentId;
    private String documentName;
    private DocumentStatus documentStatus;
    private Long assignedUserId;
    private String assignedUserName;
    private Long assignedByUserId;
    private String assignedByUserName;
    private TaskType taskType;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Long completedByUserId;
    private String completedByUserName;
}
