package ba.unsa.si.docflow.mapper;

import ba.unsa.si.docflow.dto.task.TaskResponse;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.TaskEntity;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.LongFunction;

@Component
public class TaskMapper {

    public List<TaskResponse> entitiesToDtos(
            List<TaskEntity> entities, LongFunction<String> userNameResolver) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream().map(entity -> entityToDto(entity, userNameResolver)).toList();
    }

    public TaskResponse entityToDto(TaskEntity entity, LongFunction<String> userNameResolver) {
        if (entity == null) {
            return null;
        }

        TaskResponse response = new TaskResponse();
        response.setId(entity.getId());
        response.setAssignedUserId(entity.getAssignedUserId());
        response.setAssignedByUserId(entity.getAssignedByUserId());
        response.setTaskType(entity.getTaskType());
        response.setStatus(entity.getStatus());
        response.setDueDate(entity.getDueDate());
        response.setCreatedAt(entity.getCreatedAt());
        response.setCompletedAt(entity.getCompletedAt());
        response.setCompletedByUserId(entity.getCompletedByUserId());

        DocumentEntity document = entity.getDocument();
        if (document != null) {
            response.setDocumentId(document.getId());
            response.setDocumentName(document.getName());
            response.setDocumentStatus(document.getDocumentStatus());
        }

        if (userNameResolver != null) {
            response.setAssignedUserName(resolveUserName(userNameResolver, entity.getAssignedUserId()));
            response.setAssignedByUserName(resolveUserName(userNameResolver, entity.getAssignedByUserId()));
            response.setCompletedByUserName(
                    resolveUserName(userNameResolver, entity.getCompletedByUserId()));
        }

        return response;
    }

    private String resolveUserName(LongFunction<String> userNameResolver, Long userId) {
        return userId == null ? null : userNameResolver.apply(userId);
    }
}
