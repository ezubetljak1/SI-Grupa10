package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.audit.AuditLogResponse;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.service.audit.AuditLogService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/{id}/audit-log")
    public ApiResponse<List<AuditLogResponse>> getAuditLog(
            @PathVariable Long id
    ) {

        return new ApiResponse<>(
                "SUCCESS",
                auditLogService.getDocumentAuditLog(id)
        );
    }
}
