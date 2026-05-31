package ba.unsa.si.docflow.service.task;

import ba.unsa.si.docflow.dto.task.AssignTaskRequest;
import ba.unsa.si.docflow.dto.task.TaskResponse;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.enums.TaskType;

import java.util.List;

public interface TaskService {

    TaskResponse assign(Long documentId, AssignTaskRequest request);

    List<TaskResponse> findMyTasks();

    List<TaskResponse> findAll();

    List<TaskResponse> findByDocument(Long documentId);

    TaskResponse start(Long id);

    TaskResponse complete(Long id);

    TaskResponse cancel(Long id);

    void completeActiveTaskForDocument(DocumentEntity document, TaskType taskType, Long completedByUserId);

    void createCorrectionTask(DocumentEntity document, Long assignedToUserId, Long assignedByUserId);
}
