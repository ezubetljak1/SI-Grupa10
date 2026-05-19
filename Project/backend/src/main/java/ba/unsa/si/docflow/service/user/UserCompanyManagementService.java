package ba.unsa.si.docflow.service.user;

import ba.unsa.si.docflow.dto.user.*;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.PagedResponse;
import ba.unsa.si.docflow.security.CurrentUserService;
import ba.unsa.si.docflow.service.company.CompanyService;
import ba.unsa.si.docflow.service.keycloak.KeycloakAdminService;
import ba.unsa.si.docflow.service.keycloak.KeycloakUserCreationResult;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCompanyManagementService {

    private final CurrentUserService currentUserService;
    private final CompanyService companyService;
    private final KeycloakAdminService keycloakAdminService;
    private final UserService userService;
    private final UserValidation userValidation;

    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> findAll(UserFilterRequest filter) {
        currentUserService.requireAdmin();
        return userService.findAll(filter, currentUserService.getCurrentCompanyId());
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        currentUserService.requireAdmin();
        return userService.findByIdAndCompanyId(id, currentUserService.getCurrentCompanyId());
    }

    @Transactional(readOnly = true)
    public UserResponse currentUserProfile() {
        return userService.findResponseByKeycloakUserId(currentUserService.getCurrentKeycloakUserId());
    }

    @Transactional
    public UserResponse createUser(UserCreateApiRequest request) {
        currentUserService.requireAdmin();
        Long companyId = currentUserService.getCurrentCompanyId();
        CompanyEntity company = companyService.getEntityById(companyId);

        KeycloakUserCreationResult keycloakUser =
                keycloakAdminService.createUser(
                        request.getEmail(),
                        request.getFirstName(),
                        request.getLastName(),
                        company.getKeycloakGroupId(),
                        true);

        UserResponse response = userService.createUser(companyId, request, keycloakUser.userId());
        response.setTemporaryPassword(keycloakUser.temporaryPassword());

        return response;
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        currentUserService.requireAdmin();
        return userService.update(id, request, currentUserService.getCurrentCompanyId());
    }

    @Transactional
    public UserResponse changeRole(Long id, UserRoleChangeRequest request) {
        currentUserService.requireAdmin();
        return userService.changeRole(
                id, request.getRole(), currentUserService.getCurrentCompanyId());
    }

    @Transactional
    public UserResponse changeStatus(Long id, UserStatusChangeRequest request) {
        currentUserService.requireAdmin();
        Long companyId = currentUserService.getCurrentCompanyId();
        UserEntity user = userValidation.validateExistsInCompany(id, companyId);

        keycloakAdminService.setUserEnabled(
                user.getKeycloakUserId(), request.getAccountStatus() != AccountStatus.INACTIVE);

        return userService.changeStatus(id, request.getAccountStatus(), companyId);
    }

    @Transactional
    public ApiResponse<String> resetPassword(Long id) {
        currentUserService.requireAdmin();
        Long companyId = currentUserService.getCurrentCompanyId();
        UserEntity user = userValidation.validateExistsInCompany(id, companyId);

        String temporaryPassword = keycloakAdminService.resetUserPassword(user.getKeycloakUserId());

        return new ApiResponse<>("OK", temporaryPassword);
    }
}
