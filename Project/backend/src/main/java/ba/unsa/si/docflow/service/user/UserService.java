package ba.unsa.si.docflow.service.user;

import ba.unsa.si.docflow.dto.user.UserCreateApiRequest;
import ba.unsa.si.docflow.dto.user.UserUpdateRequest;
import ba.unsa.si.docflow.dto.user.UserFilterRequest;
import ba.unsa.si.docflow.dto.user.UserResponse;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.response.PagedResponse;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.entity.enums.AccountStatus;

public interface UserService {

    UserEntity findByKeycloakUserId(String keycloakUserId);

    UserResponse findResponseByKeycloakUserId(String keycloakUserId);

    UserEntity createFirstAdmin(
            Long companyId, String keycloakUserId, String firstName, String lastName, String email);

    PagedResponse<UserResponse> findAll(UserFilterRequest filter, Long companyId);

    UserResponse findByIdAndCompanyId(Long id, Long companyId);

    UserResponse createUser(Long companyId, UserCreateApiRequest request, String keycloakUserId);

    UserResponse update(Long id, UserUpdateRequest request, Long companyId);

    UserResponse changeRole(Long id, RoleName role, Long companyId);

    UserResponse changeStatus(Long id, AccountStatus status, Long companyId);
}
