package ba.unsa.si.docflow.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    StoredFileInfo store(MultipartFile file, Long companyId);

    Resource loadAsResource(String relativePath);

    void delete(String relativePath);
}
