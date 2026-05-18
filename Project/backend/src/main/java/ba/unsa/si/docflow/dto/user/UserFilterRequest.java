package ba.unsa.si.docflow.dto.user;

import ba.unsa.si.docflow.dto.BaseFilterRequest;
import ba.unsa.si.docflow.entity.enums.AccountStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserFilterRequest extends BaseFilterRequest {

    private String search;

    private AccountStatus accountStatus;
}