package ba.unsa.si.docflow.dto.task;

import ba.unsa.si.docflow.entity.enums.TaskType;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignTaskRequest {

    @NotNull
    private Long assignedUserId;

    @NotNull
    private TaskType taskType;

    private LocalDateTime dueDate;
}
