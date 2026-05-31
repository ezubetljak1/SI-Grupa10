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

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCompanyManagementService {

    private final CurrentUserService currentUserService;
    private final CompanyService companyService;
    private final KeycloakAdminService keycloakAdminService;
    private final UserService userService;
    private final UserValidation userValidation;

    @Transactional
    public PagedResponse<UserResponse> findAll(UserFilterRequest filter) {
        currentUserService.requireAnyRole(RoleName.ADMIN, RoleName.MANAGER);

        Long companyId = currentUserService.getCurrentCompanyId();
        PagedResponse<UserResponse> response = userService.findAll(filter, companyId);

        if (response.getPayload() != null) {
            List<UserResponse> syncedUsers =
                    response.getPayload().stream()
                            .map(user -> syncPasswordChangeStatus(user, companyId))
                            .toList();

            response.setPayload(syncedUsers);
        }

        return response;
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        currentUserService.requireAdmin();
        return userService.findByIdAndCompanyId(id, currentUserService.getCurrentCompanyId());
    }

    @Transactional
    public UserResponse currentUserProfile() {
        String keycloakUserId = currentUserService.getCurrentKeycloakUserId();
        UserResponse response = userService.findResponseByKeycloakUserId(keycloakUserId);

        Long companyId = currentUserService.getCurrentCompanyId();
        return syncPasswordChangeStatus(response, companyId);
    }

    @Transactional
    public UserResponse createUser(UserCreateApiRequest request) {
        currentUserService.requireAdmin();

        Long companyId = currentUserService.getCurrentCompanyId();
        CompanyEntity company = companyService.getEntityById(companyId);

        String keycloakUserId = null;

        try {
            KeycloakUserCreationResult keycloakUser =
                    keycloakAdminService.createUser(
                            request.getEmail(),
                            request.getFirstName(),
                            request.getLastName(),
                            company.getKeycloakGroupId(),
                            true);

            keycloakUserId = keycloakUser.userId();

            UserResponse response = userService.createUser(companyId, request, keycloakUserId);
            keycloakAdminService.sendPasswordSetupEmail(keycloakUserId);

            return response;
        } catch (RuntimeException ex) {
            keycloakAdminService.deleteUser(keycloakUserId);
            throw ex;
        }
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
        UserResponse updatedUser =
                userService.changeStatus(id, request.getAccountStatus(), companyId);

        UserEntity user = userValidation.validateExistsInCompany(id, companyId);

        keycloakAdminService.setUserEnabled(
                user.getKeycloakUserId(), request.getAccountStatus() != AccountStatus.INACTIVE);

        return updatedUser;
    }

    @Transactional
    public ApiResponse<String> resetPassword(Long id) {
        currentUserService.requireAdmin();

        Long companyId = currentUserService.getCurrentCompanyId();
        UserEntity user = userValidation.validateExistsInCompany(id, companyId);

        keycloakAdminService.sendPasswordSetupEmail(user.getKeycloakUserId());
        userService.changeStatus(id, AccountStatus.PENDING_PASSWORD_CHANGE, companyId);

        return new ApiResponse<>("OK", "Password reset email has been sent.");
    }

    private UserResponse syncPasswordChangeStatus(UserResponse response, Long companyId) {
        if (!AccountStatus.PENDING_PASSWORD_CHANGE.name().equals(response.getAccountStatus())) {
            return response;
        }

        UserEntity user = userValidation.validateExistsInCompany(response.getId(), companyId);
        boolean passwordUpdateRequired =
                keycloakAdminService.isPasswordUpdateRequired(user.getKeycloakUserId());

        if (passwordUpdateRequired) {
            return response;
        }

        return userService.changeStatus(user.getId(), AccountStatus.ACTIVE, companyId);
    }
}