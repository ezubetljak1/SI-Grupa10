package ba.unsa.si.docflow.mapper;

import ba.unsa.si.docflow.dto.document.Document;
import ba.unsa.si.docflow.dto.document.DocumentCreateRequest;
import ba.unsa.si.docflow.dto.document.DocumentUpdateRequest;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.DocumentType;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(
        componentModel = "spring",
        imports = {DocumentType.class, DocumentStatus.class, LocalDateTime.class})
public interface DocumentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "createdByUserId", target = "createdBy")
    @Mapping(target = "uploadDate", expression = "java(LocalDateTime.now())")
    @Mapping(
            target = "documentType",
            expression = "java(DocumentType.valueOf(request.getDocumentType().toUpperCase()))")
    @Mapping(target = "documentStatus", expression = "java(DocumentStatus.UPLOADED)")
    DocumentEntity dtoToEntity(DocumentCreateRequest request);

    @Mapping(target = "documentType", expression = "java(entity.getDocumentType().name())")
    @Mapping(target = "documentStatus", expression = "java(entity.getDocumentStatus().name())")
    Document entityToDto(DocumentEntity entity);

    @Mapping(
            target = "name",
            expression = "java(request.getName() != null ? request.getName() : entity.getName())")
    @Mapping(
            target = "documentType",
            expression =
                    "java(request.getDocumentType() != null ? DocumentType.valueOf(request.getDocumentType().toUpperCase()) : entity.getDocumentType())")
    @Mapping(
            target = "documentStatus",
            expression =
                    "java(request.getDocumentStatus() != null ? DocumentStatus.valueOf(request.getDocumentStatus().toUpperCase()) : entity.getDocumentStatus())")
    DocumentEntity updateEntity(
            DocumentUpdateRequest request, @MappingTarget DocumentEntity entity);

    List<Document> entitiesToDtos(List<DocumentEntity> entities);
}
