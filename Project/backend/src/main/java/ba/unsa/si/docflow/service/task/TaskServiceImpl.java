package ba.unsa.si.docflow.service.task;

import ba.unsa.si.docflow.dao.NotificationDAO;
import ba.unsa.si.docflow.dao.TaskDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.task.AssignTaskRequest;
import ba.unsa.si.docflow.dto.task.TaskResponse;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.NotificationEntity;
import ba.unsa.si.docflow.entity.RoleEntity;
import ba.unsa.si.docflow.entity.TaskEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.AuditAction;
import ba.unsa.si.docflow.entity.enums.NotificationType;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.entity.enums.TaskStatus;
import ba.unsa.si.docflow.entity.enums.TaskType;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.mapper.TaskMapper;
import ba.unsa.si.docflow.response.ValidationErrors;
import ba.unsa.si.docflow.security.CurrentUser;
import ba.unsa.si.docflow.security.CurrentUserService;
import ba.unsa.si.docflow.service.audit.AuditLogService;
import ba.unsa.si.docflow.service.document.DocumentValidation;
import ba.unsa.si.docflow.service.role.RoleService;
import ba.unsa.si.docflow.service.security.WorkflowPermissionService;
import ba.unsa.si.docflow.service.user.UserValidation;

import lombok.AllArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskDAO taskDAO;
    private final UserDAO userDAO;
    private final NotificationDAO notificationDAO;
    private final DocumentValidation documentValidation;
    private final UserValidation userValidation;
    private final RoleService roleService;
    private final CurrentUserService currentUserService;
    private final WorkflowPermissionService workflowPermissionService;
    private final AuditLogService auditLogService;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse assign(Long documentId, AssignTaskRequest request) {
        CurrentUser currentUser = currentUserService.getCurrentUser();
        DocumentEntity document =
                documentValidation.validateExistsInCompany(documentId, currentUser.companyId());

        workflowPermissionService.requireCanAssignTask(document);

        UserEntity assignee =
                userValidation.validateExistsInCompany(request.getAssignedUserId(), currentUser.companyId());
        validateAssignee(request, assignee);
        validateNoDuplicateActiveTask(documentId, request.getTaskType());

        TaskEntity task = new TaskEntity();
        task.setDocument(document);
        task.setAssignedUserId(assignee.getId());
        task.setAssignedByUserId(currentUser.userId());
        task.setTaskType(request.getTaskType());
        task.setStatus(TaskStatus.OPEN);
        task.setDueDate(request.getDueDate());

        TaskEntity saved = taskDAO.persist(task);
        createAssignmentNotification(saved, assignee);
        auditLogService.log(
                document,
                currentUser.userId(),
                AuditAction.DOCUMENT_ASSIGNED,
                String.format(
                        "{\"taskId\":%d,\"assignedUserId\":%d,\"taskType\":\"%s\"}",
                        saved.getId(), assignee.getId(), saved.getTaskType()));

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> findMyTasks() {
        Long userId = currentUserService.getCurrentUserId();
        return taskMapper.entitiesToDtos(taskDAO.findByAssignedUserId(userId), this::resolveUserName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        CurrentUser currentUser = currentUserService.getCurrentUser();
        if (!workflowPermissionService.canViewAllTasks()) {
            throw new AccessDeniedException("You do not have permission to view all tasks.");
        }

        return taskMapper.entitiesToDtos(
                taskDAO.findByCompanyId(currentUser.companyId()), this::resolveUserName);
    }

    @Override
    public TaskResponse start(Long id) {
        CurrentUser currentUser = currentUserService.getCurrentUser();
        TaskEntity task = validateTaskVisible(id, currentUser);
        requireAssignedUser(task, currentUser);

        if (task.getStatus() != TaskStatus.OPEN) {
            throwValidation("TASK_START_INVALID_STATUS", "Only open tasks can be started.");
        }

        task.setStatus(TaskStatus.IN_PROGRESS);
        TaskEntity updated = taskDAO.merge(task);
        auditLogService.log(
                task.getDocument(),
                currentUser.userId(),
                AuditAction.TASK_STARTED,
                String.format("{\"taskId\":%d,\"taskType\":\"%s\"}", task.getId(), task.getTaskType()));

        return toResponse(updated);
    }

    @Override
    public TaskResponse complete(Long id) {
        CurrentUser currentUser = currentUserService.getCurrentUser();
        TaskEntity task = validateTaskVisible(id, currentUser);
        requireAssignedUser(task, currentUser);

        if (task.getStatus() != TaskStatus.OPEN && task.getStatus() != TaskStatus.IN_PROGRESS) {
            throwValidation("TASK_COMPLETE_INVALID_STATUS", "Only active tasks can be completed.");
        }

        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setCompletedByUserId(currentUser.userId());
        TaskEntity updated = taskDAO.merge(task);
        auditLogService.log(
                task.getDocument(),
                currentUser.userId(),
                AuditAction.TASK_COMPLETED,
                String.format("{\"taskId\":%d,\"taskType\":\"%s\"}", task.getId(), task.getTaskType()));

        return toResponse(updated);
    }

    @Override
    public TaskResponse cancel(Long id) {
        CurrentUser currentUser = currentUserService.getCurrentUser();
        TaskEntity task = validateTaskVisible(id, currentUser);
        workflowPermissionService.requireCanAssignTask(task.getDocument());

        if (task.getStatus() == TaskStatus.COMPLETED || task.getStatus() == TaskStatus.CANCELLED) {
            throwValidation("TASK_CANCEL_INVALID_STATUS", "Only active tasks can be cancelled.");
        }

        task.setStatus(TaskStatus.CANCELLED);
        task.setCompletedAt(LocalDateTime.now());
        task.setCompletedByUserId(currentUser.userId());
        TaskEntity updated = taskDAO.merge(task);
        auditLogService.log(
                task.getDocument(),
                currentUser.userId(),
                AuditAction.TASK_CANCELLED,
                String.format("{\"taskId\":%d,\"taskType\":\"%s\"}", task.getId(), task.getTaskType()));

        return toResponse(updated);
    }

    private void validateAssignee(AssignTaskRequest request, UserEntity assignee) {
        if (assignee.getAccountStatus() == AccountStatus.INACTIVE) {
            throwValidation("TASK_ASSIGNEE_INACTIVE", "Task assignee must be an active user.");
        }

        RoleEntity role = roleService.getById(assignee.getRoleId());
        RoleName expectedRole = expectedRole(request.getTaskType());
        if (role.getName() != expectedRole) {
            throwValidation(
                    "TASK_ASSIGNEE_ROLE_INVALID",
                    "Selected user does not have the required role for this task type.");
        }
    }

    private RoleName expectedRole(TaskType taskType) {
        return switch (taskType) {
            case EXTRACTION, CORRECTION -> RoleName.OPERATOR;
            case APPROVAL -> RoleName.APPROVER;
        };
    }

    private void validateNoDuplicateActiveTask(Long documentId, TaskType taskType) {
        if (taskDAO.findActiveByDocumentIdAndTaskType(documentId, taskType) != null) {
            throwValidation(
                    "TASK_DUPLICATE_ACTIVE",
                    "An active task of this type already exists for this document.");
        }
    }

    private TaskEntity validateTaskVisible(Long id, CurrentUser currentUser) {
        TaskEntity task = taskDAO.findByPK(id);
        if (task == null) {
            throw new ApiNotFoundException("Task not found.");
        }

        workflowPermissionService.requireSameCompany(task.getDocument().getCompanyId());

        if (!task.getAssignedUserId().equals(currentUser.userId())
                && !workflowPermissionService.canViewAllTasks()) {
            throw new AccessDeniedException("You do not have permission to access this task.");
        }

        return task;
    }

    private void requireAssignedUser(TaskEntity task, CurrentUser currentUser) {
        if (!task.getAssignedUserId().equals(currentUser.userId())) {
            throw new AccessDeniedException("Only the assigned user can perform this task action.");
        }
    }

    private void createAssignmentNotification(TaskEntity task, UserEntity assignee) {
        NotificationEntity notification = new NotificationEntity();
        notification.setUserId(assignee.getId());
        notification.setDocument(task.getDocument());
        notification.setType(NotificationType.DOCUMENT_ASSIGNED);
        notification.setTitle("New task assigned");
        notification.setText(
                "You have been assigned a "
                        + task.getTaskType().name().toLowerCase()
                        + " task for document "
                        + task.getDocument().getName()
                        + ".");
        notification.setActionUrl("/tasks/my");
        notificationDAO.persist(notification);
    }

    private TaskResponse toResponse(TaskEntity task) {
        return taskMapper.entityToDto(task, this::resolveUserName);
    }

    private String resolveUserName(Long userId) {
        UserEntity user = userDAO.findByPK(userId);
        if (user == null) {
            return "User #" + userId;
        }

        return user.getFirstName() + " " + user.getLastName();
    }

    private void throwValidation(String code, String message) {
        ValidationErrors errors = new ValidationErrors();
        errors.add(code, message);
        throw new ApiValidationException(errors);
    }
}
