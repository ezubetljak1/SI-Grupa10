package ba.unsa.si.docflow.service.user;

import ba.unsa.si.docflow.dto.company.CompanyRegisterRequest;
import ba.unsa.si.docflow.entity.UserEntity;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

/**
 * Internal provisioning API used by company registration and later by admin user management.
 */
@Service
@RequiredArgsConstructor
public class UserProvisioningService {

    private final UserService userService;

    public UserEntity provisionFirstAdmin(Long companyId, String keycloakUserId, CompanyRegisterRequest request) {
        return userService.createFirstAdmin(
                companyId,
                keycloakUserId,
                request.getAdminFirstName(),
                request.getAdminLastName(),
                request.getAdminEmail());
    }

    public UserEntity provisionFirstAdmin(
            Long companyId, String keycloakUserId, String firstName, String lastName, String email) {
        return userService.createFirstAdmin(companyId, keycloakUserId, firstName, lastName, email);
    }
}
