package ba.unsa.si.docflow.document;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.service.role.RoleService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class DocumentUploadIntegrationTest {

    private static final byte[] PDF_CONTENT =
            "%PDF-1.4 test document content".getBytes(StandardCharsets.UTF_8);

    private static final Path UPLOAD_ROOT = createTempDirectory();
    private static final String KEYCLOAK_USER_1 = "kc-doc-upload-user-1";
    private static final String KEYCLOAK_USER_2 = "kc-doc-upload-user-2";

    @Autowired private MockMvc mockMvc;

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private CompanyDAO companyDAO;

    @Autowired private UserDAO userDAO;

    @Autowired private RoleService roleService;

    @Autowired private PlatformTransactionManager transactionManager;

    private Long testCompany1Id;
    private Long testCompany2Id;
    private Long testUser1Id;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("docflow.storage.root-dir", () -> UPLOAD_ROOT.toString());
    }

    @BeforeEach
    void setUp() throws IOException {
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

        deleteChildren(UPLOAD_ROOT);
        Files.createDirectories(UPLOAD_ROOT);
        setupTestTenants();
        authenticateAs(KEYCLOAK_USER_1);
    }

    @AfterAll
    static void tearDown() throws IOException {
        deleteRecursively(UPLOAD_ROOT);
    }

    @Test
    void uploadValidPdfThenStoresMetadataAndFile() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "invoice.pdf", "application/pdf", PDF_CONTENT);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(file)
                                .param("documentType", "INVOICE")
                                .param("name", "Test invoice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.name").value("Test invoice"))
                .andExpect(jsonPath("$.payload.companyId").value(testCompany1Id.intValue()))
                .andExpect(jsonPath("$.payload.createdBy").value(testUser1Id.intValue()))
                .andExpect(jsonPath("$.payload.documentType").value("INVOICE"))
                .andExpect(jsonPath("$.payload.documentStatus").value("UPLOADED"))
                .andExpect(jsonPath("$.payload.fileType").value("application/pdf"));

        Map<String, Object> document =
                jdbcTemplate.queryForMap("SELECT * FROM document WHERE name = ?", "Test invoice");

        assertEquals("Test invoice", document.get("name"));
        assertEquals("application/pdf", document.get("file_type"));
        assertEquals("INVOICE", document.get("document_type"));
        assertEquals("UPLOADED", document.get("document_status"));

        String storagePath = (String) document.get("storage_path");
        Path storedFile = UPLOAD_ROOT.resolve(storagePath);

        assertTrue(Files.exists(storedFile));
        assertTrue(storagePath.startsWith("company-" + testCompany1Id + "/"));
    }

    @Test
    void uploadWithoutNameThenUsesOriginalFileName() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file", "original-invoice.pdf", "application/pdf", PDF_CONTENT);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(file)
                                .param("documentType", "INVOICE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.name").value("original-invoice.pdf"));

        Integer count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document WHERE name = ?",
                        Integer.class,
                        "original-invoice.pdf");

        assertEquals(1, count);
    }

    @Test
    void uploadDuplicateNameInSameCompanyThenReturnsValidationError() throws Exception {
        uploadPdf("Duplicate invoice");

        MockMultipartFile duplicateFile =
                new MockMultipartFile("file", "duplicate.pdf", "application/pdf", PDF_CONTENT);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(duplicateFile)
                                .param("documentType", "INVOICE")
                                .param("name", "Duplicate invoice"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_NAME_EXISTS"));

        Integer count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document WHERE name = ? AND company_id = ?",
                        Integer.class,
                        "Duplicate invoice",
                        testCompany1Id);

        assertEquals(1, count);
    }

    @Test
    void uploadSameNameInDifferentCompanyThenSucceeds() throws Exception {
        uploadPdf("Shared invoice name");

        authenticateAs(KEYCLOAK_USER_2);

        MockMultipartFile file =
                new MockMultipartFile("file", "shared.pdf", "application/pdf", PDF_CONTENT);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(file)
                                .param("documentType", "INVOICE")
                                .param("name", "Shared invoice name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.name").value("Shared invoice name"))
                .andExpect(jsonPath("$.payload.companyId").value(testCompany2Id.intValue()));

        authenticateAs(KEYCLOAK_USER_1);

        Integer count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document WHERE name = ?",
                        Integer.class,
                        "Shared invoice name");

        assertEquals(2, count);
    }

    @Test
    void uploadUnsupportedFileTypeThenReturnsValidationError() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "notes.txt",
                        "text/plain",
                        "hello".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(file)
                                .param("documentType", "INVOICE")
                                .param("name", "Invalid text file"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_FILE_TYPE_UNSUPPORTED"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM document", Integer.class);

        assertEquals(0, count);
    }

    @Test
    void uploadInvalidDocumentTypeThenReturnsValidationError() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "invoice.pdf", "application/pdf", PDF_CONTENT);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(file)
                                .param("documentType", "CONTRACT")
                                .param("name", "Invalid document type"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_TYPE_INVALID"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM document", Integer.class);

        assertEquals(0, count);
    }

    @Test
    void uploadTooLargeFileThenReturnsValidationError() throws Exception {
        byte[] largeContent = new byte[2048];

        MockMultipartFile file =
                new MockMultipartFile("file", "large.pdf", "application/pdf", largeContent);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(file)
                                .param("documentType", "INVOICE")
                                .param("name", "Large file"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_FILE_SIZE_EXCEEDED"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM document", Integer.class);

        assertEquals(0, count);
    }

    @Test
    void downloadUploadedFileThenReturnsStoredContent() throws Exception {
        Long documentId = uploadPdf("Download test invoice");

        mockMvc.perform(get("/api/documents/{id}/file", documentId))
                .andExpect(status().isOk())
                .andExpect(
                        header().string(
                                        HttpHeaders.CONTENT_DISPOSITION,
                                        containsString("attachment")))
                .andExpect(content().bytes(PDF_CONTENT));
    }

    @Test
    void deleteUploadedDocumentThenRemovesMetadataAndFile() throws Exception {
        Long documentId = uploadPdf("Delete test invoice");

        String storagePath =
                jdbcTemplate.queryForObject(
                        "SELECT storage_path FROM document WHERE id = ?", String.class, documentId);

        Path storedFile = UPLOAD_ROOT.resolve(storagePath);
        assertTrue(Files.exists(storedFile));

        mockMvc.perform(delete("/api/documents/{id}", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"));

        Integer count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document WHERE id = ?", Integer.class, documentId);

        assertEquals(0, count);
        assertFalse(Files.exists(storedFile));
    }

    @Test
    void findUploadedDocumentByIdThenReturnsDocumentPayload() throws Exception {
        Long documentId = uploadPdf("Details test invoice");

        mockMvc.perform(get("/api/documents/{id}", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.id").value(documentId))
                .andExpect(jsonPath("$.payload.name").value("Details test invoice"))
                .andExpect(jsonPath("$.payload.companyId").value(testCompany1Id.intValue()))
                .andExpect(jsonPath("$.payload.createdBy").value(testUser1Id.intValue()))
                .andExpect(jsonPath("$.payload.documentType").value("INVOICE"))
                .andExpect(jsonPath("$.payload.documentStatus").value("UPLOADED"))
                .andExpect(jsonPath("$.payload.fileType").value("application/pdf"));
    }

    @Test
    void findMissingDocumentThenReturnsNotFoundResponse() throws Exception {
        mockMvc.perform(get("/api/documents/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(
                        jsonPath("$.payload").value("Document with the given id does not exist."));
    }

    @Test
    void previewUploadedFileThenReturnsInlineContent() throws Exception {
        Long documentId = uploadPdf("Preview test invoice");

        mockMvc.perform(get("/api/documents/{id}/preview", documentId))
                .andExpect(status().isOk())
                .andExpect(
                        header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("inline")))
                .andExpect(
                        header().string(
                                        HttpHeaders.CONTENT_DISPOSITION,
                                        containsString("Preview test invoice.pdf")))
                .andExpect(content().bytes(PDF_CONTENT));
    }

    @Test
    void uploadEmptyFileThenReturnsValidationError() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(file)
                                .param("documentType", "INVOICE")
                                .param("name", "Empty file"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_FILE_EMPTY"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM document", Integer.class);

        assertEquals(0, count);
    }

    @Test
    void uploadPdfExtensionWithInvalidContentTypeThenReturnsValidationError() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "invoice.pdf", "text/plain", PDF_CONTENT);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(file)
                                .param("documentType", "INVOICE")
                                .param("name", "Wrong content type"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_FILE_CONTENT_TYPE_UNSUPPORTED"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM document", Integer.class);

        assertEquals(0, count);
    }

    @Test
    void createDocumentWithInvalidTypeThenReturnsValidationError() throws Exception {
        Map<String, Object> request =
                Map.of(
                        "companyId", testCompany1Id,
                        "createdByUserId", testUser1Id,
                        "name", "Manual invalid type",
                        "fileType", "application/pdf",
                        "documentType", "CONTRACT",
                        "storagePath", "company-" + testCompany1Id + "/manual-invalid-type.pdf",
                        "fileSize", 123L);

        mockMvc.perform(
                        post("/api/documents")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_TYPE_INVALID"));

        Integer count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document WHERE name = ?",
                        Integer.class,
                        "Manual invalid type");

        assertEquals(0, count);
    }

    @Test
    void createDocumentWithDuplicateNameInSameCompanyThenReturnsValidationError() throws Exception {
        uploadPdf("Existing document");

        Map<String, Object> request =
                Map.of(
                        "companyId", testCompany1Id,
                        "createdByUserId", testUser1Id,
                        "name", "Existing document",
                        "fileType", "application/pdf",
                        "documentType", "INVOICE",
                        "storagePath",
                                "company-" + testCompany1Id + "/existing-document-copy.pdf",
                        "fileSize", 123L);

        mockMvc.perform(
                        post("/api/documents")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_NAME_EXISTS"));

        Integer count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document WHERE name = ? AND company_id = ?",
                        Integer.class,
                        "Existing document",
                        testCompany1Id);

        assertEquals(1, count);
    }

    @Test
    void updateMissingDocumentThenReturnsNotFoundResponse() throws Exception {
        Map<String, Object> request = Map.of("name", "Updated missing document");

        mockMvc.perform(
                        put("/api/documents/{id}", 999999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(
                        jsonPath("$.payload").value("Document with the given id does not exist."));
    }

    @Test
    void updateDocumentWithInvalidStatusThenReturnsValidationError() throws Exception {
        Long documentId = uploadPdf("Status update test");

        Map<String, Object> request = Map.of("documentStatus", "ARCHIVED");

        mockMvc.perform(
                        put("/api/documents/{id}", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[*].code", hasItem("DOCUMENT_STATUS_INVALID")));

        String status =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals("UPLOADED", status);
    }

    @Test
    void updateDocumentWithDuplicateNameThenReturnsValidationError() throws Exception {
        uploadPdf("Original document");
        Long secondDocumentId = uploadPdf("Second document");

        Map<String, Object> request = Map.of("name", "Original document");

        mockMvc.perform(
                        put("/api/documents/{id}", secondDocumentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_NAME_EXISTS"));

        String secondDocumentName =
                jdbcTemplate.queryForObject(
                        "SELECT name FROM document WHERE id = ?", String.class, secondDocumentId);

        assertEquals("Second document", secondDocumentName);
    }

    @Test
    void confirmDocumentTypeWhenReviewRequiredThenUpdatesTypeAndStatus() throws Exception {
        Long documentId = uploadPdfWithType("Manual classification form", "OTHER");

        jdbcTemplate.update(
                """
                UPDATE document
                SET document_status = 'NEEDS_CLASSIFICATION_REVIEW',
                    detected_document_type = 'INVOICE',
                    classification_confidence = 0.400000,
                    processor_id_used = 'old-processor-id'
                WHERE id = ?
                """,
                documentId);

        mockMvc.perform(
                        patch("/api/documents/{id}/classification", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "documentType": "FORM"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.id").value(documentId))
                .andExpect(jsonPath("$.payload.documentType").value("FORM"))
                .andExpect(jsonPath("$.payload.documentStatus").value("UPLOADED"))
                .andExpect(jsonPath("$.payload.detectedDocumentType").value("INVOICE"));

        Map<String, Object> document =
                jdbcTemplate.queryForMap(
                        """
                        SELECT document_type, document_status, detected_document_type,
                               classification_confidence, processor_id_used
                        FROM document
                        WHERE id = ?
                        """,
                        documentId);

        assertEquals("FORM", document.get("document_type"));
        assertEquals("UPLOADED", document.get("document_status"));
        assertEquals("INVOICE", document.get("detected_document_type"));
        assertEquals(null, document.get("processor_id_used"));
    }

    @Test
    void confirmDocumentTypeWithLowerCaseBankStatementThenNormalizesType() throws Exception {
        Long documentId = uploadPdfWithType("Manual classification bank statement", "OTHER");

        jdbcTemplate.update(
                "UPDATE document SET document_status = 'NEEDS_CLASSIFICATION_REVIEW' WHERE id = ?",
                documentId);

        mockMvc.perform(
                        patch("/api/documents/{id}/classification", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "documentType": "bank_statement"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentType").value("BANK_STATEMENT"))
                .andExpect(jsonPath("$.payload.documentStatus").value("UPLOADED"));

        Map<String, Object> document =
                jdbcTemplate.queryForMap(
                        "SELECT document_type, document_status FROM document WHERE id = ?",
                        documentId);

        assertEquals("BANK_STATEMENT", document.get("document_type"));
        assertEquals("UPLOADED", document.get("document_status"));
    }

    @Test
    void confirmDocumentTypeWhenDocumentIsNotWaitingForReviewThenReturnsValidationError()
            throws Exception {
        Long documentId = uploadPdfWithType("Not waiting for classification", "OTHER");

        mockMvc.perform(
                        patch("/api/documents/{id}/classification", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "documentType": "INVOICE"
                                        }
                                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_STATUS_INVALID"));

        Map<String, Object> document =
                jdbcTemplate.queryForMap(
                        "SELECT document_type, document_status FROM document WHERE id = ?",
                        documentId);

        assertEquals("OTHER", document.get("document_type"));
        assertEquals("UPLOADED", document.get("document_status"));
    }

    @Test
    void confirmDocumentTypeWithUnsupportedManualTypeThenReturnsValidationError() throws Exception {
        Long documentId = uploadPdfWithType("Unsupported manual classification", "OTHER");

        jdbcTemplate.update(
                "UPDATE document SET document_status = 'NEEDS_CLASSIFICATION_REVIEW' WHERE id = ?",
                documentId);

        mockMvc.perform(
                        patch("/api/documents/{id}/classification", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "documentType": "OTHER"
                                        }
                                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_TYPE_INVALID"));

        Map<String, Object> document =
                jdbcTemplate.queryForMap(
                        "SELECT document_type, document_status FROM document WHERE id = ?",
                        documentId);

        assertEquals("OTHER", document.get("document_type"));
        assertEquals("NEEDS_CLASSIFICATION_REVIEW", document.get("document_status"));
    }

    @Test
    void confirmDocumentTypeWithUnknownTypeThenReturnsValidationError() throws Exception {
        Long documentId = uploadPdfWithType("Unknown manual classification", "OTHER");

        jdbcTemplate.update(
                "UPDATE document SET document_status = 'NEEDS_CLASSIFICATION_REVIEW' WHERE id = ?",
                documentId);

        mockMvc.perform(
                        patch("/api/documents/{id}/classification", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "documentType": "CONTRACT"
                                        }
                                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_TYPE_INVALID"));

        Map<String, Object> document =
                jdbcTemplate.queryForMap(
                        "SELECT document_type, document_status FROM document WHERE id = ?",
                        documentId);

        assertEquals("OTHER", document.get("document_type"));
        assertEquals("NEEDS_CLASSIFICATION_REVIEW", document.get("document_status"));
    }

    @Test
    void confirmDocumentTypeForMissingDocumentThenReturnsNotFound() throws Exception {
        mockMvc.perform(
                        patch("/api/documents/{id}/classification", 999999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "documentType": "INVOICE"
                                        }
                                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(
                        jsonPath("$.payload").value("Document with the given id does not exist."));
    }

    @Test
    void uploadUnknownDocumentTypeThenReturnsValidationError() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", "unknown.pdf", "application/pdf", PDF_CONTENT);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(file)
                                .param("documentType", "UNKNOWN")
                                .param("name", "Unknown document type"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_TYPE_INVALID"));
    }

    private Long uploadPdf(String name) throws Exception {
        return uploadPdfWithType(name, "INVOICE");
    }

    private Long uploadPdfWithType(String name, String documentType) throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        name.toLowerCase().replace(" ", "-") + ".pdf",
                        "application/pdf",
                        PDF_CONTENT);

        MvcResult result =
                mockMvc.perform(
                                multipart("/api/documents/upload")
                                        .file(file)
                                        .param("documentType", documentType)
                                        .param("name", name))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());

        return response.get("payload").get("id").asLong();
    }

    private void setupTestTenants() {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(
                        status -> {
                            testCompany1Id =
                                    persistCompany(
                                            "Upload Company 1",
                                            "upload-co-1@test.ba",
                                            "group-upload-1");
                            testCompany2Id =
                                    persistCompany(
                                            "Upload Company 2",
                                            "upload-co-2@test.ba",
                                            "group-upload-2");
                            testUser1Id =
                                    persistUser(
                                            testCompany1Id,
                                            KEYCLOAK_USER_1,
                                            "upload-user-1@test.ba");
                            persistUser(
                                    testCompany2Id, KEYCLOAK_USER_2, "upload-user-2@test.ba");
                            userDAO.flush();
                        });
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

    private Long persistUser(Long companyId, String keycloakUserId, String email) {
        UserEntity user = new UserEntity();
        user.setCompanyId(companyId);
        user.setRoleId(roleService.getByName(RoleName.OPERATOR).getId());
        user.setKeycloakUserId(keycloakUserId);
        user.setFirstName("Upload");
        user.setLastName("Tester");
        user.setEmail(email);
        user.setAccountStatus(AccountStatus.ACTIVE);
        return userDAO.persist(user).getId();
    }

    private void authenticateAs(String keycloakUserId) {
        Jwt jwt =
                Jwt.withTokenValue("doc-upload-token")
                        .header("alg", "none")
                        .subject(keycloakUserId)
                        .claim("email", keycloakUserId + "@test.ba")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600))
                        .build();

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new JwtAuthenticationToken(
                                jwt, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    private static Path createTempDirectory() {
        try {
            return Files.createTempDirectory("docflow-upload-integration-test-");
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private static void deleteChildren(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }

        try (Stream<Path> paths = Files.list(directory)) {
            for (Path child : paths.toList()) {
                deleteRecursively(child);
            }
        }
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(path)) {
            for (Path currentPath : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(currentPath);
            }
        }
    }
}
