package ba.unsa.si.docflow.service.xml;

import ba.unsa.si.docflow.dao.ExtractionDAO;
import ba.unsa.si.docflow.dao.XmlOutputDAO;
import ba.unsa.si.docflow.dto.document.DocumentFileResponse;
import ba.unsa.si.docflow.dto.xml.XmlOutputResponse;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.ExtractionEntity;
import ba.unsa.si.docflow.entity.XmlOutputEntity;
import ba.unsa.si.docflow.entity.enums.AuditAction;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.entity.enums.StatusHistoryAction;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.exception.StorageException;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.ValidationErrors;
import ba.unsa.si.docflow.security.CurrentUserService;
import ba.unsa.si.docflow.service.audit.AuditLogService;
import ba.unsa.si.docflow.service.document.DocumentValidation;
import ba.unsa.si.docflow.service.extraction.ExtractionValidation;
import ba.unsa.si.docflow.service.storage.StorageService;
import ba.unsa.si.docflow.service.storage.StoredFileInfo;
import ba.unsa.si.docflow.service.workflow.DocumentStatusTransitionService;

import lombok.AllArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@Transactional
@AllArgsConstructor
public class XmlOutputServiceImpl implements XmlOutputService {

    private static final String XML_CONTENT_TYPE = "application/xml";

    private final XmlOutputDAO xmlOutputDAO;

    private final ExtractionDAO extractionDAO;

    private final DocumentValidation documentValidation;

    private final ExtractionValidation extractionValidation;

    private final XmlOutputGenerator xmlOutputGenerator;

    private final StorageService storageService;

    private final CurrentUserService currentUserService;

    private final AuditLogService auditLogService;

    private final DocumentStatusTransitionService documentStatusTransitionService;

    @Override
    public ApiResponse<XmlOutputResponse> generate(Long documentId) {
        currentUserService.requireAnyRole(RoleName.ADMIN, RoleName.MANAGER);

        Long companyId = currentUserService.getCurrentCompanyId();

        Long currentUserId = currentUserService.getCurrentUserId();

        DocumentEntity document = documentValidation.validateExistsInCompany(documentId, companyId);

        validateGenerationStatus(document);

        ExtractionEntity extraction = extractionDAO.findByDocumentId(documentId);

        if (extraction == null) {
            throw new ApiNotFoundException(
                    "Extraction result was not found for document with id: " + documentId);
        }

        /*
         * XML generation must never bypass extraction validation.
         * Even if an inconsistent APPROVED state somehow exists in the DB,
         * invalid XML must not be created.
         */
        extractionValidation.validateRequiredFields(extraction);

        LocalDateTime generatedAt = LocalDateTime.now();

        byte[] xmlContent = xmlOutputGenerator.generate(document, extraction, generatedAt);

        String fileName = resolveXmlFileName(document);

        StoredFileInfo storedFileInfo = null;

        XmlOutputEntity existingOutput = xmlOutputDAO.findByDocumentId(documentId);

        String previousStoragePath =
                existingOutput != null ? existingOutput.getStoragePath() : null;

        try {
            storedFileInfo =
                    storageService.store(xmlContent, fileName, XML_CONTENT_TYPE, companyId);

            XmlOutputEntity output =
                    existingOutput != null ? existingOutput : new XmlOutputEntity();

            output.setDocument(document);
            output.setStoragePath(storedFileInfo.getRelativePath());
            output.setFileName(fileName);
            output.setGeneratedAt(generatedAt);
            output.setGeneratedBy(currentUserId);

            XmlOutputEntity saved =
                    existingOutput != null
                            ? xmlOutputDAO.merge(output)
                            : xmlOutputDAO.persist(output);

            xmlOutputDAO.flush();

            if (StringUtils.hasText(previousStoragePath)
                    && !previousStoragePath.equals(saved.getStoragePath())) {

                storageService.delete(previousStoragePath);
            }

            auditLogService.log(
                    document,
                    currentUserId,
                    AuditAction.XML_GENERATED,
                    String.format(
                            "{\"documentId\":%d,\"xmlOutputId\":%d}",
                            document.getId(), saved.getId()));

            return new ApiResponse<>("OK", toResponse(saved, xmlContent));

        } catch (RuntimeException exception) {
            if (storedFileInfo != null) {
                storageService.delete(storedFileInfo.getRelativePath());
            }

            throw exception;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<XmlOutputResponse> findByDocumentId(Long documentId) {
        currentUserService.requireAnyRole(RoleName.ADMIN, RoleName.MANAGER);

        documentValidation.validateExistsInCompany(
                documentId, currentUserService.getCurrentCompanyId());

        XmlOutputEntity output = requireXmlOutput(documentId);

        byte[] content = readContent(output.getStoragePath());

        return new ApiResponse<>("OK", toResponse(output, content));
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentFileResponse downloadFile(Long documentId) {
        currentUserService.requireAnyRole(RoleName.ADMIN, RoleName.MANAGER);

        documentValidation.validateExistsInCompany(
                documentId, currentUserService.getCurrentCompanyId());

        XmlOutputEntity output = requireXmlOutput(documentId);

        Resource resource = storageService.loadAsResource(output.getStoragePath());

        return new DocumentFileResponse(resource, output.getFileName(), XML_CONTENT_TYPE);
    }

    @Override
    public ApiResponse<XmlOutputResponse> complete(Long documentId) {
        currentUserService.requireAnyRole(RoleName.ADMIN, RoleName.MANAGER);

        DocumentEntity document =
                documentValidation.validateExistsInCompany(
                        documentId, currentUserService.getCurrentCompanyId());

        validateCompletionStatus(document);

        XmlOutputEntity output = requireXmlOutput(documentId);

        /*
         * Nije dovoljno da DB zapis postoji.
         * Fizički XML fajl također mora biti dostupan prije finalizacije.
         */
        byte[] content = readContent(output.getStoragePath());

        Long currentUserId = currentUserService.getCurrentUserId();

        documentStatusTransitionService.changeStatus(
                document,
                DocumentStatus.COMPLETED,
                StatusHistoryAction.DOCUMENT_COMPLETED,
                currentUserId,
                null,
                "Document processing completed after XML output confirmation.");

        auditLogService.log(
                document,
                currentUserId,
                AuditAction.DOCUMENT_COMPLETED,
                String.format(
                        "{\"documentId\":%d,\"xmlOutputId\":%d,\"status\":\"COMPLETED\"}",
                        document.getId(), output.getId()));

        return new ApiResponse<>("OK", toResponse(output, content));
    }

    private void validateGenerationStatus(DocumentEntity document) {

        if (document.getDocumentStatus() == DocumentStatus.APPROVED) {

            return;
        }

        ValidationErrors errors = new ValidationErrors();

        errors.add(
                "DOCUMENT_STATUS_INVALID",
                "XML output can only be generated for an approved document.");

        throw new ApiValidationException(errors);
    }

    private void validateCompletionStatus(DocumentEntity document) {

        if (document.getDocumentStatus() == DocumentStatus.APPROVED) {

            return;
        }

        ValidationErrors errors = new ValidationErrors();

        errors.add(
                "DOCUMENT_STATUS_INVALID",
                "Document processing can only be completed for an approved document.");

        throw new ApiValidationException(errors);
    }

    private XmlOutputEntity requireXmlOutput(Long documentId) {

        XmlOutputEntity output = xmlOutputDAO.findByDocumentId(documentId);

        if (output == null) {
            throw new ApiNotFoundException(
                    "XML output was not found for document with id: " + documentId);
        }

        return output;
    }

    private byte[] readContent(String storagePath) {
        Resource resource = storageService.loadAsResource(storagePath);

        try (InputStream inputStream = resource.getInputStream()) {

            return inputStream.readAllBytes();

        } catch (IOException exception) {
            throw new StorageException("XML output file could not be read.", exception);
        }
    }

    private XmlOutputResponse toResponse(XmlOutputEntity output, byte[] content) {

        return new XmlOutputResponse(
                output.getId(),
                output.getDocument().getId(),
                output.getFileName(),
                output.getGeneratedAt(),
                output.getGeneratedBy(),
                new String(content, StandardCharsets.UTF_8));
    }

    private String resolveXmlFileName(DocumentEntity document) {

        String baseName = StringUtils.stripFilenameExtension(document.getName());

        if (!StringUtils.hasText(baseName)) {
            baseName = "document-" + document.getId();
        }

        return baseName + ".xml";
    }
}
