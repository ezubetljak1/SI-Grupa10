package ba.unsa.si.docflow.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DashboardResponse {
    private Long totalDocuments;

    private Map<String, Long> documentsByStatus;

    private List<DocumentsByResponsibleUserDto> documentsByResponsibleUser;
}
