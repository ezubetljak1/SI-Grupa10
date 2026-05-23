package ba.unsa.si.docflow.workflow;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.*;
import ba.unsa.si.docflow.service.audit.AuditLogService;
import ba.unsa.si.docflow.service.role.RoleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuditLogSecurityIntegrationTest {

    private static final String ADMIN_KC = "audit-admin";
    private static final String MANAGER_KC = "audit-manager";
    private static final String OPERATOR_KC = "audit-operator";
    private static final String APPROVER_KC = "audit-approver";
    private static final String OTHER_COMPANY_ADMIN_KC = "audit-other-admin";

    @Autowired private MockMvc mockMvc;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private CompanyDAO companyDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private DocumentDAO documentDAO;
    @Autowired private RoleService roleService;
    @Autowired private AuditLogService auditLogService;
    @Autowired private PlatformTransactionManager transactionManager;

    private Long documentId;
    private DocumentEntity document;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        jdbcTemplate.execute("DELETE FROM audit_log");
        jdbcTemplate.execute("DELETE FROM notification");
        jdbcTemplate.execute("DELETE FROM workflow_task");
        jdbcTemplate.execute("DELETE FROM status_history");
        jdbcTemplate.execute("DELETE FROM document_comment");
        jdbcTemplate.execute("DELETE FROM extraction_field");
        jdbcTemplate.execute("DELETE FROM extraction");
        jdbcTemplate.execute("DELETE FROM document");
        jdbcTemplate.execute("DELETE FROM app_user");
        jdbcTemplate.execute("DELETE FROM company");

        setupData();
    }

    @Test
    void adminCanGetAuditLog() throws Exception {
        authenticateAs(ADMIN_KC);

        mockMvc.perform(get("/api/documents/{id}/audit-log", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo("SUCCESS")));
    }

    @Test
    void managerCanGetAuditLog() throws Exception {
        authenticateAs(MANAGER_KC);

        mockMvc.perform(get("/api/documents/{id}/audit-log", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo("SUCCESS")));
    }

    @Test
    void operatorCannotGetAuditLog() throws Exception {
        authenticateAs(OPERATOR_KC);

        mockMvc.perform(get("/api/documents/{id}/audit-log", documentId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    @Test
    void approverCannotGetAuditLog() throws Exception {
        authenticateAs(APPROVER_KC);

        mockMvc.perform(get("/api/documents/{id}/audit-log", documentId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    @Test
    void userFromOtherCompanyCannotGetAuditLog() throws Exception {
        authenticateAs(OTHER_COMPANY_ADMIN_KC);

        mockMvc.perform(get("/api/documents/{id}/audit-log", documentId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    @Test
    void auditLogServiceLogCreatesRecordReturnedByEndpoint() throws Exception {
        authenticateAs(ADMIN_KC);

        Long adminUserId =
                jdbcTemplate.queryForObject(
                        "SELECT id FROM app_user WHERE keycloak_user_id = ?",
                        Long.class,
                        ADMIN_KC);

        auditLogService.log(
                document,
                adminUserId,
                AuditAction.FIELD_UPDATED,
                "{\"fieldId\":1,\"fieldName\":\"invoice_id\"}"
        );

        mockMvc.perform(get("/api/documents/{id}/audit-log", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", hasSize(1)))
                .andExpect(jsonPath("$.payload[0].action", equalTo("FIELD_UPDATED")))
                .andExpect(jsonPath("$.payload[0].details", containsString("invoice_id")))
                .andExpect(jsonPath("$.payload[0].userId", equalTo(adminUserId.intValue())))
                .andExpect(jsonPath("$.payload[0].userFullName", equalTo("Audit Admin")));
    }

    private void setupData() {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(status -> {
                    CompanyEntity company = createCompany("Audit Test Company", "audit@test.ba");
                    CompanyEntity otherCompany = createCompany("Other Audit Company", "other-audit@test.ba");

                    Long companyId = company.getId();
                    Long otherCompanyId = otherCompany.getId();

                    createUser(companyId, ADMIN_KC, RoleName.ADMIN, "Audit", "Admin", "audit.admin@test.ba");
                    createUser(companyId, MANAGER_KC, RoleName.MANAGER, "Audit", "Manager", "audit.manager@test.ba");
                    createUser(companyId, OPERATOR_KC, RoleName.OPERATOR, "Audit", "Operator", "audit.operator@test.ba");
                    createUser(companyId, APPROVER_KC, RoleName.APPROVER, "Audit", "Approver", "audit.approver@test.ba");
                    createUser(otherCompanyId, OTHER_COMPANY_ADMIN_KC, RoleName.ADMIN, "Other", "Admin", "other.admin@test.ba");

                    DocumentEntity doc = new DocumentEntity();
                    doc.setCompanyId(companyId);
                    doc.setCreatedBy(1L);
                    doc.setName("Audit log test document");
                    doc.setFileType("application/pdf");
                    doc.setDocumentType(DocumentType.INVOICE);
                    doc.setStoragePath("company-" + companyId + "/audit-log-test.pdf");
                    doc.setUploadDate(LocalDateTime.now());
                    doc.setFileSize(123L);
                    doc.setDocumentStatus(DocumentStatus.EXTRACTED);

                    document = documentDAO.persist(doc);
                    documentId = document.getId();
                });
    }

    private CompanyEntity createCompany(String name, String email) {
        CompanyEntity company = new CompanyEntity();
        company.setName(name);
        company.setAddress("Test address");
        company.setEmail(email);
        company.setRegistrationDate(LocalDateTime.now());
        company.setStatus(CompanyStatus.ACTIVE);
        company.setKeycloakGroupId("group-" + email);

        return companyDAO.persist(company);
    }

    private void createUser(
            Long companyId,
            String keycloakUserId,
            RoleName role,
            String firstName,
            String lastName,
            String email
    ) {
        UserEntity user = new UserEntity();
        user.setCompanyId(companyId);
        user.setRoleId(roleService.getByName(role).getId());
        user.setKeycloakUserId(keycloakUserId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setAccountStatus(AccountStatus.ACTIVE);

        userDAO.persist(user);
    }

    private void authenticateAs(String keycloakUserId) {
        Jwt jwt =
                Jwt.withTokenValue("audit-log-test-token")
                        .header("alg", "none")
                        .subject(keycloakUserId)
                        .claim("email", keycloakUserId + "@test.ba")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600))
                        .build();

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new JwtAuthenticationToken(
                                jwt,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        )
                );
    }
}
