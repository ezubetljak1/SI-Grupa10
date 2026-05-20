package ba.unsa.si.docflow.dto.company;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyRegisterResponse {
    private Long companyId;
    private String companyName;
    private String adminTemporaryPassword;
    private String message;
}
