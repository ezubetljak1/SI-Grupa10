package ba.unsa.si.docflow.service.role;

import ba.unsa.si.docflow.dao.RoleDAO;
import ba.unsa.si.docflow.dto.role.RoleResponse;
import ba.unsa.si.docflow.entity.RoleEntity;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.mapper.RoleMapper;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleDAO roleDAO;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public RoleEntity getByName(RoleName name) {
        RoleEntity role = roleDAO.findByName(name);

        if (role == null) {
            throw new ApiNotFoundException("Role not found: " + name);
        }

        return role;
    }

    @Override
    @Transactional(readOnly = true)
    public RoleEntity getById(Long id) {
        RoleEntity role = roleDAO.findByPK(id);

        if (role == null) {
            throw new ApiNotFoundException("Role not found: " + id);
        }

        return role;
    }

    @Override
    @Transactional(readOnly = true)
    public RoleEntity getAdminRole() {
        return getByName(RoleName.ADMIN);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        return roleMapper.entitiesToDtos(roleDAO.findAllOrderedByName());
    }
}
