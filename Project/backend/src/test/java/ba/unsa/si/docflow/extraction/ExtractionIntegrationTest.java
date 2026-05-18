package ba.unsa.si.docflow.extraction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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
import org.mockito.ArgumentCaptor;
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
class ExtractionIntegrationTest {

    private static final byte[] PDF_CONTENT =
            "%PDF-1.4 fake invoice content".getBytes(StandardCharsets.UTF_8);

    private static final Path UPLOAD_ROOT = createTempDirectory();
    private static final String TEST_INVOICE_PROCESSOR_ID = "test-invoice-processor-id";
    private static final String TEST_RECEIPT_PROCESSOR_ID = "test-receipt-processor-id";
    private static final String TEST_BANK_STATEMENT_PROCESSOR_ID =
            "test-bank-statement-processor-id";
    private static final String TEST_FORM_PROCESSOR_ID = "test-form-processor-id";
    private static final String TEST_CLASSIFIER_PROCESSOR_ID = "test-classifier-processor-id";
    private static final String KEYCLOAK_USER = "kc-extraction-test-user";

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
    void processExtractionThenStoresExtractionFieldsAndUpdatesDocumentStatus() throws Exception {
        Long documentId = uploadPdf("Invoice for extraction");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId))
                .andExpect(jsonPath("$.payload.fields.length()").value(5))
                .andExpect(
                        jsonPath("$.payload.fields[?(@.fieldName == 'invoice_id')].value")
                                .value("INV-001"))
                .andExpect(
                        jsonPath("$.payload.fields[?(@.fieldName == 'supplier_name')].value")
                                .value("Test Company d.o.o."))
                .andExpect(
                        jsonPath("$.payload.fields[?(@.fieldName == 'total_amount')].value")
                                .value("117.00"));

        Integer extractionCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM extraction WHERE document_id = ?",
                        Integer.class,
                        documentId);

        Integer fieldCount =
                jdbcTemplate.queryForObject(
                        """
                SELECT COUNT(*)
                FROM extraction_field ef
                JOIN extraction e ON ef.extraction_id = e.id
                WHERE e.document_id = ?
                """,
                        Integer.class,
                        documentId);

        Map<String, Object> document =
                jdbcTemplate.queryForMap(
                        "SELECT document_status, document_type FROM document WHERE id = ?",
                        documentId);

        assertEquals(1, extractionCount);
        assertEquals(5, fieldCount);
        assertEquals("EXTRACTED", document.get("document_status"));
        assertEquals("INVOICE", document.get("document_type"));

        ArgumentCaptor<byte[]> fileCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(ocrProvider)
                .process(
                        fileCaptor.capture(), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID));
        assertArrayEquals(PDF_CONTENT, fileCaptor.getValue());
    }

    @Test
    void processReceiptDocumentThenUsesReceiptProcessorWithoutClassifier() throws Exception {
        Long documentId = uploadPdfWithType("Receipt for extraction", "RECEIPT");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_RECEIPT_PROCESSOR_ID)))
                .thenReturn(sampleReceiptOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        Map<String, Object> document = findDocumentClassificationMetadata(documentId);

        assertEquals("EXTRACTED", document.get("document_status"));
        assertEquals("RECEIPT", document.get("document_type"));
        assertNull(document.get("detected_document_type"));
        assertNull(document.get("classification_confidence"));
        assertEquals(TEST_RECEIPT_PROCESSOR_ID, document.get("processor_id_used"));

        verify(ocrProvider, times(1))
                .process(any(byte[].class), eq("application/pdf"), eq(TEST_RECEIPT_PROCESSOR_ID));
        verify(ocrProvider, never())
                .process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID));
    }

    @Test
    void processBankStatementDocumentThenUsesBankStatementProcessorWithoutClassifier()
            throws Exception {
        Long documentId = uploadPdfWithType("Bank statement for extraction", "BANK_STATEMENT");

        when(ocrProvider.process(
                        any(byte[].class),
                        eq("application/pdf"),
                        eq(TEST_BANK_STATEMENT_PROCESSOR_ID)))
                .thenReturn(sampleBankStatementOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        Map<String, Object> document = findDocumentClassificationMetadata(documentId);

        assertEquals("EXTRACTED", document.get("document_status"));
        assertEquals("BANK_STATEMENT", document.get("document_type"));
        assertNull(document.get("detected_document_type"));
        assertNull(document.get("classification_confidence"));
        assertEquals(TEST_BANK_STATEMENT_PROCESSOR_ID, document.get("processor_id_used"));

        verify(ocrProvider, times(1))
                .process(
                        any(byte[].class),
                        eq("application/pdf"),
                        eq(TEST_BANK_STATEMENT_PROCESSOR_ID));
        verify(ocrProvider, never())
                .process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID));
    }

    @Test
    void processFormDocumentThenUsesFormProcessorWithoutClassifier() throws Exception {
        Long documentId = uploadPdfWithType("Form for extraction", "FORM");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_FORM_PROCESSOR_ID)))
                .thenReturn(sampleFormOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        Map<String, Object> document = findDocumentClassificationMetadata(documentId);

        assertEquals("EXTRACTED", document.get("document_status"));
        assertEquals("FORM", document.get("document_type"));
        assertNull(document.get("detected_document_type"));
        assertNull(document.get("classification_confidence"));
        assertEquals(TEST_FORM_PROCESSOR_ID, document.get("processor_id_used"));

        verify(ocrProvider, times(1))
                .process(any(byte[].class), eq("application/pdf"), eq(TEST_FORM_PROCESSOR_ID));
        verify(ocrProvider, never())
                .process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID));
    }

    @Test
    void processOtherDocumentWhenClassifierDetectsInvoiceThenUsesInvoiceProcessor()
            throws Exception {
        Long documentId = uploadPdfWithType("Unknown invoice document", "OTHER");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID)))
                .thenReturn(classifierResult("INVOICE", "0.92"));
        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        Map<String, Object> document = findDocumentClassificationMetadata(documentId);

        assertEquals("EXTRACTED", document.get("document_status"));
        assertEquals("INVOICE", document.get("document_type"));
        assertEquals("INVOICE", document.get("detected_document_type"));
        assertBigDecimalEquals("0.92", document.get("classification_confidence"));
        assertEquals(TEST_INVOICE_PROCESSOR_ID, document.get("processor_id_used"));

        verify(ocrProvider, times(1))
                .process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID));
        verify(ocrProvider, times(1))
                .process(any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID));
    }

    @Test
    void processOtherDocumentWhenClassifierDetectsReceiptThenUsesReceiptProcessor()
            throws Exception {
        Long documentId = uploadPdfWithType("Unknown receipt document", "OTHER");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID)))
                .thenReturn(classifierResult("RECEIPT", "0.91"));
        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_RECEIPT_PROCESSOR_ID)))
                .thenReturn(sampleReceiptOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        Map<String, Object> document = findDocumentClassificationMetadata(documentId);

        assertEquals("EXTRACTED", document.get("document_status"));
        assertEquals("RECEIPT", document.get("document_type"));
        assertEquals("RECEIPT", document.get("detected_document_type"));
        assertBigDecimalEquals("0.91", document.get("classification_confidence"));
        assertEquals(TEST_RECEIPT_PROCESSOR_ID, document.get("processor_id_used"));

        verify(ocrProvider, times(1))
                .process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID));
        verify(ocrProvider, times(1))
                .process(any(byte[].class), eq("application/pdf"), eq(TEST_RECEIPT_PROCESSOR_ID));
    }

    @Test
    void processOtherDocumentWhenClassifierDetectsBankStatementThenUsesBankStatementProcessor()
            throws Exception {
        Long documentId = uploadPdfWithType("Unknown bank statement document", "OTHER");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID)))
                .thenReturn(classifierResult("BANK_STATEMENT", "0.89"));
        when(ocrProvider.process(
                        any(byte[].class),
                        eq("application/pdf"),
                        eq(TEST_BANK_STATEMENT_PROCESSOR_ID)))
                .thenReturn(sampleBankStatementOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        Map<String, Object> document = findDocumentClassificationMetadata(documentId);

        assertEquals("EXTRACTED", document.get("document_status"));
        assertEquals("BANK_STATEMENT", document.get("document_type"));
        assertEquals("BANK_STATEMENT", document.get("detected_document_type"));
        assertBigDecimalEquals("0.89", document.get("classification_confidence"));
        assertEquals(TEST_BANK_STATEMENT_PROCESSOR_ID, document.get("processor_id_used"));

        verify(ocrProvider, times(1))
                .process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID));
        verify(ocrProvider, times(1))
                .process(
                        any(byte[].class),
                        eq("application/pdf"),
                        eq(TEST_BANK_STATEMENT_PROCESSOR_ID));
    }

    @Test
    void processOtherDocumentWhenClassifierDetectsFormThenUsesFormProcessor() throws Exception {
        Long documentId = uploadPdfWithType("Unknown form document", "OTHER");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID)))
                .thenReturn(classifierResult("FORM", "0.86"));
        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_FORM_PROCESSOR_ID)))
                .thenReturn(sampleFormOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        Map<String, Object> document = findDocumentClassificationMetadata(documentId);

        assertEquals("EXTRACTED", document.get("document_status"));
        assertEquals("FORM", document.get("document_type"));
        assertEquals("FORM", document.get("detected_document_type"));
        assertBigDecimalEquals("0.86", document.get("classification_confidence"));
        assertEquals(TEST_FORM_PROCESSOR_ID, document.get("processor_id_used"));

        verify(ocrProvider, times(1))
                .process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID));
        verify(ocrProvider, times(1))
                .process(any(byte[].class), eq("application/pdf"), eq(TEST_FORM_PROCESSOR_ID));
    }

    @Test
    void processOtherDocumentWhenClassifierReturnsOtherThenRequiresManualReview() throws Exception {
        Long documentId = uploadPdfWithType("Unknown unsupported document", "OTHER");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID)))
                .thenReturn(classifierResult("OTHER", "0.88"));

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DOCUMENT_CLASSIFICATION_REVIEW_REQUIRED"))
                .andExpect(
                        jsonPath("$.payload")
                                .value("Document classification requires manual review."));

        Map<String, Object> document = findDocumentClassificationMetadata(documentId);

        assertEquals("NEEDS_CLASSIFICATION_REVIEW", document.get("document_status"));
        assertEquals("OTHER", document.get("document_type"));
        assertEquals("OTHER", document.get("detected_document_type"));
        assertBigDecimalEquals("0.88", document.get("classification_confidence"));
        assertNull(document.get("processor_id_used"));

        Integer extractionCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM extraction WHERE document_id = ?",
                        Integer.class,
                        documentId);

        assertEquals(0, extractionCount);

        verify(ocrProvider, times(1))
                .process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID));
        verifyNoParserProcessorWasCalled();
    }

    @Test
    void processOtherDocumentWhenClassifierConfidenceIsLowThenRequiresManualReview()
            throws Exception {
        Long documentId = uploadPdfWithType("Low confidence unknown document", "OTHER");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID)))
                .thenReturn(classifierResult("INVOICE", "0.40"));

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DOCUMENT_CLASSIFICATION_REVIEW_REQUIRED"))
                .andExpect(
                        jsonPath("$.payload")
                                .value("Document classification requires manual review."));

        Map<String, Object> document = findDocumentClassificationMetadata(documentId);

        assertEquals("NEEDS_CLASSIFICATION_REVIEW", document.get("document_status"));
        assertEquals("OTHER", document.get("document_type"));
        assertEquals("INVOICE", document.get("detected_document_type"));
        assertBigDecimalEquals("0.40", document.get("classification_confidence"));
        assertNull(document.get("processor_id_used"));

        Integer extractionCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM extraction WHERE document_id = ?",
                        Integer.class,
                        documentId);

        assertEquals(0, extractionCount);

        verify(ocrProvider, times(1))
                .process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID));
        verifyNoParserProcessorWasCalled();
    }

    @Test
    void processOtherDocumentWhenClassifierProviderFailsThenMarksDocumentAsProcessingFailed()
            throws Exception {
        Long documentId = uploadPdfWithType("Classifier failure document", "OTHER");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID)))
                .thenThrow(new IllegalStateException("Classifier unavailable"));

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("EXTRACTION_FAILED"))
                .andExpect(
                        jsonPath("$.payload")
                                .value("Document extraction failed: Classifier unavailable"));

        Map<String, Object> document = findDocumentClassificationMetadata(documentId);

        assertEquals("PROCESSING_FAILED", document.get("document_status"));
        assertEquals("OTHER", document.get("document_type"));
        assertNull(document.get("detected_document_type"));
        assertNull(document.get("classification_confidence"));
        assertNull(document.get("processor_id_used"));

        Integer extractionCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM extraction WHERE document_id = ?",
                        Integer.class,
                        documentId);

        assertEquals(0, extractionCount);

        verify(ocrProvider, times(1))
                .process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_CLASSIFIER_PROCESSOR_ID));
        verifyNoParserProcessorWasCalled();
    }

    @Test
    void confirmReceiptExtractionWithRequiredFieldsAndDateAliasThenSucceeds() throws Exception {
        Long documentId =
                processDocumentWithTypeAndResult(
                        "Valid receipt for confirmation",
                        "RECEIPT",
                        TEST_RECEIPT_PROCESSOR_ID,
                        sampleReceiptOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        assertDocumentStatus(documentId, "READY_FOR_APPROVAL");
    }

    @Test
    void confirmReceiptExtractionWithoutDateAliasThenReturnsBadRequest() throws Exception {
        Long documentId =
                processDocumentWithTypeAndResult(
                        "Receipt without date",
                        "RECEIPT",
                        TEST_RECEIPT_PROCESSOR_ID,
                        sampleReceiptOcrResultWithoutDateAlias());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_REQUIRED_FIELD_MISSING"));

        assertDocumentStatus(documentId, "EXTRACTED");
    }

    @Test
    void confirmReceiptExtractionWithLowConfidenceRequiredFieldThenReturnsBadRequest()
            throws Exception {
        Long documentId =
                processDocumentWithTypeAndResult(
                        "Receipt with low confidence total",
                        "RECEIPT",
                        TEST_RECEIPT_PROCESSOR_ID,
                        sampleReceiptOcrResultWithLowConfidenceRequiredField());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_LOW_CONFIDENCE"));

        assertDocumentStatus(documentId, "EXTRACTED");
    }

    @Test
    void confirmReceiptExtractionWithLowConfidenceOptionalFieldThenSucceeds() throws Exception {
        Long documentId =
                processDocumentWithTypeAndResult(
                        "Receipt with low confidence optional field",
                        "RECEIPT",
                        TEST_RECEIPT_PROCESSOR_ID,
                        sampleReceiptOcrResultWithLowConfidenceOptionalField());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        assertDocumentStatus(documentId, "READY_FOR_APPROVAL");
    }

    @Test
    void confirmBankStatementExtractionWithRequiredFieldsAndBasicStructureThenSucceeds()
            throws Exception {
        Long documentId =
                processDocumentWithTypeAndResult(
                        "Valid bank statement for confirmation",
                        "BANK_STATEMENT",
                        TEST_BANK_STATEMENT_PROCESSOR_ID,
                        sampleBankStatementOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        assertDocumentStatus(documentId, "READY_FOR_APPROVAL");
    }

    @Test
    void confirmBankStatementWithoutIdentityFieldThenReturnsBadRequest() throws Exception {
        Long documentId =
                processDocumentWithTypeAndResult(
                        "Bank statement without identity",
                        "BANK_STATEMENT",
                        TEST_BANK_STATEMENT_PROCESSOR_ID,
                        sampleBankStatementOcrResultWithoutIdentityField());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_REQUIRED_FIELD_MISSING"));

        assertDocumentStatus(documentId, "EXTRACTED");
    }

    @Test
    void confirmBankStatementWithoutActivityFieldThenReturnsBadRequest() throws Exception {
        Long documentId =
                processDocumentWithTypeAndResult(
                        "Bank statement without activity",
                        "BANK_STATEMENT",
                        TEST_BANK_STATEMENT_PROCESSOR_ID,
                        sampleBankStatementOcrResultWithoutActivityField());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_REQUIRED_FIELD_MISSING"));

        assertDocumentStatus(documentId, "EXTRACTED");
    }

    @Test
    void confirmBankStatementWithLowConfidenceRequiredFieldThenReturnsBadRequest()
            throws Exception {
        Long documentId =
                processDocumentWithTypeAndResult(
                        "Bank statement with low confidence account",
                        "BANK_STATEMENT",
                        TEST_BANK_STATEMENT_PROCESSOR_ID,
                        sampleBankStatementOcrResultWithLowConfidenceRequiredField());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_LOW_CONFIDENCE"));

        assertDocumentStatus(documentId, "EXTRACTED");
    }

    @Test
    void confirmBankStatementWithLowConfidenceOptionalFieldThenSucceeds() throws Exception {
        Long documentId =
                processDocumentWithTypeAndResult(
                        "Bank statement with low confidence optional field",
                        "BANK_STATEMENT",
                        TEST_BANK_STATEMENT_PROCESSOR_ID,
                        sampleBankStatementOcrResultWithLowConfidenceOptionalField());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        assertDocumentStatus(documentId, "READY_FOR_APPROVAL");
    }

    @Test
    void confirmBankStatementWithInvalidBalanceFormatThenReturnsBadRequest() throws Exception {
        Long documentId =
                processDocumentWithTypeAndResult(
                        "Bank statement with invalid balance",
                        "BANK_STATEMENT",
                        TEST_BANK_STATEMENT_PROCESSOR_ID,
                        sampleBankStatementOcrResultWithInvalidBalanceFormat());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_NUMERIC_FORMAT_INVALID"));

        assertDocumentStatus(documentId, "EXTRACTED");
    }

    @Test
    void confirmFormExtractionWithLowConfidenceFieldsThenSucceeds() throws Exception {
        Long documentId =
                processDocumentWithTypeAndResult(
                        "Form with low confidence fields",
                        "FORM",
                        TEST_FORM_PROCESSOR_ID,
                        sampleFormOcrResultWithLowConfidenceFields());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        assertDocumentStatus(documentId, "READY_FOR_APPROVAL");
    }

    @Test
    void findExtractionByDocumentIdThenReturnsPersistedResult() throws Exception {
        Long documentId = uploadPdf("Find extraction invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId))
                .andExpect(jsonPath("$.payload.rawJson").isNotEmpty())
                .andExpect(jsonPath("$.payload.fields.length()").value(5));
    }

    @Test
    void findExtractionFieldsByDocumentIdThenReturnsOnlyFields() throws Exception {
        Long documentId = uploadPdf("Fields by document invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/documents/{documentId}/extraction/fields", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.length()").value(5))
                .andExpect(jsonPath("$.payload[?(@.fieldName == 'currency')].value").value("EUR"))
                .andExpect(
                        jsonPath("$.payload[?(@.fieldName == 'invoice_date')].value")
                                .value("2026-05-06"));
    }

    @Test
    void findExtractionFieldsBeforeProcessingThenReturnsNotFound() throws Exception {
        Long documentId = uploadPdf("No extraction fields yet invoice");

        mockMvc.perform(get("/api/documents/{documentId}/extraction/fields", documentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void findExtractionFieldsByMissingExtractionIdThenReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/extractions/{extractionId}/fields", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void findExtractionFieldsByExtractionIdThenReturnsFields() throws Exception {
        Long documentId = uploadPdf("Fields by extraction invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        MvcResult result =
                mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        Long extractionId = response.get("payload").get("id").asLong();

        mockMvc.perform(get("/api/extractions/{extractionId}/fields", extractionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.length()").value(5))
                .andExpect(
                        jsonPath("$.payload[?(@.fieldName == 'total_amount')].value")
                                .value("117.00"));
    }

    @Test
    void findExtractionFieldsForMissingDocumentThenReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/documents/{documentId}/extraction/fields", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void retryExtractionThenReusesSameExtractionAndReplacesFields() throws Exception {
        Long documentId = uploadPdf("Retry invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult())
                .thenReturn(sampleRetryOcrResult());

        MvcResult firstResult =
                mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode firstResponse =
                objectMapper.readTree(firstResult.getResponse().getContentAsString());
        Long firstExtractionId = firstResponse.get("payload").get("id").asLong();

        Integer firstFieldCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM extraction_field WHERE extraction_id = ?",
                        Integer.class,
                        firstExtractionId);

        assertEquals(5, firstFieldCount);

        MvcResult retryResult =
                mockMvc.perform(post("/api/documents/{documentId}/extraction/retry", documentId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value("OK"))
                        .andReturn();

        JsonNode retryResponse =
                objectMapper.readTree(retryResult.getResponse().getContentAsString());
        Long retryExtractionId = retryResponse.get("payload").get("id").asLong();

        Integer retryFieldCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM extraction_field WHERE extraction_id = ?",
                        Integer.class,
                        retryExtractionId);

        String totalAmount =
                jdbcTemplate.queryForObject(
                        """
                SELECT ef."value"
                FROM extraction_field ef
                WHERE ef.extraction_id = ?
                AND ef.field_name = 'total_amount'
                """,
                        String.class,
                        retryExtractionId);

        Integer placeholderCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM extraction_field
                        WHERE extraction_id = ?
                          AND is_placeholder = true
                          AND field_name IN ('invoice_id', 'invoice_date')
                        """,
                        Integer.class,
                        retryExtractionId);

        assertEquals(firstExtractionId, retryExtractionId);
        assertEquals(5, retryFieldCount);
        assertEquals(2, placeholderCount);
        assertEquals("250.00", totalAmount);
        verify(ocrProvider, times(2))
                .process(any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID));
    }

    @Test
    void processExtractionForMissingDocumentThenReturnsNotFound() throws Exception {
        mockMvc.perform(post("/api/documents/{documentId}/extraction", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(
                        jsonPath("$.payload").value("Document with the given id does not exist."));

        verifyNoInteractions(ocrProvider);
    }

    @Test
    void findExtractionBeforeProcessingThenReturnsNotFound() throws Exception {
        Long documentId = uploadPdf("No extraction yet invoice");

        mockMvc.perform(get("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void processExtractionWhenOcrFailsThenMarksDocumentAsProcessingFailed() throws Exception {
        Long documentId = uploadPdf("Failed extraction invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenThrow(new IllegalStateException("OCR provider unavailable"));

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("EXTRACTION_FAILED"))
                .andExpect(
                        jsonPath("$.payload")
                                .value("Document extraction failed: OCR provider unavailable"));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        Integer extractionCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM extraction WHERE document_id = ?",
                        Integer.class,
                        documentId);

        assertEquals("PROCESSING_FAILED", documentStatus);
        assertEquals(0, extractionCount);
    }

    @Test
    void deleteExtractedDocumentThenRemovesDocumentExtractionFieldsAndFile() throws Exception {
        Long documentId = uploadPdf("Delete extracted invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        String storagePath =
                jdbcTemplate.queryForObject(
                        "SELECT storage_path FROM document WHERE id = ?", String.class, documentId);

        Path storedFile = UPLOAD_ROOT.resolve(storagePath);
        assertTrue(Files.exists(storedFile));

        mockMvc.perform(delete("/api/documents/{id}", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"));

        Integer documentCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document WHERE id = ?", Integer.class, documentId);

        Integer extractionCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM extraction WHERE document_id = ?",
                        Integer.class,
                        documentId);

        Integer fieldCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM extraction_field ef
                        JOIN extraction e ON ef.extraction_id = e.id
                        WHERE e.document_id = ?
                        """,
                        Integer.class,
                        documentId);

        assertEquals(0, documentCount);
        assertEquals(0, extractionCount);
        assertEquals(0, fieldCount);
        assertFalse(Files.exists(storedFile));
    }

    @Test
    void updateExtractionFieldThenChangesValueAndMarksFieldAsCorrected() throws Exception {
        Long documentId = uploadPdf("Editable extraction invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        MvcResult processResult =
                mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode processResponse =
                objectMapper.readTree(processResult.getResponse().getContentAsString());
        Long extractionId = processResponse.get("payload").get("id").asLong();

        Long fieldId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT ef.id
                        FROM extraction_field ef
                        WHERE ef.extraction_id = ?
                          AND ef.field_name = 'total_amount'
                        """,
                        Long.class,
                        extractionId);

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        fieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "value": "125.50"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.id").value(fieldId))
                .andExpect(jsonPath("$.payload.value").value("125.50"))
                .andExpect(jsonPath("$.payload.corrected").value(true));

        Map<String, Object> updatedField =
                jdbcTemplate.queryForMap(
                        """
                        SELECT "value", is_corrected, is_placeholder
                        FROM extraction_field
                        WHERE id = ?
                        """,
                        fieldId);

        assertEquals("125.50", updatedField.get("value"));
        assertEquals(true, updatedField.get("is_corrected"));
        assertEquals(false, updatedField.get("is_placeholder"));
    }

    @Test
    void updateExtractionFieldWithWrongExtractionIdThenReturnsNotFoundAndKeepsOldValue()
            throws Exception {
        Long firstDocumentId = uploadPdf("First editable extraction invoice");
        Long secondDocumentId = uploadPdf("Second editable extraction invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult())
                .thenReturn(sampleOcrResult());

        MvcResult firstProcessResult =
                mockMvc.perform(post("/api/documents/{documentId}/extraction", firstDocumentId))
                        .andExpect(status().isOk())
                        .andReturn();

        MvcResult secondProcessResult =
                mockMvc.perform(post("/api/documents/{documentId}/extraction", secondDocumentId))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode firstResponse =
                objectMapper.readTree(firstProcessResult.getResponse().getContentAsString());
        JsonNode secondResponse =
                objectMapper.readTree(secondProcessResult.getResponse().getContentAsString());

        Long firstExtractionId = firstResponse.get("payload").get("id").asLong();
        Long secondExtractionId = secondResponse.get("payload").get("id").asLong();

        Long firstFieldId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT ef.id
                        FROM extraction_field ef
                        WHERE ef.extraction_id = ?
                          AND ef.field_name = 'total_amount'
                        """,
                        Long.class,
                        firstExtractionId);

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        secondExtractionId,
                                        firstFieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "value": "999.99"
                                        }
                                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));

        Map<String, Object> unchangedField =
                jdbcTemplate.queryForMap(
                        """
                        SELECT "value", is_corrected
                        FROM extraction_field
                        WHERE id = ?
                        """,
                        firstFieldId);

        assertEquals("117.00", unchangedField.get("value"));
        assertEquals(false, unchangedField.get("is_corrected"));
    }

    @Test
    void updateDecimalFieldWithEmptyValueThenReturnsBadRequest() throws Exception {
        long[] ids = uploadAndExtractWith(sampleOcrResult());
        long extractionId = ids[0];
        long fieldId = ids[1];

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        fieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"value\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_AMOUNT_INVALID"));

        assertFieldUnchanged(extractionId, fieldId, "117.00", false);
    }

    @Test
    void updateDecimalFieldWithNonNumericValueThenReturnsBadRequest() throws Exception {
        long[] ids = uploadAndExtractWith(sampleOcrResult());
        long extractionId = ids[0];
        long fieldId = ids[1];

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        fieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"value\": \"abc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_AMOUNT_INVALID"));

        assertFieldUnchanged(extractionId, fieldId, "117.00", false);
    }

    @Test
    void updateDecimalFieldWithTooManyDecimalsThenReturnsBadRequest() throws Exception {
        long[] ids = uploadAndExtractWith(sampleOcrResult());
        long extractionId = ids[0];
        long fieldId = ids[1];

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        fieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"value\": \"117.001\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_AMOUNT_INVALID"));

        assertFieldUnchanged(extractionId, fieldId, "117.00", false);
    }

    @Test
    void updateDecimalFieldWithNegativeValueThenReturnsBadRequest() throws Exception {
        long[] ids = uploadAndExtractWith(sampleOcrResult());
        long extractionId = ids[0];
        long fieldId = ids[1];

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        fieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"value\": \"-1.00\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_AMOUNT_INVALID"));

        assertFieldUnchanged(extractionId, fieldId, "117.00", false);
    }

    @Test
    void updateDecimalFieldWithCommaAsDecimalSeparatorThenSucceeds() throws Exception {
        long[] ids = uploadAndExtractWith(sampleOcrResult());
        long extractionId = ids[0];
        long fieldId = ids[1];

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        fieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"value\": \"125,50\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.value").value("125,50"))
                .andExpect(jsonPath("$.payload.corrected").value(true));
    }

    @Test
    void updateDateFieldWithInvalidFormatThenReturnsBadRequestAndKeepsOldValue() throws Exception {
        long[] ids = uploadAndExtractFieldWith(sampleOcrResult(), "invoice_date");
        long extractionId = ids[0];
        long fieldId = ids[1];

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        fieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"value\": \"06-05-2026\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_DATE_FORMAT_INVALID"));

        assertFieldUnchanged(extractionId, fieldId, "2026-05-06", false);
    }

    @Test
    void updateDateFieldWithSupportedFormatThenSucceeds() throws Exception {
        long[] ids = uploadAndExtractFieldWith(sampleOcrResult(), "invoice_date");
        long extractionId = ids[0];
        long fieldId = ids[1];

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        fieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"value\": \"06.05.2026\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.value").value("06.05.2026"))
                .andExpect(jsonPath("$.payload.corrected").value(true));
    }

    @Test
    void updateTotalAmountWhenInconsistentWithNetPlusVatThenReturnsBadRequest() throws Exception {
        long[] ids = uploadAndExtractWith(sampleOcrResultWithNetVatTotal());
        long extractionId = ids[0];
        long totalFieldId = ids[1];

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        totalFieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"value\": \"200.00\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_AMOUNT_INCONSISTENT"));

        assertFieldUnchanged(extractionId, totalFieldId, "117.00", false);
    }

    @Test
    void confirmExtractionThenMarksDocumentReadyForApprovalAndKeepsCorrectedFields()
            throws Exception {
        Long documentId = uploadPdf("Confirm extraction invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        MvcResult processResult =
                mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode processResponse =
                objectMapper.readTree(processResult.getResponse().getContentAsString());
        Long extractionId = processResponse.get("payload").get("id").asLong();

        Long fieldId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT ef.id
                        FROM extraction_field ef
                        WHERE ef.extraction_id = ?
                          AND ef.field_name = 'total_amount'
                        """,
                        Long.class,
                        extractionId);

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        fieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "value": "125.50"
                                        }
                                        """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        Integer fieldCount =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM extraction_field WHERE extraction_id = ?",
                        Integer.class,
                        extractionId);

        Map<String, Object> correctedField =
                jdbcTemplate.queryForMap(
                        """
                        SELECT "value", is_corrected
                        FROM extraction_field
                        WHERE id = ?
                        """,
                        fieldId);

        assertEquals("READY_FOR_APPROVAL", documentStatus);
        assertEquals(5, fieldCount);
        assertEquals("125.50", correctedField.get("value"));
        assertEquals(true, correctedField.get("is_corrected"));

        verify(ocrProvider, times(1))
                .process(any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID));
    }

    @Test
    void confirmExtractionBeforeProcessingThenReturnsNotFound() throws Exception {
        Long documentId = uploadPdf("Confirm without extraction invoice");

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));

        verifyNoInteractions(ocrProvider);
    }

    @Test
    void confirmExtractionWithLowConfidenceUncorrectedFieldThenReturnsBadRequest()
            throws Exception {
        Long documentId = uploadPdf("Low confidence invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleLowConfidenceOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_LOW_CONFIDENCE"));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals("EXTRACTED", documentStatus);
    }

    @Test
    void confirmExtractionWithLowConfidenceCorrectedFieldThenSucceeds() throws Exception {
        Long documentId = uploadPdf("Corrected low confidence invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleLowConfidenceOcrResult());

        MvcResult processResult =
                mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode processResponse =
                objectMapper.readTree(processResult.getResponse().getContentAsString());
        Long extractionId = processResponse.get("payload").get("id").asLong();

        Long lowConfidenceFieldId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT ef.id
                        FROM extraction_field ef
                        WHERE ef.extraction_id = ?
                          AND ef.field_name = 'total_amount'
                        """,
                        Long.class,
                        extractionId);

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        lowConfidenceFieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "value": "117.00"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.corrected").value(true));

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals("READY_FOR_APPROVAL", documentStatus);
    }

    @Test
    void confirmInvoiceExtractionWithoutRequiredFieldThenReturnsBadRequest() throws Exception {
        Long documentId = uploadPdf("Missing required field invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResultWithoutInvoiceId());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_REQUIRED_FIELD_MISSING"));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals("EXTRACTED", documentStatus);
    }

    @Test
    void confirmInvoiceExtractionWithBlankFieldThenReturnsBadRequest() throws Exception {
        Long documentId = uploadPdf("Blank required field invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResultWithBlankSupplierName());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_EMPTY"));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals("EXTRACTED", documentStatus);
    }

    @Test
    void confirmInvoiceExtractionWithRequiredFieldsThenSucceeds() throws Exception {
        Long documentId = uploadPdf("Valid required fields invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResult());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals("READY_FOR_APPROVAL", documentStatus);
    }

    @Test
    void confirmInvoiceExtractionWithInvalidDateFormatThenReturnsBadRequest() throws Exception {
        Long documentId = uploadPdf("Invalid date format invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResultWithInvalidDateFormat());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_DATE_FORMAT_INVALID"));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals("EXTRACTED", documentStatus);
    }

    @Test
    void confirmInvoiceExtractionWithDifferentFieldNameCaseThenSucceeds() throws Exception {
        Long documentId = uploadPdf("Normalized field names invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResultWithMixedCaseFieldNames());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk());

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals("READY_FOR_APPROVAL", documentStatus);
    }

    @Test
    void processExtractionWhenRequiredFieldsAreMissingThenCreatesPlaceholderFields()
            throws Exception {
        Long documentId = uploadPdf("Missing placeholders invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResultMissingSupplierAndCurrency());

        MvcResult processResult =
                mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value("OK"))
                        .andExpect(jsonPath("$.payload.fields.length()").value(5))
                        .andExpect(
                                jsonPath(
                                                "$.payload.fields[?(@.fieldName == 'supplier_name')].placeholder")
                                        .value(true))
                        .andExpect(
                                jsonPath(
                                                "$.payload.fields[?(@.fieldName == 'currency')].placeholder")
                                        .value(true))
                        .andReturn();

        JsonNode processResponse =
                objectMapper.readTree(processResult.getResponse().getContentAsString());
        Long extractionId = processResponse.get("payload").get("id").asLong();

        Integer placeholderCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM extraction_field
                        WHERE extraction_id = ?
                          AND is_placeholder = true
                          AND field_name IN ('supplier_name', 'currency')
                        """,
                        Integer.class,
                        extractionId);

        Integer realFieldCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM extraction_field
                        WHERE extraction_id = ?
                          AND is_placeholder = false
                        """,
                        Integer.class,
                        extractionId);

        assertEquals(2, placeholderCount);
        assertEquals(3, realFieldCount);
    }

    @Test
    void confirmExtractionWithRequiredPlaceholderFieldsThenReturnsBadRequest() throws Exception {
        Long documentId = uploadPdf("Confirm placeholder invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResultMissingSupplierAndCurrency());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_REQUIRED_FIELD_MISSING"));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals("EXTRACTED", documentStatus);
    }

    @Test
    void updatePlaceholderFieldThenSetsCorrectedTrueAndPlaceholderFalse() throws Exception {
        Long documentId = uploadPdf("Edit placeholder invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResultMissingSupplierAndCurrency());

        MvcResult processResult =
                mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode processResponse =
                objectMapper.readTree(processResult.getResponse().getContentAsString());
        Long extractionId = processResponse.get("payload").get("id").asLong();

        Long supplierFieldId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT id
                        FROM extraction_field
                        WHERE extraction_id = ?
                          AND field_name = 'supplier_name'
                          AND is_placeholder = true
                        """,
                        Long.class,
                        extractionId);

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        supplierFieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "value": "Manual Supplier d.o.o."
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.value").value("Manual Supplier d.o.o."))
                .andExpect(jsonPath("$.payload.corrected").value(true))
                .andExpect(jsonPath("$.payload.placeholder").value(false));

        Map<String, Object> updatedField =
                jdbcTemplate.queryForMap(
                        """
                        SELECT "value", is_corrected, is_placeholder
                        FROM extraction_field
                        WHERE id = ?
                        """,
                        supplierFieldId);

        assertEquals("Manual Supplier d.o.o.", updatedField.get("value"));
        assertEquals(true, updatedField.get("is_corrected"));
        assertEquals(false, updatedField.get("is_placeholder"));
    }

    @Test
    void confirmExtractionAfterFillingPlaceholderFieldsThenSucceeds() throws Exception {
        Long documentId = uploadPdf("Filled placeholders invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResultMissingSupplierAndCurrency());

        MvcResult processResult =
                mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode processResponse =
                objectMapper.readTree(processResult.getResponse().getContentAsString());
        Long extractionId = processResponse.get("payload").get("id").asLong();

        Long supplierFieldId = findFieldId(extractionId, "supplier_name");
        Long currencyFieldId = findFieldId(extractionId, "currency");

        updateField(extractionId, supplierFieldId, "Manual Supplier d.o.o.");
        updateField(extractionId, currencyFieldId, "EUR");

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.documentId").value(documentId));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        Integer placeholderCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM extraction_field
                        WHERE extraction_id = ?
                          AND is_placeholder = true
                        """,
                        Integer.class,
                        extractionId);

        assertEquals("READY_FOR_APPROVAL", documentStatus);
        assertEquals(0, placeholderCount);
    }

    @Test
    void confirmInvoiceExtractionWhenTotalAmountIsSmallerThanNetAmountThenReturnsBadRequest()
            throws Exception {
        Long documentId = uploadPdf("Invoice total smaller than net");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResultWithTotalSmallerThanNet());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_AMOUNT_INCONSISTENT"));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals("EXTRACTED", documentStatus);
    }

    @Test
    void confirmInvoiceExtractionWhenNetPlusVatDoesNotMatchTotalThenReturnsBadRequest()
            throws Exception {
        Long documentId = uploadPdf("Invoice net vat mismatch");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(sampleOcrResultWithNetVatMismatch());

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_AMOUNT_INCONSISTENT"));
    }

    @Test
    void updateDecimalFieldWithCurrencyTextThenReturnsBadRequest() throws Exception {
        long[] ids = uploadAndExtractWith(sampleOcrResult());
        long extractionId = ids[0];
        long fieldId = ids[1];

        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        fieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"value\": \"1500 KM\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("EXTRACTION_FIELD_AMOUNT_INVALID"));

        assertFieldUnchanged(extractionId, fieldId, "117.00", false);
    }

    private OcrResult sampleOcrResultWithTotalSmallerThanNet() {
        return new OcrResult(
                "INVOICE\nNet 1500.00\nVAT 255.00\nTotal 1000.00 EUR\n",
                List.of(
                        field("supplier_name", "Math Test Company", null, "0.91"),
                        field("invoice_id", "INV-MATH-001", null, "0.97"),
                        field("invoice_date", "2026-05-16", "2026-05-16", "0.96"),
                        field("net_amount", "1500.00", "1500", "0.95"),
                        field("vat_amount", "255.00", "255", "0.95"),
                        field("total_amount", "1000.00", "1000", "0.95"),
                        field("currency", "EUR", "EUR", "0.89")));
    }

    private OcrResult sampleOcrResultWithNetVatMismatch() {
        return new OcrResult(
                "INVOICE\nNet 100.00\nVAT 17.00\nTotal 90.00 EUR\n",
                List.of(
                        field("supplier_name", "Mismatch Company", null, "0.91"),
                        field("invoice_id", "INV-MATH-002", null, "0.97"),
                        field("invoice_date", "2026-05-16", "2026-05-16", "0.96"),
                        field("net_amount", "100.00", "100", "0.95"),
                        field("vat_amount", "17.00", "17", "0.95"),
                        field("total_amount", "90.00", "90", "0.95"),
                        field("currency", "EUR", "EUR", "0.89")));
    }

    private Long findFieldId(Long extractionId, String fieldName) {
        return jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM extraction_field
                WHERE extraction_id = ?
                  AND field_name = ?
                """,
                Long.class,
                extractionId,
                fieldName);
    }

    private void updateField(Long extractionId, Long fieldId, String value) throws Exception {
        mockMvc.perform(
                        patch(
                                        "/api/extractions/{extractionId}/fields/{fieldId}",
                                        extractionId,
                                        fieldId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "value": "%s"
                                        }
                                        """
                                                .formatted(value)))
                .andExpect(status().isOk());
    }

    private OcrResult sampleOcrResultMissingSupplierAndCurrency() {
        return new OcrResult(
                "INVOICE\nInvoice: INV-001\nDate: 2026-05-06\nTotal: 117.00\n",
                List.of(
                        field("invoice_id", "INV-001", null, "0.97"),
                        field("invoice_date", "2026-05-06", "2026-05-06", "0.96"),
                        field("total_amount", "117.00", "117", "0.95")));
    }

    private OcrResult sampleOcrResultWithMixedCaseFieldNames() {
        return new OcrResult(
                "INVOICE\nSupplier: Test Company d.o.o.\nTotal: 117.00 EUR\n",
                List.of(
                        field(" Supplier_Name ", "Test Company d.o.o.", null, "0.91"),
                        field("Invoice_ID", "INV-001", null, "0.97"),
                        field("INVOICE_DATE", "2026-05-06", "2026-05-06", "0.96"),
                        field("Total_Amount", "117.00", "117", "0.95"),
                        field("Currency", "EUR", "EUR", "0.89")));
    }

    private OcrResult sampleOcrResultWithoutInvoiceId() {
        return new OcrResult(
                "INVOICE\nSupplier: Test Company d.o.o.\nTotal: 117.00 EUR\n",
                List.of(
                        field("supplier_name", "Test Company d.o.o.", null, "0.91"),
                        field("invoice_date", "2026-05-06", "2026-05-06", "0.96"),
                        field("total_amount", "117.00", "117", "0.95"),
                        field("currency", "EUR", "EUR", "0.89")));
    }

    private OcrResult sampleOcrResultWithBlankSupplierName() {
        return new OcrResult(
                "INVOICE\nSupplier:\nTotal: 117.00 EUR\n",
                List.of(
                        field("supplier_name", "   ", null, "0.91"),
                        field("invoice_id", "INV-001", null, "0.97"),
                        field("invoice_date", "2026-05-06", "2026-05-06", "0.96"),
                        field("total_amount", "117.00", "117", "0.95"),
                        field("currency", "EUR", "EUR", "0.89")));
    }

    private OcrResult sampleOcrResultWithInvalidDateFormat() {
        return new OcrResult(
                "INVOICE\nSupplier: Test Company d.o.o.\nTotal: 117.00 EUR\n",
                List.of(
                        field("supplier_name", "Test Company d.o.o.", null, "0.91"),
                        field("invoice_id", "INV-001", null, "0.97"),
                        field("invoice_date", "06-05-2026", null, "0.96"),
                        field("total_amount", "117.00", "117", "0.95"),
                        field("currency", "EUR", "EUR", "0.89")));
    }

    private OcrResult sampleLowConfidenceOcrResult() {
        return new OcrResult(
                "INVOICE\nSupplier: Test Company d.o.o.\nTotal: 117.00 EUR\n",
                List.of(
                        field("supplier_name", "Test Company d.o.o.", null, "0.91"),
                        field("invoice_id", "INV-001", null, "0.97"),
                        field("invoice_date", "2026-05-06", "2026-05-06", "0.96"),
                        field("total_amount", "117.00", "117", "0.26"),
                        field("currency", "EUR", "EUR", "0.89")));
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

    private void setupTestTenant() {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(
                        status -> {
                            CompanyEntity company = new CompanyEntity();
                            company.setName("Extraction Test Co");
                            company.setAddress("Address");
                            company.setEmail("extraction-" + System.nanoTime() + "@test.ba");
                            company.setRegistrationDate(LocalDateTime.now());
                            company.setStatus(CompanyStatus.ACTIVE);
                            company.setKeycloakGroupId("group-extraction");
                            Long companyId = companyDAO.persist(company).getId();

                            UserEntity user = new UserEntity();
                            user.setCompanyId(companyId);
                            user.setRoleId(roleService.getByName(RoleName.OPERATOR).getId());
                            user.setKeycloakUserId(KEYCLOAK_USER);
                            user.setFirstName("Extraction");
                            user.setLastName("Tester");
                            user.setEmail("extraction-user@test.ba");
                            user.setAccountStatus(AccountStatus.ACTIVE);
                            userDAO.persist(user);
                            userDAO.flush();
                        });
    }

    private void authenticateAs(String keycloakUserId) {
        Jwt jwt =
                Jwt.withTokenValue("extraction-test-token")
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

    private OcrResult classifierResult(String documentType, String confidence) {
        return new OcrResult(
                "Classification result: " + documentType,
                List.of(field(documentType, documentType, documentType, confidence)));
    }

    private OcrResult sampleReceiptOcrResult() {
        return new OcrResult(
                "RECEIPT\nMerchant: Test Shop\nTotal: 20.00 EUR\n",
                List.of(
                        field("supplier_name", "Test Shop", null, "0.90"),
                        field("expense_date", "2026-05-06", "2026-05-06", "0.88"),
                        field("total_amount", "20.00", "20", "0.93"),
                        field("currency", "EUR", "EUR", "0.89")));
    }

    private OcrResult sampleBankStatementOcrResult() {
        return new OcrResult(
                "BANK STATEMENT\nBank: Test Bank\nAccount: BA000123\n",
                List.of(
                        field("bank_name", "Test Bank", null, "0.92"),
                        field("account_number", "BA000123", null, "0.91"),
                        field("statement_start_date", "2026-05-01", "2026-05-01", "0.89"),
                        field("statement_end_date", "2026-05-31", "2026-05-31", "0.89")));
    }

    private OcrResult sampleFormOcrResult() {
        return new OcrResult(
                "FORM\nApplicant: Test User\nApproved: yes\n",
                List.of(
                        field("applicant_name", "Test User", null, "0.91"),
                        field("approval_date", "2026-05-06", "2026-05-06", "0.87"),
                        field("approved", "yes", null, "0.85")));
    }

    private Long processDocumentWithTypeAndResult(
            String name, String documentType, String processorId, OcrResult ocrResult)
            throws Exception {
        Long documentId = uploadPdfWithType(name, documentType);

        when(ocrProvider.process(any(byte[].class), eq("application/pdf"), eq(processorId)))
                .thenReturn(ocrResult);

        mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"));

        return documentId;
    }

    private void assertDocumentStatus(Long documentId, String expectedStatus) {
        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals(expectedStatus, documentStatus);
    }

    private OcrResult sampleReceiptOcrResultWithoutDateAlias() {
        return new OcrResult(
                "RECEIPT\nMerchant: Test Shop\nTotal: 20.00 EUR\n",
                List.of(
                        field("supplier_name", "Test Shop", null, "0.90"),
                        field("total_amount", "20.00", "20", "0.93"),
                        field("currency", "EUR", "EUR", "0.89")));
    }

    private OcrResult sampleReceiptOcrResultWithLowConfidenceRequiredField() {
        return new OcrResult(
                "RECEIPT\nMerchant: Test Shop\nTotal: 20.00 EUR\n",
                List.of(
                        field("supplier_name", "Test Shop", null, "0.90"),
                        field("expense_date", "2026-05-06", "2026-05-06", "0.88"),
                        field("total_amount", "20.00", "20", "0.26"),
                        field("currency", "EUR", "EUR", "0.89")));
    }

    private OcrResult sampleReceiptOcrResultWithLowConfidenceOptionalField() {
        return new OcrResult(
                "RECEIPT\nMerchant: Test Shop\nTotal: 20.00 EUR\nPayment: card\n",
                List.of(
                        field("supplier_name", "Test Shop", null, "0.90"),
                        field("expense_date", "2026-05-06", "2026-05-06", "0.88"),
                        field("total_amount", "20.00", "20", "0.93"),
                        field("currency", "EUR", "EUR", "0.89"),
                        field("payment_type", "card", null, "0.25")));
    }

    private OcrResult sampleBankStatementOcrResultWithoutIdentityField() {
        return new OcrResult(
                "BANK STATEMENT\nAccount: BA000123\nPeriod: 2026-05-01 - 2026-05-31\n",
                List.of(
                        field("account_number", "BA000123", null, "0.91"),
                        field("statement_start_date", "2026-05-01", "2026-05-01", "0.89"),
                        field("statement_end_date", "2026-05-31", "2026-05-31", "0.89")));
    }

    private OcrResult sampleBankStatementOcrResultWithoutActivityField() {
        return new OcrResult(
                "BANK STATEMENT\nBank: Test Bank\nAccount: BA000123\n",
                List.of(
                        field("bank_name", "Test Bank", null, "0.92"),
                        field("account_number", "BA000123", null, "0.91")));
    }

    private OcrResult sampleBankStatementOcrResultWithLowConfidenceRequiredField() {
        return new OcrResult(
                "BANK STATEMENT\nBank: Test Bank\nAccount: BA000123\n",
                List.of(
                        field("bank_name", "Test Bank", null, "0.92"),
                        field("account_number", "BA000123", null, "0.26"),
                        field("statement_start_date", "2026-05-01", "2026-05-01", "0.89"),
                        field("statement_end_date", "2026-05-31", "2026-05-31", "0.89")));
    }

    private OcrResult sampleBankStatementOcrResultWithLowConfidenceOptionalField() {
        return new OcrResult(
                "BANK STATEMENT\nBank: Test Bank\nAccount: BA000123\nType: checking\n",
                List.of(
                        field("bank_name", "Test Bank", null, "0.92"),
                        field("account_number", "BA000123", null, "0.91"),
                        field("statement_start_date", "2026-05-01", "2026-05-01", "0.89"),
                        field("statement_end_date", "2026-05-31", "2026-05-31", "0.89"),
                        field("account_type", "checking", null, "0.25")));
    }

    private OcrResult sampleBankStatementOcrResultWithInvalidBalanceFormat() {
        return new OcrResult(
                "BANK STATEMENT\nBank: Test Bank\nAccount: BA000123\nBalance: 1 000.00\n",
                List.of(
                        field("bank_name", "Test Bank", null, "0.92"),
                        field("account_number", "BA000123", null, "0.91"),
                        field("statement_start_date", "2026-05-01", "2026-05-01", "0.89"),
                        field("starting_balance", "1 000.00", null, "0.90")));
    }

    private OcrResult sampleFormOcrResultWithLowConfidenceFields() {
        return new OcrResult(
                "FORM\nApplicant: Test User\nApproved: yes\n",
                List.of(
                        field("applicant_name", "Test User", null, "0.25"),
                        field("approval_date", "2026-05-06", "2026-05-06", "0.30"),
                        field("approved", "yes", null, "0.20")));
    }

    private Map<String, Object> findDocumentClassificationMetadata(Long documentId) {
        return jdbcTemplate.queryForMap(
                """
                SELECT document_status, document_type, detected_document_type,
                       classification_confidence, processor_id_used
                FROM document
                WHERE id = ?
                """,
                documentId);
    }

    private void assertBigDecimalEquals(String expected, Object actual) {
        assertTrue(actual instanceof BigDecimal, "Expected BigDecimal but got: " + actual);
        assertEquals(0, new BigDecimal(expected).compareTo((BigDecimal) actual));
    }

    private void verifyNoParserProcessorWasCalled() {
        verify(ocrProvider, never())
                .process(any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID));
        verify(ocrProvider, never())
                .process(any(byte[].class), eq("application/pdf"), eq(TEST_RECEIPT_PROCESSOR_ID));
        verify(ocrProvider, never())
                .process(
                        any(byte[].class),
                        eq("application/pdf"),
                        eq(TEST_BANK_STATEMENT_PROCESSOR_ID));
        verify(ocrProvider, never())
                .process(any(byte[].class), eq("application/pdf"), eq(TEST_FORM_PROCESSOR_ID));
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

    private OcrResult sampleOcrResultWithNetVatTotal() {
        return new OcrResult(
                "INVOICE\nNet 100 + VAT 17 = 117\n",
                List.of(
                        field("net_amount", "100.00", "100", "0.94"),
                        field("vat_amount", "17.00", "17", "0.93"),
                        field("total_amount", "117.00", "117", "0.95"),
                        field("currency", "EUR", "EUR", "0.89")));
    }

    /**
     * @return { extractionId, total_amount field id }
     */
    private long[] uploadAndExtractWith(OcrResult ocrResult) throws Exception {
        return uploadAndExtractFieldWith(ocrResult, "total_amount");
    }

    private long[] uploadAndExtractFieldWith(OcrResult ocrResult, String fieldName)
            throws Exception {
        Long documentId = uploadPdf("Decimal validation invoice");

        when(ocrProvider.process(
                        any(byte[].class), eq("application/pdf"), eq(TEST_INVOICE_PROCESSOR_ID)))
                .thenReturn(ocrResult);

        MvcResult processResult =
                mockMvc.perform(post("/api/documents/{documentId}/extraction", documentId))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode processResponse =
                objectMapper.readTree(processResult.getResponse().getContentAsString());
        long extractionId = processResponse.get("payload").get("id").asLong();

        long fieldId =
                jdbcTemplate.queryForObject(
                        """
                        SELECT ef.id
                        FROM extraction_field ef
                        WHERE ef.extraction_id = ?
                          AND ef.field_name = ?
                        """,
                        Long.class,
                        extractionId,
                        fieldName);

        return new long[] {extractionId, fieldId};
    }

    private void assertFieldUnchanged(
            long extractionId, long fieldId, String expectedValue, boolean expectedCorrected)
            throws Exception {
        Map<String, Object> row =
                jdbcTemplate.queryForMap(
                        """
                        SELECT ef."value", ef.is_corrected
                        FROM extraction_field ef
                        WHERE ef.id = ? AND ef.extraction_id = ?
                        """,
                        fieldId,
                        extractionId);

        assertEquals(expectedValue, row.get("value"));
        assertEquals(expectedCorrected, row.get("is_corrected"));
    }

    private OcrResult sampleRetryOcrResult() {
        return new OcrResult(
                "INVOICE\nSupplier: Retry Company d.o.o.\nTotal: 250.00 EUR\n",
                List.of(
                        field("supplier_name", "Retry Company d.o.o.", null, "0.92"),
                        field("total_amount", "250.00", "250", "0.96"),
                        field("currency", "EUR", "EUR", "0.90")));
    }

    private OcrExtractedField field(
            String type, String value, String normalizedValue, String confidence) {
        return new OcrExtractedField(type, value, normalizedValue, new BigDecimal(confidence));
    }

    private static Path createTempDirectory() {
        try {
            return Files.createTempDirectory("docflow-extraction-integration-test-");
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
