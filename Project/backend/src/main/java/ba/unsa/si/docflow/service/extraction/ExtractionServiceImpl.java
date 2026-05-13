package ba.unsa.si.docflow.service.extraction;

import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.dao.ExtractionDAO;
import ba.unsa.si.docflow.dao.ExtractionFieldDAO;
import ba.unsa.si.docflow.dto.extraction.ExtractionFieldResponse;
import ba.unsa.si.docflow.dto.extraction.ExtractionResponse;
import ba.unsa.si.docflow.dto.extraction.UpdateExtractionFieldRequest;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.ExtractionEntity;
import ba.unsa.si.docflow.entity.ExtractionFieldEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.exception.ExtractionException;
import ba.unsa.si.docflow.response.ValidationErrors;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(noRollbackFor = ExtractionException.class)
@AllArgsConstructor
public class ExtractionServiceImpl implements ExtractionService {

    /** Canonical invoice amount fields (US 7.4 – matematika). */
    private static final Set<String> DECIMAL_FIELDS =
            Set.of(
                    "net_amount",
                    "vat_amount",
                    "total_amount",
                    "total_tax_amount",
                    "tax_amount",
                    "subtotal_amount",
                    "amount",
                    "price",
                    "unit_price",
                    "quantity",
                    "qty");

    private static final BigDecimal AMOUNT_TOTAL_TOLERANCE = new BigDecimal("0.01");

    private final ExtractionDAO extractionDAO;
    private final ExtractionFieldDAO extractionFieldDAO;
    private final DocumentDAO documentDAO;
    private final DocumentValidation documentValidation;
    private final StorageService storageService;
    private final OcrProvider ocrProvider;
    private final ExtractionMapper extractionMapper;
    private final ObjectMapper objectMapper;
    private final ExtractionValidation extractionValidation;

    @Override
    public ApiResponse<ExtractionResponse> process(Long documentId) {
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

            throw new ExtractionException(
                    "Document extraction failed: " + exception.getMessage(), exception);
        }
    }

    @Override
    public ApiResponse<ExtractionResponse> retry(Long documentId) {
        return process(documentId);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<ExtractionResponse> findByDocumentId(Long documentId) {
        documentValidation.validateExists(documentId);

        ExtractionEntity extraction = extractionDAO.findByDocumentId(documentId);

        if (extraction == null) {
            throw new ApiNotFoundException(
                    "Extraction result was not found for document with id: " + documentId);
        }

        return new ApiResponse<>("OK", extractionMapper.entityToDto(extraction));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ExtractionFieldResponse>> findFieldsByDocumentId(Long documentId) {
        documentValidation.validateExists(documentId);

        ExtractionEntity extraction = extractionDAO.findByDocumentId(documentId);

        if (extraction == null) {
            throw new ApiNotFoundException(
                    "Extraction result was not found for document with id: " + documentId);
        }

        List<ExtractionFieldEntity> fields = extractionFieldDAO.findByDocumentId(documentId);

        return new ApiResponse<>("OK", extractionMapper.fieldsToDto(fields));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<ExtractionFieldResponse>> findFieldsByExtractionId(Long extractionId) {
        List<ExtractionFieldEntity> fields = extractionFieldDAO.findByExtractionId(extractionId);

        if (fields.isEmpty()) {
            throw new ApiNotFoundException(
                    "Extraction fields were not found for extraction with id: " + extractionId);
        }

        return new ApiResponse<>("OK", extractionMapper.fieldsToDto(fields));
    }

    @Override
    public ApiResponse<ExtractionResponse> confirmExtraction(Long documentId) {
        DocumentEntity document = documentValidation.validateExists(documentId);

        ExtractionEntity extraction = extractionDAO.findByDocumentId(documentId);

        if (extraction == null) {
            throw new ApiNotFoundException(
                    "Extraction result was not found for document with id: " + documentId);
        }

        extractionValidation.validateRequiredFields(extraction);

        document.setDocumentStatus(DocumentStatus.READY_FOR_APPROVAL);
        documentDAO.merge(document);

        return new ApiResponse<>("OK", extractionMapper.entityToDto(extraction));
    }

    @Override
    public ApiResponse<ExtractionFieldResponse> updateField(
            Long extractionId, Long fieldId, UpdateExtractionFieldRequest request) {
        ExtractionFieldEntity field =
                extractionFieldDAO
                        .findByIdAndExtractionId(fieldId, extractionId)
                        .orElseThrow(
                                () ->
                                        new ApiNotFoundException(
                                                "Extraction field was not found for extraction with id: "
                                                        + extractionId
                                                        + " and field id: "
                                                        + fieldId));

        validateUpdatedFieldValue(field, request.getValue());

        field.setValue(request.getValue());
        field.setCorrected(true);

        ExtractionFieldEntity updatedField = extractionFieldDAO.merge(field);

        return new ApiResponse<>("OK", extractionMapper.fieldToDto(updatedField));
    }

    private void validateUpdatedFieldValue(ExtractionFieldEntity field, String newValue) {
        String fieldName = normalizeFieldName(field.getFieldName());
        if (isDateField(fieldName)) {
            extractionValidation.validateUpdatedFieldFormat(field, newValue);
            return;
        }
        if (!isDecimalField(fieldName)) {
            return;
        }

        parseNonNegativeTwoDecimalAmount(newValue, fieldName);
        validateTotalMatchesNetPlusVatIfComplete(field, newValue);
    }

    private void parseNonNegativeTwoDecimalAmount(String raw, String fieldName) {
        if (!StringUtils.hasText(raw)) {
            throw invalidAmount(fieldName, "Value must not be empty.");
        }

        String trimmed = raw.trim();
        if (trimmed.contains(" ") || trimmed.contains("\t")) {
            throw invalidAmount(fieldName, "Amount must not contain spaces.");
        }

        if (trimmed.contains(",") && trimmed.contains(".")) {
            throw invalidAmount(fieldName, "Use either comma or dot as decimal separator, not both.");
        }

        int comma = trimmed.indexOf(',');
        int dot = trimmed.indexOf('.');
        if (comma >= 0 && trimmed.indexOf(',', comma + 1) >= 0) {
            throw invalidAmount(fieldName, "Invalid amount format.");
        }
        if (dot >= 0 && trimmed.indexOf('.', dot + 1) >= 0) {
            throw invalidAmount(fieldName, "Invalid amount format.");
        }

        String normalized = trimmed.replace(',', '.');
        BigDecimal amount;
        try {
            amount = new BigDecimal(normalized);
        } catch (NumberFormatException exception) {
            throw invalidAmount(fieldName, "Amount must be a valid decimal number.");
        }

        if (amount.signum() < 0) {
            throw invalidAmount(fieldName, "Amount must not be negative.");
        }

        if (amount.scale() > 2) {
            throw invalidAmount(fieldName, "Amount must have at most 2 decimal places.");
        }
    }

    private void validateTotalMatchesNetPlusVatIfComplete(
            ExtractionFieldEntity editedField, String newValueForEditedField) {
        Long extractionId = editedField.getExtraction().getId();
        List<ExtractionFieldEntity> allFields = extractionFieldDAO.findByExtractionId(extractionId);

        Map<String, String> effective = new HashMap<>();
        for (ExtractionFieldEntity f : allFields) {
            if (!isDecimalField(normalizeFieldName(f.getFieldName()))) {
                continue;
            }
            boolean isEdited = f.getId().equals(editedField.getId());
            String v = isEdited ? newValueForEditedField : f.getValue();
            effective.put(normalizeFieldName(f.getFieldName()), v);
        }

        if (!effective.containsKey("net_amount")
                || !effective.containsKey("vat_amount")
                || !effective.containsKey("total_amount")) {
            return;
        }

        String netRaw = effective.get("net_amount");
        String vatRaw = effective.get("vat_amount");
        String totalRaw = effective.get("total_amount");
        if (!StringUtils.hasText(netRaw)
                || !StringUtils.hasText(vatRaw)
                || !StringUtils.hasText(totalRaw)) {
            return;
        }

        BigDecimal net = parseAmountStrict(netRaw, "net_amount");
        BigDecimal vat = parseAmountStrict(vatRaw, "vat_amount");
        BigDecimal total = parseAmountStrict(totalRaw, "total_amount");

        BigDecimal expected = net.add(vat).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalRounded = total.setScale(2, RoundingMode.HALF_UP);
        if (totalRounded.subtract(expected).abs().compareTo(AMOUNT_TOTAL_TOLERANCE) > 0) {
            ValidationErrors errors = new ValidationErrors();
            errors.add(
                    "EXTRACTION_FIELD_AMOUNT_INCONSISTENT",
                    "total_amount must equal net_amount + vat_amount (within 0.01).");
            throw new ApiValidationException(errors);
        }
    }

    private BigDecimal parseAmountStrict(String raw, String fieldName) {
        parseNonNegativeTwoDecimalAmount(raw, fieldName);
        return new BigDecimal(raw.trim().replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
    }

    private static ApiValidationException invalidAmount(String fieldName, String message) {
        ValidationErrors errors = new ValidationErrors();
        errors.add("EXTRACTION_FIELD_AMOUNT_INVALID", fieldName + ": " + message);
        return new ApiValidationException(errors);
    }

    private boolean isDateField(String normalizedFieldName) {
        return normalizedFieldName.contains("date") || normalizedFieldName.contains("datum");
    }

    private boolean isDecimalField(String normalizedFieldName) {
        return DECIMAL_FIELDS.contains(normalizedFieldName)
                || normalizedFieldName.contains("amount")
                || normalizedFieldName.contains("iznos")
                || normalizedFieldName.contains("cijena")
                || normalizedFieldName.endsWith("_price")
                || normalizedFieldName.endsWith("_quantity");
    }

    private String normalizeFieldName(String fieldName) {
        if (fieldName == null) {
            return "";
        }
        return fieldName.trim().toLowerCase(Locale.ROOT);
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
                ocrResult.getFields().stream()
                        .map(field -> toExtractionFieldEntity(extractionForFields, field))
                        .toList();

        extraction.getFields().addAll(fieldEntities);

        if (extraction.getId() == null) {
            return extractionDAO.persist(extraction);
        }

        return extractionDAO.merge(extraction);
    }

    private ExtractionFieldEntity toExtractionFieldEntity(
            ExtractionEntity extraction, OcrExtractedField field) {
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
                ocrResult.getFields().stream()
                        .map(OcrExtractedField::getType)
                        .filter(StringUtils::hasText)
                        .anyMatch(
                                type ->
                                        type.startsWith("invoice")
                                                || type.equals("supplier_name")
                                                || type.equals("total_amount")
                                                || type.equals("net_amount")
                                                || type.equals("total_tax_amount")
                                                || type.equals("currency"));

        boolean rawTextLooksLikeInvoice =
                StringUtils.hasText(ocrResult.getRawText())
                        && ocrResult.getRawText().toLowerCase().contains("invoice");

        if (hasInvoiceField || rawTextLooksLikeInvoice) {
            return DocumentType.INVOICE;
        }

        return DocumentType.OTHER;
    }
}
