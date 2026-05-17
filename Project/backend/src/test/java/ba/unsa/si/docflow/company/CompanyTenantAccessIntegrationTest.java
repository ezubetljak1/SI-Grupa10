package ba.unsa.si.docflow.company;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.company.CompanyUpdateRequest;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.service.role.RoleService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
@Transactional
class CompanyTenantAccessIntegrationTest {

    private static final String KEYCLOAK_USER_A = "kc-tenant-user-a";

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private CompanyDAO companyDAO;

    @Autowired private UserDAO userDAO;

    @Autowired private RoleService roleService;

    private Long companyAId;
    private Long companyBId;

    @BeforeEach
    void setUp() {
        companyAId = persistCompany("Company A", "company-a@test.ba", "group-a");
        companyBId = persistCompany("Company B", "company-b@test.ba", "group-b");

        UserEntity user = new UserEntity();
        user.setCompanyId(companyAId);
        user.setRoleId(roleService.getAdminRole().getId());
        user.setKeycloakUserId(KEYCLOAK_USER_A);
        user.setFirstName("Tenant");
        user.setLastName("User");
        user.setEmail("tenant-user-a@test.ba");
        user.setAccountStatus(AccountStatus.ACTIVE);
        userDAO.persist(user);
        userDAO.flush();
    }

    @Test
    void userCanReadOwnCompany() throws Exception {
        mockMvc.perform(
                        get("/api/companies/{id}", companyAId)
                                .with(jwtForCurrentUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.id", equalTo(companyAId.intValue())));
    }

    @Test
    void userCannotReadAnotherCompany() throws Exception {
        mockMvc.perform(
                        get("/api/companies/{id}", companyBId)
                                .with(jwtForCurrentUser()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void userCanUpdateOwnCompany() throws Exception {
        CompanyUpdateRequest request = new CompanyUpdateRequest();
        request.setName("Company A Updated");

        mockMvc.perform(
                        patch("/api/companies/{id}", companyAId)
                                .with(jwtForCurrentUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.name", equalTo("Company A Updated")));
    }

    @Test
    void userCannotUpdateAnotherCompany() throws Exception {
        CompanyUpdateRequest request = new CompanyUpdateRequest();
        request.setName("Forbidden Update");

        mockMvc.perform(
                        patch("/api/companies/{id}", companyBId)
                                .with(jwtForCurrentUser())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    private Long persistCompany(String name, String email, String groupId) {
        CompanyEntity company = new CompanyEntity();
        company.setName(name);
        company.setAddress("Address");
        company.setEmail(email);
        company.setRegistrationDate(LocalDateTime.now());
        company.setStatus(CompanyStatus.ACTIVE);
        company.setKeycloakGroupId(groupId);
        return companyDAO.persist(company).getId();
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtForCurrentUser() {
        Jwt jwt =
                Jwt.withTokenValue("tenant-test-token")
                        .header("alg", "none")
                        .subject(KEYCLOAK_USER_A)
                        .claim("email", "tenant-user-a@test.ba")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600))
                        .build();

        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt)
                .authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
