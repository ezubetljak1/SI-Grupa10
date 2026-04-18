package ba.unsa.si.docflow.service.document;

import ba.unsa.si.docflow.dto.document.Document;
import ba.unsa.si.docflow.dto.document.DocumentCreateRequest;
import ba.unsa.si.docflow.dto.document.DocumentFilterRequest;
import ba.unsa.si.docflow.dto.document.DocumentUpdateRequest;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.mapper.DocumentMapper;
import ba.unsa.si.docflow.repository.DocumentRepository;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.PagedResponse;
import ba.unsa.si.docflow.specification.DocumentSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final DocumentValidation documentValidator;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<Document> find(DocumentFilterRequest request) {
        Sort sort = request.getSortDirection().equalsIgnoreCase("asc")
                ? Sort.by(request.getSortBy()).ascending()
                : Sort.by(request.getSortBy()).descending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<DocumentEntity> page = documentRepository.findAll(
                DocumentSpecification.filterBy(request),
                pageable
        );

        return new PagedResponse<>(
                "OK",
                documentMapper.entitiesToDtos(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Document> findById(Long id) {
        DocumentEntity entity = documentValidator.validateExists(id);
        return new ApiResponse<>("OK", documentMapper.entityToDto(entity));
    }

    @Override
    public ApiResponse<Document> create(DocumentCreateRequest request) {
        documentValidator.validateCreate(request);
        DocumentEntity entity = documentMapper.dtoToEntity(request);
        DocumentEntity saved = documentRepository.save(entity);
        return new ApiResponse<>("OK", documentMapper.entityToDto(saved));
    }

    @Override
    public ApiResponse<Document> update(DocumentUpdateRequest request) {
        DocumentEntity entity = documentValidator.validateExists(request.getId());
        documentValidator.validateUpdate(request, entity);
        documentMapper.updateEntity(request, entity);
        DocumentEntity saved = documentRepository.save(entity);
        return new ApiResponse<>("OK", documentMapper.entityToDto(saved));
    }

    @Override
    public ApiResponse<String> delete(Long id) {
        DocumentEntity entity = documentValidator.validateExists(id);
        documentValidator.validateDelete(entity);
        documentRepository.delete(entity);
        return new ApiResponse<>("OK", "Document deleted successfully.");
    }
}