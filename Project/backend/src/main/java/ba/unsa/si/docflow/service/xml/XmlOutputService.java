package ba.unsa.si.docflow.service.xml;

import ba.unsa.si.docflow.dto.document.DocumentFileResponse;
import ba.unsa.si.docflow.dto.xml.XmlOutputResponse;
import ba.unsa.si.docflow.response.ApiResponse;

public interface XmlOutputService {

    ApiResponse<XmlOutputResponse> generate(Long documentId);

    ApiResponse<XmlOutputResponse> findByDocumentId(Long documentId);

    DocumentFileResponse downloadFile(Long documentId);

    ApiResponse<XmlOutputResponse> complete(Long documentId);
}
