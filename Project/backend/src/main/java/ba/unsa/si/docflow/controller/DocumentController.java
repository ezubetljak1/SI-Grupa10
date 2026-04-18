package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.document.Document;
import ba.unsa.si.docflow.dto.document.DocumentCreateRequest;
import ba.unsa.si.docflow.dto.document.DocumentFilterRequest;
import ba.unsa.si.docflow.dto.document.DocumentUpdateRequest;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.PagedResponse;
import ba.unsa.si.docflow.service.document.DocumentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@AllArgsConstructor
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

    @PutMapping("/{id}")
    public ApiResponse<Document> update(@PathVariable Long id, @Valid @RequestBody DocumentUpdateRequest request) {
        request.setId(id);
        return documentService.update(request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return documentService.delete(id);
    }
}