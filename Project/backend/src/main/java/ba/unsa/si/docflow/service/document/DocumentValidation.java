package ba.unsa.si.docflow.service.document;

import ba.unsa.si.docflow.config.StorageProperties;
import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.dto.document.DocumentCreateRequest;
import ba.unsa.si.docflow.dto.document.DocumentUpdateRequest;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.response.ValidationErrors;

import lombok.AllArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Service
@AllArgsConstructor
public class DocumentValidation {

    private final DocumentDAO documentDAO;

    private final MessageSource messageSource;

    private final StorageProperties storageProperties;

    public void validateCreate(DocumentCreateRequest request) {
        ValidationErrors errors = new ValidationErrors();

        if (documentDAO.existsByNameInCompany(request.getName(), request.getCompanyId(), null)) {
            addError(errors, "DOCUMENT_NAME_EXISTS", "document.validation.name.exists");
        }

        if (!isValidDocumentType(request.getDocumentType())) {
            addError(errors, "DOCUMENT_TYPE_INVALID", "document.validation.type.invalid");
        }

        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    public String resolveDocumentName(String name, MultipartFile file) {
        if (StringUtils.hasText(name)) {
            return name.trim();
        }

        if (file == null || !StringUtils.hasText(file.getOriginalFilename())) {
            return null;
        }

        return StringUtils.cleanPath(file.getOriginalFilename()).trim();
    }

    public void validateUpload(
            MultipartFile file,
            Long companyId,
            Long createdByUserId,
            String documentType,
            String documentName) {
        ValidationErrors errors = new ValidationErrors();

        validateRequiredUploadFields(
                errors, file, companyId, createdByUserId, documentType, documentName);

        if (file != null && !file.isEmpty()) {
            validateFileName(errors, file);
            validateFileSize(errors, file);
            validateFileExtension(errors, file);
            validateFileContentType(errors, file);
        }

        if (StringUtils.hasText(documentType) && !isValidDocumentType(documentType)) {
            addError(errors, "DOCUMENT_TYPE_INVALID", "document.validation.type.invalid");
        }

        if (StringUtils.hasText(documentName)
                && companyId != null
                && documentDAO.existsByNameInCompany(documentName, companyId, null)) {
            addError(errors, "DOCUMENT_NAME_EXISTS", "document.validation.name.exists");
        }

        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    public void validateUpdate(DocumentUpdateRequest request, DocumentEntity entity) {
        ValidationErrors errors = new ValidationErrors();

        String nameToCheck = request.getName() != null ? request.getName() : entity.getName();
        Long companyIdToCheck = entity.getCompanyId();

        if (documentDAO.existsByNameInCompany(nameToCheck, companyIdToCheck, entity.getId())) {
            addError(errors, "DOCUMENT_NAME_EXISTS", "document.validation.name.exists");
        }

        if (StringUtils.hasText(request.getDocumentType())
                && !isValidDocumentType(request.getDocumentType())) {
            addError(errors, "DOCUMENT_TYPE_INVALID", "document.validation.type.invalid");
        }

        if (StringUtils.hasText(request.getDocumentStatus())
                && !isValidDocumentStatus(request.getDocumentStatus())) {
            addError(errors, "DOCUMENT_STATUS_INVALID", "document.validation.status.invalid");
        }

        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    public void validateDelete(DocumentEntity entity) {
        ValidationErrors errors = new ValidationErrors();

        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    public DocumentEntity validateExists(Long id) {
        DocumentEntity entity = documentDAO.findByPK(id);

        if (entity == null) {
            throw new ApiNotFoundException(
                    messageSource.getMessage(
                            "document.validation.not_found", null, Locale.getDefault()));
        }

        return entity;
    }

    private void validateRequiredUploadFields(
            ValidationErrors errors,
            MultipartFile file,
            Long companyId,
            Long createdByUserId,
            String documentType,
            String documentName) {
        if (file == null) {
            addError(errors, "DOCUMENT_FILE_REQUIRED", "document.validation.file.required");
            return;
        }

        if (file.isEmpty()) {
            addError(errors, "DOCUMENT_FILE_EMPTY", "document.validation.file.empty");
        }

        if (companyId == null) {
            addError(errors, "DOCUMENT_COMPANY_REQUIRED", "document.validation.company.required");
        }

        if (createdByUserId == null) {
            addError(
                    errors,
                    "DOCUMENT_CREATED_BY_REQUIRED",
                    "document.validation.created_by.required");
        }

        if (!StringUtils.hasText(documentType)) {
            addError(errors, "DOCUMENT_TYPE_REQUIRED", "document.validation.type.required");
        }

        if (!StringUtils.hasText(documentName)) {
            addError(errors, "DOCUMENT_NAME_REQUIRED", "document.validation.name.required");
        }
    }

    private void validateFileName(ValidationErrors errors, MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        if (!StringUtils.hasText(originalFileName)) {
            addError(errors, "DOCUMENT_FILE_NAME_INVALID", "document.validation.file.name.invalid");
            return;
        }

        if (originalFileName.contains("..")
                || originalFileName.contains("/")
                || originalFileName.contains("\\")) {
            addError(errors, "DOCUMENT_FILE_NAME_INVALID", "document.validation.file.name.invalid");
        }
    }

    private void validateFileSize(ValidationErrors errors, MultipartFile file) {
        if (file.getSize() > storageProperties.getMaxFileSize()) {
            addError(
                    errors,
                    "DOCUMENT_FILE_SIZE_EXCEEDED",
                    "document.validation.file.size.exceeded");
        }
    }

    private void validateFileExtension(ValidationErrors errors, MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFileName);

        if (!StringUtils.hasText(extension)) {
            addError(
                    errors,
                    "DOCUMENT_FILE_EXTENSION_MISSING",
                    "document.validation.file.type.unsupported");
            return;
        }

        boolean allowed =
                storageProperties.getAllowedExtensions().stream()
                        .anyMatch(allowedExtension -> allowedExtension.equalsIgnoreCase(extension));

        if (!allowed) {
            addError(
                    errors,
                    "DOCUMENT_FILE_TYPE_UNSUPPORTED",
                    "document.validation.file.type.unsupported");
        }
    }

    private void validateFileContentType(ValidationErrors errors, MultipartFile file) {
        String contentType = file.getContentType();

        if (!StringUtils.hasText(contentType)) {
            addError(
                    errors,
                    "DOCUMENT_FILE_CONTENT_TYPE_UNSUPPORTED",
                    "document.validation.file.type.unsupported");
            return;
        }

        boolean allowed =
                storageProperties.getAllowedContentTypes().stream()
                        .anyMatch(
                                allowedContentType ->
                                        allowedContentType.equalsIgnoreCase(contentType));

        if (!allowed) {
            addError(
                    errors,
                    "DOCUMENT_FILE_CONTENT_TYPE_UNSUPPORTED",
                    "document.validation.file.type.unsupported");
        }
    }

    private boolean isValidDocumentType(String documentType) {
        try {
            DocumentType.valueOf(documentType.toUpperCase());
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private boolean isValidDocumentStatus(String documentStatus) {
        try {
            DocumentStatus.valueOf(documentStatus.toUpperCase());
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private void addError(ValidationErrors errors, String code, String messageKey) {
        errors.add(code, messageSource.getMessage(messageKey, null, Locale.getDefault()));
    }
}
