package ba.unsa.si.docflow.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private Long companyId;
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String accountStatus;
    private String temporaryPassword;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
