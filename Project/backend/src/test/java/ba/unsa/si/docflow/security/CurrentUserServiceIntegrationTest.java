package ba.unsa.si.docflow.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.service.role.RoleService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CurrentUserServiceIntegrationTest {

    private static final String KEYCLOAK_USER_ID = "kc-current-user-test";

    @Autowired private CurrentUserService currentUserService;

    @Autowired private CompanyDAO companyDAO;

    @Autowired private UserDAO userDAO;

    @Autowired private RoleService roleService;

    @BeforeEach
    void setUp() {
        CompanyEntity company = new CompanyEntity();
        company.setName("Current User Test Company");
        company.setAddress("Address");
        company.setEmail("current-user-company-" + System.nanoTime() + "@test.ba");
        company.setRegistrationDate(LocalDateTime.now());
        company.setStatus(CompanyStatus.ACTIVE);
        company.setKeycloakGroupId("group-test");

        Long companyId = companyDAO.persist(company).getId();

        UserEntity user = new UserEntity();
        user.setCompanyId(companyId);
        user.setRoleId(roleService.getAdminRole().getId());
        user.setKeycloakUserId(KEYCLOAK_USER_ID);
        user.setFirstName("Emina");
        user.setLastName("Test");
        user.setEmail("current-user-" + System.nanoTime() + "@test.ba");
        user.setAccountStatus(AccountStatus.ACTIVE);

        userDAO.persist(user);
        userDAO.flush();

        authenticate(buildJwt(KEYCLOAK_USER_ID, user.getEmail()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserMapsLocalUserCompanyAndRole() {
        CurrentUser currentUser = currentUserService.getCurrentUser();

        assertEquals(KEYCLOAK_USER_ID, currentUser.keycloakUserId());
        assertEquals(RoleName.ADMIN.name(), currentUser.role());
        assertEquals(userDAO.findByKeycloakUserId(KEYCLOAK_USER_ID).getCompanyId(), currentUser.companyId());
        assertEquals(userDAO.findByKeycloakUserId(KEYCLOAK_USER_ID).getId(), currentUser.userId());
    }

    @Test
    void getCurrentUserRejectsUnknownKeycloakUser() {
        authenticate(buildJwt("unknown-kc-user", "x@test.ba"));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class, () -> currentUserService.getCurrentUser());

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void getCurrentUserRejectsInactiveAccount() {
        UserEntity user = userDAO.findByKeycloakUserId(KEYCLOAK_USER_ID);
        user.setAccountStatus(AccountStatus.INACTIVE);
        userDAO.merge(user);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class, () -> currentUserService.getCurrentUser());

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void pendingPasswordChangeAccountIsAllowed() {
        UserEntity user = userDAO.findByKeycloakUserId(KEYCLOAK_USER_ID);
        user.setAccountStatus(AccountStatus.PENDING_PASSWORD_CHANGE);
        userDAO.merge(user);

        CurrentUser currentUser = currentUserService.getCurrentUser();

        assertEquals(user.getId(), currentUser.userId());
    }

    private void authenticate(Jwt jwt) {
        SecurityContextHolder.getContext()
                .setAuthentication(
                        new JwtAuthenticationToken(
                                jwt, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    private Jwt buildJwt(String subject, String email) {
        return Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject(subject)
                .claim("email", email)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
