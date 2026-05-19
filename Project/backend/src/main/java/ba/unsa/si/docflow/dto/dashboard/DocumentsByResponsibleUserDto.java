package ba.unsa.si.docflow.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsByResponsibleUserDto {
    private Long userId;

    private String fullName;

    private Long documentCount;
}
