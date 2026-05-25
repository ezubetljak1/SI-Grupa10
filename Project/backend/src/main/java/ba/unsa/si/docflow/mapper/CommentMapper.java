package ba.unsa.si.docflow.mapper;

import ba.unsa.si.docflow.dto.workflow.CommentResponse;
import ba.unsa.si.docflow.entity.CommentEntity;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.LongFunction;

@Component
public class CommentMapper {

    public List<CommentResponse> entitiesToDtos(
            List<CommentEntity> entities, LongFunction<String> userNameResolver) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream().map(entity -> entityToDto(entity, userNameResolver)).toList();
    }

    public CommentResponse entityToDto(CommentEntity entity, LongFunction<String> userNameResolver) {
        if (entity == null) {
            return null;
        }

        CommentResponse response = new CommentResponse();
        response.setId(entity.getId());
        response.setDocumentId(
                entity.getDocument() != null ? entity.getDocument().getId() : null);
        response.setUserId(entity.getUserId());
        response.setType(entity.getType());
        response.setContent(entity.getContent());
        response.setCreatedAt(entity.getCreatedAt());

        if (userNameResolver != null && entity.getUserId() != null) {
            response.setUserName(userNameResolver.apply(entity.getUserId()));
        }

        return response;
    }
}
