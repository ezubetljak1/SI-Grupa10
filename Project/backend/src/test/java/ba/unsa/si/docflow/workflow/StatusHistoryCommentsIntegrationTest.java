package ba.unsa.si.docflow.workflow;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.service.ocr.DocumentClassificationService;
import ba.unsa.si.docflow.service.ocr.OcrProvider;
import ba.unsa.si.docflow.service.ocr.model.DocumentClassificationResult;
import ba.unsa.si.docflow.service.ocr.model.OcrExtractedField;
import ba.unsa.si.docflow.service.ocr.model.OcrResult;
import ba.unsa.si.docflow.service.role.RoleService;
import ba.unsa.si.docflow.service.workflow.CommentService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
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
class StatusHistoryCommentsIntegrationTest {

    private static final byte[] PDF_CONTENT =
            "%PDF-1.4 status history test".getBytes(StandardCharsets.UTF_8);
    private static final Path UPLOAD_ROOT = createTempDirectory();
    private static final String TEST_INVOICE_PROCESSOR_ID = "test-invoice-processor-id";
    private static final String KEYCLOAK_USER = "kc-status-history-test-user";

    @Autowired private MockMvc mockMvc;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CompanyDAO companyDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private RoleService roleService;
    @Autowired private PlatformTransactionManager transactionManager;

    @Autowired private CommentService commentService;

    @MockitoBean private OcrProvider ocrProvider;
    @MockitoBean private DocumentClassificationService documentClassificationService;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("docflow.storage.root-dir", () -> UPLOAD_ROOT.toString());
        registry.add("docflow.ocr.invoice-processor-id", () -> TEST_INVOICE_PROCESSOR_ID);
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

        setupTestTenant();
        authenticateAs(KEYCLOAK_USER);
        reset(ocrProvider, documentClassificationService);
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        deleteRecursively(UPLOAD_ROOT);
    }

    @Test
    void uploadCreatesInitialStatusHistory() throws Exception {
        Long documentId = uploadPdf("History upload test");

        Map<String, Object> row =
                jdbcTemplate.queryForMap(
                        "SELECT * FROM status_history WHERE document_id = ?", documentId);

        assertEquals(documentId, ((Number) row.get("document_id")).longValue());
        assertNull(row.get("old_status"));
        assertEquals("UPLOADED", row.get("new_status"));
        assertEquals("DOCUMENT_UPLOADED", row.get("action"));
        assertNotNull(row.get("changed_by_user_id"));
        assertNotNull(row.get("changed_at"));
    }

    @Test
    void processExtractionCreatesStatusHistoryEntry() throws Exception {
        Long documentId = uploadPdf("History extraction test");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        List<Map<String, Object>> history =
                jdbcTemplate.queryForList(
                        "SELECT * FROM status_history WHERE document_id = ? ORDER BY changed_at",
                        documentId);

        assertTrue(history.size() >= 2);
        assertEquals("EXTRACTED", history.get(history.size() - 1).get("new_status"));
        assertEquals("EXTRACTION_COMPLETED", history.get(history.size() - 1).get("action"));
    }

    @Test
    void confirmExtractionCreatesReadyForApprovalHistory() throws Exception {
        Long documentId = uploadPdf("History confirm test");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk());

        Map<String, Object> latest =
                jdbcTemplate.queryForMap(
                        """
                        SELECT old_status, new_status, action
                        FROM status_history
                        WHERE document_id = ?
                        ORDER BY changed_at DESC
                        LIMIT 1
                        """,
                        documentId);

        assertEquals("EXTRACTED", latest.get("old_status"));
        assertEquals("READY_FOR_APPROVAL", latest.get("new_status"));
        assertEquals("EXTRACTION_CONFIRMED", latest.get("action"));
    }

    @Test
    void statusHistoryCannotBeDeletedThroughApi() throws Exception {
        Long documentId = uploadPdf("History delete guard test");

        Integer countBefore =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM status_history WHERE document_id = ?",
                        Integer.class,
                        documentId);

        mockMvc.perform(delete("/api/documents/{id}/status-history", documentId))
                .andExpect(status().isMethodNotAllowed());

        Integer countAfter =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM status_history WHERE document_id = ?",
                        Integer.class,
                        documentId);

        assertEquals(countBefore, countAfter);
    }

    @Test
    void validateRequiredCommentRejectsBlankContent() {
        ApiValidationException exception =
                assertThrows(
                        ApiValidationException.class,
                        () -> commentService.validateRequiredComment("   "));

        assertEquals(
                "COMMENT_REQUIRED", exception.getValidationErrors().getErrors().get(0).getCode());
    }

    @Test
    void createCommentWithoutContentIsRejected() throws Exception {
        Long documentId = uploadPdf("Comment validation test");

        mockMvc.perform(
                        post("/api/documents/{id}/comments", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "content": ""
                                        }
                                        """))
                .andExpect(status().isBadRequest());

        Integer commentCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document_comment WHERE document_id = ?",
                        Integer.class,
                        documentId);

        assertEquals(0, commentCount);
    }

    @Test
    void getStatusHistoryReturnsChronologicalEntries() throws Exception {
        Long documentId = uploadPdf("History API test");

        mockMvc.perform(get("/api/documents/{id}/status-history", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo("OK")))
                .andExpect(jsonPath("$.payload", hasSize(1)))
                .andExpect(jsonPath("$.payload[0].action", equalTo("DOCUMENT_UPLOADED")))
                .andExpect(jsonPath("$.payload[0].newStatus", equalTo("UPLOADED")));
    }

    @Test
    void createAndListGeneralComments() throws Exception {
        Long documentId = uploadPdf("Comments API test");

        mockMvc.perform(
                        post("/api/documents/{id}/comments", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "content": "Please review supplier name."
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.type", equalTo("GENERAL")))
                .andExpect(jsonPath("$.payload.content", equalTo("Please review supplier name.")));

        mockMvc.perform(get("/api/documents/{id}/comments", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", hasSize(1)))
                .andExpect(
                        jsonPath("$.payload[0].content", equalTo("Please review supplier name.")));
    }

    @Test
    void deleteDocumentWithStatusHistoryCommentsAndExtractionDeletesRelatedData() throws Exception {
        Long documentId = uploadPdf("Delete with workflow data test");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/documents/{id}/comments", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "content": "Comment before deleting document."
                                        }
                                        """))
                .andExpect(status().isOk());

        Integer documentCountBefore =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document WHERE id = ?", Integer.class, documentId);

        Integer historyCountBefore =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM status_history WHERE document_id = ?",
                        Integer.class,
                        documentId);

        Integer commentCountBefore =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document_comment WHERE document_id = ?",
                        Integer.class,
                        documentId);

        Integer extractionCountBefore =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM extraction WHERE document_id = ?",
                        Integer.class,
                        documentId);

        Integer extractionFieldCountBefore =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(ef.id)
                        FROM extraction_field ef
                        JOIN extraction e ON ef.extraction_id = e.id
                        WHERE e.document_id = ?
                        """,
                        Integer.class,
                        documentId);

        assertEquals(1, documentCountBefore);
        assertTrue(historyCountBefore != null && historyCountBefore > 0);
        assertEquals(1, commentCountBefore);
        assertEquals(1, extractionCountBefore);
        assertTrue(extractionFieldCountBefore != null && extractionFieldCountBefore > 0);

        mockMvc.perform(delete("/api/documents/{id}", documentId)).andExpect(status().isOk());

        Integer documentCountAfter =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document WHERE id = ?", Integer.class, documentId);

        Integer historyCountAfter =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM status_history WHERE document_id = ?",
                        Integer.class,
                        documentId);

        Integer commentCountAfter =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document_comment WHERE document_id = ?",
                        Integer.class,
                        documentId);

        Integer extractionCountAfter =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM extraction WHERE document_id = ?",
                        Integer.class,
                        documentId);

        Integer extractionFieldCountAfter =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(ef.id)
                        FROM extraction_field ef
                        JOIN extraction e ON ef.extraction_id = e.id
                        WHERE e.document_id = ?
                        """,
                        Integer.class,
                        documentId);

        assertEquals(0, documentCountAfter);
        assertEquals(0, historyCountAfter);
        assertEquals(0, commentCountAfter);
        assertEquals(0, extractionCountAfter);
        assertEquals(0, extractionFieldCountAfter);
    }

    @Test
    void createCommentWithWhitespaceContentIsRejected() throws Exception {
        Long documentId = uploadPdf("Comment whitespace validation test");

        mockMvc.perform(
                        post("/api/documents/{id}/comments", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "content": "   "
                                        }
                                        """))
                .andExpect(status().isBadRequest());

        Integer commentCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document_comment WHERE document_id = ?",
                        Integer.class,
                        documentId);

        assertEquals(0, commentCount);
    }

    @Test
    void getStatusHistoryForMissingDocumentReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/documents/{id}/status-history", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void getCommentsForMissingDocumentReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/documents/{id}/comments", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void createCommentForMissingDocumentReturnsNotFound() throws Exception {
        mockMvc.perform(
                        post("/api/documents/{id}/comments", 999999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "content": "This comment should not be saved."
                                        }
                                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));

        Integer commentCount =
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM document_comment", Integer.class);

        assertEquals(0, commentCount);
    }

    @Test
    void getStatusHistoryReturnsFullWorkflowInChronologicalOrder() throws Exception {
        Long documentId = uploadPdf("History ordering full workflow test");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/documents/{id}/status-history", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo("OK")))
                .andExpect(jsonPath("$.payload", hasSize(3)))
                .andExpect(jsonPath("$.payload[0].action", equalTo("DOCUMENT_UPLOADED")))
                .andExpect(jsonPath("$.payload[0].oldStatus").doesNotExist())
                .andExpect(jsonPath("$.payload[0].newStatus", equalTo("UPLOADED")))
                .andExpect(jsonPath("$.payload[1].action", equalTo("EXTRACTION_COMPLETED")))
                .andExpect(jsonPath("$.payload[1].oldStatus", equalTo("UPLOADED")))
                .andExpect(jsonPath("$.payload[1].newStatus", equalTo("EXTRACTED")))
                .andExpect(jsonPath("$.payload[2].action", equalTo("EXTRACTION_CONFIRMED")))
                .andExpect(jsonPath("$.payload[2].oldStatus", equalTo("EXTRACTED")))
                .andExpect(jsonPath("$.payload[2].newStatus", equalTo("READY_FOR_APPROVAL")));
    }

    @Test
    void getCommentsReturnsCommentsInChronologicalOrder() throws Exception {
        Long documentId = uploadPdf("Comments ordering test");

        mockMvc.perform(
                        post("/api/documents/{id}/comments", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "content": "First comment."
                                        }
                                        """))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/documents/{id}/comments", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "content": "Second comment."
                                        }
                                        """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/documents/{id}/comments", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo("OK")))
                .andExpect(jsonPath("$.payload", hasSize(2)))
                .andExpect(jsonPath("$.payload[0].content", equalTo("First comment.")))
                .andExpect(jsonPath("$.payload[1].content", equalTo("Second comment.")));
    }

    @Test
    void processExtractionFailureCreatesFailedStatusHistoryEntry() throws Exception {
        Long documentId = uploadPdf("History extraction failure test");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenThrow(new RuntimeException("Simulated OCR failure"));

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", equalTo("EXTRACTION_FAILED")));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        Map<String, Object> latestHistory =
                jdbcTemplate.queryForMap(
                        """
                        SELECT old_status, new_status, action, details
                        FROM status_history
                        WHERE document_id = ?
                        ORDER BY changed_at DESC, id DESC
                        LIMIT 1
                        """,
                        documentId);

        assertEquals("PROCESSING_FAILED", documentStatus);
        assertEquals("UPLOADED", latestHistory.get("old_status"));
        assertEquals("PROCESSING_FAILED", latestHistory.get("new_status"));
        assertEquals("EXTRACTION_FAILED", latestHistory.get("action"));
        assertTrue(String.valueOf(latestHistory.get("details")).contains("Simulated OCR failure"));
    }

    @Test
    void manualClassificationReviewCreatesStatusHistoryAndDoesNotBecomeProcessingFailed()
            throws Exception {
        Long documentId = uploadPdf("Classification review history test", "OTHER");

        when(documentClassificationService.classify(any(byte[].class), eq("application/pdf")))
                .thenReturn(
                        new DocumentClassificationResult(
                                DocumentType.OTHER, new BigDecimal("0.42")));

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", equalTo("DOCUMENT_CLASSIFICATION_REVIEW_REQUIRED")));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        Map<String, Object> latestHistory =
                jdbcTemplate.queryForMap(
                        """
                        SELECT old_status, new_status, action, details
                        FROM status_history
                        WHERE document_id = ?
                        ORDER BY changed_at DESC, id DESC
                        LIMIT 1
                        """,
                        documentId);

        assertEquals("NEEDS_CLASSIFICATION_REVIEW", documentStatus);
        assertEquals("UPLOADED", latestHistory.get("old_status"));
        assertEquals("NEEDS_CLASSIFICATION_REVIEW", latestHistory.get("new_status"));
        assertEquals("SYSTEM_STATUS_CHANGE", latestHistory.get("action"));
        assertEquals(
                "Document classification requires manual review.", latestHistory.get("details"));
    }

    private Long uploadPdf(String name) throws Exception {
        return uploadPdf(name, "INVOICE");
    }

    private Long uploadPdf(String name, String documentType) throws Exception {
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

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("payload").path("id").asLong();
    }

    private void setupTestTenant() {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(
                        status -> {
                            CompanyEntity company = new CompanyEntity();
                            company.setName("Status History Test Co");
                            company.setAddress("Address");
                            company.setEmail("status-history-" + System.nanoTime() + "@test.ba");
                            company.setRegistrationDate(LocalDateTime.now());
                            company.setStatus(CompanyStatus.ACTIVE);
                            company.setKeycloakGroupId("group-status-history");
                            Long companyId = companyDAO.persist(company).getId();

                            UserEntity user = new UserEntity();
                            user.setCompanyId(companyId);
                            user.setRoleId(roleService.getByName(RoleName.OPERATOR).getId());
                            user.setKeycloakUserId(KEYCLOAK_USER);
                            user.setFirstName("Status");
                            user.setLastName("Tester");
                            user.setEmail("status-tester@test.ba");
                            user.setAccountStatus(AccountStatus.ACTIVE);
                            userDAO.persist(user);
                        });
    }

    private void authenticateAs(String keycloakUserId) {
        Jwt jwt =
                Jwt.withTokenValue("status-history-test-token")
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

    private OcrResult sampleOcrResult() {
        return new OcrResult(
                "INVOICE\nSupplier: Test Company d.o.o.\nTotal: 117.00 EUR\n",
                List.of(
                        field("supplier_name", "Test Company d.o.o.", null, "0.91"),
                        field("invoice_id", "INV-001", null, "0.97"),
                        field("invoice_date", "2026-05-06", "2026-05-06", "0.96"),
                        field("total_amount", "117.00", "117", "0.95"),
                        field("currency", "EUR", "EUR", "0.89")));
    }

    private OcrExtractedField field(
            String type, String value, String normalizedValue, String confidence) {
        return new OcrExtractedField(
                type, value, normalizedValue, new java.math.BigDecimal(confidence));
    }

    private static Path createTempDirectory() {
        try {
            return Files.createTempDirectory("docflow-status-history-test-");
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private static void deleteChildren(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder())
                    .filter(path -> !path.equals(directory))
                    .forEach(
                            path -> {
                                try {
                                    Files.deleteIfExists(path);
                                } catch (IOException exception) {
                                    throw new UncheckedIOException(exception);
                                }
                            });
        }
    }

    private static void deleteRecursively(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }

        deleteChildren(directory);
        Files.deleteIfExists(directory);
    }
}
