package ba.unsa.si.docflow.service.task;

import ba.unsa.si.docflow.dto.task.AssignTaskRequest;
import ba.unsa.si.docflow.dto.task.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse assign(Long documentId, AssignTaskRequest request);

    List<TaskResponse> findMyTasks();

    List<TaskResponse> findAll();

    TaskResponse start(Long id);

    TaskResponse complete(Long id);

    TaskResponse cancel(Long id);
}
