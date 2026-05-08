package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.service.extraction.ExtractionService;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents/{documentId}/extraction")
@AllArgsConstructor
@Tag(name = "Document Extraction API", description = "OCR and AI extraction endpoints")
public class ExtractionController {

    private final ExtractionService extractionService;

    @PostMapping
    public ApiResponse process(@PathVariable Long documentId) {
        return extractionService.process(documentId);
    }

    @GetMapping
    public ApiResponse findByDocumentId(@PathVariable Long documentId) {
        return extractionService.findByDocumentId(documentId);
    }

    @PostMapping("/retry")
    public ApiResponse retry(@PathVariable Long documentId) {
        return extractionService.retry(documentId);
    }

    @GetMapping("/fields")
    public ApiResponse findFieldsByDocumentId(@PathVariable Long documentId) {
        return extractionService.findFieldsByDocumentId(documentId);
    }

    @PostMapping("/confirm")
    public ApiResponse confirmExtraction(@PathVariable Long documentId) {
        return extractionService.confirmExtraction(documentId);
    }
}
