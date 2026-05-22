package ba.unsa.si.docflow.dto.workflow;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {

    @NotBlank(message = "Comment content is required.")
    @Size(max = 2000, message = "Comment content must not exceed 2000 characters.")
    private String content;
}
