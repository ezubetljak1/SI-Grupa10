package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.document.*;
import ba.unsa.si.docflow.dto.task.AssignTaskRequest;
import ba.unsa.si.docflow.dto.task.TaskResponse;
import ba.unsa.si.docflow.dto.workflow.CreateCommentRequest;
import ba.unsa.si.docflow.dto.workflow.CommentResponse;
import ba.unsa.si.docflow.dto.workflow.StatusHistoryResponse;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.PagedResponse;
import ba.unsa.si.docflow.service.document.DocumentService;
import ba.unsa.si.docflow.service.task.TaskService;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@AllArgsConstructor
@Tag(name = "Document API", description = "CRUD and upload endpoints for Document entity")
public class DocumentController {

    private final DocumentService documentService;
    private final TaskService taskService;

    @GetMapping
    public PagedResponse<Document> find(@ParameterObject DocumentFilterRequest request) {
        return documentService.find(request);
    }

    @GetMapping("/{id}")
    public ApiResponse<Document> findById(@PathVariable Long id) {
        return documentService.findById(id);
    }

    @PostMapping
    public ApiResponse<Document> create(@Valid @RequestBody DocumentCreateRequest request) {
        return documentService.create(request);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Document> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam String documentType,
            @RequestParam(required = false) String name) {
        return documentService.upload(file, documentType, name);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        DocumentFileResponse fileResponse = documentService.downloadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResponse.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileResponse.getFileName() + "\"")
                .body(fileResponse.getResource());
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> previewFile(@PathVariable Long id) {
        DocumentFileResponse fileResponse = documentService.downloadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResponse.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fileResponse.getFileName() + "\"")
                .body(fileResponse.getResource());
    }

    @PutMapping("/{id}")
    public ApiResponse<Document> update(
            @PathVariable Long id, @Valid @RequestBody DocumentUpdateRequest request) {
        request.setId(id);
        return documentService.update(request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return documentService.delete(id);
    }

    @PatchMapping("/{id}/classification")
    public ApiResponse<Document> confirmDocumentType(
            @PathVariable Long id, @Valid @RequestBody ConfirmDocumentTypeRequest request) {
        return documentService.confirmDocumentType(id, request);
    }

    @PostMapping("/{id}/approval/approve")
    public ApiResponse<Document> approveDocument(
            @PathVariable Long id, @Valid @RequestBody CreateCommentRequest request) {
        return documentService.approveDocument(id, request);
    }

    @PostMapping("/{id}/approval/reject")
    public ApiResponse<Document> rejectDocument(
            @PathVariable Long id, @Valid @RequestBody CreateCommentRequest request) {
        return documentService.rejectDocument(id, request);
    }

    @PostMapping("/{id}/approval/correction")
    public ApiResponse<Document> returnDocumentForCorrection(
            @PathVariable Long id, @Valid @RequestBody CreateCommentRequest request) {
        return documentService.returnDocumentForCorrection(id, request);
    }

    @GetMapping("/{id}/status-history")
    public ApiResponse<List<StatusHistoryResponse>> getStatusHistory(@PathVariable Long id) {
        return documentService.getStatusHistory(id);
    }

    @GetMapping("/{id}/comments")
    public ApiResponse<List<CommentResponse>> getComments(@PathVariable Long id) {
        return documentService.getComments(id);
    }

    @PostMapping("/{id}/comments")
    public ApiResponse<CommentResponse> createComment(
            @PathVariable Long id, @Valid @RequestBody CreateCommentRequest request) {
        return documentService.createComment(id, request);
    }

    @PostMapping("/{id}/tasks/assign")
    public ApiResponse<TaskResponse> assignTask(
            @PathVariable Long id, @Valid @RequestBody AssignTaskRequest request) {
        return new ApiResponse<>("SUCCESS", taskService.assign(id, request));
    }

    @GetMapping("/{id}/tasks")
    public ApiResponse<List<TaskResponse>> getDocumentTasks(@PathVariable Long id) {
        return new ApiResponse<>("SUCCESS", taskService.findByDocument(id));
    }
}
