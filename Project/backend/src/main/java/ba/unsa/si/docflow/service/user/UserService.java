package ba.unsa.si.docflow.service.user;

import ba.unsa.si.docflow.dto.user.UserResponse;
import ba.unsa.si.docflow.entity.UserEntity;

public interface UserService {

    UserEntity findByKeycloakUserId(String keycloakUserId);

    UserResponse findResponseByKeycloakUserId(String keycloakUserId);

    UserEntity createFirstAdmin(
            Long companyId, String keycloakUserId, String firstName, String lastName, String email);
}
