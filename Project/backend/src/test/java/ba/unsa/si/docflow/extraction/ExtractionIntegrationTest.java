package ba.unsa.si.docflow.extraction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ba.unsa.si.docflow.service.ocr.OcrProvider;
import ba.unsa.si.docflow.service.ocr.model.OcrExtractedField;
import ba.unsa.si.docflow.service.ocr.model.OcrResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExtractionIntegrationTest {

    private static final byte[] PDF_CONTENT =
            "%PDF-1.4 fake invoice content".getBytes(StandardCharsets.UTF_8);

    private static final Path UPLOAD_ROOT = createTempDirectory();

    @Autowired private MockMvc mockMvc;

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private OcrProvider ocrProvider;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("docflow.storage.root-dir", () -> UPLOAD_ROOT.toString());
    }

    @BeforeEach
    void setUp() throws IOException {
        jdbcTemplate.execute("DELETE FROM extraction_field");
        jdbcTemplate.execute("DELETE FROM extraction");
        jdbcTemplate.execute("DELETE FROM document");

        deleteChildren(UPLOAD_ROOT);
        Files.createDirectories(UPLOAD_ROOT);

        reset(ocrProvider);
    }

    @AfterAll
    static void tearDown() throws IOException {
        deleteRecursively(UPLOAD_ROOT);
    }

    @Test
    void processExtractionThenStoresExtractionFieldsAndUpdatesDocumentStatus() throws Exception {
        Long documentId = uploadPdf("Invoice for extraction");

        when(ocrProvider.process(any(byte[].class), eq("application/pdf")))
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
        verify(ocrProvider).process(fileCaptor.capture(), eq("application/pdf"));
        assertArrayEquals(PDF_CONTENT, fileCaptor.getValue());
    }

    @Test
    void findExtractionByDocumentIdThenReturnsPersistedResult() throws Exception {
        Long documentId = uploadPdf("Find extraction invoice");

        when(ocrProvider.process(any(byte[].class), eq("application/pdf")))
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

        when(ocrProvider.process(any(byte[].class), eq("application/pdf")))
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

        when(ocrProvider.process(any(byte[].class), eq("application/pdf")))
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

        when(ocrProvider.process(any(byte[].class), eq("application/pdf")))
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

        assertEquals(firstExtractionId, retryExtractionId);
        assertEquals(3, retryFieldCount);
        assertEquals("250.00", totalAmount);
        verify(ocrProvider, times(2)).process(any(byte[].class), eq("application/pdf"));
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

        when(ocrProvider.process(any(byte[].class), eq("application/pdf")))
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

    private Long uploadPdf(String name) throws Exception {
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
                                        .param("companyId", "1")
                                        .param("createdByUserId", "1")
                                        .param("documentType", "INVOICE")
                                        .param("name", name))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("payload").get("id").asLong();
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
