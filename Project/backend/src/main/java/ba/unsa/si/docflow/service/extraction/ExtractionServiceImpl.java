package ba.unsa.si.docflow.service.extraction;

import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.dao.ExtractionDAO;
import ba.unsa.si.docflow.dao.ExtractionFieldDAO;
import ba.unsa.si.docflow.dto.extraction.ExtractionResponse;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.ExtractionEntity;
import ba.unsa.si.docflow.entity.ExtractionFieldEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.mapper.ExtractionMapper;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.service.document.DocumentValidation;
import ba.unsa.si.docflow.service.ocr.OcrProvider;
import ba.unsa.si.docflow.service.ocr.model.OcrExtractedField;
import ba.unsa.si.docflow.service.ocr.model.OcrResult;
import ba.unsa.si.docflow.service.storage.StorageService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ExtractionServiceImpl implements ExtractionService {

    private final ExtractionDAO extractionDAO;
    private final ExtractionFieldDAO extractionFieldDAO;
    private final DocumentDAO documentDAO;
    private final DocumentValidation documentValidation;
    private final StorageService storageService;
    private final OcrProvider ocrProvider;
    private final ExtractionMapper extractionMapper;
    private final ObjectMapper objectMapper;

    @Override
    public ApiResponse process(Long documentId) {
        DocumentEntity document = documentValidation.validateExists(documentId);

        try {
            byte[] fileContent = readDocumentContent(document);
            String mimeType = resolveMimeType(document);

            OcrResult ocrResult = ocrProvider.process(fileContent, mimeType);

            ExtractionEntity extraction = upsertExtraction(document, ocrResult);

            document.setDocumentStatus(DocumentStatus.EXTRACTED);
            document.setDocumentType(resolveDocumentType(ocrResult));
            documentDAO.merge(document);

            ExtractionResponse response = extractionMapper.entityToDto(extraction);
            return new ApiResponse<>("OK", response);

        } catch (Exception exception) {
            document.setDocumentStatus(DocumentStatus.PROCESSING_FAILED);
            documentDAO.merge(document);

            return new ApiResponse<>(
                    "EXTRACTION_FAILED",
                    "Document extraction failed: " + exception.getMessage()
            );
        }
    }

    @Override
    public ApiResponse retry(Long documentId) {
        return process(documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse findByDocumentId(Long documentId) {
        documentValidation.validateExists(documentId);

        ExtractionEntity extraction = extractionDAO.findByDocumentId(documentId);

        if (extraction == null) {
            throw new ApiNotFoundException(
                    "Extraction result was not found for document with id: " + documentId
            );
        }

        return new ApiResponse<>("OK", extractionMapper.entityToDto(extraction));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse findFieldsByDocumentId(Long documentId) {
        documentValidation.validateExists(documentId);

        ExtractionEntity extraction = extractionDAO.findByDocumentId(documentId);

        if (extraction == null) {
            throw new ApiNotFoundException(
                    "Extraction result was not found for document with id: " + documentId
            );
        }

        List<ExtractionFieldEntity> fields =
                extractionFieldDAO.findByDocumentId(documentId);

        return new ApiResponse<>("OK", extractionMapper.fieldsToDto(fields));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse findFieldsByExtractionId(Long extractionId) {
        List<ExtractionFieldEntity> fields =
                extractionFieldDAO.findByExtractionId(extractionId);

        if (fields.isEmpty()) {
            throw new ApiNotFoundException(
                    "Extraction fields were not found for extraction with id: " + extractionId
            );
        }

        return new ApiResponse<>("OK", extractionMapper.fieldsToDto(fields));
    }

    private byte[] readDocumentContent(DocumentEntity document) throws IOException {
        Resource resource = storageService.loadAsResource(document.getStoragePath());

        try (InputStream inputStream = resource.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }

    private String resolveMimeType(DocumentEntity document) {
        if (StringUtils.hasText(document.getFileType())) {
            return document.getFileType();
        }

        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    private ExtractionEntity upsertExtraction(DocumentEntity document, OcrResult ocrResult) {
        ExtractionEntity extraction = extractionDAO.findByDocumentId(document.getId());

        if (extraction == null) {
            extraction = new ExtractionEntity();
            extraction.setDocument(document);
        } else {
            extraction.getFields().clear();
        }

        extraction.setRawJson(serializeRawResult(ocrResult));
        extraction.setExtractionTime(LocalDateTime.now());

        final ExtractionEntity extractionForFields = extraction;

        List<ExtractionFieldEntity> fieldEntities =
                ocrResult.getFields()
                        .stream()
                        .map(field -> toExtractionFieldEntity(extractionForFields, field))
                        .toList();

        extraction.getFields().addAll(fieldEntities);

        if (extraction.getId() == null) {
            return extractionDAO.persist(extraction);
        }

        return extractionDAO.merge(extraction);
    }

    private ExtractionFieldEntity toExtractionFieldEntity(
            ExtractionEntity extraction,
            OcrExtractedField field
    ) {
        ExtractionFieldEntity entity = new ExtractionFieldEntity();

        entity.setExtraction(extraction);
        entity.setFieldName(field.getType());
        entity.setValue(resolveFieldValue(field));
        entity.setConfidence(scaleConfidence(field.getConfidence()));
        entity.setCorrected(false);

        return entity;
    }

    private String resolveFieldValue(OcrExtractedField field) {
        if (StringUtils.hasText(field.getValue())) {
            return field.getValue();
        }

        return field.getNormalizedValue();
    }

    private BigDecimal scaleConfidence(BigDecimal confidence) {
        if (confidence == null) {
            return null;
        }

        return confidence.setScale(6, RoundingMode.HALF_UP);
    }

    private String serializeRawResult(OcrResult ocrResult) {
        try {
            return objectMapper.writeValueAsString(ocrResult);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize OCR result.", exception);
        }
    }

    private DocumentType resolveDocumentType(OcrResult ocrResult) {
        boolean hasInvoiceField =
                ocrResult.getFields()
                        .stream()
                        .map(OcrExtractedField::getType)
                        .filter(StringUtils::hasText)
                        .anyMatch(type ->
                                type.startsWith("invoice")
                                        || type.equals("supplier_name")
                                        || type.equals("total_amount")
                                        || type.equals("net_amount")
                                        || type.equals("total_tax_amount")
                                        || type.equals("currency")
                        );

        boolean rawTextLooksLikeInvoice =
                StringUtils.hasText(ocrResult.getRawText())
                        && ocrResult.getRawText().toLowerCase().contains("invoice");

        if (hasInvoiceField || rawTextLooksLikeInvoice) {
            return DocumentType.INVOICE;
        }

        return DocumentType.OTHER;
    }
}