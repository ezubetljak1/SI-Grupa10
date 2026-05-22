package ba.unsa.si.docflow.dto.workflow;

import ba.unsa.si.docflow.entity.enums.CommentType;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponse {

    private Long id;
    private Long documentId;
    private Long userId;
    private String userName;
    private CommentType type;
    private String content;
    private LocalDateTime createdAt;
}
