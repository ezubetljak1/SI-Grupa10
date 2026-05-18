package ba.unsa.si.docflow.document;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.service.role.RoleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class DocumentTenantAccessIntegrationTest {

    private static final String KEYCLOAK_USER_A = "kc-doc-tenant-a";

    @Autowired private MockMvc mockMvc;
    @Autowired private CompanyDAO companyDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private DocumentDAO documentDAO;
    @Autowired private RoleService roleService;

    private Long companyAId;
    private Long companyBId;
    private Long documentBId;

    @BeforeEach
    void setUp() {
        companyAId = persistCompany("Company A", "company-a-doc@test.ba", "group-doc-a");
        companyBId = persistCompany("Company B", "company-b-doc@test.ba", "group-doc-b");

        UserEntity user = new UserEntity();
        user.setCompanyId(companyAId);
        user.setRoleId(roleService.getByName(RoleName.OPERATOR).getId());
        user.setKeycloakUserId(KEYCLOAK_USER_A);
        user.setFirstName("Doc");
        user.setLastName("User");
        user.setEmail("doc-tenant-a@test.ba");
        user.setAccountStatus(AccountStatus.ACTIVE);
        userDAO.persist(user);

        DocumentEntity document = new DocumentEntity();
        document.setCompanyId(companyBId);
        document.setCreatedBy(1L);
        document.setName("Foreign document");
        document.setFileType("application/pdf");
        document.setDocumentType(DocumentType.INVOICE);
        document.setStoragePath("company-" + companyBId + "/foreign.pdf");
        document.setUploadDate(LocalDateTime.now());
        document.setFileSize(100L);
        document.setDocumentStatus(DocumentStatus.UPLOADED);
        documentBId = documentDAO.persist(document).getId();
        userDAO.flush();
    }

    @Test
    void userCannotReadDocumentFromAnotherCompany() throws Exception {
        mockMvc.perform(get("/api/documents/{id}", documentBId).with(jwtForCurrentUser()))
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
                Jwt.withTokenValue("doc-tenant-token")
                        .header("alg", "none")
                        .subject(KEYCLOAK_USER_A)
                        .claim("email", "doc-tenant-a@test.ba")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600))
                        .build();

        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt)
                .authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
