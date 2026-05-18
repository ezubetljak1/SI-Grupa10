package ba.unsa.si.docflow.user;

import static org.junit.jupiter.api.Assertions.*;

import ba.unsa.si.docflow.config.KeycloakTestConfiguration;
import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.user.*;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.response.PagedResponse;
import ba.unsa.si.docflow.service.role.RoleService;
import ba.unsa.si.docflow.service.user.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class UserServiceCrudIntegrationTest {

    @Autowired private UserService userService;
    @Autowired private UserDAO userDAO;
    @Autowired private CompanyDAO companyDAO;
    @Autowired private RoleService roleService;

    private Long companyId;
    private Long otherCompanyId;
    private Long userId;

    @BeforeEach
    void setUp() {
        CompanyEntity company = new CompanyEntity();
        company.setName("Test Company A");
        company.setAddress("Adress A");
        company.setEmail("company-a-" + System.nanoTime() + "@test.ba");
        company.setRegistrationDate(LocalDateTime.now());
        company.setStatus(CompanyStatus.ACTIVE);
        company.setKeycloakGroupId("group-a");
        companyId = companyDAO.persist(company).getId();

        CompanyEntity otherCompany = new CompanyEntity();
        otherCompany.setName("Test Company B");
        otherCompany.setAddress("Adress B");
        otherCompany.setEmail("company-b-" + System.nanoTime() + "@test.ba");
        otherCompany.setRegistrationDate(LocalDateTime.now());
        otherCompany.setStatus(CompanyStatus.ACTIVE);
        otherCompany.setKeycloakGroupId("group-b");
        otherCompanyId = companyDAO.persist(otherCompany).getId();

        UserEntity user = new UserEntity();
        user.setCompanyId(companyId);
        user.setRoleId(roleService.getByName(RoleName.OPERATOR).getId());
        user.setKeycloakUserId("kc-user-" + System.nanoTime());
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane-" + System.nanoTime() + "@test.ba");
        user.setAccountStatus(AccountStatus.ACTIVE);
        userId = userDAO.persist(user).getId();
        userDAO.flush();
    }

    @Test
    void findByIdReturnsUserInSameCompany() {
        UserResponse response = userService.findByIdAndCompanyId(userId, companyId);

        assertEquals(userId, response.getId());
        assertEquals(companyId, response.getCompanyId());
    }

    @Test
    void findByIdThrowsNotFoundForUserInOtherCompany() {
        assertThrows(
                ApiNotFoundException.class,
                () -> userService.findByIdAndCompanyId(userId, otherCompanyId));
    }

    @Test
    void findByIdThrowsNotFoundForNonExistentUser() {
        assertThrows(
                ApiNotFoundException.class,
                () -> userService.findByIdAndCompanyId(99999L, companyId));
    }

    @Test
    void findAllReturnsOnlyUsersFromSameCompany() {
        UserEntity otherUser = new UserEntity();
        otherUser.setCompanyId(otherCompanyId);
        otherUser.setRoleId(roleService.getByName(RoleName.OPERATOR).getId());
        otherUser.setKeycloakUserId("kc-other-" + System.nanoTime());
        otherUser.setFirstName("Stranger");
        otherUser.setLastName("User");
        otherUser.setEmail("stranger-" + System.nanoTime() + "@test.ba");
        otherUser.setAccountStatus(AccountStatus.ACTIVE);
        userDAO.persist(otherUser);
        userDAO.flush();

        UserFilterRequest filter = new UserFilterRequest();
        PagedResponse<UserResponse> result = userService.findAll(filter, companyId);

        assertTrue(result.getPayload().stream().allMatch(u -> u.getCompanyId().equals(companyId)));
    }

    @Test
    void findAllSearchByFirstNameReturnsMatchingUsers() {
        UserFilterRequest filter = new UserFilterRequest();
        filter.setSearch("Jane");

        PagedResponse<UserResponse> result = userService.findAll(filter, companyId);

        assertTrue(result.getPayload().stream().anyMatch(u -> u.getFirstName().equals("Jane")));
    }

    @Test
    void findAllFilterByAccountStatusReturnsOnlyMatchingUsers() {
        UserFilterRequest filter = new UserFilterRequest();
        filter.setAccountStatus(AccountStatus.ACTIVE);

        PagedResponse<UserResponse> result = userService.findAll(filter, companyId);

        assertTrue(result.getPayload().stream()
                .allMatch(u -> u.getAccountStatus().equals(AccountStatus.ACTIVE.name())));
    }

    @Test
    void updateChangesFirstAndLastName() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");

        UserResponse updated = userService.update(userId, request, companyId);

        assertEquals("Jane", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
    }

    @Test
    void updateThrowsNotFoundForUserInOtherCompany() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Hacker");
        request.setLastName("User");

        assertThrows(
                ApiNotFoundException.class,
                () -> userService.update(userId, request, otherCompanyId));
    }

    @Test
    void changeRoleUpdatesUserRole() {
        UserResponse updated = userService.changeRole(userId, RoleName.APPROVER, companyId);

        assertEquals(RoleName.APPROVER.name(), updated.getRole());
    }

    @Test
    void changeRoleThrowsNotFoundForUserInOtherCompany() {
        assertThrows(
                ApiNotFoundException.class,
                () -> userService.changeRole(userId, RoleName.MANAGER, otherCompanyId));
    }

    @Test
    void changeStatusDeactivatesUser() {
        UserResponse updated = userService.changeStatus(userId, AccountStatus.INACTIVE, companyId);

        assertEquals(AccountStatus.INACTIVE.name(), updated.getAccountStatus());
    }

    @Test
    void createUserSavesUserInCorrectCompany() {
        UserCreateApiRequest request = new UserCreateApiRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmail("jane-" + System.nanoTime() + "@test.ba");
        request.setRole(RoleName.OPERATOR);

        String keycloakUserId = "kc-new-" + System.nanoTime();
        UserResponse created = userService.createUser(companyId, request, keycloakUserId);

        assertEquals(companyId, created.getCompanyId());
        assertEquals("Jane", created.getFirstName());
        assertEquals("Doe", created.getLastName());
        assertEquals(RoleName.OPERATOR.name(), created.getRole());
        assertEquals(AccountStatus.PENDING_PASSWORD_CHANGE.name(), created.getAccountStatus());
    }

    @Test
    void createUserEmailIsStoredLowercase() {
        UserCreateApiRequest request = new UserCreateApiRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmail("JANE-" + System.nanoTime() + "@TEST.BA");
        request.setRole(RoleName.OPERATOR);

        UserResponse created = userService.createUser(companyId, request, "kc-lc-" + System.nanoTime());

        assertTrue(created.getEmail().equals(created.getEmail().toLowerCase()));
    }

    @Test
    void createUserThrowsValidationExceptionForDuplicateEmail() {
        String existingEmail = userDAO.findByPK(userId).getEmail();

        UserCreateApiRequest request = new UserCreateApiRequest();
        request.setFirstName("Duplicate");
        request.setLastName("User");
        request.setEmail(existingEmail);
        request.setRole(RoleName.OPERATOR);

        assertThrows(
                ApiValidationException.class,
                () -> userService.createUser(companyId, request, "kc-dup-" + System.nanoTime()));
    }

    @Test
    void createUserSameEmailAllowedInDifferentCompany() {
        String existingEmail = userDAO.findByPK(userId).getEmail();

        UserCreateApiRequest request = new UserCreateApiRequest();
        request.setFirstName("Other");
        request.setLastName("Company User");
        request.setEmail(existingEmail);
        request.setRole(RoleName.OPERATOR);

        UserResponse created = userService.createUser(otherCompanyId, request, "kc-other2-" + System.nanoTime());
        assertEquals(otherCompanyId, created.getCompanyId());
    }

    @Test
    void createUserThrowsValidationExceptionForDuplicateKeycloakId() {
        String existingKeycloakId = userDAO.findByPK(userId).getKeycloakUserId();

        UserCreateApiRequest request = new UserCreateApiRequest();
        request.setFirstName("Duplicate");
        request.setLastName("Keycloak");
        request.setEmail("unique-" + System.nanoTime() + "@test.ba");
        request.setRole(RoleName.OPERATOR);

        assertThrows(
                ApiValidationException.class,
                () -> userService.createUser(companyId, request, existingKeycloakId));
    }
}