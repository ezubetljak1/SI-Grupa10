package ba.unsa.si.docflow.workflow;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ba.unsa.si.docflow.dao.*;
import ba.unsa.si.docflow.entity.*;
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
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TaskManagementIntegrationTest {

    private static final String ADMIN_KC = "task-admin";
    private static final String MANAGER_KC = "task-manager";
    private static final String OPERATOR_KC = "task-operator";
    private static final String APPROVER_KC = "task-approver";
    private static final String OTHER_OPERATOR_KC = "task-other-operator";
    private static final String SECOND_OPERATOR_KC = "task-second-operator";

    @Autowired private MockMvc mockMvc;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private CompanyDAO companyDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private DocumentDAO documentDAO;
    @Autowired private RoleService roleService;
    @Autowired private PlatformTransactionManager transactionManager;
    @Autowired private ExtractionDAO extractionDAO;
    @Autowired private ExtractionFieldDAO extractionFieldDAO;

    private Long documentId;
    private Long operatorId;
    private Long approverId;
    private Long otherCompanyOperatorId;
    private Long secondOperatorId;

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
    void adminCanAssignTaskAndCreatesNotificationAndAuditLog() throws Exception {
        authenticateAs(ADMIN_KC);

        mockMvc.perform(
                        post("/api/documents/{id}/tasks/assign", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "assignedUserId": %d,
                                          "taskType": "CORRECTION"
                                        }
                                        """
                                                .formatted(operatorId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo("SUCCESS")))
                .andExpect(jsonPath("$.payload.assignedUserId", equalTo(operatorId.intValue())))
                .andExpect(jsonPath("$.payload.taskType", equalTo("CORRECTION")))
                .andExpect(jsonPath("$.payload.status", equalTo("OPEN")));

        assertCount("notification", 1);
        assertCount("audit_log", 1);
    }

    @Test
    void duplicateActiveTaskForSameDocumentAndTypeIsBlocked() throws Exception {
        authenticateAs(ADMIN_KC);

        assignCorrectionTask(operatorId);

        mockMvc.perform(
                        post("/api/documents/{id}/tasks/assign", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "assignedUserId": %d,
                                          "taskType": "CORRECTION"
                                        }
                                        """
                                                .formatted(operatorId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code", equalTo("TASK_DUPLICATE_ACTIVE")));
    }

    @Test
    void taskAssigneeMustHaveExpectedRoleAndCompany() throws Exception {
        authenticateAs(ADMIN_KC);

        mockMvc.perform(
                        post("/api/documents/{id}/tasks/assign", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "assignedUserId": %d,
                                          "taskType": "APPROVAL"
                                        }
                                        """
                                                .formatted(operatorId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code", equalTo("TASK_ASSIGNEE_ROLE_INVALID")));

        mockMvc.perform(
                        post("/api/documents/{id}/tasks/assign", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "assignedUserId": %d,
                                          "taskType": "CORRECTION"
                                        }
                                        """
                                                .formatted(otherCompanyOperatorId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", equalTo("NOT_FOUND")));
    }

    @Test
    void assignedUserCanViewStartTaskButCannotCompleteBeforeWorkflowAction() throws Exception {
        authenticateAs(ADMIN_KC);
        Long taskId = assignCorrectionTask(operatorId);

        authenticateAs(OPERATOR_KC);

        mockMvc.perform(get("/api/tasks/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", hasSize(1)))
                .andExpect(jsonPath("$.payload[0].id", equalTo(taskId.intValue())));

        mockMvc.perform(patch("/api/tasks/{id}/start", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.status", equalTo("IN_PROGRESS")));

        mockMvc.perform(patch("/api/tasks/{id}/complete", taskId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code", equalTo("TASK_DOCUMENT_STATUS_NOT_COMPLETED")));
    }

    @Test
    void assignedUserCanCompleteTaskAfterDocumentReachesExpectedStatus() throws Exception {
        authenticateAs(ADMIN_KC);
        Long taskId = assignCorrectionTask(operatorId);
        setDocumentStatus(DocumentStatus.READY_FOR_APPROVAL);

        authenticateAs(OPERATOR_KC);

        mockMvc.perform(patch("/api/tasks/{id}/complete", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.status", equalTo("COMPLETED")))
                .andExpect(jsonPath("$.payload.completedByUserId", equalTo(operatorId.intValue())));
    }

    @Test
    void dueDateCannotBeBeforeCurrentDay() throws Exception {
        authenticateAs(ADMIN_KC);

        mockMvc.perform(
                        post("/api/documents/{id}/tasks/assign", documentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {
                                          "assignedUserId": %d,
                                          "taskType": "CORRECTION",
                                          "dueDate": "%s"
                                        }
                                        """
                                                .formatted(
                                                        operatorId,
                                                        LocalDateTime.now()
                                                                .minusDays(1)
                                                                .withHour(12)
                                                                .withMinute(0)
                                                                .withSecond(0)
                                                                .withNano(0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code", equalTo("TASK_DUE_DATE_INVALID")));
    }

    @Test
    void sameCompanyUserCanViewDocumentTasksForAssignmentBanner() throws Exception {
        authenticateAs(ADMIN_KC);
        Long taskId = assignCorrectionTask(operatorId);

        authenticateAs(SECOND_OPERATOR_KC);

        mockMvc.perform(get("/api/documents/{id}/tasks", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", hasSize(1)))
                .andExpect(jsonPath("$.payload[0].id", equalTo(taskId.intValue())))
                .andExpect(jsonPath("$.payload[0].assignedUserId", equalTo(operatorId.intValue())));
    }

    @Test
    void managerCanViewAllTasksAndCancelActiveTask() throws Exception {
        authenticateAs(ADMIN_KC);
        Long taskId = assignApprovalTask(approverId);

        authenticateAs(MANAGER_KC);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", hasSize(1)));

        mockMvc.perform(patch("/api/tasks/{id}/cancel", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.status", equalTo("CANCELLED")));
    }

    @Test
    void operatorCannotViewAllTasks() throws Exception {
        authenticateAs(ADMIN_KC);
        assignCorrectionTask(operatorId);

        authenticateAs(OPERATOR_KC);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    @Test
    void nonAssigneeCannotStartTask() throws Exception {
        authenticateAs(ADMIN_KC);
        Long taskId = assignCorrectionTask(operatorId);

        authenticateAs(SECOND_OPERATOR_KC);

        mockMvc.perform(patch("/api/tasks/{id}/start", taskId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    @Test
    void nonAssigneeCannotCompleteTask() throws Exception {
        authenticateAs(ADMIN_KC);
        Long taskId = assignCorrectionTask(operatorId);

        authenticateAs(SECOND_OPERATOR_KC);

        mockMvc.perform(patch("/api/tasks/{id}/complete", taskId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    @Test
    void operatorCannotCancelTask() throws Exception {
        authenticateAs(ADMIN_KC);
        Long taskId = assignCorrectionTask(operatorId);

        authenticateAs(OPERATOR_KC);

        mockMvc.perform(patch("/api/tasks/{id}/cancel", taskId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    @Test
    void nonAssigneeCannotConfirmExtractionWhenExtractionTaskIsAssignedToAnotherOperator()
            throws Exception {
        createValidInvoiceExtraction(DocumentStatus.EXTRACTED);

        authenticateAs(ADMIN_KC);
        assignTask(operatorId, "EXTRACTION");

        authenticateAs(SECOND_OPERATOR_KC);

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    @Test
    void assignedOperatorCanConfirmExtractionWhenExtractionTaskIsAssignedToThem() throws Exception {
        createValidInvoiceExtraction(DocumentStatus.EXTRACTED);

        authenticateAs(ADMIN_KC);
        assignTask(operatorId, "EXTRACTION");

        authenticateAs(OPERATOR_KC);

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo("OK")));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals("READY_FOR_APPROVAL", documentStatus);
        assertTaskStatus("EXTRACTION", "COMPLETED");
    }

    @Test
    void nonAssigneeCannotReconfirmExtractionWhenCorrectionTaskIsAssignedToAnotherOperator()
            throws Exception {
        createValidInvoiceExtraction(DocumentStatus.NEEDS_CORRECTION);

        authenticateAs(ADMIN_KC);
        assignCorrectionTask(operatorId);

        authenticateAs(SECOND_OPERATOR_KC);

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", equalTo("FORBIDDEN")));
    }

    @Test
    void assignedOperatorCanReconfirmExtractionWhenCorrectionTaskIsAssignedToThem()
            throws Exception {
        createValidInvoiceExtraction(DocumentStatus.NEEDS_CORRECTION);

        authenticateAs(ADMIN_KC);
        assignCorrectionTask(operatorId);

        authenticateAs(OPERATOR_KC);

        mockMvc.perform(post("/api/documents/{documentId}/extraction/confirm", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo("OK")));

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        documentId);

        assertEquals("READY_FOR_APPROVAL", documentStatus);
        assertTaskStatus("CORRECTION", "COMPLETED");
    }

    private void setupData() {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(
                        status -> {
                            CompanyEntity company =
                                    createCompany("Task Test Company", "task@test.ba");
                            CompanyEntity otherCompany =
                                    createCompany("Other Task Company", "other-task@test.ba");

                            Long companyId = company.getId();
                            Long otherCompanyId = otherCompany.getId();

                            createUser(companyId, ADMIN_KC, RoleName.ADMIN, "Task", "Admin");
                            createUser(companyId, MANAGER_KC, RoleName.MANAGER, "Task", "Manager");
                            operatorId =
                                    createUser(
                                            companyId,
                                            OPERATOR_KC,
                                            RoleName.OPERATOR,
                                            "Task",
                                            "Operator");

                            secondOperatorId =
                                    createUser(
                                            companyId,
                                            SECOND_OPERATOR_KC,
                                            RoleName.OPERATOR,
                                            "Second",
                                            "Operator");

                            approverId =
                                    createUser(
                                            companyId,
                                            APPROVER_KC,
                                            RoleName.APPROVER,
                                            "Task",
                                            "Approver");
                            otherCompanyOperatorId =
                                    createUser(
                                            otherCompanyId,
                                            OTHER_OPERATOR_KC,
                                            RoleName.OPERATOR,
                                            "Other",
                                            "Operator");

                            DocumentEntity document = new DocumentEntity();
                            document.setCompanyId(companyId);
                            document.setCreatedBy(operatorId);
                            document.setName("Task workflow document");
                            document.setFileType("application/pdf");
                            document.setDocumentType(DocumentType.INVOICE);
                            document.setStoragePath("company-" + companyId + "/task.pdf");
                            document.setUploadDate(LocalDateTime.now());
                            document.setFileSize(123L);
                            document.setDocumentStatus(DocumentStatus.EXTRACTED);

                            documentId = documentDAO.persist(document).getId();
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

    private Long createUser(
            Long companyId,
            String keycloakUserId,
            RoleName role,
            String firstName,
            String lastName) {
        UserEntity user = new UserEntity();
        user.setCompanyId(companyId);
        user.setRoleId(roleService.getByName(role).getId());
        user.setKeycloakUserId(keycloakUserId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(keycloakUserId + "@test.ba");
        user.setAccountStatus(AccountStatus.ACTIVE);

        return userDAO.persist(user).getId();
    }

    private Long assignCorrectionTask(Long assignedUserId) throws Exception {
        return assignTask(assignedUserId, "CORRECTION");
    }

    private Long assignApprovalTask(Long assignedUserId) throws Exception {
        return assignTask(assignedUserId, "APPROVAL");
    }

    private Long assignTask(Long assignedUserId, String taskType) throws Exception {
        String response =
                mockMvc.perform(
                                post("/api/documents/{id}/tasks/assign", documentId)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                """
                                                {
                                                  "assignedUserId": %d,
                                                  "taskType": "%s"
                                                }
                                                """
                                                        .formatted(assignedUserId, taskType)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Number id = com.jayway.jsonpath.JsonPath.read(response, "$.payload.id");
        return id.longValue();
    }

    private void authenticateAs(String keycloakUserId) {
        Jwt jwt =
                Jwt.withTokenValue("task-test-token")
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

    private void assertCount(String tableName, int expected) {
        Integer count =
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
        assertEquals(expected, count);
    }

    private void createValidInvoiceExtraction(DocumentStatus documentStatus) {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(
                        status -> {
                            DocumentEntity doc = setDocumentStatusInCurrentTransaction(documentStatus);

                            ExtractionEntity extraction = new ExtractionEntity();
                            extraction.setDocument(doc);
                            extraction.setRawJson("{\"test\":true}");
                            extraction.setExtractionTime(LocalDateTime.now());

                            ExtractionEntity savedExtraction = extractionDAO.persist(extraction);

                            persistExtractionField(
                                    savedExtraction, "supplier_name", "Test Supplier d.o.o.");
                            persistExtractionField(savedExtraction, "invoice_id", "INV-001");
                            persistExtractionField(savedExtraction, "invoice_date", "06.05.2026");
                            persistExtractionField(savedExtraction, "total_amount", "117.00");
                            persistExtractionField(savedExtraction, "currency", "EUR");
                        });
    }

    private void persistExtractionField(
            ExtractionEntity extraction, String fieldName, String value) {
        ExtractionFieldEntity field = new ExtractionFieldEntity();
        field.setExtraction(extraction);
        field.setFieldName(fieldName);
        field.setValue(value);
        field.setConfidence(new BigDecimal("0.95"));
        field.setCorrected(false);
        field.setPlaceholder(false);
        field.setManual(false);

        extractionFieldDAO.persist(field);
    }

    private void setDocumentStatus(DocumentStatus documentStatus) {
        new TransactionTemplate(transactionManager)
                .executeWithoutResult(status -> setDocumentStatusInCurrentTransaction(documentStatus));
    }

    private DocumentEntity setDocumentStatusInCurrentTransaction(DocumentStatus documentStatus) {
        DocumentEntity doc = documentDAO.findByPK(documentId);
        doc.setDocumentStatus(documentStatus);
        return documentDAO.merge(doc);
    }

    private void assertTaskStatus(String taskType, String expectedStatus) {
        String taskStatus =
                jdbcTemplate.queryForObject(
                        "SELECT status FROM workflow_task WHERE document_id = ? AND task_type = ?",
                        String.class,
                        documentId,
                        taskType);

        assertEquals(expectedStatus, taskStatus);
    }
}
