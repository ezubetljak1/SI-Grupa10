package ba.unsa.si.docflow.service.extraction;

import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.dao.ExtractionDAO;
import ba.unsa.si.docflow.dao.ExtractionFieldDAO;
import ba.unsa.si.docflow.dto.extraction.CreateExtractionFieldRequest;
import ba.unsa.si.docflow.dto.extraction.ExtractionFieldResponse;
import ba.unsa.si.docflow.dto.extraction.ExtractionResponse;
import ba.unsa.si.docflow.dto.extraction.UpdateExtractionFieldRequest;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.ExtractionEntity;
import ba.unsa.si.docflow.entity.ExtractionFieldEntity;
import ba.unsa.si.docflow.entity.enums.*;
import ba.unsa.si.docflow.service.audit.AuditLogService;
import ba.unsa.si.docflow.service.security.WorkflowPermissionService;
import ba.unsa.si.docflow.service.workflow.DocumentStatusTransitionService;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.exception.DocumentClassificationReviewRequiredException;
import ba.unsa.si.docflow.exception.ExtractionException;
import ba.unsa.si.docflow.mapper.ExtractionMapper;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.ValidationErrors;
import ba.unsa.si.docflow.security.CurrentUserService;
import ba.unsa.si.docflow.service.document.DocumentValidation;
import ba.unsa.si.docflow.service.ocr.DocumentAiProcessorRouter;
import ba.unsa.si.docflow.service.ocr.DocumentClassificationService;
import ba.unsa.si.docflow.service.ocr.OcrProvider;
import ba.unsa.si.docflow.service.ocr.model.DocumentClassificationResult;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private static final BigDecimal CLASSIFICATION_CONFIDENCE_THRESHOLD = new BigDecimal("0.70");
    private static final DateTimeFormatter ISO_DATE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter EUROPEAN_DOT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter EUROPEAN_SLASH_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter EUROPEAN_OUTPUT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.uuuu");

    private final ExtractionDAO extractionDAO;
    private final ExtractionFieldDAO extractionFieldDAO;
    private final DocumentDAO documentDAO;
    private final DocumentValidation documentValidation;
    private final StorageService storageService;
    private final OcrProvider ocrProvider;
    private final ExtractionMapper extractionMapper;
    private final ObjectMapper objectMapper;
    private final ExtractionValidation extractionValidation;
    private final DocumentAiProcessorRouter processorRouter;
    private final DocumentClassificationService documentClassificationService;
    private final CurrentUserService currentUserService;

    private final DocumentStatusTransitionService documentStatusTransitionService;

    private final WorkflowPermissionService workflowPermissionService;
    private final AuditLogService auditLogService;

    @Override
    public ApiResponse<ExtractionResponse> process(Long documentId) {
        currentUserService.requireAnyRole(RoleName.ADMIN, RoleName.OPERATOR);
        DocumentEntity document = requireDocumentInCurrentCompany(documentId);
        workflowPermissionService.requireCanRunExtraction(document);
        Long currentUserId = currentUserService.getCurrentUserId();

        try {
            byte[] fileContent = readDocumentContent(document);
            String mimeType = resolveMimeType(document);

            DocumentType documentType =
                    resolveDocumentTypeForProcessing(document, fileContent, mimeType);
            String processorId = processorRouter.resolveProcessorId(documentType);

            OcrResult ocrResult = ocrProvider.process(fileContent, mimeType, processorId);
            ExtractionEntity extraction = upsertExtraction(document, ocrResult, documentType);

            document.setDocumentType(documentType);
            document.setProcessorIdUsed(processorId);

            documentStatusTransitionService.changeStatus(
                    document,
                    DocumentStatus.EXTRACTED,
                    StatusHistoryAction.EXTRACTION_COMPLETED,
                    currentUserId,
                    null,
                    "Document extraction completed.");

            ExtractionResponse response = extractionMapper.entityToDto(extraction);
            return new ApiResponse<>("OK", response);

        } catch (ExtractionException exception) {
            if (document.getDocumentStatus() != DocumentStatus.NEEDS_CLASSIFICATION_REVIEW) {
                documentStatusTransitionService.changeStatus(
                        document,
                        DocumentStatus.PROCESSING_FAILED,
                        StatusHistoryAction.EXTRACTION_FAILED,
                        currentUserId,
                        null,
                        exception.getMessage());
            }

            throw exception;

        } catch (Exception exception) {
            documentStatusTransitionService.changeStatus(
                    document,
                    DocumentStatus.PROCESSING_FAILED,
                    StatusHistoryAction.EXTRACTION_FAILED,
                    currentUserId,
                    null,
                    exception.getMessage());

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
        currentUserService.requireAnyRole(
                RoleName.ADMIN, RoleName.OPERATOR, RoleName.APPROVER, RoleName.MANAGER);
        requireDocumentInCurrentCompany(documentId);

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
        currentUserService.requireAnyRole(
                RoleName.ADMIN, RoleName.OPERATOR, RoleName.APPROVER, RoleName.MANAGER);
        requireDocumentInCurrentCompany(documentId);

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
        currentUserService.requireAnyRole(
                RoleName.ADMIN, RoleName.OPERATOR, RoleName.APPROVER, RoleName.MANAGER);
        requireExtractionInCurrentCompany(extractionId);
        List<ExtractionFieldEntity> fields = extractionFieldDAO.findByExtractionId(extractionId);

        if (fields.isEmpty()) {
            throw new ApiNotFoundException(
                    "Extraction fields were not found for extraction with id: " + extractionId);
        }

        return new ApiResponse<>("OK", extractionMapper.fieldsToDto(fields));
    }

    @Override
    public ApiResponse<ExtractionResponse> confirmExtraction(Long documentId) {
        currentUserService.requireAnyRole(RoleName.ADMIN, RoleName.OPERATOR);
        DocumentEntity document = requireDocumentInCurrentCompany(documentId);
        workflowPermissionService.requireCanConfirmExtraction(document);
        ExtractionEntity extraction = extractionDAO.findByDocumentId(documentId);

        if (extraction == null) {
            throw new ApiNotFoundException(
                    "Extraction result was not found for document with id: " + documentId);
        }

        extractionValidation.validateRequiredFields(extraction);

        StatusHistoryAction confirmAction =
                document.getDocumentStatus() == DocumentStatus.NEEDS_CORRECTION
                        ? StatusHistoryAction.EXTRACTION_RECONFIRMED
                        : StatusHistoryAction.EXTRACTION_CONFIRMED;

        documentStatusTransitionService.changeStatus(
                document,
                DocumentStatus.READY_FOR_APPROVAL,
                confirmAction,
                currentUserService.getCurrentUserId(),
                null,
                "Extraction confirmed and sent for approval.");

        return new ApiResponse<>("OK", extractionMapper.entityToDto(extraction));
    }

    @Override
    public ApiResponse<ExtractionFieldResponse> updateField(
            Long extractionId, Long fieldId, UpdateExtractionFieldRequest request) {
        currentUserService.requireAnyRole(RoleName.ADMIN, RoleName.OPERATOR);
        requireExtractionInCurrentCompany(extractionId);
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

        DocumentEntity document = field.getExtraction().getDocument();
        workflowPermissionService.requireCanEditExtraction(document);
        extractionValidation.validateFieldEditAllowed(document);
        validateUpdatedFieldValue(field, request.getValue());
        field.setValue(request.getValue());
        field.setCorrected(true);
        field.setPlaceholder(false);

        ExtractionFieldEntity updatedField = extractionFieldDAO.merge(field);

        auditLogService.log(
                document,
                currentUserService.getCurrentUserId(),
                AuditAction.FIELD_UPDATED,
                "{\"fieldId\":" + fieldId + ",\"fieldName\":\"" + field.getFieldName() + "\"}"
        );

        return new ApiResponse<>("OK", extractionMapper.fieldToDto(updatedField));
    }

    @Override
    public ApiResponse<ExtractionFieldResponse> addField(
            Long extractionId, CreateExtractionFieldRequest request) {
        currentUserService.requireAnyRole(RoleName.ADMIN, RoleName.OPERATOR);
        ExtractionEntity extraction = requireExtractionInCurrentCompany(extractionId);
        DocumentEntity document = extraction.getDocument();

        workflowPermissionService.requireCanEditExtraction(document);
        extractionValidation.validateManualFieldRequest(
                document, request.getFieldName(), request.getDisplayName(), request.getValue());

        String normalizedFieldName = normalizeFieldName(request.getFieldName());

        if (extractionFieldDAO.existsByExtractionIdAndFieldName(extractionId, normalizedFieldName)) {
            ValidationErrors errors = new ValidationErrors();
            errors.add(
                    "EXTRACTION_FIELD_DUPLICATE",
                    "A field with the same name already exists for this extraction.");
            throw new ApiValidationException(errors);
        }

        ExtractionFieldEntity field = new ExtractionFieldEntity();
        field.setExtraction(extraction);
        field.setFieldName(normalizedFieldName);
        field.setDisplayName(resolveDisplayName(request, normalizedFieldName));
        field.setValue(request.getValue().trim());
        field.setConfidence(null);
        field.setCorrected(true);
        field.setManual(true);
        field.setPlaceholder(false);

        ExtractionFieldEntity savedField = extractionFieldDAO.persist(field);

        auditLogService.log(
                document,
                currentUserService.getCurrentUserId(),
                AuditAction.FIELD_ADDED,
                "{\"fieldId\":"
                        + savedField.getId()
                        + ",\"fieldName\":\""
                        + normalizedFieldName
                        + "\"}");

        return new ApiResponse<>("OK", extractionMapper.fieldToDto(savedField));
    }

    private String resolveDisplayName(CreateExtractionFieldRequest request, String normalizedFieldName) {
        if (StringUtils.hasText(request.getDisplayName())) {
            return request.getDisplayName().trim();
        }

        if (normalizedFieldName.startsWith("custom.")) {
            return null;
        }

        return null;
    }

    private DocumentType resolveDocumentTypeForProcessing(
            DocumentEntity document, byte[] fileContent, String mimeType) {
        DocumentType currentType = document.getDocumentType();

        if (currentType != DocumentType.OTHER) {
            document.setDetectedDocumentType(null);
            document.setClassificationConfidence(null);
            return currentType;
        }

        DocumentClassificationResult classification =
                documentClassificationService.classify(fileContent, mimeType);

        DocumentType detectedType = classification.documentType();
        BigDecimal confidence =
                classification.confidence() != null ? classification.confidence() : BigDecimal.ZERO;

        document.setDetectedDocumentType(detectedType);
        document.setClassificationConfidence(confidence);

        boolean supportedDetectedType =
                detectedType == DocumentType.INVOICE
                        || detectedType == DocumentType.RECEIPT
                        || detectedType == DocumentType.BANK_STATEMENT
                        || detectedType == DocumentType.FORM;

        if (!supportedDetectedType
                || confidence.compareTo(CLASSIFICATION_CONFIDENCE_THRESHOLD) < 0) {
            documentStatusTransitionService.changeStatus(
                    document,
                    DocumentStatus.NEEDS_CLASSIFICATION_REVIEW,
                    StatusHistoryAction.SYSTEM_STATUS_CHANGE,
                    currentUserService.getCurrentUserId(),
                    null,
                    "Document classification requires manual review.");

            throw new DocumentClassificationReviewRequiredException(
                    document.getId(), detectedType, confidence);
        }

        return detectedType;
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
            throw invalidAmount(fieldName, "Amount must not contain spaces or currency text.");
        }

        if (trimmed.contains(",") && trimmed.contains(".")) {
            throw invalidAmount(
                    fieldName, "Use either comma or dot as decimal separator, not both.");
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
                    "total_amount must match net_amount + vat_amount within 0.01 and cannot be smaller than its component amounts.");
            throw new ApiValidationException(errors);
        }
    }

    private BigDecimal parseAmountStrict(String raw, String fieldName) {
        parseNonNegativeTwoDecimalAmount(raw, fieldName);
        return new BigDecimal(raw.trim().replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
    }

    private static ApiValidationException invalidAmount(String fieldName, String message) {
        ValidationErrors errors = new ValidationErrors();
        errors.add(
                "EXTRACTION_FIELD_AMOUNT_INVALID",
                fieldName
                        + ": "
                        + message
                        + " Enter only the numeric value, without currency symbols or additional text. Use for example 1500, 1500.50 or 1500,50.");
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

    private ExtractionEntity upsertExtraction(
            DocumentEntity document, OcrResult ocrResult, DocumentType documentType) {
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
        addMissingRequiredFieldPlaceholders(extraction, documentType);

        if (extraction.getId() == null) {
            return extractionDAO.persist(extraction);
        }

        return extractionDAO.merge(extraction);
    }

    private void addMissingRequiredFieldPlaceholders(
            ExtractionEntity extraction, DocumentType documentType) {
        Set<String> requiredFields = extractionValidation.getRequiredFields(documentType);

        if (requiredFields.isEmpty()) {
            return;
        }

        Set<String> existingFieldNames =
                extraction.getFields().stream()
                        .map(ExtractionFieldEntity::getFieldName)
                        .filter(StringUtils::hasText)
                        .map(this::normalizeFieldName)
                        .collect(Collectors.toSet());

        for (String requiredField : requiredFields) {
            if (existingFieldNames.contains(requiredField)) {
                continue;
            }

            ExtractionFieldEntity placeholder = new ExtractionFieldEntity();
            placeholder.setExtraction(extraction);
            placeholder.setFieldName(requiredField);
            placeholder.setValue(null);
            placeholder.setConfidence(BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP));
            placeholder.setCorrected(false);
            placeholder.setPlaceholder(true);

            extraction.getFields().add(placeholder);
        }
    }

    private ExtractionFieldEntity toExtractionFieldEntity(
            ExtractionEntity extraction, OcrExtractedField field) {
        ExtractionFieldEntity entity = new ExtractionFieldEntity();

        entity.setExtraction(extraction);
        entity.setFieldName(field.getType());
        entity.setValue(sanitizeFieldValue(field));
        entity.setConfidence(scaleConfidence(field.getConfidence()));
        entity.setCorrected(false);
        entity.setPlaceholder(false);

        return entity;
    }

    private String sanitizeFieldValue(OcrExtractedField field) {
        String value = resolveFieldValue(field);

        if (!StringUtils.hasText(value)) {
            return value;
        }

        String normalizedFieldName = normalizeFieldName(field.getType());

        if (isDateField(normalizedFieldName)) {
            return sanitizeDateValue(value);
        }

        if (isDecimalField(normalizedFieldName)) {
            return sanitizeNumericValue(value);
        }

        return value;
    }

    private String sanitizeNumericValue(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }

        String sanitized = value.trim();

        sanitized = sanitized.replaceAll("[€$£₣₤₥₦₧₨₪฿₱₭₮₵₴]", "");
        sanitized = sanitized.replaceAll("^[A-Z]{2,3}\\s+", "").replaceAll("\\s+[A-Z]{2,3}$", "");
        sanitized =
                sanitized.replaceAll(
                        "(?i)\\s+(mark|kuna|rupee|dinar|rial|peso|franc|corona|won|baht|dong|rupiah|pound|dollar|euro|cent|pence)?s?$",
                        "");

        return sanitized.trim();
    }

    private String resolveFieldValue(OcrExtractedField field) {
        String normalizedFieldName = normalizeFieldName(field.getType());

        if ((isDateField(normalizedFieldName) || isDecimalField(normalizedFieldName))
                && StringUtils.hasText(field.getNormalizedValue())) {
            return field.getNormalizedValue();
        }

        if (StringUtils.hasText(field.getValue())) {
            return field.getValue();
        }

        return field.getNormalizedValue();
    }

    private String sanitizeDateValue(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }

        String trimmed = value.trim();

        LocalDate isoDate = parseIsoDate(trimmed);
        if (isoDate != null) {
            return isoDate.format(EUROPEAN_OUTPUT_DATE_FORMATTER);
        }

        LocalDate europeanDotDate = parseDate(trimmed, EUROPEAN_DOT_DATE_FORMATTER);
        if (europeanDotDate != null) {
            return europeanDotDate.format(EUROPEAN_OUTPUT_DATE_FORMATTER);
        }

        LocalDate europeanSlashDate = parseDate(trimmed, EUROPEAN_SLASH_DATE_FORMATTER);
        if (europeanSlashDate != null) {
            return europeanSlashDate.format(EUROPEAN_OUTPUT_DATE_FORMATTER);
        }

        return trimmed;
    }

    private LocalDate parseIsoDate(String value) {
        String candidate = value;

        if (value.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
            candidate = value.substring(0, 10);
        }

        return parseDate(candidate, ISO_DATE_FORMATTER);
    }

    private LocalDate parseDate(String value, DateTimeFormatter formatter) {
        try {
            return LocalDate.parse(value, formatter);
        } catch (DateTimeParseException exception) {
            return null;
        }
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

    private DocumentEntity requireDocumentInCurrentCompany(Long documentId) {
        return documentValidation.validateExistsInCompany(
                documentId, currentUserService.getCurrentCompanyId());
    }

    private ExtractionEntity requireExtractionInCurrentCompany(Long extractionId) {
        ExtractionEntity extraction = extractionDAO.findByIdWithDocument(extractionId);

        if (extraction == null || extraction.getDocument() == null) {
            throw new ApiNotFoundException(
                    "Extraction result was not found for extraction with id: " + extractionId);
        }

        documentValidation.validateExistsInCompany(
                extraction.getDocument().getId(), currentUserService.getCurrentCompanyId());

        return extraction;
    }
}
