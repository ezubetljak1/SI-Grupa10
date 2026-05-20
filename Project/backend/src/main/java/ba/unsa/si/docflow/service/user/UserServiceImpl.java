package ba.unsa.si.docflow.service.user;

import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.user.*;
import ba.unsa.si.docflow.entity.RoleEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.mapper.UserMapper;
import ba.unsa.si.docflow.response.PagedResponse;
import ba.unsa.si.docflow.response.ValidationErrors;
import ba.unsa.si.docflow.service.role.RoleService;

import lombok.AllArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> findAll(UserFilterRequest filter, Long companyId) {
        List<UserEntity> users = userDAO.findByFilter(filter, companyId);
        long total = userDAO.countByFilter(filter, companyId);

        List<UserResponse> responses =
                users.stream()
                        .map(
                                user -> {
                                    RoleEntity role = roleService.getById(user.getRoleId());
                                    return userMapper.entityToDto(user, role);
                                })
                        .toList();

        int totalPages = (int) Math.ceil((double) total / filter.getSize());

        return new PagedResponse<>(
                "OK", responses, filter.getPage(), filter.getSize(), total, totalPages);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findByIdAndCompanyId(Long id, Long companyId) {
        UserEntity user = userValidation.validateExistsInCompany(id, companyId);
        RoleEntity role = roleService.getById(user.getRoleId());
        return userMapper.entityToDto(user, role);
    }

    @Override
    public UserResponse createUser(
            Long companyId, UserCreateApiRequest request, String keycloakUserId) {
        UserCreateRequest createRequest = new UserCreateRequest();
        createRequest.setCompanyId(companyId);
        createRequest.setRole(request.getRole());
        createRequest.setKeycloakUserId(keycloakUserId);
        createRequest.setFirstName(request.getFirstName());
        createRequest.setLastName(request.getLastName());
        createRequest.setEmail(request.getEmail());

        userValidation.validateCreate(createRequest);
        RoleEntity role = roleService.getByName(request.getRole());

        UserEntity user = new UserEntity();
        user.setCompanyId(companyId);
        user.setRoleId(role.getId());
        user.setKeycloakUserId(keycloakUserId);
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setAccountStatus(AccountStatus.PENDING_PASSWORD_CHANGE);

        UserEntity saved = userDAO.persist(user);
        return userMapper.entityToDto(saved, role);
    }

    @Override
    public UserResponse update(Long id, UserUpdateRequest request, Long companyId) {
        UserEntity user = userValidation.validateExistsInCompany(id, companyId);

        userValidation.validateUpdate(request);

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());

        UserEntity updated = userDAO.merge(user);
        RoleEntity role = roleService.getById(updated.getRoleId());
        return userMapper.entityToDto(updated, role);
    }

    @Override
    public UserResponse changeRole(Long id, RoleName roleName, Long companyId) {
        UserEntity user = userValidation.validateExistsInCompany(id, companyId);
        RoleEntity newRole = roleService.getByName(roleName);

        RoleEntity currentRole = roleService.getById(user.getRoleId());

        if (currentRole.getName() == RoleName.ADMIN
                && roleName != RoleName.ADMIN
                && userDAO.countActiveAdminsByCompanyId(companyId) <= 1) {
            ValidationErrors errors = new ValidationErrors();
            errors.add(
                    "LAST_ADMIN_ROLE_CHANGE_FORBIDDEN",
                    "At least one active administrator must remain in the company.");
            throw new ApiValidationException(errors);
        }

        user.setRoleId(newRole.getId());

        UserEntity updated = userDAO.merge(user);
        return userMapper.entityToDto(updated, newRole);
    }

    @Override
    public UserResponse changeStatus(Long id, AccountStatus status, Long companyId) {
        UserEntity user = userValidation.validateExistsInCompany(id, companyId);

        RoleEntity role = roleService.getById(user.getRoleId());

        if (role.getName() == RoleName.ADMIN
                && status == AccountStatus.INACTIVE
                && userDAO.countActiveAdminsByCompanyId(companyId) <= 1) {
            ValidationErrors errors = new ValidationErrors();
            errors.add(
                    "LAST_ADMIN_DEACTIVATION_FORBIDDEN",
                    "At least one active administrator must remain in the company.");
            throw new ApiValidationException(errors);
        }

        user.setAccountStatus(status);

        UserEntity updated = userDAO.merge(user);
        return userMapper.entityToDto(updated, role);
    }
}
