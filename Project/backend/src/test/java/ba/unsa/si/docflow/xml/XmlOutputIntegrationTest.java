package ba.unsa.si.docflow.xml;

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
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.service.ocr.OcrProvider;
import ba.unsa.si.docflow.service.ocr.model.OcrExtractedField;
import ba.unsa.si.docflow.service.ocr.model.OcrResult;
import ba.unsa.si.docflow.service.role.RoleService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class XmlOutputIntegrationTest {

    private static final byte[] PDF_CONTENT =
            "%PDF-1.4 fake XML test invoice".getBytes(StandardCharsets.UTF_8);

    private static final Path UPLOAD_ROOT = createTempDirectory();

    private static final String TEST_INVOICE_PROCESSOR_ID = "test-xml-invoice-processor-id";

    private static final String TEST_RECEIPT_PROCESSOR_ID = "test-xml-receipt-processor-id";

    private static final String TEST_BANK_STATEMENT_PROCESSOR_ID =
            "test-xml-bank-statement-processor-id";

    private static final String TEST_FORM_PROCESSOR_ID = "test-xml-form-processor-id";

    private static final String TEST_CLASSIFIER_PROCESSOR_ID = "test-xml-classifier-processor-id";

    private static final String KEYCLOAK_USER = "kc-xml-output-test-user";

    private static final String MANAGER_KEYCLOAK_USER = "kc-xml-output-manager-user";

    @Autowired private MockMvc mockMvc;

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private CompanyDAO companyDAO;

    @Autowired private UserDAO userDAO;

    @Autowired private RoleService roleService;

    @Autowired private PlatformTransactionManager transactionManager;

    @MockitoBean private OcrProvider ocrProvider;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {

        registry.add("docflow.storage.root-dir", () -> UPLOAD_ROOT.toString());

        registry.add("docflow.ocr.invoice-processor-id", () -> TEST_INVOICE_PROCESSOR_ID);

        registry.add("docflow.ocr.receipt-processor-id", () -> TEST_RECEIPT_PROCESSOR_ID);

        registry.add(
                "docflow.ocr.bank-statement-processor-id", () -> TEST_BANK_STATEMENT_PROCESSOR_ID);

        registry.add("docflow.ocr.form-processor-id", () -> TEST_FORM_PROCESSOR_ID);

        registry.add("docflow.ocr.classifier-processor-id", () -> TEST_CLASSIFIER_PROCESSOR_ID);
    }

    @BeforeEach
    void setUp() throws IOException {
        jdbcTemplate.execute("DELETE FROM xml_output");
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

        reset(ocrProvider);
    }

    @AfterAll
    static void tearDown() throws IOException {
        deleteRecursively(UPLOAD_ROOT);
    }

    @Test
    void generateXmlForApprovedDocumentThenStoresDatabaseRowAndFile() throws Exception {

        ProcessedInvoice invoice =
                processApprovedInvoice("Approved invoice.pdf", sampleOcrResult());

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/documents/{documentId}/xml-output",
                                        invoice.documentId()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value("OK"))
                        .andExpect(jsonPath("$.payload.documentId").value(invoice.documentId()))
                        .andExpect(jsonPath("$.payload.fileName").value("Approved invoice.xml"))
                        .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());

        String xmlContent = response.get("payload").get("content").asText();

        assertTrue(xmlContent.contains("<docflowDocument>"));

        assertTrue(xmlContent.contains("<documentType>INVOICE</documentType>"));

        assertTrue(xmlContent.contains("name=\"supplier_name\""));

        assertTrue(xmlContent.contains("Test Company d.o.o."));

        assertTrue(xmlContent.contains("name=\"total_amount\""));

        assertTrue(xmlContent.contains(">117<"));

        var xmlRow =
                jdbcTemplate.queryForMap(
                        """
                        SELECT id, document_id, storage_path,
                               file_name, generated_at, generated_by
                        FROM xml_output
                        WHERE document_id = ?
                        """,
                        invoice.documentId());

        assertEquals("Approved invoice.xml", xmlRow.get("file_name"));

        String storagePath = (String) xmlRow.get("storage_path");

        assertTrue(Files.exists(UPLOAD_ROOT.resolve(storagePath)));
    }

    @Test
    void generateXmlForDocumentThatIsNotApprovedThenReturnsBadRequest() throws Exception {

        Long documentId = uploadPdf("Extracted invoice.pdf");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        authenticateAs(MANAGER_KEYCLOAK_USER);

        mockMvc.perform(post("/api/documents/{documentId}/xml-output", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_STATUS_INVALID"));

        Integer outputCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM xml_output
                        WHERE document_id = ?
                        """,
                        Integer.class,
                        documentId);

        assertEquals(0, outputCount);
    }

    @Test
    void generateXmlWhenApprovedDocumentContainsInvalidExtractionThenReturnsBadRequest()
            throws Exception {

        ProcessedInvoice invoice =
                processApprovedInvoice(
                        "Invalid approved invoice.pdf", sampleOcrResultWithoutCurrency());

        mockMvc.perform(post("/api/documents/{documentId}/xml-output", invoice.documentId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_REQUIRED_FIELD_MISSING"));

        Integer outputCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM xml_output
                        WHERE document_id = ?
                        """,
                        Integer.class,
                        invoice.documentId());

        assertEquals(0, outputCount);
    }

    @Test
    void findGeneratedXmlThenReturnsPreviewContent() throws Exception {

        ProcessedInvoice invoice = processApprovedInvoice("Preview invoice.pdf", sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/xml-output", invoice.documentId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/documents/{documentId}/xml-output", invoice.documentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(invoice.documentId()))
                .andExpect(
                        jsonPath("$.payload.content")
                                .value(org.hamcrest.Matchers.containsString("<docflowDocument>")));
    }

    @Test
    void downloadGeneratedXmlThenReturnsXmlFile() throws Exception {

        ProcessedInvoice invoice =
                processApprovedInvoice("Download invoice.pdf", sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/xml-output", invoice.documentId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/documents/{documentId}/xml-output/file", invoice.documentId()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/xml"))
                .andExpect(
                        header().string(
                                        HttpHeaders.CONTENT_DISPOSITION,
                                        "attachment; filename=\"Download invoice.xml\""))
                .andExpect(
                        content()
                                .string(org.hamcrest.Matchers.containsString("<docflowDocument>")));
    }

    @Test
    void getXmlBeforeGenerationThenReturnsNotFound() throws Exception {

        ProcessedInvoice invoice =
                processApprovedInvoice("Missing XML invoice.pdf", sampleOcrResult());

        mockMvc.perform(get("/api/documents/{documentId}/xml-output", invoice.documentId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void regenerateXmlThenKeepsSingleDatabaseRowAndReplacesOldFile() throws Exception {

        ProcessedInvoice invoice =
                processApprovedInvoice("Regenerated invoice.pdf", sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/xml-output", invoice.documentId()))
                .andExpect(status().isOk());

        String firstStoragePath =
                jdbcTemplate.queryForObject(
                        """
                        SELECT storage_path
                        FROM xml_output
                        WHERE document_id = ?
                        """,
                        String.class,
                        invoice.documentId());

        assertTrue(Files.exists(UPLOAD_ROOT.resolve(firstStoragePath)));

        mockMvc.perform(post("/api/documents/{documentId}/xml-output", invoice.documentId()))
                .andExpect(status().isOk());

        String secondStoragePath =
                jdbcTemplate.queryForObject(
                        """
                        SELECT storage_path
                        FROM xml_output
                        WHERE document_id = ?
                        """,
                        String.class,
                        invoice.documentId());

        Integer outputCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM xml_output
                        WHERE document_id = ?
                        """,
                        Integer.class,
                        invoice.documentId());

        assertEquals(1, outputCount);
        assertNotEquals(firstStoragePath, secondStoragePath);

        assertFalse(Files.exists(UPLOAD_ROOT.resolve(firstStoragePath)));

        assertTrue(Files.exists(UPLOAD_ROOT.resolve(secondStoragePath)));
    }

    @Test
    void generateXmlThenEscapesSpecialCharacters() throws Exception {

        ProcessedInvoice invoice =
                processApprovedInvoice(
                        "Special characters invoice.pdf", sampleOcrResultWithSpecialCharacters());

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/documents/{documentId}/xml-output",
                                        invoice.documentId()))
                        .andExpect(status().isOk())
                        .andReturn();

        String xmlContent =
                objectMapper
                        .readTree(result.getResponse().getContentAsString())
                        .get("payload")
                        .get("content")
                        .asText();

        assertTrue(xmlContent.contains("A &amp; B &lt;Supplier&gt;"));
    }

    @Test
    void completeGeneratedXmlThenMarksDocumentAsCompletedAndCreatesHistoryAndAuditLog()
            throws Exception {

        ProcessedInvoice invoice =
                processApprovedInvoice("Completed invoice.pdf", sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/xml-output", invoice.documentId()))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post(
                                "/api/documents/{documentId}/xml-output/complete",
                                invoice.documentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(invoice.documentId()))
                .andExpect(jsonPath("$.payload.content").isNotEmpty());

        String documentStatus =
                jdbcTemplate.queryForObject(
                        """
                        SELECT document_status
                        FROM document
                        WHERE id = ?
                        """,
                        String.class,
                        invoice.documentId());

        Integer historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM status_history
                        WHERE document_id = ?
                          AND old_status = 'APPROVED'
                          AND new_status = 'COMPLETED'
                          AND action = 'DOCUMENT_COMPLETED'
                        """,
                        Integer.class,
                        invoice.documentId());

        Integer auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit_log
                        WHERE document_id = ?
                          AND action = 'DOCUMENT_COMPLETED'
                        """,
                        Integer.class,
                        invoice.documentId());

        assertEquals("COMPLETED", documentStatus);
        assertEquals(1, historyCount);
        assertEquals(1, auditCount);
    }

    @Test
    void completeApprovedDocumentWithoutXmlThenReturnsNotFoundAndKeepsApprovedStatus()
            throws Exception {

        ProcessedInvoice invoice =
                processApprovedInvoice("Approved invoice without XML.pdf", sampleOcrResult());

        mockMvc.perform(
                        post(
                                "/api/documents/{documentId}/xml-output/complete",
                                invoice.documentId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        """
                        SELECT document_status
                        FROM document
                        WHERE id = ?
                        """,
                        String.class,
                        invoice.documentId());

        Integer historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM status_history
                        WHERE document_id = ?
                          AND action = 'DOCUMENT_COMPLETED'
                        """,
                        Integer.class,
                        invoice.documentId());

        assertEquals("APPROVED", documentStatus);
        assertEquals(0, historyCount);
    }

    @Test
    void completeDocumentTwiceThenSecondAttemptReturnsInvalidStatus() throws Exception {

        ProcessedInvoice invoice =
                processApprovedInvoice("Already completed invoice.pdf", sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/xml-output", invoice.documentId()))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post(
                                "/api/documents/{documentId}/xml-output/complete",
                                invoice.documentId()))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post(
                                "/api/documents/{documentId}/xml-output/complete",
                                invoice.documentId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_STATUS_INVALID"));
    }

    @Test
    void operatorCannotGenerateXmlThenReturnsForbidden() throws Exception {

        ProcessedInvoice invoice =
                processApprovedInvoice("Manager-only XML invoice.pdf", sampleOcrResult());

        authenticateAs(KEYCLOAK_USER);

        mockMvc.perform(post("/api/documents/{documentId}/xml-output", invoice.documentId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteDocumentWithGeneratedXmlThenRemovesXmlRowAndBothFiles() throws Exception {

        ProcessedInvoice invoice =
                processApprovedInvoice("Delete invoice with XML.pdf", sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/xml-output", invoice.documentId()))
                .andExpect(status().isOk());

        String documentStoragePath =
                jdbcTemplate.queryForObject(
                        """
                        SELECT storage_path
                        FROM document
                        WHERE id = ?
                        """,
                        String.class,
                        invoice.documentId());

        String xmlStoragePath =
                jdbcTemplate.queryForObject(
                        """
                        SELECT storage_path
                        FROM xml_output
                        WHERE document_id = ?
                        """,
                        String.class,
                        invoice.documentId());

        assertTrue(Files.exists(UPLOAD_ROOT.resolve(documentStoragePath)));

        assertTrue(Files.exists(UPLOAD_ROOT.resolve(xmlStoragePath)));

        /*
         * Brisanje dokumenta je dozvoljeno operatoru i adminu,
         * a ne manageru.
         */
        authenticateAs(KEYCLOAK_USER);

        mockMvc.perform(delete("/api/documents/{documentId}", invoice.documentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"));

        Integer documentCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM document
                        WHERE id = ?
                        """,
                        Integer.class,
                        invoice.documentId());

        Integer xmlOutputCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM xml_output
                        WHERE document_id = ?
                        """,
                        Integer.class,
                        invoice.documentId());

        assertEquals(0, documentCount);
        assertEquals(0, xmlOutputCount);

        assertFalse(Files.exists(UPLOAD_ROOT.resolve(documentStoragePath)));

        assertFalse(Files.exists(UPLOAD_ROOT.resolve(xmlStoragePath)));
    }

    private record ProcessedInvoice(Long documentId, Long extractionId) {}

    private ProcessedInvoice processApprovedInvoice(String documentName, OcrResult ocrResult)
            throws Exception {

        Long documentId = uploadPdf(documentName);

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(ocrResult);

        MvcResult extractionResult =
                mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                        .andExpect(status().isOk())
                        .andReturn();

        Long extractionId =
                objectMapper
                        .readTree(extractionResult.getResponse().getContentAsString())
                        .get("payload")
                        .get("id")
                        .asLong();

        jdbcTemplate.update(
                """
                UPDATE document
                SET document_status = 'APPROVED'
                WHERE id = ?
                """,
                documentId);

        authenticateAs(MANAGER_KEYCLOAK_USER);

        return new ProcessedInvoice(documentId, extractionId);
    }

    private Long uploadPdf(String name) throws Exception {
        MockMultipartFile file =
                new MockMultipartFile("file", name, "application/pdf", PDF_CONTENT);

        MvcResult result =
                mockMvc.perform(
                                multipart("/api/documents/upload")
                                        .file(file)
                                        .param("documentType", "INVOICE")
                                        .param("name", name))
                        .andExpect(status().isOk())
                        .andReturn();

        return objectMapper
                .readTree(result.getResponse().getContentAsString())
                .get("payload")
                .get("id")
                .asLong();
    }

    private OcrResult sampleOcrResult() {
        return new OcrResult(
                "INVOICE\nSupplier: Test Company d.o.o.\nTotal: 117.00 EUR\n",
                List.of(
                        field("supplier_name", "Test Company d.o.o.", null, "0.91"),
                        field("invoice_id", "INV-XML-001", null, "0.97"),
                        field("invoice_date", "06.05.2026", "06.05.2026", "0.96"),
                        field("total_amount", "117.00", "117", "0.95"),
                        field("currency", "EUR", "EUR", "0.89")));
    }

    private OcrResult sampleOcrResultWithoutCurrency() {
        return new OcrResult(
                "INVOICE\nSupplier: Missing currency\n",
                List.of(
                        field("supplier_name", "Missing Currency Company", null, "0.91"),
                        field("invoice_id", "INV-XML-002", null, "0.97"),
                        field("invoice_date", "06.05.2026", "06.05.2026", "0.96"),
                        field("total_amount", "117.00", "117", "0.95")));
    }

    private OcrResult sampleOcrResultWithSpecialCharacters() {
        return new OcrResult(
                "INVOICE\nSupplier: A & B <Supplier>\n",
                List.of(
                        field("supplier_name", "A & B <Supplier>", null, "0.91"),
                        field("invoice_id", "INV-XML-003", null, "0.97"),
                        field("invoice_date", "06.05.2026", "06.05.2026", "0.96"),
                        field("total_amount", "117.00", "117", "0.95"),
                        field("currency", "EUR", "EUR", "0.89")));
    }

    private OcrExtractedField field(
            String type, String value, String normalizedValue, String confidence) {

        return new OcrExtractedField(type, value, normalizedValue, new BigDecimal(confidence));
    }

    private void setupTestTenant() {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(
                        status -> {
                            CompanyEntity company = new CompanyEntity();

                            company.setName("XML Output Test Co");
                            company.setAddress("Address");
                            company.setEmail("xml-output-" + System.nanoTime() + "@test.ba");
                            company.setRegistrationDate(LocalDateTime.now());
                            company.setStatus(CompanyStatus.ACTIVE);
                            company.setKeycloakGroupId("group-xml-output");

                            Long companyId = companyDAO.persist(company).getId();

                            UserEntity user = new UserEntity();

                            user.setCompanyId(companyId);
                            user.setRoleId(roleService.getByName(RoleName.OPERATOR).getId());
                            user.setKeycloakUserId(KEYCLOAK_USER);
                            user.setFirstName("XML");
                            user.setLastName("Tester");
                            user.setEmail("xml-output-user@test.ba");
                            user.setAccountStatus(AccountStatus.ACTIVE);

                            userDAO.persist(user);

                            UserEntity manager = new UserEntity();

                            manager.setCompanyId(companyId);
                            manager.setRoleId(roleService.getByName(RoleName.MANAGER).getId());
                            manager.setKeycloakUserId(MANAGER_KEYCLOAK_USER);
                            manager.setFirstName("XML");
                            manager.setLastName("Manager");
                            manager.setEmail("xml-output-manager@test.ba");
                            manager.setAccountStatus(AccountStatus.ACTIVE);

                            userDAO.persist(manager);
                            userDAO.flush();
                        });
    }

    private void authenticateAs(String keycloakUserId) {
        Jwt jwt =
                Jwt.withTokenValue("xml-output-test-token")
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
            return Files.createTempDirectory("docflow-xml-output-integration-test-");
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
