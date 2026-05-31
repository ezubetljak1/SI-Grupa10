package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.extraction.CreateExtractionFieldRequest;
import ba.unsa.si.docflow.dto.extraction.ExtractionFieldResponse;
import ba.unsa.si.docflow.dto.extraction.UpdateExtractionFieldRequest;

import jakarta.validation.Valid;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.service.extraction.ExtractionService;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extractions/{extractionId}/fields")
@AllArgsConstructor
@Tag(name = "Extraction Fields API", description = "Endpoints for extracted document fields")
public class ExtractionFieldController {

    private final ExtractionService extractionService;

    @GetMapping
    public ApiResponse<List<ExtractionFieldResponse>> findFieldsByExtractionId(
            @PathVariable Long extractionId) {
        return extractionService.findFieldsByExtractionId(extractionId);
    }

    @PatchMapping("/{fieldId}")
    public ApiResponse<ExtractionFieldResponse> updateField(
            @PathVariable Long extractionId,
            @PathVariable Long fieldId,
            @RequestBody UpdateExtractionFieldRequest request) {
        return extractionService.updateField(extractionId, fieldId, request);
    }

    @PostMapping
    public ApiResponse<ExtractionFieldResponse> addField(
            @PathVariable Long extractionId,
            @Valid @RequestBody CreateExtractionFieldRequest request) {
        return extractionService.addField(extractionId, request);
    }
}
