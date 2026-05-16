package ba.unsa.si.docflow.service.document;

import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.dao.ExtractionDAO;
import ba.unsa.si.docflow.dto.document.*;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.mapper.DocumentMapper;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.PagedResponse;
import ba.unsa.si.docflow.response.ValidationErrors;
import ba.unsa.si.docflow.service.storage.StorageService;
import ba.unsa.si.docflow.service.storage.StoredFileInfo;

import lombok.AllArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentDAO documentDAO;

    private final DocumentMapper documentMapper;

    private final DocumentValidation documentValidation;

    private final StorageService storageService;

    private final ExtractionDAO extractionDAO;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<Document> find(DocumentFilterRequest request) {
        List<DocumentEntity> entities = documentDAO.findByFilter(request);
        long totalElements = documentDAO.countByFilter(request);
        int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

        return new PagedResponse<>(
                "OK",
                documentMapper.entitiesToDtos(entities),
                request.getPage(),
                request.getSize(),
                totalElements,
                totalPages);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Document> findById(Long id) {
        DocumentEntity entity = documentValidation.validateExists(id);
        return new ApiResponse<>("OK", documentMapper.entityToDto(entity));
    }

    @Override
    public ApiResponse<Document> create(DocumentCreateRequest request) {
        documentValidation.validateCreate(request);

        DocumentEntity entity = documentMapper.dtoToEntity(request);
        DocumentEntity saved = documentDAO.persist(entity);

        return new ApiResponse<>("OK", documentMapper.entityToDto(saved));
    }

    @Override
    public ApiResponse<Document> upload(
            MultipartFile file,
            Long companyId,
            Long createdByUserId,
            String documentType,
            String name) {
        String documentName = documentValidation.resolveDocumentName(name, file);

        documentValidation.validateUpload(
                file, companyId, createdByUserId, documentType, documentName);

        StoredFileInfo storedFileInfo = null;

        try {
            storedFileInfo = storageService.store(file, companyId);

            DocumentEntity entity = new DocumentEntity();
            entity.setCompanyId(companyId);
            entity.setCreatedBy(createdByUserId);
            entity.setName(documentName);
            entity.setFileType(storedFileInfo.getContentType());
            entity.setDocumentType(DocumentType.valueOf(documentType.toUpperCase()));
            entity.setStoragePath(storedFileInfo.getRelativePath());
            entity.setUploadDate(LocalDateTime.now());
            entity.setFileSize(storedFileInfo.getSize());
            entity.setDocumentStatus(DocumentStatus.UPLOADED);

            DocumentEntity saved = documentDAO.persist(entity);

            return new ApiResponse<>("OK", documentMapper.entityToDto(saved));
        } catch (RuntimeException exception) {
            if (storedFileInfo != null) {
                storageService.delete(storedFileInfo.getRelativePath());
            }

            throw exception;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentFileResponse downloadFile(Long id) {
        DocumentEntity entity = documentValidation.validateExists(id);

        Resource resource = storageService.loadAsResource(entity.getStoragePath());

        return new DocumentFileResponse(
                resource, resolveDownloadFileName(entity), resolveContentType(entity));
    }

    @Override
    public ApiResponse<Document> update(DocumentUpdateRequest request) {
        DocumentEntity entity = documentValidation.validateExists(request.getId());

        documentValidation.validateUpdate(request, entity);

        DocumentEntity saved = documentDAO.merge(documentMapper.updateEntity(request, entity));

        return new ApiResponse<>("OK", documentMapper.entityToDto(saved));
    }

    @Override
    public ApiResponse<String> delete(Long id) {
        DocumentEntity entity = documentValidation.validateExists(id);

        documentValidation.validateDelete(entity);

        String storagePath = entity.getStoragePath();

        extractionDAO.deleteByDocumentId(id);

        documentDAO.remove(entity);
        documentDAO.flush();

        storageService.delete(storagePath);

        return new ApiResponse<>("OK", "Document deleted successfully.");
    }

    @Override
    public ApiResponse<Document> confirmDocumentType(Long id, ConfirmDocumentTypeRequest request) {
        DocumentEntity entity = documentValidation.validateExists(id);

        DocumentType confirmedType = parseConfirmedDocumentType(request.getDocumentType());

        if (entity.getDocumentStatus() != DocumentStatus.NEEDS_CLASSIFICATION_REVIEW) {
            ValidationErrors errors = new ValidationErrors();
            errors.add(
                    "DOCUMENT_STATUS_INVALID",
                    "Document type can only be confirmed when document status is NEEDS_CLASSIFICATION_REVIEW.");
            throw new ApiValidationException(errors);
        }

        entity.setDocumentType(confirmedType);
        entity.setDocumentStatus(DocumentStatus.UPLOADED);
        entity.setProcessorIdUsed(null);

        DocumentEntity saved = documentDAO.merge(entity);

        return new ApiResponse<>("OK", documentMapper.entityToDto(saved));
    }

    private DocumentType parseConfirmedDocumentType(String rawDocumentType) {
        DocumentType documentType;

        try {
            documentType = DocumentType.valueOf(rawDocumentType.trim().toUpperCase());
        } catch (Exception ex) {
            ValidationErrors errors = new ValidationErrors();
            errors.add("DOCUMENT_TYPE_INVALID", "Invalid document type.");
            throw new ApiValidationException(errors);
        }

        boolean supportedManualType =
                documentType == DocumentType.INVOICE
                        || documentType == DocumentType.RECEIPT
                        || documentType == DocumentType.BANK_STATEMENT
                        || documentType == DocumentType.FORM;

        if (!supportedManualType) {
            ValidationErrors errors = new ValidationErrors();
            errors.add(
                    "DOCUMENT_TYPE_INVALID",
                    "Manual classification must be one of: INVOICE, RECEIPT, BANK_STATEMENT, FORM.");
            throw new ApiValidationException(errors);
        }

        return documentType;
    }

    private String resolveDownloadFileName(DocumentEntity entity) {
        String documentName = entity.getName();
        String storageExtension = StringUtils.getFilenameExtension(entity.getStoragePath());
        String documentNameExtension = StringUtils.getFilenameExtension(documentName);

        if (!StringUtils.hasText(documentNameExtension) && StringUtils.hasText(storageExtension)) {
            return documentName + "." + storageExtension;
        }

        return documentName;
    }

    private String resolveContentType(DocumentEntity entity) {
        if (StringUtils.hasText(entity.getFileType())) {
            return entity.getFileType();
        }

        return "application/octet-stream";
    }
}
