package ba.unsa.si.docflow.service.document;

import ba.unsa.si.docflow.dto.document.DocumentCreateRequest;
import ba.unsa.si.docflow.dto.document.DocumentUpdateRequest;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.response.ValidationErrors;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@AllArgsConstructor
public class DocumentValidation {

    private final DocumentDAO documentDAO;
    private final MessageSource messageSource;

    public void validateCreate(DocumentCreateRequest request) {
        ValidationErrors errors = new ValidationErrors();

        if (documentDAO.existsByNameInCompany(request.getName(), request.getCompanyId(), null)) {
            errors.add(
                    "DOCUMENT_NAME_EXISTS",
                    messageSource.getMessage("document.validation.name.exists", null, Locale.getDefault())
            );
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
            errors.add(
                    "DOCUMENT_NAME_EXISTS",
                    messageSource.getMessage("document.validation.name.exists", null, Locale.getDefault())
            );
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
                    messageSource.getMessage("document.validation.not_found", null, Locale.getDefault())
            );
        }

        return entity;
    }
}