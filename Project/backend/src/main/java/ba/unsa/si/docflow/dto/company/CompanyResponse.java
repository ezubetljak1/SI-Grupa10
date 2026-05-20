package ba.unsa.si.docflow.dto.company;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyResponse {
    private Long id;
    private String name;
    private String address;
    private String email;
    private LocalDateTime registrationDate;
    private String status;
}
