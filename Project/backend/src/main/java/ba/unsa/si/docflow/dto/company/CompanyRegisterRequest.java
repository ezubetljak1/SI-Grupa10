package ba.unsa.si.docflow.dto.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class CompanyRegisterRequest {

    @NotBlank(message = "Company name is required.")
    @Size(max = 255)
    private String companyName;

    @NotBlank(message = "Company address is required.")
    @Size(max = 500)
    private String companyAddress;

    @NotBlank(message = "Company email is required.")
    @Email(message = "Company email must be valid.")
    @Size(max = 255)
    private String companyEmail;

    @NotBlank(message = "Admin first name is required.")
    @Size(max = 100)
    private String adminFirstName;

    @NotBlank(message = "Admin last name is required.")
    @Size(max = 100)
    private String adminLastName;

    @NotBlank(message = "Admin email is required.")
    @Email(message = "Admin email must be valid.")
    @Size(max = 255)
    private String adminEmail;
}
