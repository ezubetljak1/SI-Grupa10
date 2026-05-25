package ba.unsa.si.docflow.mapper;

import ba.unsa.si.docflow.dto.workflow.StatusHistoryResponse;
import ba.unsa.si.docflow.entity.CommentEntity;
import ba.unsa.si.docflow.entity.StatusHistoryEntity;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.LongFunction;

@Component
public class StatusHistoryMapper {

    public List<StatusHistoryResponse> entitiesToDtos(
            List<StatusHistoryEntity> entities, LongFunction<String> userNameResolver) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream().map(entity -> entityToDto(entity, userNameResolver)).toList();
    }

    public StatusHistoryResponse entityToDto(
            StatusHistoryEntity entity, LongFunction<String> userNameResolver) {
        if (entity == null) {
            return null;
        }

        StatusHistoryResponse response = new StatusHistoryResponse();
        response.setId(entity.getId());
        response.setDocumentId(
                entity.getDocument() != null ? entity.getDocument().getId() : null);
        response.setOldStatus(entity.getOldStatus());
        response.setNewStatus(entity.getNewStatus());
        response.setAction(entity.getAction());
        response.setChangedAt(entity.getChangedAt());
        response.setChangedByUserId(entity.getChangedByUserId());
        response.setDetails(entity.getDetails());

        if (userNameResolver != null && entity.getChangedByUserId() != null) {
            response.setChangedByUserName(userNameResolver.apply(entity.getChangedByUserId()));
        }

        CommentEntity comment = entity.getComment();
        if (comment != null) {
            response.setCommentId(comment.getId());
            response.setCommentType(comment.getType());
            response.setCommentContent(comment.getContent());
        }

        return response;
    }
}
