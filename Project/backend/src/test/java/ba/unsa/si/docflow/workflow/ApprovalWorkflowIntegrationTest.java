package ba.unsa.si.docflow.workflow;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.service.role.RoleService;

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
class ApprovalWorkflowIntegrationTest {

    private static final byte[] PDF_CONTENT =
            "%PDF-1.4 approval workflow test".getBytes(StandardCharsets.UTF_8);
    private static final Path UPLOAD_ROOT = createTempDirectory();

    private static final String APPROVER_USER = "kc-approval-test-approver";
    private static final String MANAGER_USER = "kc-approval-test-manager";
    private static final String ADMIN_USER = "kc-approval-test-admin";
    private static final String OPERATOR_USER = "kc-approval-test-operator";

    @Autowired private MockMvc mockMvc;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CompanyDAO companyDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private RoleService roleService;
    @Autowired private PlatformTransactionManager transactionManager;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("docflow.storage.root-dir", () -> UPLOAD_ROOT.toString());
        registry.add("docflow.ocr.invoice-processor-id", () -> "test-invoice-processor-id");
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
        authenticateAs(APPROVER_USER);
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        deleteRecursively(UPLOAD_ROOT);
    }
    @Test
    void approverApprovesReadyForApprovalDocument() throws Exception {
        Long documentId = uploadAndSetReadyForApproval("Approve test doc");

        mockMvc.perform(post("/api/documents/{id}/approval/approve", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.documentStatus").value("APPROVED"));

        Map<String, Object> history = jdbcTemplate.queryForMap(
                "SELECT * FROM status_history WHERE document_id = ? AND new_status = 'APPROVED'",
                documentId);
        assertNotNull(history);
        assertEquals("DOCUMENT_APPROVED", history.get("action"));

        List<Map<String, Object>> auditRows = jdbcTemplate.queryForList(
                "SELECT * FROM audit_log WHERE document_id = ? AND action = 'DOCUMENT_APPROVED'",
                documentId);
        assertFalse(auditRows.isEmpty());
    }
    @Test
    void approverApprovesWithOptionalComment() throws Exception {
        Long documentId = uploadAndSetReadyForApproval("Approve with comment doc");

        mockMvc.perform(post("/api/documents/{id}/approval/approve", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Sve OK, odobravam.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.documentStatus").value("APPROVED"));

        List<Map<String, Object>> comments = jdbcTemplate.queryForList(
                "SELECT * FROM document_comment WHERE document_id = ? AND type = 'APPROVAL'",
                documentId);
        assertFalse(comments.isEmpty());
        assertEquals("Sve OK, odobravam.", comments.get(0).get("content"));
    }
    @Test
    void approverCannotApproveDocumentNotReadyForApproval() throws Exception {
        Long documentId = uploadPdf("Not ready doc");
        authenticateAs(APPROVER_USER);

        mockMvc.perform(post("/api/documents/{id}/approval/approve", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"\"}"))
                .andExpect(status().isBadRequest());

        Map<String, Object> doc = jdbcTemplate.queryForMap(
                "SELECT document_status FROM document WHERE id = ?", documentId);
        assertEquals("UPLOADED", doc.get("document_status"));
    }
    @Test
    void approverRejectsWithRequiredComment() throws Exception {
        Long documentId = uploadAndSetReadyForApproval("Reject test doc");

        mockMvc.perform(post("/api/documents/{id}/approval/reject", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Dokument nije validan.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.documentStatus").value("REJECTED"));

        List<Map<String, Object>> comments = jdbcTemplate.queryForList(
                "SELECT * FROM document_comment WHERE document_id = ? AND type = 'REJECTION'",
                documentId);
        assertFalse(comments.isEmpty());
        assertEquals("Dokument nije validan.", comments.get(0).get("content"));

        Map<String, Object> history = jdbcTemplate.queryForMap(
                "SELECT * FROM status_history WHERE document_id = ? AND new_status = 'REJECTED'",
                documentId);
        assertNotNull(history);
    }
    @Test
    void rejectWithoutCommentFails() throws Exception {
        Long documentId = uploadAndSetReadyForApproval("Reject no comment doc");

        mockMvc.perform(post("/api/documents/{id}/approval/reject", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"\"}"))
                .andExpect(status().isBadRequest());

        Map<String, Object> doc = jdbcTemplate.queryForMap(
                "SELECT document_status FROM document WHERE id = ?", documentId);
        assertEquals("READY_FOR_APPROVAL", doc.get("document_status"));
    }
    @Test
    void approverReturnsForCorrectionWithComment() throws Exception {
        Long documentId = uploadAndSetReadyForApproval("Return correction doc");

        mockMvc.perform(post("/api/documents/{id}/approval/correction", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Ispraviti iznos na fakturi.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.documentStatus").value("NEEDS_CORRECTION"));

        List<Map<String, Object>> comments = jdbcTemplate.queryForList(
                "SELECT * FROM document_comment WHERE document_id = ? AND type = 'CORRECTION_REQUEST'",
                documentId);
        assertFalse(comments.isEmpty());
        assertEquals("Ispraviti iznos na fakturi.", comments.get(0).get("content"));

        Map<String, Object> history = jdbcTemplate.queryForMap(
                "SELECT * FROM status_history WHERE document_id = ? AND new_status = 'NEEDS_CORRECTION'",
                documentId);
        assertNotNull(history);
        assertEquals("DOCUMENT_RETURNED_FOR_CORRECTION", history.get("action"));
    }
    @Test
    void returnForCorrectionWithoutCommentFails() throws Exception {
        Long documentId = uploadAndSetReadyForApproval("Return no comment doc");

        mockMvc.perform(post("/api/documents/{id}/approval/correction", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"\"}"))
                .andExpect(status().isBadRequest());

        Map<String, Object> doc = jdbcTemplate.queryForMap(
                "SELECT document_status FROM document WHERE id = ?", documentId);
        assertEquals("READY_FOR_APPROVAL", doc.get("document_status"));
    }
    @Test
    void operatorCannotCallApprovalEndpoint() throws Exception {
        authenticateAs(OPERATOR_USER);
        Long documentId = uploadAndSetReadyForApprovalAsOperator("Operator approve attempt");

        authenticateAs(OPERATOR_USER);

        mockMvc.perform(post("/api/documents/{id}/approval/approve", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"\"}"))
                .andExpect(status().isForbidden());
    }
    @Test
    void operatorCannotCallRejectEndpoint() throws Exception {
        authenticateAs(OPERATOR_USER);
        Long documentId = uploadAndSetReadyForApprovalAsOperator("Operator reject attempt");

        authenticateAs(OPERATOR_USER);

        mockMvc.perform(post("/api/documents/{id}/approval/reject", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\": \"Pokusaj rejecta.\"}"))
                .andExpect(status().isForbidden());
    }
    @Test
    void approverDocumentListShowsOnlyReadyForApprovalDocuments() throws Exception {
        Long readyDoc = uploadPdf("Ready doc");
        Long completedDoc = uploadPdf("Completed doc");
        uploadPdf("Uploaded doc");

        markDocumentStatus(readyDoc, "READY_FOR_APPROVAL");
        markDocumentStatus(completedDoc, "COMPLETED");

        authenticateAs(APPROVER_USER);

        MvcResult result = mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode payload = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("payload");

        assertTrue(payload.isArray());
        assertEquals(1, payload.size());
        assertEquals(readyDoc, payload.get(0).path("id").asLong());
        assertEquals("READY_FOR_APPROVAL", payload.get(0).path("documentStatus").asText());
    }

    @Test
    void managerCanSearchAndFilterDocumentsByMultipleCriteria() throws Exception {
        Long targetDoc = uploadPdf("Target quarterly invoice");
        Long otherDoc = uploadPdf("Other quarterly invoice");

        markDocumentStatus(targetDoc, "READY_FOR_APPROVAL");
        markDocumentStatus(otherDoc, "COMPLETED");
        markDocumentUploadDate(targetDoc, LocalDateTime.of(2026, 5, 15, 10, 0));
        markDocumentUploadDate(otherDoc, LocalDateTime.of(2026, 4, 15, 10, 0));
        insertActiveTask(targetDoc, getUserId(APPROVER_USER));

        authenticateAs(MANAGER_USER);

        MvcResult filteredResult =
                mockMvc.perform(
                                get("/api/documents")
                                        .param("search", "Target")
                                        .param("documentStatus", "READY_FOR_APPROVAL")
                                        .param("documentType", "INVOICE")
                                        .param("createdFrom", "2026-05-01")
                                        .param("createdTo", "2026-05-31")
                                        .param(
                                                "assignedUserId",
                                                String.valueOf(getUserId(APPROVER_USER))))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode filteredPayload =
                objectMapper.readTree(filteredResult.getResponse().getContentAsString())
                        .path("payload");

        assertTrue(filteredPayload.isArray());
        assertEquals(1, filteredPayload.size());
        assertEquals(targetDoc, filteredPayload.get(0).path("id").asLong());

        MvcResult idSearchResult =
                mockMvc.perform(get("/api/documents").param("search", targetDoc.toString()))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode idSearchPayload =
                objectMapper.readTree(idSearchResult.getResponse().getContentAsString())
                        .path("payload");

        assertTrue(idSearchPayload.isArray());
        assertEquals(1, idSearchPayload.size());
        assertEquals(targetDoc, idSearchPayload.get(0).path("id").asLong());
    }

    @Test
    void reviewEndpointReturnsOnlyCompletedDocumentsForManagerAndAdmin() throws Exception {
        Long completedDoc = uploadPdf("Completed review doc");
        Long readyDoc = uploadPdf("Ready review doc");

        markDocumentStatus(completedDoc, "COMPLETED");
        markDocumentStatus(readyDoc, "READY_FOR_APPROVAL");

        authenticateAs(MANAGER_USER);

        MvcResult managerResult =
                mockMvc.perform(get("/api/approvals/completed"))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode managerPayload =
                objectMapper.readTree(managerResult.getResponse().getContentAsString())
                        .path("payload");

        assertTrue(managerPayload.isArray());
        assertEquals(1, managerPayload.size());
        assertEquals(completedDoc, managerPayload.get(0).path("id").asLong());
        assertEquals("COMPLETED", managerPayload.get(0).path("documentStatus").asText());

        authenticateAs(ADMIN_USER);

        MvcResult adminResult =
                mockMvc.perform(get("/api/approvals/completed"))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode adminPayload =
                objectMapper.readTree(adminResult.getResponse().getContentAsString())
                        .path("payload");

        assertTrue(adminPayload.isArray());
        assertEquals(1, adminPayload.size());
        assertEquals(completedDoc, adminPayload.get(0).path("id").asLong());
        assertEquals("COMPLETED", adminPayload.get(0).path("documentStatus").asText());
    }

    @Test
    void approverCannotAccessCompletedReviewEndpoint() throws Exception {
        authenticateAs(APPROVER_USER);

        mockMvc.perform(get("/api/approvals/completed"))
                .andExpect(status().isForbidden());
    }

    private Long uploadPdf(String name) throws Exception {
        authenticateAs(OPERATOR_USER);
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
                                        .param("documentType", "INVOICE")
                                        .param("name", name))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("payload").path("id").asLong();
    }

    private Long uploadAndSetReadyForApproval(String name) throws Exception {
        Long documentId = uploadPdf(name);
        authenticateAs(APPROVER_USER);
        jdbcTemplate.update(
                "UPDATE document SET document_status = 'READY_FOR_APPROVAL' WHERE id = ?",
                documentId);
        jdbcTemplate.update(
                "INSERT INTO status_history (document_id, old_status, new_status, action, changed_by_user_id, changed_at) "
                        + "VALUES (?, 'EXTRACTED', 'READY_FOR_APPROVAL', 'EXTRACTION_CONFIRMED', "
                        + "(SELECT id FROM app_user WHERE keycloak_user_id = ?), NOW())",
                documentId, APPROVER_USER);
        return documentId;
    }

    private Long uploadAndSetReadyForApprovalAsOperator(String name) throws Exception {
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
                                        .param("documentType", "INVOICE")
                                        .param("name", name))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        Long documentId = root.path("payload").path("id").asLong();

        jdbcTemplate.update(
                "UPDATE document SET document_status = 'READY_FOR_APPROVAL' WHERE id = ?",
                documentId);
        return documentId;
    }

    private void markDocumentStatus(Long documentId, String status) {
        jdbcTemplate.update("UPDATE document SET document_status = ? WHERE id = ?", status, documentId);
    }

    private void markDocumentUploadDate(Long documentId, LocalDateTime uploadDate) {
        jdbcTemplate.update(
                "UPDATE document SET upload_date = ? WHERE id = ?",
                uploadDate,
                documentId);
    }

    private void insertActiveTask(Long documentId, Long assignedUserId) {
        Long assignedByUserId = getUserId(MANAGER_USER);

        jdbcTemplate.update(
                """
                INSERT INTO workflow_task (
                    document_id,
                    assigned_user_id,
                    assigned_by_user_id,
                    task_type,
                    status,
                    due_date,
                    created_at
                ) VALUES (?, ?, ?, 'APPROVAL', 'OPEN', NULL, NOW())
                """,
                documentId,
                assignedUserId,
                assignedByUserId);
    }

    private Long getUserId(String keycloakUserId) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM app_user WHERE keycloak_user_id = ?",
                Long.class,
                keycloakUserId);
    }

    private void setupTestTenant() {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(
                        status -> {
                            CompanyEntity company = new CompanyEntity();
                            company.setName("Approval Test Co");
                            company.setAddress("Test Address");
                            company.setEmail("approval-test-" + System.nanoTime() + "@test.ba");
                            company.setRegistrationDate(LocalDateTime.now());
                            company.setStatus(CompanyStatus.ACTIVE);
                            company.setKeycloakGroupId("group-approval-test");
                            Long companyId = companyDAO.persist(company).getId();
                            UserEntity approver = new UserEntity();
                            approver.setCompanyId(companyId);
                            approver.setRoleId(roleService.getByName(RoleName.APPROVER).getId());
                            approver.setKeycloakUserId(APPROVER_USER);
                            approver.setFirstName("Test");
                            approver.setLastName("Approver");
                            approver.setEmail("approver@test.ba");
                            approver.setAccountStatus(AccountStatus.ACTIVE);
                            userDAO.persist(approver);
                            UserEntity manager = new UserEntity();
                            manager.setCompanyId(companyId);
                            manager.setRoleId(roleService.getByName(RoleName.MANAGER).getId());
                            manager.setKeycloakUserId(MANAGER_USER);
                            manager.setFirstName("Test");
                            manager.setLastName("Manager");
                            manager.setEmail("manager@test.ba");
                            manager.setAccountStatus(AccountStatus.ACTIVE);
                            userDAO.persist(manager);
                            UserEntity admin = new UserEntity();
                            admin.setCompanyId(companyId);
                            admin.setRoleId(roleService.getByName(RoleName.ADMIN).getId());
                            admin.setKeycloakUserId(ADMIN_USER);
                            admin.setFirstName("Test");
                            admin.setLastName("Admin");
                            admin.setEmail("admin@test.ba");
                            admin.setAccountStatus(AccountStatus.ACTIVE);
                            userDAO.persist(admin);
                            UserEntity operator = new UserEntity();
                            operator.setCompanyId(companyId);
                            operator.setRoleId(roleService.getByName(RoleName.OPERATOR).getId());
                            operator.setKeycloakUserId(OPERATOR_USER);
                            operator.setFirstName("Test");
                            operator.setLastName("Operator");
                            operator.setEmail("operator@test.ba");
                            operator.setAccountStatus(AccountStatus.ACTIVE);
                            userDAO.persist(operator);
                        });
    }

    private void authenticateAs(String keycloakUserId) {
        Jwt jwt =
                Jwt.withTokenValue("approval-test-token")
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
            return Files.createTempDirectory("docflow-approval-test-");
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private static void deleteChildren(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(p -> !p.equals(directory))
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.delete(p); }
                        catch (IOException ignored) {}
                    });
        }
    }

    private static void deleteRecursively(Path directory) throws IOException {
        if (!Files.exists(directory)) return;
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.delete(p); }
                        catch (IOException ignored) {}
                    });
        }
    }
}
