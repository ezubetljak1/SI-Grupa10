package ba.unsa.si.docflow.dto.user;

import ba.unsa.si.docflow.entity.enums.AccountStatus;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UserStatusChangeRequest {
    @NotNull private AccountStatus accountStatus;
}
