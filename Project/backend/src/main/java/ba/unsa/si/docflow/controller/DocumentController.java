package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.document.Document;
import ba.unsa.si.docflow.dto.document.DocumentCreateRequest;
import ba.unsa.si.docflow.dto.document.DocumentFileResponse;
import ba.unsa.si.docflow.dto.document.DocumentFilterRequest;
import ba.unsa.si.docflow.dto.document.DocumentUpdateRequest;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.PagedResponse;
import ba.unsa.si.docflow.service.document.DocumentService;

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

@RestController
@RequestMapping("/api/documents")
@AllArgsConstructor
@Tag(name = "Document API", description = "CRUD and upload endpoints for Document entity")
public class DocumentController {

    private final DocumentService documentService;

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
            @RequestParam Long companyId,
            @RequestParam Long createdByUserId,
            @RequestParam String documentType,
            @RequestParam(required = false) String name) {
        return documentService.upload(file, companyId, createdByUserId, documentType, name);
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
}
