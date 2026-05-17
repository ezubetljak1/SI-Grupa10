package ba.unsa.si.docflow.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.service.role.RoleService;
import ba.unsa.si.docflow.service.user.UserProvisioningService;
import ba.unsa.si.docflow.service.user.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ba.unsa.si.docflow.config.KeycloakTestConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
@Import(KeycloakTestConfiguration.class)
@Transactional
class UserServiceIntegrationTest {

    @Autowired private CompanyDAO companyDAO;

    @Autowired private UserService userService;

    @Autowired private UserProvisioningService userProvisioningService;

    @Autowired private RoleService roleService;

    private Long companyId;

    @BeforeEach
    void setUp() {
        CompanyEntity company = new CompanyEntity();
        company.setName("Test Company");
        company.setAddress("Address");
        company.setEmail("company-" + System.nanoTime() + "@test.ba");
        company.setRegistrationDate(LocalDateTime.now());
        company.setStatus(CompanyStatus.ACTIVE);
        company.setKeycloakGroupId("group-test");

        companyId = companyDAO.persist(company).getId();
    }

    @Test
    void createFirstAdminLinksCompanyRoleAndKeycloakId() {
        UserEntity admin =
                userProvisioningService.provisionFirstAdmin(
                        companyId,
                        "kc-admin-001",
                        "Emina",
                        "Zubetljak",
                        "admin-" + System.nanoTime() + "@test.ba");

        assertEquals(companyId, admin.getCompanyId());
        assertEquals(RoleName.ADMIN, roleService.getById(admin.getRoleId()).getName());
        assertEquals("kc-admin-001", admin.getKeycloakUserId());
        assertEquals(AccountStatus.PENDING_PASSWORD_CHANGE, admin.getAccountStatus());
    }

    @Test
    void findByKeycloakUserIdReturnsPersistedUser() {
        String keycloakUserId = "kc-admin-002";
        String email = "admin2-" + System.nanoTime() + "@test.ba";

        userService.createFirstAdmin(companyId, keycloakUserId, "Aida", "Hadzic", email);

        UserEntity found = userService.findByKeycloakUserId(keycloakUserId);

        assertEquals(email.toLowerCase(), found.getEmail());
        assertEquals(companyId, found.getCompanyId());
    }

    @Test
    void cannotCreateUserWithoutValidCompany() {
        assertThrows(
                ApiNotFoundException.class,
                () ->
                        userService.createFirstAdmin(
                                99999L, "kc-missing", "Test", "User", "x@test.ba"));
    }

    @Test
    void duplicateEmailInSameCompanyIsRejected() {
        String email = "duplicate-" + System.nanoTime() + "@test.ba";

        userService.createFirstAdmin(companyId, "kc-dup-1", "First", "User", email);

        assertThrows(
                ApiValidationException.class,
                () -> userService.createFirstAdmin(companyId, "kc-dup-2", "Second", "User", email));
    }
}
