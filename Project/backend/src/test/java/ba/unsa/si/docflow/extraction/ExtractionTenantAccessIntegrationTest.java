package ba.unsa.si.docflow.extraction;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.dao.ExtractionDAO;
import ba.unsa.si.docflow.dao.ExtractionFieldDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.ExtractionEntity;
import ba.unsa.si.docflow.entity.ExtractionFieldEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.service.ocr.OcrProvider;
import ba.unsa.si.docflow.service.role.RoleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExtractionTenantAccessIntegrationTest {

    private static final byte[] PDF_CONTENT = "%PDF-1.4 tenant access test".getBytes();
    private static final String KEYCLOAK_OPERATOR_A = "kc-extraction-tenant-operator-a";
    private static final String KEYCLOAK_MANAGER_A = "kc-extraction-tenant-manager-a";
    private static final String KEYCLOAK_APPROVER_A = "kc-extraction-tenant-approver-a";

    @Autowired private MockMvc mockMvc;
    @Autowired private CompanyDAO companyDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private DocumentDAO documentDAO;
    @Autowired private ExtractionDAO extractionDAO;
    @Autowired private ExtractionFieldDAO extractionFieldDAO;
    @Autowired private RoleService roleService;

    @MockitoBean private OcrProvider ocrProvider;

    private Long companyAId;
    private Long companyBId;
    private Long foreignDocumentId;
    private Long foreignExtractionId;
    private Long foreignFieldId;
    private Long ownDocumentId;
    private Long ownExtractionId;
    private Long ownFieldId;

    @BeforeEach
    void setUp() {
        companyAId = persistCompany("Company A", "company-a-extraction@test.ba", "group-ext-a");
        companyBId = persistCompany("Company B", "company-b-extraction@test.ba", "group-ext-b");

        persistUser(companyAId, KEYCLOAK_OPERATOR_A, RoleName.OPERATOR, "operator-a@test.ba");
        persistUser(companyAId, KEYCLOAK_MANAGER_A, RoleName.MANAGER, "manager-a@test.ba");
        persistUser(companyAId, KEYCLOAK_APPROVER_A, RoleName.APPROVER, "approver-a@test.ba");
        persistUser(companyBId, "kc-extraction-tenant-operator-b", RoleName.OPERATOR, "operator-b@test.ba");

        foreignDocumentId = persistDocument(companyBId, "Foreign extraction doc");
        foreignExtractionId = persistExtraction(foreignDocumentId);
        foreignFieldId = persistField(foreignExtractionId, "total_amount", "117.00");

        ownDocumentId = persistDocument(companyAId, "Own extraction doc");
        ownExtractionId = persistExtraction(ownDocumentId);
        ownFieldId = persistField(ownExtractionId, "total_amount", "117.00");

        extractionFieldDAO.flush();
        extractionDAO.flush();
        documentDAO.flush();
        userDAO.flush();
    }

    @Test
    void operatorCannotStartExtractionForDocumentFromAnotherCompany() throws Exception {
        mockMvc.perform(
                        post("/api/documents/{documentId}/extraction", foreignDocumentId)
                                .with(jwtFor(KEYCLOAK_OPERATOR_A)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void operatorCannotConfirmExtractionForDocumentFromAnotherCompany() throws Exception {
        mockMvc.perform(
                        post("/api/documents/{documentId}/extraction/confirm", foreignDocumentId)
                                .with(jwtFor(KEYCLOAK_OPERATOR_A)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void operatorCannotReadExtractionFieldsFromAnotherCompany() throws Exception {
        mockMvc.perform(
                        get("/api/extractions/{extractionId}/fields", foreignExtractionId)
                                .with(jwtFor(KEYCLOAK_OPERATOR_A)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void operatorCannotEditExtractionFieldFromAnotherCompany() throws Exception {
        mockMvc.perform(
                        patch("/api/extractions/{extractionId}/fields/{fieldId}", foreignExtractionId, foreignFieldId)
                                .with(jwtFor(KEYCLOAK_OPERATOR_A))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "value": "125.50"
                                        }
                                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void managerCannotStartExtractionForOwnCompanyDocument() throws Exception {
        mockMvc.perform(
                        post("/api/documents/{documentId}/extraction", ownDocumentId)
                                .with(jwtFor(KEYCLOAK_MANAGER_A)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    @Test
    void approverCannotEditExtractionFieldForOwnCompany() throws Exception {
        mockMvc.perform(
                        patch("/api/extractions/{extractionId}/fields/{fieldId}", ownExtractionId, ownFieldId)
                                .with(jwtFor(KEYCLOAK_APPROVER_A))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "value": "125.50"
                                        }
                                        """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    @Test
    void managerCannotUploadDocument() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "tenant-test.pdf", "application/pdf", PDF_CONTENT);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(file)
                                .param("documentType", "INVOICE")
                                .param("name", "Manager upload should fail")
                                .with(jwtFor(KEYCLOAK_MANAGER_A)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
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

    private Long persistUser(Long companyId, String keycloakUserId, RoleName role, String email) {
        UserEntity user = new UserEntity();
        user.setCompanyId(companyId);
        user.setRoleId(roleService.getByName(role).getId());
        user.setKeycloakUserId(keycloakUserId);
        user.setFirstName("Tenant");
        user.setLastName("User");
        user.setEmail(email);
        user.setAccountStatus(AccountStatus.ACTIVE);
        return userDAO.persist(user).getId();
    }

    private Long persistDocument(Long companyId, String name) {
        DocumentEntity document = new DocumentEntity();
        document.setCompanyId(companyId);
        document.setCreatedBy(1L);
        document.setName(name);
        document.setFileType("application/pdf");
        document.setDocumentType(DocumentType.INVOICE);
        document.setStoragePath("company-" + companyId + "/" + name.replace(" ", "-").toLowerCase() + ".pdf");
        document.setUploadDate(LocalDateTime.now());
        document.setFileSize(100L);
        document.setDocumentStatus(DocumentStatus.UPLOADED);
        return documentDAO.persist(document).getId();
    }

    private Long persistExtraction(Long documentId) {
        DocumentEntity document = documentDAO.findByPK(documentId);
        ExtractionEntity extraction = new ExtractionEntity();
        extraction.setDocument(document);
        extraction.setRawJson("{\"status\":\"ok\"}");
        extraction.setExtractionTime(LocalDateTime.now());
        return extractionDAO.persist(extraction).getId();
    }

    private Long persistField(Long extractionId, String fieldName, String value) {
        ExtractionEntity extraction = extractionDAO.findByPK(extractionId);
        ExtractionFieldEntity field = new ExtractionFieldEntity();
        field.setExtraction(extraction);
        field.setFieldName(fieldName);
        field.setValue(value);
        field.setConfidence(new BigDecimal("0.95"));
        field.setCorrected(false);
        field.setPlaceholder(false);
        return extractionFieldDAO.persist(field).getId();
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtFor(String keycloakUserId) {
        Jwt jwt =
                Jwt.withTokenValue("extraction-tenant-token")
                        .header("alg", "none")
                        .subject(keycloakUserId)
                        .claim("email", keycloakUserId + "@test.ba")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600))
                        .build();

        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
