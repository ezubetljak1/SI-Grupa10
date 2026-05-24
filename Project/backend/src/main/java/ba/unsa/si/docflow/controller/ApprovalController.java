package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.dto.document.Document;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.mapper.DocumentMapper;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.security.CurrentUserService;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@AllArgsConstructor
@Tag(name = "Approval API", description = "Endpoints for approval workflow management")
public class ApprovalController {

    private final DocumentDAO documentDAO;
    private final DocumentMapper documentMapper;
    private final CurrentUserService currentUserService;

    /**
     * Returns all documents with status READY_FOR_APPROVAL for the current user's company.
     * Accessible by ADMIN, MANAGER and APPROVER roles.
     */
    @GetMapping("/pending")
    public ApiResponse<List<Document>> getPendingApprovals() {
        currentUserService.requireAnyRole(RoleName.ADMIN, RoleName.MANAGER, RoleName.APPROVER);

        Long companyId = currentUserService.getCurrentCompanyId();

        List<DocumentEntity> pendingDocuments =
                documentDAO.findByStatusAndCompanyId(DocumentStatus.READY_FOR_APPROVAL, companyId);

        List<Document> dtos = pendingDocuments.stream()
                .map(documentMapper::entityToDto)
                .toList();

        return new ApiResponse<>("OK", dtos);
    }
}
