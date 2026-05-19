package ba.unsa.si.docflow.user;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ba.unsa.si.docflow.config.KeycloakTestConfiguration;
import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.user.UserCreateApiRequest;
import ba.unsa.si.docflow.entity.RoleEntity;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.service.keycloak.KeycloakAdminService;
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
    @Autowired private KeycloakAdminService keycloakAdminService;

    private Long companyId;
    private Long foreignCompanyId;
    private Long adminUserId;
    private Long operatorUserId;
    private Long foreignUserId;

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

        CompanyEntity foreignCompany = new CompanyEntity();
        foreignCompany.setName("Foreign User Mgmt Co");
        foreignCompany.setAddress("Foreign address");
        foreignCompany.setEmail("foreign-user-mgmt-" + System.nanoTime() + "@test.ba");
        foreignCompany.setRegistrationDate(LocalDateTime.now());
        foreignCompany.setStatus(CompanyStatus.ACTIVE);
        foreignCompany.setKeycloakGroupId("group-user-mgmt-foreign");
        foreignCompanyId = companyDAO.persist(foreignCompany).getId();

        adminUserId = persistUser(companyId, KEYCLOAK_ADMIN, RoleName.ADMIN, "admin@test.ba");
        operatorUserId =
                persistUser(companyId, KEYCLOAK_OPERATOR, RoleName.OPERATOR, "operator@test.ba");
        foreignUserId =
                persistUser(
                        foreignCompanyId,
                        "kc-user-mgmt-foreign",
                        RoleName.OPERATOR,
                        "foreign@test.ba");
        userDAO.flush();
        clearInvocations(keycloakAdminService);
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
                .andExpect(jsonPath("$.payload.role", equalTo("OPERATOR")))
                .andExpect(jsonPath("$.payload.temporaryPassword", equalTo("TempPass123!")));
    }

    @Test
    void authenticatedUserCanReadOwnProfile() throws Exception {
        mockMvc.perform(get("/api/company/users/me").with(jwtFor(KEYCLOAK_OPERATOR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.id", equalTo(operatorUserId.intValue())))
                .andExpect(jsonPath("$.payload.companyId", equalTo(companyId.intValue())))
                .andExpect(jsonPath("$.payload.role", equalTo("OPERATOR")))
                .andExpect(jsonPath("$.payload.temporaryPassword").doesNotExist());
    }

    @Test
    void adminCanReadUserFromOwnCompany() throws Exception {
        mockMvc.perform(get("/api/company/users/{id}", operatorUserId).with(jwtFor(KEYCLOAK_ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.id", equalTo(operatorUserId.intValue())))
                .andExpect(jsonPath("$.payload.companyId", equalTo(companyId.intValue())))
                .andExpect(jsonPath("$.payload.role", equalTo("OPERATOR")));
    }

    @Test
    void adminCannotReadUserFromAnotherCompany() throws Exception {
        mockMvc.perform(get("/api/company/users/{id}", foreignUserId).with(jwtFor(KEYCLOAK_ADMIN)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void adminCanUpdateUserInOwnCompany() throws Exception {
        mockMvc.perform(
                        patch("/api/company/users/{id}", operatorUserId)
                                .with(jwtFor(KEYCLOAK_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "firstName": "Updated",
                                          "lastName": "Operator"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.firstName", equalTo("Updated")))
                .andExpect(jsonPath("$.payload.lastName", equalTo("Operator")));
    }

    @Test
    void adminCannotUpdateUserFromAnotherCompany() throws Exception {
        mockMvc.perform(
                        patch("/api/company/users/{id}", foreignUserId)
                                .with(jwtFor(KEYCLOAK_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "firstName": "Foreign",
                                          "lastName": "User"
                                        }
                                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void adminCanChangeRoleForOwnCompanyUser() throws Exception {
        mockMvc.perform(
                        patch("/api/company/users/{id}/role", operatorUserId)
                                .with(jwtFor(KEYCLOAK_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "role": "APPROVER"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.role", equalTo("APPROVER")));
    }

    @Test
    void adminCannotChangeRoleForUserFromAnotherCompany() throws Exception {
        mockMvc.perform(
                        patch("/api/company/users/{id}/role", foreignUserId)
                                .with(jwtFor(KEYCLOAK_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "role": "MANAGER"
                                        }
                                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void adminCanChangeStatusForOwnCompanyUserAndUpdatesKeycloak() throws Exception {
        mockMvc.perform(
                        patch("/api/company/users/{id}/status", operatorUserId)
                                .with(jwtFor(KEYCLOAK_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "accountStatus": "INACTIVE"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.accountStatus", equalTo("INACTIVE")));

        verify(keycloakAdminService).setUserEnabled(KEYCLOAK_OPERATOR, false);
    }

    @Test
    void adminCannotChangeStatusForUserFromAnotherCompany() throws Exception {
        mockMvc.perform(
                        patch("/api/company/users/{id}/status", foreignUserId)
                                .with(jwtFor(KEYCLOAK_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "accountStatus": "INACTIVE"
                                        }
                                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void adminCanInitiateResetPasswordForOwnCompanyUser() throws Exception {
        mockMvc.perform(
                        post("/api/company/users/{id}/reset-password", operatorUserId)
                                .with(jwtFor(KEYCLOAK_ADMIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", equalTo("ResetTemp123!")));

        verify(keycloakAdminService).resetUserPassword(KEYCLOAK_OPERATOR);
    }

    @Test
    void adminCannotInitiateResetPasswordForUserFromAnotherCompany() throws Exception {
        mockMvc.perform(
                        post("/api/company/users/{id}/reset-password", foreignUserId)
                                .with(jwtFor(KEYCLOAK_ADMIN)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void nonAdminCannotChangeRole() throws Exception {
        mockMvc.perform(
                        patch("/api/company/users/{id}/role", adminUserId)
                                .with(jwtFor(KEYCLOAK_OPERATOR))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "role": "MANAGER"
                                        }
                                        """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    private Long persistUser(Long tenantCompanyId, String keycloakUserId, RoleName role, String email) {
        UserEntity user = new UserEntity();
        RoleEntity roleEntity = roleService.getByName(role);
        user.setCompanyId(tenantCompanyId);
        user.setRoleId(roleEntity.getId());
        user.setKeycloakUserId(keycloakUserId);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setAccountStatus(AccountStatus.ACTIVE);
        return userDAO.persist(user).getId();
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
