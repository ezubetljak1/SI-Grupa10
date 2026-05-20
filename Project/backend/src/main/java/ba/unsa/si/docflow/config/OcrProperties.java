package ba.unsa.si.docflow.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "docflow.ocr")
public class OcrProperties {
    private String projectId;

    private String location;

    private String endpoint;

    private String classifierProcessorId;

    private String invoiceProcessorId;

    private String receiptProcessorId;

    private String bankStatementProcessorId;

    private String formProcessorId;
}
