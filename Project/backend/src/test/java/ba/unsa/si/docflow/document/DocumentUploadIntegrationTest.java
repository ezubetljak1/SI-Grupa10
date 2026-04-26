package ba.unsa.si.docflow.document;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentUploadIntegrationTest {

    private static final byte[] PDF_CONTENT =
            "%PDF-1.4 test document content".getBytes(StandardCharsets.UTF_8);

    private static final Path UPLOAD_ROOT = createTempDirectory();

    @Autowired private MockMvc mockMvc;

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("docflow.storage.root-dir", () -> UPLOAD_ROOT.toString());
    }

    @BeforeEach
    void setUp() throws IOException {
        jdbcTemplate.execute("DELETE FROM document");

        deleteChildren(UPLOAD_ROOT);
        Files.createDirectories(UPLOAD_ROOT);
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
                                .param("companyId", "1")
                                .param("createdByUserId", "1")
                                .param("documentType", "INVOICE")
                                .param("name", "Test invoice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.payload.name").value("Test invoice"))
                .andExpect(jsonPath("$.payload.companyId").value(1))
                .andExpect(jsonPath("$.payload.createdBy").value(1))
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
        assertTrue(storagePath.startsWith("company-1/"));
    }

    @Test
    void uploadWithoutNameThenUsesOriginalFileName() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file", "original-invoice.pdf", "application/pdf", PDF_CONTENT);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(file)
                                .param("companyId", "1")
                                .param("createdByUserId", "1")
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
        uploadPdf("Duplicate invoice", 1L);

        MockMultipartFile duplicateFile =
                new MockMultipartFile("file", "duplicate.pdf", "application/pdf", PDF_CONTENT);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(duplicateFile)
                                .param("companyId", "1")
                                .param("createdByUserId", "1")
                                .param("documentType", "INVOICE")
                                .param("name", "Duplicate invoice"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_NAME_EXISTS"));

        Integer count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM document WHERE name = ? AND company_id = ?",
                        Integer.class,
                        "Duplicate invoice",
                        1L);

        assertEquals(1, count);
    }

    @Test
    void uploadSameNameInDifferentCompanyThenSucceeds() throws Exception {
        uploadPdf("Shared invoice name", 1L);

        MockMultipartFile file =
                new MockMultipartFile("file", "shared.pdf", "application/pdf", PDF_CONTENT);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(file)
                                .param("companyId", "2")
                                .param("createdByUserId", "1")
                                .param("documentType", "INVOICE")
                                .param("name", "Shared invoice name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.name").value("Shared invoice name"))
                .andExpect(jsonPath("$.payload.companyId").value(2));

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
                                .param("companyId", "1")
                                .param("createdByUserId", "1")
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
                                .param("companyId", "1")
                                .param("createdByUserId", "1")
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
                                .param("companyId", "1")
                                .param("createdByUserId", "1")
                                .param("documentType", "INVOICE")
                                .param("name", "Large file"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").value("DOCUMENT_FILE_SIZE_EXCEEDED"));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM document", Integer.class);

        assertEquals(0, count);
    }

    @Test
    void downloadUploadedFileThenReturnsStoredContent() throws Exception {
        Long documentId = uploadPdf("Download test invoice", 1L);

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
        Long documentId = uploadPdf("Delete test invoice", 1L);

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

    private Long uploadPdf(String name, Long companyId) throws Exception {
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
                                        .param("companyId", String.valueOf(companyId))
                                        .param("createdByUserId", "1")
                                        .param("documentType", "INVOICE")
                                        .param("name", name))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());

        return response.get("payload").get("id").asLong();
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
