package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.document.DocumentFileResponse;
import ba.unsa.si.docflow.dto.xml.XmlOutputResponse;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.service.xml.XmlOutputService;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents/{documentId}/xml-output")
@AllArgsConstructor
@Tag(name = "XML Output API", description = "XML generation, preview and download endpoints")
public class XmlOutputController {

    private final XmlOutputService xmlOutputService;

    @PostMapping
    public ApiResponse<XmlOutputResponse> generate(@PathVariable Long documentId) {

        return xmlOutputService.generate(documentId);
    }

    @GetMapping
    public ApiResponse<XmlOutputResponse> findByDocumentId(@PathVariable Long documentId) {

        return xmlOutputService.findByDocumentId(documentId);
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@PathVariable Long documentId) {

        DocumentFileResponse fileResponse = xmlOutputService.downloadFile(documentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResponse.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileResponse.getFileName() + "\"")
                .body(fileResponse.getResource());
    }

    @PostMapping("/complete")
    public ApiResponse<XmlOutputResponse> complete(@PathVariable Long documentId) {

        return xmlOutputService.complete(documentId);
    }
}
