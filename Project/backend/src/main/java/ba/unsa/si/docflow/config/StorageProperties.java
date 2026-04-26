package ba.unsa.si.docflow.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "docflow.storage")
public class StorageProperties {
    private String rootDir = "C:/docflow-uploads";
    private Long maxFileSize = 10 * 1024 * 1024L;
    private List<String> allowedExtensions = List.of("pdf", "jpg", "jpeg", "png");
    private List<String> allowedContentTypes =
            List.of("application/pdf", "image/jpeg", "image/png");
}
