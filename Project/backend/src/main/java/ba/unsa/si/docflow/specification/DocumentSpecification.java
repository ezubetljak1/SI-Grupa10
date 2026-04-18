package ba.unsa.si.docflow.specification;

import ba.unsa.si.docflow.dto.document.DocumentFilterRequest;
import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.enums.DocumentStatus;
import ba.unsa.si.docflow.entity.enums.DocumentType;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecification {
    private DocumentSpecification(){

    }
    public static Specification<DocumentEntity> filterBy(DocumentFilterRequest request) {
        return Specification.allOf(
                hasName(request.getName()),
                hasDocumentType(request.getDocumentType()),
                hasDocumentStatus(request.getDocumentStatus()),
                hasCompanyId(request.getCompanyId())
        );
    }

    public static Specification<DocumentEntity> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<DocumentEntity> hasDocumentType(String documentType) {
        return (root, query, criteriaBuilder) -> {
            if (documentType == null || documentType.isBlank()) {
                return null;
            }
            return criteriaBuilder.equal(
                    root.get("documentType"),
                    DocumentType.valueOf(documentType.toUpperCase()));
        };
    }

    public static Specification<DocumentEntity> hasDocumentStatus(String documentStatus) {
        return (root, query, criteriaBuilder) -> {
            if (documentStatus == null || documentStatus.isBlank()) {
                return null;
            }
            return criteriaBuilder.equal(
                    root.get("documentStatus"),
                    DocumentStatus.valueOf(documentStatus.toUpperCase()));
        };
    }

    public static Specification<DocumentEntity> hasCompanyId(Long companyId) {
        return (root, query, criteriaBuilder) -> {
            if (companyId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("companyId"), companyId);
        };
    }
}