package ba.unsa.si.docflow.service.company;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dto.company.CompanyRegisterRequest;
import ba.unsa.si.docflow.dto.company.CompanyRegisterResponse;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.exception.KeycloakIntegrationException;
import ba.unsa.si.docflow.service.keycloak.KeycloakAdminService;
import ba.unsa.si.docflow.service.keycloak.KeycloakUserCreationResult;
import ba.unsa.si.docflow.service.user.UserProvisioningService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyRegistrationService {

    private final CompanyValidation companyValidation;
    private final CompanyDAO companyDAO;
    private final KeycloakAdminService keycloakAdminService;
    private final UserProvisioningService userProvisioningService;
    private final MessageSource messageSource;

    @Transactional
    public CompanyRegisterResponse register(CompanyRegisterRequest request) {
        companyValidation.validateRegister(request);

        String keycloakGroupId = null;
        String keycloakUserId = null;
        CompanyEntity company = null;

        try {
            keycloakGroupId = keycloakAdminService.createCompanyGroup(request.getCompanyName());
            company = persistCompany(request, keycloakGroupId);

            KeycloakUserCreationResult adminUser =
                    keycloakAdminService.createUser(
                            request.getAdminEmail(),
                            request.getAdminFirstName(),
                            request.getAdminLastName(),
                            keycloakGroupId,
                            true);

            keycloakUserId = adminUser.userId();

            userProvisioningService.provisionFirstAdmin(company.getId(), keycloakUserId, request);

            return new CompanyRegisterResponse(
                    company.getId(),
                    company.getName(),
                    adminUser.temporaryPassword(),
                    messageSource.getMessage(
                            "company.registration.success", null, Locale.getDefault()));
        } catch (RuntimeException ex) {
            rollbackRegistration(keycloakUserId, keycloakGroupId, company);
            throw ex;
        } catch (Exception ex) {
            rollbackRegistration(keycloakUserId, keycloakGroupId, company);
            throw new KeycloakIntegrationException("Company registration failed.", ex);
        }
    }

    private CompanyEntity persistCompany(CompanyRegisterRequest request, String keycloakGroupId) {
        CompanyEntity company = new CompanyEntity();
        company.setName(request.getCompanyName().trim());
        company.setAddress(request.getCompanyAddress().trim());
        company.setEmail(request.getCompanyEmail().trim().toLowerCase());
        company.setRegistrationDate(LocalDateTime.now());
        company.setStatus(CompanyStatus.ACTIVE);
        company.setKeycloakGroupId(keycloakGroupId);

        return companyDAO.persist(company);
    }

    private void rollbackRegistration(
            String keycloakUserId, String keycloakGroupId, CompanyEntity company) {
        keycloakAdminService.deleteUser(keycloakUserId);
        keycloakAdminService.deleteCompanyGroup(keycloakGroupId);

        if (company != null && company.getId() != null) {
            CompanyEntity managed = companyDAO.findByPK(company.getId());

            if (managed != null) {
                companyDAO.remove(managed);
            }
        }

        log.warn("Rolled back company registration attempt");
    }
}
