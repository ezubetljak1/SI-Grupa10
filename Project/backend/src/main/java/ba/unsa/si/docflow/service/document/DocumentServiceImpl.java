package ba.unsa.si.docflow.service.document;

import ba.unsa.si.docflow.dto.document.Document;
import ba.unsa.si.docflow.dto.document.DocumentCreateRequest;
import ba.unsa.si.docflow.dto.document.DocumentFilterRequest;
import ba.unsa.si.docflow.dto.document.DocumentUpdateRequest;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.mapper.DocumentMapper;
import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.PagedResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentDAO documentDAO;
    private final DocumentMapper documentMapper;
    private final DocumentValidation documentValidation;

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
                totalPages
        );
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
        documentDAO.remove(entity);
        return new ApiResponse<>("OK", "Document deleted successfully.");
    }
}