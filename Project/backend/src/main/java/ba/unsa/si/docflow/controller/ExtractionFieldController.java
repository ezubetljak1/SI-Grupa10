package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.extraction.UpdateExtractionFieldRequest;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.service.extraction.ExtractionService;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/extractions/{extractionId}/fields")
@AllArgsConstructor
@Tag(name = "Extraction Fields API", description = "Endpoints for extracted document fields")
public class ExtractionFieldController {

    private final ExtractionService extractionService;

    @GetMapping
    public ApiResponse findFieldsByExtractionId(@PathVariable Long extractionId) {
        return extractionService.findFieldsByExtractionId(extractionId);
    }

    @PatchMapping("/{fieldId}")
    public ApiResponse updateField(
            @PathVariable Long extractionId,
            @PathVariable Long fieldId,
            @RequestBody UpdateExtractionFieldRequest request) {
        return extractionService.updateField(extractionId, fieldId, request);
    }
}
