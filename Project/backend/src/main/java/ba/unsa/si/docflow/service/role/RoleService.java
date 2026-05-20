package ba.unsa.si.docflow.service.role;

import ba.unsa.si.docflow.dto.role.RoleResponse;
import ba.unsa.si.docflow.entity.RoleEntity;
import ba.unsa.si.docflow.entity.enums.RoleName;

import java.util.List;

public interface RoleService {

    RoleEntity getByName(RoleName name);

    RoleEntity getById(Long id);

    RoleEntity getAdminRole();

    List<RoleResponse> findAll();
}
