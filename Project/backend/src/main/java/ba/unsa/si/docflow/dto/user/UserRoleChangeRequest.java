package ba.unsa.si.docflow.dto.user;

import ba.unsa.si.docflow.entity.enums.RoleName;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UserRoleChangeRequest {
    @NotNull private RoleName role;
}
