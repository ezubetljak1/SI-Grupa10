package ba.unsa.si.docflow.service.user;

import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.user.UserCreateRequest;
import ba.unsa.si.docflow.dto.user.UserResponse;
import ba.unsa.si.docflow.entity.RoleEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.mapper.UserMapper;
import ba.unsa.si.docflow.service.role.RoleService;

import lombok.AllArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final UserMapper userMapper;
    private final UserValidation userValidation;
    private final RoleService roleService;
    private final MessageSource messageSource;

    @Override
    @Transactional(readOnly = true)
    public UserEntity findByKeycloakUserId(String keycloakUserId) {
        UserEntity user = userDAO.findByKeycloakUserId(keycloakUserId);

        if (user == null) {
            throw new ApiNotFoundException(
                    messageSource.getMessage(
                            "user.validation.not_found", null, Locale.getDefault()));
        }

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findResponseByKeycloakUserId(String keycloakUserId) {
        UserEntity user = findByKeycloakUserId(keycloakUserId);
        RoleEntity role = roleService.getById(user.getRoleId());
        return userMapper.entityToDto(user, role);
    }

    @Override
    public UserEntity createFirstAdmin(
            Long companyId,
            String keycloakUserId,
            String firstName,
            String lastName,
            String email) {
        UserCreateRequest request = new UserCreateRequest();
        request.setCompanyId(companyId);
        request.setRole(RoleName.ADMIN);
        request.setKeycloakUserId(keycloakUserId);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);

        userValidation.validateCreate(request);

        RoleEntity adminRole = roleService.getAdminRole();

        UserEntity user = new UserEntity();
        user.setCompanyId(companyId);
        user.setRoleId(adminRole.getId());
        user.setKeycloakUserId(keycloakUserId);
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setAccountStatus(AccountStatus.PENDING_PASSWORD_CHANGE);

        return userDAO.persist(user);
    }
}
