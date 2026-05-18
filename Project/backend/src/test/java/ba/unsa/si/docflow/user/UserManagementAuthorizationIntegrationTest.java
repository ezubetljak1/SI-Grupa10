package ba.unsa.si.docflow.user;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ba.unsa.si.docflow.config.KeycloakTestConfiguration;
import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.user.UserCreateApiRequest;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.service.role.RoleService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(KeycloakTestConfiguration.class)
@Transactional
class UserManagementAuthorizationIntegrationTest {

    private static final String KEYCLOAK_ADMIN = "kc-user-mgmt-admin";
    private static final String KEYCLOAK_OPERATOR = "kc-user-mgmt-operator";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CompanyDAO companyDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private RoleService roleService;

    private Long companyId;

    @BeforeEach
    void setUp() {
        CompanyEntity company = new CompanyEntity();
        company.setName("User Mgmt Co");
        company.setAddress("Address");
        company.setEmail("user-mgmt-" + System.nanoTime() + "@test.ba");
        company.setRegistrationDate(LocalDateTime.now());
        company.setStatus(CompanyStatus.ACTIVE);
        company.setKeycloakGroupId("group-user-mgmt");
        companyId = companyDAO.persist(company).getId();

        persistUser(KEYCLOAK_ADMIN, RoleName.ADMIN, "admin@test.ba");
        persistUser(KEYCLOAK_OPERATOR, RoleName.OPERATOR, "operator@test.ba");
        userDAO.flush();
    }

    @Test
    void adminCanListCompanyUsers() throws Exception {
        mockMvc.perform(get("/api/company/users").with(jwtFor(KEYCLOAK_ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo("OK")));
    }

    @Test
    void nonAdminCannotListCompanyUsers() throws Exception {
        mockMvc.perform(get("/api/company/users").with(jwtFor(KEYCLOAK_OPERATOR)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    @Test
    void adminCanCreateCompanyUser() throws Exception {
        UserCreateApiRequest request = new UserCreateApiRequest();
        request.setFirstName("Aida");
        request.setLastName("Hadzic");
        request.setEmail("aida-" + System.nanoTime() + "@test.ba");
        request.setRole(RoleName.OPERATOR);

        mockMvc.perform(
                        post("/api/company/users")
                                .with(jwtFor(KEYCLOAK_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.role", equalTo("OPERATOR")));
    }

    private void persistUser(String keycloakUserId, RoleName role, String email) {
        UserEntity user = new UserEntity();
        user.setCompanyId(companyId);
        user.setRoleId(roleService.getByName(role).getId());
        user.setKeycloakUserId(keycloakUserId);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userDAO.persist(user);
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtFor(
            String keycloakUserId) {
        Jwt jwt =
                Jwt.withTokenValue("user-mgmt-token")
                        .header("alg", "none")
                        .subject(keycloakUserId)
                        .claim("email", keycloakUserId + "@test.ba")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600))
                        .build();

        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt)
                .authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
