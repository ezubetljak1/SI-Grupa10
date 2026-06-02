package ba.unsa.si.docflow.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StoredFileInfo store(MultipartFile file, Long companyId);

    StoredFileInfo store(
            byte[] content, String originalFileName, String contentType, Long companyId);

    Resource loadAsResource(String relativePath);

    void delete(String relativePath);
}
