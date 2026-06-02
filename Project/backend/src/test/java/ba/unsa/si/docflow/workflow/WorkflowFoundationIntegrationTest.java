package ba.unsa.si.docflow.workflow;

import static org.junit.jupiter.api.Assertions.*;

import ba.unsa.si.docflow.dao.*;
import ba.unsa.si.docflow.entity.*;
import ba.unsa.si.docflow.entity.enums.*;
import ba.unsa.si.docflow.service.workflow.DocumentStatusTransitionService;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WorkflowFoundationIntegrationTest {

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private DocumentDAO documentDAO;

    @Autowired private StatusHistoryDAO statusHistoryDAO;

    @Autowired private CommentDAO commentDAO;

    @Autowired private TaskDAO taskDAO;

    @Autowired private NotificationDAO notificationDAO;

    @Autowired private AuditLogDAO auditLogDAO;

    @Autowired private DocumentStatusTransitionService transitionService;

    @Autowired EntityManager entityManager;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM audit_log");
        jdbcTemplate.execute("DELETE FROM notification");
        jdbcTemplate.execute("DELETE FROM workflow_task");
        jdbcTemplate.execute("DELETE FROM status_history");
        jdbcTemplate.execute("DELETE FROM document_comment");
        jdbcTemplate.execute("DELETE FROM extraction_field");
        jdbcTemplate.execute("DELETE FROM extraction");
        jdbcTemplate.execute("DELETE FROM document");
    }

    @Test
    void recordInitialStatusThenCreatesStatusHistoryRow() {
        DocumentEntity document = persistDocument("Initial history test", DocumentStatus.UPLOADED);

        transitionService.recordInitialStatus(
                document,
                1L,
                StatusHistoryAction.DOCUMENT_UPLOADED,
                "Document uploaded during workflow foundation test.");

        entityManager.flush();
        entityManager.clear();
        
        Map<String, Object> row =
                jdbcTemplate.queryForMap(
                        "SELECT * FROM status_history WHERE document_id = ?", document.getId());

        assertEquals(document.getId(), ((Number) row.get("document_id")).longValue());
        assertNull(row.get("old_status"));
        assertEquals("UPLOADED", row.get("new_status"));
        assertEquals("DOCUMENT_UPLOADED", row.get("action"));
        assertEquals(1L, ((Number) row.get("changed_by_user_id")).longValue());
        assertNotNull(row.get("changed_at"));
    }

    @Test
    void changeStatusThenUpdatesDocumentAndCreatesStatusHistoryRow() {
        DocumentEntity document =
                persistDocument("Status transition test", DocumentStatus.UPLOADED);

        transitionService.changeStatus(
                document,
                DocumentStatus.NEEDS_CORRECTION,
                StatusHistoryAction.DOCUMENT_RETURNED_FOR_CORRECTION,
                2L,
                null,
                "Returned for correction during workflow foundation test.");

        entityManager.flush();
        entityManager.clear();

        String documentStatus =
                jdbcTemplate.queryForObject(
                        "SELECT document_status FROM document WHERE id = ?",
                        String.class,
                        document.getId());

        Map<String, Object> history =
                jdbcTemplate.queryForMap(
                        "SELECT * FROM status_history WHERE document_id = ?", document.getId());

        assertEquals("NEEDS_CORRECTION", documentStatus);
        assertEquals("UPLOADED", history.get("old_status"));
        assertEquals("NEEDS_CORRECTION", history.get("new_status"));
        assertEquals("DOCUMENT_RETURNED_FOR_CORRECTION", history.get("action"));
        assertEquals(2L, ((Number) history.get("changed_by_user_id")).longValue());
    }

    @Test
    void workflowSkeletonEntitiesCanBePersisted() {
        DocumentEntity document =
                persistDocument("Workflow skeleton test", DocumentStatus.EXTRACTED);

        CommentEntity comment = new CommentEntity();
        comment.setDocument(document);
        comment.setUserId(1L);
        comment.setType(CommentType.GENERAL);
        comment.setContent("General workflow comment.");
        commentDAO.persist(comment);

        StatusHistoryEntity statusHistory = new StatusHistoryEntity();
        statusHistory.setDocument(document);
        statusHistory.setOldStatus(DocumentStatus.UPLOADED);
        statusHistory.setNewStatus(DocumentStatus.EXTRACTED);
        statusHistory.setAction(StatusHistoryAction.EXTRACTION_COMPLETED);
        statusHistory.setChangedByUserId(1L);
        statusHistory.setComment(comment);
        statusHistoryDAO.persist(statusHistory);

        TaskEntity task = new TaskEntity();
        task.setDocument(document);
        task.setAssignedUserId(10L);
        task.setAssignedByUserId(1L);
        task.setTaskType(TaskType.EXTRACTION);
        task.setStatus(TaskStatus.OPEN);
        task.setDueDate(LocalDateTime.now().plusDays(1));
        taskDAO.persist(task);

        NotificationEntity notification = new NotificationEntity();
        notification.setUserId(10L);
        notification.setDocumentId(document.getId());
        notification.setCommentId(comment.getId());
        notification.setType(NotificationType.DOCUMENT_ASSIGNED);
        notification.setTitle("Document assigned");
        notification.setText("A document has been assigned to you.");
        notification.setActionUrl("/tasks/my");
        notificationDAO.persist(notification);

        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setDocument(document);
        auditLog.setUserId(1L);
        auditLog.setAction(AuditAction.DOCUMENT_ASSIGNED);
        auditLog.setDetails("{\"assignedUserId\":10}");
        auditLogDAO.persist(auditLog);

        assertEquals(1, count("document_comment"));
        assertEquals(1, count("status_history"));
        assertEquals(1, count("workflow_task"));
        assertEquals(1, count("notification"));
        assertEquals(1, count("audit_log"));
    }

    private DocumentEntity persistDocument(String name, DocumentStatus status) {
        DocumentEntity document = new DocumentEntity();
        document.setCompanyId(1L);
        document.setCreatedBy(1L);
        document.setName(name);
        document.setFileType("application/pdf");
        document.setDocumentType(DocumentType.INVOICE);
        document.setStoragePath("company-1/" + name.toLowerCase().replace(" ", "-") + ".pdf");
        document.setUploadDate(LocalDateTime.now());
        document.setFileSize(123L);
        document.setDocumentStatus(status);

        return documentDAO.persist(document);
    }

    private int count(String tableName) {
        Integer count =
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);

        return count == null ? 0 : count;
    }
}
